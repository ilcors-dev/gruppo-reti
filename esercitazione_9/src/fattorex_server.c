/*
 * xfactor_s.c
 */

#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "fattorex.h"

#define N 6 //2 concorrenti per giudice
#define DIMSTRING 256

/*STATO INTERNO PRIVATO DEL SERVER*/
typedef struct{
	char candidato[DIMSTRING];
	char giudice[DIMSTRING];
	char categoria ;
	char nomeFile[DIMSTRING];
	char fase;
	int voto;
} Tupla;

//variabili globali statiche
static Tupla tabella[N];
static int isSetupTabella = 0;


/*Stato interno parzialmente implementato*/
void setupTabella(){
	int i;
	if (isSetupTabella == 1) return;

	strcpy(tabella[0].candidato, "Giuseppe");
	strcpy(tabella[0].giudice, "DJFrancesco");
	tabella[0].categoria = 'U';
	strcpy(tabella[0].nomeFile, "Giuseppe.txt");
	tabella[0].fase = 'A';
	tabella[0].voto = 100;

	strcpy(tabella[1].candidato, "Antonio");
	strcpy(tabella[1].giudice, "DJFrancesco");
	tabella[1].categoria = 'U';
	strcpy(tabella[1].nomeFile, "Antonio.txt");
	tabella[1].fase = 'A';
	tabella[1].voto = 100;

	strcpy(tabella[2].candidato, "Fabrizio");
	strcpy(tabella[2].giudice, "J-AX");
	tabella[2].categoria = 'U';
	strcpy(tabella[2].nomeFile, "Fabrizio.txt");
	tabella[2].fase = 'A';
	tabella[2].voto = 100;

	strcpy(tabella[3].candidato, "Filippo");
	strcpy(tabella[3].giudice, "J-AX");
	tabella[3].categoria = 'U';
	strcpy(tabella[3].nomeFile, "Filippo.txt");
	tabella[3].fase = 'A';
	tabella[3].voto = 100;

	strcpy(tabella[4].candidato, "Luisa");
	strcpy(tabella[4].giudice, "Nina");
	tabella[4].categoria = 'U';
	strcpy(tabella[0].nomeFile, "Luisa.txt");
	tabella[4].fase = 'A';
	tabella[4].voto = 100;

	strcpy(tabella[5].candidato, "Valeria");
	strcpy(tabella[5].giudice, "Nina");
	tabella[5].categoria = 'U';
	strcpy(tabella[5].nomeFile, "Valeria.txt");
	tabella[5].fase = 'A';
	tabella[5].voto = 100;

	isSetupTabella = 1;
	printf("Terminata inizializzazione struttura dati!\n");
}

//implementazione delle procedure definite nel file XDR
Classifica * classifica_giudici_1_svc(void * voidValue, struct svc_req *reqstp){
	Giudice listaGiudici[N];
	static Classifica res;
	int i, k, presente, ind=0, max, count =0;
	setupTabella();

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
	static int found;
	found = -1;
	int i, votoTot;

	setupTabella();

	for (i = 0; i < N; i++){

		printf("VOTO: %s %s %d\n", tabella[i].candidato, votazione->nomeCandidato, tabella[i].voto);

		if (strcmp(tabella[i].candidato, votazione->nomeCandidato) == 0){
			if (votazione->tipoOp == 'A'){
				tabella[i].voto++;
			}

			if (votazione->tipoOp == 'S'){
				tabella[i].voto--;
			}
			printf("VOTO: %d\n", tabella[i].voto);
			votoTot=tabella[i].voto;
			found = 0;
			break;
		}
	}
	if(found == 0)
		printf ("Risultato: \n\t Cantante = %s \n\t Voti = %d\n", votazione->nomeCandidato, votoTot);
	else 
		printf("Problemi nell'attribuzione del voto, nome non trovato\n");

	return (&found);
}
