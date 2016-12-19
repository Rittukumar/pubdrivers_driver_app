package com.oceanstyxx.pddriver.helper;

import static android.content.ContentValues.TAG;
import java.util.Date;
import java.text.SimpleDateFormat;
/**
 * Created by mohsin on 07/12/16.
 */

public class DateUtility {

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return outputDate;

    }
}
