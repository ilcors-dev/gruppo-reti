/*
 * conta.x
 */

struct Output {
	int characters;
	int words;
	int lines;
  	int errorCode;
};

struct Input {
	string directory <50>;
	int threshold;
};

program COUNTPROGRAM {
	version CONTAVERS {
    	Output CONTA(string <50>) = 1;
		int CONTA_FILE(Input) = 2;
	} = 1;
} = 0x20000015;
