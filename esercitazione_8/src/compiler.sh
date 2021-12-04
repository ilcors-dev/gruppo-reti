#!/bin/bash
echo "compiling from .x file..";
rpcgen count.x;
gcc count_client.c count_clnt.c count_xdr.c -g -o client 2> /dev/null;
gcc count_svc.c count_server.c count_xdr.c -g -o server;