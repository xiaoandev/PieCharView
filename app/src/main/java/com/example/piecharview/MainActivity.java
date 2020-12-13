package com.example.piecharview;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.piecharview.bean.Pie;
import com.example.piecharview.view.PieCharView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author uidq2429
 * @since 2020.12.10
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PieCharView pieView;
    private static final int MAX_PERCENT = 360; //最大度数
    private List<Pie> mList;
    private Button select_one;
    private Button select_two;
    private Button select_three;
    private Button select_four;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiView();
        initData();

        //默认显示第一种模式
        mList = new ArrayList<>();
        for (int i = 0; i < Constant.mModeOne.length; i++)
            mList.add(new Pie(MAX_PERCENT / Constant.mModeOne.length, Constant.mModeOne[i]));
        pieView.setPieData(mList);
        pieView.setCenterText("驾驶模式");
    }

    private void initData() {

//        pieView.setShowOutText(true);
//        pieView.setShowInText(true);
//        pieView.setTextInSize(20);
//        pieView.setTextOutSize(15);
//        pieView.setStillRadius(10);
        pieView.setTextInColor(Color.RED);
        pieView.setTextOutColor(Color.BLACK);
        pieView.setRoundWidth(100);
        pieView.setOffsetX(100);
        pieView.setOffsetY(50);

    }

    private void intiView() {
        pieView = (PieCharView) findViewById(R.id.pie_view);
        select_one = (Button) findViewById(R.id.select_one);
        select_two = (Button) findViewById(R.id.select_two);
        select_three = (Button) findViewById(R.id.select_three);
        select_four = (Button) findViewById(R.id.select_four);

        select_one.setOnClickListener(this);
        select_two.setOnClickListener(this);
        select_three.setOnClickListener(this);
        select_four.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_one:
                mList = new ArrayList<>();
                for (int i = 0; i < Constant.mModeOne.length; i++)
                    mList.add(new Pie(MAX_PERCENT / Constant.mModeOne.length, Constant.mModeOne[i]));
                pieView.setPieData(mList);
                break;
            case R.id.select_two:
                mList = new ArrayList<>();
                for (int i = 0; i < Constant.mModeTwo.length; i++)
                    mList.add(new Pie(MAX_PERCENT / Constant.mModeTwo.length, Constant.mModeTwo[i]));
                pieView.setPieData(mList);
                break;
            case R.id.select_three:
                mList = new ArrayList<>();
                for (int i = 0; i < Constant.mModeThree.length; i++)
                    mList.add(new Pie(MAX_PERCENT / Constant.mModeThree.length, Constant.mModeThree[i]));
                pieView.setPieData(mList);
                break;
            case R.id.select_four:
                mList = new ArrayList<>();
                for (int i = 0; i < Constant.mModeFour.length; i++)
                    mList.add(new Pie(MAX_PERCENT / Constant.mModeFour.length, Constant.mModeFour[i]));
                pieView.setPieData(mList);
                break;
            default:
                break;
        }
    }
}