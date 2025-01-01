/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package shared;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import core.Event;
import java.rmi.RemoteException;
import java.util.List;
import shared.User;

/**
 *
 * @author António
 */
public interface IremoteP2P {
    
    //:::: N E T WO R K  :::::::::::
    public String getAdress() throws RemoteException;

    public void addNode(IremoteP2P node) throws RemoteException;

    public List<IremoteP2P> getNetwork() throws RemoteException;

    //:::::::::::::: EVENTS  ::::::::::::::::::::
    public int getEventsSize() throws RemoteException;

    public void addEvent(String event, User userFrom, User userTo) throws RemoteException;

    public List<Event> getEvents() throws RemoteException;
    
    public void removeEvents(List<Event> events) throws RemoteException;

    public void synchronizeEvents(IremoteP2P node) throws RemoteException;

    //::::::::::::::::: M I N E R :::::::::::::::::::::::::::::::::::::::::::
    public void startMining(String msg, int zeros) throws RemoteException;

    public void stopMining(int nonce) throws RemoteException;

    public boolean isMining() throws RemoteException;

    public int mine(String msg, int zeros) throws RemoteException;

    //::::::::::::::::: B L O C K C H A I N :::::::::::::::::::::::::::::::::::::::::::
    public void addBlock(Block b) throws RemoteException;

    public int getBlockchainSize() throws RemoteException;

    public String getBlockchainLastHash() throws RemoteException;
    
    public BlockChain getBlockchain() throws RemoteException;

    public void synchnonizeBlockchain() throws RemoteException;
    
   // public List<String> getBlockchainTransactions()throws RemoteException;
    
}
