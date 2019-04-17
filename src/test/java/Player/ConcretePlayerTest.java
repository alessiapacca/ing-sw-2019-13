package Player;

import Constants.Color;
import Game.Game;
import GameBoard.GameBoard;
import Map.Map;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ConcretePlayerTest {
    //TODO when map and cards are somewhat done

    @Test
    public void testCreator(){
        GameBoard testGb = GameBoard.instance(1,8);
        PlayerAbstract player1 = new ConcretePlayer("Pippo", testGb, Figure.DESTRUCTOR);
        PlayerAbstract player2 = new ConcretePlayer("Pluto", testGb, Figure.BANSHEE);


        assertNotEquals(player1.getId(),player2.getId());

        List<Character> list1 = new ArrayList<Character>();
        list1.add(player1.getCharacter());
        List<Character> list2 = new ArrayList<Character>();
        list2.add(player2.getCharacter());

        player1.spawn(Map.getSpawnPoint(Color.BLUE));
        player2.spawn(Map.getSpawnPoint(Color.YELLOW));
        assertEquals(Map.getSpawnPoint(Color.BLUE).getCharacters(), list1);
        assertEquals(Map.getSpawnPoint(Color.YELLOW).getCharacters(), list2);
    }

}