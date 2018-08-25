package activity.userprofile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.root.skor.R;


public class ResetPasswordConfirmation extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_pasasword_confirmation);
        Button backButton=(Button)findViewById(R.id._backBtn);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordConfirmation.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
