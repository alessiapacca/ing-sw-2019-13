package server.controller.playeraction;

import server.controller.Controller;
import server.model.game.Game;
import server.model.player.PlayerAbstract;
import server.model.player.PlayerState;

public class DrawAction implements Action {

    PlayerAbstract player;
    Game game;

    public DrawAction(PlayerAbstract player, Game game){
        this.player = player;
        this.game = game;
    }

    public boolean execute(Controller controller){
        (this.player).getHand().addCard(this.game.getPowerupDeck().draw());
        if(player.getPlayerState().equals(PlayerState.TOBESPAWNED)){
            player.setState(PlayerState.NORMAL);
            execute(controller);
        }
        return true;
    }
}
