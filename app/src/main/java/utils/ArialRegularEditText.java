package utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by jessica on 7/7/17.
 */

public class ArialRegularEditText extends EditText {
    public ArialRegularEditText(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        this.setTypeface(Typeface.createFromAsset(AppController.getInstance().getAssets(),"appfont/arial-regular.ttf"));
    }

    public ArialRegularEditText(Context context) {
        super(context, null);
    }
}
