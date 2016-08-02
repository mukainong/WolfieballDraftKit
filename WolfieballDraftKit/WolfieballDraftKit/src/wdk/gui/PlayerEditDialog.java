package wdk.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wdk.WDK_PropertyType;
import wdk.data.Draft;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import static wdk.gui.WDK_GUI.CLASS_SUBHEADING_LABEL;

/**
 *
 * @author Mukai Nong
 */
public class PlayerEditDialog  extends Stage {
    // THIS IS THE OBJECT DATA BEHIND THIS UI
    Player player;
    String teamSelected;
    Player currentPlayer;
    // GUI CONTROLS FOR OUR DIALOG
    GridPane gridPane;
    Scene dialogScene;
    Label headingLabel;
    Image image;
    Image imageFlag;
    ImageView iv;
    ImageView ivFlag;
    Label playerName;
    Label position;
    Label fantasyTeamLabel;
    ComboBox fantasyTeamComboBox;
    Label positionLabel;
    ComboBox positionComboBox = new ComboBox();
    Label contractLabel;
    ComboBox contractComboBox = new ComboBox();
    Label salaryLabel;
    TextField salaryTextField = new TextField();
    Button completeButton;
    Button cancelButton;
    
    // THIS IS FOR KEEPING TRACK OF WHICH BUTTON THE USER PRESSED
    String selection;
    
    // CONSTANTS FOR OUR UI
    public static final String COMPLETE = "Complete";
    public static final String CANCEL = "Cancel";
    public static final String FANTASYTEAM_PROMPT = "Fantasy Team: ";
    public static final String POSITION_PROMPT = "Position: ";
    public static final String CONTRACT_PROMPT = "Contract: ";
    public static final String SALARY_PROMPT = "Salary($): ";
    public static final String LECTURE_HEADING = "Player Details";
    public static final String ADD_TEAM_TITLE = "Add New team";
    public static final String EDIT_TEAM_TITLE = "Edit team";
    /**
     * Initializes this dialog so that it can be used for either adding
     * new lectures or editing existing ones.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    public PlayerEditDialog(Stage primaryStage, Draft draft,  MessageDialog messageDialog) {       
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
    
        // PICTURE
        iv = new ImageView();
        ivFlag = new ImageView();
        //iv.setImage(image);
        
        playerName = new Label();
        playerName.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        position = new Label();
        position.getStyleClass().add(CLASS_PROMPT_LABEL);
        
        positionComboBox.setDisable(true);
        contractComboBox.setDisable(true);
        salaryTextField.setDisable(true);
        
        // NOW THE FIRST NAME
        fantasyTeamLabel = new Label(FANTASYTEAM_PROMPT);
        //fantasyTeamLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        fantasyTeamComboBox = new ComboBox();
        fantasyTeamComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            positionComboBox.setDisable(false);
            
            String currentTeam = currentPlayer.getFANTASYTEAM();
            player.setFANTASYTEAM(String.valueOf(fantasyTeamComboBox.getSelectionModel().getSelectedItem()));
            teamSelected = String.valueOf(fantasyTeamComboBox.getSelectionModel().getSelectedItem());
            
            positionComboBox.getItems().clear();
        ObservableList<String> position = FXCollections.observableArrayList();
        String[] parts = currentPlayer.getPosition().split("_");
        int counter_CI=0;
        int counter_MI=0;
        int counter_U=0;
        int totalStartingLineUpPlayers = 0;
        for(int i = 0; i < draft.getDraftedTeams().size(); i++){
            totalStartingLineUpPlayers += draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size();
        }
        for(int i = 0; i < parts.length; i++){
            position.add(parts[i]);
            for(int j = 0; j < draft.getDraftedTeams().size(); j++){
                //if(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName().equals(String.valueOf(fantasyTeamComboBox.getSelectionModel().getSelectedItem()))){
                if(draft.getDraftedTeams().get(j).getName().equals(teamSelected)){
                    if(totalStartingLineUpPlayers == draft.getDraftedTeams().size()*23 && draft.getDraftedTeams().get(j).getTaxiSquadPlayers().size()<8 && i <1){
                        positionComboBox.getItems().add("Taxi Squad");
                    }
                    System.out.print("->->"+currentPlayer.getFANTASYPOSITION());
                    if(i < 1 && currentTeam.equals(teamSelected) && ((draft.getDraftedTeams().get(j).getPList().size() == 9 && currentPlayer.getFANTASYPOSITION().equals("P"))||(draft.getDraftedTeams().get(j).getOFList().size() == 5 && currentPlayer.getFANTASYPOSITION().equals("OF")) || (draft.getDraftedTeams().get(j).getCList().size() == 2 && currentPlayer.getFANTASYPOSITION().equals("C"))||currentPlayer.getFANTASYPOSITION().equals("1B")||currentPlayer.getFANTASYPOSITION().equals("CI")||currentPlayer.getFANTASYPOSITION().equals("3B")||currentPlayer.getFANTASYPOSITION().equals("2B")||currentPlayer.getFANTASYPOSITION().equals("MI")||currentPlayer.getFANTASYPOSITION().equals("SS")||currentPlayer.getFANTASYPOSITION().equals("U"))){
                        positionComboBox.getItems().add(currentPlayer.getFANTASYPOSITION());
                    }
                    if(parts[i].equals("C")){
                        if(draft.getDraftedTeams().get(j).getCList().size()<2){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U<1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("1B")){
                        if(draft.getDraftedTeams().get(j).get1BList().size()<1){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getCIList().size()<1 && counter_CI<1){
                            positionComboBox.getItems().add("CI");
                            counter_CI++;
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U<1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("CI")&&draft.getDraftedTeams().get(j).getCIList().size()<1){
                        positionComboBox.getItems().add(parts[i]);
                    }
                    if(parts[i].equals("3B")){
                        if(draft.getDraftedTeams().get(j).get3BList().size()<1){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getCIList().size()<1 && counter_CI<1){
                            positionComboBox.getItems().add("CI");
                            counter_CI++;
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U<1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("2B")){
                        if(draft.getDraftedTeams().get(j).get2BList().size()<1){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getMIList().size()<1 && counter_MI<1){
                            positionComboBox.getItems().add("MI");
                            counter_MI++;
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U <1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("MI")&&draft.getDraftedTeams().get(j).getMIList().size()<1){
                        positionComboBox.getItems().add(parts[i]);
                    }
                    if(parts[i].equals("SS")){
                        if(draft.getDraftedTeams().get(j).getSSList().size()<1){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getMIList().size()<1 && counter_MI<1){
                            positionComboBox.getItems().add("MI");
                            counter_MI++;
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U < 1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("U")&&draft.getDraftedTeams().get(j).getUList().size()<1){
                        positionComboBox.getItems().add(parts[i]);
                    }
                    if(parts[i].equals("OF")){
                        if(draft.getDraftedTeams().get(j).getOFList().size()<5){
                            positionComboBox.getItems().add(parts[i]);
                        }
                        if(draft.getDraftedTeams().get(j).getUList().size()<1 && counter_U <1){
                            positionComboBox.getItems().add("U");
                            counter_U++;
                        }
                    }
                    if(parts[i].equals("P")&&draft.getDraftedTeams().get(j).getPList().size()<9){
                        positionComboBox.getItems().add(parts[i]);
                    }
                    break;
                }
                //break;
            }
//            if(gui.getDataManager().getDraft().getDraftedTeams().get(i)){
//                positionComboBox.getItems().add(parts[i]);
//            }
        }
        });
        
        // AND THE LAST NAME
        positionLabel = new Label(POSITION_PROMPT);
        //positionLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        //positionComboBox = new ComboBox();
        positionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            contractComboBox.setDisable(false);
            player.setFANTASYPOSITION(String.valueOf(positionComboBox.getSelectionModel().getSelectedItem()));
        });
        
        // AND THE PRO TEAM
        contractLabel = new Label(CONTRACT_PROMPT);
        //contractLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        //contractComboBox = new ComboBox();
        contractComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            salaryTextField.setDisable(false);
            player.setCONTRACT(contractComboBox.getSelectionModel().getSelectedItem().toString());
        });
        
        // AND THE LAST NAME
        salaryLabel = new Label(SALARY_PROMPT);
        //salaryLabel.getStyleClass().add(CLASS_PROMPT_LABEL);
        //salaryTextField = new TextField();
        salaryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            player.setSALARY(Integer.valueOf(newValue));
        });
        
        // AND FINALLY, THE BUTTONS
        completeButton = new Button(COMPLETE);
        cancelButton = new Button(CANCEL);
        
        // REGISTER EVENT HANDLERS FOR OUR BUTTONS
        EventHandler completeCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
                PlayerEditDialog.this.selection = sourceButton.getText();
                PlayerEditDialog.this.hide();
        };
        completeButton.setOnAction((ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            int cap = 0;
            for(int i =0; i < draft.getDraftedTeams().size(); i++){
                if(draft.getDraftedTeams().get(i).getName().equals(player.getFANTASYTEAM())){
                    cap = draft.getDraftedTeams().get(i).getMoneyLeft() - player.getSALARY() - draft.getDraftedTeams().get(i).getPlayersNeeded() - 1;
                }
            }
            if(player.getFANTASYTEAM().equals("Free Agent") || (!player.getFANTASYTEAM().equals("N/A") && !player.getFANTASYPOSITION().equals("N/A") && !player.getCONTRACT().equals("N/A") && player.getSALARY()!=0 && cap >=0)){
                PlayerEditDialog.this.selection = sourceButton.getText();
                PlayerEditDialog.this.hide();
//                player.setFANTASYTEAM("N/A");
//                player.setFANTASYPOSITION("N/A");
//                player.setCONTRACT("N/A");
//                player.setSALARY(0);
            }
        });
        cancelButton.setOnAction(completeCancelHandler);

        // NOW LET'S ARRANGE THEM ALL AT ONCE
        gridPane.add(headingLabel, 0, 0, 2, 1);
        gridPane.add(iv, 0, 1, 1, 1);
        gridPane.add(ivFlag, 1, 1, 1, 1);
        gridPane.add(playerName, 1, 1, 1, 5);
        gridPane.add(position, 1, 1, 1, 7);
        gridPane.add(fantasyTeamLabel, 0, 4, 1, 1);
        gridPane.add(fantasyTeamComboBox, 1, 4, 1, 1);
        gridPane.add(positionLabel, 0, 5, 1, 1);
        gridPane.add(positionComboBox, 1, 5, 1, 1);
        gridPane.add(contractLabel, 0, 6, 1, 1);
        gridPane.add(contractComboBox, 1, 6, 1, 1);
        gridPane.add(salaryLabel, 0, 7, 1, 1);
        gridPane.add(salaryTextField, 1, 7, 1, 1);
        gridPane.add(completeButton, 0, 8, 1, 1);
        gridPane.add(cancelButton, 1, 8, 1, 1);

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
    public Player showEditPlayerDialog(WDK_GUI gui, Player playerToEdit) throws FileNotFoundException {
        currentPlayer = playerToEdit;
        
        // SET THE DIALOG TITLE
        setTitle(ADD_TEAM_TITLE);
        
        // RESET THE SCHEDULE ITEM OBJECT WITH DEFAULT VALUES
        player = new Player();
               
        image = new Image("file:"+"./data/wolfieball_images/players/"+playerToEdit.getLAST_NAME()+playerToEdit.getFIRST_NAME()+".jpg");
        if(image.isError()){
            image = new Image("file:"+"./data/wolfieball_images/players/AAA_PhotoMissing.jpg");
        }
        imageFlag = new Image("file:"+"./data/wolfieball_images/flags/"+playerToEdit.getNATION_OF_BIRTH()+".png");
        iv.setImage(image);
        ivFlag.setImage(imageFlag);
        
        playerName.setText(playerToEdit.getFIRST_NAME()+" "+playerToEdit.getLAST_NAME());
        
        position.setText(playerToEdit.getPosition());
        
        // LOAD SESSIONS VALUES
        fantasyTeamComboBox.getItems().clear();
        fantasyTeamComboBox.setValue("");
        fantasyTeamComboBox.getItems().add("Free Agent");
        ObservableList<String> teamName = FXCollections.observableArrayList();
        for (int i = 0; i < gui.getDataManager().getDraft().getDraftedTeams().size(); i++) {
            teamName.add(gui.getDataManager().getDraft().getDraftedTeams().get(i).getName());
            fantasyTeamComboBox.getItems().add(gui.getDataManager().getDraft().getDraftedTeams().get(i).getName());
        }
        //fantasyTeamComboBox.getItems().add(teamName);
        
        // LOAD SESSIONS VALUES
        positionComboBox.setValue("");
        positionComboBox.getItems().clear();
//        ObservableList<String> position = FXCollections.observableArrayList();
//        String[] parts = playerToEdit.getPosition().split("_");
//        for(int i = 0; i < parts.length; i++){
//            position.add(parts[i]);
//            for(int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++){
//                System.err.print(teamSelected);
//                System.err.print(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName());
//                //if(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName().equals(String.valueOf(fantasyTeamComboBox.getSelectionModel().getSelectedItem()))){
//                if(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName().equals(teamSelected)){
//                    System.out.print("99999");
//                    if(parts[i].equals("C")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getCList().size()<=2){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("1B")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).get1BList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("CI")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getCIList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("3B")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).get3BList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("2B")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).get2BList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("MI")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getMIList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("SS")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getSSList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("U")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getUList().size()<=1){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("OF")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getOFList().size()<=5){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                    if(parts[i].equals("P")&&gui.getDataManager().getDraft().getDraftedTeams().get(j).getPList().size()<=9){
//                        positionComboBox.getItems().add(parts[i]);
//                    }
//                }
//                break;
//            }
////            if(gui.getDataManager().getDraft().getDraftedTeams().get(i)){
////                positionComboBox.getItems().add(parts[i]);
////            }
//        }
        //positionComboBox.setItems(position);
        
        contractComboBox.setValue("");
        contractComboBox.getItems().clear();
        
        // LOAD SESSIONS VALUES
        ObservableList<String> contract = FXCollections.observableArrayList("X","S1","S2");
        contractComboBox.setItems(contract);
        
        salaryTextField.clear();
        
        // AND OPEN IT UP
        this.showAndWait();
        
        return player;
    }
    
    public void loadGUIData() {
        // LOAD THE UI STUFF
          
    }
    
    public boolean wasCompleteSelected() {
        return selection.equals(COMPLETE);
    }
    
    public ComboBox getFantasyTeamComboBox(){
        return fantasyTeamComboBox;
    }
    
//    public void showEditPlayerDialog(Player playerToEdit) {
//        // SET THE DIALOG TITLE
//        setTitle(EDIT_TEAM_TITLE);
//        
//        // LOAD THE LECTURE INTO OUR LOCAL OBJECT
//        player = new Player();
//        player.setFIRST_NAME(playerToEdit.getFIRST_NAME());
//        player.setLAST_NAME(playerToEdit.getLAST_NAME());
//        
//        // AND THEN INTO OUR GUI
//        loadGUIData();
//               
//        // AND OPEN IT UP
//        this.showAndWait();
//    }
}