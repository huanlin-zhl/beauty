package com.example.beauty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * 主页面
 *
 * @author huanlin-zhl
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {


    private Button addClothButton;
    private Button addNewScheduleButton;
    private Button queryScheduleButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        //从页面获取信息
        addClothButton = findViewById(R.id.add_cloth);
        addNewScheduleButton = findViewById(R.id.add_new_schedule);
        queryScheduleButton = findViewById(R.id.query_schedule);

        //未页面按钮添加监听器
        addClothButton.setOnClickListener(this);
        addNewScheduleButton.setOnClickListener(this);
        queryScheduleButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //根据点击按钮跳转不同页面
        switch (v.getId()){
            case R.id.add_cloth:
                Intent addClothIntent = new Intent(MainActivity.this, AddClothActivity.class);
                startActivity(addClothIntent);
                break;
            case R.id.add_new_schedule:
                Intent addScheduleIntent = new Intent(MainActivity.this, ClothScheduleActivity.class);
                startActivity(addScheduleIntent);
                break;
            case R.id.query_schedule:
                Intent queryScheduleIntent = new Intent(MainActivity.this, QueryScheduleActivity.class);
                startActivity(queryScheduleIntent);
                break;
            default:break;
        }
    }
}
