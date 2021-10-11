#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <unistd.h>
#include <string.h>
#include <ctype.h>

#define MAX_STRING_LENGTH 256

/**
 * Il consumatore è un filtro a caratteri:
 * Prende in input il file passato come secondo parametro o passato come redirezione in input
 * Scopo del programma è la stampa del contenuto del file privata dei caratteri passati come primo parametro - da cancellare dall'output
 * 
 * Sintassi invocazione: ./consumatore stringa_caratteri_da_eliminare input_file.txt
*/
int main(int argc, char *argv[])
{
    /**
     * Descrizione delle variabili:
     * file_in: puntatore alla stringa nome del file da leggere - secondo parametro invocazione o stdin
     * read_char: carattere letto dal file
     * delete_chars: stringa filtrata dei caratteri da eliminare dal file - senza duplicati
     * tempC: carattere temporaneo usato per verificare presenza duplicati stringa primo parametro (caratteri da rimuovere)
     * nread: numero carattero letto dal file
     * fd: file descriptor del file di input
     * count: dimensione stringa filtrata caratteri da eliminare
     * dimS: dimensione stringa primo parametro caratteri da eliminare 
    */
    char *file_in, read_char, delete_chars[MAX_STRING_LENGTH], tempC;
    int nread, fd, count, dimS;

    /** 
     * Controllo dei parametri di invocazione
     * Il numero dei parametri possono essere compresi tra 2 e 3
     * Sintassi invocazione: 
     * ./consumatore stringa_caratteri_da_eliminare input_file.txt
     * ./consumatore stringa_caratteri_da_eliminare < input_file.txt (redirezione)
    */
    if (argc < 2 || argc > 3)
    {
        perror("numero di argomenti sbagliato");
        exit(1);
    }

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
        fd = 0; //Associo file descriptor dello stdin - indice 0 tabella
    }

    count = 0;
    dimS = strlen(argv[1]); //numero caratteri da rimuovere
    for (int i = 0; i < dimS; i++)
    {
        tempC = toupper(argv[1][i]);
        if (strchr(delete_chars, tempC) == NULL)
        {
            delete_chars[count] = tempC;
            count++;
        }
    }
    delete_chars[count] = '\0';

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