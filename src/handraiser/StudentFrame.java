package handraiser;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Filip
 */
public class StudentFrame extends SessionFrame {
    private JTextField input;
    private final Client client = new Client();
    private final Thread clientThread = new Thread(client);
    private final StudentFrame thisFrame = this;
    private JButton buttonSolution, buttonQuestion, buttonCancell;
    private final JLabel waitingSolution = new JLabel(" ", JLabel.CENTER),
                         waitingQuestion = new JLabel(" ", JLabel.CENTER);

    public StudentFrame(JPanel parentPanel, SettingsManager settings, MainFrame windowFrame) {
        super(parentPanel, settings, windowFrame);
        setStageOne();
    }
    private void setStageOne() {
        clearParentPanel();
        
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(new JLabel("Your name:", JLabel.CENTER), gbc);
        
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                error.setText("");
                String inputName = input.getText().trim().replaceAll("<", "").replaceAll(">", "");
                if (inputName.length() < 1) {
                    error.setText("You must enter a name!");
                    input.requestFocus();
                } else {
                    settings.setStudentName(inputName);
                    setStageTwo();
                }
            }
        };
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        input = new JTextField(settings.getStudentName(), 15);
        input.setHorizontalAlignment(JTextField.CENTER);
        input.addActionListener(actionListener);
        parentPanel.add(input, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        parentPanel.add(error, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton button = new JButton("Continue");
        button.addActionListener(actionListener);
        parentPanel.add(button, gbc);
        
        updateParentPanel();
        input.requestFocus();
    }
    private void setStageTwo() {
        clearParentPanel();
        
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(new JLabel("Teacher's network address:", JLabel.CENTER), gbc);
        
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                error.setText("");
                String inputAddress = input.getText().trim();
                if (inputAddress.length() < 1) {
                    error.setText("Something went wrong!");
                    input.requestFocus();
                } else {
                    settings.setTeacherAddress(inputAddress);
                    clearParentPanel();
                    parentPanel.add(new JLabel("Connecting", JLabel.CENTER), gbc);
                    client.setSettings(settings);
                    client.setFrame(thisFrame);
                    clientThread.start();
                    updateParentPanel();
                }
            }
        };
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        input = new JTextField(settings.getTeacherAddress(), 15);
        input.setHorizontalAlignment(JTextField.CENTER);
        input.addActionListener(actionListener);
        parentPanel.add(input, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        parentPanel.add(error, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        JButton button = new JButton("Connect");
        button.addActionListener(actionListener);
        parentPanel.add(button, gbc);
        
        updateParentPanel();
        input.requestFocus();
    }
    public void connectionCreated() {
        clearParentPanel();
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(new JLabel("Choose a message for your teacher:", JLabel.CENTER), gbc);
        
        gbc.gridwidth = 1;
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        parentPanel.add(waitingSolution, gbc);
        
        gbc.weighty = 2;
        gbc.gridy = 2;
        buttonSolution = new JButton("I have a solution!");
        buttonSolution.setForeground(new Color(0, 130, 0));
        buttonSolution.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonSolution.setEnabled(false);
                buttonCancell.setEnabled(true);
                client.sendAction("solution");
            }
        });
        parentPanel.add(buttonSolution, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weighty = 0.1;
        parentPanel.add(waitingQuestion, gbc);
        
        gbc.weighty = 2;
        gbc.gridy = 2;
        buttonQuestion = new JButton("I have a question!");
        buttonQuestion.setForeground(new Color(0, 0, 200));
        buttonQuestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonQuestion.setEnabled(false);
                buttonCancell.setEnabled(true);
                client.sendAction("question");
            }
        });
        parentPanel.add(buttonQuestion, gbc);
        
        gbc.gridwidth = 2;
        gbc.weighty = 0.1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        buttonCancell = new JButton("Take back your actions.");
        buttonCancell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancellQuestion();
                cancellSolution();
                client.sendAction("cancell");
            }
        });
        buttonCancell.setEnabled(false);
        parentPanel.add(buttonCancell, gbc);
        
        
        
        updateParentPanel();
    }
    public void setClassName(String title) {
        windowFrame.changeWindowTitle(title);
    }
    public void cancellQuestion() {
        buttonQuestion.setEnabled(true);
        waitingQuestion.setText(" ");
        if (buttonSolution.isEnabled()) {
            buttonCancell.setEnabled(false);
        }
    }
    public void cancellSolution() {
        buttonSolution.setEnabled(true);
        waitingSolution.setText(" ");
        if (buttonQuestion.isEnabled()) {
            buttonCancell.setEnabled(false);
        }
    }
    public void setWaitingSolutionText(int countBefore) {
        waitingSolution.setText("Students before you: "+Integer.toString(countBefore));
    }
    public void setWaitingQuestionText(int countBefore) {
        waitingQuestion.setText("Students before you: "+Integer.toString(countBefore));
    }
    
    @Override
    public void close() {
        client.close();
    }
}
