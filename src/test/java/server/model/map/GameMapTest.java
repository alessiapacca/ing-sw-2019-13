package server.model.map;

import constants.Color;
import exceptions.NoSuchSquareException;
import org.junit.jupiter.api.Test;
import server.model.cards.AmmoTile;
import server.model.player.ConcretePlayer;
import server.model.player.Figure;
import server.model.player.GameCharacter;
import server.model.player.PlayerAbstract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    @Test
    public void isMapConstructorOk(){
        GameMap gameMap = new GameMap(1);
        List<SpawnPoint> spawnPoints = new ArrayList<>();
        spawnPoints.add(new SpawnPoint(0,3, Color.BLUE));
        spawnPoints.add(new SpawnPoint(1,0, Color.RED));
        spawnPoints.add(new SpawnPoint(3,4, Color.YELLOW));
        assertEquals(spawnPoints.get(0).getColor(), gameMap.getSpawnPoints().get(0).getColor());
        assertTrue(gameMap.getSquare(2,3) instanceof SpawnPoint);
        assertEquals(gameMap.getSquare(1,0).getColor(), Color.RED);
        assertEquals(gameMap.getSquare(2,1).getColor(), Color.WHITE);
        assertTrue(gameMap.getSquare(2,1) instanceof Square);

    }



    @Test
    void getRooms() {
        GameMap gameMap = new GameMap(1);
        List<Room> roomList = gameMap.getRooms();
        assertTrue(roomList.get(0).getColor() == Color.BLUE);
        assertTrue(roomList.get(1).getColor() == Color.RED);
        assertTrue(roomList.get(2).getColor() == Color.YELLOW);
        assertTrue(roomList.get(3).getColor() == Color.WHITE);

    }

    @Test
    void getSpawnPoints() {
        GameMap gameMap = new GameMap(1);
        ArrayList<SpawnPoint> sp = (ArrayList<SpawnPoint>) gameMap.getSpawnPoints();
        assertTrue(gameMap.getSpawnPoint(Color.RED).getColor() == sp.get(1).getColor());
    }

    @Test
    void getSquareFromXY(){
        GameMap gameMap = new GameMap(1);
        assertTrue(gameMap.getSquare(1,0).getColor() == Color.RED);
    }

    @Test
    void getSquaresWithSameX(){
        GameMap gameMap = new GameMap(1);
        assertTrue(gameMap.getSquaresWithSameRow(gameMap.getSquare(1,2)).contains(gameMap.getSquare(1,0)));
        assertTrue(gameMap.getSquaresWithSameRow(gameMap.getSquare(1,2)).contains(gameMap.getSquare(1,1)));
        assertTrue(gameMap.getSquaresWithSameRow(gameMap.getSquare(1,2)).contains(gameMap.getSquare(1,3)));
        assertFalse(gameMap.getSquaresWithSameRow(gameMap.getSquare(1,2)).contains(gameMap.getSquare(2,3)));
    }

    @Test
    void getSquaresWithSameY(){
        GameMap gameMap = new GameMap(1);
        assertTrue(gameMap.getSquaresWithSameCol(gameMap.getSquare(1,1)).contains(gameMap.getSquare(0,1)));
        assertTrue(gameMap.getSquaresWithSameCol(gameMap.getSquare(1,1)).contains(gameMap.getSquare(2,1)));
        assertFalse(gameMap.getSquaresWithSameRow(gameMap.getSquare(1,1)).contains(gameMap.getSquare(0,0)));
    }

    @Test
    void testMapIterator(){
        GameMap map = new GameMap(1);
        Iterator<SquareAbstract> iterator = map.iterator();
        SquareAbstract current = null;
        for(int i = 1; i<=9; i++){
            current = iterator.next();
        }
        assertTrue(current == map.getSquare(2,2));
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    void printOnCli(){
        GameMap gameMap = new GameMap(1);
        gameMap.getSquare(1,1).addItem(new AmmoTile(null, false));
        PlayerAbstract playerAbstract = new ConcretePlayer("asd");
        playerAbstract.setPlayerCharacter(Figure.DESTRUCTOR);
        playerAbstract.spawn(gameMap.getSquare(0,0));
        gameMap.printOnCLI();
    }

    /*@Test
    void testSymbol(){
        *//*String hex = "D83D";
        String hex2 = "DCA7";
        int intValue = Integer.parseInt(hex, 16);
        int intValue2 = Integer.parseInt(hex, 16);
        System.out.println((char)(intValue+intValue2));*//*

        String PLAYER_CLI_ICON2 = "▲";

        System.out.println(PLAYER_CLI_ICON2);
    }*/
}