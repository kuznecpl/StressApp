package org.hcilab.projects.stressblocks.tetris.constants;

/**
 * Created by Sanjeev on 19.01.2017.
 */

public class StringConstants {

    public static final String USER_INFORMATION = "User Information";
    public static final String USER_INFORMATION_INFO = "Dear User,\n" +
            "this app is part of a student project at the University of Stuttgart, Germany, intended to analyze the effects of smartphone usage, especially notifications, on the stress level of the user.\n" +
            " \n" +
            "As such, we would like to collect incoming notifications including this information:\n" +
            " \n" +
            "-\tTimestamp of each notification\n" +
            "-\tApp each notification was sent from\n" +
            "-\tTitle length of each notification \n" +
            "-\tLength of each notification text\n" +
            "-\tEmoticons of each notification text\n" +
            "-\tTimestamp of unlocking the phone\n" +
            "-\tTimestamp of turning on the phone\n" +
            "-\tStressLevels of the ratings\n" +
            " \n" +
            "This data will be securely sent to servers at the University of Stuttgart and stored only for the purpose of analysis and evaluation. No data will be linked to personal information and all data will be handled securely.\n" +
            " \n" +
            "If you have questions or concerns, do not hesitate to contact hcilab@gmail.com\n";

    public static final String ANDROID_SETTINGS_NOTIFICATION_LISTENER = "android.settings." +
            "ACTION_NOTIFICATION_LISTENER_SETTINGS";

    public static final String EXIT_MESSAGE = "If you exit Stress Blocks, the current game state will be deleted!"
            + "\n" + "\n" + "Do you want to exit Stress Blocks?";

    public static final String GOLD_BLOCKS = "gold_blocks";

    public static final String NO_INTERNET_MESSAGE = "No Internet." + "\n" + "Please try again.";

    public static final String NOTIFICATION_TIMESTAMP = "notification_timestamp";

    public static final String USER_SCORES = "user_scores";

    public static final String HIGHSCORE = "highscore";


}
