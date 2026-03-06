/**
 * Course: Direct Supply
 * Author: CJ Valente
 */

package model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Represents a single entry in the quiz leaderboard
 * This class stores the rank, player name, and score for a leaderboard
 * row. Each entry corresponds to one row displayed in the leaderboard table
 * shown on the results screen {@code results.fxml}
 */
public class LeaderboardEntry {
    private final SimpleIntegerProperty rank;
    private final SimpleStringProperty name;
    private final SimpleStringProperty score;


    /**
     * Constructs a new leaderboard entry
     * @param rank the player's rank
     * @param name the player's name
     * @param score the player's quiz score
     */
    public LeaderboardEntry(int rank, String name, String score) {
        this.rank = new SimpleIntegerProperty(rank);
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleStringProperty(score);
    }

    /**
     * Returns the JavaFx property corresponding to the player's rank
     * @return the rank
     */
    public SimpleIntegerProperty rankProperty() {
        return rank;
    }

    /**
     * Returns the JavaFX property representing the player's name
     * @return the name property
     */
    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Returns the JavaFX property corresponding to the player's score
     * @return the score property
     */
    public SimpleStringProperty scoreProperty() {
        return score;
    }

}
