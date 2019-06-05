package client;

import exceptions.GameAlreadyStartedException;
import server.GameProxyInterface;
import server.model.map.GameMap;
import view.ServerAnswer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ConnectionRMI extends UnicastRemoteObject implements Serializable, Connection, ReceiverInterface {

    private GameProxyInterface gameProxy;
    private String name = "rmiconnection";
    private String mapChoice;
    private GameMap map;
    private int initialSkulls;

    private boolean error = false;
    private boolean playerNameSet = false;
    private boolean initialSkullsSet = false;
    private boolean characterNameSet = false;
    private boolean mapSet = false;
    private GameModel gameModel;
    private GameProxyInterface game;
    private int clientID;
    private static final String SERVER_ADDRESS  = "localhost";
    private static final String REGISTRATION_ROOM_NAME = "gameproxy";
    private static final int REGISTRATION_PORT = 1099;
    private int startGame = 0;

    public ConnectionRMI(int clientID) throws RemoteException{
        this.clientID = clientID;
        this.gameModel = new GameModel();
    }

    public String getCurrentCharacter(){
        try{
            return gameProxy.getCurrentCharacter();
        }
        catch(RemoteException e){
            System.out.println("Exception caught");
        }
        return null;
    }

    public GameMap getMap(){
        try{
            return gameProxy.getMap();
        }
        catch (RemoteException re){
            System.out.println("Could not take the map from the server");
            re.printStackTrace();
        }
        return null;
    }

    @Override
    public int getStartGame(){
        try{
            return gameProxy.getStartGame();
        }
        catch(RemoteException e)
        {
            System.out.println("Exception while starting the game");
        }
        return 0;
    }

    public void sendConnection() {

    }


    public void setClientID(int clientID){
        this.clientID = clientID;
    }

    public GameProxyInterface getGameProxy(){
        return this.gameProxy;
    }

    @Override
    public int getGrenadeID(){
        try{
            return gameProxy.getGrenadeID();
        }
        catch(RemoteException e){
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getCurrentID(){
        try{
            return gameProxy.getCurrentID();
        }
        catch(RemoteException e){
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public boolean CharacterChoice(String name) {
        if ((name.toUpperCase().equals("SPROG") || (name.toUpperCase().equals("DESTRUCTOR")) || (name.toUpperCase().equals("BANSHEE")) || (name.toUpperCase().equals("DOZER")) || (name.toUpperCase().equals("VIOLET")))) {
            try{
                if(gameProxy.isCharacterTaken(name)){
                    return false;
                }
                else {
                    return true;
                }
            }
            catch(RemoteException re){
                System.out.println("Exception caught");
            }
        }
        return false;
    }


    @Override
    public int getClientID() {
        return clientID;
    }

    public void send(Info action){
        try{
            game.makeAction(this.clientID, action);
        }
        catch(Exception re){
            System.out.println("Could not make the action");
            re.printStackTrace();
        }
    }

    @Override
    public void publishMessage(ServerAnswer answer) throws RemoteException{
        this.gameModel.saveAnswer(answer);
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

    /*@Override
    public void startMatch(){
        try{
            gameProxy.startMatch();
        }
        catch (RemoteException e){
            System.out.println("Exception while starting the timer");
        }
    }*/

    public boolean getError(){
        return this.error;
    }

    @Override
    public void configure() {
        try{
            this.game = initializeRMI();
            if(game == null)
                this.error = true;
        }
        catch(RemoteException|NotBoundException re){
            System.out.println("Exception while initializing");
        }
    }

    public void print() throws RemoteException{
        System.out.println("Test Connection");
    }


    public GameProxyInterface initializeRMI() throws RemoteException, NotBoundException {
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
        try{
            gameProxy.register(this);
        }
        catch(GameAlreadyStartedException e){
            System.out.println("The Game already started!");
            return null;
        }
        setClientID(gameProxy.getClientID());
        this.gameModel.setClientID(clientID);

        System.out.println("Your ClientID is " + this.clientID);

        return gameProxy;
    }

    public void addPlayerCharacter(String name){
        System.out.println("Trying to send the name of your character to the server...");
        while (characterNameSet == false) {
            try{
                characterNameSet = gameProxy.addPlayerCharacter(name, clientID);
                gameProxy.addMapPlayer(clientID);
                characterNameSet = true;
                System.out.println("Name sent to to the server!");
            }
            catch(RemoteException re){
                System.out.println("Could not send the character");
                re.printStackTrace();
            }
        }
    }

    public void startTimer() {

    }

    public void saveAnswer(ServerAnswer answer) {

    }


    @Override
    public void add(String playerName, int mapClient, int initialSkulls){
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
            try{
                this.initialSkulls = gameProxy.getInitialSkulls();
            }
            catch(RemoteException e){
                System.out.println("Remote Exception");
            }
        }

        System.out.println("Trying to send your name to the server...");
        while (playerNameSet == false) {
            try{
                playerNameSet = gameProxy.sendPlayer(playerName, clientID);
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
            try{
                this.mapChoice = gameProxy.getNameMap();
            }
            catch(RemoteException e){
                System.out.println("Remote Exception caught");
            }

        }

        //TODO manca la parte in cui salvo le scelte del client!
    }

    public int getMapIndexFromName(String name) {
        switch (name){
            case "map11.txt":
                return 0;
            case "map12.txt":
                return 1;
            case "map21.txt":
                return 2;
            case "map22.txt":
                return 3;
        }
        return  -1;
    }

    @Override
    public String getMapName() {
        try{
            return gameProxy.getMapName();
        }
        catch (RemoteException re){
            System.out.println("Could not take the map name from the server");
            re.printStackTrace();
        }
        return "No one has chosen yet";
    }
}

