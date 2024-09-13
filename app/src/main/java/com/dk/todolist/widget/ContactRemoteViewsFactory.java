package com.dk.todolist.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.TypedValue;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.dk.todolist.MainActivity;
import com.dk.todolist.R;
import com.dk.todolist.ToDoListWidget;
import com.dk.todolist.sqlite.SqliteHelper;
import com.dk.todolist.util.PlannerUtil;
import com.dk.todolist.vo.ItemVo;

import java.util.ArrayList;
import java.util.List;

public class ContactRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<ItemVo> itemList = new ArrayList<>();
    private Context context;

    public ContactRemoteViewsFactory(Context context) {
        this.context = context;
    }
    @Override
    public void onCreate() {
        getItemList();
    }

    @Override
    public void onDataSetChanged() {
        getItemList();
    }

    @Override
    public void onDestroy() {
        itemList.clear();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        @SuppressLint("RemoteViewLayout") RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recycle_widget);
        views.setTextViewText(R.id.itemTextInWidget, itemList.get(position).getItemContents());

        // click text
        Intent widgetTextIntent = new Intent(context, MainActivity.class);
        widgetTextIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingTextIntent = PendingIntent.getActivity(context, 0, widgetTextIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        views.setOnClickPendingIntent(R.id.itemTextInWidget, pendingTextIntent);

        // click list
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(ToDoListWidget.LIST_ACTION, position);
        views.setOnClickFillInIntent(R.id.itemTextInWidget, fillInIntent);

        // set font size to list
        // String appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID);
        SharedPreferences sharedPreferences = context.getSharedPreferences(PlannerUtil.widgetPrefs, Context.MODE_PRIVATE);
        int fontSize = sharedPreferences.getInt(PlannerUtil.fontSize, 15);
        views.setTextViewTextSize(R.id.itemTextInWidget, TypedValue.COMPLEX_UNIT_SP, fontSize);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private void getItemList() {
        SqliteHelper sqliteHelper = new SqliteHelper(this.context);
        itemList = sqliteHelper.getItemListByCondition(new ItemVo());
    }
}
