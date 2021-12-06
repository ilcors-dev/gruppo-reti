#!/bin/bash
echo "compiling from .x file..";
rpcgen scan.x;
gcc scanClient.c scan_clnt.c scan_xdr.c -g -o client 2> /dev/null;
gcc scan_svc.c scanServer.c scan_xdr.c -g -o server;
