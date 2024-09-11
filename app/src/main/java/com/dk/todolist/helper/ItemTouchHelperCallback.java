package com.dk.todolist.helper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperListener listener;

    public ItemTouchHelperCallback(ItemTouchHelperListener listener) { this.listener = listener; }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {

        int drag_flags = ItemTouchHelper.UP|ItemTouchHelper.DOWN;
        int swipe_flags = ItemTouchHelper.START|ItemTouchHelper.END;

        return makeMovementFlags(drag_flags,swipe_flags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return listener.onItemMove(recyclerView, viewHolder.getAdapterPosition(),target.getAdapterPosition());
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onItemSwipe(viewHolder.getAdapterPosition());
    }
//
//    enum ButtonState {
//        GONE,
//        LEFT_VISIBLE,
//        RIGHT_VISIBLE,
//        DELETE_VISIBLE
//    }
//
//    private boolean swipeBack = false;
//    private ButtonState buttonsShowedState = ButtonState.GONE;
//    int buttonWidth = 100;
//    private RectF buttonInstance = null;
//    private RecyclerView.ViewHolder currentItemViewHolder = null;
//    //private SwipeControllerActions buttonsActions = null;
//    @Override
//    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        //아이템이 스와이프 됐을경우 버튼을 그려주기 위해서 스와이프가 됐는지 확인
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            if (buttonsShowedState != ButtonState.GONE) {
//                if (buttonsShowedState == ButtonState.DELETE_VISIBLE)
//                    dX = Math.min(dX, - buttonWidth);
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//            } else {
//                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//
//            if (buttonsShowedState == ButtonState.GONE) {
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//
//        }
//        currentItemViewHolder = viewHolder;
//
//        // 삭제 버튼 그리기
//        drawButtons(c, currentItemViewHolder);
//
//    }
//
//    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
//        float corners = 5;
//        View itemView = viewHolder.itemView;
//        Paint paint = new Paint();
//        buttonInstance = null;
//
//        // 왼쪽으로 스와이프 했을 경우에만 보여지도록
//        if (buttonsShowedState == ButtonState.DELETE_VISIBLE) {
//            RectF rightButton = new RectF(itemView.getRight() - buttonWidth, itemView.getTop() + 10, itemView.getRight(),
//                    itemView.getBottom() -10);
//            paint.setColor(Color.RED);
//            c.drawRoundRect(rightButton, corners, corners, paint);
//            drawText("삭제", c, rightButton, paint);
//
//            buttonInstance = rightButton;
//        }
//    }
//
//    private void drawText(String text, Canvas c, RectF button, Paint p) {
//        float textSize = 32;
//        p.setColor(Color.WHITE);
//        p.setAntiAlias(true);
//        p.setTextSize(textSize);
//
//        float textWidth = p.measureText(text);
//        c.drawText(text, button.centerX() - (textWidth / 2), button.centerY() + (textSize / 2), p);
//    }
//
//    @Override
//    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
//        if (swipeBack) {
//            swipeBack = false;
//            return 0;
//        }
//        return super.convertToAbsoluteDirection(flags, layoutDirection);
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setTouchListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
//        recyclerView.setOnTouchListener((v, event) -> {
//            // ACTION_CANCEL >> 현재 제스처 중단, ACTION_UP >> 최종 릴리스 위치와 마지막 다운, 이동 이벤트 이후의 모든 중간 지점이 포함
//            swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
//            if (swipeBack) {
//                // state 설정
//                if (dX < -buttonWidth) buttonsShowedState = ButtonState.DELETE_VISIBLE;
//                if (buttonsShowedState != ButtonState.GONE) {
//                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//                    setItemsClickable(recyclerView, false);
//                }
//            }
//            return false;
//        });
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
//        recyclerView.setOnTouchListener((v, event) -> {
//            // 누른 제스처가 시작, 초기 시작 위치가 포함
//            if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//            return false;
//        });
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
//
//        recyclerView.setOnTouchListener((v, event) -> {
//            super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
//            recyclerView.setOnTouchListener((v1, event1) -> false);
//
//            setItemsClickable(recyclerView, true);
//            swipeBack = false;
//
//            if (listener != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())) {
//                if (buttonsShowedState == ButtonState.DELETE_VISIBLE) {
//                    listener.onItemSwipe(viewHolder.getAdapterPosition());
//                }
//            }
//
//            buttonsShowedState = ButtonState.GONE;
//            currentItemViewHolder = null;
//            return false;
//        });
//    }
//    private void setItemsClickable(RecyclerView recyclerView,
//                                   boolean isClickable) {
//        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
//            recyclerView.getChildAt(i).setClickable(isClickable);
//        }
//    }
}