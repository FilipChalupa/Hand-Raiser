package handraiser;

import java.awt.Color;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Filip
 */
public abstract class SessionFrame {
    protected JPanel parentPanel;
    protected SettingsManager settings;
    protected final GridBagConstraints gbc = new GridBagConstraints();
    protected final JLabel error = new JLabel("", JLabel.CENTER);
    protected final MainFrame windowFrame;
    
    protected SessionFrame(JPanel parentPanel, SettingsManager settings, MainFrame windowFrame) {
        this.parentPanel = parentPanel;
        this.settings = settings;
        this.windowFrame = windowFrame;
        error.setForeground(Color.RED);
    }
    protected void clearParentPanel() {
        parentPanel.removeAll();
    }
    protected void updateParentPanel() {
        parentPanel.updateUI();
    }
    public abstract void close();
    public void connectionFailed(String errorDescription) {
        clearParentPanel();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.ipady = 10;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parentPanel.add(error, gbc);
        error.setText(errorDescription);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        parentPanel.add(new JLabel("Use top menu to leave the session.", JLabel.CENTER), gbc);
        updateParentPanel();
    }
}
