package hciteam2.smartkeys;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
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
import android.widget.TableLayout;
import android.widget.TextView;
import android.os.AsyncTask;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.security.Key;
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
    private boolean editing = false;
    private boolean rearranging = false;
    private boolean scaling = false;
    private boolean deleting = false;

    private int currentMaxID = 0;

    private LinkedList<ButtonItem> coordinates;
    private TCPClient mTcpClient;
    connectTask serverTask;

    int buttonWidth = 200;
    int buttonHeight = 200;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;

    ArrayList<KeyPage> keyPages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        //viewPager.setPagingEnabled(false);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        loadConfiguration();
        addPage("Basic", createPage(0));
        viewPager.setAdapter(adapter);
    }

    private void saveConfiguration(){
        String data = ""+currentMaxID;
        for(int i = 0; i < keyPages.size(); i++){
            data += ";;"+keyPages.get(i).getIndex()+"__"+adapter.getPageTitle(i);
        }
        System.out.println(data);
        FileWriter.writeToFile(data, this, "mainConfig");
    }

    private void loadConfiguration(){
        String data = FileWriter.readFromFile(this, "mainConfig");
        String [] set = data.split(";;");
        if(data.length() > 0)
            currentMaxID = Integer.parseInt(set[0]);
        for(int i = 1; i < set.length; i++){
            String [] obj = set[i].split("__");
            addPage(obj[1], createPage(Integer.parseInt(obj[0])));
        }
    }

    private void removeCurrentPage(){
        adapter.removeCurrentPage();
        saveConfiguration();
        viewPager.setAdapter(adapter);
    }

    private void renameCurrentPage(){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.remenu, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.renamePage);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                adapter.renameCurrentPage(userInput.getText().toString());
                                saveConfiguration();
                                viewPager.setAdapter(adapter);
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
    }

    private void addPage(String title, KeyPage page){
        adapter.addFragment(page, title);
        keyPages.add(page);
        viewPager.setAdapter(adapter);
        saveConfiguration();
    }

    private KeyPage createPage(int index){
        Bundle bundle = new Bundle();
        bundle.putString("index", index+"");
        //String text = "h"+index;
        KeyPage p = new KeyPage();
        bundle.putParcelableArrayList("keylist", KeyboardLayout.getQWERTYList());
        p.setArguments(bundle);

        if(currentMaxID < index) currentMaxID = index;

        return p;
    }

    protected void onStop(){
        super.onStop();
        for(int i=0; i < keyPages.size(); i++)
            keyPages.get(i).saveKeyPage(this);
        saveConfiguration();
    }

    public void connectToServer(String ip){
        TCPClient.SERVERIP = ip;
        if(serverTask != null) serverTask.cancel(true);
        serverTask = new connectTask();
        serverTask.execute("");
    }

    public boolean isEditing(){ return editing;}
    public boolean isRearranging(){ return rearranging;}
    public boolean isScaling(){return scaling;}
    public boolean isDeleting(){return deleting;}

    public TCPClient getTcpClient(){ return mTcpClient; }

    public class connectTask extends AsyncTask<String,String,TCPClient> {

        @Override
        protected TCPClient doInBackground(String... message) {

            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
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
        boolean isCheckedEdit  = settings.getBoolean("checkbox", editing);
        boolean isCheckedRearr = settings.getBoolean("checkbox", rearranging);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_removePage:
                removeCurrentPage();
                return true;
            case R.id.action_renamePage:
                renameCurrentPage();
                return true;
            case R.id.action_newPage:
                System.out.println("maxid:"+currentMaxID);
                addPage("Page"+(currentMaxID+1),createPage(currentMaxID+1));
                return true;
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
                                        connectToServer(userInput.getText().toString());
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
                Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_check:
                item.setChecked(!item.isChecked());
                SharedPreferences settings = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("checkbox", item.isChecked());
                editor.commit();

                editing = item.isChecked();
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
                rearranging = item.isChecked();
                if(item.isChecked()){
                    Toast.makeText(getApplicationContext(),"Rearrange mode is currently ON",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Rearrange mode is currently OFF",Toast.LENGTH_LONG).show();
                }
                return true;

            case R.id.action_check3:
                item.setChecked(!item.isChecked());

                SharedPreferences settings3 = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor3 = settings3.edit();
                editor3.putBoolean("checkbox", item.isChecked());
                editor3.commit();
                scaling = item.isChecked();
                if(item.isChecked()){
                    Toast.makeText(getApplicationContext(),"Scaling mode is currently ON",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Scaling mode is currently OFF",Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.action_check4:
                item.setChecked(!item.isChecked());

                SharedPreferences settings4 = getSharedPreferences("settings", 0);
                SharedPreferences.Editor editor4 = settings4.edit();
                editor4.putBoolean("checkbox", item.isChecked());
                editor4.commit();
                deleting = item.isChecked();
                if(item.isChecked()){
                    Toast.makeText(getApplicationContext(),"Deleting mode is currently ON",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"Deleting mode is currently OFF",Toast.LENGTH_LONG).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
