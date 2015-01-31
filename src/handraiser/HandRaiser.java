package handraiser;

import javax.swing.SwingUtilities;


public class HandRaiser {
    
    public HandRaiser() {
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame();
            }
        });
    }
}
