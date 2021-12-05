/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "scan.h"

/* Default timeout can be changed using clnt_control() */
static struct timeval TIMEOUT = { 25, 0 };

Output *
file_scan_1(argp, clnt)
	char **argp;
	CLIENT *clnt;
{
	static Output clnt_res;

	memset((char *)&clnt_res, 0, sizeof(clnt_res));
	if (clnt_call(clnt, file_scan, xdr_wrapstring, argp, xdr_Output, &clnt_res, TIMEOUT) != RPC_SUCCESS)
		return (NULL);
	return (&clnt_res);
}

int *
dir_scan_1(argp, clnt)
	Input *argp;
	CLIENT *clnt;
{
	static int clnt_res;

	memset((char *)&clnt_res, 0, sizeof(clnt_res));
	if (clnt_call(clnt, dir_scan, xdr_Input, argp, xdr_int, &clnt_res, TIMEOUT) != RPC_SUCCESS)
		return (NULL);
	return (&clnt_res);
}
