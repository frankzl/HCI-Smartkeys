package hciteam2.smartkeys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

/**
 * Created by frankz on 27.11.16.
 */

public class KeysOnTouchListener implements OnTouchListener {

    TCPClient tcpClient;
    ButtonInfo info;
    float initialTouchX;
    float initialTouchY;
    float initialRawX;
    float initialRawY;
    boolean touch2;

    public KeysOnTouchListener(TCPClient tcpClient, ButtonInfo info) {
        super();
        this.tcpClient = tcpClient;
        this.info = info;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ButtonItem element = (ButtonItem) v;
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(element.isRearranging()){
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                initialTouchX = element.getX();
                initialTouchY = element.getY();
                initialRawX = event.getRawX();
                initialRawY = event.getRawY();
                return true;
            }else if(element.isEditing()){
                popUpEditing(element);
                return true;
            }else if(element.isScaling()){
                initialTouchX = element.getWidth();
                initialTouchY = element.getHeight();
                initialRawX = event.getRawX();
                initialRawY = event.getRawY();
            }else{
                if(tcpClient == null) tcpClient = element.getTcpClient();
                if(tcpClient != null) pressButton(element);
            }
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
            if(element.isRearranging()){
                element.setX(initialTouchX + event.getRawX() - initialRawX);
                element.setY(initialTouchY + event.getRawY() - initialRawY);
                return true;
            }else if(element.isScaling()){
                float width = (initialTouchX + event.getRawX() - initialRawX);
                element.setWidth(Math.round(width));
                float height = (initialTouchY + event.getRawY() - initialRawY);
                element.setHeight(Math.round(height));
                return true;
            }
        }else if(event.getAction() == MotionEvent.ACTION_UP) {
            if(tcpClient!=null) {
                if (!element.isPressed()) {
                    pressButton(element);
                }
                releaseButton(element);
                return true;
            }
        }
        return false;
    }

    public void pressButton(ButtonItem btn) {
        btn.setPressed(true);
        if(info.getVal().charAt(0) == 'Q'){
            btn.getTcpClient().sendMessage(info.getVal() + "");
        }else {
            btn.getTcpClient().sendMessage("1" + info.getVal() + "");
        }
    }

    public void releaseButton(ButtonItem btn) {
        btn.setPressed(false);
        btn.getTcpClient().sendMessage("0" + info.getVal() + "");
    }

    public void popUpEditing(final ButtonItem item) {
        LayoutInflater li = LayoutInflater.from(item.getContext());
        final View promptsView = li.inflate(R.layout.editmenu, null);

        final RadioGroup group = (RadioGroup) promptsView.findViewById(R.id.radioKeyType);
        group.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        EditText userInput = (EditText) (promptsView.findViewById(R.id.editvalue));
                        switch (checkedId){
                            case 1:
                                userInput.setText("VK_");
                                break;
                            case 2:
                                userInput.setText("F_");
                                break;
                            case 3:
                                userInput.setText("S_");
                                break;
                            case 4:
                                userInput.setText("\\u");
                                break;
                            case 5:
                                userInput.setText("Q_");
                            default: break;
                        }
                    }
                }
        );

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                item.getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText nameInput = (EditText) promptsView
                .findViewById(R.id.editname);
        nameInput.setText(info.getName());

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editvalue);
        userInput.setText(info.getVal());

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                info.setVal(userInput.getText().toString());
                                info.setName(nameInput.getText().toString());
                                item.setText();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
