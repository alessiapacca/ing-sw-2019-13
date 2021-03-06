package server;

import client.ReceiverInterface;
import client.info.SetupInfo;
import constants.Constants;
import server.model.player.ConcretePlayer;
import server.model.player.Figure;
import server.model.player.PlayerState;
import answers.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Starts socket and rmi server.
 * Handles all the clients that connect to the server.
 * @author Matteo Pacciani
 */
public class Server {

    /**
     * the client id that will be assigned to the next client that connects
     */
    private int nextClientId;

    /**
     * Contains the registry and the remote object for rmi connection
     */
    private ServerRMI serverRMI;

    /**
     *
     */
    private SocketServer socketServer;


    /**
     * A list where all the created gameManagers are stored
     */
    private List<GameManager> gameManagerList;

    /**
     * The gameManager to which new clients will be added to
     */
    private GameManager currentGameManager;

    /**
     * Maps client ids to {@link Client} objects
     */
    private Map<Integer, Client> idToClient;

    /**
     * Maps client names to client ids
     */
    private Map<String, Integer> nameToId;



    public Server(){

        idToClient = new HashMap<>();
        nameToId = new HashMap<>();

        gameManagerList = new ArrayList<>();
        currentGameManager = new GameManager(this);
        gameManagerList.add(currentGameManager);
    }

    /**
     * Creates the Runnable object that will be passed to the executor later
     * @throws RemoteException
     */
    private void startSocketRmi() throws RemoteException{
        socketServer = new SocketServer(Constants.SOCKET_PORT, this);
        serverRMI = new ServerRMI(this);
    }

    public Map<String, Integer> getNameToId() {
        return nameToId;
    }

    public Client getClientFromId(int clientID){
        return idToClient.get(clientID);
    }

    public Integer getIdFromName(String name){
        return nameToId.get(name);
    }

    public String getNameFromId(int clientId){
        return idToClient.get(clientId).getName();
    }

    public GameProxyInterface getGameProxy(){
        return serverRMI.getGameProxy();
    }

    /**
     * Get a new incremental unique id to assing to the connecting client
     * @return
     */
    public synchronized int getNewClientId(){
        int idToReturn = nextClientId;
        System.out.println("Added the clientID " + nextClientId);
        nextClientId++;
        return idToReturn;
    }

    /**
     * Removes the client from the database of the server
     * If the game is started and the client has not chosen the game character yet, it is excluded from active players list
     * Also removes the gameManager if this client was the last one connected to that gameManager
     */
    public void removeClient(int clientId){
        Client client = getClientFromId(clientId);
        String name = client.getName();
        GameManager gameManager = client.getGameManager();

        System.out.println("Removing client: " + name);


        //removing player from the active players list
        //if player setup is not complete, the player has not been added to the activePlayers list yet
        if(gameManager.getGameStarted() == 1 && client.isPlayerSetupComplete()) {
            client.getGameManager().removePlayerFromActivePlayers(name);
        }

        //removing this client from database on the server
        nameToId.remove(name);
        idToClient.remove(clientId);

        //removing ref to the gameManager
        if(gameManager.isGameOver() && gameManager.getActivePlayers().isEmpty()) {
            gameManagerList.remove(gameManager);
            System.out.println("GameManager removed");
        }

        System.out.println("Client removed: " + name);

    }

    /**
     * The method adds a Client object to the map that links id to clients
     */
    public synchronized void addClient(Client client){
        idToClient.put(client.getClientID(), client);
    }

    /**
     * Handles client connections. Distinguishes if client had already connected before, or if it is still connected.
     * Creates a new {@link Client} object if it is connecting for the first time.
     * Sends a setup request to only ask things that are needed by the server.
     *
     */
    public synchronized Integer addConnection(String name, String connectionType, SocketClientHandler socketClientHandler, ReceiverInterface receiverInterface){
        SetupRequestAnswer setupRequestAnswer = new SetupRequestAnswer();

        Integer clientID = getIdFromName(name);
        if(clientID == null){
            //player has never connected before
            clientID = getNewClientId();     //just gets the id
            Client client = new Client(clientID, getCurrentGameManager(), name);

            if(connectionType.equalsIgnoreCase("socket"))
                client.setSocketClientHandler(socketClientHandler);
            else if(connectionType.equalsIgnoreCase("rmi"))
                client.setReceiverInterface(receiverInterface);

            addClient(client);          //adds client to the hashmap

            nameToId.put(name, clientID);    //adds name to the hashmap

            if(client.getGameManager().isNoPlayer()){                       //decides skulls and map
                client.getGameManager().setNoPlayer(false);
                setupRequestAnswer.setFirstPlayer(true);
                client.setFirstPlayer(true);
            }

            setupRequestAnswer.setGameCharacter(true);
            setupRequestAnswer.setClientID(clientID);

            client.send(setupRequestAnswer);

        }else{
            //player is connecting, not for the first time
            //it may be already disconnected or still active

            Client client = getClientFromId(clientID);

            if(client.isConnected())       //client with the same name trying to connect 2 times simultaneously
                return null;


            if(connectionType.equalsIgnoreCase("socket"))
                client.setSocketClientHandler(socketClientHandler);
            else if(connectionType.equalsIgnoreCase("rmi"))
                client.setReceiverInterface(receiverInterface);

            //setting player connected so that his turn will be played
            if(client.getGameManager().getGameStarted() == 1){
                client.getGameManager().setActive(client.getPlayer());
            }

            //informing other players
            System.out.println(client.getName() + " fixed his wifi connection! Yeeehaw");
            if(client.getGameManager().getGameStarted() == 1) {
                client.getGameManager().sendEverybodyExcept(new MessageAnswer(
                        client.getName() + " fixed his wifi connection! Yeeehaw"), clientID);
            }

            //empty setupAnswer
            setupRequestAnswer.setClientID(clientID);
            setupRequestAnswer.setReconnection();
            client.send(setupRequestAnswer);
        }

        return clientID;
    }

    /**
     * Called when the client sends a setup answer containing infos as maps, skulls and game character.
     * Adds the client to the list of players that wait for the match to start
     * @param clientId the id of the client
     * @param setupInfo the sent setup info
     */
    public synchronized void setupPlayer(int clientId, SetupInfo setupInfo){

        Client client = getClientFromId(clientId);
        GameManager gameManager = client.getGameManager();
        SetupConfirmAnswer setupConfirmAnswer = new SetupConfirmAnswer();



        if(client.isPlayerSetupComplete()){

            if(client.getPlayer().getPlayerState() == PlayerState.TOBESPAWNED)
                setupConfirmAnswer.setSpawn(true);
            else if(client.getPlayer().getPlayerState() == PlayerState.DEAD)
                setupConfirmAnswer.setRespawn(true);

            if(gameManager.getGameStarted() == 1) {
                client.send(new GameBoardAnswer(client.getGameManager().getController().getCurrentGame().getCurrentGameBoard()));
                client.send(new PlayerHandAnswer(client.getPlayer().getHand()));
            }

            client.send(setupConfirmAnswer);
            return;
        }

        //else if setup is not complete

        if(gameManager.getGameStarted() == 1){
            setupConfirmAnswer.setServerOffline(true);
            client.send(setupConfirmAnswer);
            //removeClient(clientId);
            return;
        }

        if(client.isFirstPlayer() && !gameManager.isMapSkullsSet()) {
            //the gamemanager will manage illegal values
            gameManager.setInitialSkulls(setupInfo.getInitialSkulls());
            gameManager.setMapChoice(setupInfo.getMapChoice());
            gameManager.setMapSkullsSet(true);
        }

        ConcretePlayer concretePlayer = new ConcretePlayer(client.getName());
        concretePlayer.setClientID(clientId);
        concretePlayer.setState(PlayerState.TOBESPAWNED);
        setupConfirmAnswer.setSpawn(true);

        if(Figure.fromString(setupInfo.getCharacterName()) != null && gameManager.isCharacterFree(setupInfo.getCharacterName())){
            //player in Game has already been created
            concretePlayer.setPlayerCharacter(Figure.fromString(setupInfo.getCharacterName()));
        }
        else{
            concretePlayer.setPlayerCharacter(gameManager.getFreeFigure());
        }

        //adding the player to the game
        boolean added = gameManager.addPlayerBeforeMatchStarts(concretePlayer);

        //if max player is reached waits for a new gamemanager to be created
        while(!added){
            try{
                Thread.sleep(2000);
                added = currentGameManager.addPlayerBeforeMatchStarts(concretePlayer);
                //in the meantime, a new GameManager should have been created
                client.setGameManager(currentGameManager);
            }catch(InterruptedException e){
                //do nothing
            }
        }

        //player setup is ok
        client.setPlayerSetupComplete(true);

        //sending confirm with spawn and respawn
        client.send(setupConfirmAnswer);

        //sending stuff if player is reconnecting
        if(gameManager.getGameStarted()==1){
            client.send(new GameBoardAnswer(gameManager.getController().getCurrentGame().getCurrentGameBoard()));
            client.send(new PlayerHandAnswer(client.getPlayer().getHand()));
        }
    }

    public GameManager getGameManagerFromId(int clientID){
        return idToClient.get(clientID).getGameManager();
    }

    public GameManager getCurrentGameManager() {
        return currentGameManager;
    }

    /**
     * sets the current game manager, adding it to the game manager lists to store it
     * @param currentGameManager the current game manager
     */
    public void setCurrentGameManager(GameManager currentGameManager) {
        this.currentGameManager = currentGameManager;
        gameManagerList.add(currentGameManager);
    }

    public static void main(String[] args) throws RemoteException {

        Server server = new Server();
        server.startSocketRmi();

        ExecutorService executor = Executors.newCachedThreadPool();

        executor.submit(server.serverRMI);
        executor.submit(server.socketServer);
    }

}
