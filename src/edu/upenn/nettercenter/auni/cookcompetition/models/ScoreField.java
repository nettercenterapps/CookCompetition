package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ScoreField {

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    @DatabaseField
    private int type;

    public ScoreField() {
    }

    public ScoreField(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
