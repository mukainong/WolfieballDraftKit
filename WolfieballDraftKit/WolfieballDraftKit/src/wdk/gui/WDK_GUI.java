package wdk.gui;

import java.io.FileNotFoundException;
import static wdk.WDK_StartupConstants.*;
import wdk.WDK_PropertyType;
import wdk.controller.PlayersScreenUpdateController;
import wdk.data.Course;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.CourseDataView;
import wdk.data.CoursePage;
import wdk.controller.FileController;
import wdk.controller.ScheduleEditController;
import wdk.data.Player;
import wdk.file.WolfieballFileManager;
import wdk.file.CourseSiteExporter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;

import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;
import properties_manager.PropertiesManager;
import wdk.data.Team;

/**
 * This class provides the Graphical User Interface for this application,
 * managing all the UI components for editing a Course and exporting it to a
 * site.
 *
 * @author Richard McKenna
 */
public class WDK_GUI implements CourseDataView {
    
    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS GUI'S COMPONENTS TO A STYLE SHEET THAT IT USES

    static final String PRIMARY_STYLE_SHEET = PATH_CSS + "csb_style.css";
    static final String CLASS_RADIOBUTTON = "radioButton";
    static final String CLASS_SCREENVBOXBORDER = "screenVBoxBorder";
    static final String CLASS_BORDERED_PANE = "bordered_pane";
    static final String CLASS_SUBJECT_PANE = "subject_pane";
    static final String CLASS_HEADING_LABEL = "heading_label";
    static final String CLASS_NORMAL_LABEL = "normal_label";
    static final String CLASS_SUBHEADING_LABEL = "subheading_label";
    static final String CLASS_PROMPT_LABEL = "prompt_label";
    static final String EMPTY_TEXT = "";
    static final int LARGE_TEXT_FIELD_LENGTH = 20;
    static final int SMALL_TEXT_FIELD_LENGTH = 5;

    // THIS MANAGES ALL OF THE APPLICATION'S DATA
    DraftDataManager dataManager;

    // THIS MANAGES COURSE FILE I/O
    WolfieballFileManager wolfieballFileManager;

    // THIS MANAGES EXPORTING OUR SITE PAGES
    CourseSiteExporter siteExporter;

    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    FileController fileController;

    // THIS HANDLES INTERACTIONS WITH COURSE INFO CONTROLS
    PlayersScreenUpdateController playersScreenUpdateController;
    
    // THIS HANDLES REQUESTS TO ADD OR EDIT SCHEDULE STUFF
    ScheduleEditController scheduleController;

    // THIS HANDLES REQUESTS TO ADD OR EDIT LECTURE STUFF
    ScheduleEditController lectureController;
    
    // THIS HANDLES REQUESTS TO ADD OR EDIT ASSIGNMENT STUFF
    ScheduleEditController assignmentController;
    
    // THIS IS THE APPLICATION WINDOW
    Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION GUI
    BorderPane csbPane;
    
    // THIS IS THE TOP TOOLBAR AND ITS CONTROLS
    FlowPane fileToolbarPane;
    Button newCourseButton;
    Button loadCourseButton;
    Button saveCourseButton;
    Button exportSiteButton;
    Button exitButton;
    
    // THIS IS TEH BOTTOM TOOLBAR AND ITS CONTROLS
    FlowPane screenToolbarPane;
    Button playersScreenButton;
    Button teamsScreenButton;
    Button standingsScreenButton;
    Button draftScreenButton;
    Button MLBScreenButton;

    // WE'LL ORGANIZE OUR WORKSPACE COMPONENTS USING A BORDER PANE
    BorderPane workspacePane;
    boolean workspaceActivated;
    
    // WE'LL PUT THE WORKSPACE INSIDE A SCROLL PANE
    ScrollPane workspaceScrollPane;

    // WE'LL PUT THIS IN THE TOP OF THE WORKSPACE, IT WILL
    // HOLD TWO OTHER PANES FULL OF CONTROLS AS WELL AS A LABEL
    VBox topWorkspacePane;
    Label courseHeadingLabel;
    SplitPane topWorkspaceSplitPane;

    // PLAYERS SCREEN GUI COMPONENTS
    VBox playersScreenBox;
    HBox playersScreenToolbar;
    HBox radioButtonBar;
    Button addPlayerButton;
    Button removePlayerButton;
    Label playersLabel;
    Label searchLabel;
    TextField searchTextField;
    RadioButton allRadioButton;
    RadioButton CRadioButton;
    RadioButton _1BRadioButton;
    RadioButton CIRadioButton;
    RadioButton _3BRadioButton;
    RadioButton _2BRadioButton;
    RadioButton MIRadioButton;
    RadioButton SSRadioButton;
    RadioButton OFRadioButton;
    RadioButton URadioButton;
    RadioButton PRadioButton;
    Label allLabel;
    Label CLabel;
    Label _1BLabel;
    Label CILabel;
    Label _3BLabel;
    Label _2BLabel;
    Label MILabel;
    Label SSLabel;
    Label OFLabel;
    Label ULabel;
    Label PLabel;
    TableView<Player> playersTable;
    TableColumn firstNameColumn;
    TableColumn lastNameColumn;
    TableColumn proTeamColumn;
    TableColumn positionsColumn;
    TableColumn yearOfBirthColumn;
    TableColumn RWColumn;
    TableColumn RColumn;
    TableColumn WColumn;
    TableColumn HRSVColumn;
    TableColumn HRColumn;
    TableColumn SVColumn;
    TableColumn RBIKColumn;
    TableColumn RBIColumn;
    TableColumn KColumn;
    TableColumn SBERAColumn;
    TableColumn SBColumn;
    TableColumn ERAColumn;
    TableColumn BAWHIPColumn;
    TableColumn BAColumn;
    TableColumn WHIPColumn;
    TableColumn estimatedValueColumn;
    TableColumn notesColumn;
    
    // TEAMS SCREEN GUI COMPONENTS
    ScrollPane teamsScrollPane;
    VBox teamsScreenBox;
    Label teamsLabel;
    HBox draftNameBar;
    Label draftNameLabel;
    TextField draftNameTextField;
    HBox teamsScreenToolbar;
    Button addTeamButton;
    Button removeTeamButton;
    Button editTeamButton;
    Label selectFantasyTeamLabel;
    ComboBox selectFantasyTeamComboBox;
    VBox startingLineUpBox;
    Label startingLineUpLabel;
    TableView<Player> startingLineUpTable;
    VBox taxiSquadBox;
    Label taxiSquadLabel;
    TableView<Player> taxiSquadTable;
    TableColumn positionColumn_team;
    TableColumn firstNameColumn_team;
    TableColumn lastNameColumn_team;
    TableColumn proTeamColumn_team;
    TableColumn positionsColumn_team;
    TableColumn RWColumn_team;
    TableColumn HRSVColumn_team;
    TableColumn RBIKColumn_team;
    TableColumn SBERAColumn_team;
    TableColumn BAWHIPColumn_team;
    TableColumn estimatedValueColumn_team;
    TableColumn contractColumn_team;
    TableColumn salaryColumn_team;  
    TableColumn positionColumn_taxi;
    TableColumn firstNameColumn_taxi;
    TableColumn lastNameColumn_taxi;
    TableColumn proTeamColumn_taxi;
    TableColumn positionsColumn_taxi;
    TableColumn RWColumn_taxi;
    TableColumn HRSVColumn_taxi;
    TableColumn RBIKColumn_taxi;
    TableColumn SBERAColumn_taxi;
    TableColumn BAWHIPColumn_taxi;
    TableColumn estimatedValueColumn_taxi;
    TableColumn contractColumn_taxi;
    TableColumn salaryColumn_taxi; 
    
    // THIS REGION IS FOR MANAGING SCHEDULE ITEMS OTHER THAN LECTURES AND HWS
    VBox standingsScreenBox;
    Label standingsLabel;
    TableView<Team> fantasyStandingsTable;
    TableColumn teamColumn_fantasyStandings;
    TableColumn playersNeededColumn_fantasyStandings;
    TableColumn moneyLeftColumn_fantasyStandings;
    TableColumn moneyPPColumn_fantasyStandings;
    TableColumn RColumn_fantasyStandings;
    TableColumn HRColumn_fantasyStandings;
    TableColumn RBIColumn_fantasyStandings;
    TableColumn SBColumn_fantasyStandings;
    TableColumn BAColumn_fantasyStandings;
    TableColumn WColumn_fantasyStandings;
    TableColumn SVColumn_fantasyStandings;
    TableColumn KColumn_fantasyStandings;
    TableColumn ERAColumn_fantasyStandings;
    TableColumn WHIPColumn_fantasyStandings;
    TableColumn totalPointsColumn_fantasyStandings;
    
    // THIS REGION IS FOR MANAGING SCHEDULE ITEMS OTHER THAN LECTURES AND HWS
    VBox draftScreenBox;
    Label draftLabel;
    HBox draftControlBar;
    Button selectPlayerButton;
    Button startAutoDraftButton;
    Button pauseAutoDraftButton;
    TableView<Player> draftTable;
    TableColumn draftPickColumn_draft;
    TableColumn firstNameColumn_draft;
    TableColumn lastNameColumn_draft;
    TableColumn fantasyTeamColumn_draft;
    TableColumn contractColumn_draft;
    TableColumn salaryColumn_draft;
    
    // THIS REGION IS FOR MANAGING SCHEDULE ITEMS OTHER THAN LECTURES AND HWS
    VBox MLBScreenBox;
    Label MLBLabel;
    HBox selectMLBBar;
    Label selectMLBLabel;
    ComboBox selectMLBComboBox;
    TableView<Player> MLBTable;
    TableColumn firstNameColumn_MLB;
    TableColumn lastNameColumn_MLB;
    TableColumn positionsColumn_MLB;
    
    // AND TABLE COLUMNS
    static final String COL_FIRSTNAME = "First";
    static final String COL_LASTNAME = "Last";
    static final String COL_PROTEAM = "Pro Team";
    static final String COL_POSITIONS = "Positions";
    static final String COL_YEAROFBIRTH = "Year Of Birth";
    static final String COL_RW = "R/W";
    static final String COL_HRSV = "HR/SV";
    static final String COL_RBIK = "RBI/K";
    static final String COL_SBERA = "SB/ERA";
    static final String COL_BAWHIP = "BA/WHIP";
    static final String COL_ESTIMATEDVALUE = "Estimated Value";
    static final String COL_DESCRIPTION = "Description";
    static final String COL_NOTES = "Notes";
    static final String COL_DATE = "Date";
    static final String COL_LINK = "Link";
    static final String COL_TOPIC = "Topic";
    static final String COL_SESSIONS = "Number of Sessions";
    static final String COL_NAME = "Name";
    static final String COL_TOPICS = "Topics";
    static final String COL_POSITION = "Position";
    static final String COL_CONTRACT = "Contract";
    static final String COL_SALARY = "Salary";
    
    // HERE ARE OUR DIALOGS
    MessageDialog messageDialog;
    YesNoCancelDialog yesNoCancelDialog;
    
    /**
     * Constructor for making this GUI, note that it does not initialize the UI
     * controls. To do that, call initGUI.
     *
     * @param initPrimaryStage Window inside which the GUI will be displayed.
     */
    public WDK_GUI(Stage initPrimaryStage) {
        primaryStage = initPrimaryStage;
    }

    /**
     * Accessor method for the data manager.
     *
     * @return The CourseDataManager used by this UI.
     */
    public DraftDataManager getDataManager() {
        return dataManager;
    }

    /**
     * Accessor method for the file controller.
     *
     * @return The FileController used by this UI.
     */
    public FileController getFileController() {
        return fileController;
    }

    /**
     * Accessor method for the course file manager.
     *
     * @return The CourseFileManager used by this UI.
     */
    public WolfieballFileManager getWolfieballFileManager() {
        return wolfieballFileManager;
    }

    /**
     * Accessor method for the site exporter.
     *
     * @return The CourseSiteExporter used by this UI.
     */
    public CourseSiteExporter getSiteExporter() {
        return siteExporter;
    }

    /**
     * Accessor method for the window (i.e. stage).
     *
     * @return The window (i.e. Stage) used by this UI.
     */
    public Stage getWindow() {
        return primaryStage;
    }
    
    public MessageDialog getMessageDialog() {
        return messageDialog;
    }
    
    public YesNoCancelDialog getYesNoCancelDialog() {
        return yesNoCancelDialog;
    }

    /**
     * Mutator method for the data manager.
     *
     * @param initDataManager The CourseDataManager to be used by this UI.
     */
    public void setDataManager(DraftDataManager initDataManager) {
        dataManager = initDataManager;
    }

    /**
     * Mutator method for the course file manager.
     *
     * @param initCourseFileManager The CourseFileManager to be used by this UI.
     */
    public void setWolfieballFileManager(WolfieballFileManager initCourseFileManager) {
        wolfieballFileManager = initCourseFileManager;
    }

    /**
     * Mutator method for the site exporter.
     *
     * @param initSiteExporter The CourseSiteExporter to be used by this UI.
     */
    public void setSiteExporter(CourseSiteExporter initSiteExporter) {
        siteExporter = initSiteExporter;
    }

    /**
     * This method fully initializes the user interface for use.
     *
     * @param windowTitle The text to appear in the UI window's title bar.
     * @param subjects The list of subjects to choose from.
     * @throws IOException Thrown if any initialization files fail to load.
     */
    public void initGUI(String windowTitle, ArrayList<String> subjects) throws IOException {
        // INIT THE DIALOGS
        initDialogs();
        
        // INIT THE TOOLBAR
        initFileToolbar();

        // INIT THE CENTER WORKSPACE CONTROLS BUT DON'T ADD THEM
        // TO THE WINDOW YET
        initWorkspace(subjects);

        // NOW SETUP THE EVENT HANDLERS
        initEventHandlers();

        // AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
        initWindow(windowTitle);
    }

    /**
     * When called this function puts the workspace into the window,
     * revealing the controls for editing a Course.
     */
    public void activateWorkspace() {
        if (!workspaceActivated) {
            // PUT THE WORKSPACE IN THE GUI
            csbPane.setCenter(workspaceScrollPane);
            workspaceActivated = true;
        }
    }
    
    /**
     * This function takes all of the data out of the courseToReload 
     * argument and loads its values into the user interface controls.
     * 
     * @param courseToReload The Course whose data we'll load into the GUI.
     */
    @Override
    public void reloadCourse(Draft draftToReload) {
        // FIRST ACTIVATE THE WORKSPACE IF NECESSARY
        if (!workspaceActivated) {
            activateWorkspace();
        }

        // WE DON'T WANT TO RESPOND TO EVENTS FORCED BY
        // OUR INITIALIZATION SELECTIONS
        playersScreenUpdateController.enable(false);
        
        // THE SCHEDULE ITEMS TABLE
       
        // THE LECTURES TABLE
        
        // THE HWS TABLE

        // NOW WE DO WANT TO RESPOND WHEN THE USER INTERACTS WITH OUR CONTROLS
        playersScreenUpdateController.enable(true);
    }

    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Course has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveCourseButton.setDisable(saved);
        //saveCourseButton.setDisable(false);

        // ALL THE OTHER BUTTONS ARE ALWAYS ENABLED
        // ONCE EDITING THAT FIRST COURSE BEGINS
        loadCourseButton.setDisable(false);
        exportSiteButton.setDisable(false);

        // NOTE THAT THE NEW, LOAD, AND EXIT BUTTONS
        // ARE NEVER DISABLED SO WE NEVER HAVE TO TOUCH THEM
    }

    /**
     * This function loads all the values currently in the user interface
     * into the course argument.
     * 
     * @param course The course to be updated using the data from the UI controls.
     */

    
    public void updatePlayersScreen(Draft draft){
        draft.clearSelectedPlayers();
        for(int i = 0; i < draft.getInitialPlayers().size(); i++){
            if(draft.getInitialPlayers().get(i).getLAST_NAME().toLowerCase().contains(searchTextField.getText().toLowerCase()) || draft.getInitialPlayers().get(i).getFIRST_NAME().toLowerCase().contains(searchTextField.getText().toLowerCase())){
                draft.getSelectedPlayers().add(draft.getInitialPlayers().get(i));
            }
        }
        
        if(allRadioButton.isSelected()){
            RWColumn.setText("R/W");
            HRSVColumn.setText("HR/SV");
            RBIKColumn.setText("RBI/K");
            SBERAColumn.setText("SB/ERA");
            BAWHIPColumn.setText("BA/WHIP");
            
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(CRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("C"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(_1BRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("1B"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(CIRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("1B") || temList.get(i).getPosition().contains("3B"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(_3BRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("3B"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(_2BRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("2B"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(MIRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("2B") || temList.get(i).getPosition().contains("SS"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(SSRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("SS"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(OFRadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("OF"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(URadioButton.isSelected()){
            RWColumn.setText("R");
            HRSVColumn.setText("HR");
            RBIKColumn.setText("RBI");
            SBERAColumn.setText("SB");
            BAWHIPColumn.setText("BA");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("C") || temList.get(i).getPosition().contains("1B") || temList.get(i).getPosition().contains("3B") || temList.get(i).getPosition().contains("2B") || temList.get(i).getPosition().contains("SS") || temList.get(i).getPosition().contains("OF"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
        
        if(PRadioButton.isSelected()){
            RWColumn.setText("W");
            HRSVColumn.setText("SV");
            RBIKColumn.setText("K");
            SBERAColumn.setText("ERA");
            BAWHIPColumn.setText("WHIP");
            
            ObservableList<Player> temList = FXCollections.observableArrayList();
            for(int i = 0; i < draft.getSelectedPlayers().size(); i++){
                temList.add(draft.getSelectedPlayers().get(i));
            }
            draft.getSelectedPlayers().clear();
            for(int i = 0; i < temList.size(); i++){
                if(temList.get(i).getPosition().contains("P"))
                     draft.getSelectedPlayers().add(temList.get(i));
            }
            playersTable.setItems(dataManager.getDraft().getSelectedPlayers());
        }
    }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR GUI */
    /****************************************************************************/
    
    private void initDialogs() {
        messageDialog = new MessageDialog(primaryStage, CLOSE_BUTTON_LABEL);
        yesNoCancelDialog = new YesNoCancelDialog(primaryStage);
    }
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initFileToolbar() {
        fileToolbarPane = new FlowPane();

        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        newCourseButton = initChildButton(fileToolbarPane, WDK_PropertyType.NEW_COURSE_ICON, WDK_PropertyType.NEW_COURSE_TOOLTIP, false);
        loadCourseButton = initChildButton(fileToolbarPane, WDK_PropertyType.LOAD_COURSE_ICON, WDK_PropertyType.LOAD_COURSE_TOOLTIP, false);
        saveCourseButton = initChildButton(fileToolbarPane, WDK_PropertyType.SAVE_COURSE_ICON, WDK_PropertyType.SAVE_COURSE_TOOLTIP, true);
        exportSiteButton = initChildButton(fileToolbarPane, WDK_PropertyType.EXPORT_PAGE_ICON, WDK_PropertyType.EXPORT_PAGE_TOOLTIP, true);
        exitButton = initChildButton(fileToolbarPane, WDK_PropertyType.EXIT_ICON, WDK_PropertyType.EXIT_TOOLTIP, false);
    }
    
    /**
     * This function initializes all the buttons in the toolbar at the bottom of
     * the application window. 
     */
    private void initScreenToolbar() {
        screenToolbarPane = new FlowPane();

        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        teamsScreenButton = initChildButton(screenToolbarPane, WDK_PropertyType.TEAM_ICON, WDK_PropertyType.TEAM_TOOLTIP, false);
        playersScreenButton = initChildButton(screenToolbarPane, WDK_PropertyType.PLAYER_ICON, WDK_PropertyType.PLAYER_TOOLTIP, false);
        standingsScreenButton = initChildButton(screenToolbarPane, WDK_PropertyType.STANDING_ICON, WDK_PropertyType.STANDING_TOOLTIP, false);
        draftScreenButton = initChildButton(screenToolbarPane, WDK_PropertyType.DRAFT_ICON, WDK_PropertyType.DRAFT_TOOLTIP, false);
        MLBScreenButton = initChildButton(screenToolbarPane, WDK_PropertyType.MLB_ICON, WDK_PropertyType.MLB_TOOLTIP, false);
    }

    // CREATES AND SETS UP ALL THE CONTROLS TO GO IN THE APP WORKSPACE
    private void initWorkspace(ArrayList<String> subjects) throws IOException {

        // THE TOP WORKSPACE HOLDS BOTH THE BASIC COURSE INFO
        // CONTROLS AS WELL AS THE PAGE SELECTION CONTROLS
        initTopWorkspace();

        // THIS IS FOR MANAGING SCHEDULE EDITING
        initScheduleItemsControls();

        // THIS IS FOR SCREENTOOLBAR
        initScreenToolbar();
        
        // THIS HOLDS ALL OUR WORKSPACE COMPONENTS, SO NOW WE MUST
        // ADD THE COMPONENTS WE'VE JUST INITIALIZED
        workspacePane = new BorderPane();
        //workspacePane.setTop(topWorkspacePane);********************************************************************
        workspacePane.setCenter(teamsScreenBox);
        workspacePane.setBottom(screenToolbarPane);
        workspacePane.getStyleClass().add(CLASS_BORDERED_PANE);
        
        // AND NOW PUT IT IN THE WORKSPACE
        workspaceScrollPane = new ScrollPane();
        workspaceScrollPane.setContent(workspacePane);
        workspaceScrollPane.setFitToWidth(true);

        // NOTE THAT WE HAVE NOT PUT THE WORKSPACE INTO THE WINDOW,
        // THAT WILL BE DONE WHEN THE USER EITHER CREATES A NEW
        // COURSE OR LOADS AN EXISTING ONE FOR EDITING
        workspaceActivated = false;
    }
    
    // INITIALIZES THE TOP PORTION OF THE WORKWPACE UI
    private void initTopWorkspace() {
        // HERE'S THE SPLIT PANE, ADD THE TWO GROUPS OF CONTROLS
        topWorkspaceSplitPane = new SplitPane();

        // THE TOP WORKSPACE PANE WILL ONLY DIRECTLY HOLD 2 THINGS, A LABEL
        // AND A SPLIT PANE, WHICH WILL HOLD 2 ADDITIONAL GROUPS OF CONTROLS
        topWorkspacePane = new VBox();
        topWorkspacePane.getStyleClass().add(CLASS_BORDERED_PANE);

        // HERE'S THE LABEL
        courseHeadingLabel = initChildLabel(topWorkspacePane, WDK_PropertyType.COURSE_HEADING_LABEL, CLASS_HEADING_LABEL);

        // AND NOW ADD THE SPLIT PANE
        topWorkspacePane.getChildren().add(topWorkspaceSplitPane);
    }
    
    // INITIALIZE THE SCHEDULE ITEMS CONTROLS
    private void initScheduleItemsControls() {

        
        // NOW THE CONTROLS FOR DISPLAYING PLAYERS
        playersScreenBox = new VBox();
        playersScreenBox.setPrefHeight(754);
        playersScreenBox.setStyle("-fx-background-color: #FFFFFF;");
        playersScreenToolbar = new HBox();
        radioButtonBar = new HBox();
        radioButtonBar.setPrefHeight(100);
        radioButtonBar.getStyleClass().add(CLASS_RADIOBUTTON);
        playersLabel = initLabel(WDK_PropertyType.PLAYERS_SCREEN_HEADING_LABEL, CLASS_HEADING_LABEL);
        playersLabel.setPadding(new Insets(5,0,5,0));
        addPlayerButton = initChildButton(playersScreenToolbar, WDK_PropertyType.ADD_ICON, WDK_PropertyType.ADD_ITEM_TOOLTIP, false);
        removePlayerButton = initChildButton(playersScreenToolbar, WDK_PropertyType.MINUS_ICON, WDK_PropertyType.REMOVE_ITEM_TOOLTIP, true);
        searchLabel = initChildLabel(playersScreenToolbar, WDK_PropertyType.SEARCH_LABEL, CLASS_SUBHEADING_LABEL);
        searchLabel.setPadding(new Insets(10,10,10,30));
        searchTextField = initChildTextField(playersScreenToolbar);
        
        // NOW THE RADIOBUTTON
        ToggleGroup group = new ToggleGroup ();
        allRadioButton = initChildRadioButton(radioButtonBar, false, group);
        allRadioButton.setPadding(new Insets(10,0,10,10));
        allRadioButton.setSelected(true);
        allLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_ALL_LABEL, CLASS_NORMAL_LABEL);
        allLabel.setPadding(new Insets(10,10,10,0));
        CRadioButton = initChildRadioButton(radioButtonBar, false, group);
        CRadioButton.setPadding(new Insets(10,0,10,10));
        CLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_C_LABEL, CLASS_NORMAL_LABEL);
        CLabel.setPadding(new Insets(10,10,10,0));
        _1BRadioButton = initChildRadioButton(radioButtonBar, false, group);
        _1BRadioButton.setPadding(new Insets(10,0,10,10));
        _1BLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_1B_LABEL, CLASS_NORMAL_LABEL);
        _1BLabel.setPadding(new Insets(10,10,10,0));
        CIRadioButton = initChildRadioButton(radioButtonBar, false, group);
        CIRadioButton.setPadding(new Insets(10,0,10,10));
        CILabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_CI_LABEL, CLASS_NORMAL_LABEL);
        CILabel.setPadding(new Insets(10,10,10,0));
        _3BRadioButton = initChildRadioButton(radioButtonBar, false, group);
        _3BRadioButton.setPadding(new Insets(10,0,10,10));
        _3BLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_3B_LABEL, CLASS_NORMAL_LABEL);
        _3BLabel.setPadding(new Insets(10,10,10,0));
        _2BRadioButton = initChildRadioButton(radioButtonBar, false, group);
        _2BRadioButton.setPadding(new Insets(10,0,10,10));
        _2BLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_2B_LABEL, CLASS_NORMAL_LABEL);
        _2BLabel.setPadding(new Insets(10,10,10,0));
        MIRadioButton = initChildRadioButton(radioButtonBar, false, group);
        MIRadioButton.setPadding(new Insets(10,0,10,10));
        MILabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_MI_LABEL, CLASS_NORMAL_LABEL);
        MILabel.setPadding(new Insets(10,10,10,0));
        SSRadioButton = initChildRadioButton(radioButtonBar, false, group);
        SSRadioButton.setPadding(new Insets(10,0,10,10));
        SSLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_SS_LABEL, CLASS_NORMAL_LABEL);
        SSLabel.setPadding(new Insets(10,10,10,0));
        OFRadioButton = initChildRadioButton(radioButtonBar, false, group);
        OFRadioButton.setPadding(new Insets(10,0,10,10));
        OFLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_OF_LABEL, CLASS_NORMAL_LABEL);
        OFLabel.setPadding(new Insets(10,10,10,0));
        URadioButton = initChildRadioButton(radioButtonBar, false, group);
        URadioButton.setPadding(new Insets(10,0,10,10));
        ULabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_U_LABEL, CLASS_NORMAL_LABEL);
        ULabel.setPadding(new Insets(10,10,10,0));
        PRadioButton= initChildRadioButton(radioButtonBar, false, group);
        PRadioButton.setPadding(new Insets(10,0,10,10));
        PLabel = initChildLabel(radioButtonBar, WDK_PropertyType.POSITION_P_LABEL, CLASS_NORMAL_LABEL);
        PLabel.setPadding(new Insets(10,10,10,0));
        //searchTextField = initChildTextField();
        
        // NOW THE TABLEVIEW
        playersTable = new TableView();
        playersScreenBox.getChildren().add(playersLabel);
        playersScreenBox.getChildren().add(playersScreenToolbar);
        playersScreenBox.getChildren().add(radioButtonBar);
        playersScreenBox.getChildren().add(playersTable);
        playersScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        
        // NOW SETUP THE TABLE COLUMNS
        firstNameColumn = new TableColumn(COL_FIRSTNAME);
        lastNameColumn = new TableColumn(COL_LASTNAME);
        proTeamColumn = new TableColumn(COL_PROTEAM);
        positionsColumn = new TableColumn(COL_POSITIONS);
        yearOfBirthColumn = new TableColumn(COL_YEAROFBIRTH);
        RWColumn = new TableColumn(COL_RW);
        HRSVColumn = new TableColumn(COL_HRSV);
        RBIKColumn = new TableColumn(COL_RBIK);
        SBERAColumn = new TableColumn(COL_SBERA);
        BAWHIPColumn = new TableColumn(COL_BAWHIP);
        estimatedValueColumn = new TableColumn(COL_ESTIMATEDVALUE);
        notesColumn = new TableColumn(COL_NOTES);
        
        // AND LINK THE COLUMNS TO THE DATA
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FIRST_NAME"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("LAST_NAME"));
        proTeamColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("TEAM"));
        yearOfBirthColumn.setCellValueFactory(new PropertyValueFactory<Player, IntegerProperty>("YEAR_OF_BIRTH"));
        positionsColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("Position"));
        RWColumn.setCellValueFactory(new PropertyValueFactory<Player, IntegerProperty>("RW"));
        HRSVColumn.setCellValueFactory(new PropertyValueFactory<Player, IntegerProperty>("HRSV"));
        RBIKColumn.setCellValueFactory(new PropertyValueFactory<Player, IntegerProperty>("RBIK"));
        SBERAColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SBERA"));
        SBERAColumn.setComparator(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(o1.equals("-") && o2.equals("-"))return 0;
                else if(o1.equals("-") && !o2.equals("-"))return 1;
                else if(!o1.equals("-") && o2.equals("-"))return -1;
                
                if(Double.parseDouble(o1) - Double.parseDouble(o2) > 0){
                    return 1;
                }
                else if(Double.parseDouble(o1) - Double.parseDouble(o2) == 0){
                    return 0;
                }
                else if(Double.parseDouble(o1) - Double.parseDouble(o2) < 0){
                    return -1;
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
        BAWHIPColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("BAWHIP"));
        BAWHIPColumn.setComparator(new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                if(o1.equals("-") && o2.equals("-"))return 0;
                else if(o1.equals("-") && !o2.equals("-"))return 1;
                else if(!o1.equals("-") && o2.equals("-"))return -1;
                
                if(Double.parseDouble(o1) - Double.parseDouble(o2) > 0){
                    return 1;
                }
                else if(Double.parseDouble(o1) - Double.parseDouble(o2) == 0){
                    return 0;
                }
                else if(Double.parseDouble(o1) - Double.parseDouble(o2) < 0){
                    return -1;
                }
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
            
        });
//        RWColumn.setCellValueFactory(new PropertyValueFactory<String, String>("W"));
//        HRSVColumn.setCellValueFactory(new PropertyValueFactory<String, String>("SV"));
//        RBIKColumn.setCellValueFactory(new PropertyValueFactory<String, String>("K"));
//        SBERAColumn.setCellValueFactory(new PropertyValueFactory<String, String>("ERA"));
//        BAWHIPColumn.setCellValueFactory(new PropertyValueFactory<String, String>("WHIP"));
        estimatedValueColumn.setCellValueFactory(new PropertyValueFactory<Player, DoubleProperty>("estimatedValue"));
        notesColumn.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("NOTES"));
        
        playersTable.getColumns().add(firstNameColumn);
        playersTable.getColumns().add(lastNameColumn);
        playersTable.getColumns().add(proTeamColumn);
        playersTable.getColumns().add(positionsColumn);
        playersTable.getColumns().add(yearOfBirthColumn);
        playersTable.getColumns().add(RWColumn);
        playersTable.getColumns().add(HRSVColumn);
        playersTable.getColumns().add(RBIKColumn);
        playersTable.getColumns().add(SBERAColumn);
        playersTable.getColumns().add(BAWHIPColumn);
        playersTable.getColumns().add(estimatedValueColumn);
        playersTable.getColumns().add(notesColumn);
        playersTable.setItems(dataManager.getDraft().getInitialPlayers());
        //playersTable.setItems(dataManager.getCourse().getScheduleItems());
        
        // NOW THE CONTROLS FOR DISPLAYING TEAMS
        teamsScreenBox = new VBox();
        //teamsScreenBox.setPrefHeight(754);
        teamsScreenBox.setStyle("-fx-background-color: #FFFFFF;");
        draftNameBar = new HBox();
        teamsScreenToolbar = new HBox();
        startingLineUpBox = new VBox();
        //startingLineUpBox.setPrefHeight(600);
        taxiSquadBox = new VBox();
        //taxiSquadBox.setPrefHeight(1000);
        teamsScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        startingLineUpBox.getStyleClass().add(CLASS_BORDERED_PANE);
        taxiSquadBox.getStyleClass().add(CLASS_BORDERED_PANE);
        
        teamsLabel = initLabel(WDK_PropertyType.TEAMS_SCREEN_HEADING_LABEL, CLASS_HEADING_LABEL);
        teamsLabel.setPadding(new Insets(5,0,5,0));
        draftNameLabel = initChildLabel(draftNameBar, WDK_PropertyType.DRAFT_NAME_LABEL, CLASS_NORMAL_LABEL);
        draftNameLabel.setPadding(new Insets(10,10,10,0));
        draftNameTextField = initChildTextField(draftNameBar);
        draftNameTextField.setPrefColumnCount(20);
        addTeamButton = initChildButton(teamsScreenToolbar, WDK_PropertyType.ADD_ICON, WDK_PropertyType.ADD_ITEM_TOOLTIP, false);
        removeTeamButton = initChildButton(teamsScreenToolbar, WDK_PropertyType.MINUS_ICON, WDK_PropertyType.REMOVE_ITEM_TOOLTIP, true);
        editTeamButton = initChildButton(teamsScreenToolbar, WDK_PropertyType.EDIT_ICON, WDK_PropertyType.EDIT_ITEM_TOOLTIP, true);
        selectFantasyTeamLabel = initChildLabel(teamsScreenToolbar, WDK_PropertyType.SELECT_FANTASY_TEAM_LABEL, CLASS_NORMAL_LABEL);
        selectFantasyTeamComboBox = initChildComboBox(teamsScreenToolbar);
        startingLineUpLabel = initLabel(WDK_PropertyType.STARTING_LINEUP_LABEL, CLASS_SUBHEADING_LABEL);
        taxiSquadLabel = initLabel(WDK_PropertyType.TAXI_SQUAD_LABEL, CLASS_SUBHEADING_LABEL);
        
        // NOW THE TABLEVIEW
        startingLineUpTable = new TableView();
        taxiSquadTable = new TableView();
        teamsScreenBox.getChildren().add(teamsLabel);
        teamsScreenBox.getChildren().add(draftNameBar);
        teamsScreenBox.getChildren().add(teamsScreenToolbar);
        startingLineUpBox.getChildren().add(startingLineUpLabel);
        startingLineUpBox.getChildren().add(startingLineUpTable);
        teamsScreenBox.getChildren().add(startingLineUpBox);
        taxiSquadBox.getChildren().add(taxiSquadLabel);
        taxiSquadBox.getChildren().add(taxiSquadTable);
        teamsScreenBox.getChildren().add(taxiSquadBox);
        teamsScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        teamsScrollPane = new ScrollPane();
        teamsScrollPane.setContent(teamsScreenBox);
        teamsScrollPane.setFitToWidth(true);
        
        // NOW SETUP THE TABLE COLUMNS
        positionColumn_team = new TableColumn(COL_POSITION);
        firstNameColumn_team = new TableColumn(COL_FIRSTNAME);
        lastNameColumn_team = new TableColumn(COL_LASTNAME);
        proTeamColumn_team = new TableColumn(COL_PROTEAM);
        positionsColumn_team = new TableColumn(COL_POSITIONS);
        RWColumn_team = new TableColumn(COL_RW);
        HRSVColumn_team = new TableColumn(COL_HRSV);
        RBIKColumn_team = new TableColumn(COL_RBIK);
        SBERAColumn_team = new TableColumn(COL_SBERA);
        BAWHIPColumn_team = new TableColumn(COL_BAWHIP);
        //estimatedValueColumn_team = new TableColumn(COL_ESTIMATEDVALUE);
        contractColumn_team = new TableColumn(COL_CONTRACT);
        salaryColumn_team = new TableColumn(COL_SALARY);
        
        positionColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FANTASYPOSITION"));
        lastNameColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("LAST_NAME"));
        firstNameColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FIRST_NAME"));
        proTeamColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("TEAM"));
        positionsColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("Position"));
        RWColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("RW"));
        HRSVColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("HRSV"));
        RBIKColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("RBIK"));
        SBERAColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SBERA"));
        BAWHIPColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("BAWHIP"));
        //estimatedValueColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FANTASYPOSITION"));
        contractColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("CONTRACT"));
        salaryColumn_team.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SALARY"));
        
        startingLineUpTable.getColumns().add(positionColumn_team);
        startingLineUpTable.getColumns().add(firstNameColumn_team);
        startingLineUpTable.getColumns().add(lastNameColumn_team);
        startingLineUpTable.getColumns().add(proTeamColumn_team);
        startingLineUpTable.getColumns().add(positionsColumn_team);
        startingLineUpTable.getColumns().add(RWColumn_team);
        startingLineUpTable.getColumns().add(HRSVColumn_team);
        startingLineUpTable.getColumns().add(RBIKColumn_team);
        startingLineUpTable.getColumns().add(SBERAColumn_team);
        startingLineUpTable.getColumns().add(BAWHIPColumn_team);
        //startingLineUpTable.getColumns().add(estimatedValueColumn_team);
        startingLineUpTable.getColumns().add(contractColumn_team);
        startingLineUpTable.getColumns().add(salaryColumn_team);
        
        // NOW SETUP THE TABLE COLUMNS
        //positionColumn_taxi = new TableColumn(COL_POSITION);
        firstNameColumn_taxi = new TableColumn(COL_FIRSTNAME);
        lastNameColumn_taxi = new TableColumn(COL_LASTNAME);
        proTeamColumn_taxi = new TableColumn(COL_PROTEAM);
        positionsColumn_taxi = new TableColumn(COL_POSITIONS);
        RWColumn_taxi = new TableColumn(COL_RW);
        HRSVColumn_taxi = new TableColumn(COL_HRSV);
        RBIKColumn_taxi = new TableColumn(COL_RBIK);
        SBERAColumn_taxi = new TableColumn(COL_SBERA);
        BAWHIPColumn_taxi = new TableColumn(COL_BAWHIP);
        //estimatedValueColumn_taxi = new TableColumn(COL_ESTIMATEDVALUE);
        contractColumn_taxi = new TableColumn(COL_CONTRACT);
        salaryColumn_taxi = new TableColumn(COL_SALARY);
        
        //positionColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FANTASYPOSITION"));
        lastNameColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("LAST_NAME"));
        firstNameColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FIRST_NAME"));
        proTeamColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("TEAM"));
        positionsColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("Position"));
        RWColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("RW"));
        HRSVColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("HRSV"));
        RBIKColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("RBIK"));
        SBERAColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SBERA"));
        BAWHIPColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("BAWHIP"));
        //estimatedValueColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FANTASYPOSITION"));
        contractColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("CONTRACT"));
        salaryColumn_taxi.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SALARY"));
        
        //taxiSquadTable.getColumns().add(positionColumn_taxi);
        taxiSquadTable.getColumns().add(firstNameColumn_taxi);
        taxiSquadTable.getColumns().add(lastNameColumn_taxi);
        taxiSquadTable.getColumns().add(proTeamColumn_taxi);
        taxiSquadTable.getColumns().add(positionsColumn_taxi);
        taxiSquadTable.getColumns().add(RWColumn_taxi);
        taxiSquadTable.getColumns().add(HRSVColumn_taxi);
        taxiSquadTable.getColumns().add(RBIKColumn_taxi);
        taxiSquadTable.getColumns().add(SBERAColumn_taxi);
        taxiSquadTable.getColumns().add(BAWHIPColumn_taxi);
        //taxiSquadTable.getColumns().add(estimatedValueColumn_taxi);
        taxiSquadTable.getColumns().add(contractColumn_taxi);
        taxiSquadTable.getColumns().add(salaryColumn_taxi);
        //startingLineUpTable.setItems(dataManager.getDraft().getInitialPlayers());
        
        // NOW THE CONTROLS FOR DISPLAYING TEAMSTANDINGS
        standingsScreenBox = new VBox();
        standingsScreenBox.setPrefHeight(754);
        standingsScreenBox.setStyle("-fx-background-color: #FFFFFF;");
        standingsScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        standingsLabel = initLabel(WDK_PropertyType.STANDINGS_SCREEN_HEADING_LABEL, CLASS_HEADING_LABEL);
        standingsLabel.setPadding(new Insets(5,0,5,0));
        
        // NOW THE TABLEVIEW
        fantasyStandingsTable = new TableView();
        standingsScreenBox.getChildren().add(standingsLabel);
        standingsScreenBox.getChildren().add(fantasyStandingsTable);
        
        // NOW SETUP THE TABLE COLUMNS
        teamColumn_fantasyStandings = new TableColumn("Team Name");
        teamColumn_fantasyStandings.setMinWidth(120);
        playersNeededColumn_fantasyStandings = new TableColumn("Players Needed");
        playersNeededColumn_fantasyStandings.setMinWidth(120);
        moneyLeftColumn_fantasyStandings = new TableColumn("$ Left");
        moneyLeftColumn_fantasyStandings.setMinWidth(70);
        moneyPPColumn_fantasyStandings = new TableColumn("$ PP");
        moneyPPColumn_fantasyStandings.setMinWidth(70);
        RColumn_fantasyStandings = new TableColumn("R");
        RColumn_fantasyStandings.setMinWidth(70);
        HRColumn_fantasyStandings = new TableColumn("HR");
        HRColumn_fantasyStandings.setMinWidth(70);
        RBIColumn_fantasyStandings = new TableColumn("RBI");
        RBIColumn_fantasyStandings.setMinWidth(70);
        SBColumn_fantasyStandings = new TableColumn("SB");
        SBColumn_fantasyStandings.setMinWidth(70);
        BAColumn_fantasyStandings = new TableColumn("BA");
        BAColumn_fantasyStandings.setMinWidth(70);
        WColumn_fantasyStandings = new TableColumn("W");
        WColumn_fantasyStandings.setMinWidth(70);
        SVColumn_fantasyStandings = new TableColumn("SV");
        SVColumn_fantasyStandings.setMinWidth(70);
        KColumn_fantasyStandings = new TableColumn("K");
        KColumn_fantasyStandings.setMinWidth(70);
        ERAColumn_fantasyStandings = new TableColumn("ERA");
        ERAColumn_fantasyStandings.setMinWidth(70);
        WHIPColumn_fantasyStandings = new TableColumn("WHIP");
        WHIPColumn_fantasyStandings.setMinWidth(70);
        totalPointsColumn_fantasyStandings = new TableColumn("Total Points");
        totalPointsColumn_fantasyStandings.setMinWidth(100);
        
        teamColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, StringProperty>("name"));
        playersNeededColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("playersNeeded"));
        moneyLeftColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("moneyLeft"));
        moneyPPColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("moneyPP"));
        RColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("R"));
        HRColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("HR"));
        RBIColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("RBI"));
        SBColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("SB"));
        //BAColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, DoubleProperty>("BA"));
        BAColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, StringProperty>("BA_DISPLAY"));
        WColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("W"));
        SVColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("SV"));
        KColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("K"));
        //ERAColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, DoubleProperty>("ERA"));
        ERAColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, StringProperty>("ERA_DISPLAY"));
        //WHIPColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, DoubleProperty>("WHIP"));
        WHIPColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, StringProperty>("WHIP_DISPLAY"));
        totalPointsColumn_fantasyStandings.setCellValueFactory(new PropertyValueFactory<Team, IntegerProperty>("totalPoints"));
        
        fantasyStandingsTable.getColumns().add(teamColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(playersNeededColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(moneyLeftColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(moneyPPColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(RColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(HRColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(RBIColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(SBColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(BAColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(WColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(SVColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(KColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(ERAColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(WHIPColumn_fantasyStandings);
        fantasyStandingsTable.getColumns().add(totalPointsColumn_fantasyStandings);
        fantasyStandingsTable.setItems(dataManager.getDraft().getDraftedTeams());
        
        // NOW THE CONTROLS FOR DISPLAYING DRAFT
        draftScreenBox = new VBox();
        draftScreenBox.setPrefHeight(754);
        draftScreenBox.setStyle("-fx-background-color: #FFFFFF;");
        draftControlBar = new HBox();
        draftScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        draftLabel = initLabel(WDK_PropertyType.DRAFT_SCREEN_HEADING_LABEL, CLASS_HEADING_LABEL);
        draftLabel.setPadding(new Insets(5,0,5,0));
        selectPlayerButton = initChildButton(draftControlBar, WDK_PropertyType.SELECT_PLAYER_ICON, WDK_PropertyType.SELECT_PLAYER_TOOLTIP, false);
        startAutoDraftButton = initChildButton(draftControlBar, WDK_PropertyType.START_AUTO_DRAFT_ICON, WDK_PropertyType.START_AUTO_DRAFT_TOOLTIP, false);
        pauseAutoDraftButton = initChildButton(draftControlBar, WDK_PropertyType.PAUSE_AUTO_DRAFT_ICON, WDK_PropertyType.PAUSE_AUTO_DRAFT_TOOLTIP, false);
        
        // NOW THE TABLEVIEW
        draftTable = new TableView();
        draftScreenBox.getChildren().add(draftLabel);
        draftScreenBox.getChildren().add(draftControlBar);
        draftScreenBox.getChildren().add(draftTable);
        
        // NOW SETUP THE TABLE COLUMNS
        draftPickColumn_draft = new TableColumn("Pick#");
        firstNameColumn_draft = new TableColumn(COL_FIRSTNAME);
        lastNameColumn_draft = new TableColumn(COL_LASTNAME);
        fantasyTeamColumn_draft = new TableColumn("Team");
        contractColumn_draft = new TableColumn(COL_CONTRACT);
        salaryColumn_draft = new TableColumn("Salary($)");
        
        draftPickColumn_draft.setCellValueFactory(new Callback<CellDataFeatures<Player, String>, ObservableValue<String>>() {
            @Override 
            public ObservableValue<String> call(CellDataFeatures<Player, String> p) {
                return new ReadOnlyObjectWrapper(draftTable.getItems().indexOf(p.getValue()) + 1);
            }
        });  
        //draftPickColumn_draft.setCellValueFactory(cb -> new SimpleStringProperty(cb.getValue()));
        firstNameColumn_draft.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FIRST_NAME"));
        lastNameColumn_draft.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("LAST_NAME"));
        fantasyTeamColumn_draft.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FANTASYTEAM"));
        contractColumn_draft.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("CONTRACT"));
        salaryColumn_draft.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("SALARY"));
        
        draftTable.getColumns().add(draftPickColumn_draft);
        draftTable.getColumns().add(firstNameColumn_draft);
        draftTable.getColumns().add(lastNameColumn_draft);
        draftTable.getColumns().add(fantasyTeamColumn_draft);
        draftTable.getColumns().add(contractColumn_draft);
        draftTable.getColumns().add(salaryColumn_draft);
        draftTable.setItems(dataManager.getDraft().getDraftSummaryPlayers());
        
        // NOW THE CONTROLS FOR DISPLAYING MLBTEAM
        MLBScreenBox = new VBox();
        MLBScreenBox.setPrefHeight(754);
        MLBScreenBox.setStyle("-fx-background-color: #FFFFFF;");
        selectMLBBar = new HBox();
        MLBScreenBox.getStyleClass().add(CLASS_SCREENVBOXBORDER);
        MLBLabel = initLabel(WDK_PropertyType.MLB_SCREEN_HEADING_LABEL, CLASS_HEADING_LABEL);
        MLBLabel.setPadding(new Insets(5,0,5,0));
        selectMLBLabel = initChildLabel(selectMLBBar, WDK_PropertyType.DRAFT_NAME_LABEL, CLASS_NORMAL_LABEL);
        selectMLBComboBox = initChildComboBox(selectMLBBar);
        
        // NOW THE TABLEVIEW
        MLBTable = new TableView();
        MLBScreenBox.getChildren().add(MLBLabel);
        MLBScreenBox.getChildren().add(selectMLBBar);
        MLBScreenBox.getChildren().add(MLBTable);
          
        // NOW SETUP THE TABLE COLUMNS
        firstNameColumn_MLB = new TableColumn(COL_FIRSTNAME);
        lastNameColumn_MLB = new TableColumn(COL_LASTNAME);
        positionsColumn_MLB = new TableColumn(COL_POSITIONS);
        
        firstNameColumn_MLB.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("FIRST_NAME"));
        lastNameColumn_MLB.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("LAST_NAME"));
        positionsColumn_MLB.setCellValueFactory(new PropertyValueFactory<Player, StringProperty>("Position"));
        
        MLBTable.getColumns().add(firstNameColumn_MLB);
        MLBTable.getColumns().add(lastNameColumn_MLB);
        MLBTable.getColumns().add(positionsColumn_MLB);
        
        // NOW LET'S ASSEMBLE ALL THE CONTAINERS TOGETHER

        // THIS IS FOR STUFF IN THE TOP OF THE SCHEDULE PANE, WE NEED TO PUT TWO THINGS INSIDE
//        scheduleInfoPane = new VBox();
//
//        // FIRST OUR SCHEDULE HEADER
//        scheduleInfoHeadingLabel = initChildLabel(scheduleInfoPane, WDK_PropertyType.SCHEDULE_HEADING_LABEL, CLASS_HEADING_LABEL);
//
//        // AND THEN THE SPLIT PANE
//        scheduleInfoPane.getChildren().add(splitScheduleInfoPane);
//
//        // FINALLY, EVERYTHING IN THIS REGION ULTIMATELY GOES INTO schedulePane
//        schedulePane = new VBox();
//        //schedulePane.getChildren().add(scheduleInfoPane);***************************************************
//        //schedulePane.getChildren().add(scheduleItemsBox);
//        //schedulePane.getChildren().add(lecturesBox);
//        //schedulePane.getChildren().add(assignmentsBox);
//        schedulePane.getStyleClass().add(CLASS_BORDERED_PANE);
    }

    
    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Course IS CREATED OR LOADED
    private void initWindow(String windowTitle) {
        // SET THE WINDOW TITLE
        primaryStage.setTitle(windowTitle);

        // GET THE SIZE OF THE SCREEN
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        csbPane = new BorderPane();
        csbPane.setTop(fileToolbarPane);
        primaryScene = new Scene(csbPane);

        // NOW TIE THE SCENE TO THE WINDOW, SELECT THE STYLESHEET
        // WE'LL USE TO STYLIZE OUR GUI CONTROLS, AND OPEN THE WINDOW
        primaryScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    // INIT ALL THE EVENT HANDLERS
    private void initEventHandlers() throws IOException {
        // FIRST THE FILE CONTROLS
        fileController = new FileController(messageDialog, yesNoCancelDialog, wolfieballFileManager, siteExporter);
        newCourseButton.setOnAction(e -> {
            fileController.handleNewCourseRequest(this);
        });
        loadCourseButton.setOnAction(e -> {
            fileController.handleLoadCourseRequest(this);
        });
        saveCourseButton.setOnAction(e -> {
            fileController.handleSaveCourseRequest(this, dataManager.getDraft());
        });
        exportSiteButton.setOnAction(e -> {
            //fileController.handleProgressBarRequest(this);
            //fileController.handleExportCourseRequest(this);
        });            
        exitButton.setOnAction(e -> {
            fileController.handleExitRequest(this);
        });
        
        // SWITCH SCREENS
        playersScreenButton.setOnAction(e -> {
            workspacePane.setCenter(playersScreenBox);
        });
        teamsScreenButton.setOnAction(e -> {
            workspacePane.setCenter(teamsScrollPane);
        });
        standingsScreenButton.setOnAction(e -> {
            workspacePane.setCenter(standingsScreenBox);
        });
        draftScreenButton.setOnAction(e -> {
            workspacePane.setCenter(draftScreenBox);
        });            
        MLBScreenButton.setOnAction(e -> {
            workspacePane.setCenter(MLBScreenBox);
        });

        // THEN THE COURSE EDITING CONTROLS
        playersScreenUpdateController = new PlayersScreenUpdateController();

        // TEXT FIELDS HAVE A DIFFERENT WAY OF LISTENING FOR TEXT CHANGES
        registerTextFieldController(searchTextField);
        registerTextFieldControllerForDraftName(draftNameTextField);
        
        allRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        CRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        _1BRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        CIRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        _3BRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        _2BRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        MIRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        SSRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        OFRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        URadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        PRadioButton.setOnAction(e -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
        playersTable.setOnMouseClicked(e -> {
            //if (e.getClickCount() == 2) {
                // SET EDITABLE
                //System.out.println("*****");
                playersTable.setEditable(true);
                //notesColumn.setEditable(true);
                notesColumn.setCellFactory(TextFieldTableCell.forTableColumn());
                notesColumn.setOnEditCommit(
                    new EventHandler<CellEditEvent<Player, String>>() {
                        @Override
                        public void handle(CellEditEvent<Player, String> t) {
                            ((Player)t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                                ).setNOTES(t.getNewValue());
                        }
                    }
                );

                
            //}
        });
        
        // AND NOW THE SCHEDULE ITEM ADDING AND EDITING CONTROLS
        scheduleController = new ScheduleEditController(primaryStage, dataManager.getDraft(), messageDialog, yesNoCancelDialog);
        addTeamButton.setOnAction(e -> {
            scheduleController.handleAddTeamRequest(this);
        });
        removeTeamButton.setOnAction(e -> {
            scheduleController.handleRemoveTeamRequest(this);
        });
        editTeamButton.setOnAction(e -> {
            for(int i = 0; i < this.getDataManager().getDraft().getDraftedTeams().size(); i++){
                if (this.getDataManager().getDraft().getDraftedTeams().get(i).getName().equals(this.getComboBox().getSelectionModel().getSelectedItem().toString())){
                    scheduleController.handleEditTeamRequest(this, this.getDataManager().getDraft().getDraftedTeams().get(i));
                    break;
                }
            }
        });
        addPlayerButton.setOnAction(e -> {
            scheduleController.handleAddPlayerRequest(this);
        });
        removePlayerButton.setOnAction(e -> {
            scheduleController.handleRemovePlayerRequest(this,playersTable.getSelectionModel().getSelectedItem());
        });
        playersTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // OPEN UP THE SCHEDULE ITEM EDITOR
                Player p = playersTable.getSelectionModel().getSelectedItem();
                try {
                    scheduleController.handleEditPlayerRequest(this, p);
                } catch (FileNotFoundException ex) {
                    System.out.print("Error!");
                }
            }
        });
        selectFantasyTeamComboBox.setOnAction(e -> {
            scheduleController.handleFantasyTeamChangeRequest(this);
        });
        startingLineUpTable.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // OPEN UP THE SCHEDULE ITEM EDITOR
                Player p = startingLineUpTable.getSelectionModel().getSelectedItem();
                try {
                    scheduleController.handleEditPlayerInFTRequest(this, p);
                } catch (FileNotFoundException ex) {
                    System.err.print("Error!");
                }
            }
        });
        selectMLBComboBox.setOnAction(e -> {
            scheduleController.handleMLBChangeRequest(this);
        });
        selectPlayerButton.setOnAction(e -> {
           fileController.handleSelectPlayerRequest(this);
        });
        startAutoDraftButton.setOnAction(e -> {
           fileController.handleStartAutoDraftRequest(this);
        });
        pauseAutoDraftButton.setOnAction(e -> {
           fileController.handlePauseAutoDraftRequest(this);
        });
    }

    // REGISTER THE EVENT LISTENER FOR A TEXT FIELD
    private void registerTextFieldController(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            playersScreenUpdateController.handleCourseChangeRequest(this);
        });
    }
    
    // REGISTER THE EVENT LISTENER FOR A TEXT FIELD
    private void registerTextFieldControllerForDraftName(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            this.getDataManager().getDraft().setName(newValue);
        });
    }
    
    // INIT A BUTTON AND ADD IT TO A CONTAINER IN A TOOLBAR
    private Button initChildButton(Pane toolbar, WDK_PropertyType icon, WDK_PropertyType tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(icon.toString());
        Image buttonImage = new Image(imagePath);
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
        button.setTooltip(buttonTooltip);
        toolbar.getChildren().add(button);
        return button;
    }
    
    // INIT A BUTTON AND ADD IT TO A CONTAINER IN A TOOLBAR
    private RadioButton initChildRadioButton(Pane radioButtonBar, boolean disabled, ToggleGroup group) {
        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(group);
        radioButton.setDisable(disabled);
        radioButtonBar.getChildren().add(radioButton);
        return radioButton;
    }
    
    // INIT A LABEL AND SET IT'S STYLESHEET CLASS
    private Label initLabel(WDK_PropertyType labelProperty, String styleClass) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String labelText = props.getProperty(labelProperty);
        Label label = new Label(labelText);
        label.getStyleClass().add(styleClass);
        return label;
    }

    // INIT A LABEL AND PLACE IT IN A GridPane INIT ITS PROPER PLACE
    private Label initGridLabel(GridPane container, WDK_PropertyType labelProperty, String styleClass, int col, int row, int colSpan, int rowSpan) {
        Label label = initLabel(labelProperty, styleClass);
        container.add(label, col, row, colSpan, rowSpan);
        return label;
    }

    // INIT A LABEL AND PUT IT IN A TOOLBAR
    private Label initChildLabel(Pane container, WDK_PropertyType labelProperty, String styleClass) {
        Label label = initLabel(labelProperty, styleClass);
        container.getChildren().add(label);
        return label;
    }
    
    // INIT A LABEL AND PUT IT IN A TOOLBAR
    private TextField initChildTextField(Pane container) {
        TextField textField = new TextField();
        container.getChildren().add(textField);
        textField.setEditable(true);
        textField.setPrefColumnCount(67);
        textField.setStyle("-fx-font-size: 14pt");
        return textField;
    }

    // INIT A COMBO BOX AND PUT IT IN A GridPane
    private ComboBox initGridComboBox(GridPane container, int col, int row, int colSpan, int rowSpan) throws IOException {
        ComboBox comboBox = new ComboBox();
        container.add(comboBox, col, row, colSpan, rowSpan);
        return comboBox;
    }
    
    // INIT A COMBO BOX AND PUT IT IN A GridPane
    private ComboBox initChildComboBox(Pane container) {
        ComboBox comboBox = new ComboBox();
        container.getChildren().add(comboBox);
        return comboBox;
    }

    // INIT A TEXT FIELD AND PUT IT IN A GridPane
    private TextField initGridTextField(GridPane container, int size, String initText, boolean editable, int col, int row, int colSpan, int rowSpan) {
        TextField tf = new TextField();
        tf.setPrefColumnCount(size);
        tf.setText(initText);
        tf.setEditable(editable);
        container.add(tf, col, row, colSpan, rowSpan);
        return tf;
    }

    // INIT A DatePicker AND PUT IT IN A GridPane
    private DatePicker initGridDatePicker(GridPane container, int col, int row, int colSpan, int rowSpan) {
        DatePicker datePicker = new DatePicker();
        container.add(datePicker, col, row, colSpan, rowSpan);
        return datePicker;
    }

    // INIT A CheckBox AND PUT IT IN A TOOLBAR
    private CheckBox initChildCheckBox(Pane container, String text) {
        CheckBox cB = new CheckBox(text);
        container.getChildren().add(cB);
        return cB;
    }

    // INIT A DatePicker AND PUT IT IN A CONTAINER
    private DatePicker initChildDatePicker(Pane container) {
        DatePicker dp = new DatePicker();
        container.getChildren().add(dp);
        return dp;
    }
    
    // LOADS CHECKBOX DATA INTO A Course OBJECT REPRESENTING A CoursePage
    private void updatePageUsingCheckBox(CheckBox cB, Course course, CoursePage cP) {
        if (cB.isSelected()) {
            course.selectPage(cP);
        } else {
            course.unselectPage(cP);
        }
    }    
    
    public ComboBox getComboBox(){
        return selectFantasyTeamComboBox;
    }
    
    public Button getRemoveTeamButton(){
        return removeTeamButton;
    }
    
    public Button getEditTeamButton(){
        return editTeamButton;
    }
    
    public Button getRemovePlayerButton(){
        return removePlayerButton;
    }
    
    public TableView getStartingLineUpTable(){
        return startingLineUpTable;
    }
    
    public TableView getTaxiSquadTable(){
        return taxiSquadTable;
    }
    
    public boolean getWorkspaceActivated(){
        return workspaceActivated;
    }
    
    public void setWorkspaceActivated(boolean initWorkspaceActivated){
        workspaceActivated = initWorkspaceActivated;
    }
    
    public ComboBox getSelectMLBComboBox (){
        return selectMLBComboBox;
    }
    
    public TableView getMLBTable(){
        return MLBTable;
    }
}
