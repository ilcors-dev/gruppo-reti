package src;

import java.io.*;

public class FileUtility {

	static protected void trasferisci_a_byte_file_binario(DataInputStream src,
			DataOutputStream dest, long dimFile) throws IOException {

	    int buffer;
	    try {
			for (buffer = 0; buffer < dimFile; buffer++) {
	    		dest.write(src.read());
	    	}
	    	dest.flush();
	    }
	    catch (EOFException e) {
	    	System.out.println("Problemi, i seguenti: ");
	    	e.printStackTrace();
	    }
	}
}