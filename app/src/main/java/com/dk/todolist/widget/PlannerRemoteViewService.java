package com.dk.todolist.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class PlannerRemoteViewService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ContactRemoteViewsFactory(this.getApplicationContext());
    }
}
