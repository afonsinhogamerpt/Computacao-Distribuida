/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package teste;

import core.mainCore;
import java.io.Serializable;

/**
 *
 * @author afons
 */
public class Teste implements Serializable{
    public static void main(String[] args) throws Exception {
        //mainCore core = new mainCore();
        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println(cores);
    }
}
