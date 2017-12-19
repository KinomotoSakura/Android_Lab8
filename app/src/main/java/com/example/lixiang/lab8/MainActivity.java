package com.example.lixiang.lab8;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private myDB db = new myDB(this);
    private List<Map<String, String>> item;
    private SimpleAdapter simpleAdapter;
    private ListView listView;
    private Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_button = (Button)findViewById(R.id.add_item);
        listView = (ListView)findViewById(R.id.list);

        //查询
        item = db.queryArrayList();
        refreshList();

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdditemActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long idp) {
                final LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                View contentView = factory.inflate(R.layout.dialoglayout, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setView(contentView);

                final String this_name = item.get(position).get("name");
                String this_bd = item.get(position).get("birth");
                String this_gift = item.get(position).get("gift");
                TextView name_text = (TextView)contentView.findViewById(R.id.name_show);
                final EditText bd_edit = (EditText)contentView.findViewById(R.id.bd_edit);
                final EditText gift_edit = (EditText)contentView.findViewById(R.id.gift_edit);
                TextView tel_text = (TextView)contentView.findViewById(R.id.tel_text);
                TextView tel = (TextView)contentView.findViewById(R.id.tel);
                name_text.setText(this_name);
                bd_edit.setText(this_bd);
                gift_edit.setText(this_gift);

                String number = new String();
                //读取联系人
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                        null,null,null,null);
                while (cursor.moveToNext()){
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    if(!name.equals(this_name)) continue;
                    //该行联系人姓名与item姓名相同时
                    //先判断有没有电话号码
                    int isHas = Integer.parseInt(cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                    //若有电话号码根据该id查询
                    if(isHas>0){
                        Cursor c = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+id,
                                null, null);
                        //可能有多个电话号码，逐个遍历
                        while (c.moveToNext()){
                            number += c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))+" ";
                        }
                        c.close();
                        break;
                    }
                }
                if(!number.equals("")) tel_text.setText(number);
                else tel_text.setText("无");

                builder.setTitle("(๑•̀ㅂ•́)و✧")
                        .setPositiveButton("保存修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存修改
                                String new_bd = bd_edit.getText().toString();
                                String new_gift = gift_edit.getText().toString();
                                db.update(this_name, new_bd, new_gift);
                                refreshList();
                            }
                        })
                        .setNegativeButton("放弃修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long idp) {
                final String this_name = item.get(position).get("name");
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("删除条目")
                        .setMessage("删除条目 "+ this_name +"?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                db.delete(this_name);
                                refreshList();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return true;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        refreshList();
    }

    //更新listView
    private void refreshList(){
        item = db.queryArrayList();
        simpleAdapter = new SimpleAdapter(getApplicationContext(), item, R.layout.item,
                new String[]{"name","birth","gift"},
                new int[]{R.id.name_text, R.id.bd_text, R.id.gift_text});
        listView.setAdapter(simpleAdapter);
    }

}
