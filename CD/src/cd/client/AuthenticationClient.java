/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.client;

import java.util.Scanner;
import java.rmi.Naming;
import shared.AuthenticationService;
import shared.User;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
public class AuthenticationClient {
    
    public static void main(String[] args) {
        try {
            AuthenticationService authService = (AuthenticationService) Naming.lookup("//localhost:1099/AuthenticationService");

            Scanner scanner = new Scanner(System.in);
            System.out.println("Digite 1 para Login ou 2 para Registro:");
            int choice = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Digite o nome de utilizador:");
            String username = scanner.nextLine();
            System.out.println("Digite a senha:");
            String password = scanner.nextLine();

            if (choice == 1) {
                // Receber o objeto User ao invés de um boolean
                User user = authService.login(username, password);

                if (user != null) {
                    System.out.println("Login bem-sucedido!");
                    System.out.println("utilizador: " + user.getNome());
                    System.out.println("Tipo de utilizador: " + user.getUserType());
                } else {
                    System.out.println("Falha no login.");
                }

            } else if (choice == 2) {
                System.out.println("Digite o tipo de utilizador (NORMAL ou INSTITUICAO):");
                String userType = scanner.nextLine();

                User user = new User(username, userType);
                user.generateKeys();
                user.save(password);

                if (authService.register(username, password, userType)) {
                    System.out.println("Registro bem-sucedido!");
                } else {
                    System.out.println("Falha no registro.");
                }
            } else {
                System.out.println("Opção inválida.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
