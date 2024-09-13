package com.dk.todolist;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import com.dk.todolist.util.PlannerUtil;
import com.dk.todolist.widget.PlannerRemoteViewService;

import java.time.LocalDate;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ToDoListWidgetConfigureActivity ToDoListWidgetConfigureActivity}
 */
public class ToDoListWidget extends AppWidgetProvider {
    public static final String LIST_ACTION = "com.dk.todolist.LIST_ACTION";
    private static final String TEXT_CLICK_ACTION = "com.dk.plannerwithme.TEXT_CLICK";
    private static final String IMAGE_CLICK_ACTION = "com.dk.plannerwithme.IMAGE_CLICK";
    private static int transparency;
    private static int fontSize;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.to_do_list_widget);

        SharedPreferences sharedPreferences = context.getSharedPreferences(PlannerUtil.widgetPrefs, Context.MODE_PRIVATE);

        // set font & background
        String backgroundColor = sharedPreferences.getString("backgroundColor_" + appWidgetId, "Black");
        transparency = 255-(int)(sharedPreferences.getInt("transparency_" + appWidgetId, 0)*2.55);
        fontSize = sharedPreferences.getInt("fontSize_" + appWidgetId, 15);

        PlannerUtil.setShared(sharedPreferences, PlannerUtil.transparency, transparency);
        PlannerUtil.setShared(sharedPreferences, PlannerUtil.fontSize, fontSize);

        int colorResId = getColorResourceId(context, backgroundColor);
        int color = ContextCompat.getColor(context, colorResId);
        color = Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color));

        views.setTextViewTextSize(R.id.textInWidget, TypedValue.COMPLEX_UNIT_SP, fontSize);
        views.setInt(R.id.widget_main, "setBackgroundColor", color);
        // set font & background

        // 이미지 리소스 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            views.setViewLayoutWidth(R.id.iconRefreshInWidget, fontSize, TypedValue.COMPLEX_UNIT_DIP);
            views.setViewLayoutHeight(R.id.iconRefreshInWidget, fontSize, TypedValue.COMPLEX_UNIT_DIP);
        }

        // set intent
        setIntent(context, appWidgetManager, views, appWidgetId);

    }
    private static int getColorResourceId(Context context, String colorName) {
        return context.getResources().getIdentifier(colorName.toLowerCase(), "color", context.getPackageName());
    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        // 위젯 업데이트 로직
        updateWidgets(context, appWidgetManager, appWidgetIds);
        // scheduleNextUpdate(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (IMAGE_CLICK_ACTION.equals(intent.getAction())) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

                @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.to_do_list_widget);

                setIntent(context, appWidgetManager, views, appWidgetId);
            }
        }

        if (LIST_ACTION.equals(intent.getAction()) || TEXT_CLICK_ACTION.equals(intent.getAction())) {
            // 위젯에서 리스트 호출
            SharedPreferences sharedPreferences = context.getSharedPreferences(PlannerUtil.widgetPrefs, Context.MODE_PRIVATE);
            PlannerUtil.setShared(sharedPreferences, PlannerUtil.widgetClick, true);

            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // 위젯 업데이트 작업 수행
    }

    private static void addEvent(Context context, int appWidgetId, RemoteViews views) {
        // click refresh
        Intent widgetIntent = new Intent(context, ToDoListWidget.class);
        widgetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        widgetIntent.setAction(IMAGE_CLICK_ACTION);
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, appWidgetId, widgetIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        views.setOnClickPendingIntent(R.id.iconRefreshInWidget, pendingIntent);

        // click text
        Intent widgetTextIntent = new Intent(context, ToDoListWidget.class);
        widgetTextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        widgetTextIntent.setAction(TEXT_CLICK_ACTION);
        PendingIntent pendingTextIntent = PendingIntent.getBroadcast(context, 0, widgetTextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        views.setOnClickPendingIntent(R.id.textInWidget, pendingTextIntent);

        // Listview 클릭 이벤트를 위한 코드. -> 원리는 pendingIntent 부여 -> ContactRemoteViewsFactory 여기에서 클릭 처리
        Intent listIntent = new Intent(context, ToDoListWidget.class);
        listIntent.setAction(ToDoListWidget.LIST_ACTION);
        listIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, listIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        views.setPendingIntentTemplate(R.id.listViewInWidget, toastPendingIntent);
    }

    private static void setIntent(Context context, AppWidgetManager appWidgetManager, RemoteViews views, int appWidgetId) {
        Intent serviceIntent = new Intent(context, PlannerRemoteViewService.class);

        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // list 추가
        views.setRemoteAdapter(R.id.listViewInWidget, serviceIntent);

        // add event
        addEvent(context, appWidgetId, views);

        // 데이터 변경을 반영하기 위해 notifyAppWidgetViewDataChanged() 호출
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewInWidget);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}