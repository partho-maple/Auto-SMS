package edu.sust.autosms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<String> mDataset_name;
    private ArrayList<String> mDataset_number;
    static List<DatabaseSettingGetting> dbList;
    static Context context;
    SharedPreferences save_pref;


    RecyclerAdapter(Context context, List<DatabaseSettingGetting> dbList ){
        this.dbList = new ArrayList<DatabaseSettingGetting>();
        this.context = context;
        this.dbList = dbList;

    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public int currentItem;
        public TextView itemName;
        public TextView itemNumber;
        public TextView itemTags;
        public TextView itemAnswer;
        CardView cardView;

        public ViewHolder(final View itemView) {
            super(itemView);

            itemName = (TextView)itemView.findViewById(R.id.item_name);
            itemNumber = (TextView)itemView.findViewById(R.id.item_number);
            itemTags = (TextView)itemView.findViewById(R.id.item_tags);
            itemAnswer = (TextView)itemView.findViewById(R.id.item_answer);
            cardView = (CardView)itemView.findViewById(R.id.card_view);


            cardView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    int position = getAdapterPosition();

                    saveText(position);

                    Intent intent = new Intent(context, EditActivity.class);
                    intent.putExtra("name", dbList.get(position).getName());
                    intent.putExtra("number", dbList.get(position).getNumber());
                    intent.putExtra("tags", dbList.get(position).getTags());
                    intent.putExtra("answer", dbList.get(position).getAnswer());
                    intent.putExtra("update_position", Integer.toString(position));
                    ((AutoSMSActivity)context).startActivityForResult(intent, 2);

                    /*Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();*/

                }
            });
        }
    }



    //Creating cards
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //Information that is displayed on the card, position - number of the card or array cell to display
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.itemName.setText(dbList.get(position).getName());
        viewHolder.itemNumber.setText(dbList.get(position).getNumber());
        viewHolder.itemTags.setText(dbList.get(position).getTags());
        viewHolder.itemAnswer.setText(dbList.get(position).getAnswer());

    }

    //How many cards will be displayed, in this case the size of the array
    @Override
    public int getItemCount() {
        //return titles.length;
        return dbList.size();
    }

    public void saveText(int position){
        save_pref = context.getSharedPreferences("myApp",Context.MODE_PRIVATE);

        String name = dbList.get(position).getName();
        String number = dbList.get(position).getNumber();
        String tags = dbList.get(position).getTags();
        String answer = dbList.get(position).getAnswer();

        SharedPreferences.Editor ed = save_pref.edit();
        ed.putString("data_name", name);
        ed.putString("data_number", number);
        ed.putString("data_tags", tags);
        ed.putString("data_answer", answer);
        ed.commit();

        Log.d("NAME ",save_pref.getString("data_name",""));

    }
}
