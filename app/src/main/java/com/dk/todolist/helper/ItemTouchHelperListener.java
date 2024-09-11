package com.dk.todolist.helper;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperListener {
    boolean onItemMove(RecyclerView recyclerView, int form_position, int to_position);
    void onItemSwipe(int position);
}