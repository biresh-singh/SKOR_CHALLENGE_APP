package CustomClass;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by DSS14 on 9/19/17.
 */

public class RobotoRegularTextView extends TextView {
    public RobotoRegularTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            this.setTypeface(getRobotoTypeFace());
        }
    }

    public Typeface getRobotoTypeFace() {
        Typeface type = Typeface.createFromAsset(getResources().getAssets(), "appfont/Roboto-Regular.ttf");
        return type;
    }
}