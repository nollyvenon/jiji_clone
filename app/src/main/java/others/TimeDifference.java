package others;

import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Crunchify.com
 */

public class TimeDifference {
    private static String d = "";

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(date2);

            Long time = parsedDate.getTime();
            if (time < 1000000000000L) {
                // if timestamp given in seconds, convert to millis
                time *= 1000;
            }

            long now = System.currentTimeMillis();
            if (time > now || time <= 0) {
                return null;
            }

            // TODO: localize
            final long diff = now - time;
            Log.d("b400", String.valueOf(diff));
            if (diff < MINUTE_MILLIS) {
                d = "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                d = "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                d = diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                d = "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                d = diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                d = "yesterday";
            } else {
                d = diff / DAY_MILLIS + " days ago";
            }

            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return d;
    }
}
