/*
 * xfactor_s.c
 */

#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "fattorex.h"

#define NUMROWTABLE 6 //2 concorrenti per giudice

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
static Tupla tabella[NUMROWTABLE];
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

Classifica * classifica_giudici_1_svc(void * voidValue, struct svc_req *reqstp){
	Giudice listaGiudici[NUMGIUDICI];
	static Classifica result;
	int  inseritoLista, posLibera=0, count =0;
	if (!isSetupTabella) setupTabella();

	
	for(int indiceGiudice =0; indiceGiudice<NUMGIUDICI; indiceGiudice++){ //inserimento valori default
		listaGiudici[indiceGiudice].nomeGiudice = malloc(strlen("L")+1);
		strcpy(listaGiudici[indiceGiudice].nomeGiudice, "L");
		listaGiudici[indiceGiudice].punteggioTot = -1;
	}

	for (int indiceTabella =0; indiceTabella < NUMROWTABLE; indiceTabella++){
		inseritoLista = 0;

		for(int indiceGiudice=0; indiceGiudice<NUMGIUDICI; indiceGiudice++){
			if(strcmp(listaGiudici[indiceGiudice].nomeGiudice, tabella[indiceTabella].giudice)==0){
				listaGiudici[indiceGiudice].punteggioTot = listaGiudici[indiceGiudice].punteggioTot + tabella[indiceTabella].voto;
				inseritoLista = 1;
			} 
		}

		if(!inseritoLista){
			free(listaGiudici[posLibera].nomeGiudice); //rimuovo default
			listaGiudici[posLibera].nomeGiudice = malloc(strlen(tabella[indiceTabella].giudice) + 1);
			strcpy(listaGiudici[posLibera].nomeGiudice, tabella[indiceTabella].giudice);
			listaGiudici[posLibera].punteggioTot = tabella[indiceTabella].voto;
			posLibera++;
		}
	}

	for(int indiceGiudice =0; indiceGiudice< NUMGIUDICI; indiceGiudice++) {
		result.classificaGiudici[indiceGiudice].punteggioTot = -1;
		result.classificaGiudici[indiceGiudice].nomeGiudice = malloc(strlen("L")+1);
		strcpy(result.classificaGiudici[indiceGiudice].nomeGiudice, "L");
	}

	for(int i=0; i<posLibera; i++){
		for(int k=0; k<posLibera; k++) {
			if(result.classificaGiudici[i].punteggioTot < listaGiudici[k].punteggioTot){
				result.classificaGiudici[i].nomeGiudice = malloc(strlen(listaGiudici[k].nomeGiudice) + 1);
				strcpy(result.classificaGiudici[i].nomeGiudice, listaGiudici[k].nomeGiudice);
				result.classificaGiudici[i].punteggioTot = listaGiudici[k].punteggioTot;
				count = k;
			}
		}
		listaGiudici[count].punteggioTot =-1;
	}

	return &result;
}

int * esprimi_voto_1_svc(Voto* votazione, struct svc_req *reqstp){
	static int result;

	if (!isSetupTabella) setupTabella();

	result = -1;
	for (int indexTable = 0; indexTable < NUMROWTABLE; indexTable++){

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
