/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.MerkleTree;
import blockchain.utils.MinerConcurrent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import shared.User;

/**
 *
 * @author António
 */
public class mainCore implements Serializable {

    blockchain.utils.BlockChain bc;
    private List<Event> pendingEvents;
    public transient static int DIFICULTY = 5; // pode ser ajustada consoante o tempo que os mineiros vao demorando na rede.
    public transient static String fileCurriculumVitae = "fileCurriculumVitae.obj"; 
     
    public mainCore() throws Exception {
        try{
            User u = new User("jorge", "INSTITUICAO");
            pendingEvents = new ArrayList<>();
            List<String> str = new ArrayList<>();
            u.loadPublic();
            PublicKey ola = u.getPub();
            str.add(ola + "");
            Block block = new Block(str, "0");
            block.setNonce(0);
            //System.out.println(block.calculateHash());
            //System.out.println(block.getHeaderString());
            bc = new BlockChain();
            bc.add(block);
            save(fileCurriculumVitae);
        }catch (Exception e){
            System.out.println("erro");
        }
        
    }
    
    public mainCore(User u) throws Exception{
        
    }
    
 

    public void save(String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    /*public static mainCore load(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(fileName))) {
            return (mainCore) in.readObject(); // isto só funciona porque é serializable.
        }
    }*/
    public static mainCore load(String fileName) throws IOException, ClassNotFoundException {
    try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
        Object obj = in.readObject();
        if (obj instanceof mainCore core) {
            return core;
        } else {
            throw new ClassCastException("O objeto no arquivo não é do tipo mainCore.");
        }
    }
}
    

    public void addEvent(String event, User userFrom, User userTo) throws Exception {
        // Create and encrypt a new event
        Event newEvent = new Event(event, userFrom, userTo);
        newEvent.EncryptEvent();

        // Add the event to the pending list
        pendingEvents.add(newEvent);
        
        
        
        // When the pending list reaches 8 events, create a block
        if (pendingEvents.size() >= 8) {
            // Generate the Merkle root for the events
            MerkleTree mkt = new MerkleTree(pendingEvents.stream()
                    .map(pendingEvent -> pendingEvent.event)
                    .toList());

            String merkleRoot = mkt.getRoot();
            String prevHash = bc.getLastBlockHash();
            String dataToMine = prevHash + merkleRoot;


            // Mine the nonce for the block
            int nonce = MinerConcurrent.getNonce(dataToMine, 5);

            // Create the new block
            Block newBlock = new Block(prevHash, pendingEvents);

            // Set the nonce and recalculate the current hash
            newBlock.setNonce(nonce);

            // Sign the block using the private key of the institution
            newBlock.signBlock(userFrom.getPriv());

            // Add the block to the blockchain and clear the pending events
            bc.add(newBlock);
            pendingEvents.clear();

            System.out.println("Block added to blockchain successfully.");
        }
    }

    @Override
    public String toString() {
        return "mainCore{" + "bc=" + bc.toString() + ", pendingEvents=" + pendingEvents + '}';
    }
    
    

}
