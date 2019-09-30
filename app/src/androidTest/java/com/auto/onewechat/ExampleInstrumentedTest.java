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
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    public UiDevice mDevice;
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
        clear();
        mProcesser.pritLog("======      已清理 清理大师数据  = =======" + CLEAN_PKG_NAME);
        try {
            ReadTextFile read = new ReadTextFile();
            Thread.sleep(3 * 1000);
            String phone_number = read.read(SDCARD + "WeChat_Number.txt");
            cellPhoneNumber = getStringNoBlank(phone_number);
            if (cellPhoneNumber == "" || cellPhoneNumber == null) {
                mProcesser.pritLog("脚本执行结束  号码为空");
                return;
            }
            mProcesser.startApp();
            mProcesser.waitAMonent(10);
            goToOneStart();
            //goToUpdate();
            mProcesser.waitAMonent(2);
            //goToHomeInterface();
            mProcesser.waitAMonent(1);
            goToCloseRedPkg();
            mProcesser.waitAMonent(2);
            //while (maintask) {
            mainTasks();
            //}
        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.pritLog("错误");
        }
    }

    boolean maintask = true;

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
        }
        mProcesser.waitAMonent(1);
        //goToCloseRedPkg();
    }

    /**
     * 清理大师首次启动页 点击启动
     */

    public void goToOneStart() {
        mProcesser.pritLog("============== goToOneStart ===================");
        //android.lite.clean:id/oh    旧版本清理大师ID android.lite.clean:id/s4
        UiObject userOK = new UiObject(new UiSelector().className("android.widget.RelativeLayout")
                .resourceId("android.lite.clean:id/oh"));
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
            //android.lite.clean:id/qv    旧版本清理大师ID android.lite.clean:id/x3
            if (mProcesser.exitObjById("android.lite.clean:id/qv", 1)) {
                userOpenRedPkg = new UiObject(new UiSelector().className("android.widget.ImageView")
                        .resourceId("android.lite.clean:id/qv"));
                userOpenRedPkg.click();
                mProcesser.waitAMonent(1);
                goSignIn();
            }
        } catch (Exception e) {
            e.printStackTrace();
            mProcesser.waitAMonent(1);
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
                        mProcesser.waitAMonent(1);
                        if (mProcesser.exitObjById("android.lite.clean:id/x3", 1)) {
                            UiObject userOpenRedPkg = new UiObject(new UiSelector().className("android.widget.ImageView")
                                    .resourceId("android.lite.clean:id/x3"));
                            userOpenRedPkg.click();
                            mProcesser.waitAMonent(1);
                            goSignIn();
                        }
                        mProcesser.waitAMonent(1);
                        if (mDevice.hasObject(By.text("登录任务中心"))) {
                            mProcesser.waitAMonent(1);
                            goSignIn();
                            mProcesser.waitAMonent(1);
                            maintask = false;
                            mainTasks();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mProcesser.waitAMonent(1);
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
            if (mDevice.hasObject(By.text("使用手机号登录"))) {
                UiObject2 userSignin = mDevice.wait(Until.findObject(By.text("使用手机号登录")), 500);
                userSignin.click();
                mProcesser.waitAMonent(1);
            }
            mProcesser.pritLog("============== HttpGet()  初始化 请求数据 ===================");
            httpGet.getData();
            mProcesser.waitAMonent(2);
            //  android.lite.clean:id/a0q  旧版本 ->   android.lite.clean:id/wj
            UiObject userSignIns = new UiObject(new UiSelector().className("android.widget.EditText").resourceId("android.lite.clean:id/a0q"));

            //-----------------     取手机号   指定号码      ----
            cellPhoneNumber = httpGet.getCellNumberZJSJH(cellPhoneNumber);
            //-----------------------      等待短信验证码 ------------
            boolean result = false;
            int i = 0;
            sms = null;
            //------------------------输入手机号  android.lite.clean:id/a0q  旧版本ID android.lite.clean:id/wj
            //if (mProcesser.exitObjById("android.lite.clean:id/a0q", 1)) {
            mProcesser.pritLog(" =======   cellPhoneNumber : +++++++++++++++    " + cellPhoneNumber);
            userSignIns.legacySetText(cellPhoneNumber);
            UiObject2 next = mDevice.wait(Until.findObject(By.text("下一步")), 500);
            next.click();
            mProcesser.waitAMonent(2);
            //}
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

                mProcesser.pritLog("-----------  SMS  ---------------   " + sms);
                i++;
                if (i == 6) {
                    System.out.println("======   ==== result = false ======");
                    if (mDevice.hasObject(By.text("验证码超时"))) {
                        mDevice.wait(Until.findObject(By.text("确定")), 500).click();
                        i = 0;
                        if (i == 6) {
                            result = true;
                        }
                    } else {
                        result = true;
                    }

                }
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
        //android.lite.clean:id/a0s  android.widget.EditText
        // UiObject smstext1 = new UiObject(new UiSelector().className("android.widget.TextView").instance(2)); 旧版本
        UiObject smstext1 = new UiObject(new UiSelector().className("android.widget.EditText").resourceId("android.lite.clean:id/a0s"));
        smstext1.legacySetText(sms_number);
        sms = null;
        mProcesser.waitAMonent(2);
        httpGet.singOut();
        if (mDevice.hasObject(By.text("登录"))) {
            mDevice.wait(Until.findObject(By.text("登录")), 500).click();
        }
        mProcesser.waitAMonent(1);
        if (mDevice.hasObject(By.text("下一步"))) {
            mDevice.wait(Until.findObject(By.text("下一步")), 500).click();
        }
        //okGotIt();
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
        //==============       绑定微信并 定时 提现      ===================
        timerTask();
        mProcesser.pritLog("任务执行完毕================");
        mProcesser.waitAMonent(2);

    }

    /**
     * 刷新金币
     */
    public void detailed() throws Exception {
        boolean closeDeataild = false;
        //UiObject2 detailed;
        UiObject detailed, titleDeClose;
        //detailed1 = new UiObject(new UiSelector().className("android.widget.TextView").resourceId("android.lite.clean:id/zy"));

        //android.lite.clean:id/a2_   android.widget.LinearLayout  新版本
        detailed = new UiObject(new UiSelector().className("android.widget.LinearLayout").resourceId("android.lite.clean:id/a2_"));
        if (detailed != null && mDevice.hasObject(By.text("金币明细"))) {
            detailed.click();
            mProcesser.waitAMonent(1);
        }
        //  android.lite.clean:id/wl    android.widget.ImageView
        titleDeClose = new UiObject(new UiSelector().className("android.widget.ImageView").resourceId("android.lite.clean:id/wl"));
        if (titleDeClose != null && mDevice.hasObject(By.text("金币明细"))) {
            titleDeClose.click();
            mProcesser.waitAMonent(1);
        }

    }

    /**
     * 定时提现任务  15667117654
     */

    public void timerTask() throws Exception {
        Log.e(TAG, "================       提现任务模块      ======================");
        //android.lite.clean:id/h_   android.widget.ImageView
        int w = mDevice.getDisplayWidth();
        int h = mDevice.getDisplayHeight();
        UiObject touxiang, closeBack;
        //  //  android.lite.clean:id/wl    android.widget.ImageView
        //[216,117][912,198]

        Log.e(TAG, "timerTask: w: " + w + "  h  :" + h);
        mProcesser.waitAMonent(2);
        /*touxiang = new UiObject(new UiSelector().className("android.widget.ImageView").resourceId("android.lite.clean:id/wl"));
        if (touxiang != null) {
            Log.e(TAG, "timerTask:   点击头像");
            touxiang.click();
            mProcesser.waitAMonent(2);
        }else{
            Thread.sleep(3);
            Log.e(TAG, "timerTask:   点击头像");
            touxiang = new UiObject(new UiSelector().className("android.widget.ImageView").resourceId("android.lite.clean:id/wl"));
            touxiang.click();
            mProcesser.waitAMonent(2);
        }*/
        mDevice.click(80, 100);
        mProcesser.waitAMonent(3);
        UiObject2 weChat = mDevice.wait(Until.findObject(By.text("微信号")), 500);
        while (weChat == null) {
            mDevice.click(80, 100);
            mProcesser.waitAMonent(3);
            Thread.sleep(3);
            weChat = mDevice.wait(Until.findObject(By.text("微信号")), 500);
        }
        if (mDevice.hasObject(By.text("微信号")) && mDevice.hasObject(By.text("未绑定"))) {
            weChat.click();
            mProcesser.waitAMonent(3);
        } else {
            weChat = mDevice.wait(Until.findObject(By.text("微信号")), 500);
            weChat.click();
            mProcesser.waitAMonent(3);
            if (mDevice.hasObject(By.text("更多操作"))) {
                //解除已绑定的号
                UiObject2 jiechu = mDevice.wait(Until.findObject(By.text("解除绑定")), 500);
                jiechu.click();
                mProcesser.waitAMonent(1);
                if (mDevice.hasObject(By.text("确认解绑？"))) {
                    mDevice.wait(Until.findObject(By.text("确定")), 500).click();
                    mProcesser.waitAMonent(3);
                }
                weChat = mDevice.wait(Until.findObject(By.text("微信号")), 500);
                weChat.click();
                mProcesser.waitAMonent(3);
            }

            if (mDevice.hasObject(By.text("确定"))) {
                mDevice.wait(Until.findObject(By.text("确定")), 500).click();
                mProcesser.waitAMonent(3);
                weChat = mDevice.wait(Until.findObject(By.text("微信号")), 500);
                weChat.click();
                mProcesser.waitAMonent(3);
            }
        }
        mProcesser.waitAMonent(3);
        closeBack = new UiObject(new UiSelector().className("android.widget.ImageView").resourceId("android.lite.clean:id/wl"));
        //if ( mDevice.hasObject(By.text("账号资料"))) {
        //closeBack.click();
        mDevice.pressBack();
        mProcesser.waitAMonent(1);
        //}
        //--------------------  定时执行 提现任务

        showDayTime();
        while (timerStop) {
            Thread.sleep(180 * 1000);
            Log.e(TAG, "================     等待 8：30    ========================");
        }
    }

    Date defaultdate;
    static boolean timerStop = true;

    /**
     * 定时   每日 8：30执行
     */
    public void showDayTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(year, month, day, 8, 29, 40);//设置执行时间
        defaultdate = calendar.getTime();
        if (defaultdate.before(new Date())) {
            // 将发送时间设为明天
            calendar.add(Calendar.DATE, 1);
            defaultdate = calendar.getTime();
        }
        Float w = Float.valueOf(mDevice.getDisplayWidth()); // h获取屏幕宽度
        Float h = Float.valueOf(mDevice.getDisplayHeight());// h获取屏幕高度
        Timer dTimer = new Timer();
        dTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    //detailed();
                    System.out.println("当前执行时间" + defaultdate);
                    // android.lite.clean:id/a2a    旧版本  android.lite.clean:id/zw
                    UiObject jbNumber = new UiObject(new UiSelector().className("android.widget.TextView").resourceId("android.lite.clean:id/a2a"));
                    String jbNumberStr = jbNumber.getText();
                    Integer i = new Integer(jbNumberStr);  // 10
                    Log.e(TAG, "run:      jbNumberStr:" + jbNumberStr);
                    int jbNumberInt = i.intValue();
                    if (mDevice.hasObject(By.text("可提现"))) {
                        //点击提现
                        UiObject tixian = new UiObject(new UiSelector().className("android.widget.LinearLayout").resourceId("android.lite.clean:id/a28"));
                        if (tixian.exists()) {
                            tixian.click();
                        }
                    } else {
                        Log.e(TAG, "run: 当前不可提现  结束！！！！！");
                        return;
                    }
                    Log.e(TAG, "run:     ======  Thread.sleep(15*1000);=========== ");
                    Thread.sleep(15 * 1000);//等待页面刷新
                    Log.e(TAG, "run:     ======  Thread.sleep(15*1000);=========== ");
                    //  838  1145
                    if (jbNumberInt >= 100000) {
                        Log.e(TAG, "run:     =================  jbNumberInt >= 100000  ======== 0  ========   ===" + w + "     " + h + "  ----  " + mDevice.hasObject(By.text("10元")));
                        //830  970   大于十万金币的
                        //--------- 点击 10元
                        //input tap 540 660
                        String result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 825, h / 1920 * 970));
                        //String result =rootCmd.execRootCmd(String.format("input tap %f %f",555f, 660f));
                        Log.e(TAG, "run:     =================  jbNumberInt >= 100000  ======= 1=====  =======" + result + mDevice.hasObject(By.text("10元")));
                        //}
                        mProcesser.waitAMonent(1);
                        // -------  点击 立即提现
                        result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 830, h / 1920 * 1145));
                        //}
                        Log.e(TAG, "run:     =================  jbNumberInt >= 100000  ======== 2 ===========" + result + mDevice.hasObject(By.text("10元")));
                        mProcesser.waitAMonent(3);
                        //  ----------  点击  确定 提现
                        result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 530, h / 1920 * 1100));
                        Log.e(TAG, "run:     =================  jbNumberInt >= 100000  ======== 3 ===========" + result + mDevice.hasObject(By.text("10元")));
                        if (mDevice.hasObject(By.textContains("今日余额已被提空"))) {
                            result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 535, h / 1920 * 980));
                            Log.e(TAG, "run:     =================  今日余额已被提空  ===================" + result);
                            result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 830, h / 1920 * 1145));
                            Log.e(TAG, "run:     =================  今日余额已被提空  ===================" + result);
                            result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 530, h / 1920 * 1100));
                            Log.e(TAG, "run:     =================  今日余额已被提空  ===================" + result);
                        }else {
                            Log.e(TAG, "run:     =================  今日余额已被提空  =================== 未捕获到toast" );
                        }
                        //}
                    } else {
                        Log.e(TAG, "run:     =================   50000 < jbNumberInt < 100000  ===================");
                        //大于五万金币小于十万金币的
                        //--------- 点击 5元
                        String result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 535, h / 1920 * 980));
                        //}
                        // -------  点击 立即提现
                        mProcesser.waitAMonent(1);
                        result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 830, h / 1920 * 1145));
                        //}
                        mProcesser.waitAMonent(3);
                        // -------  点击 确定 提现
                        result = rootCmd.execRootCmd(String.format("input tap %f %f", w / 1080 * 530, h / 1920 * 1100));
                        //}
                    }
                    //}
                    Log.e(TAG, "run:     =================  提现结束  ===================" + mDevice.hasObject(By.text("10元")));
                    timerStop = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, defaultdate, 24 * 60 * 60 * 1000);// 24* 60* 60 * 1000  24小时
    }

}
