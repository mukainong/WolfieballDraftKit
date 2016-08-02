package wdk;

import static wdk.WDK_StartupConstants.*;
import static wdk.WDK_PropertyType.*;
import wdk.data.DraftDataManager;
import wdk.data.Instructor;
import wdk.error.ErrorHandler;
import wdk.file.JsonWolfieballFileManager;
import wdk.file.CourseSiteExporter;
import wdk.gui.WDK_GUI;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import javafx.application.Application;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import xml_utilities.InvalidXMLFileFormatException;

/**
 * CourseSiteBuilder is a JavaFX application that can be used to build the
 * pages for a course Web site. The CSE 219 course's schedule page is
 * one such page: http://www.cs.stonybrook.edu/~cse219/Section02/schedule.html
 * 
 * @author Richard McKenna
 */
public class WolfieballDraftKit extends Application {
    // THIS IS THE FULL USER INTERFACE, WHICH WILL BE INITIALIZED
    // AFTER THE PROPERTIES FILE IS LOADED
   WDK_GUI gui;

    /**
     * This is where our Application begins its initialization, it will
     * create the GUI and initialize all of its components.
     * 
     * @param primaryStage This application's window.
     */
    @Override
    public void start(Stage primaryStage) {
        // LET'S START BY GIVING THE PRIMARY STAGE TO OUR ERROR HANDLER
        ErrorHandler eH = ErrorHandler.getErrorHandler();
        eH.initMessageDialog(primaryStage);
        
        // LOAD APP SETTINGS INTO THE GUI AND START IT UP
        boolean success = loadProperties();
        if (success) {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            String appTitle = props.getProperty(PROP_APP_TITLE);
            try {                
                // WE WILL SAVE OUR COURSE DATA USING THE JSON FILE
                // FORMAT SO WE'LL LET THIS OBJECT DO THIS FOR US
                JsonWolfieballFileManager jsonFileManager = new JsonWolfieballFileManager();
                
                // AND THIS ONE WILL DO THE COURSE WEB PAGE EXPORTING
                CourseSiteExporter exporter = new CourseSiteExporter(PATH_BASE, PATH_SITES);
                
                Instructor lastInstructor = jsonFileManager.loadLastInstructor(JSON_FILE_PATH_LAST_INSTRUCTOR);
                ArrayList<String> subjects = jsonFileManager.loadSubjects(JSON_FILE_PATH_SUBJECTS);
                                
                // AND NOW GIVE ALL OF THIS STUFF TO THE GUI
                // INITIALIZE THE USER INTERFACE COMPONENTS
                gui = new WDK_GUI(primaryStage);
                gui.setWolfieballFileManager(jsonFileManager);
                gui.setSiteExporter(exporter);
                
                // CONSTRUCT THE DATA MANAGER AND GIVE IT TO THE GUI
                DraftDataManager dataManager = new DraftDataManager(gui, lastInstructor); 
                gui.setDataManager(dataManager);

                // FINALLY, START UP THE USER INTERFACE WINDOW AFTER ALL
                // REMAINING INITIALIZATION
                gui.initGUI(appTitle, subjects);                
            }
            catch(IOException ioe) {
                eH = ErrorHandler.getErrorHandler();
                eH.handlePropertiesFileError();
            }
        }
    }
    
    /**
     * Loads this application's properties file, which has a number of settings
     * for initializing the user interface.
     * 
     * @return true if the properties file was loaded successfully, false otherwise.
     */
    public boolean loadProperties() {
        try {
            // LOAD THE SETTINGS FOR STARTING THE APP
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(PropertiesManager.DATA_PATH_PROPERTY, PATH_DATA);
            props.loadProperties(PROPERTIES_FILE_NAME, PROPERTIES_SCHEMA_FILE_NAME);
            return true;
       } catch (InvalidXMLFileFormatException ixmlffe) {
            // SOMETHING WENT WRONG INITIALIZING THE XML FILE
            ErrorHandler eH = ErrorHandler.getErrorHandler();
            eH.handlePropertiesFileError();
            return false;
        }        
    }

    /**
     * This is where program execution begins. Since this is a JavaFX app
     * it will simply call launch, which gets JavaFX rolling, resulting in
     * sending the properly initialized Stage (i.e. window) to our start
     * method in this class.
     */
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        launch(args);
    }
}
