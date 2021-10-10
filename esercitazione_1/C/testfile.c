#include <stdio.h>

#define DIMSTRING 120

int main(int argc, char const *argv[])
{
    int res;
    char stringFile[DIMSTRING];
    char d;

    while (gets(stringFile) != NULL) {
       printf("%s\n", stringFile);
    }
    return 0;
}
