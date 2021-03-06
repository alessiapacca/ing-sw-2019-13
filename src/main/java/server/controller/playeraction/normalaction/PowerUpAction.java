package server.controller.playeraction.normalaction;

import client.powerups.PowerUpPack;
import server.controller.Controller;
import server.controller.playeraction.PowerUpActuator;
import server.controller.playeraction.serverinfo.PowerUpInfo;
import server.controller.playeraction.PowerUpValidator;
import server.model.game.Game;
import server.model.player.PlayerAbstract;

public class PowerUpAction implements Action {


    private PowerUpPack powerUpPack;
    private PowerUpValidator validator;
    private PowerUpActuator actuator;
    private PlayerAbstract player;

    public PowerUpAction(PowerUpPack powerUpPack, Game game, PlayerAbstract player){
        this.powerUpPack = powerUpPack;
        this.validator = new PowerUpValidator();
        this.actuator = new PowerUpActuator();
        this.player = player;
    }

    @Override
    public boolean execute(Controller controller) {
        return this.usePowerUp(controller);
    }

    public boolean usePowerUp(Controller controller){
        PowerUpInfo powerUpInfo = validator.validate(powerUpPack, controller.getCurrentGame(), player);
        if(powerUpInfo != null) {
            actuator.actuate(powerUpInfo, controller);
            return true;
        }else {
            //System.out.println("You can't collect");
            return false;
        }
    }
}
