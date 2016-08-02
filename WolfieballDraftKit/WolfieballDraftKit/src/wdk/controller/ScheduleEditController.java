package wdk.controller;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import static wdk.WDK_PropertyType.REMOVE_ITEM_MESSAGE;
import wdk.data.Course;
import wdk.data.Draft;
import wdk.data.DraftDataManager;
import wdk.data.ScheduleItem;
import wdk.data.Lecture;
import wdk.data.Assignment;
import wdk.gui.WDK_GUI;
import wdk.gui.MessageDialog;
import wdk.gui.ScheduleItemDialog;
import wdk.gui.LectureDialog;
import wdk.gui.AssignmentDialog;
import wdk.gui.YesNoCancelDialog;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;
import wdk.data.Team;
import wdk.data.Player;
import wdk.gui.PlayerDialog;
import wdk.gui.PlayerEditDialog;
import wdk.gui.TeamDialog;

/**
 *
 * @author McKillaGorilla
 */
public class ScheduleEditController {
    TeamDialog td;
    PlayerDialog pd;
    PlayerEditDialog ped;
    MessageDialog messageDialog;
    YesNoCancelDialog yesNoCancelDialog;
    
    public ScheduleEditController(Stage initPrimaryStage, Draft draft, MessageDialog initMessageDialog, YesNoCancelDialog initYesNoCancelDialog) {
        td = new TeamDialog(initPrimaryStage, draft, initMessageDialog);
        pd = new PlayerDialog(initPrimaryStage, draft, initMessageDialog);
        ped = new PlayerEditDialog(initPrimaryStage, draft, initMessageDialog);
        messageDialog = initMessageDialog;
        yesNoCancelDialog = initYesNoCancelDialog;
    }

    // THESE ARE FOR TEAMS
    
    public void handleAddTeamRequest(WDK_GUI gui) {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        td.showAddTeamDialog();
        
        // DID THE USER CONFIRM?
        if (td.wasCompleteSelected()) {
            // GET THE SCHEDULE ITEM
            Team t = td.getTeam();
            
            // AND ADD IT AS A ROW TO THE TABLE
            draft.addTeam(t);
            
            ObservableList<String> teamNames = FXCollections.observableArrayList();
            for (int i = 0; i < draft.getDraftedTeams().size(); i++) {
                teamNames.add(draft.getDraftedTeams().get(i).getName());
            }
            
            gui.getComboBox().setItems(teamNames);
            
            for(int i = 0; i < draft.getDraftedTeams().size();i++){
                draft.getDraftedTeams().get(i).setTotalPoints(0);
            }
            draft.fetchTotalPoints();
            
            gui.getDataManager().getDraft().calculateEstimatedValue();
            
            gui.getRemoveTeamButton().setDisable(false);
            gui.getEditTeamButton().setDisable(false);
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }
    }
    
    public void handleEditTeamRequest(WDK_GUI gui, Team teamToEdit) {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        td.showEditTeamDialog(teamToEdit);
        
        // DID THE USER CONFIRM?
        if (td.wasCompleteSelected()) {
            // UPDATE THE SCHEDULE ITEM
            Team t = td.getTeam();
            teamToEdit.setName(t.getName());
            teamToEdit.setOwner(t.getOwner());
            
            ObservableList<String> teamNames = FXCollections.observableArrayList();
                for (int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++) {
                    teamNames.add(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName());
                }
            gui.getComboBox().setItems(teamNames);
            //course.sortScheduleItem();
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }        
    }
    
    public void handleRemoveTeamRequest(WDK_GUI gui) {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yesNoCancelDialog.show(PropertiesManager.getPropertiesManager().getProperty(REMOVE_ITEM_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoCancelDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) {
            for(int i = 0; i < gui.getDataManager().getDraft().getDraftedTeams().size(); i++){
                if (gui.getDataManager().getDraft().getDraftedTeams().get(i).getName().equals(gui.getComboBox().getSelectionModel().getSelectedItem().toString())){
                    // return players to free agent
                    for(int k = 0; k < gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().size();k++){
                        gui.getDataManager().getDraft().getInitialPlayers().add(gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().get(k));
                    }
                    //remove players in summary table that belong to that team
                    for(int k = 0; k < gui.getDataManager().getDraft().getDraftSummaryPlayers().size(); k++){
                        if (gui.getDataManager().getDraft().getDraftSummaryPlayers().get(k).getFANTASYTEAM().equals(gui.getComboBox().getSelectionModel().getSelectedItem().toString())){
                            System.out.println("->100");
                            gui.getDataManager().getDraft().getDraftSummaryPlayers().remove(gui.getDataManager().getDraft().getDraftSummaryPlayers().get(k));
                            k--;
                        }
                    }
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).getStartingLineUpPlayers().clear();
                    gui.getDataManager().getDraft().getDraftedTeams().get(i).getTaxiSquadPlayers().clear();
                    gui.getDataManager().getDraft().removeTeam(gui.getDataManager().getDraft().getDraftedTeams().get(i));
                    
                    for(int g = 0; g < gui.getDataManager().getDraft().getDraftedTeams().size();g++){
                        gui.getDataManager().getDraft().getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    gui.getDataManager().getDraft().fetchTotalPoints();
                    
                    ObservableList<String> teamNames = FXCollections.observableArrayList();
                    for (int j = 0; j < gui.getDataManager().getDraft().getDraftedTeams().size(); j++) {
                        teamNames.add(gui.getDataManager().getDraft().getDraftedTeams().get(j).getName());
                    }
                    gui.getComboBox().setItems(teamNames);
                    break;
                }
            }
        }
    }
    
    public void handleAddPlayerRequest(WDK_GUI gui) {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        pd.showAddPlayerDialog(gui);
        
        // DID THE USER CONFIRM?
        if (pd.wasCompleteSelected()) {
            // GET THE SCHEDULE ITEM
            Player p = pd.getPlayer();
            
            // AND ADD IT AS A ROW TO THE TABLE
            draft.getInitialPlayers().add(p);
            
            gui.getRemovePlayerButton().setDisable(false);
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }
    }
    
    public void handleRemovePlayerRequest(WDK_GUI gui, Player playerToRemove) {
        // PROMPT THE USER TO SAVE UNSAVED WORK
        yesNoCancelDialog.show(PropertiesManager.getPropertiesManager().getProperty(REMOVE_ITEM_MESSAGE));
        
        // AND NOW GET THE USER'S SELECTION
        String selection = yesNoCancelDialog.getSelection();

        // IF THE USER SAID YES, THEN SAVE BEFORE MOVING ON
        if (selection.equals(YesNoCancelDialog.YES)) { 
            gui.getDataManager().getDraft().removePlayer(playerToRemove);
            gui.updatePlayersScreen(gui.getDataManager().getDraft());
        }
    }
    
    public void handleEditPlayerRequest(WDK_GUI gui, Player playerToEdit) throws FileNotFoundException {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        ped.showEditPlayerDialog(gui, playerToEdit);
        
        // DID THE USER CONFIRM?
        if (ped.wasCompleteSelected()) {
            // UPDATE THE SCHEDULE ITEM
            Player p  = ped.getPlayer();
            
            playerToEdit.setFANTASYTEAM(p.getFANTASYTEAM());
            playerToEdit.setFANTASYPOSITION(p.getFANTASYPOSITION());
            playerToEdit.setCONTRACT(p.getCONTRACT());
            playerToEdit.setSALARY(p.getSALARY());
          if(playerToEdit.getFANTASYPOSITION().equals("Taxi Squad")){
              for(int i = 0; i< draft.getDraftedTeams().size();i++){
                  if(draft.getDraftedTeams().get(i).getName().equals(String.valueOf(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem()))){
                      playerToEdit.setCONTRACT("X");
                      playerToEdit.setSALARY(1);
                      draft.getDraftedTeams().get(i).getTaxiSquadPlayers().add(playerToEdit);
                      draft.getDraftSummaryPlayers().add(playerToEdit);
                      draft.getInitialPlayers().remove(playerToEdit);
                      gui.getTaxiSquadTable().setItems(draft.getDraftedTeams().get(i).getTaxiSquadPlayers());
                      break;
                  }
              }
          }
          else{
            for(int i = 0; i < draft.getDraftedTeams().size(); i++){ //fill 10 position lists, 1st big for loop
                if (draft.getDraftedTeams().get(i).getName().equals(String.valueOf(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem()))){
                    if(playerToEdit.getFANTASYPOSITION().equals("C")){
                        draft.getDraftedTeams().get(i).getCList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("1B")){
                        draft.getDraftedTeams().get(i).get1BList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("CI")){
                        draft.getDraftedTeams().get(i).getCIList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("3B")){
                        draft.getDraftedTeams().get(i).get3BList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("2B")){
                        draft.getDraftedTeams().get(i).get2BList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("MI")){
                        draft.getDraftedTeams().get(i).getMIList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("SS")){
                        draft.getDraftedTeams().get(i).getSSList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("OF")){
                        draft.getDraftedTeams().get(i).getOFList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("U")){
                        draft.getDraftedTeams().get(i).getUList().add(playerToEdit);
                    }
                    else if(playerToEdit.getFANTASYPOSITION().equals("P")){
                        draft.getDraftedTeams().get(i).getPList().add(playerToEdit);
                    }
                    
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers(); // clear starting and fill with 10 lists
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getCList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get1BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getCIList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get3BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get2BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getMIList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getSSList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getUList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getOFList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(j));
                    }
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getPList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(j));
                    }
                    
//                    // here I update totalpoints
//                    for(int g = 0; g < gui.getDataManager().getDraft().getDraftedTeams().size();g++){
//                        gui.getDataManager().getDraft().getDraftedTeams().get(g).setTotalPoints(0);
//                    }
//                    gui.getDataManager().getDraft().fetchTotalPoints();
//                    gui.getDataManager().getDraft().calculateEstimatedValue();
                    break;
                }
            }
            
            for(int i = 0; i < draft.getDraftedTeams().size(); i++){ //update fantasyteams table, 2nd big for loop
                if (draft.getDraftedTeams().get(i).getName().equals(String.valueOf(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem()))){
                    DecimalFormat df1 = new DecimalFormat("##0.000");
                    DecimalFormat df2 = new DecimalFormat("##0.00");
                    draft.getDraftedTeams().get(i).setPlayersNeeded(23-draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                    draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft() - playerToEdit.getSALARY());
                    //draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() !=0){
                        draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    }
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() ==0){
                        draft.getDraftedTeams().get(i).setMoneyPP(-1);
                    }
                    
                    if(playerToEdit.getTYPE().equals("Hitter")){
                        draft.getDraftedTeams().get(i).setR(draft.getDraftedTeams().get(i).getR()+playerToEdit.getR());
                        draft.getDraftedTeams().get(i).setHR(draft.getDraftedTeams().get(i).getHR()+playerToEdit.getHR());
                        draft.getDraftedTeams().get(i).setRBI(draft.getDraftedTeams().get(i).getRBI()+playerToEdit.getRBI());
                        draft.getDraftedTeams().get(i).setSB(draft.getDraftedTeams().get(i).getSB()+playerToEdit.getSB());                        
                        //draft.getDraftedTeams().get(i).setBA((draft.getDraftedTeams().get(i).getBA()*(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-1)+playerToEdit.getBA())/draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                        calculateBA(draft.getDraftedTeams().get(i));
                    }
                    else if(playerToEdit.getTYPE().equals("Pitcher")){
                        draft.getDraftedTeams().get(i).setW(draft.getDraftedTeams().get(i).getW()+playerToEdit.getW());
                        draft.getDraftedTeams().get(i).setSV(draft.getDraftedTeams().get(i).getSV()+playerToEdit.getSV());
                        draft.getDraftedTeams().get(i).setK(draft.getDraftedTeams().get(i).getK()+playerToEdit.getK());
                        //draft.getDraftedTeams().get(i).setERA((draft.getDraftedTeams().get(i).getERA()*(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-1)+playerToEdit.getERA())/draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                        //draft.getDraftedTeams().get(i).setWHIP((draft.getDraftedTeams().get(i).getWHIP()*(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-1)+playerToEdit.getWHIP())/draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                        calculateERA(draft.getDraftedTeams().get(i));
                        calculateWHIP(draft.getDraftedTeams().get(i));
                    }
               }
            }
            
            // here I update totalpoints
            for(int g = 0; g < gui.getDataManager().getDraft().getDraftedTeams().size();g++){
                 gui.getDataManager().getDraft().getDraftedTeams().get(g).setTotalPoints(0);
            }
            gui.getDataManager().getDraft().fetchTotalPoints();
            gui.getDataManager().getDraft().calculateEstimatedValue();
                               
            // here I only allow add S2 player
            if(!playerToEdit.getCONTRACT().equals("S1") && !playerToEdit.getCONTRACT().equals("X")){
                draft.getDraftSummaryPlayers().add(playerToEdit);
            }
            draft.getInitialPlayers().remove(playerToEdit);
            
            if(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString().equals("Free Agent")){
                    draft.getInitialPlayers().add(playerToEdit);
            }
            
            gui.updatePlayersScreen(draft);
          }
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }        
    }
    
    public void handleEditPlayerInFTRequest(WDK_GUI gui, Player playerToEdit) throws FileNotFoundException {
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        String temTeam = null;
        String temContract = null;
        int temSalary = 0;
        if(playerToEdit != null){
            temTeam = playerToEdit.getFANTASYTEAM();
            temContract = playerToEdit.getCONTRACT();
            temSalary = playerToEdit.getSALARY();
        }
        ped.showEditPlayerDialog(gui, playerToEdit);
        
        String temName = playerToEdit.getFANTASYPOSITION();
        
        // DID THE USER CONFIRM?
        if (ped.wasCompleteSelected()) {
            // UPDATE THE SCHEDULE ITEM
            //draft.getDraftSummaryPlayers().remove(playerToEdit);
            Player p  = ped.getPlayer();
            
            playerToEdit.setFANTASYTEAM(p.getFANTASYTEAM());
            playerToEdit.setFANTASYPOSITION(p.getFANTASYPOSITION());
            playerToEdit.setCONTRACT(p.getCONTRACT());
            playerToEdit.setSALARY(p.getSALARY());

            // here I update one player info without swtiching teams
            if(temTeam.equals(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString())){
                if(!temContract.equals(playerToEdit.getCONTRACT())){
                    if(playerToEdit.getCONTRACT().equals("S2")){
                        draft.getDraftSummaryPlayers().add(playerToEdit);  
                    }
                    else {
                        draft.getDraftSummaryPlayers().remove(playerToEdit);
                    }
                }
                for (int i = 0; i < draft.getDraftedTeams().size();i++){
                    if(draft.getDraftedTeams().get(i).getName().equals(temTeam)){
                        draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft()-(playerToEdit.getSALARY()-temSalary));
                    }
                }
            }
            
            if(!temTeam.equals(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString())){ // here if user switch teams or free agent
                if(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString().equals("Free Agent")){ // first check if free agent
                    playerToEdit.setFANTASYTEAM("");
                    playerToEdit.setFANTASYPOSITION("");
                    playerToEdit.setSALARY(0);
                    draft.getInitialPlayers().add(playerToEdit);
                    draft.getDraftSummaryPlayers().remove(playerToEdit);

                    for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 6th big for loop, remove player out of startingline up in that team and 10 lists
                    if (draft.getDraftedTeams().get(i).getName().equals(temTeam)){
                        if(temName.equals("C")){
                            if(draft.getDraftedTeams().get(i).getCList().size()!=0)
                            draft.getDraftedTeams().get(i).getCList().remove(draft.getDraftedTeams().get(i).getCList().size()-1);
                        }
                        if(temName.equals("1B")){
                            if(draft.getDraftedTeams().get(i).get1BList().size()!=0)
                            draft.getDraftedTeams().get(i).get1BList().remove(draft.getDraftedTeams().get(i).get1BList().size()-1);
                        }
                        if(temName.equals("CI")){
                            if(draft.getDraftedTeams().get(i).getCIList().size()!=0)
                            draft.getDraftedTeams().get(i).getCIList().remove(draft.getDraftedTeams().get(i).getCIList().size()-1);
                        }
                        if(temName.equals("3B")){
                            if(draft.getDraftedTeams().get(i).get3BList().size()!=0)
                            draft.getDraftedTeams().get(i).get3BList().remove(draft.getDraftedTeams().get(i).get3BList().size()-1);
                        }
                        if(temName.equals("2B")){
                            if(draft.getDraftedTeams().get(i).get2BList().size()!=0)
                            draft.getDraftedTeams().get(i).get2BList().remove(draft.getDraftedTeams().get(i).get2BList().size()-1);
                        }
                        if(temName.equals("MI")){
                            if(draft.getDraftedTeams().get(i).getMIList().size()!=0)
                            draft.getDraftedTeams().get(i).getMIList().remove(draft.getDraftedTeams().get(i).getMIList().size()-1);
                        }
                        if(temName.equals("SS")){
                            if(draft.getDraftedTeams().get(i).getSSList().size()!=0)
                            draft.getDraftedTeams().get(i).getSSList().remove(draft.getDraftedTeams().get(i).getSSList().size()-1);
                        }
                        if(temName.equals("U")){
                            if(draft.getDraftedTeams().get(i).getUList().size()!=0)
                            draft.getDraftedTeams().get(i).getUList().remove(draft.getDraftedTeams().get(i).getUList().size()-1);
                        }
                        if(temName.equals("OF")){
                            if(draft.getDraftedTeams().get(i).getOFList().size()!=0)
                            draft.getDraftedTeams().get(i).getOFList().remove(draft.getDraftedTeams().get(i).getOFList().size()-1);
                        }
                        if(temName.equals("P")){
                            if(draft.getDraftedTeams().get(i).getPList().size()!=0)
                            draft.getDraftedTeams().get(i).getPList().remove(draft.getDraftedTeams().get(i).getPList().size()-1);
                        }
                        draft.getDraftedTeams().get(i).removePlayer(playerToEdit);
                        
                        draft.calculateEstimatedValue();
                        break;
                    }
                    }// end of 6th big for loop
                    
                    for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 7th big for loop, update standings table for that table
                        if (draft.getDraftedTeams().get(i).getName().equals(temTeam)){
                                draft.getDraftedTeams().get(i).setPlayersNeeded(23-draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                                draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft() + temSalary);
                                //draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    
                                if(draft.getDraftedTeams().get(i).getPlayersNeeded() !=0){
                                    draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                                }
                                if(draft.getDraftedTeams().get(i).getPlayersNeeded() ==0){
                                    draft.getDraftedTeams().get(i).setMoneyPP(-1);
                                }
                                
                                if(playerToEdit.getTYPE().equals("Hitter")){
                                    draft.getDraftedTeams().get(i).setR(draft.getDraftedTeams().get(i).getR()-playerToEdit.getR());
                                    draft.getDraftedTeams().get(i).setHR(draft.getDraftedTeams().get(i).getHR()-playerToEdit.getHR());
                                    draft.getDraftedTeams().get(i).setRBI(draft.getDraftedTeams().get(i).getRBI()-playerToEdit.getRBI());
                                    draft.getDraftedTeams().get(i).setSB(draft.getDraftedTeams().get(i).getSB()-playerToEdit.getSB());
                                    calculateBA(draft.getDraftedTeams().get(i));
                                }
                                else if(playerToEdit.getTYPE().equals("Pitcher")){
                                    draft.getDraftedTeams().get(i).setW(draft.getDraftedTeams().get(i).getW()-playerToEdit.getW());
                                    draft.getDraftedTeams().get(i).setSV(draft.getDraftedTeams().get(i).getSV()-playerToEdit.getSV());
                                    draft.getDraftedTeams().get(i).setK(draft.getDraftedTeams().get(i).getK()-playerToEdit.getK());
                                    calculateERA(draft.getDraftedTeams().get(i));
                                    calculateWHIP(draft.getDraftedTeams().get(i));
                                }
                        }
                    }
                    for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                    }
                    draft.fetchTotalPoints();
                    draft.calculateEstimatedValue();
                }
                else{// if this is not return to free agent
                    
                draft.getDraftSummaryPlayers().remove(playerToEdit);
                    
                for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 1st big for loop, clear that player out of 10 positions lists in the team
                    if (draft.getDraftedTeams().get(i).getName().equals(temTeam) && !ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString().equals("Free Agent")){// update previous team
                        draft.getDraftedTeams().get(i).removePlayer(playerToEdit); // remove starting LineUp
                        if(temName.equals("C") && draft.getDraftedTeams().get(i).getCList().size()!=0){
                            draft.getDraftedTeams().get(i).getCList().remove(draft.getDraftedTeams().get(i).getCList().size()-1);
                        }
                        if(temName.equals("1B") && draft.getDraftedTeams().get(i).get1BList().size()!=0){
                            draft.getDraftedTeams().get(i).get1BList().remove(draft.getDraftedTeams().get(i).get1BList().size()-1);
                        }
                        if(temName.equals("CI") && draft.getDraftedTeams().get(i).getCIList().size()!=0){
                            draft.getDraftedTeams().get(i).getCIList().remove(draft.getDraftedTeams().get(i).getCIList().size()-1);
                        }
                        if(temName.equals("3B") && draft.getDraftedTeams().get(i).get3BList().size()!=0){
                            draft.getDraftedTeams().get(i).get3BList().remove(draft.getDraftedTeams().get(i).get3BList().size()-1);
                        }
                        if(temName.equals("2B") && draft.getDraftedTeams().get(i).get2BList().size()!=0){
                            draft.getDraftedTeams().get(i).get2BList().remove(draft.getDraftedTeams().get(i).get2BList().size()-1);
                        }
                        if(temName.equals("MI") && draft.getDraftedTeams().get(i).getMIList().size()!=0){
                            draft.getDraftedTeams().get(i).getMIList().remove(draft.getDraftedTeams().get(i).getMIList().size()-1);
                        }
                        if(temName.equals("SS") && draft.getDraftedTeams().get(i).getSSList().size()!=0){
                            draft.getDraftedTeams().get(i).getSSList().remove(draft.getDraftedTeams().get(i).getSSList().size()-1);
                        }
                        if(temName.equals("U") && draft.getDraftedTeams().get(i).getUList().size()!=0){
                            draft.getDraftedTeams().get(i).getUList().remove(draft.getDraftedTeams().get(i).getUList().size()-1);
                        }
                        if(temName.equals("OF") && draft.getDraftedTeams().get(i).getOFList().size()!=0){
                            draft.getDraftedTeams().get(i).getOFList().remove(draft.getDraftedTeams().get(i).getOFList().size()-1);
                        }
                        if(temName.equals("P") && draft.getDraftedTeams().get(i).getPList().size()!=0){
                            draft.getDraftedTeams().get(i).getPList().remove(draft.getDraftedTeams().get(i).getPList().size()-1);
                        }
                        break; // break this for loop
                    }
                } // end of 1st big for loop
                
                for(int i = 0; i < draft.getDraftedTeams().size(); i++){ //2nd bi for loop, update fantasyteams standings table on previous team
                if (draft.getDraftedTeams().get(i).getName().equals(temTeam) && !ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString().equals("Free Agent")){
                    draft.getDraftedTeams().get(i).setPlayersNeeded(23-draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                    draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft() + temSalary);
                    //draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() !=0){
                        draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    }
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() ==0){
                        draft.getDraftedTeams().get(i).setMoneyPP(-1);
                    }
                    
                    if(playerToEdit.getTYPE().equals("Hitter")){
                        draft.getDraftedTeams().get(i).setR(draft.getDraftedTeams().get(i).getR()-playerToEdit.getR());
                        draft.getDraftedTeams().get(i).setHR(draft.getDraftedTeams().get(i).getHR()-playerToEdit.getHR());
                        draft.getDraftedTeams().get(i).setRBI(draft.getDraftedTeams().get(i).getRBI()-playerToEdit.getRBI());
                        draft.getDraftedTeams().get(i).setSB(draft.getDraftedTeams().get(i).getSB()-playerToEdit.getSB());
                            //draft.getDraftedTeams().get(i).setBA((((draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size())+1)-playerToEdit.getBA())/(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size()));
                            calculateBA(draft.getDraftedTeams().get(i));
                    }
                    else if(playerToEdit.getTYPE().equals("Pitcher")){
                        draft.getDraftedTeams().get(i).setW(draft.getDraftedTeams().get(i).getW()-playerToEdit.getW());
                        draft.getDraftedTeams().get(i).setSV(draft.getDraftedTeams().get(i).getSV()-playerToEdit.getSV());
                        draft.getDraftedTeams().get(i).setK(draft.getDraftedTeams().get(i).getK()-playerToEdit.getK());
                            //draft.getDraftedTeams().get(i).setERA((draft.getDraftedTeams().get(i).getERA()*(draft.getDraftedTeams().get(i).getPList().size()+1)-playerToEdit.getERA())/draft.getDraftedTeams().get(i).getPList().size());
                            //draft.getDraftedTeams().get(i).setWHIP((draft.getDraftedTeams().get(i).getWHIP()*(draft.getDraftedTeams().get(i).getPList().size()+1)-playerToEdit.getWHIP())/draft.getDraftedTeams().get(i).getPList().size());
                            calculateERA(draft.getDraftedTeams().get(i));
                            calculateWHIP(draft.getDraftedTeams().get(i));
                    }
                }
                } //  end of 2nd big for loop
                
                for(int i = 0; i < draft.getDraftedTeams().size(); i++){// 3rd big for loop, increment size of new selected team 10 position list
                    if (draft.getDraftedTeams().get(i).getName().equals(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString())){
                        if(playerToEdit.getFANTASYPOSITION().equals("C")){
                            draft.getDraftedTeams().get(i).getCList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("1B")){
                            draft.getDraftedTeams().get(i).get1BList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("CI")){
                            draft.getDraftedTeams().get(i).getCIList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("3B")){
                            draft.getDraftedTeams().get(i).get3BList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("2B")){
                            draft.getDraftedTeams().get(i).get2BList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("MI")){
                            draft.getDraftedTeams().get(i).getMIList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("SS")){
                            draft.getDraftedTeams().get(i).getSSList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("U")){
                            draft.getDraftedTeams().get(i).getUList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("OF")){
                            draft.getDraftedTeams().get(i).getOFList().add(playerToEdit);
                        }
                        if(playerToEdit.getFANTASYPOSITION().equals("P")){
                            draft.getDraftedTeams().get(i).getPList().add(playerToEdit);
                        }
                        //draft.getDraftedTeams().get(i).removePlayer(playerToEdit);
                        break;
                    }  
                } // end of 3rd big for loop
                
                
                for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 4th big for loop, update selected team players
                    if (draft.getDraftedTeams().get(i).getName().equals(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString())){
                        /*draft.getDraftedTeams().get(i).addPlayer(playerToEdit);*/
                        
                    draft.getDraftedTeams().get(i).clearStartingLineUpPlayers();

                    // add elements to al, including duplicates
                    Set<Player> hs = new HashSet<>();
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getCList());
                    draft.getDraftedTeams().get(i).getCList().clear();
                    draft.getDraftedTeams().get(i).getCList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getCList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).get1BList());
                    draft.getDraftedTeams().get(i).get1BList().clear();
                    draft.getDraftedTeams().get(i).get1BList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get1BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get1BList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getCIList());
                    draft.getDraftedTeams().get(i).getCIList().clear();
                    draft.getDraftedTeams().get(i).getCIList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getCIList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getCIList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).get3BList());
                    draft.getDraftedTeams().get(i).get3BList().clear();
                    draft.getDraftedTeams().get(i).get3BList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get3BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get3BList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).get2BList());
                    draft.getDraftedTeams().get(i).get2BList().clear();
                    draft.getDraftedTeams().get(i).get2BList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).get2BList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).get2BList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getMIList());
                    draft.getDraftedTeams().get(i).getMIList().clear();
                    draft.getDraftedTeams().get(i).getMIList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getMIList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getMIList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getSSList());
                    draft.getDraftedTeams().get(i).getSSList().clear();
                    draft.getDraftedTeams().get(i).getSSList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getSSList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getSSList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getUList());
                    draft.getDraftedTeams().get(i).getUList().clear();
                    draft.getDraftedTeams().get(i).getUList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getUList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getUList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getOFList());
                    draft.getDraftedTeams().get(i).getOFList().clear();
                    draft.getDraftedTeams().get(i).getOFList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getOFList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getOFList().get(j));
                    }
                    
                    hs.addAll(draft.getDraftedTeams().get(i).getPList());
                    draft.getDraftedTeams().get(i).getPList().clear();
                    draft.getDraftedTeams().get(i).getPList().addAll(hs);
                    hs.clear();
                    for(int j = 0; j < draft.getDraftedTeams().get(i).getPList().size(); j++){
                        draft.getDraftedTeams().get(i).addPlayer((Player)draft.getDraftedTeams().get(i).getPList().get(j));
                    } 
                        break;
                    }
                } // end of 4th big for loop
                
                for(int i = 0; i < draft.getDraftedTeams().size(); i++){ //5th big for loop, update fantasyteams table on new selected team
                if (draft.getDraftedTeams().get(i).getName().equals(String.valueOf(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem()))){
                    draft.getDraftedTeams().get(i).setPlayersNeeded(23-draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
                    draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft() - playerToEdit.getSALARY());
                    //draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() !=0){
                        draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
                    }
                    if(draft.getDraftedTeams().get(i).getPlayersNeeded() ==0){
                        draft.getDraftedTeams().get(i).setMoneyPP(-1);
                    }
                    
                    if(playerToEdit.getTYPE().equals("Hitter")){
                        draft.getDraftedTeams().get(i).setR(draft.getDraftedTeams().get(i).getR()+playerToEdit.getR());
                        draft.getDraftedTeams().get(i).setHR(draft.getDraftedTeams().get(i).getHR()+playerToEdit.getHR());
                        draft.getDraftedTeams().get(i).setRBI(draft.getDraftedTeams().get(i).getRBI()+playerToEdit.getRBI());
                        draft.getDraftedTeams().get(i).setSB(draft.getDraftedTeams().get(i).getSB()+playerToEdit.getSB());
                        //draft.getDraftedTeams().get(i).setBA((draft.getDraftedTeams().get(i).getBA()*((draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size())-1)+playerToEdit.getBA())/(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size()));
                        calculateBA(draft.getDraftedTeams().get(i));
                    }
                    else if(playerToEdit.getTYPE().equals("Pitcher")){
                        draft.getDraftedTeams().get(i).setW(draft.getDraftedTeams().get(i).getW()+playerToEdit.getW());
                        draft.getDraftedTeams().get(i).setSV(draft.getDraftedTeams().get(i).getSV()+playerToEdit.getSV());
                        draft.getDraftedTeams().get(i).setK(draft.getDraftedTeams().get(i).getK()+playerToEdit.getK());
                        //draft.getDraftedTeams().get(i).setERA((draft.getDraftedTeams().get(i).getERA()*(draft.getDraftedTeams().get(i).getPList().size()-1)+playerToEdit.getERA())/draft.getDraftedTeams().get(i).getPList().size());
                        //draft.getDraftedTeams().get(i).setWHIP((draft.getDraftedTeams().get(i).getWHIP()*(draft.getDraftedTeams().get(i).getPList().size()-1)+playerToEdit.getWHIP())/draft.getDraftedTeams().get(i).getPList().size());
                        calculateERA(draft.getDraftedTeams().get(i));
                        calculateWHIP(draft.getDraftedTeams().get(i));
                    }
                }
                } // end of 5th big for loop
                
                if(playerToEdit.getCONTRACT().equals("S2")){ // add to new team, if he is S2, also add to summary
                   draft.getDraftSummaryPlayers().add(playerToEdit);
                }
                
                for(int g = 0; g < draft.getDraftedTeams().size();g++){
                        draft.getDraftedTeams().get(g).setTotalPoints(0);
                }
                draft.fetchTotalPoints();
                gui.getDataManager().getDraft().calculateEstimatedValue();
                }
                // handle return player to free agent on all the table
                //if(ped.getFantasyTeamComboBox().getSelectionModel().getSelectedItem().toString().equals("Free Agent")){ // 6th big for loop
//                    draft.getInitialPlayers().add(playerToEdit);
//                    draft.getDraftSummaryPlayers().remove(playerToEdit);
//
//                    for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 6th big for loop, remove player out of startingline up in that team and 10 lists
//                    if (draft.getDraftedTeams().get(i).getName().equals(temTeam)){
//                        if(temName.equals("C")){
//                            if(draft.getDraftedTeams().get(i).getCList().size()!=0)
//                            draft.getDraftedTeams().get(i).getCList().remove(draft.getDraftedTeams().get(i).getCList().size()-1);
//                        }
//                        if(temName.equals("1B")){
//                            if(draft.getDraftedTeams().get(i).get1BList().size()!=0)
//                            draft.getDraftedTeams().get(i).get1BList().remove(draft.getDraftedTeams().get(i).get1BList().size()-1);
//                        }
//                        if(temName.equals("CI")){
//                            if(draft.getDraftedTeams().get(i).getCIList().size()!=0)
//                            draft.getDraftedTeams().get(i).getCIList().remove(draft.getDraftedTeams().get(i).getCIList().size()-1);
//                        }
//                        if(temName.equals("3B")){
//                            if(draft.getDraftedTeams().get(i).get3BList().size()!=0)
//                            draft.getDraftedTeams().get(i).get3BList().remove(draft.getDraftedTeams().get(i).get3BList().size()-1);
//                        }
//                        if(temName.equals("2B")){
//                            if(draft.getDraftedTeams().get(i).get2BList().size()!=0)
//                            draft.getDraftedTeams().get(i).get2BList().remove(draft.getDraftedTeams().get(i).get2BList().size()-1);
//                        }
//                        if(temName.equals("MI")){
//                            if(draft.getDraftedTeams().get(i).getMIList().size()!=0)
//                            draft.getDraftedTeams().get(i).getMIList().remove(draft.getDraftedTeams().get(i).getMIList().size()-1);
//                        }
//                        if(temName.equals("SS")){
//                            if(draft.getDraftedTeams().get(i).getSSList().size()!=0)
//                            draft.getDraftedTeams().get(i).getSSList().remove(draft.getDraftedTeams().get(i).getSSList().size()-1);
//                        }
//                        if(temName.equals("U")){
//                            if(draft.getDraftedTeams().get(i).getUList().size()!=0)
//                            draft.getDraftedTeams().get(i).getUList().remove(draft.getDraftedTeams().get(i).getUList().size()-1);
//                        }
//                        if(temName.equals("OF")){
//                            if(draft.getDraftedTeams().get(i).getOFList().size()!=0)
//                            draft.getDraftedTeams().get(i).getOFList().remove(draft.getDraftedTeams().get(i).getOFList().size()-1);
//                        }
//                        if(temName.equals("P")){
//                            if(draft.getDraftedTeams().get(i).getPList().size()!=0)
//                            draft.getDraftedTeams().get(i).getPList().remove(draft.getDraftedTeams().get(i).getPList().size()-1);
//                        }
//                        //draft.getDraftSummaryPlayers().remove(playerToEdit);
//                        draft.getDraftedTeams().get(i).removePlayer(playerToEdit);
//
//                        break;
//                    }
//                }// end of 6th big for loop
//                    
//                    for(int i = 0; i < draft.getDraftedTeams().size(); i++){ // 7th big for loop, update standings table for that table
//                if (draft.getDraftedTeams().get(i).getName().equals(temTeam)){
//                    draft.getDraftedTeams().get(i).setPlayersNeeded(23-draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size());
//                    draft.getDraftedTeams().get(i).setMoneyLeft(draft.getDraftedTeams().get(i).getMoneyLeft() + temSalary);
//                    draft.getDraftedTeams().get(i).setMoneyPP(draft.getDraftedTeams().get(i).getMoneyLeft()/draft.getDraftedTeams().get(i).getPlayersNeeded());
//                    
//                    if(playerToEdit.getTYPE().equals("Hitter")){
//                        draft.getDraftedTeams().get(i).setR(draft.getDraftedTeams().get(i).getR()-playerToEdit.getR());
//                        draft.getDraftedTeams().get(i).setHR(draft.getDraftedTeams().get(i).getHR()-playerToEdit.getHR());
//                        draft.getDraftedTeams().get(i).setRBI(draft.getDraftedTeams().get(i).getRBI()-playerToEdit.getRBI());
//                        draft.getDraftedTeams().get(i).setSB(draft.getDraftedTeams().get(i).getSB()-playerToEdit.getSB());
////                        if(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size() != 0){
////                            draft.getDraftedTeams().get(i).setBA((((draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size())+1)-playerToEdit.getBA())/(draft.getDraftedTeams().get(i).getStartingLineUpPlayers().size()-draft.getDraftedTeams().get(i).getPList().size()));
////                        }
////                        else{
////                            draft.getDraftedTeams().get(i).setBA(0.0);
////                        }
//                        calculateBA(draft.getDraftedTeams().get(i));
//                    }
//                    else if(playerToEdit.getTYPE().equals("Pitcher")){
//                        draft.getDraftedTeams().get(i).setW(draft.getDraftedTeams().get(i).getW()-playerToEdit.getW());
//                        draft.getDraftedTeams().get(i).setSV(draft.getDraftedTeams().get(i).getSV()-playerToEdit.getSV());
//                        draft.getDraftedTeams().get(i).setK(draft.getDraftedTeams().get(i).getK()-playerToEdit.getK());
//                        //if(draft.getDraftedTeams().get(i).getPList().size() != 0){
//                            //draft.getDraftedTeams().get(i).setERA((draft.getDraftedTeams().get(i).getERA()*(draft.getDraftedTeams().get(i).getPList().size()+1)-playerToEdit.getERA())/draft.getDraftedTeams().get(i).getPList().size());
//                            //draft.getDraftedTeams().get(i).setWHIP((draft.getDraftedTeams().get(i).getWHIP()*(draft.getDraftedTeams().get(i).getPList().size()+1)-playerToEdit.getWHIP())/draft.getDraftedTeams().get(i).getPList().size());
//                        calculateERA(draft.getDraftedTeams().get(i));
//                        calculateWHIP(draft.getDraftedTeams().get(i));
//                        //}
////                        else{
////                            System.out.print("am i here?");
////                            draft.getDraftedTeams().get(i).setERA(0.0);
////                            draft.getDraftedTeams().get(i).setWHIP(0.0);
////                        }
//                    }
//                }
//                }
                //}
            }
        }
        else {
            // THE USER MUST HAVE PRESSED CANCEL, SO
            // WE DO NOTHING
        }        
    }
    
    public void handleFantasyTeamChangeRequest(WDK_GUI gui){
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        for(int i = 0; i < draft.getDraftedTeams().size(); i++){
            if(gui.getComboBox().getSelectionModel().getSelectedItem()!=null){
                if(draft.getDraftedTeams().get(i).getName().equals(gui.getComboBox().getSelectionModel().getSelectedItem().toString())){
                    gui.getStartingLineUpTable().setItems(draft.getDraftedTeams().get(i).getStartingLineUpPlayers());
                    gui.getTaxiSquadTable().setItems(draft.getDraftedTeams().get(i).getTaxiSquadPlayers());
                    break;
                }
            }
        }
    }
    
    public void handleMLBChangeRequest(WDK_GUI gui){
        DraftDataManager cdm = gui.getDataManager();
        Draft draft = cdm.getDraft();
        ObservableList<Player> selectMLBPlayers;
        selectMLBPlayers = FXCollections.observableArrayList();
        //selectMLBPlayers.clear();
        for(int i = 0; i < draft.getInitialPlayers().size(); i++){
            if(gui.getSelectMLBComboBox().getSelectionModel().getSelectedItem()!=null){
            if(draft.getInitialPlayers().get(i).getTEAM().equals(gui.getSelectMLBComboBox().getSelectionModel().getSelectedItem().toString())){
                selectMLBPlayers.add(draft.getInitialPlayers().get(i));
            }
            }
        }
        Collections.sort(selectMLBPlayers,(Player p1, Player p2) -> {
            if (p1.getLAST_NAME().equals(p2.getLAST_NAME()))
                return p1.getFIRST_NAME().compareTo(p2.getFIRST_NAME());
            else
                return p1.getLAST_NAME().compareTo(p2.getLAST_NAME());
            });
        gui.getMLBTable().setItems(selectMLBPlayers);
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