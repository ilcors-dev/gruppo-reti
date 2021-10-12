import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// Consumatore e' un filtro
public class Consumatore {
    public static void main(String[] args) {
        String filter = "";
        FileReader r = null;
        int x;
        char ch;

        if (args.length > 2) {
            System.out.println("Utilizzo: consumatore <filterprefix> <inputFilename> oppure");
            System.out.println("Utilizzo: consumatore <filterprefix> '<' <inputFilename>");
            System.exit(0);
        }

        // rimuoviamo eventuali caratteri duplicati dal parametro filtro passato dall'utente
        for (int i = 0; i < args[0].length(); i++) {
            char atIndex = args[0].charAt(i);
            if (filter.indexOf(atIndex) == -1) {
                filter += atIndex;
            }
        }

        System.out.println("Filtro specificato: " + filter + "\n");

        try {
            // se args.length == 2 allora il filtro è passato come parametro insieme al file
            // altrimenti il file è passato mediante ridirezione input 
            if (args.length == 2) {
                r = new FileReader(args[1]);
            } else {
                r = new FileReader(FileDescriptor.in);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File non trovato");
            System.exit(1);
        }

        try {
            while ((x = r.read()) >= 0) {
                ch = (char) x;
                // se il carattere non è tra quelli da filtrare, printa
                if (filter.indexOf(ch) == -1)
                    System.out.print(ch);
            }
            r.close();
        } catch (IOException ex) {
            System.out.println("Errore di input");
            System.exit(2);
        }
    }
}
