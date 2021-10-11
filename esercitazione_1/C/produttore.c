#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

/**
 * Il produttore ha il compito di:
 * Leggere le linee scritte su terminale dall'utente
 * Scrivere sul file, passato come parametro all'invocazione, le linee ricevute
 * 
 * Sintassi invocazione: ./produttore input_file.txt
*/
int main(int argc, char* argv[]){
	/**
	 * Desrizione delle variabili:
	 * fd: File descriptor del file passato come parametro - indice elemento nella tabella dei file aperti del processo
	 * written: numero caratteri effettivamente scritti sul file passato come parametro
	 * file_out: puntatore a stringa nome del file da scrivere passato come parametro
	 * riga: stringa riga letta dall'utente
	*/
	int fd, written;
	char *file_out;
	char riga[MAX_STRING_LENGTH];
	
	/**
	 * Controllo dei parametri di invocazione
	 * Verifico la presenza del solo parametro: file da scrivere
	 * 
	 * Sintassi invocazione: ./produttore input_file.txt
	*/
	if (argc != 2){ 
		perror(" numero di argomenti sbagliato"); exit(1);
	} 
	
	file_out = argv[1];	//Stringa file input
	
	/**
	 * Apertura del file di input
	 * L'apertura viene fatta mediante l'utilizzo della primitiva open
	 * Return: File descriptor file aperto
	*/
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
	if (fd < 0){
		perror("P0: Impossibile creare/aprire il file");
		exit(2);
	}
	
	/**
	 * Lettura righe dall'utente
	 * Scrittura delle righe su file appena aperto
	*/
	printf("Inserisci le righe del file: [EOF per terminare l'inserimento] \n");
	while (gets(riga) != NULL) { 
		/* la gets legge tutta la riga, separatori inclusi, e trasforma il fine 
	       linea in fine stringa */
		riga[strlen(riga)+1]='\0';  
		riga[strlen(riga)]='\n'; // aggiungo il fine linea 
		written = write(fd, riga, strlen(riga)); // uso della primitiva
		if (written < 0){
			perror("P0: errore nella scrittura sul file");
			exit(3);
		}
	}	
	close(fd);
}
