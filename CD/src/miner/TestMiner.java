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
//::                                                               (c)2024   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////

package miner;

/**
 * Created on 10/12/2024, 15:27:22 
 * @author manso - computer
 */
public class TestMiner {
    public static void main(String[] args) throws Exception {
        String msg = "Hello distributed mining world!";
        Miner m = new Miner(null);
        int nonce = m.mine(msg, 4);
        System.out.println("Message = " + msg);
        System.out.println("Nonce   = "+ nonce);
        System.out.println("Hash    = " + Miner.getHash(msg, nonce));
        
    }

}
