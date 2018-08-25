package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by Dihardja Software Solutions on 5/23/16.
 */
public class ArialNarrowTextView extends TextView {
    public ArialNarrowTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(),"appfont/arial-narrow.ttf"));
    }

    public ArialNarrowTextView(Context context) {
        super(context, null);
    }
}
