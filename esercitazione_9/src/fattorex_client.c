#include <rpc/rpc.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "fattorex.h"

#define DIMINPUTSTRING 2

int main (int argc, char *argv[])
{
	char *host, choice[DIMINPUTSTRING];
	int *result;

  	CLIENT *gestoreTrasporto; //Gestore di trasporto

  	Classifica *classificaGiudici; //Struttura di Output
	Voto * votazione; //Struttura di Input


  	if (argc > 3){//verifica parametri invocazione
    		printf ("Errore invocazione: %s [serverHost]\n", argv[0]);
    		exit (1);
  	}
  	host = (argc == 2) ? argv[1] : "localhost"; //se non specificato viene assegnato "localhost"

	votazione = malloc(sizeof(Voto));
	votazione->nomeCandidato = (char*)malloc(MAXLENGTHSTRING);
	printf("Creazione struttura dati Input avvenuta con successo!\n");

	gestoreTrasporto = clnt_create (host, VOTAFATTOREX, VOTAFATTOREXVERS, "udp");
	if (gestoreTrasporto == NULL){
		clnt_pcreateerror (host);
		exit (1);
	}
	printf("Creazione del gestore di trasporto avvenuta con successo!\n");


	printf("Inserire una:\n1\tClassifica Giudici\n2\tEsprimi voto\n^D\tper terminare: ");

	while (scanf("%s",choice)==1){
		if( strcmp(choice,"1")==0 ){
			void *v;
			classificaGiudici = classifica_giudici_1(v, gestoreTrasporto);
			
			if(classificaGiudici == NULL){
				clnt_perror(gestoreTrasporto, host);
				exit(1);
			}
			printf("Invocazione classifica_giudici avvenuta con successo!\n");
			printf("Stampa esito:\n");

			for(int i=0; i<NUMGIUDICI; i++) {
				if(classificaGiudici->classificaGiudici[i].punteggioTot > 0) {
						printf("Giudice %s - %d VOTI\n", classificaGiudici->classificaGiudici[i].nomeGiudice, classificaGiudici->classificaGiudici[i].punteggioTot);
				}
			}

		} 

		else if( strcmp(choice,"2")==0 ){
			printf("\nInserisci il nome del candidato: \n");
			scanf("%s", votazione->nomeCandidato);
			while (getchar() != '\n'); //pulisco buffer da eventuale garbage prima di lettura carattere

			printf("\nInserisci tipo di operazione (A (addiziona) oppure (S) sottrai ): \n");
			scanf("%c", &votazione->tipoOp);

			result = esprimi_voto_1(votazione, gestoreTrasporto);

			if(result == NULL){
				clnt_perror(gestoreTrasporto, host);
				exit(1);
			}
			printf("Invocazione esprimi_voto avvenuta con successo!\n");
			if(*result < 0)
				printf("Assegnazione del voto non riuscito: inserire nome candidato valido e operazione [A|S]\n");
			else if(*result == 0)
				printf("Votazione registrata con successo\n");
		} 
		else
			printf("Operazione richiesta non gestita dal server!\n");

		printf("Inserire:\n1\tGiudice in testa\n2\tEsprimi voto\n^D\tper terminare: ");
	} 

	clnt_destroy (gestoreTrasporto);
	exit(0);
}
