package com.example.beauty;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.beauty.common.constant.URLs;
import com.example.beauty.common.domain.ClothScheduledLog;
import com.example.beauty.common.response.BeautyResult;
import com.example.beauty.common.utils.DatePickUtils;
import com.example.beauty.common.utils.GsonUtils;
import com.example.beauty.common.utils.HttpClientUtils;
import com.example.beauty.common.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 展示衣服以及对应时间选择框页面
 *
 * @author huanlin-zhl
 */
public class ClothScheduleActivity extends BaseActivity implements View.OnClickListener {

    private Intent intent;
    private Bundle bundle;
    private ClothScheduledLog scheduleLog;

    private EditText clothIdText;
    private DatePicker datePicker;
    private Button confirmButton;

    private static final String ADD_NEW_SCHEDULE_BUTTON_TEXT = "确定";
    private static final String UPDATE_SCHEDULE_BUTTON_TEXT = "修改";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloth_schedule);
        initView();
    }

    private void initView(){
        //获取传入信息，根据传入信息决定是添加预定记录还是修改记录
        intent = this.getIntent();
        bundle = intent.getExtras();
        if(bundle != null){
            scheduleLog = GsonUtils.toEntity(bundle.getString("scheduleLog"), ClothScheduledLog.class);
        }

        //获取页面view
        clothIdText = findViewById(R.id.cloth_id);
        datePicker = findViewById(R.id.date_time);
        confirmButton = findViewById(R.id.confirm);

        if(scheduleLog == null){
            //添加新记录
            confirmButton.setText(ADD_NEW_SCHEDULE_BUTTON_TEXT);
        }else {
            //修改记录，填充text以及DatePick
            clothIdText.setText(scheduleLog.getClothId());
            DatePickUtils.initDatePicker(datePicker, scheduleLog.getScheduledTime());
            confirmButton.setText(UPDATE_SCHEDULE_BUTTON_TEXT);
        }

        //添加点击监听器
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confirm){
            if(ADD_NEW_SCHEDULE_BUTTON_TEXT.equals(confirmButton.getText())){
                //添加心预定记录
                addNewSchedule();
            }else if (UPDATE_SCHEDULE_BUTTON_TEXT.equals(confirmButton.getText())){
                //修改已有预定记录
                updateSchedule();
            }
        }

    }

    /**
     * 添加新记录
     */
    private void addNewSchedule(){
        //校验参数
        if(clothIdText.getText() == null || "".equals(clothIdText.getText().toString())){
            ToastUtils.showToast(ClothScheduleActivity.this, "请输入衣服编号");
        }else {
            //构建预定记录对象并发送请求
            final ClothScheduledLog newScheduledLog = ClothScheduledLog.builder()
                    .clothId(clothIdText.getText().toString())
                    .scheduledTime(DatePickUtils.getDate(datePicker))
                    .build();

            post(URLs.ADD_NEW_SCHEDULE, newScheduledLog);
        }
    }


    /**
     * 修改已有预定记录
     */
    private void updateSchedule(){
        //获取原有记录实例，修改scheduleTime为DatePick当前时间，发送请求
        ClothScheduledLog newScheduleLog = scheduleLog;
        newScheduleLog.setScheduledTime(DatePickUtils.getDate(datePicker));
        post(URLs.UPDATE_SCHEDULE, newScheduleLog);
    }

    /**
     * 发送请求
     * @param url 请求url
     * @param scheduleLog 预定记录对象
     */
    private void post(String url, ClothScheduledLog scheduleLog){
        HttpClientUtils.post(url, scheduleLog, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //发送失败，弹出提示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(ClothScheduleActivity.this, "请求发送失败，添加失败");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                //获得服务器响应，解析返回对象
                String str = response.body().string();
                final BeautyResult beautyResult = GsonUtils.toEntity(str, BeautyResult.class);
                //成功或者失败都直接弹出服务器返回提示
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast(ClothScheduleActivity.this, beautyResult.getMsg());
                    }
                });
            }
        });
    }
}
