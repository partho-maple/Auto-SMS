package edu.sust.autosms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;


public class IncomingSMS extends BroadcastReceiver {

    private static final String TABLE_DATA = "data";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "number";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_ANSWER = "answer";

    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        DatabaseHelpher databaseHelpher = new DatabaseHelpher(context);

        try {
            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    //String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    SharedPreferences sPref = context.getSharedPreferences("name", Context.MODE_PRIVATE);

                    //String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    String teg_1 = sPref.getString("teg1.text", "");
                    String[] teg_1_massiv = teg_1.split("\\s+|,\\s*");
                    AutoSMSActivity autoReplyActivity = new AutoSMSActivity();

                    Log.d("MESSAGE", message);

                    SQLiteDatabase db = databaseHelpher.getWritableDatabase();
                    // make a request for all data from the table mytable, we get the Cursor
                    Cursor c = db.query(TABLE_DATA, null, null, null, null, null, null);

                    //put the cursor position on the first row of the sample
                    //if there are no rows in the selection, false will return
                    int sent_message = 0;

                    if (c.moveToFirst()) {
                        //determine column numbers by name in the selection
                        int idColIndex = c.getColumnIndex(KEY_ID);
                        int nameColIndex = c.getColumnIndex(KEY_NAME);
                        int numberColIndex = c.getColumnIndex(KEY_NUMBER);
                        int tagsColIndex = c.getColumnIndex(KEY_TAGS);
                        int answerColIndex = c.getColumnIndex(KEY_ANSWER);

                        int id = c.getInt(idColIndex);
                        String name = c.getString(nameColIndex);
                        String number = c.getString(numberColIndex);
                        String tags = c.getString(tagsColIndex);
                        String answer = c.getString(answerColIndex);

                        String arr_tags[] = tags.split(",");
                        for (int j = 0; j<arr_tags.length; j++) {
                            arr_tags[j]=arr_tags[j].trim();
                            Log.d("AAAAA", arr_tags[j]);
                        }
                        do {

                            for(int j = 0; j<arr_tags.length; j++){
                                if(message.toLowerCase().contains(arr_tags[j].toLowerCase())&&!tags.equals("")&&sent_message==0){
                                    Log.d("OK", "OK");
                                    sendSMS(number, answer);
                                    Toast toast;
                                    toast = Toast.makeText(context,
                                            context.getResources().getString(R.string.sent)+" "+arr_tags[j], Toast.LENGTH_SHORT);
                                    toast.show();
                                    sent_message=1;
                                    autoReplyActivity.Notifiction(context, arr_tags[j], answer);
                                    break;
                                }
                                Log.d("JJJJ", Integer.toString(j));
                            }

                           /* // we get the values ​​by column numbers and write everything to the log
                            Log.d("QQQ",
                                    "ID = " + c.getInt(idColIndex) +
                                            ", name = " + c.getString(nameColIndex) +
                                            ", number = " + c.getString(numberColIndex)+
                                            ", tags = " + c.getString(tagsColIndex)+
                                            ", answer = " + c.getString(answerColIndex));
                            // go to next line
                            //and if there is no next (current - last), then false - exit the loop*/

                        } while (c.moveToNext());
                    } else
                        Log.d("QQQ", "0 rows");
                    c.close();

                 /*   for(int j = 0; j<teg_1_massiv.length; j++){
                        if(message.toLowerCase().contains(teg_1_massiv[j].toLowerCase())&&!teg_1.equals("")){
                            sendSMS(sPref.getString("phone1", ""), sPref.getString("reply1.text", ""));
                            Toast toast;
                            toast = Toast.makeText(context,
                                    R.string.sent1+"по ключевому слову: "+teg_1_massiv[j], Toast.LENGTH_SHORT);
                            toast.show();
                            break;
                        }
                    }*/


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
        ;


    }
    //Sends an SMS message to another device

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);

    }
}
