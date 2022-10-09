#include "add.h"


int sub(int a, int b) {
    return a - b;
}

int add_sub(int a, int b, int c) {
    int ret = 0;
    int tmp = 0;

    tmp = add(a, b);
    ret = sub(tmp, c);

    return ret;
}
