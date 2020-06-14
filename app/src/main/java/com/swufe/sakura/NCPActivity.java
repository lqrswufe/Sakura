package com.swufe.sakura;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NCPActivity extends AppCompatActivity implements Runnable{
    EditText time;
    EditText province;
    EditText nation;
    TextView showOut;
    private final String TAG = "Sakura";
    Handler handler;
    private String Updatedate = "";
    private String content = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ncp);
        time = (EditText) findViewById(R.id.inpTime);
        province = (EditText) findViewById(R.id.inpProvince);
        nation = (EditText)findViewById(R.id.inpNation);
        showOut = (TextView)findViewById(R.id.showOut);
        showOut.setMovementMethod(LinkMovementMethod.getInstance());
        //获取SP里面的数据
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);//字符串，访问权限
        content = sharedPreferences.getString("keyword", "");
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        int today = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);

        Log.i(TAG, "onCreate: sp keyword=" + content);
        Log.i(TAG, "onCreate: sp Updatedate=" + Updatedate);

        //判断时间

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
                    Toast.makeText(NCPActivity.this, "数据已更新", Toast.LENGTH_SHORT).show();
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
        bundle = getList();
        Message msg = handler.obtainMessage(5);//取出来一个消息队列
        msg.obj = bundle;
        handler.sendMessage(msg);
    }
    /*
    从网页获取数据* */


   /* private Bundle getChina() {
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
    }*/
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
    private Bundle getList()  {
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            String url = "https://search.dxy.cn/?recommend=false&dt=14-16-2&agent=Firefox-77.0&words=%E7%96%AB%E6%83%85&dw=%E5%88%A9%E5%B0%BF%E5%89%82%E5%BA%94%E7%94%A8%E8%B6%85%E5%BC%BA%E6%94%BB%E7%95%A5";
            doc = Jsoup.connect(url).get();
            Elements titles = doc.getElementsByTag("h3");
            for (Element e : titles) {
                String title =e.select("a").text(); // 新闻标题
                String address = e.select("a").attr("href");
                Log.i(TAG, "getList: text="+title+"==>"+address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    }

