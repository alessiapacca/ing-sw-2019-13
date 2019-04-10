package Player;

import Constants.Constants;
import Exceptions.CharacterTakenException;
import GameBoard.GameBoard;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Array;
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
        ArrayList<Character> testList = new ArrayList<Character>();
        testList.add(testChar);
        assertEquals(Character.getValidFigures(),test2);
        assertEquals(Character.getTakenCharacters(),testList);

    }

    @Test
    public void testMove(){
       SquareAbstract square1 = new Square(3,3, 'w');
       SquareAbstract spawnPoint1 = new SpawnPoint(2,1,'R');
       Character character = new Character(Figure.BANSHEE);

       ArrayList<Character> tester = new ArrayList<Character>();
       tester.add(character);
       character.spawn(square1);
       assertEquals(character.getPosition(),square1);
       assertEquals(square1.getCharacters(), tester);
       assertEquals(spawnPoint1.getCharacters(), Collections.EMPTY_LIST);
       character.move(spawnPoint1);
       assertEquals(character.getPosition(),spawnPoint1);
       assertEquals(spawnPoint1.getCharacters(), tester);
       assertEquals(square1.getCharacters(), Collections.EMPTY_LIST);
    }

}