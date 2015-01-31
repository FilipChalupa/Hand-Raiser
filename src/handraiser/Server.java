package handraiser;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Filip
 */
public class Server extends Network {
    private TeacherFrame frame;
    private final Map<Integer, ServerServant> clients = new HashMap<>();
    private ServerSocket server;

    @Override
    public void run() {
        server = null;
        Socket client;
        int idCounter = 0;
        isRunning = true;
        
        try {
            server = new ServerSocket(PORT);
        } catch (Exception e) {
            frame.connectionFailed("Sorry, server couldn't be created.");
        }
        try {
            while (isRunning) {
                try {
                    client = server.accept();
                    ServerServant serverServant = new ServerServant(client, this, ++idCounter, settings.getClassName());
                    serverServant.start();
                    clients.put(idCounter, serverServant);
                    frame.updateConnectedCounter(clients.size());
                } catch (IOException e) {
                    // Warning: Client couldn't connect
                }
            }
            killClients();
        } catch (Exception e) {
            frame.connectionFailed("Sorry, something went wrong.");
        }
        close();
    }
    
    public void setFrame(TeacherFrame frame) {
        this.frame = frame;
    }
    
    public synchronized void action(int id, String hostName, String studentName, String action) {
        switch (action) {
            case "question":
                frame.addQuestion(id, hostName, studentName);
                break;
            case "solution":
                frame.addSolution(id, hostName, studentName);
                break;
            case "cancell":
                frame.cancellQuestionAndSolution(id);
                break;
            case "quit":
                action(id, hostName, studentName, "cancell");
                clients.remove(id);
                frame.updateConnectedCounter(clients.size());
                break;
            default:
                break;
        }
        if (action.equals("question") || action.equals("solution") || action.equals("cancell")) {
            frame.updateWaitingCounters();
        }
    }
    private synchronized void killClients() {
        for (Integer key : clients.keySet()) {
            clients.get(key).close();
        }
        clients.clear();
    }
    public synchronized void close() {
        stop();
        if(server != null) {
            try {
                server.close();
            } catch(IOException e) {}
        }
    }
    public synchronized void sendAction(int id, String primary, String secondary) {
        ServerServant client = clients.get(id);
        if (client != null) {
            client.sendAction(primary, secondary);
        }
        if (primary.equals("cancell")) {
            frame.updateWaitingCounters();
        }
    }
}
