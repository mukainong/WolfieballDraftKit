package wdk.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wdk.WDK_PropertyType;
import wdk.data.Draft;
import wdk.data.ScheduleItem;
import wdk.data.Team;
import wdk.data.Player;
import static wdk.gui.WDK_GUI.CLASS_HEADING_LABEL;
import static wdk.gui.WDK_GUI.CLASS_PROMPT_LABEL;
import static wdk.gui.WDK_GUI.PRIMARY_STYLE_SHEET;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 *
 * @author Mukai Nong
 */
public class PlayerDialog  extends Stage {
    // THIS IS THE OBJECT DATA BEHIND THIS UI
    Player player;
    
    // GUI CONTROLS FOR OUR DIALOG
    GridPane gridPane;
    Scene dialogScene;
    Label headingLabel;
    Label firstNameLabel;
    TextField firstNameTextField;
    Label lastNameLabel;
    TextField lastNameTextField;
    Label proTeamLabel;
    ComboBox proTeamComboBox;
    CheckBox C_CheckBox;
    Label C_Label;
    CheckBox _1B_CheckBox;
    Label _1B_Label;
    CheckBox _3B_CheckBox;
    Label _3B_Label;
    CheckBox _2B_CheckBox;
    Label _2B_Label;
    CheckBox SS_CheckBox;
    Label SS_Label;
    CheckBox OF_CheckBox;
    Label OF_Label;
    CheckBox P_CheckBox;
    Label P_Label;
    Button completeButton;
    Button cancelButton;
    
    // THIS IS FOR KEEPING TRACK OF WHICH BUTTON THE USER PRESSED
    String selection;
    
    // CONSTANTS FOR OUR UI
    public static final String COMPLETE = "Complete";
    public static final String CANCEL = "Cancel";
    public static final String FIRSTNAME_PROMPT = "First Name: ";
    public static final String LASTNAME_PROMPT = "Last Owner: ";
    public static final String PROTEAM_PROMPT = "Pro Team: ";
    public static final String LECTURE_HEADING = "Player Details";
    public static final String ADD_TEAM_TITLE = "Add New team";
    public static final String EDIT_TEAM_TITLE = "Edit team";
    /**
     * Initializes this dialog so that it can be used for either adding
     * new lectures or editing existing ones.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    public PlayerDialog(Stage primaryStage, Draft draft,  MessageDialog messageDialog) {       
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        initModality(Modality.WINDOW_MODAL);
        initOwner(primaryStage);
        
        // FIRST OUR CONTAINER
        gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 20, 20, 20));
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        
        // PUT THE HEADING IN THE GRID, NOTE THAT THE TEXT WILL DEPEND
        // ON WHETHER WE'RE ADDING OR EDITING
        headingLabel = new Label(LECTURE_HEADING);
        headingLabel.getStyleClass().add(CLASS_HEADING_LABEL);
    
        // NOW THE FIRST NAME
        firstNameLabel = new Label(FIRSTNAME_PROMPT);
        firstNameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        firstNameTextField = new TextField();
        firstNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            player.setFIRST_NAME(newValue);
        });
        
        // AND THE LAST NAME
        lastNameLabel = new Label(LASTNAME_PROMPT);
        lastNameLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        lastNameTextField = new TextField();
        lastNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            player.setLAST_NAME(newValue);
        });
        
        // AND THE PRO TEAM
        proTeamLabel = new Label(PROTEAM_PROMPT);
        proTeamLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        proTeamComboBox = new ComboBox();
        proTeamComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            player.setTEAM(proTeamComboBox.getSelectionModel().getSelectedItem().toString());
        });
        
        // AND THE C CHECKBOX
        C_Label = new Label("C");
        C_CheckBox = new CheckBox();
        C_CheckBox.setOnAction(e -> {
            player.setPosition("C");
        });
        
        // AND THE C CHECKBOX
        _1B_Label = new Label("1B");
        _1B_CheckBox = new CheckBox();
        _1B_CheckBox.setOnAction(e -> {
            player.setPosition("1B");
        });
        
        // AND THE C CHECKBOX
        _3B_Label = new Label("3B");
        _3B_CheckBox = new CheckBox();
        _3B_CheckBox.setOnAction(e -> {
            player.setPosition("3B");
        });
        
        // AND THE C CHECKBOX
        _2B_Label = new Label("2B");
        _2B_CheckBox = new CheckBox();
        _2B_CheckBox.setOnAction(e -> {
            player.setPosition("2B");
        });
        
        // AND THE C CHECKBOX
        SS_Label = new Label("SS");
        SS_CheckBox = new CheckBox();
        SS_CheckBox.setOnAction(e -> {
            player.setPosition("SS");
        });
        
        // AND THE C CHECKBOX
        OF_Label = new Label("OF");
        OF_CheckBox = new CheckBox();
        OF_CheckBox.setOnAction(e -> {
            player.setPosition("OF");
        });
        
        // AND THE C CHECKBOX
        P_Label = new Label("P");
        P_CheckBox = new CheckBox();
        P_CheckBox.setOnAction(e -> {
            player.setPosition("P");
        });
        
        // AND FINALLY, THE BUTTONS
        completeButton = new Button(COMPLETE);
        cancelButton = new Button(CANCEL);
        
        // REGISTER EVENT HANDLERS FOR OUR BUTTONS
        EventHandler completeCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            PlayerDialog.this.selection = sourceButton.getText();
            PlayerDialog.this.hide();
        };
        completeButton.setOnAction(completeCancelHandler);
        cancelButton.setOnAction(completeCancelHandler);

        // NOW LET'S ARRANGE THEM ALL AT ONCE
        gridPane.add(headingLabel, 0, 0, 2, 1);
        gridPane.add(firstNameLabel, 0, 1, 1, 1);
        gridPane.add(firstNameTextField, 1, 1, 1, 1);
        gridPane.add(lastNameLabel, 0, 2, 1, 1);
        gridPane.add(lastNameTextField, 1, 2, 1, 1);
        gridPane.add(proTeamLabel, 0, 3, 1, 1);
        gridPane.add(proTeamComboBox, 1, 3, 1, 1);
        gridPane.add(C_CheckBox, 0, 4, 1, 1);
        gridPane.add(C_Label, 1, 4, 1, 1);
        gridPane.add(_1B_CheckBox, 2, 4, 1, 1);
        gridPane.add(_1B_Label, 3, 4, 1, 1);
        gridPane.add(_3B_CheckBox, 4, 4, 1, 1);
        gridPane.add(_3B_Label, 5, 4, 1, 1);
        gridPane.add(_2B_CheckBox, 6, 4, 1, 1);
        gridPane.add(_2B_Label, 7, 4, 1, 1);
        gridPane.add(SS_CheckBox, 8, 4, 1, 1);
        gridPane.add(SS_Label, 9, 4, 1, 1);
        gridPane.add(OF_CheckBox, 10, 4, 1, 1);
        gridPane.add(OF_Label, 11, 4, 1, 1);
        gridPane.add(P_CheckBox, 12, 4, 1, 1);
        gridPane.add(P_Label, 13, 4, 1, 1);
        gridPane.add(completeButton, 0, 6, 1, 1);
        gridPane.add(cancelButton, 1, 6, 1, 1);

        // AND PUT THE GRID PANE IN THE WINDOW
        dialogScene = new Scene(gridPane);
        dialogScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        this.setScene(dialogScene);
    }
    
    /**
     * Accessor method for getting the selection the user made.
     * 
     * @return Either YES, NO, or CANCEL, depending on which
     * button the user selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }
    
    public Player getPlayer() { 
        return player;
    }
    
    /**
     * This method loads a custom message into the label and
     * then pops open the dialog.
     * 
     * @param message Message to appear inside the dialog.
     */
    public Player showAddPlayerDialog(WDK_GUI gui) {
        // SET THE DIALOG TITLE
        setTitle(ADD_TEAM_TITLE);
        
        // RESET THE SCHEDULE ITEM OBJECT WITH DEFAULT VALUES
        player = new Player();
        
        // LOAD THE UI STUFF
        firstNameTextField.setText(player.getFIRST_NAME());
        lastNameTextField.setText(player.getLAST_NAME());
        
        C_CheckBox.setSelected(false);
        _1B_CheckBox.setSelected(false);
        _3B_CheckBox.setSelected(false);
        _2B_CheckBox.setSelected(false);
        SS_CheckBox.setSelected(false);
        OF_CheckBox.setSelected(false);
        P_CheckBox.setSelected(false);
        
        proTeamComboBox.getItems().clear();
        proTeamComboBox.setValue("");
        // LOAD SESSIONS VALUES
        ObservableList<String> teamName = FXCollections.observableArrayList();
        for (int i = 0; i < gui.getDataManager().getDraft().getInitialPlayers().size(); i++) {
            if(!teamName.contains(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString())){
                teamName.add(gui.getDataManager().getDraft().getInitialPlayers().get(i).getTEAM().toString());
            }
        }
        proTeamComboBox.setItems(teamName);
        
        // AND OPEN IT UP
        this.showAndWait();
        
        return player;
    }
    
    public void loadGUIData() {
        // LOAD THE UI STUFF
        firstNameTextField.setText(player.getFIRST_NAME());
        lastNameTextField.setText(player.getLAST_NAME());   
    }
    
    public boolean wasCompleteSelected() {
        return selection.equals(COMPLETE);
    }
    
    public void showEditPlayerDialog(Player playerToEdit) {
        // SET THE DIALOG TITLE
        setTitle(EDIT_TEAM_TITLE);
        
        // LOAD THE LECTURE INTO OUR LOCAL OBJECT
        player = new Player();
        player.setFIRST_NAME(playerToEdit.getFIRST_NAME());
        player.setLAST_NAME(playerToEdit.getLAST_NAME());
        
        // AND THEN INTO OUR GUI
        loadGUIData();
               
        // AND OPEN IT UP
        this.showAndWait();
    }
}