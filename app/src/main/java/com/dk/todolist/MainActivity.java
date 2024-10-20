package com.dk.todolist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.dk.todolist.adapter.ChecklistAdapter;
import com.dk.todolist.component.CustomDialog;
import com.dk.todolist.helper.ItemTouchHelperCallback;
import com.dk.todolist.sqlite.SqliteHelper;
import com.dk.todolist.util.PlannerUtil;
import com.dk.todolist.vo.ItemVo;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity implements ChecklistAdapter.OnItemChangeListener, ChecklistAdapter.OnItemMoveListener {
    private SqliteHelper sqliteHelper;
    ArrayList<ItemVo> itemList;
    ChecklistAdapter adapter;
    private long backPressedTime = 0;
    private static final int BACK_PRESS_INTERVAL = 2000; // 2초
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        loadBanner();

        sqliteHelper = new SqliteHelper(getBaseContext());

        itemList = sqliteHelper.getItemListByCondition(new ItemVo());

        // recyclerview 설정
        RecyclerView listRecyclerview = (RecyclerView) findViewById(R.id.listRecyclerview);
        adapter = setRecyclerView(listRecyclerview);

        adapter.setOnItemChangeListener(this);
        adapter.setOnItemMoveListener(this);

        adapter.setItems(itemList);

        TextView versionTv = (TextView) findViewById(R.id.titleVersion);
        versionTv.setText("Version " + PlannerUtil.getAppVersion(getBaseContext()));

        ImageView shareImageView = (ImageView) findViewById(R.id.shareBtn);
        ImageView deleteImageView = (ImageView) findViewById(R.id.deleteBtn);
        ImageView calendarImageView = (ImageView) findViewById(R.id.calendarBtn);

        ImageView fab = (ImageView) findViewById(R.id.addButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(listRecyclerview);
            }
        });
        shareImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList != null && !itemList.isEmpty()) {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    shareIntent.setType("text/plain");

                    shareIntent.putExtra(Intent.EXTRA_TEXT, itemList.stream().map(ItemVo::getItemContents).collect(Collectors.joining("\n")));

                    startActivity(Intent.createChooser(shareIntent, "Share"));
                } else {
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.shareNoData), Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList != null && !itemList.isEmpty()) {
                    CustomDialog customDialog = new CustomDialog(getBaseContext(), getBaseContext().getString(R.string.deleteAllData));
                    customDialog.setDialogListener(new CustomDialog.CustomDialogInterface() {
                        @Override
                        public void okBtnClicked(String btnName) {
                            for(ItemVo deletedVo : itemList) {
                                sqliteHelper.deleteItem(deletedVo);
                            }
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.textDeleted), Toast.LENGTH_SHORT).show();
                            refreshView();
                        }

                        @Override
                        public void noBtnClicked(String btnName) {

                        }
                    });

                    customDialog.show();
                } else {
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.deleteNoData), Toast.LENGTH_SHORT).show();
                }
            }
        });

        calendarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList != null && !itemList.isEmpty()) {
                    //initCredential(list);
                } else {
                    Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.shareNoData), Toast.LENGTH_SHORT).show();
                }
            }
        });

        listRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//            super.onScrolled(recyclerView, dx, dy);
//
//            // 현재 포커스가 있는 아이템의 위치를 가져옵니다.
//            int focusedPosition = recyclerView.getChildAdapterPosition(recyclerView.getFocusedChild());
//
//            // 스크롤 후에도 포커스가 유지되도록 아이템을 다시 포커스합니다.
//            if (focusedPosition != RecyclerView.NO_POSITION) {
//                recyclerView.findViewHolderForAdapterPosition(focusedPosition).itemView.requestFocus();
//            }
            }
        });

        listRecyclerview.setOnTouchListener(new View.OnTouchListener() {
            float startY, endY;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startY = event.getY();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    endY = event.getY();
                    View child = listRecyclerview.findChildViewUnder(event.getX(), event.getY());
                    if (child == null && Math.abs(endY-startY) < 10) {
                        // 터치한 위치가 리스트 항목이 아니고, 스크롤이 아닌 경우
                        return addItem(listRecyclerview);
                    }
                }
                return false; // false를 반환하여 터치 이벤트가 계속 처리되도록 합니다.
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                long tempTime = System.currentTimeMillis();
                long intervalTime = tempTime - backPressedTime;

                if (intervalTime >= 0 && BACK_PRESS_INTERVAL >= intervalTime) {
                    finishAffinity();
                    System.exit(0);
                } else {
                    backPressedTime = tempTime;
                    Toast.makeText(getApplicationContext(), getString(R.string.backBtnEndProcess), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private ChecklistAdapter setRecyclerView(RecyclerView listRecyclerview) {
        LinearLayoutManager manager = new LinearLayoutManager(getBaseContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        listRecyclerview.setLayoutManager(manager);

        // set card size to adapter
        ChecklistAdapter adapter = new ChecklistAdapter(getBaseContext(), listRecyclerview);
        listRecyclerview.setAdapter(adapter);

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));

        mItemTouchHelper.attachToRecyclerView(listRecyclerview);

        adapter.setItemTouchHelper(mItemTouchHelper);

        return adapter;
    }

    private void refreshView() {
        ArrayList<ItemVo> list = sqliteHelper.getItemListByCondition(new ItemVo());
        this.itemList = list;
        adapter.setItems(list);
    }

    private boolean isEmpty(ArrayList<ItemVo> list) {
        for (ItemVo vo : list) {
            if (vo != null && vo.getItemContents().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean addItem(RecyclerView recyclerInCardView) {
        // find focus in list and remove focus
        View recyclerFocus = recyclerInCardView.getFocusedChild();
        if (recyclerFocus != null) {
            recyclerFocus.clearFocus();
        }

        ItemVo vo = new ItemVo();
        ArrayList<ItemVo> list = itemList;

        if (list.size() > 0 && isEmpty(list)) {
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.textInputContents), Toast.LENGTH_SHORT).show();
            return false;
        }
        setItem(vo);

        list.add(0, vo);
        adapter.setItems(list);

        PlannerUtil.showKeyboard(getBaseContext());

        return true;
    }

    @Override
    public void onSaveClick(ItemVo vo) {
        boolean result = false;
        long id = -1;
        if (vo.getId() > 0) {
            result = sqliteHelper.updateItem(vo);
        } else {
            setItem(vo);
            id = sqliteHelper.insertItem(vo);
            vo.setId(Long.valueOf(id).intValue());
        }
        if (result || id > 0) {
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.textSaved), Toast.LENGTH_SHORT).show();
        }

        // 처리 count 업데이트
        refreshView();
    }
    private void setItem(ItemVo vo) {
        vo.setItemComYn("N");
        // 수정 필요한 값
        // 수정 필요한 값
        vo.setDelYn("N");
        vo.setPriorOrder(0);
        if (vo.getCreateUserId() == null || vo.getCreateUserId().isEmpty()) {
            vo.setCreateUserId(PlannerUtil.createUserId);
        }
    }
    @Override
    public void onMoveComplete(ItemVo fromItem, ItemVo toItem) {
        sqliteHelper.updateItem(fromItem);
        sqliteHelper.updateItem(toItem);
    }

    @Override
    public void onSwipeComplete(ArrayList<ItemVo> items, ItemVo vo, int position) {
        CustomDialog customDialog = new CustomDialog(this, getBaseContext().getString(R.string.deleteSelectData));
        customDialog.setDialogListener(new CustomDialog.CustomDialogInterface() {
            @Override
            public void okBtnClicked(String btnName) {
                sqliteHelper.deleteItem(vo);

                ItemVo schVo = new ItemVo();
                schVo.setId(vo.getId());

                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.textDeleted), Toast.LENGTH_SHORT).show();

                refreshView();
            }

            @Override
            public void noBtnClicked(String btnName) {
                // 처리 count 업데이트
                refreshView();
            }
        });

        customDialog.show();
    }

    private void loadBanner() {
        MobileAds.initialize(getBaseContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        // main
        LinearLayout ll = findViewById(R.id.googleAdmob);
        AdView adView = new AdView(getBaseContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId(BuildConfig.AD_MAIN_UNIT_ID);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        ll.addView(adView);
    }
}