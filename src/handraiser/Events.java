package handraiser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.event.WindowEvent;

public class Events implements ActionListener {
    private final MainFrame parentFrame;
    
    public Events(JFrame parent) {
        parentFrame = (MainFrame)parent;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog dialog;
        switch (e.getActionCommand()) {
            case "Exit":
                parentFrame.dispatchEvent(new WindowEvent(parentFrame, WindowEvent.WINDOW_CLOSING));
                break;
            case "About":
                dialog = new JDialog(parentFrame,"About");
                dialog.setSize(300,300);
                dialog.setLocationRelativeTo(null);
                dialog.setVisible(true);
                
                dialog.add((new JPanel()).add(new JLabel(
                        "<html><div style='padding: 20px;'>"+
                          "<h1>Hand Raiser</h1>"+
                          "<p>Author: Filip Chalupa</p>"+
                          "<p>Version: 1.1</p>"+
                        "</div></html>"
                )));
                break;
            case "Student":
                parentFrame.showPanel("student");
                break;
            case "Teacher":
                parentFrame.showPanel("teacher");
                break;
            case "Leave session":
                if (parentFrame.sessionFrame != null) {
                    parentFrame.sessionFrame.close();
                }
                parentFrame.showPanel("roles");
                break;
            case "Stay on top":
                parentFrame.setOnTop();
                break;
        }
    }
}
