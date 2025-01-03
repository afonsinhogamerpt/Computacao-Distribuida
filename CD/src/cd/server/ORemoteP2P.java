/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.server;

import shared.IremoteP2P;
import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.MerkleTree;
import core.Event;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import miner.Miner;
import p2p.P2Plistener;
import shared.User;
import utils.RMI;

/**
 *
 * @author António
 */
public class ORemoteP2P extends UnicastRemoteObject implements IremoteP2P {

    final static String BLOCHAIN_FILENAME = "fileCurriculumVitae.obj"; // temos de salvar afinal apenas a blockchain!?
    String address;
    CopyOnWriteArrayList<IremoteP2P> networkPeers;
    // Set - conjunto de elementos não repetidos para acesso concorrente
    CopyOnWriteArraySet<Event> pendingEvents;
    P2Plistener p2pListener;
    //objeto mineiro concorrente e distribuido
    Miner myMiner; // cuidado com o miner que usas tem de ser o miner.miner.
    //objeto da blockchain preparada para acesso concorrente
    BlockChain myBlockchain;

    /**
     * *
     * O adress faz referencia ao ponto de acesso remoto.
     *
     * @param address
     * @param p2pListener
     * @throws RemoteException
     */
    public ORemoteP2P(String address, P2Plistener listener) throws RemoteException {
        super(RMI.getAdressPort(address));
        this.address = address;
        this.p2pListener = p2pListener;
        this.networkPeers = new CopyOnWriteArrayList<>();
        pendingEvents = new CopyOnWriteArraySet<>(); // não sei se Array set será a melhor jogada.
        this.myMiner = new Miner(listener);
        this.p2pListener = listener;
        this.myBlockchain = new BlockChain();
        //new BlockChain(BLOCHAIN_FILENAME); // provavelmente tem de estar dentro de um try catch para que ao 
        //iniciar faça o bloco genesis caso nao exista.

        listener.onStartRemote("Object " + address + " listening");

    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::  N E T WO R K  :::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * *
     *
     * @return @throws RemoteException
     */
    @Override
    public String getAdress() throws RemoteException {
        return address;
    }

    /**
     * Método verifica se um nó está na rede e elmina os que não responderem
     *
     * @param adress endereço do no
     * @return true se estiver na rede falso caso contrario
     */
    private boolean isInNetwork(String adress) {
        //fazer o acesso iterado pelo fim do array para remover os nos inativos
        for (int i = networkPeers.size() - 1; i >= 0; i--) {
            try {
                //se o no responder e o endereço for igual
                if (networkPeers.get(i).getAdress().equals(adress)) {
                    // no esta na rede 
                    return true;
                }
            } catch (RemoteException ex) {
                //remover os nós que não respondem
                networkPeers.remove(i);
            }
        }
        return false;
    }

    /**
     * *
     * Método para adicionar um nó a rede.
     *
     * @param node
     * @throws RemoteException
     */
    @Override
    public void addNode(IremoteP2P node) throws RemoteException {
        //se já tiver o nó  ---  não faz nada
        if (isInNetwork(node.getAdress())) {
            return;
        }
        p2pListener.onMessage("Network addNode ", node.getAdress());
        //adicionar o no á rede
        networkPeers.add(node);

        p2pListener.onConect(node.getAdress());
        // pedir ao no para nos adicionar
        node.addNode(this);
        //propagar o no na rede
        for (IremoteP2P iremoteP2P : networkPeers) {
            iremoteP2P.addNode(node);
        }

        //sicronizar as transaçoes
        synchronizeEvents(node);
        //sincronizar a blockchain
        synchnonizeBlockchain();
    }

    @Override
    public List<IremoteP2P> getNetwork() throws RemoteException {
        return new ArrayList<>(networkPeers);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::  N E T WO R K  :::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::  T R A N S A C T IO N S ::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    public int getEventsSize() throws RemoteException {
        return pendingEvents.size();
    }

    /**
     * *
     * Método serve para adicionar eventos 1º passo: Verifica se o evento já
     * existe na lista 1.1º passo: caso exista nao faz nada. 1.2º passo: caso
     * nao exista - adiciona na pendingEvents. 2º passo: Verifica o size, caso
     * seja 8 cria o bloco, mina e adiciona na blockchain.
     *
     * @param event
     * @param userFrom
     * @param userTo
     * @throws RemoteException
     */
    @Override
    public void addEvent(String event, User userFrom, User userTo) throws RemoteException {
        
        try{
            userTo.loadPublic();
        }catch(Exception e){
             System.out.println("user not exist in server");
        }
        
        
        // verificar 
        try {
            Event newEvent = new Event(event, userFrom, userTo);
            newEvent.EncryptEvent();
            pendingEvents.add(newEvent);
            
            System.out.println(getEventsSize());

            // Adicionar a transacao aos nos da rede
            for (IremoteP2P iremoteP2P : networkPeers) {
                iremoteP2P.addEvent(event, userFrom, userTo);
            }

            if (getEventsSize() >= 8) {
                MerkleTree mkt = new MerkleTree(pendingEvents.stream()
                        .map(pendingEvent -> pendingEvent.event)
                        .toList());

                long timestamp = System.currentTimeMillis();
                mkt.saveToFile(timestamp + ".mkt");

                String merkleRoot = mkt.getRoot();
                String prevHash = getBlockchainLastHash(); // vai buscar a hash do ultimo bloco
                String dataToMine = prevHash + merkleRoot;

                int nonce = mine(dataToMine, 4); // estes zeros podem ser dinamicos retorna o nonce
                stopMining(nonce);
                // Create the new block
                Block newBlock = new Block(prevHash, pendingEvents);
                newBlock.setNonce(nonce);
                
                // nao sei se temos de voltar a fazer pois o construtor acho que já faz
                newBlock.setMerkleRoot(merkleRoot); // nao sei se temos de dar set da merkle outra vez
                newBlock.setPendingEvents(pendingEvents);
                // nao sei se temos de voltar a fazer pois o construtor acho que já faz
                newBlock.signBlock(userFrom.getPriv());
                addBlock(newBlock);
                removeEvents(new ArrayList<>(pendingEvents));
                System.out.println(getBlockchainSize());
                System.out.println(getEventsSize());
            }
        } catch (Exception e) {

        }

    }

    
    /***
     * método retorna os eventos que estão a ser processados.
     * @return
     * @throws RemoteException 
     */
    @Override
    public List<Event> getEvents() throws RemoteException {
         return new ArrayList<>(pendingEvents);
    }

    
    /***
     * Este método vai servir para remover os eventos inseridos e propagar essa informação aos restantes nós.
     * @param events
     * @throws RemoteException 
     */
    @Override
    public void removeEvents(List<Event> events) throws RemoteException {
        // o metodo clear é thread safe por isso acho que podemos usar.
        pendingEvents.removeAll(events);
        p2pListener.onTransaction("remove " + getEventsSize() + "transactions");
        //propagar as remoções
        for (IremoteP2P iremoteP2P : networkPeers) {
            //se houver algum elemento em comum nas transações remotas
            if (iremoteP2P.getEvents().retainAll(pendingEvents)) {
                //remover as transaçoies
                iremoteP2P.removeEvents(events);
            }
        }
    }

    @Override
    public void synchronizeEvents(IremoteP2P node) throws RemoteException {
         //tamanho anterior
        int oldsize = getEventsSize();
        p2pListener.onMessage("sinchronizeTransactions", node.getAdress());
        // juntar as transacoes todas (SET elimina as repetidas)
        this.pendingEvents.addAll(node.getEvents());
        int newSize = pendingEvents.size();
        //se o tamanho for incrementado
        if (oldsize < newSize) {
            p2pListener.onMessage("sinchronizeTransactions", "tamanho diferente");
            //pedir ao no para sincronizar com as nossas
            node.synchronizeEvents(this);
            p2pListener.onTransaction(address);
            p2pListener.onMessage("sinchronizeTransactions", "node.sinchronizeTransactions(this)");
            //pedir á rede para se sincronizar
            for (IremoteP2P iremoteP2P : networkPeers) {
                //se o tamanho for menor
                if (iremoteP2P.getEventsSize()< newSize) {
                    //cincronizar-se com o no actual
                    p2pListener.onMessage("sinchronizeTransactions", " iremoteP2P.sinchronizeTransactions(this)");
                    iremoteP2P.synchronizeEvents(this);
                }
            }
        }
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::::::  T R A N S A C T IO N S ::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    public void startMining(String msg, int zeros) throws RemoteException {
              try {
            //colocar a mineiro a minar
            myMiner.startMining(msg, zeros);
            p2pListener.onStartMining(msg, zeros);
            //mandar minar a rede
            for (IremoteP2P iremoteP2P : networkPeers) {
                //se o nodo nao estiver a minar
                if (!iremoteP2P.isMining()) {
                    p2pListener.onStartMining(iremoteP2P.getAdress() + " mining", zeros);
                    //iniciar a mineracao no nodo
                    iremoteP2P.startMining(msg, zeros);
                }
            }
        } catch (Exception ex) {
            p2pListener.onException(ex, "startMining");
        }
    }

    @Override
    public void stopMining(int nonce) throws RemoteException {
        //parar o mineiro e distribuir o nonce
        myMiner.stopMining(nonce);
        //mandar parar a rede
        for (IremoteP2P iremoteP2P : networkPeers) {
            //se o nodo estiver a minar   
            if (iremoteP2P.isMining()) {
                //parar a mineração no nodo 
                iremoteP2P.stopMining(nonce);
            }
        }
    }

    @Override
    public boolean isMining() throws RemoteException {
      return myMiner.isMining();
    }

    @Override
    public int mine(String msg, int zeros) throws RemoteException {
         try {
            //começar a minar a mensagem
            startMining(msg, zeros);
            //esperar que o nonce seja calculado
            return myMiner.waitToNonce();
        } catch (InterruptedException ex) {
            p2pListener.onException(ex, "Mine");
            return -1;
        }
    }

    @Override
    public void addBlock(Block b) throws RemoteException {
      try {
            //se não for válido
            if (!b.isValid()) {
                throw new RemoteException("invalid block");
            }
            //se encaixar adicionar o bloco
            if (myBlockchain.getLastBlockHash().equals(b.getPreviousHash())) {
                myBlockchain.add(b);
                //guardar a blockchain
                myBlockchain.save(BLOCHAIN_FILENAME);
                p2pListener.onBlockchainUpdate(myBlockchain);
            }
            //propagar o bloco pela rede
            for (IremoteP2P iremoteP2P : networkPeers) {
                //se encaixar na blockcahin dos nodos remotos
                if (!iremoteP2P.getBlockchainLastHash().equals(b.getPreviousHash())
                        || //ou o tamanho da remota for menor
                        iremoteP2P.getBlockchainSize() < myBlockchain.getSize()) {
                    //adicionar o bloco ao nodo remoto
                    iremoteP2P.addBlock(b);
                }
            }
            //se não encaixou)
            if (!myBlockchain.getLastBlockHash().equals(b.getCurrentHash())) {
                //sincronizar a blockchain
                synchnonizeBlockchain();
            }
        } catch (Exception ex) {
            p2pListener.onException(ex, "Add bloco " + b);
        }
    }

    @Override
    public int getBlockchainSize() throws RemoteException {
        return myBlockchain.getSize();
    }

    @Override
    public String getBlockchainLastHash() throws RemoteException {
       return myBlockchain.getLastBlockHash();
    }

    @Override
    public BlockChain getBlockchain() throws RemoteException {
       return myBlockchain;
    }

    @Override
    public void synchnonizeBlockchain() throws RemoteException {
        //para todos os nodos da rede
        for (IremoteP2P iremoteP2P : networkPeers) {
            //se a blockchain for maior
            if (iremoteP2P.getBlockchainSize() > myBlockchain.getSize()) {
                BlockChain remote = iremoteP2P.getBlockchain();
                //e a blockchain for válida
                if (remote.isValid()) {
                    //atualizar toda a blockchain
                    myBlockchain = remote;
                    //deveria sincronizar apenas os blocos que faltam
                    p2pListener.onBlockchainUpdate(myBlockchain);
                }
            }
        }
    }
    
    

    /*
    @Override
    public List<String> getBlockchainTransactions() throws RemoteException {
          ArrayList<String> allTransactions = new ArrayList<>();
        for(Block b: myBlockchain.getChain()){
            allTransactions.addAll(b.events());
        }
        return allTransactions;
    }
*/

}
