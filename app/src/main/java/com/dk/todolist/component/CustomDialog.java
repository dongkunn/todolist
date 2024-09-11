package com.dk.todolist.component;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dk.todolist.R;

import org.jetbrains.annotations.NotNull;

public class CustomDialog extends Dialog implements View.OnClickListener {
    private CustomDialogInterface customDialogInterface;
    private Button mDlgOkBtn, mDlgNoBtn;
    public String mBtnName,mTitle;

    @Override
    public void onClick(View v) {

    }

    public interface CustomDialogInterface {
        void okBtnClicked(String btnName);
        void noBtnClicked(String btnName);
    }

    public void setDialogListener(CustomDialogInterface customDialogInterface) {
        this.customDialogInterface = customDialogInterface;
    }

    public CustomDialog(@NotNull Context context, String title) {
        super(context);
        this.mTitle = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm);

        TextView title = findViewById(R.id.confirmTextView);
        if (!mTitle.isEmpty()) {
            title.setText(mTitle);
        }

        mDlgOkBtn = findViewById(R.id.okButton);
        mDlgNoBtn = findViewById(R.id.noButton);

        mDlgOkBtn.setOnClickListener(this);
        mDlgNoBtn.setOnClickListener(this);

        mDlgOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnName="OK";
                customDialogInterface.okBtnClicked(mBtnName);
                dismiss();
            }
        });

        mDlgNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnName="NO";
                customDialogInterface.noBtnClicked(mBtnName);
                dismiss();
            }
        });
    }

}
