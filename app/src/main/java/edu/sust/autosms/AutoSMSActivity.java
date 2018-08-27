package edu.sust.autosms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

public class AutoSMSActivity extends AppCompatActivity {

    public RecyclerView.LayoutManager layoutManager;
    ImageView click_m;
    ImageView how_m;
    ImageView click_message;
    ImageView how_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_sms);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        click_m = (ImageView) findViewById(R.id.click_message);
        how_m = (ImageView) findViewById(R.id.how_message);

        SharedPreferences sp = getSharedPreferences("mySetting",
                Context.MODE_PRIVATE);

        boolean hasVisited = sp.getBoolean("hasVisited", false);
        if (hasVisited) {
            SharedPreferences.Editor e = sp.edit();
            click_m.setVisibility(View.INVISIBLE);
            how_m.setVisibility(View.INVISIBLE);
            e.commit();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sp = getSharedPreferences("mySetting",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("hasVisited", true);
                e.commit();

                click_m.setVisibility(View.INVISIBLE);
                how_m.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(AutoSMSActivity.this, EditActivity.class);
                startActivityForResult(intent,1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_auto_sm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {

            startActivity( new Intent(AutoSMSActivity.this, InfoActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
