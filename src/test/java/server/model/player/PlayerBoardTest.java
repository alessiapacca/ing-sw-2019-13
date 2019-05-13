package server.model.player;

import constants.Color;
import exceptions.InvalidMoveException;
import org.junit.jupiter.api.Test;
import server.model.cards.AmmoTile;
import server.model.cards.Bullet;
import server.model.gameboard.GameBoard;
import server.model.items.AmmoCube;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerBoardTest {

    @Test
    public void bulletTest(){
        GameBoard gbtest = new GameBoard(1,8);
        ConcretePlayer player = new ConcretePlayer("Pippo",gbtest,Figure.SPROG);
        PlayerBoard testPlayerBoard = player.getBoard();

        player.spawn(gbtest.getMap().getSpawnPoint(Color.BLUE));

        Bullet testBullet1 = new Bullet(0,0,3,3,'z');
        player.receiveBullet(testBullet1,Color.RED);

        assertEquals(3, testPlayerBoard.getDamageTaken());
        for(int i = 0; i < testPlayerBoard.getDamageTaken(); i++){
            assertEquals(Color.RED,testPlayerBoard.getTokenColor(i));
        }
        assertEquals(3, testPlayerBoard.getMarks().size());
        for(Color c : testPlayerBoard.getMarks()){
            assertEquals(Color.RED,c);
        }

        Bullet testBullet2 = new Bullet(0,0,1,1,'z');
        player.receiveBullet(testBullet2,Color.RED);

        assertEquals(7, testPlayerBoard.getDamageTaken());
        for(int i = 3; i < testPlayerBoard.getDamageTaken(); i++){
            assertEquals(Color.RED,testPlayerBoard.getTokenColor(i));
        }
        assertEquals(1, testPlayerBoard.getMarks().size());
        for(Color c : testPlayerBoard.getMarks()){
            assertEquals(Color.RED,c);
        }

        Bullet testBullet3 = new Bullet(0,0,1,3,'z');
        player.receiveBullet(testBullet3,Color.BLUE);

        assertEquals(8, testPlayerBoard.getDamageTaken());
        for(int i = 7; i < testPlayerBoard.getDamageTaken(); i++){
            assertEquals(Color.BLUE,testPlayerBoard.getTokenColor(i));
        }
        assertEquals(3, testPlayerBoard.getMarks().size());
        assertEquals(1,testPlayerBoard.getMarksOfAColor(Color.RED));
        assertEquals(2,testPlayerBoard.getMarksOfAColor(Color.BLUE));
    }

    @Test
    public void deathTest(){
        GameBoard gbtest = new GameBoard(1,8);
        ConcretePlayer player = new ConcretePlayer("Pippo",gbtest,Figure.SPROG);
        PlayerBoard testPlayerBoard = player.getBoard();

        player.spawn(gbtest.getMap().getSpawnPoint(Color.BLUE));

        //test that before death the player's value is 8 points
        assertEquals(8,testPlayerBoard.getPointValue());

        Bullet testBullet1 = new Bullet(0,0,12,2,'z');
        player.receiveBullet(testBullet1,Color.RED);

        assertEquals(1,testPlayerBoard.getNumberOfDeaths());
        assertEquals(2,testPlayerBoard.getMarks().size());
        assertEquals(0,testPlayerBoard.getDamageTaken());
        assertEquals(7,gbtest.getTrack().getRemainingSkulls());
        assertEquals(2,gbtest.getTrack().getMulticiplity(7));
        assertEquals(Color.RED,gbtest.getTrack().getColorAtIndex(7));
        //test that after the first death its value decreases
        assertEquals(6,testPlayerBoard.getPointValue());

    }


    @Test
    public void drawAmmoTileTest(){
        GameBoard testGameBoard = new GameBoard(4,8);
        ConcretePlayer testPlayer = new ConcretePlayer("Carlo", testGameBoard,Figure.BANSHEE);

        PlayerBoard testPlayerBoard = testPlayer.getBoard();

        List<AmmoCube> testList = new ArrayList<>();
        testList.add(new AmmoCube(Color.RED));
        testList.add(new AmmoCube(Color.RED));
        testList.add(new AmmoCube(Color.BLUE));
        testList.add(new AmmoCube(Color.RED));
        AmmoTile testAmmoTile = new AmmoTile(testList, false);


        testPlayerBoard.processAmmoTile(testAmmoTile);

        assertEquals(3,testPlayerBoard.getRedAmmo());
        assertEquals(2,testPlayerBoard.getBlueAmmo());
        assertEquals(1,testPlayerBoard.getYellowAmmo());

        testList.add(new AmmoCube(Color.YELLOW));


        try {
            testPlayerBoard.useAmmo(testList);
        } catch (InvalidMoveException e) {
            e.printStackTrace();
        }

        assertEquals(0,testPlayerBoard.getRedAmmo());
        assertEquals(1,testPlayerBoard.getBlueAmmo());
        assertEquals(0,testPlayerBoard.getYellowAmmo());

        assertThrows(InvalidMoveException.class, () ->
                testPlayerBoard.useAmmo(testList));
    }
}