package com.devmoroz.moneyme.helpers;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GeocodeHelper{

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyDGww6BVqsgVCMBlqUexcVeCK_Apqf4CBw";


    public static boolean areCoordinates(String string) {
        Pattern p = Pattern.compile("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|" +
                "([1-9]?\\d))(\\.\\d+)?)$");
        Matcher m = p.matcher(string);
        return m.matches();
    }
}
