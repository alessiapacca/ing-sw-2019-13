package client.cli;


import client.*;
import client.info.*;
import constants.Color;
import constants.Constants;
import server.controller.TurnPhase;
import server.model.cards.PowerUpCard;
import server.model.cards.TagbackGrenade;
import server.model.cards.TargetingScope;
import server.model.cards.WeaponCard;
import server.model.map.SpawnPoint;
import server.model.player.Figure;
import server.model.player.GameCharacter;
import server.model.player.PlayerAbstract;
import answers.PlayerBoardAnswer;
import answers.PlayerHandAnswer;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * This is the main class for client command line interface.
 * The user has to choose a name, the connection method (rmi or socket) and the first one to connect will choose the map of the match (1,2,3,4) and the number of skulls (5,6,7,8).
 * Then each user chooses a character.
 */
public class UpdaterCLI  implements Updater,Runnable{

    private Scanner scanner;
    /**
     * Used to create the actions of the client
     */
    private ActionParser actionParser;
    /**
     * The connection of the client (it can be rmi or socket)
     */
    private Connection connection;
    /**
     * The gamemodel of the client, it is used to read some values sent by the server
     */
    private GameModel gameModel;
    private boolean keepThreadAlive;

    public UpdaterCLI(){
        super();
        scanner = new Scanner(System.in);
        actionParser = new ActionParser(this);
        keepThreadAlive = true;
    }


    @Override
    public void update(Observable obs, Object object){
        if(object.equals("GameBoard")){
            System.out.println("New Update of the gameboard");
        }

        else if(object.equals("Change player")){
            System.out.println("Next player");
            if(connection.getClientID() == connection.getCurrentID()){
                System.out.println("It's your turn!");
            }else{
                System.out.println("It's " + connection.getCurrentCharacterName() + "'s turn!");
            }
        }

        else if(object.equals("Spawn")){
            System.out.println("New update of spawn");
        }

        else if(object.equals("Map initialized")){
            System.out.println("New Update of the map number");
        }

        else if(object.equals("Map")){
            System.out.println("New Update of the map");
        }

        else if(object.equals("PlayerHand")) {
            System.out.println("New Update of the playerhand");
        }

        else if(object.equals("Message")){
            System.out.println(Color.RED.getAnsi() + "New message from the server:\n" +
                gameModel.getMessage() + Constants.ANSI_RESET);

        }
        else if(object.equals("Server offline")){
            keepThreadAlive = false;
        }
    }

    /**
     * The method is used to ask the client the name, the connection method chosen, the character and, to the first that connects, it asks the map and the skulls
     * @throws RemoteException
     */
    @Override
    public void set(){
        //this method has to be run every time a new client starts. every cli needs to be an observer of the gameModel
        boolean hasChosen = false;
        String playerName;
        String methodChosen;
        boolean mapChosen = false;
        int initialSkulls = -1;
        boolean initialSkullsChosen = false;

        String characterName = "";


        do{
            System.out.println(">Insert your name:");
            playerName = scanner.nextLine();
        } while (playerName.equals(""));

        System.out.println("Name is: " +playerName);

        do {
            System.out.println(">Choose connection method:");
            System.out.println("(1) RMI");
            System.out.println("(2) Socket");
            methodChosen = scanner.nextLine();
            if (methodChosen.equals("1") || methodChosen.equals("2")) {
                hasChosen = true;
            }
        } while (!hasChosen);
        System.out.println("Method set: " +methodChosen);


        if (methodChosen.equals("1")) {
            try {
                connection = new ConnectionRMI();
            }catch(RemoteException e){
                keepThreadAlive = false;
                return;
            }
            System.out.println("RMI connection was set up");
        } else{
            connection = new ConnectionSocket();
            System.out.println("Socket connection was set up");
        }
        gameModel = connection.getGameModel();
        //because the gameModel is instantiated in the connection when it is started.
        // this way both socket and RMI can read it
        gameModel.addObserver(this);

        //this sets the client ID and add a ReceiverInterface as a client added in the server
        connection.configure(playerName);

        //waiting for setupAnswer to arrive
        while(gameModel.getSetupRequestAnswer() == null) {
            try {
                Thread.sleep(300);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        //the setup confirm answer can set serverOffline if the client is sending the setup after his original
        //  game has started
        if(gameModel.isServerOffline())
            return;

        //now we know what we have to ask to the user: it's all written into the setupAnswer

        int mapNumber = 0;
        if(gameModel.getSetupRequestAnswer().isFirstPlayer()) {//only if it is the first client!
            do{
                String stringChoice;
                int choice;
                System.out.println(">Choose the number of skulls (MIN: " +
                        Constants.MIN_SKULLS + ", MAX: " + Constants.MAX_SKULLS + "): ");
                try{
                    stringChoice = scanner.nextLine();
                    choice = Integer.parseInt(stringChoice);
                    if(choice < Constants.MIN_SKULLS || choice > Constants.MAX_SKULLS)
                        System.out.println("Please insert a valid number.");
                    else {
                        initialSkullsChosen = true;
                        initialSkulls = choice;
                    }
                }catch(NumberFormatException e){
                    System.out.println("Please insert a valid number.");
                }
            } while(!initialSkullsChosen);



            System.out.println(">You chose "+initialSkulls+" initial skulls!" );
            System.out.println(">Now choose the map you want to play with:" );
            String[] mapNames = {"Little", "Normal", "Big", "Huge"};
            List<String> mnList = Arrays.asList(mapNames);
            int choice;
            String stringChoice;
            do {
                for(int i = 0; i< mnList.size(); i++){
                    System.out.println(mnList.get(i) + " (" + (i+1) + ")");
                }
                try {
                    stringChoice =  scanner.nextLine();
                    choice = Integer.parseInt(stringChoice) - 1;
                    if(choice <mnList.size() && choice >= 0){
                        mapChosen = true;
                        mapNumber = choice + 1;     //maps start from 1
                    }
                    else{
                        System.out.println("Error: insert a valid number!");
                    }
                } catch(NumberFormatException e){
                    System.out.println("Error: insert a valid number");
                }

            } while (!mapChosen);
        }

        if(gameModel.getSetupRequestAnswer().isGameCharacter()){
            int choice;
            boolean set = false;
            String stringChoice;

            do{
                System.out.println(">Choose your fighter:");
                for(int i = 0; i< Figure.values().length; i++){
                    System.out.println(Figure.values()[i].name().toUpperCase() + " (" + (i+1) + ")");
                }
                try {
                    stringChoice =  scanner.nextLine();
                    choice = Integer.parseInt(stringChoice) - 1;
                    if(choice < Figure.values().length && choice >= 0){
                        set = true;
                        characterName = Figure.values()[choice].name();
                    }
                    else{
                        System.out.println("Error: insert a valid number!");
                    }
                } catch(NumberFormatException e){
                    System.out.println("Error: insert a valid number");
                }
            } while (!set);

            System.out.println("Name is: " +characterName.toUpperCase());
        }

        //prepares and sends setupInfo
        SetupInfo setupInfo = new SetupInfo();

        if(gameModel.getSetupRequestAnswer().isGameCharacter())
            setupInfo.setCharacterName(characterName);
        if(gameModel.getSetupRequestAnswer().isFirstPlayer()){
            setupInfo.setInitialSkulls(initialSkulls);
            setupInfo.setMapChoice(mapNumber);
        }

        connection.send(setupInfo);

        System.out.println("Your choices have been sent to the server");
    }

    @Override
    public void run(){

        set();

        while(keepThreadAlive){
            startLoop();
        }

        if(gameModel.isServerOffline())
            System.out.println("OOOOPS! Server is offline, we're sorry. Bye bye");
        else if(gameModel.isGameOver())
            System.out.println("See you!");

    }

    private void startLoop() {

        while (!gameModel.isDisconnected() && !gameModel.isServerOffline()) {

            //this is to avoid (printing wrong informations && blocking on input)
            waitAfterAction();

            if(gameModel.getGameBoard() == null){
                continue;
            }


            if (connection.getStartGame() == 1) {

                actionParser.addGameModel(gameModel);

                if(gameModel.isGameOver()){
                    printFinalScore();
                    keepThreadAlive = false;
                    return;
                }

                if (connection.getClientID() == connection.getCurrentID()) {

                    //Someone else is playing a tagback
                    if(gameModel.getCurrentTurnPhase() == TurnPhase.TAGBACK_PHASE && !gameModel.isPlayTagback()){
                            System.out.println("Someone is playing a tagback, please wait");
                        gameModel.setGamemodelNotUpdated(true);
                        continue;
                    }

                    //someone else is respawning
                    if(gameModel.getCurrentTurnPhase() == TurnPhase.RESPAWN_PHASE && !gameModel.isToRespawn()){
                        System.out.println("Someone is respawning, please wait");
                        gameModel.setGamemodelNotUpdated(true);
                        continue;
                    }

                    //you have to spawn at the start of the match
                    if(gameModel.isToSpawn()){
                        spawn();
                        //sending spawnInfo already sets gamemodelNotUpdated to true
                        continue;
                    }

                    //else: it's my turn
                    askInput();
                }
                else if(gameModel.isPlayTagback()){
                    playTagback();
                }
                else if(gameModel.isToRespawn()){
                    System.out.println("You died but don't worry, you can respawn right now");
                    spawn();
                    //sending spawn info already set gamemodelNotUpdated to true
                    //no need to continue beacuse it's an exclusive branch
                }
                else{
                    gameModel.setGamemodelNotUpdated(true);
                }
            }
            else{ //match is not started yet
                System.out.println("Match isn't started, please wait");
                gameModel.setGamemodelNotUpdated(true);

            }
        }

        if(gameModel.isServerOffline() || gameModel.isGameOver()){
            keepThreadAlive = false;
            return;
        }

        System.out.println("You have been disconnected! Press ENTER to reconnect");
        scanner.nextLine();
        connection.send(new ReconnectInfo(connection.getClientID()));
        System.out.println("Sent reconnect request");

        while(gameModel.isDisconnected()){
            try{
                TimeUnit.SECONDS.sleep(1);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * The method is used to print the final score on the console
     */
    private void printFinalScore() {
        Map<PlayerAbstract, Integer> map = gameModel.getGameOverAnswer().getPlayerToPoint();
        System.out.print("\nThe final score is:\n");
        for(PlayerAbstract playerAbstract : map.keySet()){
            System.out.println("\t" + playerAbstract.printOnCli() + " scored " + map.get(playerAbstract) + " points");
        }

        System.out.println("\nThe winner is... " + gameModel.getGameOverAnswer().getWinner().printOnCli());
    }

    /**
     * The method is called when the tagback can be used by a client out of his turn
     */
    private void playTagback() {
        int numberOfTagbacks = gameModel.getPlayerHand().getPlayerHand().getNumberOfTagbacks();
        if(numberOfTagbacks > 0){
            System.out.println("Do you wanna play a Tagback Grenade?");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                boolean ask = true;
                String strChoice = "";
                int choice = 0;
                while (ask) {
                    System.out.println(">Choose the card: ");
                    for (int i = 0; i < gameModel.getPlayerHand().getPowerupHand().size(); i++) {
                        System.out.println(gameModel.getPlayerHand().getPowerupHand().get(i).printOnCli() + " (" + (i + 1) + ")");
                    }
                    try {
                        strChoice = scanner.nextLine();
                        choice = Integer.parseInt(strChoice) - 1;
                        if (choice >= 0 && choice < gameModel.getPlayerHand().getPowerupHand().size() &&
                            gameModel.getPlayerHand().getPowerupHand().get(choice).getName().equalsIgnoreCase("Tagback Grenade")) {
                            ask = false;
                        }else if(choice >= 0 && choice < gameModel.getPlayerHand().getPowerupHand().size() &&
                                !gameModel.getPlayerHand().getPowerupHand().get(choice).getName().equalsIgnoreCase("Tagback Grenade")) {
                            System.out.println("You can only play a Tagback Grenade now");
                        }
                        else
                            System.out.println("Please insert a valid number.");
                    } catch (NumberFormatException e) {
                        System.out.println("Please insert a valid number.");
                    }
                }

                System.out.println("Sending tagback to your opponents...");

                connection.sendAsynchronous(
                        actionParser.createPowerUpEvent(gameModel.getPlayerHand().getPowerupHand().get(choice)));

                System.out.println("Tagback sent!");

                //there are no more tagbacks to play
                if(numberOfTagbacks == 1){
                    gameModel.setPlayTagback(false);
                    connection.sendAsynchronous(new TagbackStopInfo());
                    System.out.println("Sent tagback stop info");
                }

            }
            else{
                gameModel.setPlayTagback(false);
                connection.sendAsynchronous(new TagbackStopInfo());
                System.out.println("Sent tagback stop info");
            }
        }
    }

    private void waitAfterAction() {
        if(gameModel.isGamemodelNotUpdated() && !gameModel.isServerOffline())
            System.out.println("Wait please");

        while(gameModel.isGamemodelNotUpdated() && !gameModel.isServerOffline()){
            try {
                TimeUnit.SECONDS.sleep(1);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * The method is called to ask the client which powerup he wants to discard when he is spawning
     */
    public void spawn(){
        String read;

        List<PowerUpCard> powerups = gameModel.getPlayerHand().getPowerupHand();
        while ((powerups == null) || (powerups.isEmpty())) {
            System.out.println(">You have no powerups. There's a problem as you should have drawn them");
            powerups = gameModel.getPlayerHand().getPowerupHand();
            try{
                TimeUnit.SECONDS.sleep(5);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        boolean askSpawn = true;
        int spawnChoice = 0;
        while(askSpawn){
            System.out.println(">You have the following powerups: ");
            for (int i = 0; i < powerups.size(); i++) {
                System.out.println("> " + powerups.get(i).printOnCli() + " (" +(i+1) +")");
            }
            System.out.println(">Choose one of the powerups and discard it. You will be put in the spawn point of the color of that card : ");
            read = scanner.nextLine();
            try{
                spawnChoice = Integer.parseInt(read) - 1;
                if(spawnChoice>=0 && spawnChoice< powerups.size())
                    askSpawn = false;
                else
                    System.out.println("Please insert a valid number.");
            }catch(NumberFormatException e){
                System.out.println("Please insert a valid number.");
            }
        }

        Info action1 = actionParser.createSpawnEvent(powerups.get(spawnChoice));
        System.out.println(">Sending your choice to the server: ");
        if(gameModel.isToSpawn())
            connection.send(action1);
        else
            connection.sendAsynchronous(action1);

        //avoid asking again
        gameModel.setToSpawn(false);
        gameModel.setToRespawn(false);
    }

    /**
     * The method is used to ask the input of each action to the client
     */
    public void askInput(){
        String read;
        int row;
        int col;

        int collectDecision = 0;
        boolean collectChosen = false;

        printHand();

        gameModel.getMap().printOnCLI();

        System.out.println(">Write a command: \nM to Move\nC to Collect\nS to Shoot\nP to use a PowerUp\nR to Reload\\pass" +
                "\nMAP to show the map\nPL to show all the playerboards");
        read = scanner.nextLine();

        //this is made to avoid asking for an action if player is disconnected
        if(gameModel.isDisconnected())
            return;

        int result = -1;
        if (read.equalsIgnoreCase("M")) {       //move

            row = askCoordinate("row");
            col = askCoordinate("col");

            Info action = actionParser.createMoveEvent(row, col);
            connection.send(action);
        }
        else if (read.equalsIgnoreCase("S")) {        //shoot
            if(gameModel.getPlayerHand().getWeaponHand().isEmpty()){
                System.out.println("You have no weapon! Sorry.");
                return;
            }
            if(gameModel.getPlayerHand().getPlayerHand().areAllWeaponsUnloaded()){
                System.out.println("You have no loaded weapons! You can reload at the end of your turn");
                return;
            }
            boolean askShoot = false;
            String strChoice = "";
            int choice = 0;
            while (!askShoot) {
                System.out.println(">Choose the name of the weapon: ");
                for (int i = 0; i < gameModel.getPlayerHand().getWeaponHand().size(); i++) {
                    System.out.println(gameModel.getPlayerHand().getWeaponHand().get(i) + " (" + (i + 1) + ")");
                }
                try {
                    strChoice = scanner.nextLine();
                    choice = Integer.parseInt(strChoice) - 1;
                    if (choice >= 0 && choice < gameModel.getPlayerHand().getWeaponHand().size() &&
                            gameModel.getPlayerHand().getWeaponHand().get(choice).isReady()) {
                        askShoot = true;
                    }else if(choice >= 0 && choice < gameModel.getPlayerHand().getWeaponHand().size() &&
                            !gameModel.getPlayerHand().getWeaponHand().get(choice).isReady()) {
                        System.out.println("The selected weapon is unloaded. Reload it at the end of your turn.");
                    }
                    else
                        System.out.println("Please insert a valid number.");
                } catch (NumberFormatException e) {
                    System.out.println("Please insert a valid number.");
                }
            }
            Info action = actionParser.createShootEvent(gameModel.getPlayerHand().getWeaponHand().get(choice));
            connection.send(action);
        }
        else if (read.equalsIgnoreCase("C")) {      //collect
            do {
                System.out.println(">Choose what you want to collect: ");
                System.out.println("Weapon Card (1)"); //1 is to collect weapon
                System.out.println("Ammo (2)"); //2 is to collect ammo
                read = scanner.nextLine();
                if (read.equals("1")) {
                    collectDecision = 1;        //weapon
                    collectChosen = true;
                } else if (read.equals("2")) {
                collectDecision = 2;            //ammo
                    collectChosen = true;
                }
            } while (!collectChosen);

            //ask for move
            System.out.println(">Do you want to move before collecting? [y to move]");
            if(scanner.nextLine().equalsIgnoreCase("y")){
                row = askCoordinate("row");
                col = askCoordinate("col");
            }
            else{
                row = gameModel.getMyPlayer().getPosition().getRow();//gameModel.getPlayerBoard(connection.getClientID()).getRow();
                col = gameModel.getMyPlayer().getPosition().getCol();//gameModel.getPlayerBoard(connection.getClientID()).getCol();
            }

            if (collectDecision == 1) { //collect weapon
                if (gameModel.getMap().getSquare(row, col) instanceof SpawnPoint) {     //collect weapon in a spawnpoint
                    SpawnPoint spawnPoint = ((SpawnPoint) gameModel.getMap().getSquare(row, col));

                    //ask weapon
                    boolean askWeapon = true;
                    while(askWeapon) {
                        System.out.println(">Choose which weapon you want to collect ");
                        for (int i = 0; i < spawnPoint.getWeaponCards().size(); i++) {
                            System.out.println(spawnPoint.getWeaponCards().get(i).getName() + " (" + (i+1) + ")");
                        }
                        try {
                            result = Integer.parseInt(scanner.nextLine()) - 1;
                            if(result>= 0 && result <spawnPoint.getWeaponCards().size())
                                askWeapon = false;
                            else
                                System.out.println("Please insert a valid number.");
                        }catch(NumberFormatException e){
                            System.out.println("Please insert a valid number.");
                        }
                    }

                    //checks max number of weapons reached and choose what to discard
                    WeaponCard weaponToDiscard = null;
                    if(gameModel.getPlayerHand().getWeaponHand().size() == Constants.MAX_WEAPON_HAND) {
                        int weaponToDiscardIndex = 0;
                        askWeapon = true;
                        while (askWeapon) {
                            System.out.println(">Choose which weapon you want to discard ");
                            for (int i = 0; i < gameModel.getPlayerHand().getWeaponHand().size(); i++) {
                                System.out.println(gameModel.getPlayerHand().getWeaponHand().get(i).getName() +
                                        " (" + (i + 1) + ")");
                            }
                            try {
                                weaponToDiscardIndex = Integer.parseInt(scanner.nextLine()) - 1;
                                if (weaponToDiscardIndex >= 0 && weaponToDiscardIndex < gameModel.getPlayerHand().getWeaponHand().size())
                                    askWeapon = false;
                                else
                                    System.out.println("Please insert a valid number.");
                            } catch (NumberFormatException e) {
                                System.out.println("Please insert a valid number.");
                            }
                        }
                        weaponToDiscard = gameModel.getPlayerHand().getWeaponHand().get(weaponToDiscardIndex);
                    }

                    //asking for powerup cards to pay with
                    List<PowerUpCard> powerUpCards;
                    if(!gameModel.getPlayerHand().getPowerupHand().isEmpty())
                        powerUpCards = actionParser.getInput().askPowerUps();
                    else
                        powerUpCards = Collections.emptyList();


                    Info action = actionParser.createCollectEvent(row, col, result, weaponToDiscard, powerUpCards);
                    connection.send(action);
                } else {
                    System.out.println("This is not a spawn point, you can't collect weapons");
                    return;
                }
            } else  {       //collect ammo
                //if collectdecision == 2
                Info action = actionParser.createCollectEvent(row, col, Constants.NO_CHOICE, null, Collections.emptyList());
                connection.send(action);
            }
        }
        else if (read.equalsIgnoreCase("P")) {      //powerUp
            if(gameModel.getPlayerHand().getPowerupHand().isEmpty()){
                System.out.println("You have no powerUp! Sorry.");
                return;
            }

            String strChoice;
            int choice = 0;
            boolean askPowerUp = true;
            boolean playPowerUp = false;
            while (askPowerUp) {
                System.out.println(">Choose what powerup you want to use: ");
                for (int i = 0; i < gameModel.getPlayerHand().getPowerupHand().size(); i++) {
                    PowerUpCard powerUpCard = gameModel.getPlayerHand().getPowerupHand().get(i);
                    System.out.println(powerUpCard.printOnCli() + " (" + (i + 1) + ")");
                }
                strChoice = scanner.nextLine();
                try {
                    choice = Integer.parseInt(strChoice) - 1;
                    if (choice >= 0 && choice < gameModel.getPlayerHand().getPowerupHand().size() &&
                            !(gameModel.getPlayerHand().getPowerupHand().get(choice).getPowerUp() instanceof TagbackGrenade) &&
                            !(gameModel.getPlayerHand().getPowerupHand().get(choice).getPowerUp() instanceof TargetingScope)) {
                        askPowerUp = false;
                        playPowerUp = true;

                    } else if (choice >= 0 && choice < gameModel.getPlayerHand().getPowerupHand().size() && (
                            gameModel.getPlayerHand().getPowerupHand().get(choice).getPowerUp() instanceof TagbackGrenade ||
                            gameModel.getPlayerHand().getPowerupHand().get(choice).getPowerUp() instanceof TargetingScope)){
                        System.out.println("Oak's words echoed... There's a time and place for everything, but not now.");
                        askPowerUp = false;
                    }
                    else
                        System.out.println("Please insert a valid number.");
                } catch (NumberFormatException e) {
                    System.out.println("Please insert a valid number.");

                }
            }
            if(playPowerUp) {
                Info action = actionParser.createPowerUpEvent(gameModel.getPlayerHand().getPowerupHand().get(choice));
                connection.send(action);
            }
        }
        else if(read.equalsIgnoreCase("r")){        //reload
            List<WeaponCard> weaponCards = new ArrayList<>();
            List<PowerUpCard> powerUpCards = new ArrayList<>();
            if(!gameModel.getPlayerHand().getPlayerHand().areAllWeaponsLoaded()) {
                System.out.println("Do you want to reload any weapon? [y]");
                if (scanner.nextLine().equalsIgnoreCase("y")) {
                    weaponCards = askWeaponsToReload();
                    powerUpCards = actionParser.getInput().askPowerUps();
                }
            }
            System.out.println("Ok, your turn is over.");
            connection.send(actionParser.createReloadEvent(weaponCards, powerUpCards));
        }
        else if(read.equalsIgnoreCase("map")){
            gameModel.getMap().printOnCLI();
        }
        else if(read.equalsIgnoreCase("pl")){
            printPlayerBoards();
            System.out.println("Press enter to exit");
            scanner.nextLine();
        }
        else {
            System.out.println(">Please insert a valid action.");
        }
    }

    /**
     * The method is used to print on the console all the playerboards of the players
     */
    private void printPlayerBoards(){
        System.out.println("\n\nYou have " + gameModel.getPlayerHand().getPlayerHand().getPoints() + " points");
        for(GameCharacter gameCharacter : gameModel.getGameBoard().getResult().getActiveCharacters()){
            printPlayerBoard(gameCharacter.getConcretePlayer());
        }
    }

    /**
     * The method is used to print on the console the playerboard of the player
     */
    private void printPlayerBoard(PlayerAbstract playerAbstract){
        System.out.print("\n" + playerAbstract.printOnCli() + "'s playerboard:");
        //prints damage
        System.out.print("\nDAMAGE: ");
        int i = 0;
        for(Color color : playerAbstract.getPlayerBoard().getDamage()){
            if(color != null) {
                i++;
                System.out.print(color.getAnsi() + Constants.DAMAGE_TOKEN_CLI_ICON + Constants.ANSI_RESET);
            }
        }
        System.out.print(" (tot: " + i + ")");

        //prints marks
        System.out.print("\nMARKS: ");
        i=0;
        for(Color color : playerAbstract.getPlayerBoard().getMarks()){
            i++;
            System.out.print(color.getAnsi() + Constants.DAMAGE_TOKEN_CLI_ICON + Constants.ANSI_RESET);
        }
        System.out.print(" (tot: " + i + ")");

        //print cards
        System.out.print("\nUNLOADED WEAPONS: ");
        int j =0;
        for(WeaponCard weaponCard : playerAbstract.getPlayerBoard().getUnloadedWeapons()){
            System.out.print((j==0 ? "" : ", ") + weaponCard);
            j++;
        }
        System.out.print("\nNUMBER OF LOADED WEAPONS: " + (playerAbstract.getPlayerBoard().getWeaponHandSize() - j));
        System.out.print("\nNUMBER OF POWERUPS: " + playerAbstract.getPlayerBoard().getPowerUpHandSize() + "\n\n");
    }

    /**
     * The method is used to ask the client the input of the coordinates he wants to move to
     */
    public int askCoordinate(String coordinateType){
        boolean ask = true;
        int coordinate = 0;
        while(ask) {
            System.out.println(">Choose the " + coordinateType + " you want to move to: ");
            try {
                coordinate = Integer.parseInt(scanner.nextLine());
                ask = false;
            }catch(NumberFormatException e){
                //ask == true
            }
        }
        return coordinate;
    }

    /**
     * The method is used to ask the client the input of the reload action
     */
    public List<WeaponCard> askWeaponsToReload(){
        List<WeaponCard> returnedList = new ArrayList<>();
        List<WeaponCard> cardsToAsk = new ArrayList<>();

        //will ask only for unloaded weapons
        for(WeaponCard weaponCard : gameModel.getPlayerHand().getPlayerHand().getWeaponHand()){
            if(!weaponCard.isReady())
                cardsToAsk.add(weaponCard);
        }

        boolean ask = true;
        while(ask){
            System.out.println("Choose the card you want to reload, or say \"stop\":");
            for(int i = 0; i<cardsToAsk.size(); i++){

                    System.out.println( cardsToAsk.get(i) + " (" + (i+1) + ")");
            }
            try{
                String strChoice = scanner.nextLine();
                if(strChoice.equalsIgnoreCase("stop")){
                    break;
                }

                int choice = Integer.parseInt(strChoice) - 1;
                if(choice>=0 && choice < cardsToAsk.size()){
                    returnedList.add(cardsToAsk.get(choice));
                    cardsToAsk.remove(choice);
                    if(cardsToAsk.isEmpty())
                        ask = false;
                }
                else{
                    System.out.println("Please insert a valid number.");
                }
            }catch(NumberFormatException e){
                System.out.println("Please insert a valid number, or say \"stop\".");
            }
        }
        return returnedList;
    }

    /**
     * The method prints on the console the hand of the player
     */
    private void printHand(){
        PlayerHandAnswer playerHand = gameModel.getPlayerHand();
        PlayerBoardAnswer playerBoard = gameModel.getPlayerBoard(connection.getClientID());

        //print weapons
        List<WeaponCard> weapons = playerHand.getWeaponHand();
        if (weapons == null || weapons.isEmpty()) {
            System.out.print("\nYou have no weapons!");
        }
        else{
            System.out.print("\nYou have the following weapons:\n\t");
            int i =0;
            for (WeaponCard weaponCard : weapons) {
                System.out.print((i==0 ? "" : ", ") + weaponCard.printOnCli());
                i++;
            }
        }

        //print powerups
        List<PowerUpCard> powerups = playerHand.getPowerupHand();
        if ((powerups == null) || (powerups.isEmpty())) {
            System.out.print("\nYou have no powerups!");
        } else {
            System.out.print("\nYou have the following powerups:\n\t");
            int i = 0;
            for (PowerUpCard powerUpCard : powerups) {
                System.out.print((i==0 ? "" : ", ") + powerUpCard.printOnCli());
                i++;
            }
        }

        //print ammos
        if(playerBoard != null) { //these checks are used when the clients still do not have any update, at the start of the match
            System.out.println();
            for(Color color : Color.values()){
                if(color.isAmmoColor()){
                    System.out.print(color.getAnsi() + Constants.AMMOCUBE_CLI_ICON + " x" + playerBoard.getResult().getAmmo(color) +
                            Constants.ANSI_RESET + "\t");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        UpdaterCLI updaterCLI = new UpdaterCLI();
        updaterCLI.run();

        System.out.println("\n\nPress ENTER to close");
        updaterCLI.scanner.nextLine();

        System.exit(0);
    }
}
