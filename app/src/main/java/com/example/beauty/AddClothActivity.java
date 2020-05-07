package com.example.beauty;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View.OnClickListener;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.beauty.common.constant.URLs;
import com.example.beauty.common.response.BeautyResult;
import com.example.beauty.common.utils.GsonUtils;
import com.example.beauty.common.utils.HttpClientUtils;
import com.example.beauty.common.utils.ImageUtils;
import com.example.beauty.common.utils.PermissionUtil;
import com.example.beauty.common.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 添加新衣服页面
 *
 * @author huanlin-zhl
 */
public class AddClothActivity extends BaseActivity implements OnClickListener {

    private EditText clothIdText;
    private ImageView clothImg;
    private Button nativePicture;
    private Button takePhoto;
    private Button submit;

    private static String imagePath;
    private static Uri imageUri;

    private String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cloth);
        initView();
    }

    private void initView() {
        //获取页面参数
        clothIdText = findViewById(R.id.cloth_id);
        clothImg = findViewById(R.id.cloth_picture);
        nativePicture = findViewById(R.id.native_picture);
        takePhoto = findViewById(R.id.take_photo);
        submit = findViewById(R.id.submit);

        //添加点击事件
        nativePicture.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        submit.setOnClickListener(this);

        //申请权限
        PermissionUtil.requestPermission(AddClothActivity.this, permissions);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //EasyPermission要求添加
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.native_picture:
                //从本地相册获取图片
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_PICK);
                startActivityForResult(local, 1);
                break;
            case R.id.take_photo:
                //从相机获取图片
                imagePath = getExternalFilesDir(null).getPath() + "/" + System.currentTimeMillis() + ".jpg";
                File cameraSavePath = new File(imagePath);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //注：需要在Manifest中配置FileProvider信息
                imageUri = FileProvider.getUriForFile(AddClothActivity.this,
                        "com.example.beauty.fileprovider", cameraSavePath);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);
                break;
            case R.id.submit:
                //提交新衣服信息，需要校验图片地址是否为空
                if (imagePath == null || "".equals(imagePath)) {
                    ToastUtils.showToast(AddClothActivity.this, "文件不存在");
                }else{
                    //开始进行上传操作
                    upload();
                }
                break;
            default:break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //根据返回的requestCode确定相应信息
        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1:
                    //从本地相册传回的图片
                    //获取uri以及sd卡中真实全路径
                    imageUri = data.getData();
                    imagePath = ImageUtils.getRealPathFromUri(AddClothActivity.this, imageUri);

                    //使用Glide实现对图片的动态加载
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                    clothImg.setImageBitmap(bitmap);
                    Glide.with(this).load(imagePath).into(clothImg);
                    break;
                case 2:
                    //从相机返回的图片，其中uri以及imagePath已经在调用相机前设置过了
                    Glide.with(AddClothActivity.this).load(imagePath).into(clothImg);
                case 3:
                    //测试使用，release版本可进行注释
                    Glide.with(AddClothActivity.this).load(imagePath).into(clothImg);
                    break;
                default:
                    break;
            };
        }
//    	n =1;
    }

    /**
     * 执行上传操作
     */
    private void upload(){
        //校验衣服Id是否为空
        if(clothIdText.getText() == null || "".equals(clothIdText.getText().toString())){
            ToastUtils.showToast(AddClothActivity.this, "请输入衣服编号");
        }else if (imagePath == null || !(new File(imagePath).exists())){
            ToastUtils.showToast(AddClothActivity.this, "请添加图片");
        }else {
            //获取图片文件，并构建图片的requestBody对象
            File clothImageFile = new File(imagePath);
            RequestBody imageRequestBody = RequestBody.Companion.create(clothImageFile, MediaType.parse("image/jpg"));

            //构建添加新衣服请求的requestBody，发送请求
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("clothId", clothIdText.getText().toString())
                    .addFormDataPart("clothImage", clothImageFile.getName(), imageRequestBody)
                    .build();
            HttpClientUtils.upload(URLs.ADD_NEW_CLOTH, requestBody, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //请求失败，弹出提示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(AddClothActivity.this, "请求发送失败，添加失败");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //解析返回对象
                    String str = response.body().string();
                    final BeautyResult beautyResult = GsonUtils.toEntity(str, BeautyResult.class);
                    //成功或者失败都可直接提示后端返回消息（成功为SUCCESS，失败为错误提示）
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(AddClothActivity.this, beautyResult.getMsg());
                        }
                    });
                }
            });
        }
    }

}
