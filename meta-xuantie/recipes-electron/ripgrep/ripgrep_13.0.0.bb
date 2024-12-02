SUMMARY = "ripgrep is a line-oriented search tool that recursively searches the current \
directory for a regex pattern while respecting gitignore rules. ripgrep has \
first class support on Windows, macOS and Linux."
HOMEPAGE = "https://github.com/BurntSushi/ripgrep"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=034e2d49ef70c35b64be514bef39415a"

inherit cargo cargo_common rust rust-target-config

SRC_URI += "git://github.com/BurntSushi/ripgrep.git;protocol=https;nobranch=1"
SRCREV = "af6b6c543b224d348a8876f0c06245d9ea7929c5"
S = "${WORKDIR}/git"
CARGO_SRC_DIR = ""

SRC_URI += " \
    crate://crates.io/aho-corasick/0.7.18 \
    crate://crates.io/atty/0.2.14 \
    crate://crates.io/base64/0.13.0 \
    crate://crates.io/bitflags/1.2.1 \
    crate://crates.io/bstr/0.2.16 \
    crate://crates.io/bytecount/0.6.2 \
    crate://crates.io/cc/1.0.68 \
    crate://crates.io/cfg-if/0.1.10 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/clap/2.33.3 \
    crate://crates.io/crossbeam-channel/0.5.1 \
    crate://crates.io/crossbeam-utils/0.8.5 \
    crate://crates.io/encoding_rs/0.8.28 \
    crate://crates.io/encoding_rs_io/0.1.7 \
    crate://crates.io/fnv/1.0.7 \
    crate://crates.io/fs_extra/1.2.0 \
    crate://crates.io/glob/0.3.0 \
    crate://crates.io/hermit-abi/0.1.18 \
    crate://crates.io/itoa/0.4.7 \
    crate://crates.io/jemalloc-sys/0.3.2 \
    crate://crates.io/jemallocator/0.3.2 \
    crate://crates.io/jobserver/0.1.22 \
    crate://crates.io/lazy_static/1.4.0 \
    crate://crates.io/libc/0.2.97 \
    crate://crates.io/libm/0.1.4 \
    crate://crates.io/log/0.4.14 \
    crate://crates.io/memchr/2.4.0 \
    crate://crates.io/memmap2/0.3.0 \
    crate://crates.io/num_cpus/1.13.0 \
    crate://crates.io/once_cell/1.7.2 \
    crate://crates.io/packed_simd_2/0.3.5 \
    crate://crates.io/pcre2-sys/0.2.5 \
    crate://crates.io/pcre2/0.2.3 \
    crate://crates.io/pkg-config/0.3.19 \
    crate://crates.io/proc-macro2/1.0.27 \
    crate://crates.io/quote/1.0.9 \
    crate://crates.io/regex-automata/0.1.10 \
    crate://crates.io/regex-syntax/0.6.25 \
    crate://crates.io/regex/1.5.4 \
    crate://crates.io/ryu/1.0.5 \
    crate://crates.io/same-file/1.0.6 \
    crate://crates.io/serde/1.0.126 \
    crate://crates.io/serde_derive/1.0.126 \
    crate://crates.io/serde_json/1.0.64 \
    crate://crates.io/strsim/0.8.0 \
    crate://crates.io/syn/1.0.73 \
    crate://crates.io/termcolor/1.1.2 \
    crate://crates.io/textwrap/0.11.0 \
    crate://crates.io/thread_local/1.1.3 \
    crate://crates.io/unicode-width/0.1.8 \
    crate://crates.io/unicode-xid/0.2.2 \
    crate://crates.io/walkdir/2.3.2 \
    crate://crates.io/winapi-i686-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi-util/0.1.5 \
    crate://crates.io/winapi-x86_64-pc-windows-gnu/0.4.0 \
    crate://crates.io/winapi/0.3.9 \
"

export RUST_BACKTRACE = "full"
export RUSTFLAGS
export RUST_TARGET = "${RUST_HOST_SYS}"
