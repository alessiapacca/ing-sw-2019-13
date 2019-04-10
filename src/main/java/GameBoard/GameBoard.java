package GameBoard;

import Constants.Constants;
import Map.Map;

/**
 * 
 */
public class GameBoard {

    private Map gameMap;
    private KillshotTrack track;
    private WeaponDeck weaponDeck;
    private PowerupDeck powerupDeck;
    public static GameBoard istance;


    private GameBoard(int mapChoice, int skullChoice) {
        this.gameMap = new Map(mapChoice);
        /*try {
            this.gameMap = new Map(mapChoice);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        this.track = new KillshotTrack(skullChoice);
        this.weaponDeck = new WeaponDeck(Constants.NUMBER_OF_WEAPONS);
        this.powerupDeck = new PowerupDeck();
    }

    public static GameBoard istance(int mapChoice, int skullChoice){
        if(istance != null) istance = new GameBoard(mapChoice, skullChoice);
        return istance;
    }


    /**
     * @return
     */
    public void setup() {
        weaponDeck.shuffle();
        powerupDeck.shuffle();
    }

}