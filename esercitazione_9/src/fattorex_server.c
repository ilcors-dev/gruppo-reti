/*
 * xfactor_s.c
 */

#include <rpc/rpc.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "fattorex.h"

#define N 9 //3 concorrenti per giudice

/*STATO INTERNO PRIVATO DEL SERVER*/
typedef struct{
	char* candidato;
	char* giudice;
	char categoria ;
	char* nomeFile;
	char fase;
	int voto;
} Riga;

//variabili globali statiche
static Riga t[N];
static int inizializzato = 0;


/*Stato interno parzialmente implementato*/
void setupTabella(){
	int i;
	if (inizializzato == 1) return;

	for (i = 0; i < N; i++){

		t[i].candidato = malloc(2);
		strcpy(t[i].candidato, "L");

		t[i].giudice = malloc(2);
		strcpy(t[i].giudice, "L");

		t[i].categoria = 'L';

		t[i].nomeFile = malloc(2);
		strcpy(t[i].nomeFile, "L");

		t[i].fase = 'L';

		t[i].voto = -1;
	}

	free(t[1].candidato);
	t[1].candidato = malloc(strlen("Brasco") + 1);
	strcpy(t[1].candidato, "Brasco");
	free(t[1].giudice);
	t[1].giudice = malloc(strlen("Bowie") + 1);
	strcpy(t[1].giudice, "Bowie");
	t[1].categoria = 'U';
	free(t[1].nomeFile);
	t[1].nomeFile = malloc(strlen("BrascoProfile.txt") + 1);
	strcpy(t[1].nomeFile, "BrascoProfile.txt");
	t[1].fase = 'A';
	t[1].voto = 100;

	free(t[2].candidato);
	t[2].candidato = malloc(strlen("Viga") + 1);
	strcpy(t[2].candidato, "Viga");
	free(t[2].giudice);
	t[2].giudice = malloc(strlen("Winehouse") + 1);
	strcpy(t[2].giudice, "Winehouse");
	t[2].categoria = 'D';
	free(t[2].nomeFile);
	t[2].nomeFile = malloc(strlen("alfredo.txt") + 1);
	strcpy(t[2].nomeFile, "alfredo.txt");
	t[2].fase = 'S';
	t[2].voto = 50;

	free(t[3].candidato);
	t[3].candidato = malloc(strlen("Pippo") + 1);
	strcpy(t[3].candidato, "Pippo");
	free(t[3].giudice);
	t[3].giudice = malloc(strlen("Bowie") + 1);
	strcpy(t[3].giudice, "Bowie");
	t[3].categoria = 'D';
	free(t[3].nomeFile);
	t[3].nomeFile = malloc(strlen("canzone.txt") + 1);
	strcpy(t[3].nomeFile, "canzone.txt");
	t[3].fase = 'S';
	t[3].voto = 200;

	free(t[4].candidato);
	t[4].candidato = malloc(strlen("Mike") + 1);
	strcpy(t[4].candidato, "Mike");
	free(t[4].giudice);
	t[4].giudice = malloc(strlen("Steve") + 1);
	strcpy(t[4].giudice, "Steve");
	t[4].categoria = 'D';
	free(t[4].nomeFile);
	t[4].nomeFile = malloc(strlen("track.txt") + 1);
	strcpy(t[3].nomeFile, "track.txt");
	t[4].fase = 'S';
	t[4].voto = 400;

	inizializzato = 1;
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
			if(strcmp(listaGiudici[k].nomeGiudice, t[i].giudice)==0){
				listaGiudici[k].punteggioTot = listaGiudici[k].punteggioTot + t[i].voto;
				presente = 1;
			} 
		}

		if(presente == 0){
			free(listaGiudici[ind].nomeGiudice);
			listaGiudici[ind].nomeGiudice = malloc(strlen(t[i].giudice) + 1);
			strcpy(listaGiudici[ind].nomeGiudice, t[i].giudice);
			listaGiudici[ind].punteggioTot = t[i].voto;
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

		printf("VOTO: %s %s %d\n", t[i].candidato, votazione->nomeCandidato, t[i].voto);

		if (strcmp(t[i].candidato, votazione->nomeCandidato) == 0){
			if (votazione->tipoOp == 'A'){
				t[i].voto++;
			}

			if (votazione->tipoOp == 'S'){
				t[i].voto--;
			}
			printf("VOTO: %d\n", t[i].voto);
			votoTot=t[i].voto;
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
