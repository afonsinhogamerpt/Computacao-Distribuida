/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.server;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
import blockchain.utils.Hash;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.Key;
import java.security.PublicKey;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import shared.AuthenticationService;
import shared.User;

/**
 * *
 * Lembrar que isto corre no servidor, o utilizador apenas faz uso das funções
 * mas as mesmas estão a correr no servidor.
 *
 * @author António Gonçalves e Afonso Costa
 */
public class AuthenticationServiceImpl extends UnicastRemoteObject implements AuthenticationService {

    private final ExecutorService executorService;
    private final LinkedBlockingQueue<Runnable> taskQueue;
    private final String publicKeyDir = "server_pub_keys"; // nome da pasta para armazenar as chaves publicas.

    /**
     * *
     * Lista de tarefas para 100 pedidos, bem como uma pool de 10 threads
     * disponíveis para executar as tarefas em lista. Cria pasta para chaves
     * públicas, se não existir...
     *
     * @throws RemoteException
     */
    public AuthenticationServiceImpl() throws RemoteException, Exception {
        super();

        this.taskQueue = new LinkedBlockingQueue<>(100);
        this.executorService = Executors.newFixedThreadPool(10);

        File dir = new File(publicKeyDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        startProcessingQueue();
    }

    /**
     * *
     * Vai executando as tarefas que estiverem disponiveis na lista.
     */
    private void startProcessingQueue() {
        Runnable taskProcessor = () -> {
            try {
                while (true) {
                    Runnable task = taskQueue.take();
                    task.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        new Thread(taskProcessor).start();
    }

    /**
     * 1ºpasso: verificação da existencia de utilizador 1.1º: caso não exista
     * chave publica devolvemos logo false, pois nao existe
     *
     * @param username
     * @param password
     * @return
     * @throws RemoteException
     */
    @Override
    public User login(String username, String password) throws RemoteException {
        // Verificar se a chave pública do usuário existe
        Path publicKeyPath = Path.of(publicKeyDir, username + ".pub");
        if (!Files.exists(publicKeyPath)) {
            System.out.println("Utilizador não encontrado.");
            return null;  // Retorna null caso o usuário não exista
        }

        try {

            // Verificar se o hash da senha existe
            Path passwordPath = Path.of(publicKeyDir, username + ".password");
            if (!Files.exists(passwordPath)) {
                System.out.println("Senha não encontrada para o utilizador.");
                return null;
            }

            // Carregar o hash da senha armazenado
            String storedPasswordHash = Files.readString(passwordPath);

            // Gerar o hash da senha fornecida
            String providedPasswordHash = Hash.getHash(password);

            // Comparar as duas hash
            if (!storedPasswordHash.equals(providedPasswordHash)) {
                System.out.println("Senha incorreta.");
                return null; // Retorna null caso as senhas não correspondam
            }
            // constroi o utilizador.
            User user = new User(username, "NORMAL");  // Tipo "NORMAL" como padrão

            return user;

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Retorna null em caso de erro
        }
    }

    /**
     * *
     * Registo do utilizador
     *
     * @param username
     * @param password
     * @param userType
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean register(String username, String publicKeyEncoded, String userType, String password) throws RemoteException {
        taskQueue.offer(() -> {
            try {

                // Salva a chave pública no servidor.
                savePublicKey(username, publicKeyEncoded);

                //salva o tipo do utilizador no servidor.
                saveUserType(username, userType);

                //salva a password
                savePassword(username, password);

                System.out.println("Registro bem-sucedido para " + username + " como " + userType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    private void savePublicKey(String username, String publicKeyEncoded) throws IOException {
        Path publicKeyPath = Path.of(publicKeyDir, username + ".pub");
        Files.write(publicKeyPath, publicKeyEncoded.getBytes());
        System.out.println("Chave pública de " + username + " guardada no servidor.");
    }

    private void saveUserType(String username, String userType) throws IOException {
        Path publicKeyPath = Path.of(publicKeyDir, username + ".type");
        Files.write(publicKeyPath, userType.getBytes());
        System.out.println("Tipo " + userType + " de " + username + " guardada no servidor.");
    }

    private void savePassword(String username, String passwordHash) throws IOException {
        Path publicKeyPath = Path.of(publicKeyDir, username + ".password");
        Files.write(publicKeyPath, passwordHash.getBytes());
        System.out.println("Password de " + username + " guardada no servidor.");
    }

    public void shutdown() {
        executorService.shutdown();
    }

    /*@Override
    public void addCurriculum(String curriculum, String username) throws RemoteException {
        try {
            byte[] content = utils.SecurityUtils.encrypt(curriculum.getBytes(), keyServer);
            Path pathName = Path.of(curriculumFilesDir, username ,".cur");
            Files.write(pathName, content);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
       
    }

    public DefaultListModel getCurriculum() {
        DefaultListModel model = new DefaultListModel();
        File rootDir = new File(".");
        File[] files = rootDir.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".cur")) {
                model.addElement(file.getName().split("\\.")[0]);
            }
        }
        return model;
    }
     */
}
