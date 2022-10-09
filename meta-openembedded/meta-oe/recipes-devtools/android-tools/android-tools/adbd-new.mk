# Makefile for adbd

SRCDIR ?= $(S)

VPATH += $(SRCDIR)
adbd_SRC_FILES += adb.c
adbd_SRC_FILES += backup_service.c
adbd_SRC_FILES += fdevent.c
adbd_SRC_FILES += transport.c
adbd_SRC_FILES += transport_local.c
adbd_SRC_FILES += transport_usb.c
adbd_SRC_FILES += adb_auth_client.c
adbd_SRC_FILES += sockets.c
adbd_SRC_FILES += services.c
adbd_SRC_FILES += file_sync_service.c
adbd_SRC_FILES += jdwp_service.c
adbd_SRC_FILES += framebuffer_service.c
adbd_SRC_FILES += remount_service.c
adbd_SRC_FILES += utils.c
adbd_SRC_FILES += log_service.c
adbd_SRC_FILES += usb_linux_client.c
adbd_OBJS := $(adbd_SRC_FILES:.c=.o)

VPATH += $(SRCDIR)/libs/libcutils
libcutils_SRC_FILES += array.c
libcutils_SRC_FILES += buffer.c
libcutils_SRC_FILES += hashmap.c
libcutils_SRC_FILES += native_handle.c
libcutils_SRC_FILES += socket_inaddr_any_server.c
libcutils_SRC_FILES += socket_local_client.c
libcutils_SRC_FILES += socket_local_server.c
libcutils_SRC_FILES += socket_loopback_client.c
libcutils_SRC_FILES += socket_loopback_server.c
libcutils_SRC_FILES += socket_network_client.c
libcutils_SRC_FILES += sockets.c
libcutils_SRC_FILES += config_utils.c
libcutils_SRC_FILES += cpu_info.c
libcutils_SRC_FILES += load_file.c
libcutils_SRC_FILES += list.c
libcutils_SRC_FILES += open_memstream.c
libcutils_SRC_FILES += strdup16to8.c
libcutils_SRC_FILES += strdup8to16.c
libcutils_SRC_FILES += record_stream.c
libcutils_SRC_FILES += process_name.c
libcutils_SRC_FILES += qsort_r_compat.c
libcutils_SRC_FILES += threads.c
libcutils_SRC_FILES += sched_policy.c 
libcutils_SRC_FILES += iosched_policy.c 
libcutils_SRC_FILES += str_parms.c 
libcutils_SRC_FILES += properties.c
libcutils_SRC_FILES += abort_socket.c 
libcutils_SRC_FILES += fs.c 
libcutils_SRC_FILES += selector.c 
libcutils_SRC_FILES += tztime.c  
libcutils_SRC_FILES += multiuser.c  
libcutils_SRC_FILES += zygote.c 
libcutils_SRC_FILES += android_reboot.c 
libcutils_SRC_FILES += debugger.c 
libcutils_SRC_FILES += klog.c 
libcutils_SRC_FILES += mq.c 
libcutils_SRC_FILES += partition_utils.c 
libcutils_SRC_FILES += qtaguid.c 
libcutils_SRC_FILES += uevent.c 
libcutils_SRC_FILES += misc_rw.c 
libcutils_SRC_FILES += memory.c
libcutils_OBJS := $(libcutils_SRC_FILES:.c=.o)


CFLAGS += -std=gnu11
CFLAGS += -DANDROID
CFLAGS += -DADB_HOST=0
CFLAGS += -D_XOPEN_SOURCE -D_GNU_SOURCE
CFLAGS += -DALLOW_ADBD_ROOT=1
CFLAGS += -DALLOW_ADBD_DISABLE_VERITY=1
CFLAGS += -DPROP_NAME_MAX=32
CFLAGS += -DPROP_VALUE_MAX=92
CFLAGS += -DAUDITD_LOG_TAG=1003
# CFLAGS += -DHOST
CFLAGS += -DANDROID_SMP=0
CFLAGS += -I$(SRCDIR)
CFLAGS += -I$(SRCDIR)/libs/libcutils
CFLAGS += -I$(SRCDIR)/libs/libmincrypt
LDFLAGS += -L$(SRCDIR)

LIBS += libmincrypt.a libcutils.a -lpthread -lbsd -lpcre -lresolv -lcrypto

all: adbd

adbd: $(adbd_OBJS)
	$(CC) -o $@ $(LDFLAGS) $(adbd_OBJS) $(LIBS) $(LDFLAGS)

libcutils.a: $(libcutils_OBJS)
	$(AR) rcs $@ $(libcutils_OBJS)

clean:
	$(RM) *.o *.a adbd
