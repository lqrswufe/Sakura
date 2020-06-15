package com.swufe.sakura;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyListActivity  extends ListActivity implements Runnable, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
   private final String TAG = "Sakura";
    EditText input;
    Handler handler;
    int k =0;
    private ArrayList<HashMap<String, String>> listItems; // 存放文字、图片信息
    private SimpleAdapter listItemAdapter; // 适配器
    private int msgWhat = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    listItemAdapter = new SimpleAdapter(MyListActivity.this, listItems,// listItems数据源
                            R.layout.list_item,
                            new String[]{"ItemTitle", "ItemDetail"},
                            new int[]{R.id.itemTitle, R.id.itemDetail}
                    );
                    setListAdapter(listItemAdapter);
                }else if (listItems.size() == 0) {
                        Toast.makeText(MyListActivity.this, "无匹配结果", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, String.valueOf(listItems));
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
        SharedPreferences sharedPreferences = getSharedPreferences("myword", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("thread", "run.....");
        boolean marker = false;
        List<HashMap<String, String>> rateList = new ArrayList<HashMap<String, String>>();
        try {
            Document doc = Jsoup.connect("https://search.dxy.cn/?recommend=false&dt=14-16-2&agent=Firefox-77.0&words=%E7%96%AB%E6%83%85&dw=%E5%88%A9%E5%B0%BF%E5%89%82%E5%BA%94%E7%94%A8%E8%B6%85%E5%BC%BA%E6%94%BB%E7%95%A5").get();
            Elements tbs = doc.getElementsByClass("tableDataTable");
            Elements titles = doc.getElementsByTag("h3");
            for (Element e : titles) {
                SharedPreferences sp = getSharedPreferences("myword", Activity.MODE_PRIVATE);
                editor = sp.edit();
                String title = e.select("a").text(); // 新闻标题
                String address = e.select("a").attr("href");
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("ItemTitle", title);
                map.put("ItemDetail", address);
                rateList.add(map);
                Log.i(TAG, "getList: text=" + title + "==>" + address);
                editor.putString("ItemTitle", title);
                editor.putString("ItemDetail", address);
                editor.commit();
            }
            marker = true;
            contain();
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
    private void contain() {
        final SharedPreferences sp = getSharedPreferences("myword", Activity.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        //判断标题中是否包含关键词
        input = (EditText) findViewById(R.id.inpNews);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                listItems.clear();
                for (int i = 0; i <= k + 1; i++) {
                    String title = sp.getString("ItemTitle", "");
                    if (title.contains(s)) {
                        initListView();
                        Log.i("thread", "包含：" + title);
                    } else {
                        Log.i("thread", "不包含：");
                    }
                }
            }
        });
        Message msg = handler.obtainMessage(5);
        msg.obj = listItems;
        handler.sendMessage(msg);

        Log.i("thread", "sendMessage......");
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
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(detailStr));
        startActivity(web);
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










