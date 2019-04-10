package Player;

import Exceptions.CharacterTakenException;
import Map.*;
import Constants.*;
import java.util.*;
import java.lang.*;
/**
 * 
 */
public class Character {


    private Figure figure;
    private static boolean[] figureChosen = new boolean[Constants.NUM_FIGURES];


    private SquareAbstract position;
    private ConcretePlayer concretePlayer;


    Character(Figure f){
        this.figure = f;
        f.setOwner(this);
        setTaken(f);
    }

    public static void setTaken(Figure f){
        figureChosen[f.getId()] = true;
    }

    public static boolean isTaken(int id){
        return figureChosen[id];
    }

    public Figure getFigure(){ return this.figure;}

    public ConcretePlayer getConcretePlayer(){
        //TODO implement with clone
        return concretePlayer;
    }

    public static List<Figure> getValidFigures(){
        //Maybe implement in java functional
        List<Figure> res = new ArrayList<Figure>();
        for(Figure f : Figure.values()){
            if(!isTaken(f.getId())){
                res.add(f);
            }
        }
        return res;
    }

    public static List<Character> getTakenCharacters(){
        List<Character> res = new ArrayList<Character>();
        for(Figure f : Figure.values()){
            if(isTaken(f.getId())){
                res.add(f.getOwner());
            }
        }
        return res;
    }


    private void setPosition(SquareAbstract position){
        this.position = position;
    }
    /**
     *
     * @param sq
     */
    public void move(SquareAbstract sq) {
        getPosition().removeCharacter(this);
        setPosition(sq);
        sq.addCharacter(this);
    }

    /**
     * @return
     */
    public SquareAbstract getPosition() {
        return position;
    }

    public void spawn(SquareAbstract sp) {
        if(position == null){
            sp.addCharacter(this);
            setPosition(sp);
        } else{
            System.out.println("Invalid move");
        }
    }
}