import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Produttore {
    public static void main(String[] args) {
        BufferedReader in = null;

        if (args.length != 1) {
            System.out.println("Utilizzo: produttore <inputFilename>");
            System.exit(0);
        }

        in = new BufferedReader(new InputStreamReader(System.in));

        FileWriter fout = null;
        String inputl = null;
        try {
            fout = new FileWriter(args[0]);
            System.out.println("Inserisci linee del file - [EOF per terminare]:");
            while ((inputl = in.readLine()) != null) {
                inputl += System.lineSeparator();
                fout.write(inputl, 0, inputl.length());
            }
            fout.close();
        } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
            System.exit(1); // uscita con errore, intero positivo a livello di sistema Unix
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2); // uscita con errore, intero positivo a livello di sistema Unix
        }
    }
} 
