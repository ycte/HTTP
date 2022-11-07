import java.io.*;
import java.net.*;
import java.util.*;

public class SHTTPTestThread extends Thread {

//    final Socket welcomeSocket;
    String[] filename;
    InetAddress serverIPAddress;
    int serverPort;
    int TIME_NUM;
    public SHTTPTestThread(InetAddress serverIPAddress, int serverPort, String[] filename, int TIME_NUM) throws IOException {
//        welcomeSocket = new Socket(serverIPAddress,serverPort);
        this.serverIPAddress = serverIPAddress;
        this.serverPort = serverPort;
        this.filename = filename;
        this.TIME_NUM = TIME_NUM;
    }

    public void run() {
        System.out.println("Thread " + this + " started.");
        long timeSrcFlag = System.currentTimeMillis();
        while (true) {
            long timeDstFlag = System.currentTimeMillis();
            if (timeDstFlag - timeSrcFlag >= TIME_NUM * 1000L) {
                break;
            }
            for (String value : filename) {
                Socket welcomeSocket;
                try {
                    welcomeSocket = new Socket(serverIPAddress, serverPort);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                synchronized (welcomeSocket) {
                    try {
                        System.out.println(value);
                        String sentence = "GET " + value + " HTTP/1.0";

                        // write to server
                        DataOutputStream outToServer
                                = new DataOutputStream(welcomeSocket.getOutputStream());
                        long timeSrcFlagTemp = System.currentTimeMillis();
                        outToServer.writeBytes(sentence + '\n');
                        String htName =  InetAddress.getLocalHost().getHostName();
//                        System.out.println(htName);
                        outToServer.writeBytes("Host: "+ "yunxi.site" + "\n");
                        outToServer.writeBytes("Connection: close\n");
                        outToServer.writeBytes("User-agent: Mozillaphone/4.0 \n");
                        outToServer.writeBytes("Accept: text/html, image/gif, image/jpeg\n");
                        outToServer.writeBytes("Accept-language: en\n");
                        outToServer.writeBytes("\n");
                        System.out.println("written to server; waiting for server reply...");
//                        System.out.println("From Server:");

                        // create read stream and receive from server
                        BufferedReader inFromServer
                                = new BufferedReader(new InputStreamReader(welcomeSocket.getInputStream()));
                        long timeDstFlagTemp = System.currentTimeMillis();
                        SHTTPTestClient.timeSum += timeDstFlagTemp - timeSrcFlagTemp;
                        System.out.println(timeDstFlagTemp-timeSrcFlagTemp);
                        int bytesCnt = inFromServer.toString().getBytes().length;
                        SHTTPTestClient.byteSum += bytesCnt;
                        SHTTPTestClient.fileCntNum++;

                        // Debug response
                        String sentenceFromServer;
                        System.out.println(inFromServer.readLine());
                        while ((sentenceFromServer = inFromServer.readLine()) != null) {
                            System.out.println(sentenceFromServer);
                        }

                        welcomeSocket.close();
                    } catch (IOException e) {
                        System.out.println("E");
                    }
                }
            }// end of extract a request

//            serveARequest( s );

        } // end while
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
