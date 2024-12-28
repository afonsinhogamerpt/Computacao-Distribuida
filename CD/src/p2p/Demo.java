/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package p2p;

/**
 *
 * @author Trabalho
 */
public class Demo {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
             final int k = i;
             java.awt.EventQueue.invokeLater(() -> {
                 new NodeP2PGui(k).setVisible(true);
             });
            
            Thread.sleep(1000);
            
        }
    }
    
}
