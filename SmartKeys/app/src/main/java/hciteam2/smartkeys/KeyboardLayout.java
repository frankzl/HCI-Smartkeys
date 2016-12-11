package hciteam2.smartkeys;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frankz on 26.11.16.
 */

public class KeyboardLayout {

    private static String [] qwerty = {"VK_DEAD_TILDEx1234567890xVK_MINUSxVK_EQUALSxVK_BACK_SPACE",
                                "VK_TABxQWERTYUIOPxVK_OPEN_BRACKETxVK_CLOSE_BRACKETxVK_ENTER",
                                "VK_CAPS_LOCKxASDFGHJKLxVK_SEMICOLON'\\",
                                "VK_SHIFTxVK_BACK_QUOTExZXCVBNMxVK_COMMAxVK_PERIODxVK_SLASHxVK_SHIFT",
                                "VK_CONTROLxVK_WINDOWSxVK_ALTxVK_SPACExVK_ALT_GRAPHxVK_CONTROLxVK_LEFTxVK_UPxVK_DOWNxVK_RIGHT"};

    public static List<ButtonInfo> getQWERTYList(){
        ArrayList<ButtonInfo> keylist = new ArrayList<>();
        for(int i = 0; i < qwerty.length; i++){
            String[] line = qwerty[i].split("x");
            int line_position = 0;
            for(int j = 0; j < line.length; j++){
                if(line[j].charAt(0) == 'V' && line[j].charAt(1)=='K'){
                    keylist.add(new ButtonInfo(line_position, i*Constants.key_default_height, line[j]));
                    line_position+=Constants.key_default_width;
                }else{
                    for(int z = 0; z < line[j].length(); z++){
                        keylist.add(new ButtonInfo(line_position, i*Constants.key_default_height, "VK_"+line[j].charAt(z)));
                        line_position+=Constants.key_default_width;
                    }
                }
            }
        }
        return keylist;
    }

}
