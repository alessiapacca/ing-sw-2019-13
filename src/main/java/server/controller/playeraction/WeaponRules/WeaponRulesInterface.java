package server.controller.playeraction.WeaponRules;

import server.controller.playeraction.ShootInfo;

public interface WeaponRulesInterface {

    public boolean validate(ShootInfo pack);

    public void actuate(ShootInfo pack);
}
