TARGET=helloYocto zlibtest

all: $(TARGET)
helloYocto: helloYocto.o
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $^

# -lz must come AFTER $^ otherwise --as-needed (set in Yocto LDFLAGS)
# drops libz before zlibtest.o pulls in symbols like inflateBackInit_.
zlibtest: zlibtest.o
	$(CC) $(CFLAGS1) $(LDFLAGS) -o $@ $^ -lz

clean:
	rm -f $(OBJS) $(TARGET) *.o
