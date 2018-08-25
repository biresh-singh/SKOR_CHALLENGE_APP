package fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.root.skor.R;


public class BeFitAndHealthyFragment extends Fragment {
    private TextView befitandhealthytext;
    private ImageView greetimageanimationid;
    private TextView swipetext;
    private RelativeLayout befitandhealthylayout;
    private Button getstarted;
    private LinearLayout befitandhealthytextid;
    public String baseUrl;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.befitandhealthy, null);
        this.befitandhealthytextid = (LinearLayout) rootView.findViewById(R.id.befitandhealthytextid);
        this.getstarted = (Button) rootView.findViewById(R.id.get_started);
        this.befitandhealthylayout = (RelativeLayout) rootView.findViewById(R.id.befitandhealthylayout);
        this.swipetext = (TextView) rootView.findViewById(R.id.swipetext);
        this.greetimageanimationid = (ImageView) rootView.findViewById(R.id.greetimageanimationid);
        this.befitandhealthytext = (TextView) rootView.findViewById(R.id.befitandhealthytext);
        baseUrl = getResources().getString(R.string.base_url);

        return rootView;
    }
}
