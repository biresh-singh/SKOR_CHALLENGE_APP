package bean;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;


public class CalenderUtility {
    public static ArrayList<String> nameOfEvent = new ArrayList<String>();
    public static ArrayList<String> startDates = new ArrayList<String>();
    public static ArrayList<String> endDates = new ArrayList<String>();
    public static ArrayList<String> descriptions = new ArrayList<String>();
    public static ArrayList<String> eventIds=new ArrayList<>();
    public static ArrayList<String> eventLocation=new ArrayList<>();

    public static ArrayList<String> readCalendarEvent(Context context,String start_date,String end_date) {

        String dtstart = "dtstart";
        String dtend = "dtend";
        Calendar endOfDay=null;


     /*   String[] l_projection = new String[] { "title", "dtstart", "dtend" };*/

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Calendar calendar = Calendar.getInstance();
        long after=0l;

        //StartDate is today 10/28/2013
        try {


            Date dateCC = formatter.parse(start_date);
            calendar.setTime(dateCC);
            after = calendar.getTimeInMillis();


            SimpleDateFormat formatterr = new SimpleDateFormat("MM/dd/yy hh:mm:ss");

            endOfDay = Calendar.getInstance();
            Date dateCCC = formatterr.parse(end_date+ " 23:59:59");
            endOfDay.setTime(dateCCC);
        }
        catch(ParseException ex)
        {
            ex.printStackTrace();
        }
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, "(" + dtstart + ">" + after + " and "
                                + dtend + "<" + endOfDay.getTimeInMillis() + ")", null,
                        "dtstart ASC");
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        eventIds.clear();
        for (int i = 0; i < CNames.length; i++) {
            eventIds.add(cursor.getString(0));
            nameOfEvent.add(cursor.getString(1));
            startDates.add(getDate(Long.parseLong(cursor.getString(3))));
            endDates.add(getDate(Long.parseLong(cursor.getString(4))));
            descriptions.add(cursor.getString(2));
            eventLocation.add(cursor.getString(5));
            //CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }

        return nameOfEvent;
    }
    public static ArrayList<String> getStartDateArray()
    {
        return startDates;

    }
    public static ArrayList<String> getDescriptionArray()
    {
        return descriptions;
    }
    public static  ArrayList<String> getEventLocations()
    {
        return eventLocation;
    }
    public static ArrayList<String> getEventsIds()
    {
        return eventIds;
    }
    public static ArrayList<String> getEnddateArray()
    {
        return  endDates;
    }
    /* public ArrayList<String> getLocationArray()
     {

     }*/
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
    static ArrayList<String> removeDuplicates(ArrayList<String> list) {

        // Store unique items in result.
        ArrayList<String> result = new ArrayList<>();

        // Record encountered Strings in HashSet.
        HashSet<String> set = new HashSet<>();

        // Loop over argument list.
        for (String item : list) {

            // If String is not in set, add it to the list and the set.
            if (!set.contains(item)) {
                result.add(item);
                set.add(item);
            }
        }
        return result;
    }
}
