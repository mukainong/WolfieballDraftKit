package wdk.controller;

import static wdk.WDK_PropertyType.COURSE_SAVED_MESSAGE;
import static wdk.WDK_PropertyType.NEW_COURSE_CREATED_MESSAGE;
import static wdk.WDK_PropertyType.SAVE_UNSAVED_WORK_MESSAGE;
import static wdk.WDK_StartupConstants.JSON_FILE_PATH_LAST_INSTRUCTOR;
import static wdk.WDK_StartupConstants.PATH_COURSES;
import wdk.data.Course;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.CoursePage;
import wdk.data.Instructor;
import wdk.error.ErrorHandler;
import wdk.file.WolfieballFileManager;
import wdk.file.CourseSiteExporter;
import wdk.gui.WDK_GUI;
import wdk.gui.MessageDialog;
import wdk.gui.WebBrowser;
import wdk.gui.YesNoCancelDialog;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import properties_manager.PropertiesManager;

import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static wdk.WDK_StartupConstants.PATH_DRAFTS;
import wdk.data.Player;
import wdk.data.Team;

/**
 * This controller class provides responses to interactions with the buttons in
 * the file toolbar.
 *
 * @author Richard McKenna
 */
public class FileController {

    // WE WANT TO KEEP TRACK OF WHEN SOMETHING HAS NOT BEEN SAVED
    private boolean saved;

    // THIS GUY KNOWS HOW TO READ AND WRITE COURSE DATA
    private WolfieballFileManager courseIO;

    // THIS GUY KNOWS HOW TO EXPORT COURSE SCHEDULE PAGES
    private CourseSiteExporter exporter;

    // THIS WILL PROVIDE FEEDBACK TO THE USER WHEN SOMETHING GOES WRONG
    ErrorHandler errorHandler;
    
    // THIS WILL PROVIDE FEEDBACK TO THE USER AFTER
    // WORK BY THIS CLASS HAS COMPLETED
    MessageDialog messageDialog;
    
    // AND WE'LL USE THIS TO ASK YES/NO/CANCEL QUESTIONS
    YesNoCancelDialog yesNoCancelDialog;
    
    // WE'LL USE THIS TO GET OUR VERIFICATION FEEDBACK
    PropertiesManager properties;
    
    
    int counter1;
    int counter;
    boolean flag = false;
    boolean pauseFlag = false;
    boolean selectPlayerFlag = false;
    
    /**
     * This default constructor starts the program without a course file being
     * edited.
     *
     * @param primaryStage The primary window for this application, which we
     * need to set as the owner for our dialogs.
     * @param initCourseIO The object that will be reading and writing course
     * data.
     * @param initExporter The object that will be exporting courses to Web
     * sites.
     */
    public FileController(
            MessageDialog initMessageDialog,
            YesNoCancelDialog initYesNoCancelDialog,
            WolfieballFileManager initCourseIO,
            CourseSiteExporter initExporter) {
        // NOTHING YET
        saved = true;
        
        // KEEP THESE GUYS FOR LATER
        courseIO = initCourseIO;
        exporter = initExporter;
        
        // BE READY FOR ERRORS
        errorHandler = ErrorHandler.getErrorHandler();
        
        // AND GET READY TO PROVIDE FEEDBACK
        messageDialog = initMessageDialog;
        yesNoCancelDialog = initYesNoCancelDialog;
        properties = PropertiesManager.getPropertiesManager();
    }
    
    /**
     * This method marks the appropriate variable such that we know
     * that the current Course has been edited since it's been saved.
     * The UI is then updated to reflect this.
     * 
     * @param gui The user interface editing the Course.
     */
    public void markAsEdited(WDK_GUI gui) {
        // THE Course OBJECT IS NOW DIRTY
        saved = false;
        
        // LET THE UI KNOW
        gui.updateToolbarControls(saved);
    }

    /**
     * This method starts the process of editing a new Course. If a course is
     * already being edited, it will prompt the user to save it first.
     * 
     * @param gui The user interface editing the Course.
     */
    public void handleNewCourseRequest(WDK_GUI gui) {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToMakeNew = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToMakeNew = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO MAKE A NEW COURSE
            if (continueToMakeNew) {
                // RESET THE DATA, WHICH SHOULD TRIGGER A RESET OF THE UI
                DraftDataManager dataManager = gui.getDataManager();
                dataManager.reset();
                saved = false;

                // REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
                // THE APPROPRIATE CONTROLS
                gui.updateToolbarControls(saved);

                Draft draftToStart = gui.getDataManager().getDraft();
                courseIO.loadHitterPlayers(draftToStart, "./data/Hitters.json");
                courseIO.loadPitcherPlayers(draftToStart, "./data/Pitchers.json");
                
                //gui.getDataManager().getDraft().calculateEstimatedValue();
                
                gui.getSelectMLBComboBox().getItems().clear();
                gui.getSelectMLBComboBox().setValue("");
                // LOAD SESSIONS VALUES
                ObservableList<String> teamName = FXCollections.observableArrayList();
                for (int i = 0; i < gui.getDataManager().getDraft().getInitialPlayers().size(); i++) {
                    if(!teamName.contains(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString())){
                        teamName.add(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString());
                    }
                }
                gui.getSelectMLBComboBox().setItems(teamName);
                
                // TELL THE USER THE COURSE HAS BEEN CREATED
                messageDialog.show(properties.getProperty(NEW_COURSE_CREATED_MESSAGE));
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG, PROVIDE FEEDBACK
            errorHandler.handleNewCourseError();
        }
    }

    /**
     * This method lets the user open a Course saved to a file. It will also
     * make sure data for the current Course is not lost.
     * 
     * @param gui The user interface editing the course.
     */
    public void handleLoadCourseRequest(WDK_GUI gui) {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToOpen = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE WITH A CANCEL
                continueToOpen = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO OPEN A Course
            if (continueToOpen) {
                // GO AHEAD AND PROCEED LOADING A Course
                promptToOpen(gui);
                
                DecimalFormat df1 = new DecimalFormat("##0.000");
                DecimalFormat df2 = new DecimalFormat("##0.00");
                
                int hitterCounter = 0;
                int pitcherCounter = 0;
                int salarySum = 0;
                int RSum = 0 ;
                int HRSum = 0 ;
                int RBISum = 0 ;
                int SBSum = 0 ;
                double BASum = 0.0 ;
                int WSum = 0 ;
                int SVSum = 0 ;
                int KSum = 0 ;
                double ERASum = 0.0 ;
                double WHIPSum = 0.0 ;                
                
                for(int i = 0; i < gui.getDataManager().getDraft().getDraftedTeams().size(); i++){ // update tandings table
                    for(int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().size(); j++){
                        salarySum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getSALARY();
                        if(gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getTYPE().equals("Hitter")){
                            RSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getR();
                            HRSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getHR();
                            RBISum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getRBI();
                            SBSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getSB();
                            BASum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getBA();
                            hitterCounter++;
                        }
                        else if(gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getTYPE().equals("Pitcher")){
                            WSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getW();
                            SVSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getSV();
                            KSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getK();
                            ERASum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getERA();
                            WHIPSum += gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(j).getWHIP();
                            pitcherCounter++;
                        }
                    }
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setMoneyLeft(260 - salarySum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setPlayersNeeded(23 - hitterCounter - pitcherCounter);
                    //gui.getDataManager().getDraft().getDraftedTeams().get(i).setMoneyPP(gui.getDataManager().getDraft().getDraftedTeams().get(i).getMoneyLeft()/gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                    if(gui.getDataManager().getDraft().getDraftedTeams().get(i).getPlayersNeeded() !=0){
                        gui.getDataManager().getDraft().getDraftedTeams().get(i).setMoneyPP(gui.getDataManager().getDraft().getDraftedTeams().get(i).getMoneyLeft()/gui.getDataManager().getDraft().getDraftedTeams().get(i).getPlayersNeeded());
                    }
                    if(gui.getDataManager().getDraft().getDraftedTeams().get(i).getPlayersNeeded() ==0){
                        gui.getDataManager().getDraft().getDraftedTeams().get(i).setMoneyPP(-1);
                    }
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setR(RSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setHR(HRSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setRBI(RBISum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setSB(SBSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setBA((float)(BASum/hitterCounter));
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setBA_DISPLAY(df1.format((float)BASum/hitterCounter));
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setW(WSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setSV(SVSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setK(KSum);
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setERA(((float)ERASum/pitcherCounter));
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setERA_DISPLAY(df2.format((float)ERASum/pitcherCounter));
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setWHIP(((float)WHIPSum/pitcherCounter));
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).setWHIP_DISPLAY(df2.format((float)WHIPSum/pitcherCounter));
                    
                hitterCounter = 0;
                pitcherCounter = 0;
                salarySum = 0;
                RSum = 0 ;
                HRSum = 0 ;
                RBISum = 0 ;
                SBSum = 0 ;
                BASum = 0.0 ;
                WSum = 0 ;
                SVSum = 0 ;
                KSum = 0 ;
                ERASum = 0.0 ;
                WHIPSum = 0.0 ;
                }

                for(int g = 0; g < gui.getDataManager().getDraft().getDraftedTeams().size();g++){ // update total points and estimated values
                    gui.getDataManager().getDraft().getDraftedTeams().get(g).setTotalPoints(0);
                }
                gui.getDataManager().getDraft().fetchTotalPoints();
                gui.getDataManager().getDraft().calculateEstimatedValue();
                
                gui.getSelectMLBComboBox().getItems().clear();
                gui.getSelectMLBComboBox().setValue("");
                // LOAD SESSIONS VALUES
                ObservableList<String> teamName = FXCollections.observableArrayList();
                for (int i = 0; i < gui.getDataManager().getDraft().getInitialPlayers().size(); i++) {
                    if(!teamName.contains(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString())){
                        teamName.add(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString());
                    }
                }
                gui.getSelectMLBComboBox().setItems(teamName);
            }
        } catch (IOException ioe) {
            // SOMETHING WENT WRONG
            errorHandler.handleLoadCourseError();
        }
    }

    /**
     * This method will save the current course to a file. Note that we already
     * know the name of the file, so we won't need to prompt the user.
     * 
     * @param gui The user interface editing the Course.
     * 
     * @param courseToSave The course being edited that is to be saved to a file.
     */
    public void handleSaveCourseRequest(WDK_GUI gui, Draft draftToSave) {
        try {
            // SAVE IT TO A FILE
            courseIO.saveDraft(draftToSave);

            // MARK IT AS SAVED
            saved = true;

            // TELL THE USER THE FILE HAS BEEN SAVED
            messageDialog.show(properties.getProperty(COURSE_SAVED_MESSAGE));

            // AND REFRESH THE GUI, WHICH WILL ENABLE AND DISABLE
            // THE APPROPRIATE CONTROLS
            gui.updateToolbarControls(saved);
        } catch (IOException ioe) {
            errorHandler.handleSaveCourseError();
        }
    }

    public void handleSelectPlayerRequest(WDK_GUI gui) {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        //System.err.print("*-*");
        outerloop:
        for(int i = 0; i < draft.getDraftedTeams().size(); i++){
            //System.err.print("*-*");
            for(int j = 0; j < draft.getInitialPlayers().size(); j++){
                //System.err.print("*-*");
                if(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()==23){
                    break;
                }
                //System.err.print(draft.getInitialPlayers().get(j).getFANTASYPOSITION()+" ");
                if(draft.getInitialPlayers().get(j).getPosition().contains("C") && draft.getDraftedTeams().get(i).getCList().size()<2){
                    System.err.print("*-*");
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("C");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getCList().add(draft.getInitialPlayers().get(j));
                    
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(draft.getInitialPlayers().get(j).getPosition().contains("1B") && draft.getDraftedTeams().get(i).get1BList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("1B");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).get1BList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if((draft.getInitialPlayers().get(j).getPosition().contains("1B") || draft.getInitialPlayers().get(j).getPosition().contains("3B"))&& draft.getDraftedTeams().get(i).getCIList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("CI");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getCIList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(draft.getInitialPlayers().get(j).getPosition().contains("3B") && draft.getDraftedTeams().get(i).get3BList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("3B");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).get3BList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(draft.getInitialPlayers().get(j).getPosition().contains("2B") && draft.getDraftedTeams().get(i).get2BList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("2B");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).get2BList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if((draft.getInitialPlayers().get(j).getPosition().contains("2B") || draft.getInitialPlayers().get(j).getPosition().contains("SS")) && draft.getDraftedTeams().get(i).getMIList().size()<1){
                    
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("MI");
                    draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getMIList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    System.err.println(draft.getDraftedTeams().get(i).getCList().size());
                    System.err.println(draft.getDraftedTeams().get(i).get1BList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getCIList().size());
                    System.err.println(draft.getDraftedTeams().get(i).get3BList().size());
                    System.err.println(draft.getDraftedTeams().get(i).get2BList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getMIList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getSSList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getUList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getOFList().size());
                    System.err.println(draft.getDraftedTeams().get(i).getPList().size());
                    //System.err.println(draft.getDraftedTeams().get(i));
      
                    //System.err.println(draft.getInitialPlayers().get(j).getFIRST_NAME());
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    //draft.getDraftedTeams().get(i).addPlayer(draft.getInitialPlayers().get(j));
                    //System.err.println(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().get(0));
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(draft.getInitialPlayers().get(j).getPosition().contains("SS") && draft.getDraftedTeams().get(i).getSSList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("SS");
                     draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getSSList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(!draft.getInitialPlayers().get(j).getPosition().contains("P") && draft.getDraftedTeams().get(i).getUList().size()<1){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("U");
                     draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getUList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                if(draft.getInitialPlayers().get(j).getPosition().contains("OF") && draft.getDraftedTeams().get(i).getOFList().size()<5){
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("OF");
                     draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getOFList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
                //System.out.println("we try to add P");
                if(draft.getInitialPlayers().get(j).getPosition().contains("P") && draft.getDraftedTeams().get(i).getPList().size()<9){
                    //System.out.println("we try to add P");
                    draft.getInitialPlayers().get(j).setFANTASYPOSITION("P");
                     draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                    draft.getInitialPlayers().get(j).setCONTRACT("S2");
                    draft.getInitialPlayers().get(j).setSALARY(1);
                    draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                    draft.getDraftedTeams().get(i).getPList().add(draft.getInitialPlayers().get(j));
//                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
//                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();
                    
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get1BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getCIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get3BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).get2BList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getMIList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getSSList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getUList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getOFList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(k));
                    }
                    for(int k = 0; k < draft.getDraftedTeams().get(i).getPList().size();k++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(k));
                    }  
                    updateFantasyStandings(draft.getDraftedTeams().get(i), draft.getInitialPlayers().get(j));
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break outerloop;
                }
            }
        }
        
        //handleDraftingTaxiSquad(draft);
        
        counter = gui.getDataManager().getDraft().getDraftedTeams().size()*23;
        for(int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++){
            counter = counter - gui.getDataManager().getDraft().getDraftedTeams().get(j).getStartingLineUpPlayers().size();
        }
        
        if(counter == 0 ){
            flag = true;
        }
        
        outerloop1:
        if(flag == true){
        for(int i = 0; i < draft.getDraftedTeams().size(); i++){
            for(int j = 0; j < draft.getInitialPlayers().size(); j++){
                if(draft.getDraftedTeams().get(i).getTaxiSquadPlayers().size()==8){
                    flag  = false;
                    break;
                }
                
                draft.getInitialPlayers().get(j).setFANTASYPOSITION(draft.getInitialPlayers().get(j).getPosition());
                draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
                draft.getInitialPlayers().get(j).setCONTRACT("X");
                draft.getInitialPlayers().get(j).setSALARY(1);
                draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
                draft.getDraftedTeams().get(i).getTaxiSquadPlayers().add(draft.getInitialPlayers().get(j));
                draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
                break outerloop1;
            }
        }
        }
    }
    
    public void updateFantasyStandings(Team teamToEdit, Player playerToEdit){
        DecimalFormat df1 = new DecimalFormat("##0.000");
        DecimalFormat df2 = new DecimalFormat("##0.00");
            if(teamToEdit.getStartingLineUpPlayers().size()<=23){
                teamToEdit.setPlayersNeeded(23-teamToEdit.getStartingLineUpPlayers().size());
                teamToEdit.setMoneyLeft(teamToEdit.getMoneyLeft() - playerToEdit.getSALARY());
                if(teamToEdit.getPlayersNeeded() !=0){
                    teamToEdit.setMoneyPP(teamToEdit.getMoneyLeft()/teamToEdit.getPlayersNeeded());
                }
                if(teamToEdit.getPlayersNeeded() ==0){
                    teamToEdit.setMoneyPP(-1);
                }
                    
                if(playerToEdit.getTYPE().equals("Hitter")){
                    teamToEdit.setR(teamToEdit.getR()+playerToEdit.getR());
                    teamToEdit.setHR(teamToEdit.getHR()+playerToEdit.getHR());
                    teamToEdit.setRBI(teamToEdit.getRBI()+playerToEdit.getRBI());
                    teamToEdit.setSB(teamToEdit.getSB()+playerToEdit.getSB());
                    //teamToEdit.setBA(Double.valueOf(df1.format((float)(teamToEdit.getBA()*((teamToEdit.getStartingLineUpPlayers().size()-teamToEdit.getPList().size())-1)+playerToEdit.getBA())/(teamToEdit.getStartingLineUpPlayers().size()-teamToEdit.getPList().size()))));
                    //teamToEdit.setBA_DISPLAY(df1.format((float)(teamToEdit.getBA()*((teamToEdit.getStartingLineUpPlayers().size()-teamToEdit.getPList().size())-1)+playerToEdit.getBA())/(teamToEdit.getStartingLineUpPlayers().size()-teamToEdit.getPList().size())));
                    calculateBA(teamToEdit);
                }
                else if(playerToEdit.getTYPE().equals("Pitcher")){
                    teamToEdit.setW(teamToEdit.getW()+playerToEdit.getW());
                    teamToEdit.setSV(teamToEdit.getSV()+playerToEdit.getSV());
                    teamToEdit.setK(teamToEdit.getK()+playerToEdit.getK());
                    //teamToEdit.setERA(Double.valueOf(df2.format((float)(teamToEdit.getERA()*(teamToEdit.getPList().size()-1)+playerToEdit.getERA())/teamToEdit.getPList().size())));
                    //teamToEdit.setWHIP(Double.valueOf(df2.format((float)(teamToEdit.getWHIP()*(teamToEdit.getPList().size()-1)+playerToEdit.getWHIP())/teamToEdit.getPList().size())));
                    //teamToEdit.setERA_DISPLAY(df2.format((float)(teamToEdit.getERA()*(teamToEdit.getPList().size()-1)+playerToEdit.getERA())/teamToEdit.getPList().size()));
                    //teamToEdit.setWHIP_DISPLAY(df2.format((float)(teamToEdit.getWHIP()*(teamToEdit.getPList().size()-1)+playerToEdit.getWHIP())/teamToEdit.getPList().size()));
                    calculateERA(teamToEdit);
                    calculateWHIP(teamToEdit);
                }    
            }
    }
//    public void handleDraftingTaxiSquad(Draft draft){
//        for(int i = 0; i < draft.getDraftedTeams().size(); i++){
//            for(int j = 0; j < draft.getInitialPlayers().size(); j++){
//                if(draft.getDraftedTeams().get(i).getTaxiSquadPlayers().size()==8){
//                    break;
//                }
//                
//                draft.getInitialPlayers().get(j).setFANTASYPOSITION(draft.getInitialPlayers().get(j).getPosition());
//                draft.getInitialPlayers().get(j).setFANTASYTEAM(draft.getDraftedTeams().get(i).getName());
//                draft.getInitialPlayers().get(j).setCONTRACT("X");
//                draft.getInitialPlayers().get(j).setSALARY(1);
//                draft.getDraftSummaryPlayers().add(draft.getInitialPlayers().get(j));
//                draft.getDraftedTeams().get(i).getTaxiSquadPlayers().add(draft.getInitialPlayers().get(j));
//                draft.getInitialPlayers().remove(draft.getInitialPlayers().get(j));
//            }
//        }
//    }
    
    public void handleStartAutoDraftRequest(WDK_GUI gui) {
        counter = gui.getDataManager().getDraft().getDraftedTeams().size()*23;
        for(int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++){
            counter = counter - gui.getDataManager().getDraft().getDraftedTeams().get(j).getStartingLineUpPlayers().size();
        }
        counter1 = gui.getDataManager().getDraft().getDraftedTeams().size()*31;
        for(int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++){
            counter1 = counter1 - gui.getDataManager().getDraft().getDraftedTeams().get(j).getStartingLineUpPlayers().size();
        }
        ReentrantLock progressLock = new ReentrantLock();
        Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        try {
                            progressLock.lock();
                        for (int i = 0; i < counter1+1; i++) {
                            if(pauseFlag == false){
//                            if(i >= counter){
//                                flag = true;
//                            }
//                            if(i == counter1){
//                                flag = false;
//                            }
                            //System.out.print(i);
                            
                            // THIS WILL BE DONE ASYNCHRONOUSLY VIA MULTITHREADING
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    handleSelectPlayerRequest(gui);
                                }
                            });

                            // SLEEP EACH FRAME
                            try {
                                Thread.sleep(250);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                            }
                            else{
                                pauseFlag = false;
                                flag = false;
                                return null;
                            }
                        }
                        }
                        finally {
                                progressLock.unlock();
                                }

                        return null;
                    }
        };
        // THIS GETS THE THREAD ROLLING
        Thread thread = new Thread(task);
        thread.start();
//        flag = false;
    }
    
    public void handlePauseAutoDraftRequest(WDK_GUI gui) {
        pauseFlag = true;
    }
    
    /**
     * This method will export the current course.
     * 
     * @param gui
     */
    public void handleExportCourseRequest(WDK_GUI gui) {
        // EXPORT THE COURSE
        DraftDataManager dataManager = gui.getDataManager();
        Course courseToExport = dataManager.getCourse();

        // WE'LL NEED THIS TO LOAD THE EXPORTED PAGE FOR VIEWING
        String courseURL = exporter.getPageURLPath(courseToExport, CoursePage.SCHEDULE);
        
        // NOW GET THE EXPORTER
        try {            
            
            // AND EXPORT THE COURSE
            exporter.exportCourseSite(courseToExport);
            
            // AND THEN OPEN UP THE PAGE IN A BROWSER
            Stage webBrowserStage = new Stage();
            WebBrowser webBrowser = new WebBrowser(webBrowserStage, courseURL);
            webBrowserStage.show();
        }
        // WE'LL HANDLE COURSE EXPORT PROBLEMS AND COURSE PAGE VIEWING
        // PROBLEMS USING DIFFERENT ERROR MESSAGES
        catch (MalformedURLException murle) {
            errorHandler.handleViewSchedulePageError(courseURL);
        } catch (Exception ioe) {
            errorHandler.handleExportCourseError(courseToExport);
        }
    }

    
    /**
     * This method will exit the application, making sure the user doesn't lose
     * any data first.
     * 
     * @param gui
     */
    public void handleExitRequest(WDK_GUI gui) {
        try {
            // WE MAY HAVE TO SAVE CURRENT WORK
            boolean continueToExit = true;
            if (!saved) {
                // THE USER CAN OPT OUT HERE
                continueToExit = promptToSave(gui);
            }

            // IF THE USER REALLY WANTS TO EXIT THE APP
            if (continueToExit) {
                // EXIT THE APPLICATION
                System.exit(0);
            }
        } catch (IOException ioe) {
            ErrorHandler eH = ErrorHandler.getErrorHandler();
            eH.handleExitError();
        }
    }

    /**
     * This helper method verifies that the user really wants to save their
     * unsaved work, which they might not want to do. Note that it could be used
     * in multiple contexts before doing other actions, like creating a new
     * Course, or opening another Course. Note that the user will be
     * presented with 3 options: YES, NO, and CANCEL. YES means the user wants
     * to save their work and continue the other action (we return true to
     * denote this), NO means don't save the work but continue with the other
     * action (true is returned), CANCEL means don't save the work and don't
     * continue with the other action (false is returned).
     *
     * @return true if the user presses the YES option to save, true if the user
     * presses the NO option to not save, false if the user presses the CANCEL
     * option to not continue.
     */
    private boolean promptToSave(WDK_GUI gui) throws IOException {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yesNoCancelDialog.show(properties.getProperty(SAVE_UNSAVED_WORK_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoCancelDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) {
            // SAVE THE COURSE
            DraftDataManager dataManager = gui.getDataManager();
            courseIO.saveCourse(dataManager.getCourse());
            saved = true;
            
            // AND THE INSTRUCTOR INFO
            Instructor lastInstructor = dataManager.getCourse().getInstructor();
            courseIO.saveLastInstructor(lastInstructor, JSON_FILE_PATH_LAST_INSTRUCTOR);
        } // IF THE USER SAID CANCEL, THEN WE'LL TELL WHOEVER
        // CALLED THIS THAT THE USER IS NOT INTERESTED ANYMORE
        else if (selection.equals(YesNoCancelDialog.CANCEL)) {
            return false;
        }

        // IF THE USER SAID NO, WE JUST GO ON WITHOUT SAVING
        // BUT FOR BOTH YES AND NO WE DO WHATEVER THE USER
        // HAD IN MIND IN THE FIRST PLACE
        return true;
    }

    /**
     * This helper method asks the user for a file to open. The user-selected
     * file is then loaded and the GUI updated. Note that if the user cancels
     * the open process, nothing is done. If an error occurs loading the file, a
     * message is displayed, but nothing changes.
     */
    private void promptToOpen(WDK_GUI gui) {
        // AND NOW ASK THE USER FOR THE COURSE TO OPEN
        FileChooser courseFileChooser = new FileChooser();
        courseFileChooser.setInitialDirectory(new File(PATH_DRAFTS));
        File selectedFile = courseFileChooser.showOpenDialog(gui.getWindow());

//        DraftDataManager dataManager = gui.getDataManager();
//        dataManager.reset();
        
        // ONLY OPEN A NEW FILE IF THE USER SAYS OK
        if (selectedFile != null) {
            try {

                Draft draftToLoad = gui.getDataManager().getDraft();
                courseIO.loadDraft(draftToLoad, selectedFile.getAbsolutePath());
//                gui.setWorkspaceActivated(false);
//                gui.activateWorkspace();

                DraftDataManager dataManager = gui.getDataManager();
                dataManager.reset();
                gui.reloadCourse(draftToLoad);
                ObservableList<String> teamNames = FXCollections.observableArrayList();
                    for (int i = 0; i < draftToLoad.getDraftedTeams().size(); i++) {
                        teamNames.add(draftToLoad.getDraftedTeams().get(i).getName());
                    }            
                gui.getComboBox().setItems(teamNames);

                gui.getRemoveTeamButton().setDisable(false);
                gui.getEditTeamButton().setDisable(false);
                
                saved = true;
                gui.updateToolbarControls(saved);
                //Instructor lastInstructor = courseToLoad.getInstructor();
                //courseIO.saveLastInstructor(lastInstructor, JSON_FILE_PATH_LAST_INSTRUCTOR);
            } catch (Exception e) {
                ErrorHandler eH = ErrorHandler.getErrorHandler();
                eH.handleLoadCourseError();
            }
        }
    }

    /**
     * This mutator method marks the file as not saved, which means that when
     * the user wants to do a file-type operation, we should prompt the user to
     * save current work first. Note that this method should be called any time
     * the course is changed in some way.
     */
    public void markFileAsNotSaved() {
        saved = false;
    }

    /**
     * Accessor method for checking to see if the current course has been saved
     * since it was last edited.
     *
     * @return true if the current course is saved to the file, false otherwise.
     */
    public boolean isSaved() {
        return saved;
    }
    
    public void calculateBA(Team t){
        DecimalFormat df = new DecimalFormat("##0.000");
        double sum = 0;
        int counter = 0;
        for (int i =0; i< t.getStartingLineUpPlayers().size();i++){
            if(t.getStartingLineUpPlayers().get(i).getTYPE().equals("Hitter")){
                sum = sum + t.getStartingLineUpPlayers().get(i).getBA();
                counter++;
            }
        }
        if(counter != 0){
            t.setBA(sum/counter);
            t.setBA_DISPLAY(df.format((float)sum/counter));
        }
        else{
            t.setBA(0.000);
            t.setBA_DISPLAY("0.000");
        }
    }
    
    public void calculateERA(Team t){
        DecimalFormat df = new DecimalFormat("##0.00");
        double sum = 0;
        int counter = 0;
        for (int i =0; i< t.getStartingLineUpPlayers().size();i++){
            if(t.getStartingLineUpPlayers().get(i).getTYPE().equals("Pitcher")){
                sum+=t.getStartingLineUpPlayers().get(i).getERA();
                counter++;
            }
        }
        if(counter != 0){
            t.setERA(sum/counter);
            t.setERA_DISPLAY(df.format((float)sum/counter));
        }
        else{
            t.setERA(0.00);
            t.setERA_DISPLAY("0.00");
        }
    }
    
    public void calculateWHIP(Team t){
        DecimalFormat df = new DecimalFormat("##0.00");
        double sum = 0;
        int counter = 0;
        for (int i =0; i< t.getStartingLineUpPlayers().size();i++){
            if(t.getStartingLineUpPlayers().get(i).getTYPE().equals("Pitcher")){
                sum+=t.getStartingLineUpPlayers().get(i).getWHIP();
                counter++;
            }
        }
        if(counter != 0){
            t.setWHIP(sum/counter);
            t.setWHIP_DISPLAY(df.format((float)sum/counter));
        }
        else{
            t.setWHIP(0.00);
            t.setWHIP_DISPLAY("0.00");
        }
    }
}
