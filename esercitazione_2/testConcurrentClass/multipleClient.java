package testConcurrentClass;

public class multipleClient {
    public static void main(String[] args) {
        String[] args1 = new String[]{"127.0.0.1", "54321", "DIRETTORIO_TOREPLACE" ,"800"};
        String[] args2 = new String[]{"127.0.0.1", "54321", "DIRETTORIO_TOREPLACE" ,"800"};
        String[] args3 = new String[]{"127.0.0.1", "54321", "DIRETTORIO_TOREPLACE" ,"800"};
        String[] args4 = new String[]{"127.0.0.1", "54321", "DIRETTORIO_TOREPLACE" ,"800"};
        PutFileClientTest p1 = new PutFileClientTest(args1);
        PutFileClientTest p2 = new PutFileClientTest(args2);
        PutFileClientTest p3 = new PutFileClientTest(args3);
        PutFileClientTest p4 = new PutFileClientTest(args4);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
    }
}
