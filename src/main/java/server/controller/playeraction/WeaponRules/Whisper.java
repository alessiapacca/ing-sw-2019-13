package server.controller.playeraction.WeaponRules;

import server.controller.playeraction.ShootInfo;

public class Whisper implements WeaponRulesInterface{

    public boolean validate(ShootInfo pack){
        return true;

    }

    public void actuate(ShootInfo pack){

    }
}
