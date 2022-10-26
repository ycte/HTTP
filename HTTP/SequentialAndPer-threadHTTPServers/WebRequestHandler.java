/**
 ** XMU CNNS Class Demo Basic Web Server
 **/
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

class WebRequestHandler {

    static boolean _DEBUG = true;
    static int     reqCount = 0;

    String WWW_ROOT;
    Socket connSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;

    String urlName;
    String fileName;
    File fileInfo;

    public WebRequestHandler(Socket connectionSocket, 
			     String WWW_ROOT) throws Exception
    {
        reqCount ++;

	    this.WWW_ROOT = WWW_ROOT;
	    this.connSocket = connectionSocket;

	    inFromClient =
	      new BufferedReader(new InputStreamReader(connSocket.getInputStream()));

	    outToClient =
	      new DataOutputStream(connSocket.getOutputStream());

    }

    public void processRequest() 
    {

	try {
	    mapURL2File();

	    if ( fileInfo != null ) // found the file and knows its info
	    {
		    outputResponseHeader();
		    outputResponseBody();
	    } // dod not handle error

	    connSocket.close();
	} catch (Exception e) {
	    outputError(400, "Server error");
	}



    } // end of processARequest

    private void mapURL2File() throws Exception 
    {
				
	    String requestMessageLine = inFromClient.readLine();
	    DEBUG("Request " + reqCount + ": " + requestMessageLine);
		// String requestMessageLine1 = inFromClient.readLine();
	    // DEBUG(requestMessageLine1);
	    // process the request
	    String[] request = requestMessageLine.split("\\s");
		
	    if (request.length < 2 || !request[0].equals("GET"))
	    {
		    outputError(500, "Bad request");
		    return;
	    }

	    // parse URL to retrieve file name
	    urlName = request[1];
	    DEBUG(urlName);
		if (urlName.equals("/"))
			urlName = "index.html";
	    else if ( urlName.startsWith("/") == true )
	       urlName  = urlName.substring(1);

        // debugging
        // if (_DEBUG) {
        //    String line = inFromClient.readLine();
        //    while ( !line.equals("") ) {
        //       DEBUG( "Header: " + line );
        //       line = inFromClient.readLine();
        //    }
        // }

	    // map to file name
	    fileName = WWW_ROOT + urlName;
	    DEBUG("Map to File name: " + fileName);
	    fileInfo = new File( fileName );
	    if ( !fileInfo.isFile() ) 
	    {
		    outputError(404,  "Not Found");
		    fileInfo = null;
	    }

    } // end mapURL2file


    private void outputResponseHeader() throws Exception 
    {
	    outToClient.writeBytes("HTTP/1.0 200 Document Follows\r\n");
	    outToClient.writeBytes("Set-Cookie: MyCool433Seq12345\r\n");

		// Time in the format of Java currentTimeMill
		// rfc1123-date = wkday "," SP date1 SP time SP "GMT"
		// date1        = 2DIGIT SP month SP 4DIGIT
		SimpleDateFormat formatter= new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date date = new Date(System.currentTimeMillis());
//        System.out.println("Start time:" + formatter.format(date));
		Date date1 = formatter.parse("wed, 26 oct 2022 15:37:15 CST");
//        System.out.println("Start time:" + formatter.format(date1));
		outToClient.writeBytes("Date: "+formatter.format(date));

		String htName =  InetAddress.getLocalHost().getHostName();
		outToClient.writeBytes("Server: "+htName);

	    if (urlName.endsWith(".jpg"))
	        outToClient.writeBytes("Content-Type: image/jpeg\r\n");
	    else if (urlName.endsWith(".gif"))
	        outToClient.writeBytes("Content-Type: image/gif\r\n");
	    else if (urlName.endsWith(".html") || urlName.endsWith(".htm"))
	        outToClient.writeBytes("Content-Type: text/html\r\n");
	    else
	        outToClient.writeBytes("Content-Type: text/plain\r\n");
    }

    private void outputResponseBody() throws Exception 
    {

	    int numOfBytes = (int) fileInfo.length();
	    outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
	    outToClient.writeBytes("\r\n");
	
	    // send file content
	    FileInputStream fileStream  = new FileInputStream (fileName);
	
	    byte[] fileInBytes = new byte[numOfBytes];
	    fileStream.read(fileInBytes);
		// DEBUG(fileInBytes.toString());
	    outToClient.write(fileInBytes, 0, numOfBytes);
    }

    void outputError(int errCode, String errMsg)
    {
	    try {
	        outToClient.writeBytes("HTTP/1.0 " + errCode + " " + errMsg + "\r\n");
	    } catch (Exception e) {}
    }

    static void DEBUG(String s) 
    {
       if (_DEBUG)
          System.out.println( s );
    }
}
