package com.example.dellpc.todo;

/**
 * Created by dell pc on 13-Jan-17.
 */

public class Activities {
    private String activityName;
    private String categName;
    private String dataTime;
    private String refKey;

    public Activities(){

    }

    public Activities(String mActivityName, String mCategName, String mDataTime, String mRefKey){
        activityName = mActivityName;
        categName = mCategName;
        dataTime = mDataTime;
        refKey = mRefKey;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getCategName() {
        return categName;
    }

    public String getDataTime() {
        return dataTime;
    }

    public String getRefKey() {
        return refKey;
    }


    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public void setValues(Activities newActy){
        activityName = newActy.activityName;
    }
}
