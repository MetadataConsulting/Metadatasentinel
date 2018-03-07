package metadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class Validation {

    public static int yearsBetween(Date last, Date first) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;

    }

    public static boolean beforeDate(Date last, Date first) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        return a.before(b);
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }

    public static boolean matchesRegex(String value, String pattern) {
        String regex = pattern;
        return Pattern.compile(regex).matcher(value).matches();
    }

    public static boolean blank(String value) {
        if ( value == null ) {
            return true;
        }
        value = value.trim();

        if ( value.equals("") ) {
            return true;
        }
        return false;
    }

    public static boolean matchesDatePattern(String value, String pattern) {
        String regex = pattern;
        if ( pattern.equals("yyyy-MM-dd") ) {
            regex = "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))";
        }
        return Pattern.compile(regex).matcher(value).matches();
    }

    public static int yearsBetween(String last, String first, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return yearsBetween(simpleDateFormat.parse(last), simpleDateFormat.parse(first));
    }

    public static boolean beforeDate(String last, String first, String format) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return beforeDate(simpleDateFormat.parse(last), simpleDateFormat.parse(first));
    }


}
