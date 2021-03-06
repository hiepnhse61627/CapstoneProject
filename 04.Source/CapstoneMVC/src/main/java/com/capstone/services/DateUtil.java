package com.capstone.services;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtil {

    // List of all date formats that we want to parse.
    // Add your own format here.
    private static List<SimpleDateFormat>
            dateFormats = new ArrayList<SimpleDateFormat>() {
        private static final long serialVersionUID = 1L;
        {
//            add(new SimpleDateFormat("M/dd/yyyy"));
//            add(new SimpleDateFormat("dd.M.yyyy"));
//            add(new SimpleDateFormat("M/dd/yyyy hh:mm:ss a"));
//            add(new SimpleDateFormat("dd.M.yyyy hh:mm:ss a"));
//            add(new SimpleDateFormat("dd.MMM.yyyy"));
//            add(new SimpleDateFormat("dd-MM-yyyy hh:mm"));
            add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        }
    };

    /**
     * Convert String with various formats into java.util.Date
     *
     * @param input
     *            Date as a string
     * @return java.util.Date object if input string is parsed
     *          successfully else returns null
     */
    public static Date convertToDate(String input) {
        Date date = null;
        if(input == null ) {
            return null;
        }
        for (SimpleDateFormat format : dateFormats) {
            try {
                format.setLenient(false);
                date = format.parse(input);
            } catch (ParseException e) {
                //Shhh.. try other formats
            }
            if (date != null) {
                break;
            }
        }

        return date;
    }

    public static Date getDate(String s) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = format.parse(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatDate(Date s) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String date = "";
        try {
            date = format.format(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

}
