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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on 28/09/2022, 11:13:39
 *
 * @author IPT - computer
 * @version 1.0
 */
public class MinerConcurrent {

    private static class Thr extends Thread {

        AtomicInteger ticketNonce;
        AtomicInteger truenonce;
        String data;
        int dificulty;

        public Thr(AtomicInteger nonce, AtomicInteger truenonce, String data, int dificulty) {
            this.ticketNonce = nonce;
            this.truenonce = truenonce;
            this.data = data;
            this.dificulty = dificulty;
        }

        public void run() {
            //String of zeros
            String zeros = String.format("%0" + dificulty + "d", 0);
            //starting nonce

            while (truenonce.get() == 0) {
                //calculate hash of block
                int nonce = ticketNonce.getAndIncrement();
                String hash = Hash.getHash(nonce + data);
                //DEBUG .... DEBUG .... DEBUG .... DEBUG .... DEBUG .... DEBUG
                //System.out.println(nonce + " " + hash);
                //Nounce found
                if (hash.endsWith(zeros)) {
                    truenonce.set(nonce);
                }
            }
        }

    }
    //maximum number of Nonce
    public static int MAX_NONCE = (int) 1E9;

    public static int getNonce(String data, int dificulty) {
        AtomicInteger sharedNonce = new AtomicInteger(0);
        AtomicInteger truNonce = new AtomicInteger(0);
        int cores = Runtime.getRuntime().availableProcessors();
        Thr[] thr = new Thr[cores];
        for (int i = 0; i < thr.length; i++) {
            thr[i] = new Thr(sharedNonce, truNonce, data, dificulty);
            thr[i].start();
        }
        try {
            //esperar que a primeira termine == todas terminarem
            thr[0].join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MinerConcurrent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return truNonce.get();
    }
    
    public static void main(String[] args) {
        String txt = "hello secure world!!!!";
        int dif = 5;
        
        long timeS = System.currentTimeMillis();
        int nonces = Miner.getNonce(txt, dif);
        timeS = System.currentTimeMillis() - timeS;
        String hash = Hash.getHash(nonces + txt);
        System.out.println(nonces + " " + hash);
        
        
        long timeP = System.currentTimeMillis();
        int nonce = getNonce(txt, dif);
        timeP = System.currentTimeMillis() - timeP;
        
        String hash2 = Hash.getHash(nonce + txt);
        System.out.println(nonce + " " + hash2);
        
        System.out.println("Aceleracao = " + timeS / (double)timeP);
        
        
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202209281113L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
