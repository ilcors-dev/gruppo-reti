/* count_client.c */

#include <stdio.h>
#include <rpc/rpc.h>
#include "count.h"

int main(int argc, char *argv[])
{

    CLIENT *clnt;
    int *ris;
    Output *out;
    char *server, *fileName;
    Input input;
    char chars, ok[128];

    input.directory = (char *)malloc(50);
    input.threshold = 0;
    fileName = (char *)malloc(50);

    if (argc != 2)
    {
        fprintf(stderr, "uso: %s nomehost\n", argv[0]);
        exit(1);
    }

    clnt = clnt_create(argv[1], COUNTPROGRAM, CONTAVERS, "udp");

    if (clnt == NULL)
    {
        clnt_pcreateerror(argv[1]);
        exit(1);
    }

    printf("richieste servizio fino a fine file\n");
    printf("operazioni: CS = Conta File maggiori di, C = Conta\n");

    while (gets(ok))
    {
        if ((strcmp(ok, "CS") != 0) && (strcmp(ok, "C") != 0))
        {
            printf("scelta non disponibile\n");
            printf("richieste servizio fino a fine file\n");
            printf("operazioni: CS = Conta File maggiori di, C = Conta\n");
            continue;
        }

        printf("Richiesto servizio: %s\n", ok);

        // richiesta conteggio file nel directory remoto
        if (strcmp(ok, "CS") == 0)
        {
            printf("inserisci il nome directory: \n");
            gets(input.directory);
            printf("inserisci la threshold: \n");
            //controllo intero
            while (scanf("%d", &input.threshold) != 1)
            {
                do
                {
                    chars = getchar();
                    printf("%c ", chars);
                } while (chars != '\n');
                printf("Inserire int");
                continue;
            }
            gets(ok);
            printf("Stringa letta: %s\n", ok);

            ris = conta_file_1(&input, clnt);

            if (ris == (int *)NULL)
                clnt_perror(clnt, "call failed");
            else if (*ris == -1)
                printf("E' avvenuto un errore lato server\n");
            else
                printf("Ho contato %d file con dim >= %d!\n", *ris, input.threshold);
        } // CS

        // richiesta conteggio characters nel file remoto
        else if (strcmp(ok, "C") == 0)
        {
            printf("inserisci il nome del file: \n");
            gets(fileName);
            out = conta_1(&fileName, clnt);

            if (out == (int *)NULL)
                clnt_perror(clnt, "call failed");
            else if (out->errorCode == -1)
                printf("E' avvenuto un errore lato server\n");
            else
                printf("Ho contato %d characters, %d words e %d lines !\n",
                       out->characters, out->words, out->lines);
            //clean input buffer
            memset(fileName, 0, sizeof(fileName));
        } // CF
        printf("richieste servizio fino a fine file\n");
        printf("operazioni: CS = Conta File maggiori di, C = Conta\n");
    } //while

    clnt_destroy(clnt);
    free(input.directory);
    free(fileName);
    printf("Esco dal client\n");
}