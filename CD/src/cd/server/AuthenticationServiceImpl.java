/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cd.server;

/**
 *
 * @author António Gonçalves e Afonso Costa
 */
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.*;
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
    public AuthenticationServiceImpl() throws RemoteException {
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
    public boolean login(String username, String password) throws RemoteException {
        taskQueue.offer(() -> {
            try {
                Path publicKeyPath = Path.of(publicKeyDir, username + ".pub");
                if (!Files.exists(publicKeyPath)) {
                    System.out.println("Utilizador não se encontra registado.");
                    return;
                }

                // Carregar as informações do utilizador
                User user = new User(username, "NORMAL");  // Tipo "NORMAL" como padrão
                user.load(password);  // Carregar a senha, o que também carrega o tipo de utilizador

                // Verificar o tipo de utilizador
                String userType = user.getUserType();
                if ("INSTITUICAO".equals(userType)) {
                    System.out.println("Utilizador " + username + " é uma instituição.");
                    // Tratar caso seja uma instituição
                    // Por exemplo, você pode definir permissões ou comportamentos específicos
                } else {
                    System.out.println("Utilizador " + username + " é normal.");
                }

                System.out.println("Login bem-sucedido para " + username + " com tipo " + userType);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return true;
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
    public boolean register(String username, String password, String userType) throws RemoteException {
        taskQueue.offer(() -> {
            try {
                User user = new User(username, userType);  // O tipo de utilizador pode ser "NORMAL" ou "INSTITUICAO"
                user.generateKeys();
                user.save(password); // Salva chaves localmente no cliente

                // Salva a chave pública no servidor
                savePublicKey(username, user.getPublicKeyEncoded());

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
        System.out.println("Chave pública de " + username + " salva no servidor.");
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
