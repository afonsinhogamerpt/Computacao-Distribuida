/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.server;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
public class AuthenticationServer {
    
     public static void main(String[] args) {
        try {
            // Inicia o RMI Registry na porta padrão (1099)
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Cria uma instância da implementação do serviço
            AuthenticationServiceImpl authServiceImpl = new AuthenticationServiceImpl();
            
            // Registra o serviço no RMI Registry com o nome "AuthenticationService"
            Naming.rebind("//localhost/AuthenticationService", authServiceImpl);
            
            System.out.println("Servidor RMI de autenticação iniciado.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
