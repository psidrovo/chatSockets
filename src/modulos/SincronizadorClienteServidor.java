

package modulos;

import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
/**
 *
 * @author mohammed
 */

// the Server class
public class SincronizadorClienteServidor {
   // The server socket.
  private static ServerSocket servirdorSocket = null;
  // The client socket.
  private static Socket clienteSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections.
  private static final int maximoClientes = 10;
  private static final ClienteHilo[] hilosCliente = new ClienteHilo[maximoClientes];

  public static void main(String args[]) {

    // The default port number.
    int numeroPuerto = 2222;
    if (args.length < 1) {
      System.out.println("Usage: java MultiThreadChatServerSync <portNumber>\n"
          + "Now using port number=" + numeroPuerto);
    } else {
      numeroPuerto = Integer.parseInt(args[0]);
    }

    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      servirdorSocket = new ServerSocket(numeroPuerto);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a client socket for each connection and pass it to a new client
     * thread.
     */
    while (true) {
      try {
        clienteSocket = servirdorSocket.accept();
        int i = 0;
        for (i = 0; i < maximoClientes; i++) {
          if (hilosCliente[i] == null) {
            (hilosCliente[i] = new ClienteHilo(clienteSocket, hilosCliente)).start();
            break;
          }
        }
        if (i == maximoClientes) {
          PrintStream os = new PrintStream(clienteSocket.getOutputStream());
          os.println("Servidor ocupado. Pruebelo despues.");
          os.close();
          clienteSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }  
}
