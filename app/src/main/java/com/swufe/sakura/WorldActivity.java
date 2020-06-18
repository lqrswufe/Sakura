
package com.swufe.sakura;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorldActivity extends AppCompatActivity implements Runnable,  AdapterView.OnItemClickListener,View.OnClickListener{
    EditText time;
    EditText inpprovince;
    Button btn;
    TextView t1,t2,t3,t4;
    String resprovince,resconfirm,resdead,rescured;
    Map<String, String> map = new HashMap<String, String>();
    private final String TAG = "Sakura";
    Handler handler;
    private String Updatedate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_world);
        final ListView listview = (ListView) findViewById(R.id.world_list);
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
        time = (EditText) findViewById(R.id.inpTime);
        t1= (TextView) findViewById(R.id.result_area);
        t2= (TextView) findViewById(R.id.result_confirm);
        t3= (TextView) findViewById(R.id.result_dead);
        t4= (TextView) findViewById(R.id.result_cured);
        inpprovince = (EditText) findViewById(R.id.inpArea);
        btn= (Button) findViewById(R.id.btn_2);
        btn.setOnClickListener(this);
        t4.setText("无数据");

        //获取SP里面的数据
        Updatedate = sharedPreferences.getString("update_date", "");
        //获取当前系统时间
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String todayStr = sdf.format(today);
        Log.i(TAG, "onCreate: sp keyword=" + resprovince);
        Log.i(TAG, "onCreate: sp keyword=" + resconfirm);
        Log.i(TAG, "onCreate: sp keyword=" + rescured);
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
                    List<String> List2 = (List<String>) msg.obj;
                    if (List2.size() == 0) {
                        Toast.makeText(WorldActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, String.valueOf(List2));
                    } else {
                        Log.i(TAG, String.valueOf(List2));
                    }
                    ListAdapter adapter = new ArrayAdapter<String>(WorldActivity.this, android.R.layout.simple_list_item_1, List2);
                    listview.setAdapter(adapter);
                    //用SP保存时间
                    SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("update_date", todayStr);
                    editor.apply();
                }
                super.handleMessage(msg);
            }


            //匿名类改写，相当于重新创建一个类 Handler就是拿到消息之后怎么处理

        };
        listview.setOnItemClickListener((AdapterView.OnItemClickListener) this);

    }

    @Override
    public void run() {
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i(TAG, "run: run().....");
        try {
            Document doc = Jsoup.connect( "https://m.sinovision.net/newpneumonia.php").get();
            Elements tbs = doc.getElementsByClass("todaydata");
            Element table=tbs.get(2);
            //Log.i(TAG, "run: table6=" + table);
            Elements prod = table.getElementsByClass("prod-city-block prod-city-blockNY close");
            int i=0;
            for(Element e: prod) {
                Elements spans = e.getElementsByTag("span");
                Element td1 = spans.get(0);
                Element td2 = td1.nextElementSibling();
                Element td3 = td2.nextElementSibling();
                Element td4 = td3.nextElementSibling();
                String str1 = td1.text();
                String str2 = td2.text();
                String str3 = td3.text();
                String tt = str1.concat("确诊人数：").concat(str2).concat("死亡人数：").concat(str3);
                Log.i(TAG, "run: "+tt);
                editor.putString(String.valueOf(i), tt);
                editor.commit();
                i++;
            }
            query();
           /* int j =1;
              for (Element e : prod) {
                  Elements spans = e.getElementsByTag("span");
                  Element td1 = spans.get(0);
                  Element td2 = td1.nextElementSibling();
                  String str2 = td2.text();
                  Log.i(TAG, "run: ["+j+"]"+str2);
                  editor.putString(String.valueOf(j), str2);
                  editor.commit();
                  j += 4;
              }
              int m =2;
                  for (Element e : prod) {
                      Elements spans = e.getElementsByTag("span");
                      Element td1 = spans.get(0);
                      Element td2 = td1.nextElementSibling();
                      Element td3 = td2.nextElementSibling();
                      String str3 = td3.text();
                      editor.putString(String.valueOf(m), str3);
                      Log.i(TAG, "run: ["+m+"]" +str3);
                      editor.commit();
                      m+= 4;
                  }
                  int n=3;
                      for (Element e : prod) {
                          Elements spans = e.getElementsByTag("span");
                          Element td1 = spans.get(0);
                          Element td2 = td1.nextElementSibling();
                      Element td3 = td2.nextElementSibling();
                      Element td4 = td3.nextElementSibling();
                      String str4 = td4.text();
                          Log.i(TAG, "run: ["+n+"] "+str4);
                    editor.putString(String.valueOf(n), str4);
                          editor.commit();
                 n+=4;
            }*/
        } catch (MalformedURLException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("www", e.toString());
            e.printStackTrace();
        }

    }
    private void query() {
        final SharedPreferences sp = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        final List<String> searchList = new ArrayList<String>();
        //判断标题中是否包含关键词
        inpprovince = (EditText) findViewById(R.id.inpArea);
        inpprovince.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchList.clear();
                for (int i = 0; i <=50; i++) {
                    String title = sp.getString(String.valueOf(i), "");
                    if (title.contains(s)) {
                        searchList.add(title);
                        Log.i("thread", "包含：" + title);
                    } else {
                        Log.i("thread", "不包含：");
                        Toast.makeText(WorldActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        Message msg = handler.obtainMessage(5);
        msg.obj = searchList;
        handler.sendMessage(msg);
    }
    /*
    从网页获取数据* */


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
        final SharedPreferences sp = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        if (v.getId() == R.id.btn_2) {
            Log.i(TAG, "onClick: ");
            String strtime = time.getText().toString();
            String strprovince = inpprovince.getText().toString();
            Log.i(TAG, "onClick:get str= " + strtime + strprovince);
            if ((strtime.length() == 0) || (strtime == null)) {
                //提示用户输入信息
                Toast.makeText(this, "请输入时间", Toast.LENGTH_SHORT).show();
            } else if (strprovince.length() == 0 || strprovince == null) {
                //提示用户输入信息
                Toast.makeText(this, "请输入地区", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick:parents = " + parent);
        Log.i(TAG, "onItemClick:view = " + view);
        Log.i(TAG, "onItemClick:position = " + position);
        Log.i(TAG, "onItemClick:id = " + id);
        SharedPreferences sharedPreferences = getSharedPreferences("mywords", Activity.MODE_PRIVATE);
        String Position = String.valueOf(position);
        String Position1 = String.valueOf(position+1);
        String Position2= String.valueOf(position+2);
        String Position3= String.valueOf(position+3);
        String povince = sharedPreferences.getString(Position, "");
        String confirm = sharedPreferences.getString(Position1, "");
        String dead = sharedPreferences.getString(Position2, "");
        String cured = sharedPreferences.getString(Position3, "");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.world,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_set) {
            Intent intent = new Intent(this, WorldListActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}



