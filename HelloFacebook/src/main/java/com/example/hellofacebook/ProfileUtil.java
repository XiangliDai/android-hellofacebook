package com.example.hellofacebook;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by xdai on 11/18/13.
 */
public class ProfileUtil {

    public GraphUser convertJSONObjectToGraphUser(JSONObject graphResponse) {
        GraphUser user = GraphObject.Factory.create(graphResponse, GraphUser.class);
        return user;
    }

    //TODO: clean up this shit
    public String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");
        try{
            // Example: typed access (name)
            // - no special permissions required
            userInfo.append(String.format("Name: %s\n\n", user.getName()));
            if( user.getProperty("gender")!= null){
            userInfo.append(String.format("Gender: %s\n\n", user.getProperty("gender").toString()));
            }
            if( user.getProperty("relationship_status")!= null){
                userInfo.append(String.format("Status: %s\n\n", user.getProperty("relationship_status").toString()));
            }

            if(user.getBirthday() != null){
                String bDay = user.getBirthday().toString();
                Date birthday =  new Date(String.format("%s/%s/%s", bDay.split("/")[0], bDay.split("/")[1], "2000"));
                userInfo.append(String.format("Zodiac: %s\n\n", getZodiac(birthday)));
            }
            // Example: partially typed access, to location field,
            // name key (location)
            // - requires user_location permission

            if(user.getProperty("work") != null){
                JSONArray works = (JSONArray)user.getProperty("work");
                JSONObject work = works.optJSONObject(0);
                userInfo.append(String.format("Work: \n"));
                if(work.has("employer")){
                JSONObject employer = (JSONObject)work.get("employer");
                    userInfo.append(String.format("  at: %s\n",  employer.get("name")));
                }
                if(work.has("position")){
                    JSONObject position = (JSONObject)work.get("position");
                    userInfo.append(String.format("  as: %s\n",  position.get("name")));
                }
                userInfo.append(String.format("\n\n"));
            }
            // Example: partially typed access, to location field,
            // name key (location)
            // - requires user_location permission
            if( user.getLocation() != null)
                userInfo.append(String.format("Location: %s\n\n", user.getLocation().getProperty("name")));

            if(user.getProperty("hometown") != null)
                userInfo.append(String.format("Hometown: %s\n\n", ((JSONObject)user.getProperty("hometown")).optString("name")));
            // Example: access via property name (locale)
            // - no special permissions required
           // userInfo.append(String.format("Locale: %s\n\n",
               //     user.getProperty("locale")));

            // Example: access via key for array (languages)
            // - requires user_likes permission
            if(user.getProperty("languages") != null){
                JSONArray languages = (JSONArray)user.getProperty("languages");
                if (languages.length() > 0) {
                    ArrayList<String> languageNames = new ArrayList<String> ();
                    for (int i=0; i < languages.length(); i++) {
                        JSONObject language = languages.optJSONObject(i);
                        // Add the language name to a list. Use JSON
                        // methods to get access to the name field.
                        languageNames.add(language.optString("name"));
                    }
                    userInfo.append(String.format("Languages: %s\n\n", languageNames.toString()));
                }
            }
            return userInfo.toString();

        } catch (JSONException e) {
            return e.getMessage();
        }

    }


    private String getZodiac(Date date){
        String[] signs = {"Aries", "Taurus","Gemini","Cancer", "Leo","Virgo","Libra","Scorpius",
                "Sagittarius", "Capricorn","Aquarius", "Pisces"};
        String[][] dates = {
                {"12 March", "18 April"},
                {"19 April","13 May"},
                {"14 May","21 June"},
                {"20 June", "20 July"},
                {"21 July","9 August"},
                {"10 August","15 September"},
                {"16 September", "30 October"},
                {"31 October","22 November"},
                {"23 November","17 December"},
                {"18 December", "18 January"},
                {"19 January","15 February"},
                {"16 February","11 March"},

        };
        for(int i=0; i< signs.length; i++){
            Date startDate = new Date(String.format("%s/%s", dates[i][0], "2000"));
            Date endDate = new Date(String.format("%s/%s", dates[i][1], "2000"));
            if(date.getMonth() >= startDate.getMonth()  &&
                    date.getMonth() <= endDate.getMonth()) return signs[i];

        }
        return "";
    }
}
