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

import cd.utils.SecurityUtils;
import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created on 28/09/2022, 11:02:33
 *
 * @author IPT - computer
 * @version 1.0
 */
public class Hash {
    public static final String alg = "SHA3-256";

    public static String toHexString(int n) {
        return Integer.toHexString(n).toUpperCase();
    }

    public static String getHash(String data) {
        try {
            byte[] hash = SecurityUtils.calculateHash(data.getBytes(), alg);
            return new BigInteger(hash).abs().toString(16);
        } catch (Exception ex) {
            Logger.getLogger(Hash.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR in HASH";
        
       
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202209281102L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
