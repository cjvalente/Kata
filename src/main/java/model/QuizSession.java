/**
 * Course: Direct Supply
 * Author: CJ Valente
 */
package model;

/**
 * Keeps track of the name variable that stays constant throughout the program.
 */
public class QuizSession {
    private static String name = "";


    public static String getName() {
        return name;
    }

    public static void setName(String name) {
        QuizSession.name = name;
    }
}
