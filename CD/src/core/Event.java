/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.Base64;
import shared.User;
import utils.SecurityUtils;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
public class Event implements Serializable {

    public String event; // evento vai ser encoded pela chave publica do utilizador associado ao evento.

    public String userTo; // utilizador que vai ficar associado ao evento.
    public String userFrom; // utilizador que vai ficar associado ao bloco (nao utilizado para a classe por enquanto (escabilidade)).

    private PublicKey fromPub;
    private PublicKey toPub;

    /**
     * *
     *
     * @param event
     * @param userTo
     */
    public Event(String event, String userTo) {
        this.event = event;
        this.userTo = userTo;
    }

    /**
     * *
     *
     * @param event
     * @param userFrom
     * @param userTo
     */
    public Event(String event, User userFrom, User userTo) {
        this.event = event;
        this.userTo = userTo.getNome();
        this.userFrom = userFrom.getNome();
        this.fromPub = userFrom.getPub();
        this.toPub = userTo.getPub();
    }
    
    /***
     * Encripta o evento com a chave publica do utilizador que vai ficar associado ao mesmo.
     * @return
     * @throws Exception 
     */
    public void EncryptEvent() throws Exception {
        byte[] encryptedEvent = utils.SecurityUtils.encrypt(event.getBytes(), toPub);
        String encodedEvent = Base64.getEncoder().encodeToString(encryptedEvent);
        this.event = encodedEvent;
    }

    @Override
    public String toString() {
        return ""+event;
    }

}
