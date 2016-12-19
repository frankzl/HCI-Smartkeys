package hciteam2.smartkeys;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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
    AlertDialog popUp;

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
        System.out.println(event.getAction());

         if(event.getAction() == MotionEvent.ACTION_DOWN ) {
             if (!element.isRearranging() && !element.isScaling() && !element.isEditing() && !element.isDeleting()) {
                 if (tcpClient == null) tcpClient = element.getTcpClient();
                 if (tcpClient != null && !element.isPressed()) pressButton(element);
                 return true;
             } else if (element.isRearranging()) {
                 RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                 initialTouchX = element.getX();
                 initialTouchY = element.getY();
                 initialRawX = event.getRawX();
                 initialRawY = event.getRawY();
                 return true;
             } else if (element.isEditing()) {
                 if (popUp == null) {
                     popUp = createPopUpDialog(element);
                 }
                 popUp.show();

                 return true;
             } else if (element.isScaling()) {
                 initialTouchX = element.getWidth();
                 initialTouchY = element.getHeight();
                 initialRawX = event.getRawX();
                 initialRawY = event.getRawY();
                 return true;
             } else if (element.isDeleting()) {
                 element.removeButton();
                 return true;
             }
         } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
             if (element.isRearranging()) {
                 element.setX(initialTouchX + event.getRawX() - initialRawX);
                 element.setY(initialTouchY + event.getRawY() - initialRawY);
                 return true;
             } else if (element.isScaling()) {
                 float width = (initialTouchX + event.getRawX() - initialRawX);
                 element.setWidth(Math.round(width));
                 float height = (initialTouchY + event.getRawY() - initialRawY);
                 element.setHeight(Math.round(height));
                 return true;
             }
         }else if(event.getAction() == MotionEvent.ACTION_UP) {
             if (tcpClient != null) {
                 if (element.isEditing() || element.isScaling() || element.isRearranging()) {
                     return true;
                 }
                 if (element.isPressed())
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

    public AlertDialog createPopUpDialog(final ButtonItem item) {
        LayoutInflater li = LayoutInflater.from(item.getContext());
        final View promptsView = li.inflate(R.layout.editmenu, null);

        GridLayout grid = (GridLayout) promptsView.findViewById(R.id.buttonlist);
        String[] buttonlist = {"A","B","C","D","E","F","G","H","I",
        "J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X",
        "Y","Z","ALT","BACK_SPACE","WINDOWS","CONTROL","EQUALS","MINUS","ENTER",
        "SHIFT","TAB","LEFT","DOWN","UP","RIGHT","1","2","3","4","5","6","7","8","9","0"};
        for( int i = 0; i < buttonlist.length; i++ ){
            Button btn = new Button(grid.getContext());
            btn.setText(buttonlist[i]);
            btn.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        EditText text = (EditText) promptsView.findViewById(R.id.editvalue);
                        String str = text.getText().toString();
                        str += ";1VK_"+((Button) v).getText().toString();
                        text.setText(str);
                        return true;
                    }else if(event.getAction() == MotionEvent.ACTION_UP){
                        EditText text = (EditText) promptsView.findViewById(R.id.editvalue);
                        String str = text.getText().toString();
                        str += ";0VK_"+((Button) v).getText().toString();
                        text.setText(str);
                        return true;
                    }
                    return false;
                }
            });
            grid.addView(btn);
        }

        final RadioGroup group = (RadioGroup) promptsView.findViewById(R.id.radioKeyType);
        group.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        EditText userInput = (EditText) (promptsView.findViewById(R.id.editvalue));
                        System.out.println("onCheckedChanged"+checkedId);
                        switch ((checkedId-1)%5){
                            case 0:
                                userInput.setText("VK_");
                                break;
                            case 1:
                                userInput.setText("F_");
                                break;
                            case 2:
                                userInput.setText("S_");
                                break;
                            case 3:
                                userInput.setText("\\u");
                                break;
                            case 4:
                                userInput.setText("Q");
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
        return alertDialog;
    }
}
