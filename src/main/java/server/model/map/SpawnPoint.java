package server.model.map;

import constants.Color;
import server.controller.Controller;
import server.model.cards.CollectableInterface;
import server.model.cards.WeaponCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SpawnPoint extends SquareAbstract implements Serializable {

    private ArrayList<WeaponCard> weaponCards;

    protected SpawnPoint(int x, int y, Color color) {
        super(x,y,color);

        weaponCards = new ArrayList<>();
    }


    public List<WeaponCard> getWeaponCards() {
        return (ArrayList<WeaponCard>) weaponCards.clone();
    }

    public void addItem(CollectableInterface itemToAdd){
        weaponCards.add((WeaponCard)itemToAdd);
    }

    public void removeItem(CollectableInterface itemToRemove, Controller controller){
        WeaponCard temp = null;
        for(int i = 0; i < weaponCards.size(); i++){
            if(weaponCards.get(i).getName().equals(((WeaponCard)itemToRemove).getName())){
                temp = weaponCards.get(i);
            }
        }
        weaponCards.remove(temp);
        controller.addSquareToUpdate(this);
    }

    @Override
    public String toString() {
        String stringToReturn = "Spawnpoint, Room: " + getColor() + " Weapons: ";
        for(WeaponCard card : this.weaponCards){
            stringToReturn += card + " - ";
        }
        return stringToReturn;
    }

    @Override
    public boolean isEmpty() {
        return weaponCards.isEmpty();
    }
}