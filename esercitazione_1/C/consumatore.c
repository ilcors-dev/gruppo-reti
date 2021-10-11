#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

/**
 * Il consumatore è un filtro a caratteri:
 * prende in input il file passato come parametro o il file passato come redirezione in input
 * Scopo del programma è la stampa del contenuto del file privata dei caratteri passati come parametro
 * 
 * Sintassi invocazione: ./consumatore stringa_caratteri_da_eliminare input_file.txt
*/ 
int main(int argc, char *argv[])
{
    /**
     * Descrizione delle variabili:
     * file_in: puntatore alla stringa nome del file da leggere
     * read_char: carattere letto dal file
     * delete_chars: puntatore alla stringa dei caratteri da rimuovere dalla stampa file
     * nread: numero carattere letto dal file
     * fd: file descriptor del file di input
    */
    char *file_in, read_char, *delete_chars;
    int nread, fd;

    /** 
     * Controllo dei parametri in ingresso
     * Il numero dei parametri possono essere compresi tra 2 e 3 (compresi)
     * Sintassi invocazione: ./consumatore stringa_caratteri_da_eliminare input_file.txt
    */
    if (argc < 2 || argc > 3)
    {
        perror("numero di argomenti sbagliato");
        exit(1);
    }

    delete_chars = argv[1];

    //Conversione dei caratteri della stringa nel rispettivo carattere maiuscolo
    for (int index = 0; delete_chars[index] != '\0'; ++index){
		delete_chars[index] = toupper(delete_chars[index]);
	}

    printf("Stringa caratteri da rimuovere: %s\n", delete_chars);

    if (argc == 3) //Caso 1: file passato come parametro
    {
        file_in = argv[2];
        fd = open(file_in, O_RDONLY);
        if (fd < 0)
        {
            perror("P0: Impossibile aprire il file.");
            exit(2);
        }
    }
    else //Caso 2: file passato mediante redirezione input
    {
        fd = 0; //Associo file descriptor dello stdin
    }
    while ((nread = read(fd, &read_char, sizeof(char)))) /* Fino ad EOF*/
    {
        if (nread >= 0)
        {
            if ((strchr(delete_chars, toupper(read_char))) == NULL) //Verifico che carattere letto non appartenga a stringa caratteri da rimuovere dall'output
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