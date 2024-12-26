package shared;

import cd.utils.SecurityUtils;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import javax.swing.DefaultListModel;

/**
 * *
 * Classe do utilizador que poderá ser um utilizador 'NORMAL' ou 'INSTITUICAO'.
 * Temos de adicionar o Serializable para que se possa enviar por RMI.
 *
 * @author António Gonçalves e Afonso Costa
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private PrivateKey privKey;
    private PublicKey pubKey;
    private String nome;
    private Key simKey;
    private String userType; // NORMAL ou INSTITUICAO

    public User() {
        this.nome = "noName";
        this.privKey = null;
        this.pubKey = null;
        this.simKey = null;
        this.userType = "NORMAL"; // Tipo normal por defeito
    }

    public User(String nome, String userType) {
        this.nome = nome;
        this.privKey = null;
        this.pubKey = null;
        this.simKey = null;
        this.userType = userType;
    }

    public void generateKeys() throws Exception {
        KeyPair kp = SecurityUtils.generateECKeyPair(256);
        this.simKey = SecurityUtils.generateAESKey(256);
        this.privKey = kp.getPrivate();
        this.pubKey = kp.getPublic();
    }

    
    /***
     * Salva o utilizador
     * @param password
     * @throws Exception 
     */
    public void save(String password) throws Exception {
        byte[] secret = SecurityUtils.encrypt(this.privKey.getEncoded(), password);
        byte[] sim = SecurityUtils.encrypt(this.simKey.getEncoded(), password);

        // Codificar o userType em formato simples (por exemplo, texto) para ser salvo
        byte[] userTypeData = this.userType.getBytes();

        // Salva as chaves e o tipo de utilizador em arquivos
        Files.write(Path.of(this.nome + ".sim"), sim);
        Files.write(Path.of(this.nome + ".priv"), secret);
        Files.write(Path.of(this.nome + ".pub"), this.pubKey.getEncoded());
        Files.write(Path.of(this.nome + ".type"), userTypeData);  // Verifique se este arquivo é criado
    }

    
    /***
     * Carrega o utilizaddor
     * @param password
     * @throws Exception 
     */
    public void load(String password) throws Exception {
        byte[] privData = Files.readAllBytes(Path.of(this.nome + ".priv"));
        byte[] simData = Files.readAllBytes(Path.of(this.nome + ".sim"));
        byte[] pubData = Files.readAllBytes(Path.of(this.nome + ".pub"));
        byte[] userTypeData = Files.readAllBytes(Path.of(this.nome + ".type"));

        privData = SecurityUtils.decrypt(privData, password);
        simData = SecurityUtils.decrypt(simData, password);

        this.privKey = SecurityUtils.getPrivateKey(privData);
        this.pubKey = SecurityUtils.getPublicKey(pubData);
        this.userType = new String(userTypeData);
    }

    public String getPublicKeyEncoded() {
        return Base64.getEncoder().encodeToString(pubKey.getEncoded());
    }

    public String getUserType() {
        return userType;
    }

    public PublicKey getPub() {
        return pubKey;
    }

    public String getNome() {
        return nome;
    }

    /***
     * Usado para a lista de utilizadores encontrados no sistema.
     * @return 
     */
    public DefaultListModel getUsers() {
        DefaultListModel model = new DefaultListModel();
        File rootDir = new File(".");
        File[] files = rootDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".pub")) {
                model.addElement(file.getName().split("\\.")[0]);
            }
        }
        return model;
    }
}
