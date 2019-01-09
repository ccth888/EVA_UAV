package com.example.admin.myapplication;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.UUID;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.admin.myapplication.RockerView.OnShakeListener;
import com.example.admin.myapplication.RockerView.Direction;
import com.example.admin.myapplication.RockerView.DirectionMode;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;//定义MediaPlayer
    public String info;//记录需要触发语音的类型
//    private boolean run = false;


    //    public  OutputStream out;//定义输出流
    public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//定义UUID
    //    private final Handler handler = new Handler();
//    private int a=1,b=1;
    private TextView tipe;
    private TextView qh;
    private TextView zy;
    private TextView fg;
    private TextView hx;
    private TextView fy;
    //public String address="00:0E:0E:15:85:04";//蓝牙mac地址
    public byte[] data = new byte[34];//定义通信数组
    private VerticalSeekBar verticalSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verticalSeekBar = findViewById(R.id.verticalSeekBar);
        qh = findViewById(R.id.txtqian);
        tipe = findViewById(R.id.textView);
        zy = findViewById(R.id.txtzy);
        fg =findViewById(R.id.textView6);
        fy =findViewById(R.id.textView5);
        hx =findViewById(R.id.textView7);
        Bluetooth_Conn.tip = findViewById(R.id.txtqian);
        initrokerview();
       SharedPreferences sp=getSharedPreferences("Aircraft_data",MODE_PRIVATE);
        Initialization.initial.course= sp.getInt("course",1500);
        Initialization.initial.pitch= sp.getInt("pitch",1500);
        Initialization.initial.Rollover= sp.getInt("Rollover",1500);


        fy.setText("俯仰:"+Initialization.initial.pitch);
        fg.setText("翻滚:"+Initialization.initial.Rollover);
        hx.setText("翻滚:"+Initialization.initial.course);
        //run = true;
        //handler.postDelayed(task, 3000);
        verticalSeekBar.setOnSeekBarChangeListener(new VerticalSeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(VerticalSeekBar VerticalBar, int progress, boolean fromUser) {
                tipe.setText("油门：" + progress);
                Initialization.initial.speed = progress;
            }

            @Override
            public void onStartTrackingTouch(VerticalSeekBar VerticalBar) {

            }

            @Override
            public void onStopTrackingTouch(VerticalSeekBar VerticalBar) {

            }
        });
    }

    //释放播放资源
    private void ReleasePlayer() {
        if (mp != null && mp.isPlaying()) {
            mp.stop();
            //关键语句
            mp.reset();
            mp.release();
            mp = null;
        }
    }

    //音频预备以及播放
    public void init() {
        ReleasePlayer();
        switch (info) {
            case "背景":
                mp = MediaPlayer.create(this, R.raw.dy);
                break;
            case "前":
                mp = MediaPlayer.create(this, R.raw.qian);//前飞
                break;
            case "后":
                mp = MediaPlayer.create(this, R.raw.hou);//后飞
                break;
            case "左":
                mp = MediaPlayer.create(this, R.raw.zuo);//左飞
                break;
            case "右":
                mp = MediaPlayer.create(this, R.raw.you);//右飞
                break;
            case "连":
                mp = MediaPlayer.create(this, R.raw.lian);//没连上
                break;
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!mp.isPlaying()) {
            mp.start();
        }

    }


    public void initrokerview() {
        //找到RockerView控件
        RockerView roker = (RockerView) findViewById(R.id.RockerView);
        //实时监测摇动方向
        roker.setOnShakeListener(DirectionMode.DIRECTION_8, new OnShakeListener() {
            //开始摇动时要执行的代码写在本方法里
            @Override
            public void onStart() {

            }

            //结束摇动时要执行的代码写在本方法里
            @Override
            public void onFinish() {
                info = null;
                //Toast.makeText(MainActivity.this, "已复位", Toast.LENGTH_SHORT).show();
            }

            //摇动方向时要执行的代码写在本方法里
            @Override
            public void direction(Direction direction) {
                if (direction == RockerView.Direction.DIRECTION_CENTER) {
                    Initialization.initial.pitch = 1500;
                    Initialization.initial.course = 1500;
                    Initialization.initial.Rollover = 1500;
                    tipe.setText("中心");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN) {

                    Initialization.initial.pitch = Initialization.Aircraft_data.pitch_Small;//向后
                    tipe.setText("" + Initialization.initial.pitch);
                    if (info != "后") {
                        //info="后";
                        //init();
                        // tipe.setText("后");
                    }
                } else if (direction == RockerView.Direction.DIRECTION_LEFT) {
                    // tipe.setText("左");
                    Initialization.initial.Rollover = Initialization.Aircraft_data.Rollover_Big;//向左
                    tipe.setText("" + Initialization.initial.Rollover);

                    if (info != "左") {
                        //info="左";
                        //init();

                        //tipe.setText(Initialization.initial.Rollover);
                    }
                } else if (direction == RockerView.Direction.DIRECTION_UP) {
                    //tipe.setText("上");
                    RockerView.DirectionMode.values();
                    Initialization.initial.pitch = Initialization.Aircraft_data.pitch_Big;//向前
                    tipe.setText("" + Initialization.initial.pitch);
                    if (info != "前") {
//                        info="前";
//                        init();
                    }

                } else if (direction == RockerView.Direction.DIRECTION_RIGHT) {
                    //tipe.setText("右");
                    Initialization.initial.Rollover = Initialization.Aircraft_data.Rollover_Small;//向右
                    tipe.setText("" + Initialization.initial.Rollover);
                    if (info != "右") {
//                        info="右";
//                        init();
                    }
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_LEFT) {
                    //tv.setText("左下");
                } else if (direction == RockerView.Direction.DIRECTION_DOWN_RIGHT) {
                    //tv.setText("右下");
                } else if (direction == RockerView.Direction.DIRECTION_UP_LEFT) {
                    //tv.setText("左上");
                } else if (direction == RockerView.Direction.DIRECTION_UP_RIGHT) {
                    //tv.setText("右上");
                }

            }
        });
    }
//    public void sxTouch(View view)
//    {
//        ImageButton sxbtn= findViewById(R.id.lximgbtn);
//        if (b==1)
//        {
//            b++;
//            sxbtn.setBackgroundResource(R.drawable.shexiang2);
//        }
//        else
//        {
//            b--;
//            sxbtn.setBackgroundResource(R.drawable.shexiang);
//        }
//    }


    public boolean flag = false;

    /**
     * 点击按钮启动线程
     *
     * @param view
     */
    public void btn_qd(View view) {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
        if (!blueadapter.isEnabled()) {
            blueadapter.enable();
            Toast.makeText(getApplicationContext(), "蓝牙状态：未开启", Toast.LENGTH_SHORT).show();
        } else {
            flag = true;
            findViewById(R.id.imgbtn_ly).setBackgroundResource(R.drawable.ly_on);
            new Thread(new Bluetooth_Conn.ConnectThread()).start();
            Toast.makeText(getApplicationContext(), "开始连接", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 点击按钮发送数据
     * @param view
     */
    public void  btn_send(View view){
        Toast.makeText(getApplicationContext(), "开始发送数据！", Toast.LENGTH_SHORT).show();
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        findViewById(R.id.imageButton).setBackgroundResource(R.drawable.qd_on);
            Thread t = new Thread(new SendThread());
            t.start();
    }
    public static class SendThread implements Runnable{
        @Override
        public void run() {
            try{
                while(Initialization.lock_bool){
                    //速度 and 航向 and 横滚 and 俯仰
                    System.out.println("油门"+Initialization.initial.speed);
                    char[] data=DataManage.Behavior((char) Initialization.initial.speed,(char)Initialization.initial.course,(char)Initialization.initial.Rollover,(char)Initialization.initial.pitch);
                    byte[] byteData = DataManage.charToByteArray(data);
                    Initialization.out.write(byteData);//发送通信数组给飞机
                    try {
                        Thread.sleep(5);//5毫秒发送一次数据
                    } catch (Exception e) {
                        break;
                    }
                }
            }
            catch (Exception e) {

            }
        }
    }

    public void btn_lock(View view) {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
            if (Initialization.lock_bool) {
                ImageButton imageButton_lock = findViewById(R.id.imgbtn_lock);
                imageButton_lock.setBackgroundResource(R.drawable.lock_on);
                Initialization.initial.speed=0;
                tipe.setText("油门:0");
                Initialization.lock_bool=false;
            } else {
                ImageButton imageButton_lock = findViewById(R.id.imgbtn_lock);
                imageButton_lock.setBackgroundResource(R.drawable.lock_off);
                Initialization.lock_bool=true;
                Thread t = new Thread(new SendThread());
                t.start();
            }
    }

    public void btn_fx_z(View view)
    {
        if (Initialization.initial.Rollover!=3000)
        {
            Initialization.initial.Rollover+=10;
            zy.setText("左:"+Initialization.initial.Rollover);
        }
    }
    public void btn_fx_y(View view)
    {
        if (Initialization.initial.Rollover!=0)
        {
            Initialization.initial.Rollover-=10;
            zy.setText("右:"+Initialization.initial.Rollover);
        }
    }
    public void btn_fx_q(View view)
    {
        if (Initialization.initial.pitch!=3000)
        {
            Initialization.initial.pitch+=10;
            qh.setText("前:"+Initialization.initial.pitch);
        }
    }
    public void btn_fx_h(View view)
    {
        if (Initialization.initial.pitch!=0)
        {
            Initialization.initial.pitch-=10;
            qh.setText("后:"+Initialization.initial.pitch);
        }
    }
    public void btn_fx_zx(View view)
    {
        if (Initialization.initial.course!=3000)
        {
            Initialization.initial.course-=10;
        }
    }
    public void btn_fx_yx(View view)
    {
        if (Initialization.initial.course!=0)
        {
            Initialization.initial.course+=10;
        }
    }
    public void btn_info_bc(View view)
    {
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        SharedPreferences sp=getSharedPreferences("Aircraft_data",MODE_PRIVATE);
        SharedPreferences.Editor spe=sp.edit();
        spe.putInt("course",Initialization.initial.course);
        spe.putInt("pitch",Initialization.initial.pitch);
        spe.putInt("Rollover",Initialization.initial.Rollover);
        spe.commit();
        fy.setText("俯仰:"+Initialization.initial.pitch);
        fg.setText("翻滚:"+Initialization.initial.Rollover);
        hx.setText("翻滚:"+Initialization.initial.course);
        Toast.makeText(getApplicationContext(), "保存成功！", Toast.LENGTH_SHORT).show();
    }
    /**
     * 设置弹框修改连接蓝牙MAC地址
     * @param view
     */
    public void alert_edit(View view){
        final EditText et = new EditText(this);
        et.setText("00:0E:0E:15:85:04");
        new AlertDialog.Builder(this).setTitle("蓝牙MAC地址更改:")
                .setIcon(R.drawable.sz2)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件
                        String telReg="([a-fA-F0-9]{2}:){5}[a-fA-F0-9]{2}";
                        if (!et.getText().toString().equals("")&&et.getText().toString().matches(telReg))
                        {
                            Initialization.address=et.getText().toString();
                            Toast.makeText(getApplicationContext(),"蓝牙地址更改为:"+et.getText().toString(),Toast.LENGTH_SHORT).show();
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"提示:蓝牙MAC地址格式错误！",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("取消",null).show();
    }


}