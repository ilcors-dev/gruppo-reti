/*
 * xfactor_c.c
 */

#include <rpc/rpc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "xfactor.h"

#define DIM 256
#define N 4 //numero giudici

int main(int argc, char *argv[])
{
	char *host; //nome host
	CLIENT *cl; //gestore del trasporto

	int i, *ris, numVoti = -1;
	char c;
	Output *classificaGiudici;
	void *voidValue;

	//per leggere stringhe da standard di input
	char ok[DIM];
	Input *input;

	printf("ciao --------------------------\n");
	input = malloc(sizeof(Input));
	input->nomeCandidato = (char *)malloc(DIM);
	printf("ciao --------------------------ciao\n");

	/****************************************************************************************************************/

	//Controllo degli argomenti --> 2 di default...
	if (argc != 2)
	{
		printf("usage: %s server_host\n", argv[0]);
		exit(1);
	}
	host = argv[1];

	//Creazione gestore del trasporto
	cl = clnt_create(host, OPERATION, OPERATIONVERS, "udp");
	if (cl == NULL)
	{
		clnt_pcreateerror(host);
		exit(1);
	}

	//Interazione con l'utente
	printf("Inserire:\n1\tClassifica Giudici\n2\tEsprimi voto\n^D\tper terminare: ");

	while (scanf("%s", ok) == 1)
	{
		if (strcmp(ok, "1") == 0)
		{
			void *v;
			// Invocazione remota
			classificaGiudici = classifica_giudici_1(v, cl);

			//Controllo del risultato
			if (classificaGiudici == NULL)
			{
				//Errore di RPC
				clnt_perror(cl, host);
				exit(1);
			}

			printf("Classifica ordinata giudici:\n");

			for (int i = 0; i < N; i++)
			{
				if (classificaGiudici->classificaGiudici[i].punteggioTot > 0)
				{
					printf("%s con %d voti\n", classificaGiudici->classificaGiudici[i].nomeGiudice, classificaGiudici->classificaGiudici[i].punteggioTot);
				}
			}

		} // if 1

		else if (strcmp(ok, "2") == 0)
		{
			printf("\nInserisci il nome del candidato: \n");
			scanf("%s", input->nomeCandidato);
			getchar();

			printf("\nInserisci tipo di operazione (A (addiziona) oppure (S) sottrai ): \n");
			scanf("%c", &input->tipoOp);

			// Invocazione remota
			ris = esprimi_voto_1(input, cl);

			//Controllo del risultato
			if (ris == NULL)
			{
				//Errore di RPC
				clnt_perror(cl, host);
				exit(1);
			}

			if (*ris < 0)
				//Eventuale errore di logica del programma
				printf("Problemi nell'attribuzione del voto, nome non trovato\n");
			else if (*ris == 0)
				//Tutto ok
				printf("Votazione effettuata con successo\n");
		} // if 2
		else
			printf("Operazione richiesta non disponibile!!\n");

		printf("Inserire:\n1\tGiudice in testa\n2\tEsprimi voto\n^D\tper terminare: ");
	} // while

	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy(cl);
	exit(0);
} //main
