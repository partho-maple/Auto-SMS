package edu.sust.autosms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    Button save_btn;
    EditText enter_name;
    EditText enter_number;
    EditText enter_tags;
    EditText enter_answer;

    SharedPreferences load_pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        save_btn = (Button) findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);

        enter_name = (EditText) findViewById(R.id.enter_name_edit);
        enter_number = (EditText) findViewById(R.id.enter_number_edit);
        enter_tags = (EditText) findViewById(R.id.enter_tags_edit);
        enter_answer = (EditText) findViewById(R.id.enter_answer_edit);

        load_text();

    }

    @Override
    public void onClick(View v) {

        String name = enter_name.getText().toString();
        String number = enter_number.getText().toString();
        String tags = enter_tags.getText().toString();
        String answer = enter_answer.getText().toString();

        if(v.getId()==R.id.save_btn){
            if(name.equals("") || number.equals("") || tags.equals("") || answer.equals("")){
                Toast.makeText(EditActivity.this,R.string.fill_fields,Toast.LENGTH_LONG).show();
            }else {
                Intent intent = new Intent();
                intent.putExtra("name", name);
                intent.putExtra("number", number);
                intent.putExtra("tags", tags);
                intent.putExtra("answer", answer);

                try {
                intent.putExtra("update_position", getIntent().getExtras().getString("update_position")); //для обновления данных
                }
                catch (Exception main) {}

                setResult(RESULT_OK, intent);
                EditActivity.this.finish();
            }
        }
    }

    void load_text(){
        try {
            String name = getIntent().getExtras().getString("name");
            String number = getIntent().getExtras().getString("number");
            String tags = getIntent().getExtras().getString("tags");
            String answer = getIntent().getExtras().getString("answer");
            enter_name.setText(name);
            enter_number.setText(number);
            enter_tags.setText(tags);
            enter_answer.setText(answer);
        } catch (Exception main) {}
    }
}
