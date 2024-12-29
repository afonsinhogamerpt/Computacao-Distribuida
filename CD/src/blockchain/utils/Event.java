/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain.utils;

import java.io.Serializable;

/**
 *
 * @author António
 */
public class Event implements Serializable {

    private String userPublicKey; // Identificador do utilizador
    private String encryptedEvent; // Evento criptografado

    public Event(String userPublicKey, String encryptedEvent) {
        this.userPublicKey = userPublicKey;
        this.encryptedEvent = encryptedEvent;
    }

    public String getUserPublicKey() {
        return userPublicKey;
    }

    public String getEncryptedEvent() {
        return encryptedEvent;
    }

}
