package client;

import client.weapons.Weapon;
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


    public GameModel(){ //THERE IS A NEW gamemodel for every client!
    }

    public List<String> getPlayersNames(){
        List<String> listNames = gameBoard.getCharacterNames();
        return listNames;
    }

    public void setClientID(int clientID){
        this.clientID = clientID;
    }


    public static int getNextClientID(){
        return lastClientID;
    }


    public Integer getClientID(){
        return clientID;
    }

    public void saveAnswer(ServerAnswer answer) { //the GameModel will save the answer of the Server updating the model elements needed and notifying the observers
        if (answer instanceof GameBoardAnswer) {
            gameBoard = (GameBoardAnswer) answer;
            setChanged();
            notifyObservers("GameBoard");
        }

        if (answer instanceof InitialMapAnswer) {
            mapnum = ((InitialMapAnswer) answer).getNumMap();
            setChanged();
            notifyObservers("Map initialized");
        }

        if (answer instanceof MapAnswer) {
            map = (MapAnswer) answer;
            setChanged();
            notifyObservers("Map");
        }

        if (answer instanceof PlayerBoardAnswer) {
            playerBoard = (PlayerBoardAnswer) answer;
            setChanged();
            notifyObservers("PlayerBoard");
        }

        if (answer instanceof PlayerHandAnswer) {
            playerHand = (PlayerHandAnswer) answer;
            setChanged();
            notifyObservers("PlayerHand");
        }

        if(answer instanceof ListOfWeaponsAnswer) {
            weaponList = (ListOfWeaponsAnswer) answer;
            setChanged();
            notifyObservers("Weapons list");
        }
    }



    public MapAnswer getMap(){
        return this.map;
    }

    public PlayerBoardAnswer getPlayerBoard() {
        return playerBoard;
    }

    public PlayerHandAnswer getPlayerHand() {
        return playerHand;
    }

    public GameBoardAnswer getGameBoard() {
        return gameBoard;
    }

    public ListOfWeaponsAnswer getWeaponList() {return weaponList; }

}
