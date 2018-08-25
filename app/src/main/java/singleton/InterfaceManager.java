package singleton;

import android.text.Html;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jessica on 6/13/17.
 */

public class InterfaceManager {
    private static InterfaceManager INTERFACEMANAGER = null;
    private Boolean isErrMsgShown = false;

    public static InterfaceManager sharedInstance() {
        if (INTERFACEMANAGER == null) {
            INTERFACEMANAGER = new InterfaceManager();
        }
        return INTERFACEMANAGER;
    }
    public InterfaceManager() {
    }
    public String getInitial(String s){

        int wordCount = 0;
        String letters;
        boolean word = false;
        int endOfLine = s.length() - 1;

        letters = s.charAt(0)+"";
        for (int i = 0; i < s.length(); i++) {
            if(wordCount == 1){
                break;
            }
            // if the char is a letter, word = true.
            if (Character.isLetter(s.charAt(i)) && i != endOfLine) {
                word = true;
                // if char isn't a letter and there have been letters before,
                // counter goes up.
            } else if (!Character.isLetter(s.charAt(i)) && word) {
                if(i+1 != s.length()){
                    wordCount++;
                    word = false;
                    letters+=s.charAt(i+1);
                }
            }
        }
        return letters;
    }

    public Long utcToDateInMillis(String dateInUTC){
        Long dateInMillis = new Long("0");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        try{
            dateInMillis = sdf.parse(dateInUTC).getTime();
        }catch (ParseException e){
            e.printStackTrace();
        }
        return dateInMillis;
    }

    public String millisToDateInWallet(Long dateInMillis){
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm a");
        sdf.setTimeZone(TimeZone.getDefault());
        date = sdf.format(dateInMillis);
        return date;
    }

    public String millisToDateInVoucher(Long dateInMillis){
        String date = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd - MMM - yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        date = sdf.format(dateInMillis);
        return date;
    }

    public String stripHtml(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            return Html.fromHtml(html).toString();
        }
    }
}

