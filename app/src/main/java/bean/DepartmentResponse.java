package bean;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Ferry on 2/27/18.
 */

public class DepartmentResponse {
    public List<Department> data = new ArrayList<>();

    public DepartmentResponse(JSONArray jsonArray) {
        int size = jsonArray.length();
        for (int i=0; i<size; i++) {
            try {
                Department department = new Department(jsonArray.getJSONObject(i));
                data.add(department);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
