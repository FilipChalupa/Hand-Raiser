package handraiser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Filip
 */
public class ServerServant extends Thread {
    private final Socket socket;
    private final int id;
    private final String className;
    private String studentName;
    private String hostName;
    private final Server server;
    private boolean isRunning;
    private DataOutputStream out;
    
    public ServerServant(Socket clientSocket, Server server, int id, String className) {
        this.socket = clientSocket;
        this.server = server;
        this.id = id;
        this.className = className;
        studentName = "Unknown student";
        hostName = "Unknown hostname";
        isRunning = true;
    }
    
    @Override
    public void run() {
        InputStream inp;
        BufferedReader brinp;
        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
            sendAction("classname", className);
        } catch (IOException e) {
            return;
        }
        String line;
        while (isRunning) {
            try {
                line = brinp.readLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    close();
                    return;
                } else {
                    switch (line) {
                        case "info":
                            hostName = brinp.readLine();
                            studentName = brinp.readLine();
                            break;
                        case "action":
                            server.action(id, hostName, studentName, brinp.readLine());
                            break;
                        default:
                            return;
                    }
                }
            } catch (IOException e) {
                close();
                return;
            }
        }
    }
    public synchronized void sendAction(String primary, String secondary) {
        try {
            out.writeBytes(primary + '\n');
            out.writeBytes(secondary + '\n');
            out.flush();
        } catch (IOException e) {
            // Warning: Action was not sent
        }
    }
    public synchronized void destroy() {
        isRunning = false;
        server.action(id, hostName, studentName, "destroy");
        try {
            socket.close();
        } catch (IOException e) {}
    }
    public synchronized void close() {
        isRunning = false;
        server.action(id, hostName, studentName, "quit");
        try {
            socket.close();
        } catch (IOException e) {}
    }
}
