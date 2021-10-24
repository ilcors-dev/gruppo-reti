package testConcurrentClass;

public class multipleClient {
    public static void main(String[] args) {
        String[] args1 = new String[] { "127.0.0.1", "54321", ".\\1", "1" };
        String[] args2 = new String[] { "127.0.0.1", "54321", ".\\2", "1" };
        String[] args3 = new String[] { "127.0.0.1", "54321", ".\\3", "1" };
        String[] args4 = new String[] { "127.0.0.1", "54321", ".\\4", "1" };
        String[] args5 = new String[] { "127.0.0.1", "54321", ".\\5", "1" };
        String[] args6 = new String[] { "127.0.0.1", "54321", ".\\6", "1" };
        PutFileClientTest p1 = new PutFileClientTest(args1);
        PutFileClientTest p2 = new PutFileClientTest(args2);
        PutFileClientTest p3 = new PutFileClientTest(args3);
        PutFileClientTest p4 = new PutFileClientTest(args4);
        PutFileClientTest p5 = new PutFileClientTest(args5);
        PutFileClientTest p6 = new PutFileClientTest(args6);

        p1.start();
        p2.start();
        p3.start();
        p4.start();
        p5.start();
        p6.start();
    }
}
