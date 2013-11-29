package edu.upenn.nettercenter.auni.cookcompetition.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable
public class ScoreField {

    public static int FIELD_TYPE_STUDENT = 1;
    public static int FIELD_TYPE_TEAM = 2;

    @DatabaseField(generatedId = true)
    private long id;

    @DatabaseField
    private String name;

    // A hack to avoid persisting ScoreFieldType in database
    @DatabaseField
    private String scoreFieldTypeName;

    private ScoreFieldType scoreFieldType;

    @DatabaseField
    private int type;

    public ScoreField() {
    }

    public ScoreField(String name, int type, ScoreFieldType scoreFieldType) {
        this.name = name;
        this.type = type;
        this.scoreFieldType = scoreFieldType;
        this.scoreFieldTypeName = scoreFieldType.getName();
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

    public ScoreFieldType getScoreFieldType() {
        if (scoreFieldType == null) {
            if (scoreFieldTypeName.equals(ScoreFieldType.GOLD_SILVER_BRONZE.getName())) {
                scoreFieldType = ScoreFieldType.GOLD_SILVER_BRONZE;
            } else if (scoreFieldTypeName.equals(ScoreFieldType.CHECK_BOX.getName())) {
                scoreFieldType = ScoreFieldType.CHECK_BOX;
            }
        }
        return scoreFieldType;
    }

    public void setScoreFieldType(ScoreFieldType scoreFieldType) {
        this.scoreFieldType = scoreFieldType;
        this.scoreFieldTypeName = scoreFieldType.getName();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScoreField that = (ScoreField) o;
        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
