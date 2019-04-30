package server.model.cards;

import server.model.map.SquareAbstract;
import server.model.player.Character;

import java.util.ArrayList;

/**
 * 
 */
public class AimThroughWallsCommand implements Command {

    public AimThroughWallsCommand() {
    }

    /**
     * Returns the characters that the player shooting can see through walls depending on the direction
     * @param square where the player is
     * @return an arraylist of arralist of the characters that the player shooting can see through walls depending on the direction
     */
    public ArrayList<ArrayList<Character>> execute(SquareAbstract square){
        ArrayList<Character> e = (ArrayList)square.getCharactersThroughWalls();

        ArrayList<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>(); //this will be the return for my player

        ArrayList<Character> eN = new ArrayList<Character>(); //NORTH
        ArrayList<Character> eS = new ArrayList<Character>(); //SOUTH
        ArrayList<Character> eW = new ArrayList<Character>(); //WEST
        ArrayList<Character> eE = new ArrayList<Character>(); //EAST

        //now I have to create an array of array with 4 different lists.
        for (Character t : e) {
            if (square.getRow() == t.getPosition().getRow()) {
                if (square.getCol() > t.getPosition().getCol()) { //if this is true then we are considering the SOUTH LIST
                    eS.add(t);
                } else if (square.getCol() < t.getPosition().getCol()) {//if this is true then we are considering the NORTH LIST
                    eN.add(t);
                } else if(square.getCol() == t.getPosition().getCol()){
                    eN.add(t);
                    eS.add(t);
                    eW.add(t);
                    eE.add(t);
                }

            }
            else if(square.getCol() == t.getPosition().getCol()){
                 if(square.getRow() < t.getPosition().getRow()) { //if this is true then we are considering the EAST LIST
                     eE.add(t);
                 }
                 else if(square.getRow() > t.getPosition().getRow()){ //if this is true then we are considering the WEST LIST
                     eW.add(t);
                  }
            }
        }

        result.add(eN);
        result.add(eS);
        result.add(eW);
        result.add(eE);

        return result;
    }
}