//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2024   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package p2p;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import core.Event;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Created on 27/11/2024, 17:42:15
 *
 * @author manso - computer
 */
public interface IremoteP2P extends Remote {

    //:::: N E T WO R K  :::::::::::
    public String getAdress() throws RemoteException;

    public void addNode(IremoteP2P node) throws RemoteException;

    public List<IremoteP2P> getNetwork() throws RemoteException;

    //::::::::::: T R A N S A C T IO N S  :::::::::::
    public int getTransactionsSize() throws RemoteException;

    public void addTransaction(Event data) throws RemoteException;

    public List<Event> getTransactions() throws RemoteException;
    
    public void removeTransactions(List<String> transactions) throws RemoteException;

    public void synchronizeTransactions(IremoteP2P node) throws RemoteException;

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
    
    public List<String> getBlockchainTransactions()throws RemoteException;

}
