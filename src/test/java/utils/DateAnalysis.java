package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * DateAnalysis is an assistance class which includes methods that get a task entered due date
 * and return the expected due date text displayed in the Tasks page
 */
public class DateAnalysis {
    private static String dueDate;
    private static String shortDateFormat;
    private static String shortDateCurrentYear;


    public static String getDueDateDay(String format) {
        String returnDayVal = "";
        String inputDateDay = dueDate.split("\\.")[0];
        if (format.equals("j")) { // i.e. d - show single digit if date is 08.11.2021 --> day: 8
            if (inputDateDay.length() == 2) {
                if (inputDateDay.charAt(0) == '0')
                    returnDayVal = inputDateDay.substring(1);
                else returnDayVal = inputDateDay;
            } else returnDayVal = inputDateDay;
        } else if (format.equals("d")) { // i.e. dd - show double digit day if date is 8.11.2021 --> day: 08
            if (inputDateDay.length() == 1)
                returnDayVal = "0" + inputDateDay;
            else returnDayVal = inputDateDay;
        }
        return returnDayVal;
    }

    public static String getDueDateMonth(String format) {
        String returnMonthVal = "";
        String inputDateMonth = dueDate.split("\\.")[1];
        switch (format) {
            case "n": // show single digit month val: 08.02.2021 --> month val: 2
                if (inputDateMonth.length() == 2) {
                    if (inputDateMonth.charAt(0) == '0')
                        returnMonthVal = inputDateMonth.substring(1);
                    else returnMonthVal = inputDateMonth;
                } else returnMonthVal = inputDateMonth;
                break;
            case "m": // show double digit month val: 08.2.2021 --> month val: 02
                if ((inputDateMonth.length() == 1))
                    returnMonthVal = "0" + inputDateMonth;
                else returnMonthVal = inputDateMonth;
                break;
            case "M": // show month name val: 08.2.2021 --> month val: February
                if (inputDateMonth.length() == 2) {
                    if (inputDateMonth.charAt(0) == '0')
                        returnMonthVal = getMonthName(Integer.parseInt(inputDateMonth.substring(1)));
                    else returnMonthVal = getMonthName(Integer.parseInt(inputDateMonth));
                } else returnMonthVal = getMonthName(Integer.parseInt(inputDateMonth));
                break;
        }
        return returnMonthVal;
    }

    public static String getMonthName(int monthNum) {
        String month = null;
        switch (monthNum) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Oct";
                break;
            case 11:
                month = "Nov";
                break;
            case 12:
                month = "Dec";
                break;
        }
        return month;
    }

    public static String getDueDateYear(String format) {
        String inputDateYear = dueDate.split("\\.")[2];
        String returnYearVal = "";
        if (format.equals("y")) { // show double digit year val: 8.2.2021 --> year val: 21
            if (inputDateYear.length() == 4)
                returnYearVal = inputDateYear.substring(2);
            else returnYearVal = inputDateYear;
        } else if (format.equals("Y")) { // show 4 digit year val: 08.2.21 --> year val: 2021
            if (inputDateYear.length() == 2)
                returnYearVal = "20" + inputDateYear;
            else returnYearVal = inputDateYear;
        }
        return returnYearVal;
    }

    /**
     * @description this returns a short format date string based on shortDateFormat received from Settings page;
     * in the Settings page there are several formats and this function created the date string according to these formats
     * @return - String - returnDateDisplay
     */
    public static String getShortDateFormatDisplay() {
        String returnDateDisplay = "";
        String day = "";
        String month = "";
        String year = "";
        switch (shortDateFormat) {
            case "Y-m-d":
                day = getDueDateDay("d");
                month = getDueDateMonth("m");
                year = getDueDateYear("Y");
                returnDateDisplay = year + "-" + month + "-" + day;
                break;
            case "n/j/y":
                day = getDueDateDay("j");
                month = getDueDateMonth("n");
                year = getDueDateYear("y");
                returnDateDisplay = month + "/" + day + "/" + year;
                break;
            case "d.m.y":
                day = getDueDateDay("d");
                month = getDueDateMonth("m");
                year = getDueDateYear("y");
                returnDateDisplay = day + "." + month + "." + year;
                break;
            case "d/m/y":
                day = getDueDateDay("d");
                month = getDueDateMonth("m");
                year = getDueDateYear("y");
                returnDateDisplay = day + "/" + month + "/" + year;
                break;
        }
        return returnDateDisplay;
    }

    /**
     * @description - this methods creates a date string based on shortDateCurrentYearFormat string received from Settings page
     * @return - String - returnDateDisplay
     */
    public static String getShortDateCurrentYearDisplay(){
        String returnDateDisplay = "";
        String day = "";
        String month = "";
        switch (shortDateCurrentYear){
            case "M d":
                day = getDueDateDay("d");
                month = getDueDateMonth("M");
                returnDateDisplay = month + " " + day;
                break;
            case "j M":
                day = getDueDateDay("j");
                month = getDueDateMonth("M");
                returnDateDisplay = day + " " + month;
                break;
            case "n/j":
                day = getDueDateDay("j");
                month = getDueDateMonth("n");
                returnDateDisplay = month + "/" + day;
                break;
            case "d.m":
                day = getDueDateDay("d");
                month = getDueDateMonth("m");
                returnDateDisplay = day + "." + month;
                break;
        }
        return returnDateDisplay;
    }

    /**
     * @description - this method creates the expected date string displayed for an advanced task in the Tasks page.
     * it checks if the due date in current year or not, if it is in current year it checks the numbers of days from current
     * date to due date and creates the expected due date string displayed;
     * @param dueDateIn - String - due date entered in task creation
     * @param shortDateFormatIn - String - received from Settings page
     * @param shortDateCurrentYearIn - String - received from Settings page
     * @return - String - dueDateDisplay
     * @throws ParseException
     */
    public static String getExpectedDateDisplay(String dueDateIn, String shortDateFormatIn, String shortDateCurrentYearIn) throws ParseException {
        dueDate = dueDateIn;
        shortDateFormat = shortDateFormatIn;
        shortDateCurrentYear = shortDateCurrentYearIn;
        String dueDateDisplay = "";
        // checking if due date is in current year or not
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int dueDateYear = Integer.parseInt(getDueDateYear("Y"));
        if(dueDateYear == currentYear){ // due date is in current year; will use shortDateFormatCurrentYear
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
            Date duedate = formatter.parse(dueDate.replaceAll("\\.","\\/")); // due date will be represented as dd/mm/yy
            Date currentDate = formatter.parse(formatter.format(new Date())); // current date will be represented as dd/mm/yy
            // case due date > current date
            if(duedate.after(currentDate)){
                long diff = TimeUnit.DAYS.convert(duedate.getTime() - currentDate.getTime(), TimeUnit.MILLISECONDS);
                if(diff > 7) // if due date is in more than 7 days from current date
                    dueDateDisplay = getShortDateCurrentYearDisplay(); // will display 8 Oct for example
                else if(diff == 1) // if due date is in 1 day from current date --> tomorrow
                    dueDateDisplay = "tomorrow";
                else dueDateDisplay = String.format("in %d days",diff); // if due date is in 2-7 days from current date
            }
            // case due date = current date
            if(duedate.equals(currentDate))
                dueDateDisplay = "today";
            // case due date < current date
            if(duedate.before(currentDate)){
                long diff = TimeUnit.DAYS.convert(currentDate.getTime() - duedate.getTime(), TimeUnit.MILLISECONDS);
                if(diff > 7)
                    dueDateDisplay = getShortDateCurrentYearDisplay();
                else if(diff == 1)
                    dueDateDisplay = "yesterday";
                else dueDateDisplay = String.format("%d days ago",diff);
            }
        }
        else { // due date is not in current year; will use shortDateFormat
            dueDateDisplay = getShortDateFormatDisplay();
        }
        return dueDateDisplay;
    }
}
