/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package modulos;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

/**
 *
 * @author mohammed
 */


// For every client's connection we call this class
public class ClienteHilo extends Thread{
  private String nombreCliente = null;
  private DataInputStream entradaTexto = null;
  private PrintStream salidaTexto = null;
  private Socket clienteSocket = null;
  private final ClienteHilo[] threads;
  private int maxClientsCount;

  public ClienteHilo(Socket clientSocket, ClienteHilo[] threads) {
    this.clienteSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    ClienteHilo[] threads = this.threads;

    try {
      /*
       * Create input and output streams for this client.
       */
      entradaTexto = new DataInputStream(clienteSocket.getInputStream());
      salidaTexto = new PrintStream(clienteSocket.getOutputStream());
      String name;
      while (true) {
        salidaTexto.println("Ingrese su nombre.");
        name = entradaTexto.readLine().trim();
        if (name.indexOf('@') == -1) {
          break;
        } else {
          salidaTexto.println("No puede contener '@' su nombre.");
        }
      }

      /* Welcome the new the client. */
      salidaTexto.println("Bienvenido " + name
          + " al Chat UPS Cuenca.\nPara salir escriba /salir en una nueva linea.");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            nombreCliente = "@" + name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this) {
            threads[i].salidaTexto.println("*** Nuevo usuario: " + name
                + " conectado a la sala!!! ***");
          }
        }
      }
      /* Start the conversation. */
      while (true) {
        String line = entradaTexto.readLine();
        if (line.startsWith("/salir")) {
          break;
        }
        /* If the message is private sent it to the given client. */
        if (line.startsWith("@")) {
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].nombreCliente != null
                      && threads[i].nombreCliente.equals(words[0])) {
                    threads[i].salidaTexto.println("<" + name + "> " + words[1]);
                    /*
                     * Echo this message to let the client know the private
                     * message was sent.
                     */
                    this.salidaTexto.println(">" + name + "> " + words[1]);
                    break;
                  }
                }
              }
            }
          }
        } else {
          /* The message is public, broadcast it to all other clients. */
          synchronized (this) {
            for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i].nombreCliente != null) {
                threads[i].salidaTexto.println("<" + name + "> " + line);
              }
            }
          }
        }
      }
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] != this
              && threads[i].nombreCliente != null) {
            threads[i].salidaTexto.println("*** El usuario " + name
                + " ha salido de la sala !!! ***");
          }
        }
      }
      salidaTexto.println("*** Adios " + name + " ***");

      /*
       * Clean up. Set the current thread variable to null so that a new client
       * could be accepted by the server.
       */
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }
      /*
       * Close the output stream, close the input stream, close the socket.
       */
      entradaTexto.close();
      salidaTexto.close();
      clienteSocket.close();
    } catch (IOException e) {
    }
  }
}
