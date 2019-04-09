package com.example.permissionutil;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.permissionutil.utils.PermissionUtils;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button request_single_permission,request_multiple_permission,request_single_custom_permission,request_multiple_custom_permission;
    // 相机权限、多个权限
    private final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    private final String PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALENDAR};

    // 打开相机请求Code，多个权限请求Code
    private final int REQUEST_CODE_CAMERA = 1,REQUEST_CODE_PERMISSIONS=2,REQUEST_CODE_LOCATION=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        request_single_permission.setOnClickListener(this);
        request_multiple_permission.setOnClickListener(this);
        request_single_custom_permission.setOnClickListener(this);
        request_multiple_custom_permission.setOnClickListener(this);
    }

    private void initView() {
        request_single_permission=findViewById(R.id.request_single_permission);
        request_multiple_permission=findViewById(R.id.request_multiple_permission);
        request_single_custom_permission=findViewById(R.id.request_single_custom_permission);
        request_multiple_custom_permission=findViewById(R.id.request_multiple_custom_permission);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.request_single_permission:
                //请求一个权限
                requestSinglePermission();
                break;
            case R.id.request_multiple_permission:
                //请求多个权限
                requestMultiplePermission();
                break;
            case R.id.request_single_custom_permission:
                //请求一个自定义权限
                requestSingleCustomPermission();
                break;
            case R.id.request_multiple_custom_permission:
                requestMultipleCustomPermission();
                break;
        }
    }


    /**
     * 解释权限的dialog
     *
     */
    private void showExplainDialog(String[] permission, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(this)
                .setTitle("申请权限")
                .setMessage("我们需要" + Arrays.toString(permission)+"权限")
                .setPositiveButton("确定", onClickListener)
                .show();
    }
    /**
     * 显示前往应用设置Dialog
     *
     */
    private void showToAppSettingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("需要权限")
                .setMessage("我们需要相关权限，才能实现功能，点击前往，将转到应用的设置界面，请开启应用的相关权限。")
                .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.toAppSetting(MainActivity.this);
                    }
                })
                .setNegativeButton("取消", null).show();
    }
    /**
     * 检测并申请多个自定义权限
     */
    private void requestMultipleCustomPermission() {
        PermissionUtils.checkMorePermissions(this, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                toCamera();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(final String... permission) {
                showExplainDialog(permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        PermissionUtils.requestMorePermissions(MainActivity.this,PERMISSIONS,REQUEST_CODE_PERMISSIONS);
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                PermissionUtils.requestMorePermissions(MainActivity.this,PERMISSIONS,REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    /**
     * 请求一个自定义权限
     */
    private void requestSingleCustomPermission() {
        PermissionUtils.checkPermission(this, PERMISSION_CAMERA, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                toCamera();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                //解释所需要的权限
                showExplainDialog(permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PermissionUtils.requestPermission(MainActivity.this, PERMISSION_CAMERA
                                , REQUEST_CODE_CAMERA);
                    }
                });
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {

                //用户拒绝显示权限申请
//                showToAppSettingDialog();
                PermissionUtils.requestPermission(MainActivity.this, PERMISSION_CAMERA, REQUEST_CODE_CAMERA);
            }
        });
    }

    /**
     * 检测并请求多个权限
     */
    private void requestMultiplePermission() {
        PermissionUtils.checkAndRequestMorePermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS, new PermissionUtils.PermissionRequestSuccessCallBack() {
            @Override
            public void onHasPermission() {
                //已经允许权限
                toCamera();
            }
        });
    }

    /**
     * 检测并请求一个权限
     */
    private void requestSinglePermission() {

        PermissionUtils.checkAndRequestPermission(this, PERMISSION_CAMERA, REQUEST_CODE_CAMERA, new PermissionUtils.PermissionRequestSuccessCallBack() {
            @Override
            public void onHasPermission() {
                //已经允许权限
                toCamera();
            }
        });
    }

    private void toCamera() {
        Intent intent = new Intent();
        intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
        startActivity(intent);
    }

    /**
     * 请求权限回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
                    //权限申请成功
                    toCamera();
                } else {
                    Toast.makeText(getBaseContext(),"打开相机失败",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_PERMISSIONS:
                PermissionUtils.onRequestMorePermissionsResult(MainActivity.this, PERMISSIONS, new PermissionUtils.PermissionCheckCallBack() {
                    @Override
                    public void onHasPermission() {
                        //权限申请成功
                        toCamera();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDown(String... permission) {
                        Toast.makeText(getBaseContext(), "我们需要"+Arrays.toString(permission)+"权限", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                        showToAppSettingDialog();
                    }
                });
                break;
            case REQUEST_CODE_LOCATION:
                if (PermissionUtils.isPermissionRequestSuccess(grantResults)) {
                    //权限申请成功
                    Toast.makeText(getBaseContext(),"开启定位",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(),"开启定位失败",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
