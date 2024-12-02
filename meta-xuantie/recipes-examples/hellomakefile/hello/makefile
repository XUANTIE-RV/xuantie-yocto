TARGET=helloYocto zlibtest

#CFLAGS=-D_REENTRANT  -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/alsa-lib
#CFLAGS1=-D_REENTRANT  -I$(PKG_CONFIG_SYSROOT_DIR)/usr/include/libmad
LDFLAGS= -lz

all: $(TARGET)
helloYocto: helloYocto.o
	$(CC) $(CFLAGS) -o $@ $^ 

zlibtest: zlibtest.o
	$(CC) $(CFLAGS1) -o $@ $^ $(LDFLAGS)

clean:
	rm -f $(OBJS) $(TARGET)
