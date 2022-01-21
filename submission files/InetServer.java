// Note: You must have TCP/IP loaded on your machine.

import java.io.*;   // Get the Input Output Libraries
import java.net.*;  // Get the Java networking libraries

class Worker extends Thread {     // Class definition
  Socket sock;                    // Class member, socket, local to Worker
  Worker (Socket s) {sock = s;}   // Constructor, assign arg s to loacl sock

  // The run method is what starts when run/start his class.
  public void run() {
    // Get I/O stream in & out from socket:
    PrintStream out = null;   // To massage our output into nicer formatting.
    BufferedReader in = null; // BufferedReader is simply an input formatter.
    try {
      in = new BufferedReader // Another bufferedReader massaging our input
        (new InputStreamReader(sock.getInputStream()));
      out = new PrintStream(sock.getOutputStream());
      // Note that this branch might not execute when expected (???):
      try {
        String name;
        name = in.readLine ();
        System.out.println("Looking up " + name);
        printRemoteAddress(name, out);        // prints name on outputstream.
      } catch (IOException x) {
        System.out.println("Server read error");
        x.printStackTrace ();
      }
      sock.close(); // Close this connection, but not the server
    } catch (IOException ioe) {System.out.println(ioe);}
  }

  /* The logic within this method does not actually matter for our program.
     the important part is that we are connecting our server and client.
     the out printstream is outputting to the socket. */
  static void printRemoteAddress (String name, PrintStream out) {
    try {
      out.println("Looking up " + name + "...");
      InetAddress machine = InetAddress.getByName (name);
      out.println("Host name : " + machine.getHostName ());
      out.println("Host IP : " + toText (machine.getAddress ()));
    } catch(UnknownHostException ex) {
      out.println ("Failed attempting to look up " + name);
    }
  }

  // This code just formats the ip address. Not of interest to us.
  static String toText (byte ip[]) {
    // make portable for 128 bit format
    StringBuffer result = new StringBuffer ();
    for (int i = 0; i < ip.length; ++ i) {
      if (i > 0) result.append(".");
      result.append (0xff & ip[i]);
    }
    return result.toString ();
  }
}


public class InetServer {
  // Our main function takes in an array of arguments, but we are not currently passing an args into the program.
  public static void main(String a[]) throws IOException {
    int q_len = 6;   /* Number of requests that can be handled at a time for OpSys to queue. For example
                       if the server receives more than 6 connections at the same exact before the sever has time
                       to spawn off a thread to make room in the queue, then the 7th request is thrown out.
                       This is very unlikely. */
    int port = 1565; /* We should probably be in the 45750 or 55000 range where the user ports reside, but the prof has
                        has had problems with startup programs.. (??). Don't go below 1025. */
    Socket sock;     // Allocate a socket object since we are using socket programming

    ServerSocket servsock = new ServerSocket(port, q_len);  // new ServerSocket type called servsock

    System.out.println
      ("Benjamin Chavez's Inet Server 1.0 starting up, listening at port 1565.\n");
    while (true) {                /* while true == forever */
      sock = servsock.accept();   // Serversock is blocked here waiting for a connection. When a connection is accepted, we put it into `sock`
      new Worker(sock).start();   // Once a connection is accpeted we Spawn a worker thread to handle the connecction
    }
  }
}
