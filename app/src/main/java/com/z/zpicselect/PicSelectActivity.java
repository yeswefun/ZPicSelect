package com.z.zpicselect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
    图片选择
 */
public class PicSelectActivity extends AppCompatActivity implements View.OnClickListener, PicSelectListener {

    private static final String TAG = "IMAGE";

    // 多选
    public static final int MODE_MULTI = 1;
    // 单选
    public static final int MODE_SINGLE = 2;

    // 加载类型
    public static final int LOAD_TYPE = 0x0021;


    //参数
    public static final String KEY_ARGS = "key_args";

    //选择图片模式
    public static final String KEY_SELECT_MODE = "key_select_mode";

    //是否显示相机的EXTRA_KEY
    public static final String KEY_SHOW_CAMERA = "SHOW_CAMERA";

    //总共可以选择多少张图片的EXTRA_KEY
    public static final String KEY_MAX_COUNT = "key_max_count";

    //原始的图片路径的EXTRA_KEY
    public static final String KEY_SELECT_LIST = "key_select_list";

    //返回选择图片列表的EXTRA_KEY
    public static final String KEY_RESULT = "key_result";



    //图片选择器的参数
    private Bundle mSelectArgs;

    //模式，单选还是多选
    private int mMode = MODE_SINGLE;

    //最多可选张数
    private int mMaxCount = 9;

    //是否显示相机
    private boolean isShowCamera = true;

    //选择的图片结果
    private ArrayList<PicEntity> mResultList;

    //打开相机时的临时文件路径
    private String mTempFilePath;

    private RecyclerView mImageList;

    private String TEMP_KEY = "temp_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_select);
        initView();
        initData();
    }

    /*
        1. 获取上一个页面传入的参数
     */
    private void initData() {
        mSelectArgs = getIntent().getBundleExtra(KEY_ARGS);
        mMode = mSelectArgs.getInt(KEY_SELECT_MODE, MODE_SINGLE);
        mMaxCount = mSelectArgs.getInt(KEY_MAX_COUNT, 1);
        isShowCamera = mSelectArgs.getBoolean(KEY_SHOW_CAMERA, true);
        mResultList = mSelectArgs.getParcelableArrayList(KEY_SELECT_LIST);
//        mResultList = mSelectArgs.getStringArrayList(KEY_SELECT_LIST);
        if (mResultList == null) {
            mResultList = new ArrayList<>();
        }

        // 2. 通过ContentProvider获取本地图片
        initImageList();

        // 4. 改变显示信息
        updateSelectedStateInfo();
    }

    /*
        2. 通过ContentProvider获取本地图片
            耗时操作
     */
    private void initImageList() {
        //耗时操作 time-consuming operation
        // 1. spwan Thread; 2. AsyncTask
        // LoaderManager.getInstance()
        getSupportLoaderManager().initLoader(LOAD_TYPE, null, mLoadCallBack);
    }

    //预览按钮
    private TextView mSelectPreviewTv;

    //显示所选张数
    private TextView mSelectNumTv;

    //确定按钮
    private TextView mSelectOkTv;

    /*
        需要实时更新
     */
    private void updateSelectedStateInfo() {

        if (mResultList.size() > 0) {
            mSelectPreviewTv.setEnabled(true);
            mSelectNumTv.setOnClickListener(this);
        } else {
            mSelectPreviewTv.setEnabled(false);
            mSelectNumTv.setOnClickListener(null);
        }

        mSelectNumTv.setText(mResultList.size() + "/" + mMaxCount);
    }

    /**
     * 加载图片的回调
     */
    private LoaderManager.LoaderCallbacks<Cursor> mLoadCallBack = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // 查询语句
            CursorLoader cursorLoader = new CursorLoader(
                    PicSelectActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
                    new String[]{"image/jpeg", "image/png"},
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null && data.getCount() > 0) {

                List<PicEntity> picList = new ArrayList<>();

                // add flag for needing to show camera(the first one)
                if (isShowCamera) {
                    picList.add(null);
                }

                // only save image path
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    long time = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    PicEntity picEntity = new PicEntity(path, name, time);
                    picList.add(picEntity);
                    Log.e(TAG, "pic: " + picEntity);
                }

                // 3. 显示图片列表
                showImageList(picList);
            }
        }

        public void test() {
            /*if (data != null && data.getCount() > 0){
                List<ImageEntity> images = new ArrayList<>();
                data.moveToFirst();

                while (data.moveToNext()){
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    Log.w(TAG, path + "  " + name + "  " + dateTime);

                    // 判断文件是不是存在
                    if (!pathExist(path)) {
                        continue;
                    }
                    // 封装数据对象
                    ImageEntity image = new ImageEntity(path, name, dateTime);
                    images.add(image);
                }

                Log.d(TAG,"images:" + images.size());

                // 显示列表数据
                showListData(images);
            }*/
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        /**
         * 判断该路径文件是不是存在
         */
        /*private boolean pathExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }*/
    };

    /*
        3. 显示图片列表
     */
    private void showImageList(List<PicEntity> picList) {
        PicListAdapter picListAdapter = new PicListAdapter(this, picList, mResultList, mMaxCount);
        picListAdapter.setOnPicSelectListener(this);
        mImageList.setLayoutManager(new GridLayoutManager(this, 4));
        mImageList.setAdapter(picListAdapter);
    }

    public void initView() {
        mImageList = findViewById(R.id.image_list_rv);
        mSelectNumTv = findViewById(R.id.select_num);
        mSelectPreviewTv = findViewById(R.id.select_preview);
        mSelectOkTv = findViewById(R.id.select_finish);
        mSelectOkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmImageSelected();
            }
        });
    }

    /*
        点击确定按钮, 将已经选择的图片返回给上一个页面
     */
    private void confirmImageSelected() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(KEY_RESULT, mResultList);
        setResult(RESULT_OK, intent);
        finish();// close current activity
    }

    /*
        图片预览
     */
    @Override
    public void onClick(View v) {

    }

    /*
        每次选择都及时更新状态信息
     */
    @Override
    public void onSelect() {
        updateSelectedStateInfo();
    }
}