package edu.sust.autosms;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

public class AutoSMSActivity extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public RecyclerView.Adapter adapter;
    List<DatabaseSettingGetting> dbList;
    CardView cardViewR;
    int position; //card position
    String name_cardview; //name in the card to be deleted
    String number_cardview; //number in the deleted card
    ImageView click_m;
    ImageView how_m;
    DatabaseHelpher helpher;
    //DBHelper dbHelper;
    ArrayList <String> DBtrueIDsForRececler;
    ImageView click_message;
    ImageView how_message;
    // Notification ID
    private static final int NOTIFY_ID = 101;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_sms);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        //Admob ads
        //AdMob app ID
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713"); //YOUR_ADMOB_APP_ID
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) !=
                    PackageManager.PERMISSION_GRANTED) {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECEIVE_SMS},1);
                // MY_PERMISSIONS_REQUEST is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        cardViewR = (CardView) findViewById(R.id.card_view);
        final ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.coordinator);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //myDataset = getDataSet();

        click_m = (ImageView) findViewById(R.id.click_message);
        how_m = (ImageView) findViewById(R.id.how_message);

        //create an object for creating and managing database versions
        helpher = new DatabaseHelpher(this);
        //Data from the database
        dbList= new ArrayList<DatabaseSettingGetting>();
        dbList = helpher.getDataFromDB();
        DBtrueIDsForRececler = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(this,dbList);
        recyclerView.setAdapter(adapter);

        helpher.add_to_list();

        //Disable click to start output after first pressing FloatButton
        SharedPreferences sp = getSharedPreferences("mySetting",
                Context.MODE_PRIVATE);
        // check whether the program opens for the first time
        boolean hasVisited = sp.getBoolean("hasVisited", false); //false is the initial parameter if it is empty when the variable is received
        if (hasVisited) {
            // display the desired activity
            SharedPreferences.Editor e = sp.edit();
            //e.putBoolean("hasVisited", true); //enter data into variable
            click_m.setVisibility(View.INVISIBLE);
            how_m.setVisibility(View.INVISIBLE);
            e.commit(); // do not forget to confirm the changes
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //myDataset.add("Hello");
                //adapter.notifyDataSetChanged(); //Updating and displaying new data (Adding a card)

                //Disable click to start output after first pressing FloatButton
                SharedPreferences sp = getSharedPreferences("mySetting",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor e = sp.edit();
                e.putBoolean("hasVisited", true); //enter data into variable
                e.commit(); // do not forget to confirm the changes

                click_m.setVisibility(View.INVISIBLE);
                how_m.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(AutoSMSActivity.this, EditActivity.class);
                startActivityForResult(intent,1);

                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });

        //Code below to remove the card with a swipe
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // callback for swipe to dismiss, removing item from data and adapter

                position = viewHolder.getAdapterPosition();//Card position

                //helpher.deleteARow(Integer.toString(position));
                //adapter.notifyItemRemoved(viewHolder.getAdapterPosition());


                helpher.showDB(); //show in console

                Snackbar.make(constraintLayout, getResources().getString(R.string.card_number)+" "+position+" "+getResources().getString(R.string.deleted),
                        Snackbar.LENGTH_SHORT)
                        .setAction(getResources().getString(R.string.cancel), snackbarOnClickListener)
                        .setActionTextColor(Color.WHITE)
                        .setDuration(2000)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                switch (event){
                                    case Snackbar.Callback.DISMISS_EVENT_TIMEOUT:
                                        Log.d("TIMEOUT","TIMEOUT");
                                        helpher.deleteARow(Integer.toString(position));
                                        updateRecyclerAdapter();
                                        adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .show();
            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(recyclerView);
    }


    //Button click handler on a snapshot
    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //return of a deleted card
            //myDataset_name.add(position,name_cardview);
            //myDataset_number.add(position,number_cardview);
            adapter.notifyDataSetChanged(); //Updating and displaying new data (Adding a card)
        }
    };

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

    @Override
    public void onResume(){
        super.onResume();
    }

    public void updateRecyclerAdapter(){ //update and display of new data in the list
        helpher = new DatabaseHelpher(this);
        //Data from the database
        dbList= new ArrayList<DatabaseSettingGetting>();
        dbList = helpher.getDataFromDB();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerAdapter(this,dbList);
        recyclerView.setAdapter(adapter);
    }

    public void Notifiction(Context context, String teg, String message){

        Intent notificationIntent = new Intent(context, AutoSMSActivity.class);
        PendingIntent contentIntent = PendingIntent.getBroadcast(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher) // Small icon
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher)) // Large icon
                .setWhen(System.currentTimeMillis())
                .setContentTitle(getResources().getString(R.string.sent_notif))  // Content title
                .setContentText(getResources().getString(R.string.tag)+" " + teg+" \n"+getResources().getString(R.string.message)+" "+ message); // Content text

        Notification notification = builder.getNotification();
        notification.ledARGB = Color.BLUE;
        notification.ledOffMS = 1000;
        notification.ledOnMS = 700;
        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, notification);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){ //Метод получения результата с данными с EditActivity
        if(data == null) {return;}
        //Get data from input field

        Log.d("requestCode: ", Integer.toString(requestCode));
        String name = data.getStringExtra("name");
        String number = data.getStringExtra("number");
        String tags = data.getStringExtra("tags");
        String answer = data.getStringExtra("answer");
        String update_position = data.getStringExtra("update_position");

        helpher = new DatabaseHelpher(AutoSMSActivity.this);
        if (requestCode==1) {
            helpher.insertIntoDB(name, number, tags, answer); //Data entry in the database
        }
        else {
            helpher.updateIntoDB(name, number, tags, answer, update_position); //Update data in the database
        }

        updateRecyclerAdapter(); //update

        adapter.notifyDataSetChanged(); //Updating and displaying new data (Adding a card)
    }
}
