package server.controller.playeraction;

import client.ReloadInfo;
import server.controller.Controller;
import server.model.player.PlayerAbstract;

public class ReloadAction implements Action {

    private ReloadInfo reloadInfo;
    private PlayerAbstract playerAbstract;
    private ReloadValidator reloadValidator;
    private ReloadActuator reloadActuator;

    public ReloadAction(ReloadInfo reloadInfo, PlayerAbstract playerAbstract){
        this.reloadInfo = reloadInfo;
        this.playerAbstract = playerAbstract;
        this.reloadValidator = new ReloadValidator();
        this.reloadActuator = new ReloadActuator();
    }

    public boolean execute(Controller controller){
        ReloadInfo reloadInfoValidated = reloadValidator.validate(this.reloadInfo, playerAbstract);
        if(reloadInfo != null) {
            reloadActuator.actuate(reloadInfoValidated, playerAbstract);
            return true;
        }
        else
            return false;
    }

}