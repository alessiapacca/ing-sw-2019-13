package Player;

import Constants.Constants;
import Exceptions.CharacterTakenException;
import GameBoard.GameBoard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import Map.*;
import Constants.*;

class CharacterTest {


    @Test
    public void testGetValidFigures(){
        ArrayList<Figure>  test1= new ArrayList<Figure>();
        test1.add(Figure.DESTRUCTOR);
        test1.add(Figure.BANSHEE);
        test1.add(Figure.DOZER);
        test1.add(Figure.VIOLET);
        test1.add(Figure.SPROG);
        assertEquals(Character.getValidFigures(),test1);

        ArrayList<Figure>  test2= new ArrayList<Figure>();
        test2.add(Figure.BANSHEE);
        test2.add(Figure.DOZER);
        test2.add(Figure.VIOLET);
        test2.add(Figure.SPROG);

        Character testChar = new Character(Figure.DESTRUCTOR);
        assertEquals(Character.getValidFigures(),test2);

    }

    @Test
    public void testMove(){
       SquareAbstract square1 = new Square(3,3, 'w');
       SquareAbstract spawnPoint1 = new SpawnPoint(2,1,'R');
       Character character = new Character(Figure.BANSHEE);

       character.spawn(square1);
       assertEquals(character.getPosition(),square1);
       assertEquals(square1.getCharacters(), Collections.singleton(character));
       assertEquals(spawnPoint1.getCharacters(), Collections.EMPTY_LIST);
       character.move(spawnPoint1);
       assertEquals(character.getPosition(),spawnPoint1);
       assertEquals(spawnPoint1.getCharacters(), Collections.singleton(character));
       assertEquals(square1.getCharacters(), Collections.EMPTY_LIST);
    }

}