package server.model.gameboard;

import server.model.cards.AmmoTile;

import constants.*;
import exceptions.ReadJsonErrorException;
import server.model.items.AmmoCube;
import server.model.items.AmmoTileType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

/**
 * Class used to manage ammotiles
 */
public class AmmoTileDeck implements Serializable {


    private LinkedList<AmmoTile> deck = new LinkedList<>();

    /**
     * Default constructor using initializeDeck to create the list of tiles
     */
    AmmoTileDeck(){
        try {
            initializeDeck();
        }catch (ReadJsonErrorException e){
            e.printStackTrace();
        }
    }

    /**
     * Function that reads a JSON file containing all the data
     * about ammotiles and creates all the objects required
     * @throws ReadJsonErrorException if ther is a problem in the reading phase
     */
    public void initializeDeck() throws ReadJsonErrorException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream(Constants.PATH_TO_AMMOTILE_JSON);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        AmmoTileType[] arrayOfTiles = new AmmoTileType[0];

        try {
            arrayOfTiles = mapper.readValue(reader, AmmoTileType[].class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(arrayOfTiles.length == 0) throw new ReadJsonErrorException();

        for(int i = 0; i < arrayOfTiles.length ; i++){
            for(int j = 0; j < arrayOfTiles[i].getNumberOfCards(); j++){
                this.deck.push(new AmmoTile(createListFromType(arrayOfTiles[i]),
                        arrayOfTiles[i].hasPowerup(),arrayOfTiles[i].getPath()));
            }
        }
        shuffle();
    }

    /**
     * Pops the first element of the queue containg the ammotiles
     * @return Returns the AmmoTile popped.
     */
    public AmmoTile draw(){
        AmmoTile ammoTile = this.deck.pop();
        if(this.deck.isEmpty()){
            try {
                initializeDeck();
            } catch (ReadJsonErrorException e) {
                e.printStackTrace();
            }
        }
        return ammoTile;
    }

    /**
     * Auxiliary function called in the initialization phase that creates a
     * list containg all the cubes in a AmmoTile
     * @param type AmmoTileType read from the JSON file
     * @return The list created
     */
    public List<AmmoCube> createListFromType(AmmoTileType type){
        List<AmmoCube> listToReturn = new ArrayList<>();

        for(int i = 0; i < type.getRedCubes(); i++) listToReturn.add(new AmmoCube(Color.RED));
        for(int i = 0; i < type.getBlueCubes(); i++) listToReturn.add(new AmmoCube(Color.BLUE));
        for(int i = 0; i < type.getYellowCubes(); i++) listToReturn.add(new AmmoCube(Color.YELLOW));
        return listToReturn;
    }

    /**
     *
     * @return the reference to the deck.
     */
    public LinkedList<AmmoTile> getDeck() {
        return this.deck;
    }

    /**
     * Shuffles the deck
     */
    public void shuffle(){
        Collections.shuffle(this.deck);
    }

    @Override
    public String toString() {
        int sizeOfDeck = this.deck.size();
        String stringToReturn = "";
        for(int i = 0; i < sizeOfDeck; i++){
            stringToReturn += this.deck.get(i).toString() + '\n';
        }
        return stringToReturn;
    }
}
