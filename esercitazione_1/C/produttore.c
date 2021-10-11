#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

/**
 * Il produttore ha il compito di:
 * Leggere le linee del fil scritte dal cliente
 * Scrivere sul file passato come parametro
 * 
 * Sintassi invocazione: ./produttore input_file.txt
*/
int main(int argc, char* argv[]){
	/**
	 * Desrizione delle variabili:
	 * fd: File descriptor del file passato come parametro
	 * written: numero caratteri scritti sul file
	 * file_out: puntatore a stringa nome del file da scrivere passato come parametro
	 * riga: riga letta dall'utente
	*/
	int fd, written;
	char *file_out;
	char riga[MAX_STRING_LENGTH];
	
	/**
	 * Controllo dei parametri di invocazione
	 * Verifico la presenza del solo parametro: file da scrivere
	 * 
	 * Sintassi invocazione: ./consumatore stringa_caratteri_da_eliminare input_file.txt
	*/
	if (argc != 2){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
	
	file_out = argv[1];	//Stringa file input
	
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd < 0){
		perror("P0: Impossibile creare/aprire il file");
		exit(2);
	}
	
	printf("Inserisci le righe del file: [EOF per terminare l'inserimento] \n");
	while (gets(riga) != NULL) { 
		/* la gets legge tutta la riga, separatori inclusi, e trasforma il fine 
	       linea in fine stringa */
		// aggiungo il fine linea
		riga[strlen(riga)+1]='\0';  
		riga[strlen(riga)]='\n';  
		written = write(fd, riga, strlen(riga)); // uso della primitiva
		if (written < 0){
			perror("P0: errore nella scrittura sul file");
			exit(3);
		}
	}	
	close(fd);
}
