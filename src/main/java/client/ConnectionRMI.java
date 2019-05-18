package client;

import server.GameProxyInterface;
import server.ServerAnswer.ServerAnswer;
import server.controller.playeraction.Action;
import server.model.player.PlayerAbstract;

import java.io.Serializable;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionRMI extends Connection implements Serializable, ReceiverInterface, Remote {

    private GameProxyInterface gameProxy;
    private String name = "rmiconnection";
    private String mapChoice;
    private int initialSkulls;

    private boolean playerNameSet = false;
    private boolean initialSkullsSet = false;
    private boolean mapSet = false;
    private  transient GameModel gameModel;
    private GameProxyInterface game;
    private int clientID;
    private static final String SERVER_ADDRESS  = "localhost";
    private static final String REGISTRATION_ROOM_NAME = "gameproxy";
    private static final int REGISTRATION_PORT = 1099;


    public String getMap(){
        try{
            return gameProxy.getMap();
        }
        catch (RemoteException re){
            System.out.println("Could not take the map from the server");
            re.printStackTrace();
        }
        return "No one has chosen yet";
    }

    public ConnectionRMI(int clientID){
        this.clientID = clientID;
        this.gameModel = new GameModel();
    }

    public void setClientID(int clientID){
        this.clientID = clientID;
    }

    @Override
    public int getClientID() {
        return clientID;
    }

    @Override
    public void send(Action action){
        try{
            boolean serverAnswer = game.makeAction(this.clientID, action);
        }
        catch(RemoteException re){
            System.out.println("Could not make the action");
        }
    }

    @Override
    public void publishMessage(ServerAnswer answer){
    //TODO has to store the answer in the ClientModel
        //this.ClientModel.storeResponse(message);
        return;
    }

    @Override
    public GameModel getGameModel(){
        return this.gameModel;
    }


    @Override
    public int getInitialSkulls(){
        try {
            return gameProxy.getInitialSkulls();
        }
        catch (RemoteException re){
            System.out.println("Could not get the initial skulls from the server");
            re.printStackTrace();
        }
        return 0;
    }

    @Override
    public void configure() throws RemoteException, NotBoundException {
        try{
            this.game = initializeRMI();
        }
        catch (AlreadyBoundException e)
        {
            System.out.println("Exception while binding");
        }
    }

    @Override
    public GameProxyInterface initializeRMI() throws RemoteException, NotBoundException, AlreadyBoundException {
        System.out.println("Connecting to the Remote Object... ");

        System.out.println("Connecting to the registry... ");
        Registry registry = LocateRegistry.getRegistry(SERVER_ADDRESS,REGISTRATION_PORT);
        gameProxy = (GameProxyInterface) registry.lookup(REGISTRATION_ROOM_NAME);

        System.out.println("Registering... ");

        /*Registry registryClient = LocateRegistry.createRegistry(1000);
        registryClient.bind(this.name, this);
        System.out.println("I am exporting the remote object...");
        (ReceiverInterface) UnicastRemoteObject.exportObject(this, 1000)*/

        gameProxy.setClientRMI(this);
        gameProxy.register(this);
        setClientID(gameProxy.getClientID());
        this.gameModel.setClientID(clientID);

        System.out.println("Your ClientID is " + this.clientID);

        return gameProxy;
    }


    @Override
    public void add(String playerName, int mapClient, int initialSkulls) throws RemoteException{
        //gameProxy.addClient(player, this.clientID); //i add a line in the hashmap of the gameModel

        if(clientID == 0) {
            System.out.println("Trying to send your choice of initial skulls to the server...");
            while (initialSkullsSet == false) {
                try {
                    initialSkullsSet = gameProxy.sendInitialSkulls(initialSkulls);
                    initialSkullsSet = true;
                } catch (RemoteException re) {
                    System.out.println("Could not send the initial skulls");
                    re.printStackTrace();
                }
            }
            this.initialSkulls = gameProxy.getInitialSkulls();
        }

        System.out.println("Trying to send your name to the server...");
        while (playerNameSet == false) {
            try{
                playerNameSet = gameProxy.sendPlayer(playerName);
                playerNameSet = true;
            }
            catch(RemoteException re){
                System.out.println("Could not send the player");
                re.printStackTrace();
            }
        }

        System.out.println("The server received your name...");

        if(clientID == 0){
            System.out.println("Sending your chosen map to the server...");
            while(mapSet == false){
                try{
                    mapSet = gameProxy.sendMap(mapClient);
                    mapSet = true;
                }
                catch(RemoteException re){
                    System.out.println("Could not send the map");
                }
            }
            System.out.println("The server received your choice of the map...");
            this.mapChoice = gameProxy.getMap();

        }


        //TODO manca la parte in cui salvo le scelte del client!

    }
}

