#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>

#define MAX_STRING_LENGTH 256

// consumatore.c e' un filtro
int main(int argc, char *argv[])
{

    char *file_in, read_char, buf[MAX_STRING_LENGTH], *delete_chars;
    int nread, fd;

    //controllo numero argomenti
    if (argc < 2 || argc > 3)
    {
        perror(" numero di argomenti sbagliato");
        exit(1);
    }

    delete_chars = argv[1];

    if (argc == 3)
    {
        file_in = argv[2];
        fd = open(file_in, O_RDONLY);
        if (fd < 0)
        {
            perror("P0: Impossibile aprire il file.");
            exit(2);
        }
    }
    else
    {
        fd = 0; //File descriptor stdin
    }
    while ((nread = read(fd, &read_char, sizeof(char)))) /* Fino ad EOF*/
    {
        if (nread >= 0)
        {
            if ((strchr(delete_chars, read_char)) == NULL)
                putchar(read_char);
        }
        else
        {
            printf("(PID %d) impossibile leggere dal file %s", getpid(), file_in);
            perror("Errore!");
            close(fd);
            exit(3);
        }
    }
    close(fd);
}