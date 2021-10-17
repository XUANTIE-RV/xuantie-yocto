#include <stdio.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <errno.h>
#include <linux/input.h>

#if 0
int main( void )
{
	int			fd;
	fd_set			rds;
	int			ret;
	struct input_event	event;
	struct timeval		time;
    int version;

	fd = open( "/dev/input/event2", O_RDWR);
	if ( fd < 0 )
	{
		perror( "/dev/input/event2" );
		return(-1);
	}
    ioctl(fd, EVIOCGVERSION, &version);
    printf("the event version is 0x%x\n", version);

    memset(&event, 0, sizeof(event));

    event.type = atoi("1");
    event.code = atoi("158");
    event.value = atoi("1");
    gettimeofday(&event.time,0);

    ret = write(fd, &event, sizeof(struct input_event));
    if(ret < 0 ) {
        printf("write the event2 fail, ret = %d\n", ret);
        perror("write the event2 fail");
    }

    printf("write the event2, ret = %d\n", ret);
	close( fd );
	return(0);
}
#endif

int main(int argc, char *argv[])
{
    int fd;
    ssize_t ret;
    int version;
    struct input_event event;
    if(argc != 5) {
        fprintf(stderr, "use: %s device type code value\n", argv[0]);
        return 1;
    }
    fd = open(argv[1], O_RDWR);
    if(fd < 0) {
        fprintf(stderr, "could not open %s, %s\n", argv[optind], strerror(errno));
        return 1;
    }
    if (ioctl(fd, EVIOCGVERSION, &version)) {
        fprintf(stderr, "could not get driver version for %s, %s\n", argv[optind], strerror(errno));
        return 1;
    }
    memset(&event, 0, sizeof(event));
    event.type = atoi(argv[2]);
    event.code = atoi(argv[3]);
    event.value = atoi(argv[4]);
    ret = write(fd, &event, sizeof(event));
    if(ret < (ssize_t) sizeof(event)) {
        fprintf(stderr, "write event failed, %s\n", strerror(errno));
        return -1;
    }
    return 0;
}
