/*
 * xfactor_s.c
 */

#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "fattorex.h"

#define N 6 //2 concorrenti per giudice

/*STATO INTERNO PRIVATO DEL SERVER*/
typedef struct{
	char candidato[MAXLENGTHSTRING];
	char giudice[MAXLENGTHSTRING];
	char categoria ;
	char nomeFile[MAXLENGTHSTRING];
	char fase;
	int voto;
} Tupla;

//variabili globali statiche
static Tupla tabella[N];
static int isSetupTabella = 0;


/*Stato interno parzialmente implementato*/
void setupTabella(){

	strcpy(tabella[0].candidato, "Giuseppe");
	strcpy(tabella[0].giudice, "DJFrancesco");
	tabella[0].categoria = 'O';
	strcpy(tabella[0].nomeFile, "Giuseppe.txt");
	tabella[0].fase = 'A';
	tabella[0].voto = 20;

	strcpy(tabella[1].candidato, "Antonio");
	strcpy(tabella[1].giudice, "DJFrancesco");
	tabella[1].categoria = 'U';
	strcpy(tabella[1].nomeFile, "Antonio.txt");
	tabella[1].fase = 'B';
	tabella[1].voto = 70;

	strcpy(tabella[2].candidato, "Fabrizio");
	strcpy(tabella[2].giudice, "J-AX");
	tabella[2].categoria = 'U';
	strcpy(tabella[2].nomeFile, "Fabrizio.txt");
	tabella[2].fase = 'S';
	tabella[2].voto = 100;

	strcpy(tabella[3].candidato, "Filippo");
	strcpy(tabella[3].giudice, "J-AX");
	tabella[3].categoria = 'U';
	strcpy(tabella[3].nomeFile, "Filippo.txt");
	tabella[3].fase = 'B';
	tabella[3].voto = 50;

	strcpy(tabella[4].candidato, "Luisa");
	strcpy(tabella[4].giudice, "Nina");
	tabella[4].categoria = 'D';
	strcpy(tabella[0].nomeFile, "Luisa.txt");
	tabella[4].fase = 'A';
	tabella[4].voto = 30;

	strcpy(tabella[5].candidato, "Valeria");
	strcpy(tabella[5].giudice, "Nina");
	tabella[5].categoria = 'D';
	strcpy(tabella[5].nomeFile, "Valeria.txt");
	tabella[5].fase = 'S';
	tabella[5].voto = 100;

	isSetupTabella = 1;
	printf("Terminata inizializzazione struttura dati!\n");
}

//implementazione delle procedure definite nel file XDR
Classifica * classifica_giudici_1_svc(void * voidValue, struct svc_req *reqstp){
	Giudice listaGiudici[N];
	static Classifica res;
	int i, k, presente, ind=0, count =0;
	if (!isSetupTabella) setupTabella();

	// inizializzo listaGiudici
	for(i=0; i<N; i++){
		listaGiudici[i].nomeGiudice = malloc(strlen("L")+1);
		strcpy(listaGiudici[i].nomeGiudice, "L");
		listaGiudici[i].punteggioTot = -1;
	}

	//
	for (i=0; i < N; i++){
		presente = 0;

		for(k=0; k<N; k++){
			if(strcmp(listaGiudici[k].nomeGiudice, tabella[i].giudice)==0){
				listaGiudici[k].punteggioTot = listaGiudici[k].punteggioTot + tabella[i].voto;
				presente = 1;
			} 
		}

		if(presente == 0){
			free(listaGiudici[ind].nomeGiudice);
			listaGiudici[ind].nomeGiudice = malloc(strlen(tabella[i].giudice) + 1);
			strcpy(listaGiudici[ind].nomeGiudice, tabella[i].giudice);
			listaGiudici[ind].punteggioTot = tabella[i].voto;
			ind++;
		}
	}

	// inizializzo res
	for(int i=0; i< N; i++) {
		res.classificaGiudici[i].punteggioTot = -1;
		res.classificaGiudici[i].nomeGiudice = malloc(strlen("L")+1);
		strcpy(res.classificaGiudici[i].nomeGiudice, "L");
	}

	// ordinamento
	for(i=0; i<ind; i++){
		for(k=0; k<ind; k++) {
			if(res.classificaGiudici[i].punteggioTot < listaGiudici[k].punteggioTot){
				res.classificaGiudici[i].nomeGiudice = malloc(strlen(listaGiudici[k].nomeGiudice) + 1);
				strcpy(res.classificaGiudici[i].nomeGiudice, listaGiudici[k].nomeGiudice);
				res.classificaGiudici[i].punteggioTot = listaGiudici[k].punteggioTot;
				count = k;
			}
		}
		listaGiudici[count].punteggioTot =-1;
	}

	return &res;
}

int * esprimi_voto_1_svc(Voto* votazione, struct svc_req *reqstp){
	static int result;

	if (!isSetupTabella) setupTabella();

	result = -1;
	for (int indexTable = 0; indexTable < N; indexTable++){

		if (strcmp(tabella[indexTable].candidato, votazione->nomeCandidato) == 0){

			switch (votazione->tipoOp)
			{
			case 'A':
					{
						tabella[indexTable].voto++;
						printf("[Incrementa Voto] : Aggiornato voto candidato %s a votazione %d\n", tabella[indexTable].candidato, tabella[indexTable].voto);
						result = 0;
					}	
				break;

			case 'S':
					{
						if (tabella[indexTable].voto > 0) {
							tabella[indexTable].voto--;
							printf("[Decrementa Voto]: Aggiornato voto candidato %s a votazione %d\n", tabella[indexTable].candidato, tabella[indexTable].voto);
							result = 0;
						} else printf("[Decrementa Voto]: impossibile decrementare voto candidato %s essendo pari a 0\n", tabella[indexTable].candidato);
					}	
				break;
			
			default:
					printf("%c : operazone non gestita dal server",votazione->tipoOp);
				break;
			}
			
		}
	}
	printf("Stato del risultato: %d\n",result);
	if(result == -1)
		printf("Nome del concorrente non trovato o azione non effettuabile: invio esito: %d  al client\n", result);

	return (&result);
}
