package server;


import answers.ServerAnswer;
import answers.SocketInfo;
import client.info.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Thread that manages the connection with a particular client.
 * @author Matteo Pacciani
 */
public class SocketClientHandler implements Runnable {

    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    /**
     * The id of the client managed by this thread
     */
    private Integer clientID;

    private Server server;

    private boolean keepThreadAlive;


    public SocketClientHandler(Socket socket, Server server){
        try {
            this.outputStream = new ObjectOutputStream(socket.getOutputStream());
            this.inputStream = new ObjectInputStream(socket.getInputStream());
            this.server = server;
            clientID = -1;
            keepThreadAlive = true;
        } catch (IOException e) {
            System.console().printf("ERROR DURING INITIALIZATION PROCESS");
            e.printStackTrace();
        }
    }

    /**
     * Waits for an action and processes it. If the client disconnects, an IOException is caught
     * and the client is either removed or disconnected. In both case, the thread is stopped
     */
    @Override
    public void run() {

        //the following loop waits for client action
        //the first thing required is the name
        while(keepThreadAlive){ //NOSONAR
            try {
                Info infoRead = (Info) inputStream.readObject();
                processAction(infoRead);
            } catch (IOException e) {
                System.out.println("IOException caught");
                try{
                    if(clientID != -1){
                        if(server.getClientFromId(clientID).getGameManager().getGameStarted() == 1 &&
                                server.getClientFromId(clientID).getGameManager().isGameOver())
                            server.removeClient(clientID);
                        else
                            server.getClientFromId(clientID).disconnectClient();

                        keepThreadAlive = false;
                    }
                }catch(NullPointerException npe){
                    //do nothing
                    e.printStackTrace();
                }
                break;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Processes the action calling the methods of {@link GameManager}.
     * @param info the received action
     */
    private void processAction(Info info) {
        if(info instanceof NameInfo){
            NameInfo nameInfo = (NameInfo) info;

            clientID = server.addConnection(nameInfo.getName(), "socket",
                    this, null);        //automatically sends setupAnswer

            if(clientID == null)            //client is already connected from another client, stops the thread
                keepThreadAlive = false;
        }

        else if(info instanceof SetupInfo){
            SetupInfo setupInfo = (SetupInfo) info;
            server.setupPlayer(clientID, setupInfo);
        }
        else if(info instanceof AsynchronousInfo){
            server.getGameManagerFromId(clientID).getController()
                    .makeAsynchronousAction(clientID, ((AsynchronousInfo) info).getInfo());
        }
        else if(info instanceof ReconnectInfo){
            server.getGameManagerFromId(clientID).setActive(server.getClientFromId(clientID).getPlayer());
        }
        else{
            server.getGameManagerFromId(clientID).getController().makeAction(clientID, info);
        }
    }

    private synchronized void writeToStream(SocketInfo socketInfo){
        try {
            outputStream.reset();
            outputStream.writeObject(socketInfo);
            outputStream.flush();
        }catch(IOException e){
            server.getClientFromId(clientID).disconnectClient();
        }

    }


    /**
     * Sends the answer to the client
     * @param answer
     */
    public void publishSocketMessage(ServerAnswer answer){
        //sends the answer to the client through the output stream, encapsulating the answer in a SocketInfo object
        //if the match is started, various IDs are added to the SocketInfo object to let the client know about the match status

        //this method will only be called by the controller's thread
        SocketInfo socketInfo = new SocketInfo();
        socketInfo.setServerAnswer(answer);

        try{
            GameManager gameManager = server.getGameManagerFromId(clientID);

            socketInfo.setStartGame(gameManager.getGameStarted());
            socketInfo.setCurrentID(gameManager.getController().getCurrentGame().getCurrentPlayer().getClientID());
            socketInfo.setCurrentCharacter(gameManager.getController().getCurrentGame().getCurrentPlayer().getCharacterName());
        }catch(NullPointerException|IndexOutOfBoundsException e){
            //dont't send
        }
        writeToStream(socketInfo);

    }

    public int getClientID() {
        return clientID;
    }
}
