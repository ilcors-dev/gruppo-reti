/* Server Select
 * 	Nuovo figlio per ogni richiesta file.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <dirent.h>

#define DIM_BUFF 100
#define LENGTH_FILE_NAME 100
#define max(a, b) ((a) > (b) ? (a) : (b))

/*Funzione conteggio file in un direttorio*/
/********************************************************/
int conta_file(char *name)
{
	DIR *dir;
	struct dirent *dd;
	int count = 0;
	dir = opendir(name);
	if (dir == NULL)
		return -1;
	while ((dd = readdir(dir)) != NULL)
	{
		printf("Trovato il file %s\n", dd->d_name);
		count++;
	}
	/*Conta anche direttorio stesso e padre*/
	printf("Numero totale di file %d\n", count);
	closedir(dir);
	return count;
}
/********************************************************/
/*Funzione conteggio parole in un file*/
/********************************************************/
int conta_parole_cancellate(char *msg)
{
	int numOcc = 0, fdread, fdwrite, lenParola, curDimBuff = 0, dimStringFileName = 0;
	char filename[LENGTH_FILE_NAME];
	char buff[DIM_BUFF], temp;

	while (*(msg) != ';' && *(msg) != '\0' && *(msg) != '\n')
	{
		filename[dimStringFileName] = *(msg);
		msg++;
		dimStringFileName++;
	}
	if (*(msg) != '\0' && *(msg) != '\n') msg++;
	filename[dimStringFileName] = '\0';
	printf("Nome file estrapolato %s\n", filename);
	printf("Parola da rimuovere %s\n", msg);

	lenParola = strlen(msg);
	if ((fdread = open(filename, O_RDONLY)) == -1)
		return -1;
	if ((fdwrite = open("temp.txt", O_CREAT | O_WRONLY | O_TRUNC, 0644)) == -1)
		return -1;
	while ((read(fdread, &temp, 1)) > 0)
	{
		printf("Carattere letto: %c\n", temp);
		if (curDimBuff == lenParola)
		{		
			if ((temp >= 'A' && temp <= 'Z') || (temp >= 'a' && temp <= 'z')) //Parola composta
			{
				write(fdwrite, buff, curDimBuff);
				write(fdwrite, &temp, 1);
				curDimBuff = 0;
			} else { //Parola cancellata
				numOcc++;
				curDimBuff = 0;
				printf("Carattere scritto dopo parola rifiutata: %c\n", temp);
				write(fdwrite, &temp, 1);
			}	 
		}
		else //Non ho ancora niente di significativo buffer
		{
			if (curDimBuff < lenParola && temp == msg[curDimBuff]) //Ho un carattere interessante
			{
				printf("Carattere letto interessante: %c\n", temp);
				buff[curDimBuff] = temp;
				curDimBuff++;
			}
			else
			{
				printf("Carattere letto poco utile: %c\n", temp);
				buff[curDimBuff] = temp;
				write(fdwrite, buff, curDimBuff+1);
				curDimBuff = 0;
			}
		}
	}
	buff[curDimBuff] = '\0';
	printf("Buff restante: %s\n", buff);
	if (strcmp(buff, msg) == 0) numOcc++;
	close(fdwrite); close(fdread);
	rename("temp.txt",filename);
	printf("Numero totale di parole %d\n", numOcc);
	return numOcc;
}
/********************************************************/

void gestore(int signo)
{
	int stato;
	printf("esecuzione gestore di SIGCHLD\n");
	wait(&stato);
}
/********************************************************/

int main(int argc, char **argv) {
	int listenfd, connfd, udpfd, fd_file, nready, maxfdp1;
	const int on = 1;
	char buff[DIM_BUFF], nome_file[LENGTH_FILE_NAME], nomedir[LENGTH_FILE_NAME];
	fd_set rset;
	int len, nread, nwrite, num, ris, port;
	struct sockaddr_in cliaddr, servaddr;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if (argc != 2)
	{
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}

	nread = 0;
	while (argv[1][nread] != '\0')
	{
		if ((argv[1][nread] < '0') || (argv[1][nread] > '9'))
		{
			printf("Terzo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[1]);
	if (port < 1024 || port > 65535)
	{
		printf("Porta scorretta...");
		exit(2);
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER E BIND ---------------------------- */
	memset((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);
	printf("Server avviato\n");

	/* CREAZIONE SOCKET TCP ------------------------------------------------ */
	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	if (listenfd < 0)
	{
		perror("apertura socket TCP ");
		exit(1);
	}
	printf("Creata la socket TCP d'ascolto, fd=%d\n", listenfd);

	if (setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
	{
		perror("set opzioni socket TCP");
		exit(2);
	}
	printf("Set opzioni socket TCP ok\n");

	if (bind(listenfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
	{
		perror("bind socket TCP");
		exit(3);
	}
	printf("Bind socket TCP ok\n");

	if (listen(listenfd, 5) < 0)
	{
		perror("listen");
		exit(4);
	}
	printf("Listen ok\n");

	/* CREAZIONE SOCKET UDP ------------------------------------------------ */
	udpfd = socket(AF_INET, SOCK_DGRAM, 0);
	if (udpfd < 0)
	{
		perror("apertura socket UDP");
		exit(5);
	}
	printf("Creata la socket UDP, fd=%d\n", udpfd);

	if (setsockopt(udpfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on)) < 0)
	{
		perror("set opzioni socket UDP");
		exit(6);
	}
	printf("Set opzioni socket UDP ok\n");

	if (bind(udpfd, (struct sockaddr *)&servaddr, sizeof(servaddr)) < 0)
	{
		perror("bind socket UDP");
		exit(7);
	}
	printf("Bind socket UDP ok\n");

	/* AGGANCIO GESTORE PER EVITARE FIGLI ZOMBIE -------------------------------- */
	signal(SIGCHLD, gestore);

	/* PULIZIA E SETTAGGIO MASCHERA DEI FILE DESCRIPTOR ------------------------- */
	FD_ZERO(&rset);
	maxfdp1 = max(listenfd, udpfd) + 1;

	/* CICLO DI RICEZIONE EVENTI DALLA SELECT ----------------------------------- */
	for (;;) {
		/* ASSEGNO A MASCHERA FILE DESCRIPTOR DI SOCKET UDP E TCP */
		FD_SET(listenfd, &rset);
		FD_SET(udpfd, &rset);

		if ((nready = select(maxfdp1, &rset, NULL, NULL, NULL)) < 0)
		{
			if (errno == EINTR)
				continue;
			else
			{
				perror("select");
				exit(8);
			}
		}

		/* GESTIONE RICHIESTE DI GET DI UN FILE ------------------------------------- */
		if (FD_ISSET(listenfd, &rset)) {
			printf("Ricevuta richiesta di get di un file\n");
			len = sizeof(struct sockaddr_in);
			if ((connfd = accept(listenfd, (struct sockaddr *)&cliaddr, &len)) < 0)
			{
				if (errno == EINTR)
					continue;
				else
				{
					perror("accept");
					exit(9);
				}
			}

			if (fork() == 0) { 
				close(listenfd);
				printf("Dentro il figlio, pid=%i\n", getpid());
				
				struct dirent *dp;
				struct stat sb;	
				char dirPath[255];
				char fileName[255];
				char tempD_name[255];

				while(read(connfd, &nomedir, sizeof(nomedir)) > 0){
					
					getcwd(dirPath, sizeof(dirPath));
					strcat(dirPath,"/"); 
					strcat(dirPath,nomedir);

					printf("Richiesto Dir %s\n", dirPath);

					DIR *dir = opendir(dirPath);

					if (!dir) {
						printf("Dir inesistente\n");
						write(connfd, "N", 1);
					}
					else {
						write(connfd, "S", 1);
						while ((dp = readdir(dir)) != NULL) {
							if (strcmp(dp->d_name, ".") != 0 && strcmp(dp->d_name, "..") != 0){

								strcpy(tempD_name,nomedir); 
								strcat(tempD_name,"/"); 
								strcat(tempD_name,dp->d_name);
					
								if(stat(tempD_name, &sb) == 0 && S_ISDIR(sb.st_mode)) {
									DIR *dir2 = opendir(tempD_name);

									while ((dp = readdir(dir2)) != NULL) {;
										if (strcmp(dp->d_name, ".") != 0 && strcmp(dp->d_name, "..") != 0) {
											strncpy(fileName, dp->d_name, 254);
											fileName[255] = '\0';
											write(0, fileName, sizeof(fileName));
											if (write(connfd, fileName, sizeof(fileName)) < 0) {
												perror("write");
												break;
											}
										}
									}
									closedir(dir2);
								}
							}
						}

						if (write(connfd, "\0", sizeof("\0")) < 0) {
							perror("write");
							break;
						}
					}
					closedir(dir);
				}

				/*la connessione assegnata al figlio viene chiusa*/
				printf("Figlio %i: termino\n", getpid());
				shutdown(connfd, 0);
				shutdown(connfd, 1);
				close(connfd);
				exit(0);
			} //figlio-fork
		}	  /* fine gestione richieste di file */

		/* GESTIONE RICHIESTE DI CONTEGGIO ------------------------------------------ */
		if (FD_ISSET(udpfd, &rset))
		{
			printf("Ricevuta richiesta di conteggio file\n");

			len = sizeof(struct sockaddr_in);
			if (recvfrom(udpfd, &nome_file, sizeof(nome_file), 0, (struct sockaddr *)&cliaddr, &len) < 0)
			{
				perror("recvfrom");
				continue;
			}

			printf("Richiesto eliminazione parole da file %s\n", nome_file);
			num = conta_parole_cancellate(nome_file);
			printf("Risultato del conteggio: %i\n", num);

			/*
			* Cosa accade se non commentiamo le righe di codice qui sotto?
			* Cambia, dal punto di vista del tempo di attesa del client,
			* l'ordine col quale serviamo le due possibili richieste?
			* Cosa cambia se utilizziamo questa realizzazione, piuttosto
			* che la prima?
			*
			*/
			/*
			printf("Inizio sleep\n");
			sleep(30);
			printf("Fine sleep\n");*/

			ris = htonl(num);
			if (sendto(udpfd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, len) < 0)
			{
				perror("sendto");
				continue;
			}
		} /* fine gestione richieste di conteggio */

	} /* ciclo for della select */
	/* NEVER ARRIVES HERE */
	exit(0);
}
