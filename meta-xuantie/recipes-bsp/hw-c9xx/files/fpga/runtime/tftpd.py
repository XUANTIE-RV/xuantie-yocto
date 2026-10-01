#!/usr/bin/env python3

import argparse
import pathlib
import threading
import selectors
import socket
import gzip
import logging

from fbtftp.base_handler import BaseHandler, ResponseData
from fbtftp.base_server import BaseServer

log = logging.getLogger(__name__)

def parser_tftp_name(path):
    parse_path = path.split("@")
    path = parse_path[0]
    offset = 0
    size = 0
    next_size = 0
    compress = False
    if len(parse_path) > 1:
        offset = int(parse_path[1], 0)
    if len(parse_path) > 2:
        size = int(parse_path[2], 0)
    if len(parse_path) > 3:
        next_size = int(parse_path[3], 0)
    if len(parse_path) > 4:
        compress = parse_path[4] == "gz"
    return (path, offset, size, next_size, compress)


class CompressTask:
    """管理文件分片的压缩任务，支持预读下一个分片"""
    def __init__(self, file_path, offset, size, next_size):
        self.file_path = file_path
        self.offset = offset
        self.size = size
        self.next_size = next_size

        # 当前压缩的数据 (offset, size, compressed_data)
        self.current_data = None
        self.error = None
        self._lock = threading.Lock()

        # 两个 Event 用于同步
        self._data_ready_event = threading.Event()  # 数据准备好
        self._restart_event = threading.Event()     # 需要重启

        # 数据缓存字典，key 为 (offset, size)，value 为 compressed_data
        self._data_cache = {}

        # 启动后台压缩线程
        self.thread = threading.Thread(target=self._compress_worker, daemon=True)
        self.thread.start()

    def _compress_worker(self):
        """后台线程：第一次使用size分片，后续使用next_size循环分片整个文件"""
        while True:
            try:
                self._restart_event.clear()
                file_size = pathlib.Path(self.file_path).stat().st_size

                with open(self.file_path, 'rb') as f:
                    while self.offset < file_size and self.size > 0:
                        # 检查是否需要重启
                        if self._restart_event.is_set():
                            break

                        # 计算实际读取大小（不超过文件末尾）
                        read_size = min(self.size, file_size - self.offset)

                        f.seek(self.offset, 0)
                        data = f.read(read_size)
                        compressed_data = gzip.compress(data)

                        # 设置当前数据
                        with self._lock:
                            self.current_data = (self.offset, read_size, compressed_data)

                        # 通知数据准备好
                        self._data_ready_event.set()

                        # 等待数据被取走
                        while self._data_ready_event.is_set():
                            if self._restart_event.is_set():
                                break
                            self._data_ready_event.wait(timeout=0.1)

                        # 如果需要重启，跳出循环
                        if self._restart_event.is_set():
                            break

                        with self._lock:
                            self.offset += read_size
                            self.size = self.next_size or self.size

                # 如果正常结束（没有重启），退出循环
                if not self._restart_event.is_set():
                    break

            except Exception as e:
                self.error = e
                self._data_ready_event.set()
                break

    def _validate_and_cache(self, chunk_offset, chunk_size, compressed_data, offset, size, next_size, cache_key):
        """验证数据是否匹配，匹配则缓存并返回"""
        if chunk_offset == offset and chunk_size == size:
            with self._lock:
                self.next_size = next_size
                self._data_cache[cache_key] = compressed_data
            self._data_ready_event.clear()
            return compressed_data
        else:
            raise RuntimeError(f"Expected offset {offset}, got {chunk_offset}")

    def get_data(self, offset, size, next_size):
        """从缓存中获取指定 offset 的切片"""
        # 优先从缓存中获取
        cache_key = (offset, size)
        with self._lock:
            if cache_key in self._data_cache:
                # 缓存命中，更新 next_size 并返回
                self.next_size = next_size
                return self._data_cache[cache_key]

        # 缓存未命中，等待数据准备好
        if not self._data_ready_event.wait(timeout=30):
            if self.error:
                raise self.error
            raise TimeoutError("Compression timeout - no data available")

        # 获取当前数据
        with self._lock:
            if self.current_data is None:
                if self.error:
                    raise self.error
                raise RuntimeError("No data available")

            chunk_offset, chunk_size, compressed_data = self.current_data

        # 如果 offset 和 size 匹配，返回数据
        if chunk_offset == offset and chunk_size == size:
            return self._validate_and_cache(chunk_offset, chunk_size, compressed_data,
                                            offset, size, next_size, cache_key)

        # 如果不匹配，重新开始
        with self._lock:
            self.offset = offset
            self.size = size
            self.next_size = next_size

        # 清除数据准备标志并触发重启
        self._data_ready_event.clear()
        self._restart_event.set()

        # 等待新数据
        if not self._data_ready_event.wait(timeout=30):
            if self.error:
                raise self.error
            raise TimeoutError("Compression timeout - no data available")

        with self._lock:
            if self.current_data is None:
                raise RuntimeError("No data available after restart")
            chunk_offset, chunk_size, compressed_data = self.current_data

        # 验证并返回
        return self._validate_and_cache(chunk_offset, chunk_size, compressed_data,
                                        offset, size, next_size, cache_key)


class CachedResponseData(ResponseData):
    """使用缓存的压缩任务的响应数据"""
    def __init__(self, compress_task, offset, size, next_size):
        self.compress_task = compress_task
        # 从缓存队列获取指定 offset, size, next_size 的数据
        self.data = self.compress_task.get_data(offset, size, next_size)
        self._size = len(self.data)
        self.offset = 0

    def read(self, n=0):
        n = n or (self._size - self.offset)
        data = self.data[self.offset: self.offset + n]
        self.offset += n
        return data

    def size(self):
        return self._size

    def close(self):
        pass


class FileResponseData(ResponseData):
    """简单的文件响应数据，用于不需要压缩的场景"""
    def __init__(self, path, offset=0, size=0):
        fs = pathlib.Path(path)
        file_size = size or fs.stat().st_size

        with fs.open('rb') as f:
            f.seek(offset, 0)
            self.data = f.read(file_size)
        self._size = len(self.data)
        self.offset = 0

    def read(self, n=0):
        n = n or (self._size - self.offset)
        data = self.data[self.offset: self.offset + n]
        self.offset += n
        return data

    def size(self):
        return self._size

    def close(self):
        pass


class StaticHandler(BaseHandler):
    def __init__(self, server_addr, peer, path, options, root_path: pathlib.Path, stats_callback):
        self.original_path = path
        self._root_path = root_path
        self.target_address = options.get('server_addr')
        self.target_port = options.get('server_port')
        self.tftp_server = options.get('tftp_server')

        super().__init__(server_addr, peer, path, options, stats_callback)

    def get_response_data(self):
        path, offset, size, next_size, compress = parser_tftp_name(self.original_path)
        files = list(self._root_path.rglob(path))
        if files:
            file_path = files[0].as_posix()
            if compress and self.tftp_server:
                # 使用服务器级别的压缩缓存
                task = self.tftp_server.get_or_create_compress_task(file_path, offset, size, next_size)
                return CachedResponseData(task, offset, size, next_size)
            else:
                # 不需要压缩，直接读取文件
                return FileResponseData(file_path, offset, size)

        raise FileNotFoundError(f"{self.original_path} does not exist!")


class ImageTftpServer(BaseServer, threading.Thread):
    def __init__(self, root_path, address='0.0.0.0', port=69, retries=3, timeout=3):
        threading.Thread.__init__(self)
        BaseServer.__init__(self, address, port, retries, timeout, self.print_server_stats)

        self._root_path = pathlib.Path(root_path)
        self._handler_stats_callback = self.print_session_stats
        self.target_address = None
        self.target_port = None

        # 压缩任务缓存，key 是 (file_path, offset, size)
        self._compress_cache = {}
        self._cache_lock = threading.Lock()

        self._listener.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.start()

    def get_or_create_compress_task(self, file_path, offset, size, next_size):
        """获取或创建压缩任务"""

        with self._cache_lock:
            if file_path not in self._compress_cache:
                self._compress_cache[file_path] = CompressTask(file_path, offset, size, next_size)
            return self._compress_cache[file_path]

    def get_handler(self, server_addr, peer, path, options):
        options['server_addr'] = self.target_address
        options['server_port'] = self.target_port
        options['tftp_server'] = self
        return StaticHandler(server_addr, peer, path, options, self._root_path, self._handler_stats_callback)

    def run_once(self):
        """
        Uses edge polling object (`socket.epoll`) as an event notification
        facility to know when data is ready to be retrived from the listening
        socket. See http://linux.die.net/man/4/epoll .
        """
        events = self._selector.select(1)
        for key, mask in events:
            if not mask & selectors.EVENT_READ:
                continue
            if key.fd == self._listener.fileno():
                self.on_new_data()
                continue

    def run(self):
        try:
            BaseServer.run(self)
        except KeyboardInterrupt:
            BaseServer.close(self)

    def close(self):
        BaseServer.close(self)

    @staticmethod
    def print_session_stats(stats):
        pass

    @staticmethod
    def print_server_stats(stats):
        pass


def main():
    parser = argparse.ArgumentParser(description='TFTP server with on-the-fly gzip compression')
    parser.add_argument('-r', '--root', default='.', help='TFTP root directory (default: current dir)')
    parser.add_argument('-a', '--address', default='0.0.0.0', help='Bind address (default: 0.0.0.0)')
    parser.add_argument('-p', '--port', type=int, default=6969, help='Bind port (default: 6969)')
    args = parser.parse_args()

    root = pathlib.Path(args.root).resolve()
    if not root.is_dir():
        raise SystemExit(f"Root path is not a directory: {root}")

    server = ImageTftpServer(root_path=root, address=args.address, port=args.port)
    try:
        server.join()
    except KeyboardInterrupt:
        server.close()
        server.join()


if __name__ == '__main__':
    main()
