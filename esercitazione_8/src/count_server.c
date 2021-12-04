/* count_server.c */

#include <stdio.h>
#include <rpc/rpc.h>
#include <sys/types.h>
#include <unistd.h>
#include <dirent.h>
#include <fcntl.h>
#include "count.h"

#define DIM 100

/********************************************************/
void conta(int fd, int *characters, int *words, int *lines)
{
	*characters = 0;
	*words = 0;
	*lines = 0;

	int nread, index;
	char car;
	char buffer;
	char stringBuffer[DIM];

	while (1)
	{
		index = 0;
		do
		{ //leggo a characters (spazi e \n iniziali)
			nread = read(fd, &car, 1);
			*characters = *characters + 1;
			if (car == '\n') //new line
				*lines = *lines + 1;
			stringBuffer[index] = car;
			index = index + 1;
		} while (((car == ' ') || (car == '\n')) && (nread > 0));

		if (nread <= 0)
		{
			*characters = *characters - 1;
			return;
		}
		else
			buffer = car;

		while (1)
		{ //leggo a characters tutto il resto
			nread = read(fd, &car, 1);
			if (nread > 0)
			{
				*characters = *characters + 1;
				buffer = car;

				stringBuffer[index] = car;
				index = index + 1;
			}
			else
				return;
			// CONTA OCCORRENZE PAROLA
			if ((buffer == ' ') || (buffer == '\n'))
			{
				if (buffer == '\n')
					*lines = *lines + 1;
				*words = *words + 1;
				/*if (strstr(stringBuffer,parola)!=NULL){
					printf("DEBUG: print occurrence %s \n", stringBuffer);
					*occorrenze = *occorrenze +1;
				}*/
				memset(stringBuffer, 0, sizeof(stringBuffer));
				index = 0;
				break;
			}
		}
	}
}

/********************************************************/
Output *conta_1_svc(char **input, struct svc_req *rp)
{
	static Output result;
	int fd_file, nread;
	char cCorr;

	result.characters = 0;
	result.words = 0;
	result.lines = 0;
	result.errorCode = -1;

	printf("Richiesto file %s \n", *input);

	fd_file = open(*input, O_RDONLY);
	if (fd_file < 0)
	{
		printf("File inesistente\n");
		return (&result);
	}
	else
	{
		result.errorCode = 0;
		/* Conteggio characters */
		conta(fd_file, &(result.characters), &(result.words), &(result.lines));

		printf("Ho letto %d characters, %d words e %d lines\n", result.characters,
			   result.words, result.lines);
		close(fd_file); //TODO
		return (&result);
	}
}

int *conta_file_1_svc(Input *input, struct svc_req *rp)
{
	static int result = 0;
	DIR *dir;
	struct dirent *dd;
	int i, fd_file;
	off_t fileSize;

	result = -1;

	printf("Richiesto directory %s\n", input->directory);
	if ((dir = opendir(input->directory)) == NULL)
		return (&result);

	result = 0;
	while ((dd = readdir(dir)) != NULL)
	{

		fd_file = open(dd->d_name, O_RDONLY);
		if (fd_file < 0)
		{
			printf("File inesistente\n");
			return (&result);
		}
		fileSize = lseek(fd_file, 0L, SEEK_END);
		/*memcpy(stringBuffer, &dd->d_name[0], strlen(input->suffisso));
			stringBuffer[strlen(input->suffisso)+1] = '\0';
			if(strstr(stringBuffer,input->suffisso) != NULL)*/
		if (fileSize >= input->threshold)
		{
			printf("DEBUG: candidate file size %ld\n", fileSize);
			result++;
		}
	}
	printf("Numero totale di file nel directory richiesto %d\n", result);

	return (&result);
}
