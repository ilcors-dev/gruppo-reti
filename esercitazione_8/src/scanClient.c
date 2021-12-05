/* count_client.c */

#include <stdio.h>
#include <rpc/rpc.h>
#include "scan.h"

int main(int argc, char *argv[])
{

    CLIENT *clnt;
    int *ris;
    Output *out;
    char *server, *fileName;
    Input input;
    char chars, ok[128];

    input.directory = (char *)malloc(50);
    input.soglia = 0;
    fileName = (char *)malloc(50);

    if (argc != 2)
    {
        fprintf(stderr, "uso: %s nomehost\n", argv[0]);
        exit(1);
    }

    clnt = clnt_create(argv[1], SCANPROGRAM, SCANVERS, "udp");

    if (clnt == NULL)
    {
        clnt_pcreateerror(argv[1]);
        exit(1);
    }

    printf("richieste servizio fino a fine file\n");
    printf("operazioni: SD = Scansiona File maggiori di nella Directory specificata, SF = Scansiona File\n");

    while (gets(ok))
    {
        if ((strcmp(ok, "SD") != 0) && (strcmp(ok, "SF") != 0))
        {
            printf("scelta non disponibile\n");
            printf("richieste servizio fino a fine file\n");
            printf("operazioni: SD = Conta File maggiori di, SF = Conta\n");
            continue;
        }

        // richiesta conteggio file nel directory remoto
        if (strcmp(ok, "SD") == 0)
        {
            printf("inserisci il nome directory: \n");
            gets(input.directory);
            printf("inserisci la threshold: \n");
            //controllo intero
            while (scanf("%d", &input.soglia) != 1)
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
                printf("Ho contato %d file con dim >= %d!\n", *ris, input.soglia);
        } // CS

        // richiesta conteggio characters nel file remoto
        else if (strcmp(ok, "SF") == 0)
        {
            printf("inserisci il nome del file: \n");
            gets(fileName);
            out = file_scan_1(&fileName, clnt);

            if (out == (int *)NULL)
                clnt_perror(clnt, "call failed");
            else if (out->errore == -1)
                printf("E' avvenuto un errore lato server\n");
            else
                printf("Ho contato %d characters, %d words e %d lines !\n",
                       out->caratteri, out->parole, out->linee);
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