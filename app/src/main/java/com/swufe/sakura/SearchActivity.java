package com.swufe.sakura;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {

    //用变量去存储，方便后续修改；
    private final String TAG = "Sakura";
    private String text = "";
    private String Updatedate = "";
    EditText input;
    Handler handler;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        final ListView listview = (ListView) findViewById(R.id.search_list);
        SharedPreferences sharedPreferences = getSharedPreferences("myword", Activity.MODE_PRIVATE);
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
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
                if (msg.what == 5) {
                    List<String> List2 = (List<String>) msg.obj;
                    if (List2.size() == 0) {
                        Toast.makeText(SearchActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, String.valueOf(List2));
                    } else {
                        Log.i(TAG, String.valueOf(List2));
                    }
                    ListAdapter adapter = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, List2);
                    listview.setAdapter(adapter);
                    //用SP保存时间
                    SharedPreferences sharedPreferences = getSharedPreferences("myword", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.apply();
                }
                super.handleMessage(msg);
            }
        };
        listview.setOnItemClickListener((AdapterView.OnItemClickListener) this);
    }


    @Override
    public void run() {
        //获取网络数据，放入List带回主线程
        Log.i("thread", "run......");
//        final List<String> rateList = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences("myword", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("thread", "run.....");
        try {
            Document doc = Jsoup.connect("https://search.dxy.cn/?recommend=false&dt=14-16-2&agent=Firefox-77.0&words=%E7%96%AB%E6%83%85&dw=%E5%88%A9%E5%B0%BF%E5%89%82%E5%BA%94%E7%94%A8%E8%B6%85%E5%BC%BA%E6%94%BB%E7%95%A5").get();
            Elements tbs = doc.getElementsByClass("wrap");
            Elements titles = doc.getElementsByTag("h3");
            int i =0;
                for (Element e : titles) {
                    String title = e.select("a").text();
                    Log.i(TAG, "run: title[" + i + "]" + title);
                    editor.putString(String.valueOf(i), title);
                    editor.commit();
                    i+=2;

                }
           int j =1;
            for (Element e : titles) {
                String address = e.select("a").attr("href");
                editor.putString(String.valueOf(j), address);
                Log.i(TAG, "run: url["+j+"]" + address);
                editor.commit();
                j+=2;
            }
            query();

        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }

    }
    private void query() {
        final SharedPreferences sp = getSharedPreferences("myword", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        final List<String> searchList = new ArrayList<String>();
        //判断标题中是否包含关键词
        input = (EditText) findViewById(R.id.search_keyword);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchList.clear();
                for (int i = 0; i <=30; i++) {
                    String title = sp.getString(String.valueOf(i), "");
                    if (title.contains(s)) {
                        searchList.add(title);
                        Log.i("thread", "包含：" + title);
                    }
                }
            }
        });
        Message msg = handler.obtainMessage(5);
        msg.obj = searchList;
        handler.sendMessage(msg);
        }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick:parents = " + parent);
        Log.i(TAG, "onItemClick:view = " + view);
        Log.i(TAG, "onItemClick:position = " + position);
        Log.i(TAG, "onItemClick:id = " + id);
        SharedPreferences sharedPreferences = getSharedPreferences("myword", Activity.MODE_PRIVATE);
            String Position = String.valueOf(position+1);
            String address = sharedPreferences.getString(Position, "");
        Log.i("TAG", "run=" + address);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
       startActivity(intent);
    }

    }









