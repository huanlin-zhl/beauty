package com.example.beauty;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.beauty.common.constant.BeautyConstant;
import com.example.beauty.common.constant.URLs;
import com.example.beauty.common.domain.Cloth;
import com.example.beauty.common.domain.ClothScheduledLog;
import com.example.beauty.common.response.BeautyResult;
import com.example.beauty.common.utils.GsonUtils;
import com.example.beauty.common.utils.HttpClientUtils;
import com.example.beauty.common.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 展示衣服预定记录列表页面
 *
 * @author huanlin-zhl
 */
public class ShowScheduleActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

    private Intent intent;
    private Bundle bundle;

    private TextView clothIdText;
    private ListView scheduleList;
    private ImageView clothImage;
    private Button deleteButton;

    private String clothId;
    private LinkedList<ClothScheduledLog> logList;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_schedule);

        initView();
    }

    private void initView(){
        //获取页面参数
        clothIdText = findViewById(R.id.cloth_id);
        scheduleList = findViewById(R.id.schedule_list);
        clothImage = findViewById(R.id.cloth_image);
        deleteButton = findViewById(R.id.delete);

        //添加点击监听器
        deleteButton.setOnClickListener(this);

        //获取传入数据
        intent = this.getIntent();
        bundle = intent.getExtras();
        //从后端返回的result中解析已有预定记录列表信息
        String beautyResultStr = bundle.getString("beauty_result");
        logList = GsonUtils.getLogListFromBeautyResult(beautyResultStr);

        //处理记录展示内容放入数组
        String[] itemArray = new String[logList.size()];
        for(int i = 0; i < logList.size(); i++){
            String dateString = "" + (i+1) + ":   " + dateFormat.format(logList.get(i).getScheduledTime());
            itemArray[i] = dateString;
        }
        //获取clothId信息
        clothId = bundle.getString("clothId");

        //对页面展示进行填充
        //填充clothId
        clothIdText.setText(clothId);
        //填充衣服图片信息
        initClothImage();
        //填充预定记录列表
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ShowScheduleActivity.this, android.R.layout.simple_list_item_1, itemArray);
        scheduleList.setAdapter(arrayAdapter);
        scheduleList.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击预定记录进入预定详情页面，可对已有预定记录进行修改
        //获取点击的记录对象
        ClothScheduledLog scheduledLog = logList.get(position);
        //初始化修改页面所需参数
        Intent clothScheduleIntent = new Intent(ShowScheduleActivity.this, ClothScheduleActivity.class);
        Bundle clothScheduleBundle = new Bundle();
        clothScheduleBundle.putString("scheduleLog", GsonUtils.toJsonString(scheduledLog));
        clothScheduleIntent.putExtras(clothScheduleBundle);
        //页面跳转
        startActivity(clothScheduleIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.delete:
                //点击删除按钮，弹出是否确定删除提示框
                showDiaLog();
                break;
            default:
                break;

        }
    }

    private void showDiaLog(){
        //构建提示框信息
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowScheduleActivity.this);
        builder.setTitle("确定删除该衣服所有记录？");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击取消，提示框消失
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击确定按钮，对衣服进行删除
                //构建删除衣服url，并请求后端进行删除操作
                String deleteUrl = URLs.DELETE_CLOTH + clothId;
                HttpClientUtils.delete(deleteUrl, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //请求发送失败，弹出提示
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(ShowScheduleActivity.this, "请求发送失败，添加失败");
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        //得到后端相应，对返回结果进行解析
                        String str = response.body().string();
                        final BeautyResult beautyResult = GsonUtils.toEntity(str, BeautyResult.class);

                        //弹出后端信息（成功则弹出SUCCESS，失败则弹出错误提示）
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtils.showToast(ShowScheduleActivity.this, beautyResult.getMsg());
                            }
                        });
                    }
                });
            }
        });
        builder.show();
    }

    /**
     * 初始化衣服图片信息
     */
    private void initClothImage(){
        //首先根据衣服id获取衣服实例得到衣服图片存放地址
        HttpClientUtils.get(URLs.GET_CLOTH + clothId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //请求发送失败，弹出提示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(ShowScheduleActivity.this, "请求发送失败，添加失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                //请求响应成功，解析返回的result对象
                String beautyResultStr = response.body().string();
                final BeautyResult beautyResult = GsonUtils.toEntity(beautyResultStr, BeautyResult.class);
                //根据返回消息进行处理
                if(BeautyConstant.DEFAULT_SUCCESS_MESSAGE.equals(beautyResult.getMsg())){
                    //消息返回成功，解析衣服信息
                    Cloth cloth = GsonUtils.toEntity(GsonUtils.toJsonString(beautyResult.getData()), Cloth.class);
                    //获取衣服信息中的图片名称，并构建url通过Glide去后端获取图片信息填充页面
                    String clothImageFileName = cloth.getPicture();
                    final String loadClothImageUrl = URLs.LOAD_IMAGE + clothImageFileName;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(ShowScheduleActivity.this).load(loadClothImageUrl).into(clothImage);
                        }
                    });
                }else {
                    //获取图片失败，弹出提示
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(ShowScheduleActivity.this, beautyResult.getMsg());
                        }
                    });
                }
            }
        });
    }
}
