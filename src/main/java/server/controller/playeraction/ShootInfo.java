package server.controller.playeraction;

import exceptions.NoSuchEffectException;
import server.model.cards.MicroEffect;
import server.model.cards.Weapon;
import server.model.player.PlayerAbstract;

import java.util.List;

public class ShootInfo {

    private PlayerAbstract attacker;
    private Weapon weapon;
    private List<MacroInfo> activatedMacros;


    public ShootInfo(PlayerAbstract attacker, Weapon weapon, List<MacroInfo> activatedMacros){
        this.attacker = attacker;
        this.weapon = weapon;
        this.activatedMacros = activatedMacros;
    }


    public PlayerAbstract getAttacker() {
        return attacker;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public List<MacroInfo> getActivatedMacros() {
        return activatedMacros;
    }

    public MacroInfo getActivatedMacro(int macro) throws NoSuchEffectException{
        for(MacroInfo macroInfo : activatedMacros)
            if(macroInfo.getMacroNumber() == macro)
                return activatedMacros.get(macro);
        throw new NoSuchEffectException();
    }

    public MicroInfo getActivatedMicro(int macro, int micro) throws NoSuchEffectException{
        return getActivatedMacro(macro).getActivatedMicro(micro);
    }

    public boolean areDimensionsOk(MicroInfo microInfo, MicroEffect microEffect){
        //checks if playerList is ok
        if(microEffect.isGeneratePlayerFlag()) {
            if (!microInfo.getPlayersList().isEmpty())
                return false;
        }
        else{
            if(microEffect.getMaxTargetPlayerSize() == 0) {
                if (!microInfo.getPlayersList().isEmpty())
                    return false;
            }
            else{
                    if(microInfo.getPlayersList().isEmpty() ||
                            microInfo.getPlayersList().size() > microEffect.getMaxTargetPlayerSize())
                        return false;
                }

        }

        //checks if square is ok
        if(((!microEffect.isMoveFlag() || microEffect.isGenerateSquareFlag()) && microInfo.getSquare() != null) ||
                (microEffect.isMoveFlag() && !microEffect.isGenerateSquareFlag() && microInfo.getSquare() == null))
            return false;

        //check if noMoveSquares are ok
        if(microEffect.getMaxNmSquareSize() == 0) {
            if (!microInfo.getNoMoveSquaresList().isEmpty())
                return false;
        }
        else{
            //se bisognava inserire square
                //
            if(microInfo.getNoMoveSquaresList().isEmpty() ||
                    microInfo.getNoMoveSquaresList().size() > microEffect.getMaxNmSquareSize())
                return false;
        }

        //check if rooms are ok
        if(microEffect.getMaxTargetRoomSize() == 0) {
            if (!microInfo.getRoomsList().isEmpty())
                return false;
        }
        else{
            if(microInfo.getRoomsList().isEmpty() ||
                    microInfo.getRoomsList().size() > microEffect.getMaxTargetRoomSize())
                return false;
        }

        return true;
    }

    public void actuate(){
        //for every macro and micro ACTUATE (SUL MICRO)
    }
}
