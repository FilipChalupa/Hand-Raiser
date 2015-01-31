package handraiser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Filip
 */
public class Client extends Network {
    private StudentFrame frame;
    private Socket clientSocket;
    private String machineName;
    DataOutputStream outToServer;
    BufferedReader inFromServer;

    @Override
    public void run() {
        clientSocket = null;
        try {
            machineName = java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            machineName = "Unnamed";
        }
        try {
            clientSocket = new Socket(settings.getTeacherAddress(), PORT);
            outToServer = new DataOutputStream(clientSocket.getOutputStream());
            frame.connectionCreated();
            
            sendInfoAboutUser();
            
            inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
        } catch (Exception e) {
            frame.connectionFailed("Sorry, class was not found.");
        }
        listener();
        close();
    }
    private String getLine() throws IOException {
        return inFromServer.readLine();
    }
    private void handleAction(String action) throws IOException {
        switch (action) {
            case "classname":
                frame.setClassName(settings.getStudentName() + "@" + getLine());
                break;
            case "cancell":
                if (getLine().equals("question")) {
                    frame.cancellQuestion();
                } else {
                    frame.cancellSolution();
                }
                break;
            case "wait-question":
                frame.setWaitingQuestionText(Integer.parseInt(getLine()));
                break;
            case "wait-solution":
                frame.setWaitingSolutionText(Integer.parseInt(getLine()));
                break;
            default:
                break;
        }
    }
    private void listener() {
        String line;
        try {
            while (isRunning) {
                line = getLine();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    close();
                    frame.connectionFailed("The class was ended.");
                } else {
                    handleAction(line);
                }
            }
        } catch (Exception e) {
            if (isRunning) {
                frame.connectionFailed("Connection to the class was lost.");
            }
        }
    }
    public synchronized void sendInfoAboutUser() {
        try {
            outToServer.writeBytes("info\n");
            outToServer.writeBytes(machineName + '\n');
            outToServer.writeBytes(settings.getStudentName() + '\n');
            outToServer.flush();
        } catch (Exception e) {
            // Warning: Info about user was not sent
        }
    }
    public synchronized void sendAction(String actionName) {
        try {
            outToServer.writeBytes("action\n");
            outToServer.writeBytes(actionName + '\n');
            outToServer.flush();
        } catch (Exception e) {
            frame.connectionFailed("Sorry, something went wrong.");
        }
    }

    public void setFrame(StudentFrame frame) {
        this.frame = frame;
    }
    public synchronized void close() {
        isRunning = false;
        try {
            clientSocket.close();
        } catch (Exception e) {}
    }
    
}
