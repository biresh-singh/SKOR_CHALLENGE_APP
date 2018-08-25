package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by jessica on 7/5/17.
 */

public class ArialBoldTextView extends TextView {
    public ArialBoldTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(),"appfont/arial-bold.ttf"));
    }
    public ArialBoldTextView(Context context) {
        super(context, null);
    }
}
