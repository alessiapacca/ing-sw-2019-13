package server.controller.playeraction;

import client.CollectInfo;
import constants.Constants;
import constants.Direction;
import server.controller.Controller;
import server.model.cards.CardInterface;
import server.model.cards.CollectableInterface;
import server.model.map.SpawnPoint;
import server.model.map.Square;
import server.model.map.SquareAbstract;
import server.model.player.PlayerAbstract;

import java.util.List;

public class CollectActuator {

    public CollectActuator(){}

    public void actuate(PlayerAbstract player, SquareAbstract square, int choice, Controller controller, CollectInfo collectInfo){
        player.getGameCharacter().move(square);
        CollectableInterface card;
        if(choice == Constants.NO_CHOICE){
            Square squarePlayer =  (Square)player.getPosition();
            player.collect(squarePlayer);
            card = ((Square) player.getPosition()).getAmmoTile();
            squarePlayer.removeItem(card, controller);
        }else {     //collecting a weapon
            SpawnPoint spawnPoint = (SpawnPoint) player.getPosition();

            if(player.getHand().getWeaponHand().size() == Constants.MAX_WEAPON_HAND) {
                //reloads weapon to discard
                player.getWeaponCard(collectInfo.getWeaponToDiscard().getWeapon()).setReady(true);

                //discard weapon from hand and from PLAYERBOARD
                player.getHand().removeWeaponCard(collectInfo.getWeaponToDiscard());
                player.getPlayerBoard().removeWeaponCard(collectInfo.getWeaponToDiscard());
            }


            player.collect(spawnPoint, choice);
            card = ((SpawnPoint)player.getPosition()).getWeaponCards().get(choice);
            spawnPoint.removeItem(card, controller);
        }
    }
}
