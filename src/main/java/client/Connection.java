package client;

import view.ServerAnswer;

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public abstract class Connection implements Serializable {

    private GameModel gamemodel;

    public abstract void addPlayerCharacter(String name);

    public abstract void saveAnswer(ServerAnswer answer);

    public abstract boolean CharacterChoice(String name);

    public abstract int getInitialSkulls();

    public abstract int getClientID();

    public abstract void send(ActionInfo action);

    public abstract String getMap();

    public abstract void configure();

    public abstract GameModel getGameModel();

    public abstract void add(String playerName, int map, int initialSkulls) throws RemoteException;
}