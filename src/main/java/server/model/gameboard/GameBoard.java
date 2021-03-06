package server.model.gameboard;

import client.weapons.Weapon;
import constants.Constants;
import server.controller.TurnPhase;
import server.model.cards.AmmoTile;
import server.model.cards.PowerUpCard;
import server.model.cards.WeaponCard;
import server.model.game.GameState;
import server.model.map.GameMap;
import server.model.map.Room;
import server.model.map.SpawnPoint;
import server.model.map.SquareAbstract;
import server.model.player.ConcretePlayer;
import server.model.player.GameCharacter;
import server.model.player.PlayerBoard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class containing all the different physical elements of a certain game.
 */
public class GameBoard implements Serializable {

    /**
     * reference to the game map
     */
    private GameMap gameMap;

    /**
     * reference to the killshot track
     */
    private KillshotTrack track;

    /**
     * reference to the weapon deck
     */
    private transient WeaponDeck weaponDeck;

    /**
     * reference to the powerups deck
     */
    private transient PowerupDeck powerupDeck;

    /**
     * reference to the AmmoTile deck
     */
    private transient AmmoTileDeck ammoTileDeck;

    private Map<Integer,PlayerBoard> mapPlayerBoard;

    private List<GameCharacter> activeCharacters;

    private TurnPhase currentTurnPhase;

    private GameState gameState;

    public GameBoard(){

    }

    /**
     * Constructor called by the instance method
     * @param mapChoice index of the map chosen by the players in the setup phase
     * @param skullChoice number of skulls
     */
    public GameBoard(int mapChoice, int skullChoice) {
        this.gameMap = new GameMap(mapChoice);
        this.track = new KillshotTrack(skullChoice);
        this.weaponDeck = new WeaponDeck();
        this.powerupDeck = new PowerupDeck();
        this.ammoTileDeck = new AmmoTileDeck();
        this.mapPlayerBoard = new HashMap<>();
        activeCharacters = new ArrayList<>();
        currentTurnPhase = TurnPhase.FIRST_ACTION;
        setupGameBoard();
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public void setCurrentTurnPhase(TurnPhase currentTurnPhase) {
        this.currentTurnPhase = currentTurnPhase;
    }

    public TurnPhase getCurrentTurnPhase() {
        return currentTurnPhase;
    }

    public List<GameCharacter> getActiveCharacters() {
        return activeCharacters;
    }

    public AmmoTile drawAmmo(){
        return this.ammoTileDeck.draw();
    }

    public WeaponCard drawWeapon(){
        return this.weaponDeck.draw();
    }

    public Map getHashMap(){
        return this.mapPlayerBoard;
    }

    /**
     * Method that puts an AmmoTile on every square and three weapons on
     * every spawnpoint
     */
    public void setupGameBoard(){
        List<SquareAbstract> listOfSquares = new ArrayList<>();
        List<SpawnPoint> listOfSpawnPoints = gameMap.getSpawnPoints();

        for(Room room : gameMap.getRooms()){
            listOfSquares.addAll(room.getSquares());
        }

        listOfSquares.removeAll(listOfSpawnPoints);

        for(SquareAbstract square : listOfSquares){
            square.addItem(this.ammoTileDeck.draw());
        }

        for(SpawnPoint sp : listOfSpawnPoints){
            for(int i = 0; i < Constants.NUMBER_OF_WEAPON_PER_SPAWN_POINT; i++){
                if(weaponDeck.getSize() != 0)
                    sp.addItem(this.weaponDeck.draw());
            }
        }
    }

    public void addPlayerBoard(ConcretePlayer p){
        this.mapPlayerBoard.put(p.getClientID(), p.getPlayerBoard());
    }

    public KillshotTrack getTrack() {
        return this.track;
    }

    public GameMap getMap(){
        return this.gameMap;
    }

    public WeaponDeck getWeaponDeck() {
        return weaponDeck;
    }

    public PowerupDeck getPowerupDeck() {
        return powerupDeck;
    }

    public AmmoTileDeck getAmmoTileDeck() {
        return ammoTileDeck;
    }

    public Weapon getWeapon(String name){
        return getWeaponCard(name).getWeapon();
    }

    public WeaponCard getWeaponCard(String name){
        return weaponDeck.getWeaponCard(name);
    }

    public PowerUpCard getPowerUpCard(int id){
        return powerupDeck.getPowerUpCard(id);
    }
}