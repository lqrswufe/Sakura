package com.swufe.sakura;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldListActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
private final String TAG = "Sakura";

        Handler handler;
private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
private SimpleAdapter listItemAdapter; // 适配器
private int msgWhat = 7;
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();
        this.setListAdapter(listItemAdapter);
        Myadapter myAdapter = new Myadapter(this, R.layout.list_item, listItems);
        this.setListAdapter(myAdapter);
        //setContentView(R.layout.activity_my_list);
        Thread t = new Thread(this); // 创建新线程
        t.start(); // 开启线程
        handler = new Handler() {
@Override
public void handleMessage(Message msg) {
        if (msg.what == 7) {
        listItems = (ArrayList<HashMap<String, String>>) msg.obj;
        listItemAdapter = new SimpleAdapter(WorldListActivity.this, listItems,// listItems数据源
        R.layout.list_item,
        new String[]{"ItemTitle", "ItemDetail"},
        new int[]{R.id.itemTitle, R.id.itemDetail}
        );
        setListAdapter(listItemAdapter);
        }
        super.handleMessage(msg);
        }
        };

        getListView().setOnItemClickListener(this);//获得控件内容
        getListView().setOnItemLongClickListener(this);
        }

private void initListView() {
        listItems = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < 10; i++) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ItemTitle", "Rate： " + i); // 标题文字
        map.put("ItemDetail", "detail" + i); // 详情描述
        listItems.add(map);
        }
        // 生成适配器的Item和动态数组对应的元素
        listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
        R.layout.list_item, // ListItem的XML布局实现
        new String[]{"ItemTitle", "ItemDetail"},
        new int[]{R.id.itemTitle, R.id.itemDetail}
        );
        }

@Override
public void run() {
        Log.i("thread", "run.....");
        boolean marker = false;
        List<HashMap<String, String>> rateList = new ArrayList<HashMap<String, String>>();
        try {

            String url = "https://m.sinovision.net/newpneumonia.php";
           Document doc = Jsoup.connect(url).get();
            Elements tbs = doc.getElementsByClass("todaydata");
            Element table=tbs.get(2);
            //Log.i(TAG, "run: table6=" + table);
            Elements prod = table.getElementsByClass("prod-city-block prod-city-blockNY close");
            for(Element e: prod) {
                Elements spans = e.getElementsByTag("span");
                Element td1 = spans.get(0);
                Element td2 = td1.nextElementSibling();
                Element td3 = td2.nextElementSibling();
                String str1 = td1.text();
                String str2 = td2.text();
                String str3 = td3.text();
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", str1);
                map.put("ItemDetail", "确诊人数："+str2+"  死亡人数："+str3);
                rateList.add(map);

        }
        marker = true;
        } catch (MalformedURLException e) {
        Log.e("www", e.toString());
        e.printStackTrace();
        } catch (IOException e) {
        Log.e("www", e.toString());
        e.printStackTrace();
        }
        Message msg = handler.obtainMessage();
        msg.what = msgWhat;
        if (marker) {
        msg.arg1 = 1;
        } else {
        msg.arg1 = 0;
        }
        msg.obj = rateList;
        handler.sendMessage(msg);
        Log.i("thread", "sendMessage.....");
        }

@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i(TAG, "onItemClick: parent="+parent);
        Log.i(TAG, "onItemClick: view="+view);
        Log.i(TAG, "onItemClick: position="+position);
        Log.i(TAG, "onItemClick: id="+id);
        HashMap<String, String> map = (HashMap<String, String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        Log.i(TAG, "onItemClick: titleStr="+titleStr);
        Log.i(TAG, "onItemClick: detailStr="+detailStr);
        TextView title = (TextView)view.findViewById(R.id.itemTitle);
        TextView detail = (TextView)view.findViewById(R.id.itemDetail);
        String title2=String.valueOf(title.getText());
        String detail2=String.valueOf(detail.getText());
        Log.i(TAG, "onItemClick: title2="+title2);
        Log.i(TAG, "onItemClick: detail2="+detail2);
        //打开新的页面

        }
@Override
public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        Log.i(TAG, "onItemLongClick: 长按列表项position="+position);
        //删除操作,在ArrayAdaoter里面有remove方法,其他的adapter就是先删除再刷新
        //listItems.remove(position);
        //listItemAdapter.notifyDataSetChanged();//数据发生了改变，是来自与list2的
//构造对话框进行操作AlertFialog
        AlertDialog.Builder bulider = new AlertDialog.Builder(this);
        bulider.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是", new DialogInterface.OnClickListener() {
@Override
public void onClick(DialogInterface dialog, int which) {
        Log.i(TAG, "onClick: 对话框事件处理");
        listItems.remove(position);
        listItemAdapter.notifyDataSetChanged();//数据发生了改变，是来自与list2的
        }
        })
        .setNegativeButton("否",null);
        bulider.create().show();
        return true;//短按事件依旧生效

        }
        }








