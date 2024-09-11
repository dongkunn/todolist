package com.dk.todolist.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.dk.todolist.R;
import com.dk.todolist.helper.ItemTouchHelperListener;
import com.dk.todolist.util.PlannerUtil;
import com.dk.todolist.vo.ItemVo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ItemViewHolder>
        implements ItemTouchHelperListener {
    private Context context;
    private OnItemChangeListener changeListener;
    private OnItemMoveListener moveListener;
    private RecyclerView recyclerView;
    private ItemTouchHelper mItemTouchHelper;
    public ArrayList<ItemVo> items = new ArrayList<>();
    public void setOnItemChangeListener(OnItemChangeListener listener) {
        this.changeListener = listener;
    }

    public void setOnItemMoveListener(OnItemMoveListener listener) {
        this.moveListener = listener;
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        mItemTouchHelper = itemTouchHelper;
    }
    public ChecklistAdapter(Context context, RecyclerView recyclerView) {
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recycle_list_item, parent, false);
        return new ItemViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.onBind(items.get(position),position);

//        holder.contentsText.setOnLongClickListener(view -> {
//            int itemPosition = (int) view.getTag();
//            ClipData.Item item = new ClipData.Item(String.valueOf(itemPosition));
//            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//            ClipData dragData = new ClipData(String.valueOf(itemPosition), mimeTypes, item);
//
//            // 커스텀 DragShadowBuilder 생성
//            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view) {
//                @Override
//                public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
//                    // 그림자 크기를 뷰와 동일하게 설정
//                    outShadowSize.set(view.getWidth(), view.getHeight());
//                    // 터치 포인트를 텍스트의 중앙으로 설정
//                    outShadowTouchPoint.set(view.findViewById(R.id.contentsText).getWidth() / 2, view.getHeight() / 2);
//                }
//
//                @Override
//                public void onDrawShadow(Canvas canvas) {
//                    // 뷰를 그대로 그림자로 그립니다
//                    view.draw(canvas);
//                }
//            };
//
//            // 드래그 시작
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                view.startDragAndDrop(dragData, shadowBuilder, view, 0);
//            } else {
//                view.startDrag(dragData, shadowBuilder, view, 0);
//            }
//            return true;
//        });

        // drag 아이콘에만 long click 이벤트 추가
        holder.iconComplete.setOnLongClickListener(v -> {
            if (mItemTouchHelper != null) {
                mItemTouchHelper.startDrag(holder);
            }
            return true;
        });

        if (position == 0 && holder.contentsText.length() == 0) {
            // 첫번째 항목(추가시 첫번째에 추가) 이면서 값이 없을때가 신규 항목이어서 focus 필요
            // 포커스 및 키보드 자동 활성화
            holder.contentsText.requestFocus();

            //키보드 보이게 하는 부분
            PlannerUtil.showKeyboard(context);
        } else {
            holder.contentsText.clearFocus();
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(ArrayList<ItemVo> itemList){
        items = itemList;
        try {
            notifyDataSetChanged();
        } catch (Exception ignored) {

        }
    }

    @Override
    public boolean onItemMove(RecyclerView recyclerView, int from_position, int to_position) {
        ItemVo fromItem = items.get(from_position);
        ItemVo toItem = items.get(to_position);
        items.remove(from_position);
        items.add(to_position,fromItem);
        fromItem.setPriorOrder(to_position);
        toItem.setPriorOrder(from_position);
        notifyItemMoved(from_position, to_position);

        if (moveListener != null) {
            moveListener.onMoveComplete(fromItem, toItem);
        }
        return true;
    }

    @Override
    public void onItemSwipe(int position) {
        if (moveListener != null) {
            ItemVo vo = items.get(position);
            // 반복 항목 이동 불가
            moveListener.onSwipeComplete(items, vo, position);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llCard;
        public TextView contentsText;
        CheckBox checkBox;
        ImageView iconComplete;

        public ItemViewHolder(@NonNull View convertView) {
            super(convertView);
            llCard = convertView.findViewById(R.id.llCard);
            contentsText = convertView.findViewById(R.id.contentsText);
            checkBox = convertView.findViewById(R.id.checkbox);
            iconComplete = convertView.findViewById(R.id.iconComplete);

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ItemVo vo = items.get(position);

                        if (((CheckBox) v).isChecked()) {
                            vo.setItemComYn("Y");

                            contentsText.setEnabled(false);
                        } else {
                            vo.setItemComYn("N");

                            contentsText.setEnabled(true);
                        }
                        // remove focus
                        contentsText.clearFocus();;

                        changeListener.onSaveClick(vo);
                    }
                }
            });

            contentsText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            if (contentsText.getText().toString().trim().isEmpty()) {
//                                Toast.makeText(context, context.getString(R.string.noInputData), Toast.LENGTH_SHORT).show();
//                                return true;
//                            }
//                            ItemVo vo = items.get(position);
//                            vo.setItemContents(contentsText.getText().toString());
//                            // changeListener.onSaveClick(vo);
//
//                            // clear focus
//                            contentsText.clearFocus();
//                            // change image
//                            iconComplete.setImageResource(R.drawable.baseline_drag_handle_24);
//                        }
//                    }
                    return false;
                }
            });

            contentsText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ItemVo vo = items.get(position);
                        vo.setItemContents(contentsText.getText().toString());
                    }
                }
            });
            contentsText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                String prevStr="";
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        // 포커스가 없어졌을 때 실행되는 코드
                        if (!prevStr.equals(contentsText.getText().toString())) {
                            // 문자가 변경되거나 반복 설정이 변경된 항목만 처리
                            if (contentsText.getText().toString().trim().isEmpty()) {
                                Toast.makeText(context, context.getString(R.string.noInputData), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {
                                ItemVo vo = items.get(position);
                                vo.setItemContents(contentsText.getText().toString());
                                changeListener.onSaveClick(vo);
                            }
                        }

                        // iconComplete.setImageResource(0);
                        iconComplete.setImageResource(R.drawable.baseline_drag_handle_24);

                        if (contentsText.getText().toString().trim().isEmpty()) {
                            // 항목이 비어있으면 삭제처리한다.
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) {

                                // 삭제된 이후 recyclerview 스크롤시 프로그램 종료 현상 처리
                                recyclerView.post(() -> {
                                    Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(() -> {
                                        contentsText.clearFocus();
                                        items.remove(position);
                                        recyclerView.scrollToPosition(items.size());
                                        notifyDataSetChanged();
                                    }, 100);
                                });
                                //notifyDataSetChanged();

                            }
                        }
                        // 여기에서 포커스가 없어졌을 때 해야할 작업을 수행합니다.
                        Log.d("FocusChange", "EditText 내용: " + contentsText.getText().toString());
                    } else {
                        prevStr = contentsText.getText().toString();
                        iconComplete.setImageResource(R.drawable.baseline_add_card_24);
                        // 포커스가 생겼을 때 실행되는 코드
                        Log.d("FocusChange", "EditText에 포커스가 생겼습니다.");
                    }
                }
            });
            iconComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (contentsText.getText().toString().trim().isEmpty()) {
                            Toast.makeText(context, context.getString(R.string.noInputData), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        ItemVo vo = items.get(position);
                        vo.setItemContents(contentsText.getText().toString());
                        // changeListener.onSaveClick(vo);

                        // clear focus
                        contentsText.clearFocus();
                        // change image
                        // iconComplete.setImageResource(0);
                        iconComplete.setImageResource(R.drawable.baseline_drag_handle_24);
                    }
                }
            });

        }

        public void onBind(ItemVo itemVo, int position){
            contentsText.setText(itemVo.getItemContents());
            checkBox.setChecked(itemVo.getItemComYn().equals("Y"));
            contentsText.setEnabled(!itemVo.getItemComYn().equals("Y"));

            // set tag
            contentsText.setTag(itemVo.getId());
            llCard.setTag(itemVo.getId());

            // LayoutParams 생성
            if (itemVo.isRepetition()) {
                iconComplete.setVisibility(View.INVISIBLE);

                ViewGroup.LayoutParams checkParams = checkBox.getLayoutParams();
                checkParams.height = 0;
                checkParams.width = 0;
                checkBox.setLayoutParams(checkParams);

            } else {
                iconComplete.setVisibility(View.VISIBLE);

                ViewGroup.LayoutParams checkParams = checkBox.getLayoutParams();
                checkParams.height = PlannerUtil.dpToPx(context, 17);
                checkParams.width = PlannerUtil.dpToPx(context, 17);
                checkBox.setLayoutParams(checkParams);
            }
        }
    }

    public interface OnItemChangeListener {
        void onSaveClick(ItemVo vo);
    }

    public interface OnItemMoveListener {
        void onMoveComplete(ItemVo fromItem, ItemVo toItem);
        void onSwipeComplete(ArrayList<ItemVo> items, ItemVo vo, int position);
    }
}
