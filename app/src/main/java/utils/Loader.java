package utils;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.root.skor.R;


public class Loader {
    public static Dialog dialog;
    public static void showProgressDialog(Context mContext) {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.loader);
        dialog.show();
    }

    public static void dialogDissmiss(Context context){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
            dialog.hide();
        }
    }

    public static boolean isDialogShowing(){
        if(dialog!=null){
            return dialog.isShowing();
        }else{
            return false;
        }
    }
}