#include <rpc/rpc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "fattorex.h"

#define DIM 256
#define N 3	//numero giudici

int main (int argc, char *argv[])
{
	char *host; 
  	CLIENT *gestoreTrasporto; 

	int *ris;
  	Classifica *classificaGiudici;

  	char ok[DIM];
	Voto * votazione;


  	if (argc > 3){//verifica parametri invocazione
    		printf ("usage: %s server_host\n", argv[0]);
    		exit (1);
  	}
  	host = (argc == 2) ? argv[1] : "localhost"; //se non specificato viene assegnato "localhost"

	votazione = malloc(sizeof(Voto));
	votazione->nomeCandidato = (char*)malloc(DIM);
	printf("Creazione struttura dati input avvenuta con successo!\n");

	gestoreTrasporto = clnt_create (host, VOTAFATTOREX, VOTAFATTOREXVERS, "udp");
	if (gestoreTrasporto == NULL){
		clnt_pcreateerror (host);
		exit (1);
	}
	printf("Creazione del gestore di trasporto avvenuta con successo!\n");


	//Interazione con l'utente
	printf("Inserire una:\n1\tClassifica Giudici\n2\tEsprimi voto\n^D\tper terminare: ");

	while (scanf("%s",ok)==1){
		if( strcmp(ok,"1")==0 ){
			void *v;
			// Invocazione remota
			classificaGiudici = classifica_giudici_1(v, gestoreTrasporto);
			

			//Controllo del risultato
			if(classificaGiudici == NULL){
				//Errore di RPC
				clnt_perror(gestoreTrasporto, host);
				exit(1);
			}
			printf("Invocazione classifica_giudici avvenuta con successo!\n");
			printf("Classifica ordinata giudici:\n");

			for(int i=0; i<N; i++) {
				if(classificaGiudici->classificaGiudici[i].punteggioTot > 0) {
						printf("%s con %d voti\n", classificaGiudici->classificaGiudici[i].nomeGiudice, classificaGiudici->classificaGiudici[i].punteggioTot);
				}
			}

		} // if 1

		else if( strcmp(ok,"2")==0 ){
			printf("\nInserisci il nome del candidato: \n");
			scanf("%s", votazione->nomeCandidato);
			getchar();

			printf("\nInserisci tipo di operazione (A (addiziona) oppure (S) sottrai ): \n");
			scanf("%c", &votazione->tipoOp);

			// Invocazione remota
			ris = esprimi_voto_1(votazione, gestoreTrasporto);

			//Controllo del risultato
			if(ris == NULL){
				//Errore di RPC
				clnt_perror(gestoreTrasporto, host);
				exit(1);
			}

			if(*ris < 0)
				//Eventuale errore di logica del programma
				printf("Problemi nell'attribuzione del voto, nome non trovato\n");
			else if(*ris == 0)
				//Tutto ok
				printf("Votazione effettuata con successo\n");
		} // if 2
		else
			printf("Operazione richiesta non disponibile!!\n");

		printf("Inserire:\n1\tGiudice in testa\n2\tEsprimi voto\n^D\tper terminare: ");
	} // while

	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy (gestoreTrasporto);
	exit(0);
}//main
