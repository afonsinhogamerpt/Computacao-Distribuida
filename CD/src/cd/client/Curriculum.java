/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.client;

import blockchain.utils.MerkleTree;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    blockchain.utils.BlockChain bc;    
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
        try 
        {
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
            try{
                byte[] content = Files.readAllBytes(file.toPath());
                content = utils.SecurityUtils.decrypt(content, user.getPriv());
                model.addElement(file.getName());
            }catch (Exception e){
                //System.out.println("Ficheiro nao faz parte a este user: " + file);
            }
            
        }
        
        return model;
    }
    
    public List<String> getCurriculumList(User user) throws IOException, Exception{
        File server_curriculum = new File("server_curriculum.");
        for(File file : server_curriculum.listFiles()){
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
}
