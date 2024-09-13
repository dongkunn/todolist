package com.dk.todolist;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dk.todolist.util.PlannerUtil;

import hearsilent.discreteslider.DiscreteSlider;

/**
 * The configuration screen for the {@link ToDoListWidget ToDoListWidget} AppWidget.
 */
public class ToDoListWidgetConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private DiscreteSlider dsTransparency;
    private DiscreteSlider dsFontSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_do_list_widget_configure);

        // 위젯 ID 가져오기
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // 취소 시 결과 설정
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_CANCELED, resultValue);

        // 위젯 ID가 유효하지 않으면 종료
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        // 미리보기
        View widgetMain = findViewById(R.id.included_layout_id);
        // UI 요소 초기화
        dsTransparency = findViewById(R.id.ds_transparency);
        dsFontSize = findViewById(R.id.ds_font_size);

        dsTransparency.setOnValueChangedListener(new DiscreteSlider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int progress, boolean fromUser) {
                super.onValueChanged(progress, fromUser);

                int transparency = 255-(int)(progress*2.55);

                int colorResId = getColorResourceId(getApplicationContext(), "Black");
                int color = ContextCompat.getColor(getApplicationContext(), colorResId);
                color = Color.argb(transparency, Color.red(color), Color.green(color), Color.blue(color));
                widgetMain.setBackgroundColor(color);
            }
        });

        dsFontSize.setOnValueChangedListener(new DiscreteSlider.OnValueChangedListener() {
            @Override
            public void onValueChanged(int progress, boolean fromUser) {
                super.onValueChanged(progress, fromUser);
                int newWidthInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, progress, getResources().getDisplayMetrics());
                int newHeightInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, progress, getResources().getDisplayMetrics());

                // 이미지 사이즈
                ImageView refreshIcon = widgetMain.findViewById(R.id.iconRefreshInWidget);

                ViewGroup.LayoutParams layoutParams = refreshIcon.getLayoutParams();
                layoutParams.width = newWidthInPx;
                layoutParams.height = newHeightInPx;

                refreshIcon.setLayoutParams(layoutParams);

                // 텍스트 사이즈
                TextView dateText = widgetMain.findViewById(R.id.textInWidget);
                dateText.setTextSize(progress);
            }
        });
        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWidgetSettings();
            }
        });
    }

    private static int getColorResourceId(Context context, String colorName) {
        return context.getResources().getIdentifier(colorName.toLowerCase(), "color", context.getPackageName());
    }

    private void saveWidgetSettings() {
        int transparency = dsTransparency.getProgress();
        int fontSize = dsFontSize.getProgress();

        // 설정 저장
        SharedPreferences.Editor prefs = getSharedPreferences(PlannerUtil.widgetPrefs, Context.MODE_PRIVATE).edit();
        prefs.putInt("transparency_" + appWidgetId, transparency);
        prefs.putInt("fontSize_" + appWidgetId, fontSize);
        prefs.apply();

        // 위젯 업데이트
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ToDoListWidget.updateAppWidget(this, appWidgetManager, appWidgetId);

        // 결과 설정 및 종료
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}