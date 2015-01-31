package handraiser;

/**
 *
 * @author Filip
 */
public abstract class Network implements Runnable {
    public static final int PORT = 10023;
    protected SettingsManager settings;
    protected boolean isRunning;
    
    public void setSettings(SettingsManager settings) {
        this.settings = settings;
        start();
    }
    
    public void start() {
        isRunning = true;
    }
    public void stop() {
        isRunning = false;
    }
    
}
