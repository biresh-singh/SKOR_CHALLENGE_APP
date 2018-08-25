package bean;



import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class Utility {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    public static ArrayList<String> typeOfEvent = new ArrayList<String>();
    public static ArrayList<String> grettingstartDates = new ArrayList<String>();
    public static ArrayList<String> grettingTypeEvents = new ArrayList<String>();
    public static ArrayList<String> appointmentsstartDates = new ArrayList<String>();
    public static ArrayList<String> appointmentsTypesEvent = new ArrayList<String>();




    public static ArrayList<String> readCalendarEvent(Context context) {


        return nameOfEvent;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    public  static void addEvent(String event , String date,String event_type){
        if(event_type.equals("event"))
        {
            typeOfEvent.add(event_type);
            startDates.add(date);
        }
        else if(event_type.equals("greeting"))
        {
            grettingstartDates.add(date);
            grettingTypeEvents.add(event_type);
        }
        else if(event_type.equals("appointments"))
        {
            appointmentsstartDates.add(date);
            appointmentsTypesEvent.add(event_type);
        }
        nameOfEvent.add(event);

        endDates.add("");
        descriptions.add("");

    }
}
