package handraiser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class MainFrame extends JFrame {
    private final Events events = new Events(this);
    private final JMenuItem leaveSessionMenuItem = new JMenuItem("Leave session");
    public SessionFrame sessionFrame = null;
    private final SettingsManager settings = new SettingsManager();
    public final JCheckBoxMenuItem onTopMenuItem = new JCheckBoxMenuItem("Stay on top");
    
    private final JPanel wrapperPanel = new JPanel();
    
    private final String windowTitle;
    
    public MainFrame() {
        super();
        windowTitle = "Hand Raiser";
        changeWindowTitle(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(this.settings.getWindowWidth(), this.settings.getWindowHeight());
        
        addMenuBar();
        
        if (settings.getPositionTop() < 0 || settings.getPositionLeft() < 0) {
            setLocationRelativeTo(null);
        } else {
            setLocation(settings.getPositionLeft(), settings.getPositionTop());
        }
        onTopMenuItem.setState(settings.getStayOnTop());
        this.setAlwaysOnTop(settings.getStayOnTop());
        
        this.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent e){
                    settings.setWindowSize((int)getSize().getWidth(),
                                           (int)getSize().getHeight());
                    settings.setWindowPosition((int)getLocationOnScreen().getX(),
                                               (int)getLocationOnScreen().getY());
                    settings.save();
                }
            });
        
        setVisible(true);
        
        wrapperPanel.setLayout(new GridBagLayout());
        this.add(wrapperPanel);
        
        leaveSessionMenuItem.addActionListener(events);
        onTopMenuItem.addActionListener(events);
        
        showPanel("roles");
        
    }
    public void showPanel(String name) {
        JButton button;
        GridBagConstraints gbc = new GridBagConstraints();
        leaveSessionMenuItem.setEnabled(!name.equals("roles"));
        if (sessionFrame != null) {
            sessionFrame.close();
        }
        wrapperPanel.removeAll();
        wrapperPanel.updateUI();
        switch (name) {
            case "roles":
                changeWindowTitle(null);
                sessionFrame = null;
                
                gbc.gridwidth = 2;
                gbc.ipady = 10;
                gbc.anchor = GridBagConstraints.SOUTH;
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.weightx = 1.0;
                gbc.weighty = 10.0;
                
                wrapperPanel.add(new JLabel("Choose your role:", JLabel.CENTER), gbc);
                
                button = new JButton("Student");
                button.addActionListener(events);
                
                gbc.ipady = 1;
                gbc.gridwidth = 1;
                gbc.weighty = 1.0;
                
                gbc.gridx = 0;
                gbc.gridy = 1;
                
                wrapperPanel.add(button, gbc);
                
                button = new JButton("Teacher");
                button.addActionListener(events);
                
                gbc.gridx = 1;
                gbc.gridy = 1;
                
                wrapperPanel.add(button, gbc);
                break;
            case "student":
                sessionFrame = new StudentFrame(wrapperPanel, settings, this);
                break;
            case "teacher":
                sessionFrame = new TeacherFrame(wrapperPanel, settings, this);
                break;
        }
    }
    private JMenuBar addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        
        exitMenuItem.addActionListener(events);
        aboutMenuItem.addActionListener(events);
        
        fileMenu.add(onTopMenuItem);
        fileMenu.add(leaveSessionMenuItem);
        fileMenu.add(exitMenuItem);
        helpMenu.add(aboutMenuItem);
        
        return menuBar;
    }
    public void setOnTop() {
        boolean state = onTopMenuItem.getState();
        settings.setStayOnTop(state);
        this.setAlwaysOnTop(state);
    }
    public void changeWindowTitle(String title) {
        if (title == null) {
            this.setTitle(windowTitle);
        } else {
            this.setTitle(title);
        }
    }
}
