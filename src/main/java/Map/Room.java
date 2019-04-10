package Map;

import Constants.Color;
import Player.Character;

import java.util.*;

/**
 * 
 */
public class Room {

    private Color color;
    private List<Character> charactersList;
    private List<SquareAbstract> squareList;

    /**
     * Default constructor
     */
    public Room(Color color) {
        this.color = color;
        charactersList = new ArrayList<>();
        squareList = new ArrayList<>();
    }

    public Color getColor() {
        return color;
    }

    public List<Character> getCharacters(){
        ArrayList<Character> returnedList = (ArrayList<Character>) charactersList;
        return (ArrayList<Character>)  returnedList.clone();
    }

    public void addCharacter(Character character){
        if(!charactersList.contains((character)))
            charactersList.add(character);
    }

    public void removeCharacter(Character character){
        charactersList.remove(character);
    }

    public void addSquare(SquareAbstract square){
        squareList.add(square);
    }


    public List<SquareAbstract> getSquares() {
        ArrayList<SquareAbstract> returnedList = (ArrayList<SquareAbstract>) squareList;
        return (ArrayList<SquareAbstract>) returnedList.clone();
    }

}