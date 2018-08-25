package CustomClass;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by DSS14 on 9/19/17.
 */

public class RobotoRegularEditText extends EditText {
    public RobotoRegularEditText(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            this.setTypeface(getOpenSansTypeFace());
        }
    }

    public Typeface getOpenSansTypeFace() {
        Typeface type = Typeface.createFromAsset(getResources().getAssets(), "appfont/Roboto-Regular.ttf");
        return type;
    }
}
