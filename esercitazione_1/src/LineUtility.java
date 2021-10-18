/*
 * LineUtility.java
 *
 * Classe usata per definire alcuni metodi utili nella selezione delle linee di
 * un file
 *
 */

import java.io.*;

public class LineUtility {
    static final int EXCEPTION_THROWN = -1;
    static final int SAME_INDEX = -2;
    static final int LINE_NOT_FOUND = -3;

    /**
     * Swappa due linee di un file passato come parametro.
     * @param file
     * @param aIndex indice della riga da scambiare con bIndex
     * @param bIndex indice della riga da scambiare con aIndex
     * @return 0 => se tutto è andato bene, < 0 se qualcosa è andato storto
     */
    static int swapLines(File file, int aIndex, int bIndex) {
        if (aIndex == bIndex) {
            return SAME_INDEX;
        }

        int i = 1;
        String a = null;
        String b = null;
        String line = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while (((line = reader.readLine()) != null) && (a == null || b == null)) {
                if (i == aIndex) {
                    a = line;
                } else if (i == bIndex) {
                    b = line;
                }
                i++;
            }

            reader.close();

            if (a == null || b == null) {
                return LINE_NOT_FOUND;
            }

            reader = new BufferedReader(new FileReader(file));

            StringBuilder builder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.equals(a)) {
                    builder.append(b);
                } else if (line.equals(b)) {
                    builder.append(a);
                } else {
                    builder.append(line);
                }
                builder.append(System.lineSeparator());
            }

            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            writer.write(builder.toString());

            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return EXCEPTION_THROWN;
        } catch (IOException e) {
            e.printStackTrace();
            return EXCEPTION_THROWN;
        }

        return 0;
    }
}