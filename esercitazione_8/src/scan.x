/*
 * scan.x
 */

struct Output {
	int caratteri;
	int parole;
	int linee;
  	int errore;
};

struct Input {
	string directory <50>;
	int soglia;
};

program SCANPROGRAM {
	version SCANVERS {
    	Output file_scan(string <50>) = 1;
		int dir_scan(Input) = 2;
	} = 1;
} = 0x20000015;
