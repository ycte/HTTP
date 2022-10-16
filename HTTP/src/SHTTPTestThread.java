import java.io.*;
import java.net.*;
import java.util.*;

public class SHTTPTestThread extends Thread {

    Socket welcomeSocket;
    String[] filename;
    public SHTTPTestThread(InetAddress serverIPAddress, int serverPort, String[] filename) throws IOException {
        welcomeSocket = new Socket(serverIPAddress,serverPort);
        this.filename = filename;
    }

    public void run() {

        System.out.println("Thread " + this + " started.");
//        while (true) {
            // get a new request connection
            Socket s = null;

            synchronized (welcomeSocket) {
                try {
//                    s = welcomeSocket.accept();
//                    System.out.println("Thread " + this
//                            + " process request " + s);
                    for(int i = 0; i < filename.length; i++) {
//                        System.out.println(filename[i]);
                        String sentence = "GET " + filename[i] + " HTTP/1.0";

                        // write to server
                        DataOutputStream outToServer
                                = new DataOutputStream(welcomeSocket.getOutputStream());
                        outToServer.writeBytes(sentence + '\n');
                        outToServer.writeBytes("Host: localhost\n");
                        outToServer.writeBytes("\n");
                        System.out.println("written to server; waiting for server reply...");
                        System.out.println("From Server:");
//                        // create read stream and receive from server
//                        BufferedReader inFromServer
//                                = new BufferedReader(new InputStreamReader(welcomeSocket.getInputStream()));
//                        String sentenceFromServer;
////                        System.out.println(inFromServer.readLine());
//                        while ((sentenceFromServer = inFromServer.readLine()) != null) {
//                            System.out.println(sentenceFromServer);
//                        }
////                        welcomeSocket.close();
                    }
                } catch (IOException e) {
                }
            } // end of extract a request

//            serveARequest( s );

//        } // end while

    } // end run

    private void serveARequest(Socket connSock) {

        try {
            // create read stream to get input
            BufferedReader inFromClient =
                    new BufferedReader(new InputStreamReader(connSock.getInputStream()));
            String clientSentence = inFromClient.readLine();

            // process input
            String capitalizedSentence = clientSentence.toUpperCase() + '\n';

            // send reply
            DataOutputStream outToClient =
                    new DataOutputStream(connSock.getOutputStream());
            outToClient.writeBytes(capitalizedSentence);

            connSock.close();

            System.out.println("Finish a request");

        } catch (Exception e) {
            System.out.println("Exception happened in Thread " + this);
        } // end of catch

    } // end of serveARequest

} // end ServiceThread
