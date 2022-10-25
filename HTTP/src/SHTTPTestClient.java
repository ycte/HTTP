
/*
 * Simple client
 *
 * * usage:%java SHTTPTestClient -server <server> -servname <server name>
 * * * -port <server port> -parallel <# of threads> -files <file name>
 * * * * -T <time of test in seconds>
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class SHTTPTestClient {

    private Socket welcomeSocket;

    public final static int THREAD_COUNT = 2;
    private SHTTPTestThread[] threads;

    private static int TIME_NUM = 0;

    /* Constructor: starting all threads at once */
    public SHTTPTestClient(int serverPort, InetAddress serverIPAddress, int threadNum, String[] filename) {

        try {
            // create server socket
//            welcomeSocket = new Socket(serverIPAddress,serverPort);
//            System.out.println("Server started; listening at " + serverPort);

            // create thread pool
            threads = new SHTTPTestThread[threadNum];

            // start all threads
            for (int i = 0; i < threads.length; i++) {
                threads[i] = new SHTTPTestThread(serverIPAddress, serverPort, filename, TIME_NUM);
                threads[i].start();
//                Thread.sleep(TIME_NUM);
//                threads[i].interrupt();
            }
        } catch (Exception e) {
            System.out.println("Server construction failed.");
        } // end of catch

    } // end of Server

    public static void main(String[] args) throws Exception {

        // get server address
        String serverName = "localhost";
//        if (args.length >= 2)
//            serverName = args[1];
//        else {
//            System.out.println("usage:%java SHTTPTestClient -server <server> -servname <server name>\n" +
//                    "-port <server port> -parallel <# of threads> -files <file name>\n" +
//                    "-T <time of test in seconds>");
//        }
        if (args.length >= 4)
            serverName = args[3];
        else {
            System.out.println("usage:%java SHTTPTestClient -server <server> -servname <server name>\n" +
                    "-port <server port> -parallel <# of threads> -files <file name>\n" +
                    "-T <time of test in seconds>");
            return;
        }
        InetAddress serverIPAddress = InetAddress.getByName(serverName);

        // get server port
        int serverPort = 6789;
        if (args.length >= 6)
            serverPort = Integer.parseInt(args[5]);
        else {
            System.out.println("usage:%java SHTTPTestClient -server <server> -servname <server name>\n" +
                    "-port <server port> -parallel <# of threads> -files <file name>\n" +
                    "-T <time of test in seconds>");
            return;
        }

        // get # of threads
        int threadNum = 1;
        if (args.length >= 8)
            threadNum = Integer.parseInt(args[7]);
        else {
            System.out.println("usage:%java SHTTPTestClient -server <server> -servname <server name>\n" +
                    "-port <server port> -parallel <# of threads> -files <file name>\n" +
                    "-T <time of test in seconds>");
            return;
        }

        // get file name and time of test in seconds
        String[] fileName1 = new String[100];
        int fileNum = 0;
        if (args.length >= 10) {
            for(int i = 9; ; i++) {
                if (args[i].compareTo("-T") != 0) {
                    fileName1[i-9] = args[i];
                }
                else {
                    fileNum = i - 9;
                    break;
                }
            }
        }
        if (args.length >= fileNum+10) {
            TIME_NUM = Integer.parseInt(args[fileNum+10]);
        } else {
            System.out.println("usage:%java SHTTPTestClient -server <server> -servname <server name>\n" +
                    "-port <server port> -parallel <# of threads> -files <file name>\n" +
                    "-T <time of test in seconds>");
            return;
        }
        String[] fileName = new String[fileNum];
        for(int i = 0; i < fileNum; i++) fileName[i] = fileName1[i];
//        for(int i = 0; i < fileName.length; i++) {
//            System.out.println(fileName[i]);
//        }
        SHTTPTestClient clientServer = new SHTTPTestClient(serverPort, serverIPAddress, threadNum, fileName);
        clientServer.run();
    } // end of main

    // Wait for all threads to finish
    public void run() {

        try {
            for (SHTTPTestThread thread : threads) {
                thread.join();
            }
            System.out.println("All threads finished. Exit");
        } catch (Exception e) {
            System.out.println("Join errors");
        } // end of catch

    } // end of run
}
