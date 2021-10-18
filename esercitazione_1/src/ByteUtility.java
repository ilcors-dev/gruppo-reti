import java.io.*;

public class ByteUtility {
    public static int bytesToInt(byte[] bytes) throws IOException {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInStream = new DataInputStream(byteInStream);
        return dataInStream.readInt();
    }

    public static byte[] intToBytes(int integer) throws IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream dataOutStream = new DataOutputStream(byteOutStream);
        dataOutStream.writeInt(integer);
        return byteOutStream.toByteArray();
    }

    public static String bytesToStringUTF (byte[] bytes) throws IOException {
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(bytes);
        DataInputStream dataInStream = new DataInputStream(byteInStream);
        return dataInStream.readUTF();
    }

    public static byte[] stringUTFToBytes (String string) throws IOException {
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        DataOutputStream dataOutStream = new DataOutputStream(byteOutStream);
        dataOutStream.writeUTF(string);
        return byteOutStream.toByteArray();
    }
}
