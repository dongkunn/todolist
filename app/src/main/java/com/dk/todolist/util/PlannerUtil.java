package com.dk.todolist.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class PlannerUtil {
    public static final String groupCode="GROUP";
    public static final String createUserId = "default.user";
    public static final String createAuthCode = "MASTER";
    public static final String additionalObject = "Additional Object";
    public static final boolean additionalYn = false;
    public static final String defaultOrder  = " DESC";
    public static final String optionOrder  = " ASC";
    public static final boolean defaultBottomOption  = false;
    public static final boolean defaultRepeat  = false;
    public static final String colorCode = "#A387DC";
    public static final int maxCardSize = 8;
    // app bar 상 오늘 날짜
    public static final int dateStrIdx = 0;
    public static final int firstMemberIdx = 3;
    public static final int repetitionMaxSize = 5;
    public static final String appId="ca-app-pub-1417793607999454~7122068583";
    public static final String bannerId="ca-app-pub-1417793607999454/2687999553";
    public static final String bannerTestId="ca-app-pub-3940256099942544/9214589741";
    public static String currentOrder = " DESC";
    public static String startDay = "MONDAY";
    public static int startDayCalendar = Calendar.MONDAY;
    public static int midPagePosition = 200;
//    public static int midPagePosition = Integer.MAX_VALUE/2;
    public static String transparency = "TRANSPARENCY";
    public static String fontSize = "FONT_SIZE";
    public static String currDate = "CURR_DATE";
    public static String widgetClick = "widgetClick";
    public static String dateClick = "dateClick";
    public static String userLogin = "UserLogin";
    public static String widgetPrefs = "WidgetPrefs";
    public static String calendarDate = "calendarDate";
    public static String calendarTitle = "calendarTitle";
    public static String calendarData = "calendarData";
    public static String plannerDb = "PlannerApp.db";
    public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    public static LocalDate getCurrDate(String date) {
        return LocalDate.parse(date, dtf);
    }
    public static void setShared(SharedPreferences sharedPreferences, String name, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.clear(); // SharedPreferences 내의 모든 데이터를 삭제합니다.

        editor.remove(name);
        if (value instanceof String) {
            editor.putString(name, (String) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(name, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(name, (int) value);
        } else if (value instanceof Long) {
            editor.putLong(name, (long) value);
        } else if (value instanceof Float) {
            editor.putFloat(name, (float) value);
        }
        editor.apply(); // 변경 사항을 적용합니다.
    }

    public static void removeShared(SharedPreferences sharedPreferences, String name) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // editor.clear(); // SharedPreferences 내의 모든 데이터를 삭제합니다.

        editor.remove(name);
        editor.apply(); // 변경 사항을 적용합니다.
    }

    public static String getCommonDate(String inputStr) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat srcDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat tarDate = new SimpleDateFormat("MM.dd HH:mm");
        try {
            Date date = srcDate.parse(inputStr);
            assert date != null;
            return tarDate.format(date);
        } catch (ParseException e) {
            return "";
        }
    }

    public static String getDayOfWeek(String dateString) {
        // DateTimeFormatter를 사용하여 문자열을 LocalDate로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate date = LocalDate.parse(dateString, formatter);

        // 요일을 구함
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        return dayOfWeek.toString().substring(0, 2);
    }

    public static int getWeekofYear(LocalDate today) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            // API 수정분
            // Calendar 객체 생성
            Calendar calendar = Calendar.getInstance();

            // 주의 시작 요일을 setting에서 설정한 값으로 설정
            calendar.setFirstDayOfWeek(PlannerUtil.getStartDayCalendar());

            Date date = null;
            date = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
            calendar.setTime(date);

            // 현재 날짜의 주차 계산
            return calendar.get(Calendar.WEEK_OF_YEAR);
        }
        return 0;
    }

    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);

            if (pInfo.versionName.contains("-")) {
                version = pInfo.versionName.substring(0, pInfo.versionName.indexOf("-"));
            } else {
                version = pInfo.versionName;
            }
            version += "."+ pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getCurrentOrder() {
        return currentOrder;
    }

    public static void setCurrentOrder(String currentOrder) {
        PlannerUtil.currentOrder = currentOrder;
    }

    public static String getStartDay() {
        return startDay;
    }

    public static void setStartDay(String startDay) {
        PlannerUtil.startDay = startDay;
    }

    public static int getStartDayCalendar() {
        return startDayCalendar;
    }

    public static void setStartDayCalendar(int startDayCalendar) {
        PlannerUtil.startDayCalendar = startDayCalendar;
    }

    public static void showKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public static void hideKeyboard(Context context, TextView editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static int pxToDp(Context context, float px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }
    public static int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}
