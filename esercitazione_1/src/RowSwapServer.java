import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.StringTokenizer;

public class RowSwapServer extends Thread {
    private int port;
    private File file;

    public RowSwapServer(File file, int port) {
        this.file = file;
        this.port = port;
    }

    @Override
    public void run() {
        super.run();

        System.out.println("[RowSwapServer-" + port + "] avviato");

        DatagramSocket socket = null;
        DatagramPacket packet = null;
        byte[] buf = new byte[256];

        // creazione socket
        try {
            socket = new DatagramSocket(port);
            packet = new DatagramPacket(buf, buf.length);
            System.out.println("[RowSwapServer-" + port + "] Creata la socket: " + socket);
        } catch (SocketException e) {
            System.out.println("[RowSwapServer-" + port + "] Problemi nella creazione della socket: ");
            e.printStackTrace();
            System.exit(1);
        }

        int aLineIndex = -1;
        int bLineIndex = -1;
        StringTokenizer tokenizer = null;
        // attesa di richieste
        try {
            while (true) {
                buf = new byte[256];
                System.out.println("[RowSwapServer-" + port + "] In attesa di richieste...");
                packet.setData(buf);
                socket.receive(packet);

                // ricezione richiesta con le righe da swappare
                tokenizer = new StringTokenizer(ByteUtility.bytesToStringUTF(packet.getData()), "-");
                aLineIndex = Integer.parseInt(tokenizer.nextToken());
                bLineIndex = Integer.parseInt(tokenizer.nextToken());

                System.out.println("[RowSwapServer-" + port + "] Ricevuta richiesta di swap di righe: swapping riga " + aLineIndex + " con riga " + bLineIndex);

                // swap delle righe
                int status = LineUtility.swapLines(this.file, aLineIndex, bLineIndex);
                System.out.println("[RowSwapServer-" + port + "] Esito swap: " + (status < 0 ? "errore" : "successo") + ". Invio risposta al client..");
                packet.setData(ByteUtility.intToBytes(status));
                socket.send(packet);
            }
        } catch (IOException e) {
            System.err.println("[RowSwapServer-" + port + "] Problemi nella lettura: "
                    + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("[RowSwapServer-" + port + "] termina..");
        socket.close();
    }
}
