package fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.root.skor.R;

import activity.userprofile.LoginActivity;
import database.SharedDatabase;


public class GetRewardzFragment extends Fragment {
    Button getStartedButton;
    String flag = "one";
    private Button getstarted;
    private TextView getrewardztext;
    private ImageView greetimageanimationid;
    private TextView rewardztext;
    SharedDatabase sharedDatabase;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.getreward, null);
        this.rewardztext = (TextView) rootView.findViewById(R.id.rewardztext);
        this.greetimageanimationid = (ImageView) rootView.findViewById(R.id.greetimageanimationid);
        this.getrewardztext = (TextView) rootView.findViewById(R.id.getrewardztext);
        this.getstarted = (Button) rootView.findViewById(R.id.get_started);
        sharedDatabase = new SharedDatabase(getActivity());
        sharedDatabase.setFlag(flag);
        getStartedButton = (Button) rootView.findViewById(R.id.get_started);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "two";
                sharedDatabase.setFlag(flag);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });
        return rootView;
    }
}
