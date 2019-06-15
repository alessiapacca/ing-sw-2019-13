package client;

import client.weapons.Weapon;
import server.controller.PlayerDiedAnswer;
import server.controller.SpawnCommandAnswer;
import server.model.map.GameMap;
import server.model.player.GameCharacter;
import server.model.player.PlayerAbstract;
import server.model.player.PlayerBoard;
import view.*;

import java.io.Serializable;
import java.util.*;

public class GameModel extends Observable implements Serializable {
    // so that the GUI can be an observer of this class and
    // this class is an observer of the model GAMESTATE


    private GameBoardAnswer gameBoard;
    private MapAnswer map;
    private int mapnum;
    private PlayerBoardAnswer playerBoard;
    private PlayerHandAnswer playerHand;
    private List<Weapon> weapons;
    private int clientID;
    private static int lastClientID = 0;
    private ListOfWeaponsAnswer weaponList;
    public boolean toSpawn;
    private boolean clientChoice;
    private List<Info> grenadeAction = new ArrayList<>();
    private int numberOfGrenades;



    public GameModel(){ //THERE IS A NEW gamemodel for every client!
        toSpawn = true;
    }

    public void setGrenadeAction(List<Info> action){
        this.grenadeAction = action;
    }

    public List<Info> getGrenadeAction(){
        return this.grenadeAction;
    }

    public int getNumberOfGrenades(){
        return this.numberOfGrenades;
    }

    public void setToSpawn(boolean decision){
        toSpawn = decision;
    }

    public List<String> getCharactersNames(){
        return gameBoard.getCharacterNames();
    }

    public List<String> getPlayersNames(){
        return gameBoard.getPlayerNames();
    }

    public void setClientID(int clientID){
        this.clientID = clientID;
    }

    public boolean getClientChoice(){
        return this.clientChoice;
    }

    public void setClientChoice(boolean choice){
        this.clientChoice = choice;
    }


    public static int getNextClientID(){
        return lastClientID;
    }


    public Integer getClientID(){
        return clientID;
    }

    public void saveAnswer(ServerAnswer answer) {

        //the GameModel will save the answer of the Server
        // updating the model elements needed and notifying the observers
        if(answer instanceof ResetAnswer) {
            clientChoice = false;
            setChanged();
            notifyObservers("reset");
        }

        if (answer instanceof GameBoardAnswer) {
            gameBoard = (GameBoardAnswer) answer;
            setChanged();
            notifyObservers("GameBoard");
        }

        if (answer instanceof InitialMapAnswer) {
            mapnum = ((InitialMapAnswer) answer).getNumMap();
            setChanged();
            notifyObservers("Initialized Map");
        }

        if (answer instanceof MapAnswer) {
            map = ((MapAnswer) answer);
            setChanged();
            notifyObservers("Map");
        }

        /*if (answer instanceof PlayerBoardAnswer) {
            playerBoard = (PlayerBoardAnswer) answer;
            setChanged();
            notifyObservers("PlayerBoard");
        }*/
        if (answer instanceof SetSpawnAnswer) {
            setToSpawn(((SetSpawnAnswer) answer).getResult());
            setChanged();
            notifyObservers("Spawn");
        }

        if (answer instanceof PlayerHandAnswer) {
            playerHand = (PlayerHandAnswer) answer;
            setChanged();
            notifyObservers("PlayerHand");
        }

        if(answer instanceof  ChangeCurrentPlayerAnswer) {
            setChanged();
            notifyObservers("Change player");
        }

        if(answer instanceof ListOfWeaponsAnswer) {
            weaponList = (ListOfWeaponsAnswer) answer;
            setChanged();
            notifyObservers("Weapons list");
        }

        if(answer instanceof PlayerDiedAnswer) {
            setChanged();
            notifyObservers("Player died");
        }

        if(answer instanceof SpawnCommandAnswer) {
            setChanged();
            notifyObservers("Spawn phase");
        }
    }

    public boolean getToSpawn(){
        return this.toSpawn;
    }


    public GameMap getMap(){
        return gameBoard.getResult().getMap();
    }

    public PlayerBoardAnswer getPlayerBoard(int clientID) {
        return gameBoard.getPlayerBoard(clientID);
    }

    public PlayerHandAnswer getPlayerHand() {
        return playerHand;
    }

    public GameBoardAnswer getGameBoard() {
        return gameBoard;
    }

    public ListOfWeaponsAnswer getWeaponList() {return weaponList; }

    public PlayerAbstract getMyPlayer(){
        for(GameCharacter gameCharacter : gameBoard.getResult().getActiveCharacters()){
            if(gameCharacter.getConcretePlayer().getClientID() == clientID)
                return gameCharacter.getConcretePlayer();
        }
        return null;
    }

}
