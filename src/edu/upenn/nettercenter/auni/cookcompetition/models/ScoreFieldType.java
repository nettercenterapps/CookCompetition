package edu.upenn.nettercenter.auni.cookcompetition.models;

import android.graphics.Color;

import java.util.Arrays;
import java.util.List;

public class ScoreFieldType {

    public static final ScoreFieldType GOLD_SILVER_BRONZE = new ScoreFieldType(
            "Gold/Silver/Bronze",
            Arrays.asList(
                    new ScoreFieldValue("Gold", 5, Color.parseColor("#FFD700")),
                    new ScoreFieldValue("Silver", 3, Color.parseColor("#C0C0C0")),
                    new ScoreFieldValue("Bronze", 1, Color.parseColor("#B8860B"))
            )
    );
    public static final ScoreFieldType CHECK_BOX = new ScoreFieldType(
            "Check Box",
            Arrays.asList(new ScoreFieldValue("Checked", 5))
    );


    private String name;
    private List<ScoreFieldValue> values;

    public ScoreFieldType(String name, List<ScoreFieldValue> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ScoreFieldValue> getValues() {
        return values;
    }

    public void setValues(List<ScoreFieldValue> values) {
        this.values = values;
    }
}
