package activity.walkthrough;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.root.skor.R;


public class FragmentReplaceAnimationActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animationfragmentreplces);

        fragment = new CustomAnimation(getApplicationContext());
        fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.first, fragment);

        fragmentTransaction.commit();
    }
}
