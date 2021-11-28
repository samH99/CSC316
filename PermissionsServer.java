import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

/** distributed program that works with permissions information represented using a global table. */
public class PermissionsServer {
  /** Port number used by the server.  */
  public static final int SERVER_PORT = 26132;

  // Arraylists that hold the information regardign the users the objects and the table that grants permissions
  public static ArrayList<String> users = new ArrayList<String>();
  public static ArrayList<String> objects = new ArrayList<String>();
  public static ArrayList<String> table = new ArrayList<String>();

  public static void main( String[] args ) {
    // Make a socket for sending and receiving messages.
    DatagramSocket sock = null;
    try {
      sock = new DatagramSocket( SERVER_PORT );
    } catch( IOException e ){
      System.err.println( "Can't create socket: " + e );
      System.exit( -1 );
    }

    // Get information from users.txt and puts it in the users ArrayList
    try {
      File file = new File("users.txt");
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        users.add(scanner.nextLine());
      }
      scanner.close();
     } catch (FileNotFoundException e) {
      e.printStackTrace();
     } 

     // Get information from objects.txt and puts it in the objects ArrayList
     try {
      File file = new File("objects.txt");
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        objects.add(scanner.nextLine());
      }
      scanner.close();
     } catch (FileNotFoundException e) {
      e.printStackTrace();
     } 

     // Get information from table.txt and puts it in the table ArrayList
     try {
      File file = new File("table.txt");
      Scanner scanner = new Scanner(file);
      while (scanner.hasNextLine()) {
        table.add(scanner.nextLine());
      }
      scanner.close();
     } catch (FileNotFoundException e) {
      e.printStackTrace();
     } 
    
    // Reusable packet for receiving messages, hopefully big enough
    // for any message we'll receive.
    byte[] recvBuffer = new byte [ 1024 ];
    DatagramPacket recvPacket = new DatagramPacket( recvBuffer, recvBuffer.length );

    // Keep reading messages and sending responses.
    try{
      while( true ){
        // Get a packet.
        sock.receive( recvPacket );

        // Turn it into a string.
        String str = new String( recvBuffer, 0, recvPacket.getLength() );
      
        // splits the string apart to use the user and the object seperately
        String[] sparts = str.split(" ");

        // the index the user is at
        int u = users.indexOf(sparts[0]);
        // the index the object is at
        int o = objects.indexOf(sparts[1]);

        // String made up of the user index, the object index and the command
        str = u+" "+o+" "+sparts[2];

        // if the table lookup return is -1 that means that the user doesn't have that permission
        if(table.indexOf(str) != -1){
            str = "Access granted";
        }else{
            str = "Access denied";
        } 
        
        // Turn the string into a datagram packet, and send it back where the
        // messagee came from.
        byte[] sendBuffer = str.getBytes();
        DatagramPacket sendPacket = new DatagramPacket( sendBuffer, sendBuffer.length,
                                                        recvPacket.getAddress(),
                                                        recvPacket.getPort() );
        sock.send( sendPacket );
      }
      // if there is an error proccessing and an exception is thrown
    } catch( Exception e ){
      System.err.println( "Error communicating with a client." );
    }
  }
}
