package hciteam2.smartkeys;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frankz on 17.12.16.
 */

public class KeyPage extends Fragment {

    RelativeLayout layout;
    List<ButtonInfo> keyList;

    int index;

    String text;
    MainActivity widget;

    private boolean isVisibleToUser = false;

    public KeyPage(){
        keyList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        widget = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        View v = (RelativeLayout) inflater.inflate(R.layout.fragment, container, false);
        layout = (RelativeLayout) v.findViewById(R.id.keypage);

        ArrayList<ButtonInfo> tempKeyList = new ArrayList<>();
        Bundle bundle = this.getArguments();

        if(bundle != null) {
            if(bundle.getParcelableArrayList("keylist") != null) {
                tempKeyList = bundle.getParcelableArrayList("keylist");
            }
            index = Integer.parseInt(bundle.getString("index"));
        }

        System.out.println("bdl:"+bundle);

        String stored = FileWriter.readFromFile(getContext(), "config"+index);

        System.out.println("stored:"+stored);
        if(stored.length() > 0) {
            String[] objectList = stored.split(";key:");
            tempKeyList.clear();
            for (int i = 0; i < objectList.length; i++) {
                System.out.println(objectList[i]);
                tempKeyList.add(new ButtonInfo(objectList[i]));
                System.out.println("adding");
            }
        }

        createKeysFromList(tempKeyList);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    System.out.println(event.getX()+"|"+event.getY());
                if(event.getAction() == MotionEvent.ACTION_DOWN && widget.isEditing()){
                    generateListView(event.getX(), event.getY());
                    return true;
                }
                return false;
            }
        });

        //saveKeyPage(widget);

        return v;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // Make sure that we are currently visible
        if (this.isVisible()) {
            this.isVisibleToUser = true;
            // If we are becoming invisible, then...
            if (!isVisibleToUser) {
                this.isVisibleToUser = false;
                Log.d("MyFragment", "Not visible anymore.  Stopping audio.");
                saveKeyPage(getActivity());
            }
        }
    }

    public boolean isVisibleToUser(){
        if(isVisibleToUser && !isVisible()){
            return false;
        }
        return isVisibleToUser;
    }

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        widget = (MainActivity) activity;
    }

    public void generateListView(final float x, final float y){
        final Dialog dialog = new Dialog(this.getContext());
        dialog.setContentView(R.layout.list);

        GridView lv = (GridView ) dialog.findViewById(R.id.lv);

        // Defined Array values to show in ListView
        String list = "abcdefghijklmnopqrstuvwxyz1234567890".toUpperCase();
        final String [] values = new String[list.length()+9];
        values[list.length()] = "BACK_SPACE";
        values[list.length()+1] = "ENTER";
        values[list.length()+2] = "SHIFT";
        values[list.length()+3] = "CONTROL";
        values[list.length()+4] = "SPACE";
        values[list.length()+5] = "ALT";
        values[list.length()+6] = "WINDOWS";
        values[list.length()+7] = "CAPS_LOCK";
        values[list.length()+8] = "META";

        for(int i = 0; i < list.length(); i++){
            values[i] = ""+list.charAt(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ButtonItem btn = createKey(x, y-Constants.key_offset_Y, "VK_"+values[position].charAt(0));
                keyList.add(btn.getInformation());
                layout.addView(btn);
                dialog.dismiss();
            }
        });


        dialog.setCancelable(true);
        dialog.setTitle("ListView");
        dialog.show();
    }

    public void removeKey(ButtonInfo info){
        for(int i = 0; i < keyList.size(); i++){
            ButtonInfo item = keyList.get(i);
            int r1 = (int) (item.getX()+item.getY());
            int r2 = (int) (info.getX()+info.getY());
            if(r1 == r2){
                keyList.remove(i);
                saveKeyPage(widget);
                break;
            }
        }
    }

    public ButtonItem createKey(float x, float y, String val){
        final ButtonItem btn = new ButtonItem(x, y, val, (MainActivity) getActivity(), this);
        btn.setOnTouchListener(new KeysOnTouchListener(((MainActivity)getActivity()).getTcpClient(), btn.information));
        return btn;
    }

    public void setKeyList(List<ButtonInfo> infoList){
        this.keyList = infoList;
    }

    public ButtonItem createKey(ButtonInfo info){
        ButtonItem btn = new ButtonItem(info, (MainActivity) getActivity(), this);
        btn.setOnTouchListener(new KeysOnTouchListener(((MainActivity)getActivity()).getTcpClient(), btn.information));
        return btn;
    }

    public void createKeysFromList(List<ButtonInfo> infoList){
        keyList.clear();
        for(int i = 0; i < infoList.size(); i++){
            ButtonInfo info = infoList.get(i);
            ButtonItem btn = createKey(info);
            keyList.add(btn.getInformation());
            //ButtonItem btn = createKey(info.getX(), info.getY(), info.getVal());
            layout.addView(btn);
        }
    }

    public void saveKeyPage(Context context){
        System.out.println("widg"+context);
        String data="";
        for(int i = 0; i < keyList.size(); i++){
            data += keyList.get(i).toString()+";key:";
        }
        FileWriter.writeToFile(data, context, "config"+index);
    }

    public int getIndex(){return this.index;}
}
