package others;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Crunchify.com
 */

public class TimeDifference {
    private static String d;

    public static String getDiff(String date2) {
        try {

            Date todayDate = Calendar.getInstance().getTime();

            String format = "yyyy/MM/dd HH:mm";//"MM/dd/yyyy hh:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            String today = sdf.format(todayDate);

            Date dateObj1 = sdf.parse(today);
            Date dateObj2 = sdf.parse(date2);

            DecimalFormat dFormat = new DecimalFormat("###,###");

            assert dateObj2 != null;
            assert dateObj1 != null;
            long diff = dateObj2.getTime() - dateObj1.getTime();

            int diffhours = (int) (diff / (60 * 60 * 1000));

            double bidDate = Double.parseDouble(dFormat.format(diffhours));
            if (bidDate < 0) {
                d = "Bid is closed";
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                double f = (bidDate) / 24;
                String timeLeft = df.format(f);
                String[] ary = timeLeft.split("\\.");
                Log.d("b40", timeLeft);
                d = "Bid ends in " + ary[0] + " days " + Integer.parseInt(String.valueOf(ary[1].charAt(0))) + " hours";
            }

            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;
    }

    public static String getDiffTwo(String date2) {
        try {

            Date todayDate = Calendar.getInstance().getTime();

            String format = "yyyy/MM/dd HH:mm";//"MM/dd/yyyy hh:mm a";
            SimpleDateFormat sdf = new SimpleDateFormat(format);

            String today = sdf.format(todayDate);

            Date dateObj1 = sdf.parse(today);
            Date dateObj2 = sdf.parse(date2);

            DecimalFormat dFormat = new DecimalFormat("###,###");

            assert dateObj2 != null;
            assert dateObj1 != null;
            long diff = dateObj1.getTime() - dateObj2.getTime();

            int diffhours = (int) (diff / (60 * 60 * 1000));
            int diffmins = (int) (diff / (60 * 1000));

            double hr = Double.parseDouble(dFormat.format(diffhours));

            double f = hr / 24;

            if (f >= 1) {
                d = (int) f + " days ago";
            } else if (hr > 0) {
                d = diffhours + " hours ago";
            } else if (diffmins > 0) {
                d = diffmins + " minutes ago";
            } else {
                d = "Just now";
            }

            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;
    }
}
