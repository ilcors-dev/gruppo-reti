import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

// Consumatore e' un filtro
public class ConsumatoreCleaned {
    public static void main(String[] args) {
        String filter = null;
        FileReader r = null;
        char ch;
        int x;

        if (args.length > 2) {
            System.out.println("Utilizzo: consumatore <filterprefix> <inputFilename> oppure");
            System.out.println("Utilizzo: consumatore <inputFilename>");
            System.exit(0);
        }

        try {
            // se args.length == 2 allora il filtro è passato come parametro
            // altrimenti è da leggere da un file
            if (args.length == 2) {
                filter = args[0];
                r = new FileReader(args[1]);
            } else {
                r = new FileReader(args[0]);
                filter = getFilter(r);
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

    public static String getFilter(FileReader r) {
        String filter = "";
        int x;

        try {
            while ((char) (x = r.read()) != '\n') {
                filter += (char) x;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filter;
    }
}
