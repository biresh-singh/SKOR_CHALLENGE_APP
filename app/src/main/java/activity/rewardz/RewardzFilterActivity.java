package activity.rewardz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.root.skor.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adaptor.FilterListViewAdapter;


public class RewardzFilterActivity extends Activity {
    ListView filterListiew;
    String selectedJsonObject;
    ArrayList<String> subCategoryArrayList = new ArrayList<>();
    ArrayList<String> subCategorySlugArrayList = new ArrayList<>();
    TextView done;
    String slug = "all" ;
    LinearLayout back;
    FilterListViewAdapter filterListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_activity);
        filterListiew = (ListView) findViewById(R.id.filter_list);
        done = (TextView) findViewById(R.id.done);
        back = (LinearLayout) findViewById(R.id.back);
        selectedJsonObject = getIntent().getStringExtra("jsonobject_for_selected_item");

        parseJson(selectedJsonObject);
        filterListiew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                slug = subCategorySlugArrayList.get(position);

                filterListViewAdapter.setSelectedIndex(position);
                filterListViewAdapter.notifyDataSetChanged();


            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("sub_category", slug);
                //   intent.putExtra("",);
                setResult(200, intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("sub_category", slug);
                setResult(200, intent);
                finish();
            }
        });

    }

    public void parseJson(String json) {
        try {

            subCategorySlugArrayList.add("All");
            subCategoryArrayList.add("All");
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("sub_categories");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject subCategoryJsonObject = jsonArray.getJSONObject(i);
                subCategoryArrayList.add(subCategoryJsonObject.getString("name"));
                subCategorySlugArrayList.add(subCategoryJsonObject.getString("slug"));

            }
            if (getApplicationContext() != null) {
                filterListViewAdapter = new FilterListViewAdapter(getApplicationContext(), subCategoryArrayList);
                filterListiew.setAdapter(filterListViewAdapter);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("sub_category", slug);
        setResult(200, intent);
        finish();
        // code here to show dialog
        super.onBackPressed();  // optional depending on your needs
    }
}
