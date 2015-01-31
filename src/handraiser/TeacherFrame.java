package handraiser;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Filip
 */
public class TeacherFrame extends SessionFrame {
    private JTextField input;
    private final JLabel errorLabel = new JLabel("", JLabel.CENTER),
                         connectedStudents = new JLabel("", JLabel.CENTER);
    private final Server server = new Server();
    private final Thread serverThread = new Thread(server);
    private final TeacherFrame thisFrame = this;
    
    private final Map<Integer, JPanel> questions = new LinkedHashMap<>();
    private final Map<Integer, JPanel> solutions = new LinkedHashMap<>();
    private final JPanel studentBoxes = new JPanel();
    
    
    public TeacherFrame(JPanel parentPanel, SettingsManager settings, MainFrame windowFrame) {
        super(parentPanel, settings, windowFrame);
        setStageOne();
        errorLabel.setForeground(Color.RED);
        
        studentBoxes.setLayout(new BoxLayout(studentBoxes, BoxLayout.Y_AXIS));

    }
    private void setStageOne() {
        clearParentPanel();
        
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(new JLabel("Class name:", JLabel.CENTER), gbc);
        
        ActionListener actionListener;
        actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorLabel.setText("");
                String inputName = input.getText().trim().replaceAll("<", "").replaceAll(">", "");
                if (inputName.length() < 1) {
                    errorLabel.setText("You must enter a name!");
                    input.requestFocus();
                } else {
                    settings.setClassName(inputName);
                    windowFrame.changeWindowTitle(inputName);
                    
                    clearParentPanel();
                    
                    gbc.anchor = GridBagConstraints.NORTH;
                    gbc.ipady = 1;
                    gbc.gridwidth = 1;
                    gbc.weightx = 1.0;
                    gbc.weighty = 0.2;
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    parentPanel.add(new JLabel("<html><h1 style='margin: 0; text-decoration: underline;'><span style='font-weight: normal;'>Class: </span>"+inputName+"</h1></html>", JLabel.CENTER), gbc);
                    gbc.gridy = 1;
                    gbc.weighty = 0.1;
                    updateConnectedCounter(0);
                    parentPanel.add(connectedStudents, gbc);
                    
                    gbc.weighty = 2.2;
                    gbc.gridy = 2;
                    studentBoxes.removeAll();
                    parentPanel.add(studentBoxes, gbc);
                    
                    server.setSettings(settings);
                    server.setFrame(thisFrame);
                    server.start();
                    serverThread.start();
                    
                    updateParentPanel();
                }
            }
        };
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        input = new JTextField(settings.getClassName(), 15);
        input.setHorizontalAlignment(JTextField.CENTER);
        input.addActionListener(actionListener);
        parentPanel.add(input, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        parentPanel.add(errorLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton button = new JButton("Continue");
        button.addActionListener(actionListener);
        parentPanel.add(button, gbc);
        
        updateParentPanel();
        input.requestFocus();
    }
    private String studentBoxHTML(String hostName, String studentName, String title, String color) {
        String top, sub, bot;
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        
        top = "<h2 style='margin: 0;'><span style='color: "+color+"; font-weight: normal;'>"+title+":</span> "+studentName+"</h2>";
        sub = "<h3 style='margin: 0;'><span style='font-weight: normal;'>@</span>"+hostName+"</h3>";
        bot = "<h4 style='margin: 0; font-weight: normal;'>"+dateFormat.format(date)+"</h4>";
        
        return "<html><div style='text-align: center;'>"+top+sub+bot+"</div></html>";
    }
    private JButton getHideButton(int id, String type) {
        JButton button = new JButton("Hide");
        button.setActionCommand(id + "-" + type);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String parts[] = e.getActionCommand().split("-");
                int id = Integer.parseInt(parts[0]);
                String type = parts[1];
                if (type.equals("question")) {
                    cancellQuestion(id);
                } else {
                    cancellSolution(id);
                }
                server.sendAction(id, "cancell", type);
            }
        });
        
        return button;
    }
    public void addQuestion(int id, String hostName, String studentName) {
        JPanel studentBox = new JPanel();
        studentBox.add(new JLabel(studentBoxHTML(hostName, studentName, "Question", "blue")));
        studentBox.add(Box.createHorizontalGlue());
        studentBox.add(getHideButton(id, "question"));
        questions.put(id, studentBox);
        
        studentBoxes.add(studentBox);
        updateParentPanel();
    }
    public void addSolution(int id, String hostName, String studentName) {
        JPanel studentBox = new JPanel();
        studentBox.add(new JLabel(studentBoxHTML(hostName, studentName, "Solution", "green")));
        studentBox.add(Box.createHorizontalGlue());
        studentBox.add(getHideButton(id, "solution"));
        solutions.put(id, studentBox);
        
        studentBoxes.add(studentBox);
        updateParentPanel();
    }
    public void cancellQuestionAndSolution(int id) {
        cancellQuestion(id);
        cancellSolution(id);
    }
    public void cancellQuestion(int id) {
        JPanel studentBox = questions.get(id);
        if (studentBox != null) {
            studentBoxes.remove(studentBox);
            questions.remove(id);
            updateParentPanel();
        }
    }
    public void cancellSolution(int id) {
        JPanel studentBox = solutions.get(id);
        if (studentBox != null) {
            studentBoxes.remove(studentBox);
            solutions.remove(id);
            updateParentPanel();
        }
    }
    public void updateWaitingCounters() {
        int counter = 0;
        for (Integer id : solutions.keySet()) {
            server.sendAction(id, "wait-solution", Integer.toString(counter++));
        }
        counter = 0;
        for (Integer id : questions.keySet()) {
            server.sendAction(id, "wait-question", Integer.toString(counter++));
        }
    }
    public void updateConnectedCounter(int count) {
        connectedStudents.setText("Connected students: " + count);
    }
    
    @Override
    public void close() {
        server.close();
    }
}
