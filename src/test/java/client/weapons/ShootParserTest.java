package client.weapons;

import org.junit.jupiter.api.Test;

import java.io.*;

class ShootParserTest {

    @Test
    void getWeaponInput() throws FileNotFoundException {

        /*File file = new File("./src/test/resources/weaponTest.txt");
        InputStream inputStream = new FileInputStream(file);
        System.setIn(inputStream);

        Game game = new Game(1,5, null);
        GameBoard gameBoard = game.getCurrentGameBoard();
        InputAbstract inputAbstract = new CliInput(null);
        List<String> playersNamesList = new ArrayList<>();

        List<String> roomNameList = new ArrayList<>();
        for(Room room : gameBoard.getMap().getRooms()){
            roomNameList.add(room.getColor().toString());
        }


        PlayerAbstract player1 = new ConcretePlayer("player1");
        PlayerAbstract player2 = new ConcretePlayer("player2");
        PlayerAbstract player3 = new ConcretePlayer("player3");
        PlayerAbstract player4 = new ConcretePlayer("player4");
        PlayerAbstract player5 = new ConcretePlayer("player5");
        player1.setPlayerCharacter(Figure.DOZER);
        player2.setPlayerCharacter(Figure.BANSHEE);
        player3.setPlayerCharacter(Figure.DESTRUCTOR);
        player4.setPlayerCharacter(Figure.SPROG);
        player5.setPlayerCharacter(Figure.VIOLET);

        playersNamesList.add(player2.getName());
        playersNamesList.add(player3.getName());
        playersNamesList.add(player4.getName());
        playersNamesList.add(player5.getName());

        for(PlayerAbstract playerAbstract : game.getPlayers())
            System.out.println(playerAbstract.getName());

        *//*gameBoard.addGameCharacter(player1.getGameCharacter());
        gameBoard.addGameCharacter(player2.getGameCharacter());
        gameBoard.addGameCharacter(player3.getGameCharacter());
        gameBoard.addGameCharacter(player4.getGameCharacter());
        gameBoard.addGameCharacter(player5.getGameCharacter());*//*

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.addPlayer(player3);
        game.addPlayer(player4);
        game.addPlayer(player5);

        player1.spawn(gameBoard.getMap().getSquare(0,0));
        player2.spawn(gameBoard.getMap().getSquare(1,1));
        player3.spawn(gameBoard.getMap().getSquare(1,1));
        player4.spawn(gameBoard.getMap().getSquare(1,1));
        player5.spawn(gameBoard.getMap().getSquare(2,1));

        gameBoard.getMap().printOnCLI();


        ShootParser shootParser = new ShootParser(new GameModel(), inputAbstract);
        ShootPack shootPack = shootParser.getWeaponInput(gameBoard.getWeaponCard("electroscythe"));

        ShootValidator shootValidator = new ShootValidator();
        ShootInfo shootInfo = shootValidator.validate(shootPack, game, player1);

        //assertNotNull(shootInfo);

        System.setIn(System.in);*/
    }


}
