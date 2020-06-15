
package com.swufe.sakura;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChinaActivity extends AppCompatActivity implements Runnable, View.OnClickListener{
    EditText time;
    EditText province;
    EditText nation;
    TextView showOut;
    Button btn;
    private final String TAG = "Sakura";
    Handler handler;
    private String Updatedate = "";
    private String content = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_china);
        time = (EditText) findViewById(R.id.inpTime);
        province = (EditText) findViewById(R.id.inpProvince);
        btn= (Button) findViewById(R.id.btn_1);
        nation = (EditText)findViewById(R.id.inpNation);
        showOut = (TextView)findViewById(R.id.showOut);
        showOut.setMovementMethod(LinkMovementMethod.getInstance());
        //获取SP里面的数据
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);//字符串，访问权限
        content = sharedPreferences.getString("keyword", "");
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate: sp keyword=" + content);
        Log.i(TAG, "onCreate: sp Updatedate=" + Updatedate);
        Log.i(TAG, "onCreate: sp updateDate=" + Updatedate);
        Log.i(TAG, "onCreate: todayStr=" + todayStr);
//判断时间
        if(!todayStr.equals(Updatedate)){
            Log.i(TAG, "onCreate: 需要更新");
            //开启子线程
            Thread t = new Thread(this);
            t.start();
        }else{
            Log.i(TAG, "onCreate: 不需要更新");
        }

        Thread t = new Thread(this);//要记得加当前对象,才能调用到Run方法,t就代表当前线程
        t.start();


        handler = new Handler() {
            public void handleMessage(Message msg) {
                //将子线程带回到主线程
                if (msg.what == 5) {//5是判断从哪个线程得到的数据
                    Bundle bdl = (Bundle) msg.obj;
                    content = bdl.getString("keyword");
                    // Log.i(TAG, "handleMessage: keyword=" + content);
                    //保存更新日期
                    SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);//要记得获取和写入的文件名要一样，都是myrate
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.putString("keyword", content);
                    editor.apply();
                    Toast.makeText(ChinaActivity.this, "数据已更新", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }


            //匿名类改写，相当于重新创建一个类 Handler就是拿到消息之后怎么处理

        };
    }


    @Override
    public void run() {
        Log.i(TAG, "run: run().....");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Bundle bundle = new Bundle();
        URL url = null;
        //将msg发送到队列里
        //2同步加引入包
        bundle = getChina();
        Message msg = handler.obtainMessage(5);//取出来一个消息队列
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
    /*
    从网页获取数据* */
    private Bundle getChina() {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            String url = "https://m.sinovision.net/newpneumonia.php";
            doc = Jsoup.connect(url).get();
            Elements tbs = doc.getElementsByClass("todaydata");
            Element table=tbs.get(6);
            //Log.i(TAG, "run: table6=" + table);
            Elements spans = table.getElementsByClass("prod");
            for (int i = 0; i <spans.size(); i++) {
                Element td1 = spans.get(i);
                String str1 = td1.text();
                Log.i(TAG, "run: text=" + str1);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }
   /* private Bundle getWorld()  {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            String url = "https://m.sinovision.net/newpneumonia.php";
            doc = Jsoup.connect(url).get();
            Elements tbs = doc.getElementsByClass("todaydata");
            for(Element table:tbs) {
                Elements spans = table.getElementsByClass("prod");
                for (int i = 0; i < spans.size(); i++) {
                    Element td1 = spans.get(i);
                    String str1 = td1.text();
                    Log.i(TAG, "run: text=" + str1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }*/
    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        String strtime = time.getText().toString();
        String strprovince = province.getText().toString();
        Log.i(TAG, "onClick:get str= " + strtime+strprovince);
        if (strtime.length() == 0 || strtime == null) {
            //提示用户输入信息
            Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show();
        } else if (strprovince.length() == 0 || strprovince == null) {
            //提示用户输入信息
            Toast.makeText(this, "请输入地区", Toast.LENGTH_SHORT).show();
        }
        if (btn.getId() == R.id.btn_1) {
            if (content != null && content.contains((CharSequence) strprovince)) {
                showOut.setText(content);
            } else {
                showOut.setText("无匹配数据");
            }

        }
    }
}



