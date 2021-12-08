  
struct Giudice{
	string nomeGiudice <256>; 
	int punteggioTot;
}; 

struct Output{
	Giudice classificaGiudici [4]; 
}; 

struct Input{
	string nomeCandidato <256>;
	char tipoOp;
};
  
program OPERATION {
	version OPERATIONVERS {         
		Output CLASSIFICA_GIUDICI() = 1;        
        int ESPRIMI_VOTO(Input) = 2;
	} = 1;
} = 0x20000013;

