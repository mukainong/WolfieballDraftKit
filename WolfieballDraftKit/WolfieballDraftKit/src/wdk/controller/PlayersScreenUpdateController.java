package wdk.controller;

import wdk.error.ErrorHandler;
import wdk.gui.WDK_GUI;

/**
 * This controller class handles the responses to all course
 * editing input, including verification of data and binding of
 * entered data to the Course object.
 * 
 * @author Richard McKenna
 */
public class PlayersScreenUpdateController {
    // WE USE THIS TO MAKE SURE OUR PROGRAMMED UPDATES OF UI
    // VALUES DON'T THEMSELVES TRIGGER EVENTS
    private boolean enabled;

    /**
     * Constructor that gets this controller ready, not much to
     * initialize as the methods for this function are sent all
     * the objects they need as arguments.
     */
    public PlayersScreenUpdateController() {
        enabled = true;
    }

    /**
     * This mutator method lets us enable or disable this controller.
     * 
     * @param enableSetting If false, this controller will not respond to
     * Course editing. If true, it will.
     */
    public void enable(boolean enableSetting) {
        enabled = enableSetting;
    }

    /**
     * This controller function is called in response to the user changing
     * course details in the UI. It responds by updating the bound Course
     * object using all the UI values, including the verification of that
     * data.
     * 
     * @param gui The user interface that requested the change.
     */
    public void handleCourseChangeRequest(WDK_GUI gui) {
        if (enabled) {
            try {
                // UPDATE THE COURSE, VERIFYING INPUT VALUES
                //gui.updateCourseInfo(gui.getDataManager().getCourse());
                gui.updatePlayersScreen(gui.getDataManager().getDraft());
                
                // THE COURSE IS NOW DIRTY, MEANING IT'S BEEN 
                // CHANGED SINCE IT WAS LAST SAVED, SO MAKE SURE
                // THE SAVE BUTTON IS ENABLED
                gui.getFileController().markAsEdited(gui);
            } catch (Exception e) {
                // SOMETHING WENT WRONG
                ErrorHandler eH = ErrorHandler.getErrorHandler();
                eH.handleUpdateCourseError();
            }
        }
    }    
}