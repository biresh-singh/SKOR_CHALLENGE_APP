package activity.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.root.skor.R;

import java.util.ArrayList;

import activity.userprofile.MainActivity;
import adaptor.MultipleAddressAdapter;


public class MultipleAddressActivity extends Activity {
    ArrayList<String> addressArray;
    LinearLayout back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_address);
        //getActionBar().hide();
        RelativeLayout titlebar = (RelativeLayout) findViewById(R.id.titlebar);
        ListView listViewMultipleAddress = (ListView) findViewById(R.id.listformultiple_address);
        back = (LinearLayout) findViewById(R.id.back_arrow);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        addressArray = getIntent().getStringArrayListExtra("address_array");
        // ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, addressArray);
        if (getApplicationContext() != null) {
            MultipleAddressAdapter multipleAddressAdapter = new MultipleAddressAdapter(getApplicationContext(), addressArray);

            listViewMultipleAddress.setAdapter(multipleAddressAdapter);
        }
        listViewMultipleAddress.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                MainActivity.isInternetConnection = false;
                intent.putExtra("position", position);
                setResult(100, intent);
                finish();


            }
        });

    }

}
