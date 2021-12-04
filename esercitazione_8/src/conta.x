/* conta.x
 *
 */

struct Output{
	int caratteri;
	int parole;
	int linee;
  	int codiceErrore;
};

struct Input{
	string direttorio <50>;
	int soglia;
};

program CONTAPROG {
	version CONTAVERS {
    	Output CONTA(string <50>) = 1;
		int CONTA_FILE(Input) = 2;
	} = 1;
} = 0x20000015;
