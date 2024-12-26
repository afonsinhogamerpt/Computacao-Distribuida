/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
public interface AuthenticationService extends Remote {
    boolean login(String username, String password) throws RemoteException;
    boolean register(String username, String password, String userType) throws RemoteException;
}
