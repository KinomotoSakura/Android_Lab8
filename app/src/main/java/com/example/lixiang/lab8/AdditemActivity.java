package com.example.lixiang.lab8;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdditemActivity extends AppCompatActivity {
    myDB db = new myDB(this);
    private Button add_button;
    private EditText name_edit, bd_edit, gift_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additem);

        add_button = (Button)findViewById(R.id.add_button);
        name_edit = (EditText)findViewById(R.id.name_edit);
        bd_edit = (EditText)findViewById(R.id.bd_edit);
        gift_edit = (EditText)findViewById(R.id.gift_edit);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = name_edit.getText().toString();
                String birth = bd_edit.getText().toString();
                String gift = gift_edit.getText().toString();

                int result = db.insert(name, birth, gift);

                if(result==0){
                    Intent intent = getIntent();
                    setResult(1, intent);
                    finish();
                } else if(result==1){
                    Toast.makeText(AdditemActivity.this, "名字为空，请完善", Toast.LENGTH_SHORT).show();
                }
                else if(result==2){
                    Toast.makeText(AdditemActivity.this, "名字重复啦，请检查", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
