/*
 * *******************************************************************************
 *         文 件：MainActivity.java     模 块：app      项 目：onewechat
 *         当前修改时间：2019年09月28日 11:08:46
 *         上次修改时间：2019年09月28日 11:07:39
 *         作者：liutaos@qq.com        Copyright (c) 2019
 * *******************************************************************************
 */

package com.auto.onewechat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.auto.onewechat.interfaces.InfoCallback;
import com.auto.onewechat.utils.FileTools;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "com.auto.onewechat";
    private String start_tag;
    private String end_tag;
    private RadioGroup radioGroup;
    private String selectPkName;
    public final String appPackageName = "android.lite.clean";
    public String inPut;
    private final String SDCARD = "/storage/emulated/0/WechatNumber/";

    private InfoCallback mInfoCallback;
    SharedPreferences userSettings;
    TextView oldFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userSettings = getSharedPreferences("old_file", 0);
        oldFile = findViewById(R.id.old_file);
        oldFile.setText("上次的手机号：" + userSettings.getString("olde_file", ""));
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText inputs = findViewById(R.id.input_phone_number);

        inputs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inPut = s.toString();
                SharedPreferences.Editor editor = userSettings.edit();
                editor.putString("olde_file", inPut);
                editor.commit();
            }
        });

        findViewById(R.id.runBtn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                oldFile.setText("上次的手机号：" + userSettings.getString("olde_file", ""));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.e(TAG, "正在输出文件");
                            FileTools fileTools = new FileTools();
                            if (inPut == null) {
                                inPut = userSettings.getString("olde_file", "");
                            }
                            fileTools.writeTxtToFile(inPut + "\n", SDCARD, "WeChat_Number.txt");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
                new UiautomatorThread("com.auto.onewechat", "ExampleInstrumentedTest", "useAppContext").start();
                Toast.makeText(getApplicationContext(), "运行任务中。。。。。", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
