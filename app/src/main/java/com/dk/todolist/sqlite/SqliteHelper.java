package com.dk.todolist.sqlite;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dk.todolist.R;
import com.dk.todolist.util.PlannerUtil;
import com.dk.todolist.vo.ItemVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "TodoList.db";
    private static final int DB_VERSION = 1;

    // 가계부 contents
    private static final String DB_TABLE = "planner_contents";
    private static final String FIELD_ID = "ID";
    private static final String FIELD_ITEM_CONTENTS = "ITEM_CONTENTS";
    private static final String FIELD_ITEM_COM_YN = "ITEM_COM_YN";
    private static final String FIELD_PRIOR_ORDER = "PRIOR_ORDER";
    private static final String FIELD_DEL_YN = "DEL_YN";
    private static final String FIELD_CREATE_USER_ID = "CREATE_USER_ID";
    private static final String FIELD_CREATE_DATE = "CREATE_DATE";
    private static final String FIELD_UPDATE_USER_ID = "UPDATE_USER_ID";
    private static final String FIELD_UPDATE_DATE = "UPDATE_DATE";
    private Context context;
    public SqliteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + "(" +
                FIELD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FIELD_ITEM_CONTENTS + " TEXT NOT NULL," +
                FIELD_ITEM_COM_YN + " TEXT NOT NULL," +
                FIELD_PRIOR_ORDER + " INTEGER," +
                FIELD_DEL_YN + " TEXT NOT NULL," +
                FIELD_CREATE_USER_ID + " TEXT," +
                FIELD_CREATE_DATE + " TEXT," +
                FIELD_UPDATE_USER_ID + " TEXT," +
                FIELD_UPDATE_DATE + " TEXT)";
        db.execSQL(createTableQuery);

//        ItemVo vo = new ItemVo();
//        vo.setId(1);
//        vo.setItemComYn("N");
//        vo.setPriorOrder(1);
//        vo.setItemContents(this.context.getString(R.string.textTouch));
//
//        insertItem(vo);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public long insertItem(ItemVo vo) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ITEM_CONTENTS, vo.getItemContents());
        values.put(FIELD_ITEM_COM_YN, vo.getItemComYn());
        values.put(FIELD_PRIOR_ORDER, vo.getPriorOrder());
        values.put(FIELD_DEL_YN, "N");
        values.put(FIELD_CREATE_USER_ID, vo.getCreateUserId());
        values.put(FIELD_CREATE_DATE, nowDate.format(new Date()));

        long result = db.insert(DB_TABLE, null, values);

        return result;
    }

    public boolean updateItem(ItemVo vo) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat nowDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(FIELD_ITEM_CONTENTS, vo.getItemContents());
        values.put(FIELD_ITEM_COM_YN, vo.getItemComYn());
        values.put(FIELD_PRIOR_ORDER, vo.getPriorOrder());
        values.put(FIELD_DEL_YN, "N");
        values.put(FIELD_UPDATE_USER_ID, vo.getUpdateUserId());
        values.put(FIELD_UPDATE_DATE, nowDate.format(new Date()));

        long result = db.update(DB_TABLE, values, FIELD_ID+"=?", new String[]{String.valueOf(vo.getId())});

        return result != -1;
    }

    public boolean deleteItem(ItemVo vo) {
        SQLiteDatabase db = getWritableDatabase();
        long result = db.delete(DB_TABLE, FIELD_ID+"=?", new String[]{String.valueOf(vo.getId())});

        return result != -1;
    }

    public ItemVo getItemByCondition(ItemVo schVo) {
        Cursor cursor = getItemList(schVo);

        ItemVo returnVo = new ItemVo();

        if (cursor.getCount() == 0) {
            return returnVo;
        } else if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) {
                returnVo = setItemVo(cursor);
            }
        }
        cursor.close();
        return returnVo;
    }

    public ArrayList<ItemVo> getItemListByCondition(ItemVo schVo, boolean isRepetition) {
        Cursor cursor = getItemList(schVo);

        ArrayList<ItemVo> returnList = new ArrayList<ItemVo>();

        if (cursor.getCount() > 0){
            if (cursor.moveToFirst()) {
                do {
                    ItemVo returnVo = new ItemVo();
                    returnVo = setItemVo(cursor);
                    returnList.add(returnVo);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        return returnList;
    }

    private Cursor getItemList(ItemVo schVo) {
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DB_TABLE;
        StringBuilder whereQuery = new StringBuilder(" WHERE 1=1 ");

        if (schVo.getId() > 0) {
            whereQuery.append(" AND ").append(FIELD_ID).append("=").append(schVo.getId());
        }
        if (null != schVo.getItemContents() && !schVo.getItemContents().isEmpty()) {
            whereQuery.append(" AND ").append(FIELD_ITEM_CONTENTS).append("='").append(schVo.getItemContents()).append("'");
        }
        if (null != schVo.getSchIitemContents() && !schVo.getSchIitemContents().isEmpty()) {
            whereQuery.append(" AND UPPER(").append(FIELD_ITEM_CONTENTS).append(") LIKE UPPER('%").append(schVo.getSchIitemContents()).append("%')");
        }
        if (null != schVo.getItemComYn() && !schVo.getItemComYn().isEmpty()) {
            whereQuery.append(" AND ").append(FIELD_ITEM_COM_YN).append("='").append(schVo.getItemComYn()).append("'");
        }

        String orderByQuery = " ORDER BY " + FIELD_ITEM_COM_YN + " ASC, " + FIELD_PRIOR_ORDER + ", " + FIELD_CREATE_DATE + PlannerUtil.getCurrentOrder();

        if (null != schVo.getSchIitemContents() && !schVo.getSchIitemContents().isEmpty()) {
            orderByQuery = " ORDER BY " + FIELD_CREATE_DATE + PlannerUtil.getCurrentOrder();
        }

        return db.rawQuery(selectQuery+whereQuery.toString()+orderByQuery, null);
    }

    @SuppressLint("Range")
    private ItemVo setItemVo(Cursor cursor) {
        ItemVo returnVo = new ItemVo();

        returnVo.setId(cursor.getInt(cursor.getColumnIndex(FIELD_ID)));
        returnVo.setItemContents(cursor.getString(cursor.getColumnIndex(FIELD_ITEM_CONTENTS)));
        returnVo.setItemComYn(cursor.getString(cursor.getColumnIndex(FIELD_ITEM_COM_YN)));
        returnVo.setPriorOrder(cursor.getInt(cursor.getColumnIndex(FIELD_PRIOR_ORDER)));
        returnVo.setDelYn(cursor.getString(cursor.getColumnIndex(FIELD_DEL_YN)));
        returnVo.setCreateUserId(cursor.getString(cursor.getColumnIndex(FIELD_CREATE_USER_ID)));
        returnVo.setCreateDate(cursor.getString(cursor.getColumnIndex(FIELD_CREATE_DATE)));
        returnVo.setUpdateUserId(cursor.getString(cursor.getColumnIndex(FIELD_UPDATE_USER_ID)));
        returnVo.setUpdateDate(cursor.getString(cursor.getColumnIndex(FIELD_UPDATE_DATE)));
        returnVo.setRepetition(false);

        return returnVo;
    }
}
