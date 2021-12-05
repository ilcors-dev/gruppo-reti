/* count_server.c */

#include <stdio.h>
#include <rpc/rpc.h>
#include <sys/types.h>
#include <unistd.h>
#include <dirent.h>
#include <fcntl.h>
#include "scan.h"

#define DIM 100

/********************************************************/
Output *file_scan_1_svc(char **input, struct svc_req *rp)
{
	static Output result;
	int fd_file, nread;
	char currentCar, precCar = "";

	result.caratteri = 0;
	result.parole = 0;
	result.linee = 0;
	result.errore = -1;

	fd_file = open(*input, O_RDONLY);
	if (fd_file < 0)
	{
		printf("File inesistente\n");
		return (&result);
	}
	else
	{
		result.errore = 0;
		while ((nread = read(fd_file, &currentCar,1))>0)
		{
			result.caratteri = result.caratteri + 1;
			if((currentCar == ' ' || currentCar == '\n') && precCar != ' ' && precCar != '\n') result.parole = result.parole + 1;
			if(currentCar == '\n') result.linee = result.linee + 1;
		}
		printf("Ho letto %d characters, %d words e %d lines\n", result.caratteri, result.parole, result.linee);
		close(fd_file);
		return (&result);
	}
}

int *dir_scan_1_svc(Input *input, struct svc_req *rp)
{
	static int result = 0;
	DIR *dir;
	struct dirent *dd;
	int fd_file;
	off_t fileSize;

	result = -1;

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
		if (fileSize >= input->soglia)
		{
			result++;
		}
	}
	printf("Numero totale di file nel directory richiesto %d\n", result);

	return (&result);
}
