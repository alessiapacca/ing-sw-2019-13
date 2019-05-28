package server.controller.turns;

import exceptions.WrongGameStateException;
import server.controller.playeraction.Action;
import server.model.player.ConcretePlayer;
import server.model.player.PlayerAbstract;

public class TurnHandler {

    private PlayerAbstract currentPlayer;

    private TurnPhase currentPhase;

    private Action action;

    public void setCurrentPlayer(PlayerAbstract currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public PlayerAbstract getCurrentPlayer() {
        return currentPlayer;
    }

    public void playPowerup(int index){
        currentPlayer.usePowerup(index);
    }

    /*
    public void reloadWeapon(List<Weapon> weaponsToReload) throws InvalidMoveException{
        if(this.currentPhase == TurnPhase.END_TURN) {
            for (Weapon w : weaponsToReload) {
                w.charge();
            }
        } throw new InvalidMoveException();
    }
    */

    public TurnHandler(){
        this.currentPhase = TurnPhase.FIRST_ACTION;
    }


    public TurnPhase getCurrentPhase(){
        return this.currentPhase;
    }

    public void setAndDoAction(Action action){
        if(currentPhase == TurnPhase.FIRST_ACTION
                || currentPhase == TurnPhase.SECOND_ACTION) {
            this.action = action;
            //if returns false then disconnects the player
            this.action.execute();
            nextPhase();
        }
    }

    /*public void startTurn(PlayerAbstract player){
        this.currentPlayer = player;
        this.currentPhase = TurnPhase.FIRST_ACTION;
    }*/

    public void pass(){
        if(currentPhase == TurnPhase.END_TURN){

        }
    }

    public void nextPhase(){
        switch (currentPhase){
            case FIRST_ACTION:
                this.currentPhase = TurnPhase.SECOND_ACTION;
                break;
            case SECOND_ACTION:
                this.currentPhase = TurnPhase.END_TURN;
                break;
            case END_TURN:
                try{
                    ((ConcretePlayer)currentPlayer).getCurrentGame().nextPlayer();
                }
                catch(WrongGameStateException e){
                    e.printStackTrace();
                }
                this.currentPhase = TurnPhase.FIRST_ACTION;
                break;
            default: break;
        }
    }



}
