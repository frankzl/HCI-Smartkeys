package hciteam2.smartkeys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {

    boolean editing = false;
    LinkedList<Point> coordinates;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    coordinates.add(new Point((int)event.getX(), (int)event.getY()));
                }
                return true;
            }
        });

        Button btn_toggleEditing = (Button) findViewById(R.id.btn_toggleEditing);

        coordinates = new LinkedList<>();

        btn_toggleEditing.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) findViewById(R.id.txt_mode);

                if(editing){
                    createKeys();
                    view.setText("Mode: Editing Off");
                }else{
                    view.setText("Mode: Editing On");
                }
                editing = !editing;
            }
        });
    }

    public void createKeys(){
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        while(!coordinates.isEmpty()){
            Point point = coordinates.remove();

            Button btn = new Button(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
            params.leftMargin = point.getX();
            params.topMargin = point.getY();
            layout.addView(btn, params);
        }
    }

    private class Point{
        private int x;
        private int y;
        public Point (int x, int y){
            this.x = x;
            this.y = y;
        }
        public void setX(int x){ this.x = x; }

        public int getX(){ return x; }

        public void setY(int y){ this.y = y; }

        public int getY(){ return y; }
    }
}
