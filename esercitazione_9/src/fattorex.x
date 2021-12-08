const MAXLENGTHSTRING = 256;
const NUMGIUDICI = 3;

struct Giudice{
	string nomeGiudice <MAXLENGTHSTRING>; 
	int punteggioTot;
}; 

struct Classifica{
	Giudice classificaGiudici [NUMGIUDICI]; 
}; 

struct Voto{
	string nomeCandidato <MAXLENGTHSTRING>;
	char tipoOp;
};
  
program VOTAFATTOREX {
	version VOTAFATTOREXVERS {         
		Classifica CLASSIFICA_GIUDICI() = 1;        
        int ESPRIMI_VOTO(Voto) = 2;
	} = 1;
} = 0x20000020;

