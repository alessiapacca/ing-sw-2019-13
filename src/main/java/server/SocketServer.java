package server;

import constants.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Creates a socket that accept client connections. When a client socket is created, it is passed to a new thread
 * that will handle that client as long as it is connected.
 * @author Tommaso Pegolotti
 * @author Matteo Pacciani
 */
public class SocketServer implements Runnable{
    private int port;
    private ExecutorService executorService;
    private Server server;

    SocketServer(int port, Server server){
        this.port = port;
        this.server = server;
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void run(){
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(Constants.SOCKET_PORT);
            System.out.println("SOCKET SERVER RUNNING");

            startAcceptingConnections(serverSocket);

        } catch (IOException e) {
            System.out.println("Error during ServerSocket initialization, closing program...");
            e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Accepts socket connections as long as the server is running. Starts a new thread for every socket connected client
     * @param serverSocket the server socket
     */
    private void startAcceptingConnections(ServerSocket serverSocket){
            while (true) {    //NOSONAR
                try {
                    SocketClientHandler socketClientHandler = new SocketClientHandler(serverSocket.accept(), server);
                    executorService.submit(socketClientHandler);
                }catch(IOException e){
                    //
                }
            }
    }
}
