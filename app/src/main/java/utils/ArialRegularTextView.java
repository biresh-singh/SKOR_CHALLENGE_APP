package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jessica on 7/6/17.
 */

public class ArialRegularTextView extends TextView {
    public ArialRegularTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(),"appfont/arial-regular.ttf"));
    }

    public ArialRegularTextView(Context context) {
        super(context, null);
    }
}
