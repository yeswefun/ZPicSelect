package com.z.zpicselect;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/*
    ContentProvider

    点击跳转到选择图片页面
    选择图片|取消选择图片
    确定返回上一个页面(带上结果)
    压缩|加密上传到服务
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        请求码
     */
    public static final int REQUEST_IMAGE_LIST = 1;

    /*
        已经选中的图片列表
     */
    private ArrayList<PicEntity> mImageList = new ArrayList<>();

    /*
        选择图片
     */
    public void selectImage(View view) {
        // request permission: SDcard, camera

        // improve / optimize
        Intent intent = new Intent(this, PicSelectActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(PicSelectActivity.KEY_MAX_COUNT, 9);
        bundle.putInt(PicSelectActivity.KEY_SELECT_MODE, PicSelectActivity.MODE_MULTI);
        bundle.putParcelableArrayList(PicSelectActivity.KEY_SELECT_LIST, mImageList);
        bundle.putBoolean(PicSelectActivity.KEY_SHOW_CAMERA, true);
        intent.putExtra(PicSelectActivity.KEY_ARGS, bundle);
        startActivityForResult(intent, REQUEST_IMAGE_LIST);

//        ImageSelector.create()
//                .count(9)
//                .multi()
//                .showCamera(true)
//                .origin(mImageList)
//                .start(this, REQUEST_IMAGE_LIST);
    }

    public void compressImage(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_LIST && data != null) {
                mImageList = data.getParcelableArrayListExtra(PicSelectActivity.KEY_RESULT);
                // show selected image list
                Toast.makeText(this, "size: " + mImageList.size(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "data: " + mImageList);
            }
        }
    }
}

