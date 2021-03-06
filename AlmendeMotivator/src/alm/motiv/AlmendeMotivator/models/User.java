package alm.motiv.AlmendeMotivator.models;

import android.text.format.Time;
import com.mongodb.BasicDBObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by AsterLaptop on 4/1/14.
 */
public class User extends BasicDBObject {
   // private String facebookID;

    public User(){}

    public User(String facebookID, String name){
        put("facebookID", facebookID);
        setName(name);
    }

    public void setAbout(String about){
        put("about", about);
    }

    public String getAbout(){
        return this.getString("about");
    }

    public void setAge(String age){
        put("age", age);
    }

    public String getAge(){
        return this.getString("age");
    }

    public void setCity(String city){
        put("city", city);
    }

    public String getCity(){
        return this.getString("city");
    }

    //TODO make arraylist, subarray inside user
    public void setSports(String sports){
        put("sports", sports);
    }

    public String getSports(){
        return this.getString("sports");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getName(){
        return this.getString("name");
    }

    public void setGoal(String goal){
        put("goal",goal);
    }

    public String getGoal(){
        return this.getString("goal");
    }

    public int getXP(){
        return this.getInt("XP");
    }

    public void setXP(int XP){
        this.put("XP", XP);
    }

    public void updateLoginDate(){
        long time = System.currentTimeMillis();
        this.put("lastlogin", time);
    }

    public long getLoginDate(){
        long lastLogin = 0;
        long time = System.currentTimeMillis();

        try{
            lastLogin = this.getLong("lastlogin");
        }catch (Exception e){
            this.put("lastlogin", time);
        }
        if(lastLogin != 0){
            return lastLogin;
        }

        return time;
    }

    public ArrayList<BasicDBObject> getFriends(){
        return (ArrayList<BasicDBObject>) this.get("friends");
    }
}