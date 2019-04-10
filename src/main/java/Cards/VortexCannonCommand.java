package Cards;
import java.lang.reflect.Array;
import java.util.*;
import Map.*;
import Player.Character;


public class VortexCannonCommand implements Command {


    public VortexCannonCommand() {
    }

    public ArrayList<ArrayList<Character>> execute(Square square) { //in this case the square is NOT THE SQUARE OF THE PERSON SHOOTING, BUT THE SQUARE OF THE FIRST PERSON CHOSEN. SEE THE RULES OF THE WEAPONS PLEASE
        ArrayList<Character> e = (ArrayList<Character>)square.getCharacters(); //the characters in the same square
        //union between the same square and the ones distant 1 movement

        ArrayList<Character> e1 = (ArrayList<Character>)square.getExactlyOneMovementCharacters();

        e.addAll(e1);

        ArrayList<ArrayList<Character>> result = new ArrayList<ArrayList<Character>>();
        result.add(e);

        return result;
    }
}
