package server.controller.playeraction;

import client.info.ReloadInfo;
import server.controller.Controller;
import server.model.cards.WeaponCard;
import server.model.player.PlayerAbstract;
import answers.MessageAnswer;

public class ReloadActuator {

    public void actuate(ReloadInfo reloadInfo, PlayerAbstract playerAbstract, Controller controller){
        String weapons = "";

        for(WeaponCard weaponCard : reloadInfo.getWeaponCards()){
            weapons = weapons + (weaponCard.getName() + ", ");
            for(WeaponCard cardInHand : playerAbstract.getHand().getWeaponHand()){
                if(cardInHand.getId() == weaponCard.getId()){
                    cardInHand.setReady(true);
                    playerAbstract.getPlayerBoard().removeUnloadedWeapon(cardInHand);
                    break;
                }
            }
        }

        playerAbstract.payWithPowerUps(reloadInfo.getCost(), reloadInfo.getPowerUpCards());

        if(controller != null) {
            //sending message
            String message = playerAbstract.getName() + (weapons.equalsIgnoreCase("") ? " completed his turn"
                    : " reloaded " + weapons);
            controller.getGameManager().sendEverybodyExcept(new MessageAnswer(message), playerAbstract.getClientID());
        }
    }
}
