package wdk;

/**
 * These are properties that are to be loaded from properties.xml. They
 * will provide custom labels and other UI details for our Course Site Builder
 * application. The reason for doing this is to swap out UI text and icons
 * easily without having to touch our code. It also allows for language
 * independence.
 * 
 * @author Richard McKenna
 */
public enum WDK_PropertyType {
        // LOADED FROM properties.xml
        PROP_APP_TITLE,
        
        // APPLICATION ICONS
        NEW_COURSE_ICON,
        LOAD_COURSE_ICON,
        SAVE_COURSE_ICON,
        VIEW_SCHEDULE_ICON,
        EXPORT_PAGE_ICON,
        TEAM_ICON,
        PLAYER_ICON,
        STANDING_ICON,
        DRAFT_ICON,
        MLB_ICON,
        DELETE_ICON,
        EXIT_ICON,
        ADD_ICON,
        MINUS_ICON,
        EDIT_ICON,
        MOVE_UP_ICON,
        MOVE_DOWN_ICON,
        SELECT_PLAYER_ICON,
        START_AUTO_DRAFT_ICON,
        PAUSE_AUTO_DRAFT_ICON,
        
        // APPLICATION TOOLTIPS FOR BUTTONS
        NEW_COURSE_TOOLTIP,
        LOAD_COURSE_TOOLTIP,
        SAVE_COURSE_TOOLTIP,
        VIEW_SCHEDULE_TOOLTIP,
        EXPORT_PAGE_TOOLTIP,
        TEAM_TOOLTIP,
        PLAYER_TOOLTIP,
        STANDING_TOOLTIP,
        DRAFT_TOOLTIP,
        MLB_TOOLTIP,
        DELETE_TOOLTIP,
        EXIT_TOOLTIP,
        ADD_ITEM_TOOLTIP,
        REMOVE_ITEM_TOOLTIP,
        EDIT_ITEM_TOOLTIP,
        SELECT_PLAYER_TOOLTIP,
        START_AUTO_DRAFT_TOOLTIP,
        PAUSE_AUTO_DRAFT_TOOLTIP,
        ADD_LECTURE_TOOLTIP,
        REMOVE_LECTURE_TOOLTIP,
        MOVE_UP_LECTURE_TOOLTIP,
        MOVE_DOWN_LECTURE_TOOLTIP,
        ADD_HW_TOOLTIP,
        REMOVE_HW_TOOLTIP,        

        // FOR COURSE EDIT WORKSPACE
        COURSE_HEADING_LABEL,
        COURSE_INFO_LABEL,
        COURSE_SUBJECT_LABEL,
        COURSE_NUMBER_LABEL,
        COURSE_SEMESTER_LABEL,
        COURSE_YEAR_LABEL,
        COURSE_TITLE_LABEL,
        INSTRUCTOR_NAME_LABEL,
        INSTRUCTOR_URL_LABEL,
        PAGES_SELECTION_HEADING_LABEL,
        PLAYERS_SCREEN_HEADING_LABEL,
        TEAMS_SCREEN_HEADING_LABEL,
        STANDINGS_SCREEN_HEADING_LABEL,
        DRAFT_SCREEN_HEADING_LABEL,
        MLB_SCREEN_HEADING_LABEL,
        SCHEDULE_ITEMS_HEADING_LABEL,
        LECTURES_HEADING_LABEL,
        HWS_HEADING_LABEL,
        SEARCH_LABEL,
        POSITION_ALL_LABEL,
        POSITION_C_LABEL,
        POSITION_1B_LABEL,
        POSITION_CI_LABEL,
        POSITION_3B_LABEL,
        POSITION_2B_LABEL,
        POSITION_MI_LABEL,
        POSITION_SS_LABEL,
        POSITION_OF_LABEL,
        POSITION_U_LABEL,
        POSITION_P_LABEL,
        DRAFT_NAME_LABEL,
        SELECT_FANTASY_TEAM_LABEL,
        STARTING_LINEUP_LABEL,
        TAXI_SQUAD_LABEL,

        // PAGE CHECKBOX LABELS
        INDEX_CHECKBOX_LABEL,
        SYLLABUS_CHECKBOX_LABEL,
        SCHEDULE_CHECKBOX_LABEL,
        HWS_CHECKBOX_LABEL,
        PROJECTS_CHECKBOX_LABEL,
                
        // FOR SCHEDULE EDITING
        SCHEDULE_HEADING_LABEL,
        DATE_BOUNDARIES_LABEL,
        STARTING_MONDAY_LABEL,
        ENDING_FRIDAY_LABEL,
        LECTURE_DAY_SELECT_LABEL,
        
        // ERROR DIALOG MESSAGES
        START_DATE_AFTER_END_DATE_ERROR_MESSAGE,
        START_DATE_NOT_A_MONDAY_ERROR_MESSAGE,
        END_DATE_NOT_A_FRIDAY_ERROR_MESSAGE,
        ILLEGAL_DATE_MESSAGE,
        
        // AND VERIFICATION MESSAGES
        NEW_COURSE_CREATED_MESSAGE,
        COURSE_LOADED_MESSAGE,
        COURSE_SAVED_MESSAGE,
        SITE_EXPORTED_MESSAGE,
        SAVE_UNSAVED_WORK_MESSAGE,
        REMOVE_ITEM_MESSAGE
}
