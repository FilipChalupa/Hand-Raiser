package handraiser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

/**
 * Loads and saves user's settings. Doesn't modify the environment. The
 * environment should be modified by other classes which get the requested
 * state from the SettingsManager.
 * 
 * @author Filip
 * @version 1.1
 * 
 */
public class SettingsManager {
    
    /**
     * File object used to access the config.properties file stored in
     * application directory.
     */
    private final File configFile;
    
    /**
     * Window width in pixels.
     */
    private int windowWidth;
    
    /**
     * Window height in pixels.
     */
    private int windowHeight;
    
    /**
     * Window shift from left display border in pixels.
     */
    private int positionLeft;
    
    /**
     * Window shift from top display border in pixels.
     */
    private int positionTop;
    
    /**
     * Student name from student role.
     */
    private String studentName;
    
    /**
     * Teacher address from student role. Can be IP address or hostname.
     */
    private String teacherAddress;
    
    /**
     * Class name from teacher role.
     */
    private String className;
    
    /**
     * Makes the window to stay always on top.
     */
    private boolean stayOnTop;
    
    /**
     * Constructs a SettingsManager with data from the file config.properties
     * or sets a defaults. The data are then validated.
     */
    public SettingsManager() {
        this.configFile = new File("config.properties");
        String ontop = "no";
        try {
            FileReader reader = new FileReader(configFile);
            Properties props = new Properties();
            props.load(reader);

            windowWidth = Integer.parseInt(props.getProperty("width"));
            windowHeight = Integer.parseInt(props.getProperty("height"));
            
            positionTop = Integer.parseInt(props.getProperty("top"));
            positionLeft = Integer.parseInt(props.getProperty("left"));
            
            studentName = props.getProperty("student");
            teacherAddress = props.getProperty("address");
            
            className = props.getProperty("class");
            
            ontop = props.getProperty("ontop");

            reader.close();
        } catch (Exception e) {
            windowWidth = 0;
            windowHeight = 0;
            positionTop = -1;
            positionLeft = -1;
            studentName = null;
            teacherAddress = null;
            className = null;
        }
        if (windowWidth < 300) {
            windowWidth = 300;
        }
        if (windowHeight < 300) {
            windowHeight = 300;
        }
        if (studentName == null) {
            studentName = "Anonymous student";
        }
        if (teacherAddress == null) {
            teacherAddress = "localhost";
        }
        if (className == null) {
            className = "Unnamed class";
        }
        stayOnTop = !(ontop == null || ontop.equals("no"));
    }
    
    /**
     * 
     * @param w window width.
     * @param h window height.
     */
    public void setWindowSize(int w, int h) {
        windowWidth = w;
        windowHeight = h;
    }
    
    /**
     * 
     * @param left shift from left display border.
     * @param top shift from top display border.
     */
    public void setWindowPosition(int left, int top) {
        positionTop = top;
        positionLeft = left;
    }
    
    /**
     * 
     * @return window width from settings.
     */
    public int getWindowWidth() {
        return windowWidth;
    }
    
    /**
     * 
     * @return window height from settings.
     */
    public int getWindowHeight() {
        return windowHeight;
    }
    
    /**
     * 
     * @return shift from top display border from settings.
     */
    public int getPositionTop() {
        return positionTop;
    }
    
    /**
     * 
     * @return shift from left display border from settings.
     */
    public int getPositionLeft() {
        return positionLeft;
    }
    
    /**
     * 
     * @return last student name from settings.
     */
    public String getStudentName() {
        return studentName;
    }
    
    /**
     * 
     * @param name student name.
     */
    public void setStudentName(String name) {
        studentName = name;
    }
    
    /**
     * 
     * @return teacher name.
     */
    public String getTeacherAddress() {
        return teacherAddress;
    }
    
    /**
     * 
     * @param address last server address. Can be IP address or hostname.
     */
    public void setTeacherAddress(String address) {
        teacherAddress = address;
    }
    
    /**
     * 
     * @return last classname.
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * 
     * @param name class name.
     */
    public void setClassName(String name) {
        className = name;
    }
    
    /**
     * 
     * @return true if window should always stay on top.
     */
    public boolean getStayOnTop() {
        return stayOnTop;
    }
    
    /**
     * 
     * @param stayOnTop true if window should always stay on top.
     */
    public void setStayOnTop(boolean stayOnTop) {
        this.stayOnTop = stayOnTop;
    }
    
    /**
     * Saves the settings to the file config.properties. Fails silently.
     */
    public void save() {
        try {
            Properties props = new Properties();
            
            props.setProperty("width", Integer.toString(windowWidth));
            props.setProperty("height", Integer.toString(windowHeight));
            
            props.setProperty("top", Integer.toString(positionTop));
            props.setProperty("left", Integer.toString(positionLeft));
            
            props.setProperty("student", studentName);
            
            props.setProperty("address", teacherAddress);
            
            props.setProperty("class", className);
            
            props.setProperty("ontop", stayOnTop?"yes":"no");
            
            FileWriter writer = new FileWriter(configFile);
            props.store(writer, "Hand Raiser settings");
            writer.close();
        } catch (Exception e) {}
    }
    
}
