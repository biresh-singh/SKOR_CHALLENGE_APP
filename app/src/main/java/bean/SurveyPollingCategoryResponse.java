package bean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by mac on 2/22/18.
 */

public class SurveyPollingCategoryResponse {
    public ArrayList<SurveyPollingCategory> data = new ArrayList<>();

    public SurveyPollingCategoryResponse(JSONArray jsonArray) {
        int size = jsonArray.length();
        for (int i=0; i<size; i++) {
            try {
                SurveyPollingCategory category = new SurveyPollingCategory(jsonArray.getJSONObject(i));
                data.add(category);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


