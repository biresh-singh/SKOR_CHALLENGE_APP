package bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mac on 2/22/18.
 */

public class SurveyPollingResponse {
    public int count;
    public String next;
    public String prev;
    public List<SurveyPolling> results = new ArrayList<>();

    public SurveyPollingResponse(JSONObject jsonObject) {
        try {
            if (jsonObject.has("count"))
                count = jsonObject.getInt("count");

            if (jsonObject.has("next"))
                next = jsonObject.getString("next");

            if (jsonObject.has("prev"))
                prev = jsonObject.getString("prev");

            if (jsonObject.has("results")) {
                JSONArray resultsJsonArray = jsonObject.getJSONArray("results");
                int size = resultsJsonArray.length();
                for (int i = 0; i<size; i++) {
                    try {
                        SurveyPolling surveyPolling = new SurveyPolling(resultsJsonArray.getJSONObject(i));
                        results.add(surveyPolling);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}


