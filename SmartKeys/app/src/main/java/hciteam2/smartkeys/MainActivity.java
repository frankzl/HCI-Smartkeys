package hciteam2.smartkeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import android.content.SharedPreferences;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Switch;
import android.widget.PopupWindow;

import android.content.Context;
import android.content.DialogInterface;

import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    final Context context = this;
    public Switch switch_mode;
    private boolean editing = true;
    private LinkedList<ButtonItem> coordinates;
    private TCPClient mTcpClient;

    int buttonWidth = 200;
    int buttonHeight = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if(editing) {
                        //coordinates.add(new ButtonItem((int) event.getX(), (int) event.getY(), 'c', MainActivity.this));
                        generateListView(event.getX(), event.getY());
                    }
                }
                return true;
            }
        });

        createKeysFromList(KeyboardLayout.getQWERTYList());


        Button btn_toggleEditing = (Button) findViewById(R.id.btn_toggleEditing);

        coordinates = new LinkedList<>();
        new connectTask().execute("");

        btn_toggleEditing.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.txt_mode);

                if(editing){
                    view.setText("Mode: Editing Off");
                }else{
                    view.setText("Mode: Editing On");
                }
                editing = !editing;
            }
        });
    }

    public ButtonItem createKey(float x, float y, String val){
        //new SelectKeyDialog().show(getFragmentManager(), "Test");
        //generateListView(x);

        final ButtonItem btn = new ButtonItem(x, y, val, this);
        btn.setOnTouchListener(new KeysOnTouchListener(mTcpClient, btn.information));
        return btn;
    }

    public ButtonItem createKey(ButtonInfo info){
        ButtonItem btn = new ButtonItem(info, this);
        return btn;
    }


    public void createKeys(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

        while(!coordinates.isEmpty()){
            Random rnd = new Random();

            final ButtonItem buttonItem = coordinates.remove();

            Button btn = new Button(this);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTcpClient != null) {
                        mTcpClient.sendMessage(buttonItem.getVal()+"");
                    }
                }
            });
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
            //layout.addView(btn, params);
            btn.setText("A");
            btn.setHeight(10);
            btn.setWidth(10);
            //btn.set
            btn.setX(buttonItem.getX());
            btn.setY(buttonItem.getY());
        }
    }

    public void createKeysFromList(List<ButtonInfo> infoList){
        System.out.println("creating my buttons"+infoList.size());
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

        for(int i = 0; i < infoList.size(); i++){
            ButtonInfo info = infoList.get(i);
            ButtonItem btn = createKey(info.getX(), info.getY(), info.getVal());
            /*final ButtonItem item = new ButtonItem(infoList.get(i), this);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTcpClient != null) {
                        mTcpClient.sendMessage(item.getVal()+"");
                    }
                }

            });*/
            layout.addView(btn);
        }
    }

    public void generateListView(final float x, final float y){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.list);

        GridView lv = (GridView ) dialog.findViewById(R.id.lv);

        // Defined Array values to show in ListView
        String list = "abcdefghijklmnopqrstuvwxyz";
        final String [] values = new String[list.length()];

        for(int i = 0; i < list.length(); i++){
            values[i] = ""+list.charAt(i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ButtonItem btn = createKey(x, y, values[position].charAt(0)+"");
                RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
                layout.addView(btn);
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        dialog.setTitle("ListView");
        dialog.show();
    }

    public boolean isEditing(){ return editing;}

    public TCPClient getTcpClient(){ return mTcpClient; }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

        }
    }


    //Menu-main
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);//Menu Resource, Menu

        SharedPreferences settings = getSharedPreferences("settings", 0);
        boolean isChecked = settings.getBoolean("checkbox", false);
        MenuItem item1 = menu.findItem(R.id.action_check);
        MenuItem item2 = menu.findItem(R.id.action_check2);
        item1.setChecked(isChecked);
        item2.setChecked(isChecked);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:

                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        //result.setText(userInput.getText());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

                //Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_check:
                item.setChecked(!item.isChecked());
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("checkbox", item.isChecked());
                editor.commit();

                if(item.isChecked()){
                    Toast.makeText(getApplicationContext(),"Edit mode is currently ON",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Edit mode is currently OFF",Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_check2:
                item.setChecked(!item.isChecked());
                SharedPreferences settings2 = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor2 = settings2.edit();
                editor2.putBoolean("checkbox", item.isChecked());
                editor2.commit();

                if(item.isChecked()){
                    Toast.makeText(getApplicationContext(),"Rearrange mode is currently ON",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Rearrange mode is currently OFF",Toast.LENGTH_LONG).show();
                }

                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Display popup attached to the button as a position anchor

}
