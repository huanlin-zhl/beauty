package com.example.beauty;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.beauty.common.constant.BeautyConstant;
import com.example.beauty.common.constant.URLs;
import com.example.beauty.common.response.BeautyResult;
import com.example.beauty.common.utils.GsonUtils;
import com.example.beauty.common.utils.HttpClientUtils;
import com.example.beauty.common.utils.ToastUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 查询页面，可输入clothId
 *
 * @author huanlin-zhl
 */
public class QueryScheduleActivity extends BaseActivity implements View.OnClickListener {

    private EditText clothIdText;
    private Button queryButton;
    private String clothId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_schedule);
        initView();
    }

    private void initView(){
        //从页面获取view
        clothIdText = findViewById(R.id.cloth_id);
        queryButton = findViewById(R.id.query_schedule);

        //添加监听器
        queryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.query_schedule:
                //获取输入的clothId，并构造url
                if (clothIdText.getText() == null || "".equals(clothIdText.getText().toString())){
                    ToastUtils.showToast(QueryScheduleActivity.this, "请输入衣服编号");
                }else {
                    clothId = clothIdText.getText().toString();
                    String url = URLs.FIND_ALL_SCHEDULE_BY_CLOTH_ID + clothId;

                    HttpClientUtils.get(url, new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            //请求失败，弹出提示
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(QueryScheduleActivity.this, "请求发送失败，添加失败");
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                            //请求成功，解析返回对象
                            final String beautyResultStr = response.body().string();
                            final BeautyResult beautyResult = GsonUtils.toEntity(beautyResultStr, BeautyResult.class);

                            //根据返回结果，进行处理
                            if(BeautyConstant.DEFAULT_SUCCESS_MESSAGE.equals(beautyResult.getMsg())){
                                //查找成功，准备信息，跳转展示页面
                                final Intent intent = new Intent(QueryScheduleActivity.this, ShowScheduleActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("beauty_result", beautyResultStr);
                                bundle.putString("clothId", clothId);
                                intent.putExtras(bundle);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(intent);
                                    }
                                });
                            }else {
                                //查找失败，提示后端返回错误信息
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(QueryScheduleActivity.this, beautyResult.getMsg());
                                    }
                                });
                            }
                        }
                    });
                }
                break;
            default:
                break;
        }
    }
}
