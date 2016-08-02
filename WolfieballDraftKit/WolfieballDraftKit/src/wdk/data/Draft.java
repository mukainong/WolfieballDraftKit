package wdk.data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class represents a course to be edited and then used to generate a site.
 *
 * @author Richard McKenna
 */
public class Draft {

    
    List<CoursePage> pages;

    String name;
    
    // THESE ARE THE THINGS WE'LL PUT IN OUR SCHEDULE, LECTURE AND ASSIGNMENT PAGE
    ObservableList<Player> initialPlayers;
    ObservableList<Player> selectedPlayers;
    ObservableList<Player> draftedPlayers;
    ObservableList<Team> draftedTeams;
    ObservableList<Player> draftSummaryPlayers;

    /**
     * Constructor for setting up a Course, it initializes the Instructor, which
     * would have already been loaded from a file.
     *
     * @param initInstructor The instructor for this course. Note that this can
     * be changed by getting the Instructor and then calling mutator methods on
     * it.
     */
    public Draft() {
        // INITIALIZE THIS OBJECT'S DATA STRUCTURES
        pages = new ArrayList();

        name = new String();
        // INIT THE SCHEDULE STUFF
        initialPlayers = FXCollections.observableArrayList();
        selectedPlayers = FXCollections.observableArrayList();
        draftedPlayers = FXCollections.observableArrayList();
        draftedTeams = FXCollections.observableArrayList();
        draftSummaryPlayers = FXCollections.observableArrayList();
    }

    // BELOW ARE ALL THE ACCESSOR METHODS FOR A COURSE
    // AND THE MUTATOR METHODS. NOTE THAT WE'LL NEED TO CALL
    // THESE AS USERS INPUT VALUES IN THE GUI
    public boolean hasCoursePage(CoursePage testPage) {
        return pages.contains(testPage);
    }

    public void addPage(CoursePage pageToAdd) {
        pages.add(pageToAdd);
    }

    public List<CoursePage> getPages() {
        return pages;
    }

    public void selectPage(CoursePage coursePage) {
        if (!pages.contains(coursePage)) {
            pages.add(coursePage);
        }
    }

    public void unselectPage(CoursePage coursePage) {
        if (pages.contains(coursePage)) {
            pages.remove(coursePage);
        }
    }



    public void clearPages() {
        pages.clear();
    }


    public void clearInitialPlayers() {
        initialPlayers.clear();
    }

    public void clearDraftedPlayers() {
        draftedPlayers.clear();
    }

    public void clearDraftedTeams() {
        draftedTeams.clear();
    }
    
    public void clearSelectedPlayers() {
        selectedPlayers.clear();
    }

    public void addPlayer(Player p) {
        initialPlayers.add(p);
        //Collections.sort(scheduleItems);
    }
    
    public void addTeam(Team t) {
        draftedTeams.add(t);
        //Collections.sort(scheduleItems);
    }
//
//    public void sortScheduleItem() {
//        Collections.sort(scheduleItems);
//    }
    
    public ObservableList<Player> getInitialPlayers() {
        return initialPlayers;
    }
    
    public ObservableList<Player> getSelectedPlayers() {
        return selectedPlayers;
    }

//    public void removeScheduleItem(ScheduleItem itemToRemove) {
//        scheduleItems.remove(itemToRemove);
//    }
//
//    public void addLecture(Lecture l) {
//        lectures.add(l);
//    }

    public ObservableList<Player> getDraftedPlayers() {
        return draftedPlayers;
    }
    
    public ObservableList<Player> getDraftSummaryPlayers() {
        return draftSummaryPlayers;
    }

    public void removeTeam(Team teamToRemove) {
        draftedTeams.remove(teamToRemove);
    }
    
    public void removePlayer(Player playerToRemove) {
        initialPlayers.remove(playerToRemove);
    }
//
//    public void addAssignment(Assignment a) {
//        assignments.add(a);
//        Collections.sort(assignments);
//    }
//    
//    public void sortAssignment() {
//        Collections.sort(assignments);
//    }

    public ObservableList<Team> getDraftedTeams() {
        return draftedTeams;
    }

//    public void removeAssignment(Assignment assignmentToRemove) {
//        assignments.remove(assignmentToRemove);
//    }
    public String getName(){
        return name;
    }
    
    public void setName(String initName){
        name = initName;
    }
    
    public void fetchTotalPoints(){
        ObservableList<Team> temDraftedTeams = FXCollections.observableArrayList();
        temDraftedTeams.addAll(draftedTeams);
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getR()-t2.getR();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getHR()-t2.getHR();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getRBI()-t2.getRBI();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getSB()-t2.getSB();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return Double.valueOf(t1.getBA()).compareTo(Double.valueOf(t2.getBA()));
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getW()-t2.getW();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getSV()-t2.getSV();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return t1.getK()-t2.getK();
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+i+1);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return Double.valueOf(t1.getERA()).compareTo(Double.valueOf(t2.getERA()));
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+temDraftedTeams.size() - i);
        }
        
        Collections.sort(temDraftedTeams,(Team t1, Team t2) -> {
                return Double.valueOf(t1.getWHIP()).compareTo(Double.valueOf(t2.getWHIP()));
            });
        for(int i = 0; i < temDraftedTeams.size(); i++){
            temDraftedTeams.get(i).setTotalPoints(temDraftedTeams.get(i).getTotalPoints()+temDraftedTeams.size() - i);
        }
    }
    
    public void calculateEstimatedValue(){
        ObservableList<Player> temInitialPlayers = FXCollections.observableArrayList();
        temInitialPlayers.addAll(initialPlayers);
        int counter = 0;
        
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getR()-p2.getR();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setR_rank(++counter);
                //System.out.println(temInitialPlayers.get(i).getR()+"->"+temInitialPlayers.get(i).getR_rank());
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getHR()-p2.getHR();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setHR_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getRBI()-p2.getRBI();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setRBI_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getSB()-p2.getSB();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setSB_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return Double.valueOf(p1.getBA()).compareTo(Double.valueOf(p2.getBA()));
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setBA_rank(++counter);
                //System.out.println(temInitialPlayers.get(i).getBA()+"->"+temInitialPlayers.get(i).getBA_rank());
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getW()-p2.getW();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setW_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getSV()-p2.getSV();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setSV_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return p1.getK()-p2.getK();
            });
        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setK_rank(++counter);
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return Double.valueOf(p1.getERA()).compareTo(Double.valueOf(p2.getERA()));
            });
        for(int i = 0; i < temInitialPlayers.size(); i++){
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setERA_rank(++counter);
                //System.out.println(temInitialPlayers.get(i).getERA()+"->"+temInitialPlayers.get(i).getERA_rank());
            }
        }
        
        counter = 0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return Double.valueOf(p1.getWHIP()).compareTo(Double.valueOf(p2.getWHIP()));
            });
        for(int i = 0; i < temInitialPlayers.size(); i++){
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setWHIP_rank(++counter);
                //System.out.println(temInitialPlayers.get(i).getWHIP()+"->"+temInitialPlayers.get(i).getWHIP_rank());
            }
        }
        
        for(int i = 0; i < temInitialPlayers.size(); i++){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setAvg_rank_raw((temInitialPlayers.get(i).getR_rank()+temInitialPlayers.get(i).getHR_rank()+temInitialPlayers.get(i).getRBI_rank()+temInitialPlayers.get(i).getSB_rank()+temInitialPlayers.get(i).getBA_rank())/5);
                //System.out.println(temInitialPlayers.get(i).getR_rank()+"->"+temInitialPlayers.get(i).getHR_rank()+"->"+temInitialPlayers.get(i).getRBI_rank()+"->"+temInitialPlayers.get(i).getSB_rank()+"->"+temInitialPlayers.get(i).getBA_rank()+"->"+temInitialPlayers.get(i).getAvg_rank_raw());
            }
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setAvg_rank_raw((temInitialPlayers.get(i).getW_rank()+temInitialPlayers.get(i).getSV_rank()+temInitialPlayers.get(i).getK_rank()+temInitialPlayers.get(i).getERA_rank()+temInitialPlayers.get(i).getWHIP_rank())/5);
                //System.out.println(temInitialPlayers.get(i).getW_rank()+"->"+temInitialPlayers.get(i).getSV_rank()+"->"+temInitialPlayers.get(i).getK_rank()+"->"+temInitialPlayers.get(i).getERA_rank()+"->"+temInitialPlayers.get(i).getWHIP_rank()+"->"+temInitialPlayers.get(i).getAvg_rank_raw());
            }
        }
        
        int counter1 =0;
        int counter2 =0;
        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
                return Double.valueOf(p1.getAvg_rank_raw()).compareTo(Double.valueOf(p2.getAvg_rank_raw()));
            });
        for(int i = 0; i < temInitialPlayers.size(); i++){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setAvg_rank_hitter(++counter1);
                //System.out.println(temInitialPlayers.get(i).getAvg_rank_raw()+"->"+temInitialPlayers.get(i).getAvg_rank_hitter());
            }
            else if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setAvg_rank_pitcher(++counter2);
                //System.out.println(temInitialPlayers.get(i).getAvg_rank_raw()+"->"+temInitialPlayers.get(i).getAvg_rank_pitcher());
            }
        }
        
        int sum = 0;
        for (int i =0; i < this.getDraftedTeams().size(); i++){
            sum+=this.getDraftedTeams().get(i).getMoneyLeft();
        }
        
        for(int i = 0; i < temInitialPlayers.size(); i++){
            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
                temInitialPlayers.get(i).setEstimatedValue(Math.round((float)sum/temInitialPlayers.get(i).getAvg_rank_hitter()*10.0)/10.0);
            }
            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
                temInitialPlayers.get(i).setEstimatedValue(Math.round((float)sum/temInitialPlayers.get(i).getAvg_rank_pitcher()*10.0)/10.0);
            }
        }
    }
    
//    public void calculateEstimatedValue(){
//        ObservableList<Player> temInitialPlayers = FXCollections.observableArrayList();
//        temInitialPlayers.addAll(initialPlayers);
//        int counter = 0;
//        
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getR()-p2.getR();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getHR()-p2.getHR();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getRBI()-p2.getRBI();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getSB()-p2.getSB();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return Double.valueOf(p1.getBA()).compareTo(Double.valueOf(p2.getBA()));
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getW()-p2.getW();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getSV()-p2.getSV();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return p1.getK()-p2.getK();
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return Double.valueOf(p1.getERA()).compareTo(Double.valueOf(p2.getERA()));
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        counter = 0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return Double.valueOf(p1.getWHIP()).compareTo(Double.valueOf(p2.getWHIP()));
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//                temInitialPlayers.get(i).setR_rank(++counter);
//            }
//        }
//        
//        for(int i = 0; i < initialPlayers.size(); i++){
//            if(initialPlayers.get(i).getTYPE().equals("Hitter")){
//                initialPlayers.get(i).setAvg_rank_raw((initialPlayers.get(i).getR()+initialPlayers.get(i).getHR()+initialPlayers.get(i).getRBI()+initialPlayers.get(i).getSB()+initialPlayers.get(i).getBA())/5);
//            }
//            if(initialPlayers.get(i).getTYPE().equals("Pitcher")){
//                initialPlayers.get(i).setAvg_rank_raw((initialPlayers.get(i).getW()+initialPlayers.get(i).getSV()+initialPlayers.get(i).getK()+initialPlayers.get(i).getERA()+initialPlayers.get(i).getWHIP())/5);
//            }
//        }
//        
//        int counter1 =0;
//        int counter2 =0;
//        Collections.sort(temInitialPlayers,(Player p1, Player p2) -> {
//                return Double.valueOf(p1.getAvg_rank_raw()).compareTo(Double.valueOf(p2.getAvg_rank_raw()));
//            });
//        for(int i = temInitialPlayers.size()-1; i >= 0; i--){
//            if(temInitialPlayers.get(i).getTYPE().equals("Hitter")){
//                temInitialPlayers.get(i).setAvg_rank_hitter(++counter1);
//            }
//            else if(temInitialPlayers.get(i).getTYPE().equals("Pitcher")){
//
//                temInitialPlayers.get(i).setAvg_rank_pitcher(++counter2);
//            }
//        }
//        
//        int sum = 0;
//        for (int i =0; i < this.getDraftedTeams().size(); i++){
//            sum+=this.getDraftedTeams().get(i).getMoneyLeft();
//        }
//        
//        for(int i = 0; i < this.getInitialPlayers().size(); i++){
//            if(this.getInitialPlayers().get(i).getTYPE().equals("Hitter")){
//                this.getInitialPlayers().get(i).setEstimatedValue(Math.round((float)sum/this.getInitialPlayers().get(i).getAvg_rank_hitter()*10.0)/10.0);
//            }
//            if(this.getInitialPlayers().get(i).getTYPE().equals("Pitcher")){
//                this.getInitialPlayers().get(i).setEstimatedValue(Math.round((float)sum/this.getInitialPlayers().get(i).getAvg_rank_pitcher()*10.0)/10.0);
//            }
//        }
//    }
}
