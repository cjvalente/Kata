package model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeadboardEntry {
    private final SimpleIntegerProperty rank;
    private final SimpleStringProperty name;
    private final SimpleStringProperty score;


    public LeadboardEntry(int rank, String name, String score) {
        this.rank = new SimpleIntegerProperty(rank);
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleStringProperty(score);
    }




    public int getRank() {
        return rank.get();
    }

    public String getName() {
        return name.get();
    }

    public String getScore() {
        return score.get();
    }

    public SimpleIntegerProperty rankProperty() {
        return rank;
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public SimpleStringProperty scoreProperty() {
        return score;
    }

}
