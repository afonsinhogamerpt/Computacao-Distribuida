//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2022   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
 //////////////////////////////////////////////////////////////////////////////
package blockchain.utils;

import core.Event;
import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created on 22/08/2022, 09:23:49
 *
 * Block with consensus of Proof of Work
 *
 * @author IPT - computer
 * @version 1.0
 */
public class Block implements Serializable, Comparable<Block> {

    String previousHash; // link to previous block
    String merkleRoot;   // merkleRoot in the block
    String currentHash;  // Hash of block
    private List<Event> events; // Lista de eventos no bloco
    int nonce;           // proof of work 
    private String signature; // Assinatura da instituição pois cada bloco tem de ser adicionado por uma instituição valida.
    //List<String> transactions; // transações do bloco (devem ser guardadas em separado)
    
    //int ID;
    //Timestamp time;
    
    public Block(List<String> key, String hash, List<Event> events) {
        MerkleTree mkt = new MerkleTree(key);
        this.merkleRoot = mkt.getRoot();
        this.currentHash = calculateHash();
        this.previousHash = hash;
        this.events = events;
    }

    public void setMerkleRoot(String merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

    
    
    public Block(String previousHash, List<Event> events) {
        this.previousHash = previousHash;
        this.events = new ArrayList<>(events);
        //MerkleTree mkt = new MerkleTree(transactions);
        MerkleTree mkt = new MerkleTree(events.stream()
                .map(event -> event.event)
                .toList());
        this.merkleRoot = mkt.getRoot();
        this.currentHash = calculateHash();
    }

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///// FUNCOES NOVAS    //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////
 
    public void signBlock(PrivateKey privateKey) throws Exception {
        String dataToSign = previousHash + merkleRoot;
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);
        rsa.update(dataToSign.getBytes());
        this.signature = Base64.getEncoder().encodeToString(rsa.sign());
    }
    
    public boolean verifySignature(PublicKey publicKey) throws Exception {
        String dataToVerify = previousHash + merkleRoot;
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initVerify(publicKey);
        rsa.update(dataToVerify.getBytes());
        return rsa.verify(Base64.getDecoder().decode(signature));
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
    
    

    /////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///// FUNCOES NOVAS    //////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////


    public String getMinerData() {
        return previousHash + merkleRoot;
    }

    public String getMerkleRoot() {
        return merkleRoot;
    }

    //   public List<String> transactions() {
    //      return transactions;
    //  }
    public String getPreviousHash() {
        return previousHash;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
        //calcular o hash
        this.currentHash = Hash.getHash(nonce + getMinerData());
    }
    

    public String calculateHash() {
        return Hash.getHash(nonce + getMinerData());
    }

    public String getCurrentHash() {
        return currentHash;
    }

    @Override
    public String toString() {
        return // (isValid() ? "OK\t" : "ERROR\t")+
                String.format("[ %8s", previousHash) + " <- "
                + String.format("%-10s", merkleRoot) + String.format(" %7d ] = ", nonce)
                + String.format("%8s", currentHash)
                + "Signature: " + signature;
    }
    
    public String events(){
        List<String> str = new ArrayList<String>();
        for(Event evt : events){
            str.add(evt + "");
        }
        return "" + str;
    }
   

    public String getHeaderString() {
        return "prev Hash: " + previousHash
                + "\tMkt Root : " + merkleRoot
                + "\tnonce    : " + nonce
                + "\tcurr Hash: " + currentHash;
               
    }

    // public String getTransactionsString() {
    //     StringBuilder txt = new StringBuilder();
    //    for (String transaction : transactions) {
    //         txt.append(transaction + "\n");
    //     }
    //     return txt.toString();
    //   }
    public boolean isValid() {
        return currentHash.equals(calculateHash());
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202208220923L;

    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Block other = (Block) obj;
        if (this.nonce != other.nonce) {
            return false;
        }
        if (!Objects.equals(this.previousHash, other.previousHash)) {
            return false;
        }
        if (!Objects.equals(this.merkleRoot, other.merkleRoot)) {
            return false;
        }
        return Objects.equals(this.currentHash, other.currentHash);
    }

    @Override
    public int compareTo(Block o) {
        return this.currentHash.compareTo(o.currentHash);
    }

}
