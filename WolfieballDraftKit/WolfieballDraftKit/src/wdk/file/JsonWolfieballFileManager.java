package wdk.file;

import static wdk.WDK_StartupConstants.PATH_COURSES;
import wdk.data.Assignment;
import wdk.data.Course;
import wdk.data.Draft;
import wdk.data.CoursePage;
import wdk.data.Instructor;
import wdk.data.Lecture;
import wdk.data.Player;
import wdk.data.ScheduleItem;
import wdk.data.Semester;
import wdk.data.Subject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.json.JsonValue;
import java.text.DecimalFormat;
import static wdk.WDK_StartupConstants.PATH_DRAFTS;
import wdk.data.Team;

/**
 * This is a CourseFileManager that uses the JSON file format to 
 * implement the necessary functions for loading and saving different
 * data for our courses, instructors, and subjects.
 * 
 * @author Richard McKenna
 */
public class JsonWolfieballFileManager implements WolfieballFileManager {
    // JSON FILE READING AND WRITING CONSTANTS
    String JSON_SUBJECTS = "subjects";
    String JSON_SUBJECT = "subject";
    String JSON_NUMBER = "number";
    String JSON_TITLE = "title";
    String JSON_SEMESTER = "semester";
    String JSON_YEAR = "year";
    String JSON_SECTION = "section";
    String JSON_PAGES = "pages";
    String JSON_STARTING_MONDAY = "startingMonday";
    String JSON_ENDING_FRIDAY = "endingFriday";
    String JSON_MONTH = "month";
    String JSON_DAY = "day";
    String JSON_INSTRUCTOR = "instructor";
    String JSON_INSTRUCTOR_NAME = "instructorName";
    String JSON_HOMEPAGE_URL = "homepageURL";
    String JSON_LECTURE_DAYS = "lectureDays";
    String JSON_SCHEDULE_ITEMS = "scheduleItems";
    String JSON_LECTURES = "lectures";
    String JSON_HITTERS = "Hitters";
    String JSON_PITCHERS = "Pitchers";
    String JSON_HWS = "hws";
    String JSON_SCHEDULE_ITEM_DESCRIPTION = "description";
    String JSON_SCHEDULE_ITEM_DATE = "date";
    String JSON_SCHEDULE_ITEM_LINK = "link";
    String JSON_LECTURE_TOPIC = "topic";
    String JSON_LECTURE_SESSIONS = "sessions";
    String JSON_ASSIGNMENT_NAME = "name";
    String JSON_ASSIGNMENT_TOPICS = "topics";
    String JSON_ASSIGNMENT_DATE = "date";
    String JSON_PLAYER_TEAM = "TEAM";
    String JSON_PLAYER_LAST_NAME = "LAST_NAME";
    String JSON_PLAYER_FIRST_NAME = "FIRST_NAME";
    String JSON_POSITION = "POSITION";
    String JSON_PLAYER_QP = "QP";
    String JSON_PLAYER_AB = "AB";
    String JSON_PLAYER_R = "R";
    String JSON_PLAYER_H = "H";
    String JSON_PLAYER_HR = "HR";
    String JSON_PLAYER_RBI = "RBI";
    String JSON_PLAYER_SB = "SB";
    String JSON_PLAYER_IP = "IP";
    String JSON_PLAYER_ER = "ER";
    String JSON_PLAYER_W = "W";
    String JSON_PLAYER_SV = "SV";
    String JSON_PLAYER_H_PITCHER = "H";
    String JSON_PLAYER_BB = "BB";
    String JSON_PLAYER_K = "K";
    String JSON_PLAYER_NOTES = "NOTES";
    String JSON_PLAYER_YEAR_OF_BIRTH = "YEAR_OF_BIRTH";
    String JSON_PLAYER_NATION_OF_BIRTH = "NATION_OF_BIRTH";
    String JSON_EXT = ".json";
    String SLASH = "/";

    public void saveDraft(Draft draftToSave) throws IOException {
        // BUILD THE FILE PATH
        String draftListing = "" + draftToSave.getName();
        String jsonFilePath = PATH_DRAFTS + SLASH + draftListing + JSON_EXT;

        // INIT THE WRITER
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);
        
        // MAKE A JSON ARRAY FOR THE PAGES ARRAY
        JsonArray initialPlayersJsonArray = makeInitialPlayersJsonArray(draftToSave.getInitialPlayers());
        
        // MAKE A JSON ARRAY FOR THE PAGES ARRAY
        JsonArray draftedTeamsJsonArray = makeDraftedTeamsJsonArray(draftToSave.getDraftedTeams());
        
        // MAKE A JSON ARRAY FOR THE PAGES ARRAY
        JsonArray draftSummaryPlayersJsonArray = makeDraftSummaryPlayersJsonArray(draftToSave.getDraftSummaryPlayers());
        
        JsonObject draftJsonObject = Json.createObjectBuilder()
                                    .add("initialPlayers", initialPlayersJsonArray)
                                    .add("draftedTeams", draftedTeamsJsonArray)
                                    .add("draftSummaryPlayers", draftSummaryPlayersJsonArray)
                .build();
        
        // AND SAVE EVERYTHING AT ONCE
        jsonWriter.writeObject(draftJsonObject);
    }
    
    /**
     * This method saves all the data associated with a course to
     * a JSON file.
     * 
     * @param courseToSave The course whose data we are saving.
     * 
     * @throws IOException Thrown when there are issues writing
     * to the JSON file.
     */
    @Override
    public void saveCourse(Course courseToSave) throws IOException {
        // BUILD THE FILE PATH
        String courseListing = "" + courseToSave.getSubject() + courseToSave.getNumber();
        String jsonFilePath = PATH_COURSES + SLASH + courseListing + JSON_EXT;
        
        // INIT THE WRITER
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);  
        
        // MAKE A JSON ARRAY FOR THE PAGES ARRAY
        JsonArray pagesJsonArray = makePagesJsonArray(courseToSave.getPages());
        
        // AND AN OBJECT FOR THE INSTRUCTOR
        JsonObject instructorJsonObject = makeInstructorJsonObject(courseToSave.getInstructor());
        
        // ONE FOR EACH OF OUR DATES
        JsonObject startingMondayJsonObject = makeLocalDateJsonObject(courseToSave.getStartingMonday());
        JsonObject endingFridayJsonObject = makeLocalDateJsonObject(courseToSave.getEndingFriday());
        
        // THE LECTURE DAYS ARRAY
        JsonArray lectureDaysJsonArray = makeLectureDaysJsonArray(courseToSave.getLectureDays());
        
        // THE SCHEDULE ITEMS ARRAY
        JsonArray scheduleItemsJsonArray = makeScheduleItemsJsonArray(courseToSave.getScheduleItems());
        
        // THE LECTURES ARRAY
        JsonArray lecturesJsonArray = makeLecturesJsonArray(courseToSave.getLectures());
        
        // THE HWS ARRAY
        JsonArray hwsJsonArray = makeHWsJsonArray(courseToSave.getAssignments());
        
        // NOW BUILD THE COURSE USING EVERYTHING WE'VE ALREADY MADE
        JsonObject courseJsonObject = Json.createObjectBuilder()
                                    .add(JSON_SUBJECT, courseToSave.getSubject().toString())
                                    .add(JSON_NUMBER, courseToSave.getNumber())
                                    .add(JSON_TITLE, courseToSave.getTitle())
                                    .add(JSON_SEMESTER, courseToSave.getSemester().toString())
                                    .add(JSON_YEAR, courseToSave.getYear())
                                    .add(JSON_PAGES, pagesJsonArray)
                                    .add(JSON_INSTRUCTOR, instructorJsonObject)
                                    .add(JSON_STARTING_MONDAY, startingMondayJsonObject)
                                    .add(JSON_ENDING_FRIDAY, endingFridayJsonObject)
                                    .add(JSON_LECTURE_DAYS, lectureDaysJsonArray)
                                    .add(JSON_SCHEDULE_ITEMS, scheduleItemsJsonArray)
                                    .add(JSON_LECTURES, lecturesJsonArray)
                                    .add(JSON_HWS, hwsJsonArray)
                .build();
        
        // AND SAVE EVERYTHING AT ONCE
        jsonWriter.writeObject(courseJsonObject);
    }
    
    /**
     * Loads the players into the program when user hits the new button.
     * 
     * @throws IOException 
     */
    @Override
    public void loadHitterPlayers(Draft draftToStart, String jsonFilePath) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        
        DecimalFormat df = new DecimalFormat("##0.000");
        
        JsonArray jsonLecturesArray = json.getJsonArray(JSON_HITTERS);
        draftToStart.clearInitialPlayers();
        for (int i = 0; i < jsonLecturesArray.size(); i++) {
            JsonObject jso = jsonLecturesArray.getJsonObject(i);
            Player p = new Player();
            p.setTYPE("Hitter");
            p.setTEAM(jso.getString(JSON_PLAYER_TEAM));
            p.setLAST_NAME(jso.getString(JSON_PLAYER_LAST_NAME));
            p.setFIRST_NAME(jso.getString(JSON_PLAYER_FIRST_NAME));
            p.setQP(jso.getString(JSON_PLAYER_QP));
            p.setAB( Integer.parseInt(jso.getString(JSON_PLAYER_AB)));
            p.setR(Integer.parseInt(jso.getString(JSON_PLAYER_R)));
            p.setH(Integer.parseInt(jso.getString(JSON_PLAYER_H)));
            p.setHR(Integer.parseInt(jso.getString(JSON_PLAYER_HR)));
            p.setRBI(Integer.parseInt(jso.getString(JSON_PLAYER_RBI)));
            p.setSB(Integer.parseInt(jso.getString(JSON_PLAYER_SB)));
            p.setPosition(jso.getString(JSON_PLAYER_QP));
            p.setRW(Integer.parseInt(jso.getString(JSON_PLAYER_R)));
            p.setHRSV(Integer.parseInt(jso.getString(JSON_PLAYER_HR)));
            p.setRBIK(Integer.parseInt(jso.getString(JSON_PLAYER_RBI)));
            p.setSBERA(jso.getString(JSON_PLAYER_SB));
            //p.setBAWHIP(jso.getString(JSON_PLAYER_SB));
            if(Double.parseDouble(jso.getString(JSON_PLAYER_AB)) != 0){
                p.setBAWHIP(String.valueOf(df.format((float)Double.parseDouble(jso.getString(JSON_PLAYER_H))/Double.parseDouble(jso.getString(JSON_PLAYER_AB)))));
                p.setBA(Double.valueOf(df.format((float)Double.parseDouble(jso.getString(JSON_PLAYER_H))/Double.parseDouble(jso.getString(JSON_PLAYER_AB)))));
            }
            else{
                p.setBAWHIP("-");
                p.setBA(0.000);
            }
            p.setNOTES(jso.getString(JSON_PLAYER_NOTES));
            p.setYEAR_OF_BIRTH(Integer.parseInt(jso.getString(JSON_PLAYER_YEAR_OF_BIRTH)));
            p.setNATION_OF_BIRTH(jso.getString(JSON_PLAYER_NATION_OF_BIRTH));
            
            // ADD IT TO THE COURSE
            draftToStart.addPlayer(p);
        }
        
    }
    
    /**
     * Loads the players into the program when user hits the new button.
     * 
     * @throws IOException 
     */
    @Override
    public void loadPitcherPlayers(Draft draftToStart, String jsonFilePath) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        
        DecimalFormat df = new DecimalFormat("##0.00");
        
        JsonArray jsonLecturesArray1 = json.getJsonArray(JSON_PITCHERS);
        //draftToStart.clearInitialPlayers();
        for (int i = 0; i < jsonLecturesArray1.size(); i++) {
            JsonObject jso = jsonLecturesArray1.getJsonObject(i);
            Player p = new Player();
            p.setTYPE("Pitcher");
            p.setTEAM(jso.getString(JSON_PLAYER_TEAM));
            p.setLAST_NAME(jso.getString(JSON_PLAYER_LAST_NAME));
            p.setFIRST_NAME(jso.getString(JSON_PLAYER_FIRST_NAME));
            p.setIP(Double.parseDouble(jso.getString(JSON_PLAYER_IP)));
            p.setER(Integer.parseInt(jso.getString(JSON_PLAYER_ER)));
            p.setW(Integer.parseInt(jso.getString(JSON_PLAYER_W)));
            p.setSV(Integer.parseInt(jso.getString(JSON_PLAYER_SV)));
            p.setH_PITCHER(Integer.parseInt(jso.getString(JSON_PLAYER_H_PITCHER)));
            p.setBB(Integer.parseInt(jso.getString(JSON_PLAYER_BB)));
            p.setK(Integer.parseInt(jso.getString(JSON_PLAYER_K)));
            p.setPosition("P");
            p.setRW(Integer.parseInt(jso.getString(JSON_PLAYER_W)));
            p.setHRSV(Integer.parseInt(jso.getString(JSON_PLAYER_SV)));
            p.setRBIK(Integer.parseInt(jso.getString(JSON_PLAYER_K)));
            if(Double.parseDouble(jso.getString(JSON_PLAYER_IP)) != 0){
                p.setSBERA(df.format((float)(Double.parseDouble(jso.getString(JSON_PLAYER_ER))/Double.parseDouble(jso.getString(JSON_PLAYER_IP)))*9));
                p.setERA(Double.valueOf(df.format((float)(Double.parseDouble(jso.getString(JSON_PLAYER_ER))/Double.parseDouble(jso.getString(JSON_PLAYER_IP)))*9)));
            }
            else{
                p.setSBERA("-");
                p.setERA(0.00);
            }
            //p.setBAWHIP(jso.getString(JSON_PLAYER_SB));
            if(Double.parseDouble(jso.getString(JSON_PLAYER_IP)) != 0){
                p.setBAWHIP(df.format((float)Double.parseDouble(jso.getString(JSON_PLAYER_W))+Double.parseDouble(jso.getString(JSON_PLAYER_H))/Double.parseDouble(jso.getString(JSON_PLAYER_IP))));
                p.setWHIP(Double.valueOf(df.format((float)Double.parseDouble(jso.getString(JSON_PLAYER_W))+(float)Double.parseDouble(jso.getString(JSON_PLAYER_H))/Double.parseDouble(jso.getString(JSON_PLAYER_IP)))));
            }
            else{
                p.setBAWHIP("-");
                p.setWHIP(0.00);
            }
            p.setNOTES(jso.getString(JSON_PLAYER_NOTES));
            p.setYEAR_OF_BIRTH(Integer.parseInt(jso.getString(JSON_PLAYER_YEAR_OF_BIRTH)));
            p.setNATION_OF_BIRTH(jso.getString(JSON_PLAYER_NATION_OF_BIRTH));
            
            // ADD IT TO THE COURSE
            draftToStart.addPlayer(p);
        }
    }
    
    public void loadDraft(Draft draftToLoad, String jsonFilePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(jsonFilePath);
        
        // GET THE LECTURES
        JsonArray jsonInitialPlayersArray = json.getJsonArray("initialPlayers");
        draftToLoad.clearInitialPlayers(); // very import, clear free agent at first
        for (int i = 0; i < jsonInitialPlayersArray.size(); i++) {
            JsonObject jso = jsonInitialPlayersArray.getJsonObject(i);
            Player player = new Player();
            player.setTEAM(jso.getString(JSON_PLAYER_TEAM));
            player.setLAST_NAME(jso.getString(JSON_PLAYER_LAST_NAME));
            player.setFIRST_NAME(jso.getString(JSON_PLAYER_FIRST_NAME));
            player.setPosition(jso.getString(JSON_POSITION));
            player.setYEAR_OF_BIRTH(jso.getInt(JSON_PLAYER_YEAR_OF_BIRTH));
            player.setNATION_OF_BIRTH(jso.getString(JSON_PLAYER_NATION_OF_BIRTH));
            player.setRW(jso.getInt("RW"));
            player.setHRSV(jso.getInt("HRSV"));
            player.setRBIK(jso.getInt("RBIK"));
            player.setSBERA(jso.getString("SBERA"));
            player.setBAWHIP(jso.getString("BAWHIP"));
            player.setTYPE(jso.getString("TYPE"));
            player.setR(jso.getInt("R"));
            player.setHR(jso.getInt("HR"));
            player.setRBI(jso.getInt("RBI"));
            player.setSB(jso.getInt("SB"));
            player.setBA(Double.valueOf(jso.getString("BA")));
            player.setW(jso.getInt("W"));
            player.setSV(jso.getInt("SV"));
            player.setK(jso.getInt("K"));
            player.setERA(Double.valueOf(jso.getString("ERA")));
            player.setWHIP(Double.valueOf(jso.getString("WHIP")));
            player.setNOTES(jso.getString("Notes"));
            player.setCONTRACT(jso.getString("Contract"));
            player.setSALARY(jso.getInt("Salary"));
            player.setFANTASYTEAM(jso.getString("FantasyTeam"));
            player.setFANTASYPOSITION(jso.getString("FantasyPosition"));
            
            // ADD IT TO THE COURSE
            draftToLoad.addPlayer(player);
        }
        
        // GET THE LECTURES
        JsonArray jsonTeamsArray = json.getJsonArray("draftedTeams");
        draftToLoad.clearDraftedTeams(); // very important, clear draft teams
        System.out.print(jsonTeamsArray.size());
        for (int g = 0; g < jsonTeamsArray.size(); g++) {
            JsonObject jso = jsonTeamsArray.getJsonObject(g);
            Team team = new Team();
            
            team.setName(jso.getString("name"));
            team.setOwner(jso.getString("owner"));
            
            JsonArray jsonStartingLineUpArray = jso.getJsonArray("startingLineUp");
            JsonArray jsonTaxiSquadArray = jso.getJsonArray("taxiSquad");
            team.clearStartingLineUpPlayers(); // very important, clear starting line up at first
            team.getCList().clear();
            team.get1BList().clear();
            team.getCIList().clear();
            team.get3BList().clear();
            team.get2BList().clear();
            team.getMIList().clear();
            team.getSSList().clear();
            team.getOFList().clear();
            team.getUList().clear();
            team.getPList().clear();
            if(jsonStartingLineUpArray != null){

            for (int  j= 0; j < jsonStartingLineUpArray.size(); j++) {
                JsonObject jso1 = jsonStartingLineUpArray.getJsonObject(j);
                Player player = new Player();
                player.setTEAM(jso1.getString(JSON_PLAYER_TEAM));
                player.setLAST_NAME(jso1.getString(JSON_PLAYER_LAST_NAME));
                player.setFIRST_NAME(jso1.getString(JSON_PLAYER_FIRST_NAME));
                player.setPosition(jso1.getString(JSON_POSITION));
                player.setYEAR_OF_BIRTH(jso1.getInt(JSON_PLAYER_YEAR_OF_BIRTH));
                player.setNATION_OF_BIRTH(jso1.getString(JSON_PLAYER_NATION_OF_BIRTH));
                player.setRW(jso1.getInt("RW"));
                player.setHRSV(jso1.getInt("HRSV"));
                player.setRBIK(jso1.getInt("RBIK"));
                player.setSBERA(jso1.getString("SBERA"));
                player.setBAWHIP(jso1.getString("BAWHIP"));
                player.setTYPE(jso1.getString("TYPE"));
                player.setR(jso1.getInt("R"));
                player.setHR(jso1.getInt("HR"));
                player.setRBI(jso1.getInt("RBI"));
                player.setSB(jso1.getInt("SB"));
                player.setBA(Double.valueOf(jso1.getString("BA")));
                player.setW(jso1.getInt("W"));
                player.setSV(jso1.getInt("SV"));
                player.setK(jso1.getInt("K"));
                player.setERA(Double.valueOf(jso1.getString("ERA")));
                player.setWHIP(Double.valueOf(jso1.getString("WHIP")));
                player.setNOTES(jso1.getString("Notes"));
                player.setCONTRACT(jso1.getString("Contract"));
                player.setSALARY(jso1.getInt("Salary"));
                player.setFANTASYTEAM(jso1.getString("FantasyTeam"));
                player.setFANTASYPOSITION(jso1.getString("FantasyPosition"));
            
                // ADD IT TO THE COURSE
                //team.getStartingLineUpPlayers().add(player);
                
                if(player.getFANTASYPOSITION().equals("C")){
                        team.getCList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("1B")){
                        team.get1BList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("CI")){
                        team.getCIList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("3B")){
                        team.get3BList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("2B")){
                        team.get2BList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("MI")){
                        team.getMIList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("SS")){
                        team.getSSList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("OF")){
                        team.getOFList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("U")){
                        team.getUList().add(player);
                }
                else if(player.getFANTASYPOSITION().equals("P")){
                        team.getPList().add(player);
                }
            }
            
                    for(int k = 0; k < team.getCList().size();k++){ // load 10 positions lists into starting line up
                        team.addPlayer((Player)team.getCList().get(k));
                    }
                    for(int k = 0; k < team.get1BList().size();k++){
                        team.addPlayer((Player)team.get1BList().get(k));
                    }
                    for(int k = 0; k < team.getCIList().size();k++){
                        team.addPlayer((Player)team.getCIList().get(k));
                    }
                    for(int k = 0; k < team.get3BList().size();k++){
                        team.addPlayer((Player)team.get3BList().get(k));
                    }
                    for(int k = 0; k < team.get2BList().size();k++){
                        team.addPlayer((Player)team.get2BList().get(k));
                    }
                    for(int k = 0; k < team.getMIList().size();k++){
                        team.addPlayer((Player)team.getMIList().get(k));
                    }
                    for(int k = 0; k < team.getSSList().size();k++){
                        team.addPlayer((Player)team.getSSList().get(k));
                    }
                    for(int k = 0; k < team.getOFList().size();k++){
                        team.addPlayer((Player)team.getOFList().get(k));
                    }
                    for(int k = 0; k < team.getUList().size();k++){
                        team.addPlayer((Player)team.getUList().get(k));
                    }
                    for(int k = 0; k < team.getPList().size();k++){
                        team.addPlayer((Player)team.getPList().get(k));
                    }
            
            if(jsonTaxiSquadArray != null){
            for (int  j= 0; j < jsonTaxiSquadArray.size(); j++) {
                JsonObject jso1 = jsonTaxiSquadArray.getJsonObject(j);
                Player player = new Player();
                player.setTEAM(jso1.getString(JSON_PLAYER_TEAM));
                player.setLAST_NAME(jso1.getString(JSON_PLAYER_LAST_NAME));
                player.setFIRST_NAME(jso1.getString(JSON_PLAYER_FIRST_NAME));
                player.setPosition(jso1.getString(JSON_POSITION));
                player.setYEAR_OF_BIRTH(jso1.getInt(JSON_PLAYER_YEAR_OF_BIRTH));
                player.setNATION_OF_BIRTH(jso1.getString(JSON_PLAYER_NATION_OF_BIRTH));
                player.setRW(jso1.getInt("RW"));
                player.setHRSV(jso1.getInt("HRSV"));
                player.setRBIK(jso1.getInt("RBIK"));
                player.setSBERA(jso1.getString("SBERA"));
                player.setBAWHIP(jso1.getString("BAWHIP"));
                player.setTYPE(jso1.getString("TYPE"));
                player.setR(jso1.getInt("R"));
                player.setHR(jso1.getInt("HR"));
                player.setRBI(jso1.getInt("RBI"));
                player.setSB(jso1.getInt("SB"));
                player.setBA(Double.valueOf(jso1.getString("BA")));
                player.setW(jso1.getInt("W"));
                player.setSV(jso1.getInt("SV"));
                player.setK(jso1.getInt("K"));
                player.setERA(Double.valueOf(jso1.getString("ERA")));
                player.setWHIP(Double.valueOf(jso1.getString("WHIP")));
                player.setNOTES(jso1.getString("Notes"));
                player.setCONTRACT(jso1.getString("Contract"));
                player.setSALARY(jso1.getInt("Salary"));
                player.setFANTASYTEAM(jso1.getString("FantasyTeam"));
                player.setFANTASYPOSITION(jso1.getString("FantasyPosition"));
                
                team.getTaxiSquadPlayers().add(player);
            }
            }
            
            }
            
            // ADD IT TO THE COURSE
            draftToLoad.addTeam(team);
        }
        
        // GET THE LECTURES
        JsonArray jsonDraftSummaryPlayersArray = json.getJsonArray("draftSummaryPlayers");
        draftToLoad.getDraftSummaryPlayers().clear();
        for (int i = 0; i < jsonDraftSummaryPlayersArray.size(); i++) {
            JsonObject jso = jsonDraftSummaryPlayersArray.getJsonObject(i);
            Player player = new Player();
            player.setFIRST_NAME(jso.getString(JSON_PLAYER_FIRST_NAME));
            player.setLAST_NAME(jso.getString(JSON_PLAYER_LAST_NAME));
                       player.setFANTASYTEAM(jso.getString("FantasyTeam"));
            player.setCONTRACT(jso.getString("Contract"));
            player.setSALARY(jso.getInt("Salary"));
            
            // ADD IT TO THE COURSE
            draftToLoad.getDraftSummaryPlayers().add(player);
        }
    }
    
    /**
     * Loads the courseToLoad argument using the data found in the json file.
     * 
     * @param courseToLoad Course to load.
     * @param jsonFilePath File containing the data to load.
     * 
     * @throws IOException Thrown when IO fails.
     */
    @Override
    public void loadCourse(Course courseToLoad, String jsonFilePath) throws IOException {
        // LOAD THE JSON FILE WITH ALL THE DATA
        JsonObject json = loadJSONFile(jsonFilePath);
        
        // NOW LOAD THE COURSE
        courseToLoad.setSubject(Subject.valueOf(json.getString(JSON_SUBJECT)));
        courseToLoad.setNumber(json.getInt(JSON_NUMBER));
        courseToLoad.setSemester(Semester.valueOf(json.getString(JSON_SEMESTER)));
        courseToLoad.setYear(json.getInt(JSON_YEAR));
        courseToLoad.setTitle(json.getString(JSON_TITLE));
        
        // GET THE PAGES TO INCLUDE 
        courseToLoad.clearPages();
        JsonArray jsonPagesArray = json.getJsonArray(JSON_PAGES);
        for (int i = 0; i < jsonPagesArray.size(); i++)
            courseToLoad.addPage(CoursePage.valueOf(jsonPagesArray.getString(i)));
        
        // GET THE LECTURE DAYS TO INCLUDE
        courseToLoad.clearLectureDays();
        JsonArray jsonLectureDaysArray = json.getJsonArray(JSON_LECTURE_DAYS);
        for (int i = 0; i < jsonLectureDaysArray.size(); i++)
            courseToLoad.addLectureDay(DayOfWeek.valueOf(jsonLectureDaysArray.getString(i)));

        // LOAD AND SET THE INSTRUCTOR
        JsonObject jsonInstructor = json.getJsonObject(JSON_INSTRUCTOR);
        Instructor instructor = new Instructor( jsonInstructor.getString(JSON_INSTRUCTOR_NAME),
                                                jsonInstructor.getString(JSON_HOMEPAGE_URL));
        courseToLoad.setInstructor(instructor);
        
        // GET THE STARTING MONDAY
        JsonObject startingMonday = json.getJsonObject(JSON_STARTING_MONDAY);
        int year = startingMonday.getInt(JSON_YEAR);
        int month = startingMonday.getInt(JSON_MONTH);
        int day = startingMonday.getInt(JSON_DAY);
        courseToLoad.setStartingMonday(LocalDate.of(year, month, day));

        // GET THE ENDING FRIDAY
        JsonObject endingFriday = json.getJsonObject(JSON_ENDING_FRIDAY);
        year = endingFriday.getInt(JSON_YEAR);
        month = endingFriday.getInt(JSON_MONTH);
        day = endingFriday.getInt(JSON_DAY);
        courseToLoad.setEndingFriday(LocalDate.of(year, month, day));
        
        // GET THE SCHEDULE ITEMS
        courseToLoad.clearScheduleItems();
        JsonArray jsonScheduleItemsArray = json.getJsonArray(JSON_SCHEDULE_ITEMS);
        for (int i = 0; i < jsonScheduleItemsArray.size(); i++) {
            JsonObject jso = jsonScheduleItemsArray.getJsonObject(i);
            ScheduleItem si = new ScheduleItem();
            si.setDescription(jso.getString(JSON_SCHEDULE_ITEM_DESCRIPTION));
            JsonObject jsoDate = jso.getJsonObject(JSON_SCHEDULE_ITEM_DATE);
            year = jsoDate.getInt(JSON_YEAR);
            month = jsoDate.getInt(JSON_MONTH);
            day = jsoDate.getInt(JSON_DAY);            
            si.setDate(LocalDate.of(year, month, day));
            si.setLink(jso.getString(JSON_SCHEDULE_ITEM_LINK));
            
            // ADD IT TO THE COURSE
            courseToLoad.addScheduleItem(si);
        }
        
        // GET THE LECTURES
        JsonArray jsonLecturesArray = json.getJsonArray(JSON_LECTURES);
        courseToLoad.clearLectures();
        for (int i = 0; i < jsonLecturesArray.size(); i++) {
            JsonObject jso = jsonLecturesArray.getJsonObject(i);
            Lecture l = new Lecture();
            l.setTopic(jso.getString(JSON_LECTURE_TOPIC));
            l.setSessions(jso.getInt(JSON_LECTURE_SESSIONS));
            
            // ADD IT TO THE COURSE
            courseToLoad.addLecture(l);
        }
        
        // GET THE HWS
        JsonArray jsonHWsArray = json.getJsonArray(JSON_HWS);
        courseToLoad.clearHWs();
        for (int i = 0; i < jsonHWsArray.size(); i++) {
            JsonObject jso = jsonHWsArray.getJsonObject(i);
            Assignment a = new Assignment();
            a.setName(jso.getString(JSON_ASSIGNMENT_NAME));
            JsonObject jsoDate = jso.getJsonObject(JSON_ASSIGNMENT_DATE);
            year = jsoDate.getInt(JSON_YEAR);
            month = jsoDate.getInt(JSON_MONTH);
            day = jsoDate.getInt(JSON_DAY);            
            a.setDate(LocalDate.of(year, month, day));
            a.setTopics(jso.getString(JSON_ASSIGNMENT_TOPICS));
            
            // ADD IT TO THE COURSE
            courseToLoad.addAssignment(a);
        }
    }
    
    /**
     * This function saves the last instructor to a json file. This provides 
     * a convenience to the user, who is likely always the same instructor.
     * @param lastInstructor Instructor to save.
     * @param jsonFilePath File in which to put the data.
     * @throws IOException Thrown when I/O fails.
     */
    @Override
    public void saveLastInstructor(Instructor lastInstructor, String jsonFilePath) throws IOException {
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os); 
        JsonObject instructorJsonObject = makeInstructorJsonObject(lastInstructor);
        jsonWriter.writeObject(instructorJsonObject);
    }
    
    /**
     * Loads an instructor from the provided file, returning a constructed
     * object to represent it.
     * @param filePath Path of json file containing instructor data.
     * @return A constructed Instructor initialized with the data from the file
     * @throws IOException Thrown when I/O fails.
     */
    @Override
    public Instructor loadLastInstructor(String filePath) throws IOException {
        JsonObject json = loadJSONFile(filePath);
        return buildInstructorJsonObject(json);
    }
    
    /**
     * Saves the subjects list to a json file.
     * @param subjects List of Subjects to save.
     * @param jsonFilePath Path of json file.
     * @throws IOException Thrown when I/O fails.
     */
    @Override
    public void saveSubjects(List<Object> subjects, String jsonFilePath) throws IOException {
        JsonObject arrayObject = buildJsonArrayObject(subjects);
        OutputStream os = new FileOutputStream(jsonFilePath);
        JsonWriter jsonWriter = Json.createWriter(os);  
        jsonWriter.writeObject(arrayObject);        
    }
    
    /**
     * Loads subjects from the json file.
     * @param jsonFilePath Json file containing the subjects.
     * @return List full of Subjects loaded from the file.
     * @throws IOException Thrown when I/O fails.
     */
    @Override
    public ArrayList<String> loadSubjects(String jsonFilePath) throws IOException {
        ArrayList<String> subjectsArray = loadArrayFromJSONFile(jsonFilePath, JSON_SUBJECTS);
        ArrayList<String> cleanedArray = new ArrayList();
        for (String s : subjectsArray) {
            // GET RID OF ALL THE QUOTE CHARACTERS
            s = s.replaceAll("\"", "");
            cleanedArray.add(s);
        }
        return cleanedArray;
    }
    
    // AND HERE ARE THE PRIVATE HELPER METHODS TO HELP THE PUBLIC ONES
    
    // LOADS A JSON FILE AS A SINGLE OBJECT AND RETURNS IT
    private JsonObject loadJSONFile(String jsonFilePath) throws IOException {
        InputStream is = new FileInputStream(jsonFilePath);
        JsonReader jsonReader = Json.createReader(is);
        JsonObject json = jsonReader.readObject();
        jsonReader.close();
        is.close();
        return json;
    }    
    
    // LOADS AN ARRAY OF A SPECIFIC NAME FROM A JSON FILE AND
    // RETURNS IT AS AN ArrayList FULL OF THE DATA FOUND
    private ArrayList<String> loadArrayFromJSONFile(String jsonFilePath, String arrayName) throws IOException {
        JsonObject json = loadJSONFile(jsonFilePath);
        ArrayList<String> items = new ArrayList();
        JsonArray jsonArray = json.getJsonArray(arrayName);
        for (JsonValue jsV : jsonArray) {
            items.add(jsV.toString());
        }
        return items;
    }
    
    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED SCHEDULE ITEM
    private JsonObject makeScheduleItemJsonObject(ScheduleItem scheduleItem) {
        JsonObject date = makeLocalDateJsonObject(scheduleItem.getDate());
        JsonObject jso = Json.createObjectBuilder().add(JSON_SCHEDULE_ITEM_DESCRIPTION, scheduleItem.getDescription())
                                                    .add(JSON_SCHEDULE_ITEM_DATE, date)
                                                    .add(JSON_SCHEDULE_ITEM_LINK, scheduleItem.getLink())
                                                    .build();
        return jso;
    }
    
    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED LECTURE
    private JsonObject makeLectureJsonObject(Lecture lecture) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_LECTURE_TOPIC, lecture.getTopic())
                                                    .add(JSON_LECTURE_SESSIONS, lecture.getSessions())
                                                    .build();
        return jso;
    }
    
    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED ASSIGNMENT
    private JsonObject makeAssignmentJsonObject(Assignment assignment) {
        JsonObject dateJSO = makeLocalDateJsonObject(assignment.getDate());
        JsonObject jso = Json.createObjectBuilder().add(JSON_ASSIGNMENT_NAME, assignment.getName())
                                                    .add(JSON_ASSIGNMENT_TOPICS, assignment.getTopics())
                                                    .add(JSON_ASSIGNMENT_DATE, dateJSO)
                                                    .build();
        return jso;
    }
    
    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED INSTRUCTOR
    private JsonObject makeInstructorJsonObject(Instructor instructor) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_INSTRUCTOR_NAME, instructor.getName())
                                                   .add(JSON_HOMEPAGE_URL, instructor.getHomepageURL())
                                                   .build(); 
        return jso;                
    }

    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED DATE
    private JsonObject makeLocalDateJsonObject(LocalDate dateToSave) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_YEAR, dateToSave.getYear())
                                                   .add(JSON_MONTH, dateToSave.getMonthValue())
                                                   .add(JSON_DAY, dateToSave.getDayOfMonth())
                                                   .build(); 
        return jso;
    }
    
    // BUILDS AND RETURNS THE INSTRUCTOR FOUND IN THE JSON OBJECT
    public Instructor buildInstructorJsonObject(JsonObject json) {
        Instructor instructor = new Instructor( json.getString(JSON_INSTRUCTOR_NAME),
                                                    json.getString(JSON_HOMEPAGE_URL));
        return instructor;
    }

    // BUILDS AND RETURNS A JsonArray CONTAINING ALL THE PAGES FOR THIS COURSE
    public JsonArray makePagesJsonArray(List<CoursePage> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (CoursePage cP : data) {
           jsb.add(cP.toString());
        }
        JsonArray jA = jsb.build();
        return jA;        
    }

    // BUILDS AND RETURNS A JsonArray CONTAINING ALL THE LECTURE DAYS FOR THIS COURSE
    public JsonArray makeLectureDaysJsonArray(List<DayOfWeek> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (DayOfWeek dow : data) {
            jsb.add(dow.toString());
        }
        JsonArray jA = jsb.build();
        return jA;
    }
    
    // MAKE AN ARRAY OF SCHEDULE ITEMS
    private JsonArray makeScheduleItemsJsonArray(ObservableList<ScheduleItem> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (ScheduleItem si : data) {
            jsb.add(makeScheduleItemJsonObject(si));
        }
        JsonArray jA = jsb.build();
        return jA;
    }
    
    // MAKE AN ARRAY OF LECTURE ITEMS
    private JsonArray makeLecturesJsonArray(ObservableList<Lecture> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Lecture l : data) {
            jsb.add(makeLectureJsonObject(l));
        }
        JsonArray jA = jsb.build();
        return jA;
    }
    
    // MAKE AN ARRAY OF ASSIGNMENTS
    public JsonArray makeHWsJsonArray(ObservableList<Assignment> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Assignment a : data) {
            jsb.add(this.makeAssignmentJsonObject(a));
        }
        JsonArray jA = jsb.build();
        return jA;
    }
    
    // MAKE AN ARRAY OF LECTURE ITEMS
    private JsonArray makeInitialPlayersJsonArray(ObservableList<Player> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Player p : data) {
            jsb.add(makeInitialPlayersJsonObject(p));
        }
        JsonArray jA = jsb.build();
        return jA;
    }

    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED LECTURE
    private JsonObject makeInitialPlayersJsonObject(Player player) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_PLAYER_TEAM, player.getTEAM())
                                                   .add(JSON_PLAYER_LAST_NAME, player.getLAST_NAME())
                                                   .add(JSON_PLAYER_FIRST_NAME, player.getFIRST_NAME())
                                                   .add(JSON_POSITION, player.getPosition())
                                                   //.add(JSON_PLAYER_TEAM, player.getTEAM())
                                                   .add(JSON_PLAYER_YEAR_OF_BIRTH, player.getYEAR_OF_BIRTH())
                                                   .add(JSON_PLAYER_NATION_OF_BIRTH, player.getNATION_OF_BIRTH())
                                                   //.add(JSON_POSITION, player.getPosition())
                                                   .add("RW", player.getRW())
                                                   .add("HRSV", player.getHRSV())
                                                   .add("RBIK", player.getRBIK())
                                                   .add("SBERA", player.getSBERA())
                                                   .add("BAWHIP", player.getBAWHIP())
                                                   .add("TYPE", player.getTYPE())
                                                   .add("R", player.getR())
                                                   .add("HR", player.getHR())
                                                   .add("RBI", player.getRBI())
                                                   .add("SB", player.getSB())
                                                   .add("BA", Double.toString(player.getBA()))
                                                   .add("W", player.getW())
                                                   .add("SV", player.getSV())
                                                   .add("K", player.getK())
                                                   .add("ERA", Double.toString(player.getERA()))
                                                   .add("WHIP", Double.toString(player.getWHIP()))
                                                   .add("Notes", player.getNOTES())
                                                   .add("Contract", player.getCONTRACT())
                                                   .add("Salary", player.getSALARY())
                                                   .add("FantasyTeam", player.getFANTASYTEAM())
                                                   .add("FantasyPosition", player.getFANTASYPOSITION())
                                                   .build();
        return jso;
    }
    
    // MAKE AN ARRAY OF LECTURE ITEMS
    private JsonArray makeDraftedTeamsJsonArray(ObservableList<Team> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Team t : data) {
            jsb.add(makeDraftedTeamsJsonObject(t));
        }
        JsonArray jA = jsb.build();
        return jA;
    }

    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED LECTURE
    private JsonObject makeDraftedTeamsJsonObject(Team team) {
        JsonArray startingLineUpJsonArray = makeInitialPlayersJsonArray(team.getStartingLineUpPlayers());
        JsonArray taxiSquadJsonArray = makeInitialPlayersJsonArray(team.getTaxiSquadPlayers());
        
        JsonObject jso = Json.createObjectBuilder().add("name", team.getName())
                                                   .add("owner", team.getOwner())
                                                   .add("startingLineUp", startingLineUpJsonArray)
                                                   .add("taxiSquad", taxiSquadJsonArray)
                                                   .build();
        return jso;
    }
    
    // MAKE AN ARRAY OF LECTURE ITEMS
    private JsonArray makeDraftSummaryPlayersJsonArray(ObservableList<Player> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Player p : data) {
            jsb.add(makeDraftSummaryPlayersJsonObject(p));
        }
        JsonArray jA = jsb.build();
        return jA;
    }

    // MAKES AND RETURNS A JSON OBJECT FOR THE PROVIDED LECTURE
    private JsonObject makeDraftSummaryPlayersJsonObject(Player player) {
        JsonObject jso = Json.createObjectBuilder().add(JSON_PLAYER_FIRST_NAME, player.getFIRST_NAME())
                                                   .add(JSON_PLAYER_LAST_NAME, player.getLAST_NAME())
                                                   .add(JSON_POSITION, player.getPosition())
                                                   .add("FantasyTeam", player.getFANTASYTEAM())
                                                   .add("Contract", player.getCONTRACT())
                                                   .add("Salary", player.getSALARY())
                                                   .build();
        return jso;
    }
    
    // BUILDS AND RETURNS A JsonArray CONTAINING THE PROVIDED DATA
    public JsonArray buildJsonArray(List<Object> data) {
        JsonArrayBuilder jsb = Json.createArrayBuilder();
        for (Object d : data) {
           jsb.add(d.toString());
        }
        JsonArray jA = jsb.build();
        return jA;
    }

    // BUILDS AND RETURNS A JsonObject CONTAINING A JsonArray
    // THAT CONTAINS THE PROVIDED DATA
    public JsonObject buildJsonArrayObject(List<Object> data) {
        JsonArray jA = buildJsonArray(data);
        JsonObject arrayObject = Json.createObjectBuilder().add(JSON_SUBJECTS, jA).build();
        return arrayObject;
    }
}
