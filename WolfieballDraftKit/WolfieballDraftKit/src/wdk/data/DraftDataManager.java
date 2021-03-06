package wdk.data;

import wdk.file.WolfieballFileManager;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * This class manages a Course, which means it knows how to
 * reset one with default values and generate useful dates.
 * 
 * @author Richard McKenna
 */
public class DraftDataManager {
    // THIS IS THE COURSE BEING EDITED
    Course course;
    
    Draft draft;
    
    // THIS IS THE UI, WHICH MUST BE UPDATED
    // WHENEVER OUR MODEL'S DATA CHANGES
    CourseDataView view;
    
    // THIS HELPS US LOAD THINGS FOR OUR COURSE
    WolfieballFileManager fileManager;
    
    // DEFAULT INITIALIZATION VALUES FOR NEW COURSES
    static Subject  DEFAULT_COURSE_SUBJECT = Subject.CSE;
    static int      DEFAULT_NUM = 0;
    static String   DEFAULT_TEXT = "Unknown";
    static Semester DEFAULT_SEMESTER = Semester.FALL;
    
    public DraftDataManager(   CourseDataView initView,
                                Instructor lastInstructor) {
        view = initView;
        course = new Course(lastInstructor);
        draft = new Draft();
    }
    
    /**
     * Accessor method for getting the Course that this class manages.
     */
    public Course getCourse() {
        return course;
    }
    
    public Draft getDraft(){
        return draft;
    }
    
    /**
     * Accessor method for getting the file manager, which knows how
     * to read and write course data from/to files.
     */
    public WolfieballFileManager getFileManager() {
        return fileManager;
    }

    /**
     * Resets the course to its default initialized settings, triggering
     * the UI to reflect these changes.
     */
    public void reset() {
        // CLEAR ALL THE COURSE VALUES
        course.setSubject(DEFAULT_COURSE_SUBJECT);
        course.setNumber(DEFAULT_NUM);
        course.setTitle(DEFAULT_TEXT);
        course.setSemester(DEFAULT_SEMESTER);
        course.setYear(LocalDate.now().getYear());
        LocalDate nextMonday = getNextMonday();
        course.setStartingMonday(nextMonday);
        course.setEndingFriday(getNextFriday(nextMonday));
        course.clearLectureDays();
        course.clearPages();
        
        // AND THEN FORCE THE UI TO RELOAD THE UPDATED COURSE
        view.reloadCourse(draft);
    }
    
    // PRIVATE HELPER METHODS
    
    private LocalDate getNextMonday() {
        LocalDate date = LocalDate.now();
        while (date.getDayOfWeek() != DayOfWeek.MONDAY) {
            date = date.plusDays(1);
        }
        return date;
    }
    
    private LocalDate getNextFriday(LocalDate monday) {
        return monday.plusDays(4);
    }
}
