/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package core;

import blockchain.utils.Block;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.swing.DefaultListModel;
import shared.User;

/**
 *
 * @author afons
 */
public class Curriculum {

    private final String curriculumFilesDir = "server_curriculum";
    private String from;
    private String to;

    private String fromPub;
    private String toPub;

    private String signature;

    List<String> elements = new ArrayList<>();

    public Curriculum() {
        File dir = new File(curriculumFilesDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public Curriculum(String to) {
        this.to = to;
    }

    public Curriculum(String from, String to, List<String> elements) {
        this.from = from;
        this.to = to;
        this.elements = elements;
    }

    public Curriculum(String fromPub, String toPub) {
        this.fromPub = fromPub;
        this.toPub = toPub;
    }

    /*
    public void sign(PrivateKey priv, File file) throws Exception {
        byte[] content = Files.readAllBytes(file.toPath());
        byte[] dataSign = utils.SecurityUtils.sign(
                (fromPub + toPub + content).getBytes(),
                priv);
        this.signature = Base64.getEncoder().encodeToString(dataSign);
    }*/
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromPub() {
        return fromPub;
    }

    public void setFromPub(String fromPub) {
        this.fromPub = fromPub;
    }

    public String getToPub() {
        return toPub;
    }

    public void setToPub(String toPub) {
        this.toPub = toPub;
    }

    public void addCurriculum(String curriculum, User user) throws RemoteException {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyyHHmm");
            String formattedDateTime = now.format(formatter);

            byte[] content = utils.SecurityUtils.encrypt(curriculum.getBytes(), user.getPub());
            Path pathName = Path.of(curriculumFilesDir, user.getNome() + "-" + formattedDateTime + ".cur");

            Files.write(pathName, content);
            elements.add("" + content + pathName);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public DefaultListModel getCurriculum(User user) throws IOException, Exception {
        DefaultListModel model = new DefaultListModel();

        File server_curriculum = new File(curriculumFilesDir);
        File[] files = server_curriculum.listFiles();

        for (File file : files) {
            //se conseguir desencriptar o file com a sua chave privada
            //adicionar ao model
            //caso contrário, não faz nada
            try {
                byte[] content = Files.readAllBytes(file.toPath());
                content = utils.SecurityUtils.decrypt(content, user.getPriv());
                model.addElement(file.getName());
            } catch (Exception e) {
                //System.out.println("Ficheiro nao faz parte a este user: " + file);
            }

        }

        return model;
    }

    public List<String> getCurriculumList(User user) throws IOException, Exception {
        File server_curriculum = new File("server_curriculum.");
        for (File file : server_curriculum.listFiles()) {
            byte[] content = Files.readAllBytes(file.toPath());
            //adicionei o getPriv() mas não deve ser a maneira correta para ir buscar a chave privada
            content = utils.SecurityUtils.decrypt(content, user.getPriv());
            String curriculum = new String(content, StandardCharsets.UTF_8);
            //System.out.println(curriculum);
            elements.add(curriculum);
        }
        return elements;
    }

    //metodo para listar users
    //metodo para adicionar cenas bc
    //if(elements.size()>=8)
    //  bc.add
    @Override
    public String toString() {
        return "elements=" + elements;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    ///FUNCOES DE TESTE //////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////
    
    /***
     * Adicionar Evento a lista até atingir 8 posições, após isso adiciona na blockchain.
     * @param event
     * @param user
     * @throws Exception 
     */
    public void addEvent(String event, User user, blockchain.utils.BlockChain bc, List<Event> pendingEvents) throws Exception {

        //carregar o utilizador que vai ficar associado ao evento
        User uTo = new User(to, "NORMAL");
        uTo.loadPublic();

        // encripta o evento com a chave pública do utilizador para que seja adicionado.
        byte[] encryptedEvent = utils.SecurityUtils.encrypt(event.getBytes(), uTo.getPub());
        String encodedEvent = Base64.getEncoder().encodeToString(encryptedEvent);
        Event newEvent = new Event(uTo.getPub().toString(), encodedEvent);

        // Adiciona evento à lista de pendentes
        pendingEvents.add(newEvent);

        System.out.println("evento adicionado");

        // Quando a lista atingir 8 eventos, cria um bloco
        if (pendingEvents.size() >= 8) {
            Block newBlock = new Block(bc.getLastBlockHash(), new ArrayList<>(pendingEvents));
            int nonce = 0;
            int difficulty = 4; // Number of leading zeros required in the hash
            while (true) {
                try {
                    //newBlock.setNonce(nonce, difficulty);
                    break; // Exit loop when a valid nonce is found
                } catch (Exception e) {
                    nonce++; // Increment nonce and retry
                }
            }
            newBlock.signBlock(user.getPriv()); // Assina com a chave privada da instituição
            bc.add(newBlock);
            pendingEvents.clear();
        }
    }

    /**
     * *
     * Listar eventos de um utilizador.
     *
     * @param user
     * @return
     * @throws Exception
     */
//    public List<String> getUserEvents(User user, blockchain.utils.BlockChain bc) throws Exception {
//        List<String> userEvents = new ArrayList<>();
//        for (Block block : bc.getBlocks()) {
//            for (Event event : block.getEvents()) {
//                if (event.getUserPublicKey().equals(user.getPub().toString())) {
//                    byte[] encryptedEvent = Base64.getDecoder().decode(event.getEncryptedEvent());
//                    String decryptedEvent = new String(utils.SecurityUtils.decrypt(encryptedEvent, user.getPriv()));
//                    userEvents.add(decryptedEvent);
//                }
//            }
//        }
//        return userEvents;
//    }

}
