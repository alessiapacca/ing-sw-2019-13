package server;

import client.ReceiverInterface;
import server.controller.Controller;
import server.model.player.PlayerAbstract;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;


public class ServerRMI implements Runnable {

    private Server server;
    private GameProxyInterface gameProxy = null;


    public Server getServer(){
        return this.server;
    }

    public ServerRMI(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            gameProxy = new GameProxy(this);
            server.getRegistry().bind(server.getRemoteObjectName(), gameProxy);
            System.out.println("RMI started!!!");
        }
        catch (RemoteException | AlreadyBoundException e) {
            System.out.println("Exception caught while binding...");
            e.printStackTrace();
        }
    }

    public List<ReceiverInterface> getClientsAdded() {
        try {
            return gameProxy.getClientsRMIadded();
        }
        catch(RemoteException e){
            return Collections.emptyList();
        }
    }

    public int addClient(){
        return this.server.addClient();
    }

    public void addMapClient(PlayerAbstract p) {
        System.out.println("Trying to add the client to the map ");
        server.getController().addClientInMap(p);
    }
}
