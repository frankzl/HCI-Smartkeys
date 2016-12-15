package hciteam2.smartkeys;

/**
 * Created by frankz on 26.11.16.
 */

public class ButtonInfo {
    private float x;
    private float y;

    private String val;
    private String name;

    public ButtonInfo(float x, float y, String symbol){
        this.x = x;
        this.y = y;
        this.val = symbol;
        if(symbol.length() >= 3){
            this.name = val.substring(3);
        }
    }

    public void setVal(String val){ this.val = val;}
    public void setName(String val){ this.name = val; }

    public String getName(){
        return name;
    }

    public float getX(){return this.x;}
    public float getY(){return this.y;}

    public String getVal(){
        return this.val;
    }
}
