package hciteam2.smartkeys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.RelativeLayout;

/**
 * Created by frankz on 27.11.16.
 */

public class KeysOnTouchListener implements OnTouchListener{

    TCPClient tcpClient;
    ButtonInfo info;
    float initialTouchX;
    float initialTouchY;
    float initialRawX;
    float initialRawY;

    public KeysOnTouchListener(TCPClient tcpClient, ButtonInfo info){
        super();
        this.tcpClient = tcpClient;
        this.info = info;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        System.out.println(event.getAction());
        ButtonItem element = (ButtonItem) v;

        if(element.isRearranging()) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                initialTouchX = element.getX();
                initialTouchY = element.getY();
                initialRawX = event.getRawX();
                initialRawY = event.getRawY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                element.setX(initialTouchX + event.getRawX() - initialRawX);
                element.setY(initialTouchY + event.getRawY() - initialRawY);
            }
            return true;
        }else if(element.isEditing()){
            if(event.getAction() == MotionEvent.ACTION_DOWN)
                popUpEditing(element);
        }else if(element.isScaling()){
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                initialTouchX = element.getWidth();
                initialTouchY = element.getHeight();
                initialRawX = event.getRawX();
                initialRawY = event.getRawY();
            }else if (event.getAction() == MotionEvent.ACTION_MOVE){
                float width = (initialTouchX + event.getRawX() - initialRawX);
                element.setWidth( Math.round(width));
                float height = (initialTouchY + event.getRawY() - initialRawY);
                element.setHeight( Math.round(height));
            }
        }
        if(tcpClient == null){
            tcpClient = element.getTcpClient();
        }else{
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                pressButton(element);
            }else if(event.getAction() == MotionEvent.ACTION_UP){
                if(!element.isPressed()){
                    pressButton(element);
                    releaseButton(element);
                }else{
                    releaseButton(element);
                }
            }
        }
        return true;
    }

    public void pressButton(ButtonItem btn){
        btn.setPressed(true);
        btn.getTcpClient().sendMessage("1"+info.getVal()+"");
    }

    public void releaseButton(ButtonItem btn){
        btn.setPressed(false);
        btn.getTcpClient().sendMessage("0"+info.getVal()+"");
    }

    public void popUpEditing(final ButtonItem item){
            LayoutInflater li = LayoutInflater.from(item.getContext());
            View promptsView = li.inflate(R.layout.editmenu, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    item.getContext());

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
                                    info.setVal(userInput.getText().toString());
                                    item.setText();
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
}
