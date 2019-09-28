/*
 * *******************************************************************************
 *         文 件：ExampleInstrumentedTest.java     模 块：app      项 目：onewechat
 *         当前修改时间：2019年09月28日 11:08:46
 *         上次修改时间：2019年09月28日 10:59:18
 *         作者：liutaos@qq.com        Copyright (c) 2019
 * *******************************************************************************
 */

package com.auto.onewechat;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.auto.onewechat.tools.FileTools;
import com.auto.onewechat.tools.ReadTextFile;
import com.auto.onewechat.tools.RootCmd;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    public static final String TAG = "com.auto.onewechat";
    private com.auto.onewechat.Processer mProcesser;
    UiDevice mDevice;
    private static final String CLEAN_PKG_NAME = "android.lite.clean";

    private static final String STRING_TO_BE_TYPED = "UiAutomator";
    private final String SDCARD = "/storage/emulated/0/WechatNumber/";

    public static String sms, cellPhoneNumberSJ, cellPhoneNumber;

    public List<String> mApplist = new ArrayList();
    public boolean next = true;
    Context appContext;

    RootCmd rootCmd;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.auto.onewechat", appContext.getPackageName());
        mDevice = UiDevice.getInstance(androidx.test.platform.app.InstrumentationRegistry.getInstrumentation());
        mProcesser = new com.auto.onewechat.Processer(mDevice, CLEAN_PKG_NAME);
        rootCmd = new RootCmd();
        mProcesser.pritLog("======      正在清理 清理大师数据  = =======" + CLEAN_PKG_NAME);
        //mDevice.executeShellCommand("pm clear " + CLEAN_PKG_NAME);
        clear();
        mProcesser.pritLog("======      已清理 清理大师数据  = =======" + CLEAN_PKG_NAME);
        try {
            ReadTextFile read = new ReadTextFile();
            while (true) {
                //sms = null;
                Thread.sleep(3 * 1000);
                String phone_number = read.read(SDCARD+"WeChat_Number.txt");
                cellPhoneNumber = getStringNoBlank(phone_number);
                if (cellPhoneNumber == "" || cellPhoneNumber == null) {
                    mProcesser.pritLog("脚本执行结束  号码为空");
                    break;
                }
                mProcesser.startApp();
                mProcesser.waitAMonent(10);
                goToOneStart();
                goToUpdate();
                mProcesser.waitAMonent(2);
                goToHomeInterface();
                mProcesser.waitAMonent(1);
                goToCloseRedPkg();
                mProcesser.waitAMonent(2);
                if (next) {
                    mainTasks();
                } else {
                    break;
                }
                mProcesser.pritLog("===================进行下一个号码=====================");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.pritLog("错误");
        }
    }

    public static String getStringNoBlank(String str) {

        if (str != null && !"".equals(str)) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            String strNoBlank = m.replaceAll("");
            return strNoBlank;
        } else {
            return str;
        }
    }

    /**
     * 清理清理大师数据
     */
    public void clear() {
        String result = rootCmd.execRootCmd("pm clear " + CLEAN_PKG_NAME);
        Log.i("com.auto.onewechat", "清理清理大师数据   = " + result);
    }

    /**
     * 清理大师主页
     */
    public void goToHomeInterface() {
        mProcesser.pritLog("====================goToHomeInterface()=====================");
        UiObject userLayout;
        try {
            userLayout = new UiObject(new UiSelector().className("android.widget.LinearLayout")
                    .resourceId("android.lite.clean:id/a6r"));
            userLayout.click();
            if (mDevice.hasObject(By.text("登录任务中心"))) {
                UiObject2 sigin = mDevice.wait(Until.findObject(By.text("登录任务中心")), 500);
                sigin.click();
                goSignIn();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.waitAMonent(1);
            //mDevice.pressBack();
        }
        mProcesser.waitAMonent(1);
        //goToCloseRedPkg();
    }

    /**
     * 清理大师首次启动页 点击启动
     */

    public void goToOneStart() {
        mProcesser.pritLog("============== goToOneStart ===================");
        UiObject userOK = new UiObject(new UiSelector().className("android.widget.RelativeLayout")
                .resourceId("android.lite.clean:id/s4"));
        try {
            userOK.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        mProcesser.waitAMonent(3);

    }

    /**
     * 打开红包
     */
    public void goToCloseRedPkg() {
        mProcesser.pritLog("====================goToCloseRedPkg()=====================");
        UiObject userOpenRedPkg, userLayout;
        try {
            if (mProcesser.exitObjById("android.lite.clean:id/x3", 1)) {
                userOpenRedPkg = new UiObject(new UiSelector().className("android.widget.ImageView")
                        .resourceId("android.lite.clean:id/x3"));
                userOpenRedPkg.click();
                mProcesser.waitAMonent(1);
                goSignIn();
            } else {
                userLayout = new UiObject(new UiSelector().className("android.widget.LinearLayout")
                        .resourceId("android.lite.clean:id/a6r"));
                userLayout.click();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.waitAMonent(1);
            mDevice.pressBack();
        }

    }

    /**
     * 清理大师更新提示 点击忽略
     */
    public void goToUpdate() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {
                    mProcesser.waitAMonent(2);
                    try {
                        //if (mProcesser.exitObjById("android.lite.clean:id/h5", 1)) {
                        if (mDevice.hasObject(By.text("忽略"))) {
                            mProcesser.pritLog("====================goToUpdate()=====================");
                            UiObject2 ignore = mDevice.wait(Until.findObject(By.text("忽略")), 500);
                            ignore.click();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        mProcesser.waitAMonent(1);
                    } finally {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
        goToHomeInterface();
    }


    /**
     * 登录注册
     */

    public com.auto.onewechat.HttpGet httpGet = new com.auto.onewechat.HttpGet();
    public UiObject userSignIn;

    public void goSignIn() {

        mProcesser.pritLog("====================goSignIn()=====================");
        try {
            while (mDevice.hasObject(By.text("手机号码一键登录"))) {
                UiObject2 userSignin = mDevice.wait(Until.findObject(By.text("手机号码一键登录")), 500);
                userSignin.click();
                mProcesser.waitAMonent(30);
                Thread.sleep(30 * 1000);
            }
            mProcesser.pritLog("============== HttpGet()  初始化 请求数据 ===================");
            httpGet.getData();
            mProcesser.waitAMonent(2);
            UiObject userSignIns = new UiObject(new UiSelector().className("android.widget.EditText").resourceId("android.lite.clean:id/wj"));
            //-----------------     取手机号   随机取      ----
            /*cellPhoneNumberSJ = httpGet.getCellNumberSJ();

            if (mProcesser.exitObjById("android.lite.clean:id/wj", 1)) {
                mProcesser.pritLog(" =======   cellPhoneNumber : +++++++++++++++ " + cellPhoneNumberSJ);
                userSignIns.setText(cellPhoneNumberSJ);
            }
            sms = httpGet.getSmsMsg(cellPhoneNumberSJ);*/

            //-----------------     取手机号   指定号码      ----
            cellPhoneNumber = httpGet.getCellNumberZJSJH(cellPhoneNumber);
            //-----------------------      等待短信验证码 ------------
            boolean result = false;
            int i = 0;
            sms = null;
            //------------------------输入手机号
            if (mProcesser.exitObjById("android.lite.clean:id/wj", 1)) {
                mProcesser.pritLog(" =======   cellPhoneNumber : +++++++++++++++    " + cellPhoneNumber);
                userSignIns.setText(cellPhoneNumber);
            }
            while (!result && sms == null) {
                sms = httpGet.getSmsMsg(cellPhoneNumber);
                mProcesser.pritLog("-----------cellPhoneNumber ---------短信获取次数------   " + i);
                try {
                    TimeUnit.SECONDS.sleep(11);
                    if (sms == null) {
                        sms = httpGet.getSmsMsg(cellPhoneNumber);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //TimeUnit.SECONDS.sleep(11);
                //sms = httpGet.getSmsMsg(cellPhoneNumberSJ);

                mProcesser.pritLog("-----------  SMS  ---------------   " + sms);
                i++;
                if (i == 6) {
                    System.out.println("======   ==== result = false ======");
                    result = true;
                }
            }
            if (sms == null) {
                next = false;
                System.out.println("==================   SMS  信息== 空  下一步 =========     " + sms);
                return;
            }
            String sms_number = Pattern.compile("[^0-9]").matcher(sms).replaceAll("");
            System.out.println("==================   SMS  信息==================     " + sms);
            sms = sms_number;
            smsInput(sms);

        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.waitAMonent(1);
            //mDevice.pressBack();
        }

    }

    /**
     * 短信验证码输入
     */
    public void smsInput(String sms_number) throws Exception {
        System.out.println("     legacySetText      " + sms_number);
        if (sms_number == null) {
            System.out.println("          空         ");
            return;
        }
        UiObject smstext1 = new UiObject(new UiSelector().className("android.widget.TextView").instance(2));
        smstext1.legacySetText(sms_number);
        sms = null;
        mProcesser.waitAMonent(2);
        httpGet.singOut();
        //okGotIt();
        if (mDevice.hasObject(By.text("知道了"))) {
            UiObject2 objNext = mDevice.wait(Until.findObject(By.text("知道了")), 500);
            objNext.click();
        }

    }

    /**
     * 知道了 按钮 点击
     */
    public void okGotIt() throws Exception {

        //UiObject ok_next = new UiObject(new UiSelector().className("android.widget.TextView").resourceId("android.lite.clean:id/et"));
        UiObject2 ok_next;
        while (mDevice.hasObject(By.text("知道了"))) {
            ok_next = mDevice.wait(Until.findObject(By.text("知道了")), 500);
            ok_next.click();
            mProcesser.waitAMonent(2);
            Log.i(TAG, "点击知道了===========");
            Thread.sleep(1000);
        }
    }

    /**
     * 个人中心 点击各种 项目
     */

    public void mainTasks() throws Exception {

        //金币明细  Details of gold coins
        System.out.println("=================  mainTasks () 执行业务模块 ========================");
        mProcesser.waitAMonent(2);
        System.out.println("=================  mainTasks () =  金币明细  开始========================");
        UiObject2 detailed;
        detailed();
        mProcesser.waitAMonent(3);
        System.out.println("=================  mainTasks () =  金币明细  结束========================");
        while (!mDevice.hasObject(By.text("任务攻略"))) {
            detailed();
        }

        mProcesser.pritLog("任务执行完毕================");
        mProcesser.waitAMonent(2);

    }

    /**
     * 刷新金币
     */
    public void detailed() throws Exception {
        boolean closeDeataild = false;
        //UiObject2 detailed;
        UiObject detailed1, titleDetailde;
        detailed1 = new UiObject(new UiSelector().className("android.widget.TextView").resourceId("android.lite.clean:id/zy"));
        if (detailed1 != null) {
            //detailed1 = mDevice.wait(Until.findObject(By.text("金币明细")), 500);
            detailed1.click();
            mDevice.waitForWindowUpdate(CLEAN_PKG_NAME, 3 * 1000);
            Thread.sleep(3000);
            Log.e(TAG, "  detailed1   " + detailed1);
            titleDetailde = new UiObject(new UiSelector().className("android.widget.TextView").resourceId("android.lite.clean:id/rw"));
            if (titleDetailde.getText().equals("金币明细")) {
                mProcesser.waitAMonent(1);
                mDevice.pressBack();
            }
            Thread.sleep(1000);

        }
    }

    /**
     * 定时提现任务
     */

    public void timerTask(){




    }

    /**
     * 定时
     */
    static Date defaultdate;
    public static void showDayTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 8, 30, 00);//设置执行时间
        defaultdate = calendar.getTime();
        if (defaultdate.before(new Date())) {
            // 将发送时间设为明天
            calendar.add(Calendar.DATE, 1);
            defaultdate = calendar.getTime();
        }
        Timer dTimer = new Timer();
        dTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("当前执行时间" + defaultdate);
            }
        }, defaultdate, 24 * 60 * 60 * 1000);// 24* 60* 60 * 1000  24小时
    }
}
