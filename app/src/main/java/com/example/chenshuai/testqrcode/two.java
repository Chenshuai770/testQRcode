package com.example.chenshuai.testqrcode;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

/**
 * Created by chenshuai on 2016/7/31.
 */
public class two extends Activity implements QRCodeView.Delegate {
    private static final String TAG = two.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;
    private SurfaceView surfaceView;

    private QRCodeView mQRCodeView;
    private RelativeLayout rl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxingview);
       /* rl= (RelativeLayout) findViewById(R.id.rl);
        rl.getBackground().setAlpha(30);*/



        mQRCodeView = (ZXingView) findViewById(R.id.zxingview);
        mQRCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mQRCodeView.showScanRect();

        if (requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY && resultCode == Activity.RESULT_OK && null != data) {
            String picturePath;
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                picturePath = c.getString(columnIndex);
                c.close();
            } catch (Exception e) {
                picturePath = data.getData().getPath();
            }

            if (new File(picturePath).exists()) {
                QRCodeDecoder.decodeQRCode(BitmapFactory.decodeFile(picturePath), new QRCodeDecoder.Delegate() {
                    @Override
                    public void onDecodeQRCodeSuccess(String result) {
                        Toast.makeText(two.this, result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDecodeQRCodeFailure() {
                        Toast.makeText(two.this, "未发现二维码", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
