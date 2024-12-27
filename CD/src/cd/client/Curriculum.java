/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultListModel;
import shared.User;

/**
 *
 * @author afons
 */
public class Curriculum {
    private final String curriculumFilesDir = "server_curriculum";


    public Curriculum() {
        File dir = new File(curriculumFilesDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
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
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       
    }
    
    public DefaultListModel getCurriculum() throws IOException {
        DefaultListModel model = new DefaultListModel();
    
        File server_curriculum = new File("server_curriculum.");
        File[] files = server_curriculum.listFiles();
      
        for (File file : files) {
            model.addElement(file.getName());
        }
        
        return model;
    }
}
