package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jessica on 7/5/17.
 */

public class RobotoRegularTextView extends TextView {
    public RobotoRegularTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(), "appfont/Roboto-Regular.ttf"));
    }

    public RobotoRegularTextView(Context context) {
        super(context, null);
    }
}
