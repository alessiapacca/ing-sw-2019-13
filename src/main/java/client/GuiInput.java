package client;

import client.gui.MainGuiController;
import client.gui.UpdaterGui;
import client.info.ReloadInfo;
import client.info.SquareInfo;
import client.weapons.MacroEffect;
import client.weapons.MicroEffect;
import client.weapons.ScopePack;
import client.weapons.Weapon;
import constants.Color;
import server.model.cards.PowerUpCard;
import server.model.cards.WeaponCard;

import java.util.List;

public class GuiInput extends InputAbstract {

    //mettere un riferimento al maingui controller
    private MainGuiController guiController;

    //quando premo sulla carta devo passare la wapon a al shootparser e chiamare geat weapon input,
    //il get weapon input in base all'arma chiama metodi di input abstract. devo organizzare sta classe
    // per far uscire i prompt giusti.
    //dentro inputabstract ci sono delle liste di giocatori che sono quelli che devo far scegliere al giocatore.
    //si potrebbero mettere i nomi dentro a dei bottoni.

    public GuiInput(Updater updater){
        UpdaterGui updaterGui = (UpdaterGui) updater;
        this.guiController = (MainGuiController)((UpdaterGui) updater).getControllerFromString("gui.fxml");
        this.guiController.setInput(this);
    }

    @Override
    public ReloadInfo askReload(WeaponCard weaponCard) {
        return (ReloadInfo) guiController.askReload();
    }

    @Override
    public boolean getChoice(MacroEffect macroEffect) {
        return this.guiController.askMacro(macroEffect);
    }

    @Override
    public MacroEffect chooseOneMacro(Weapon weapon) {
        return this.guiController.chooseOneMacro(weapon);
    }

    @Override
    public boolean getChoice(MicroEffect microEffect) {
        return this.guiController.askMicro(microEffect);
    }

    @Override
    public List<SquareInfo> askSquares(int maxSquares) {
        return this.guiController.askSquares(maxSquares);
    }

    @Override
    public List<String> askPlayers(int maxTargetPlayerSize) {
        return this.guiController.askPlayersOrRooms(maxTargetPlayerSize,true);
    }

    @Override
    public List<String> askRooms(int maxTargetRoomSize) {
        return this.guiController.askPlayersOrRooms(maxTargetRoomSize,false);
    }

    @Override
    public boolean getMoveChoice() {
        //chiede al player se vuole muoversi prima di sparare, viene chiamato solo se lo stato è ADRENALINIC_SHOOT
        return this.guiController.getMoveChoice();
    }

    @Override
    public List<PowerUpCard> askPowerUps() {
        return this.guiController.askPowerups();
    }

    @Override
    public List<ScopePack> askTargetingScopes() {
        return this.guiController.askTargetingScopes();
    }

    public ScopePack manageScope(PowerUpCard powerUpCard){
        String target = askPlayers(1).get(0);
        Color color = this.guiController.chooseAmmoColor();
        return new ScopePack(powerUpCard, target, color);
    }

}
