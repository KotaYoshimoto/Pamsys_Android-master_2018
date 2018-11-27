package com.example.yamaguchi.pamsys.Common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yamaguchi on 2017/11/15.
 */

public class InputChecker {

    public static boolean isNumber(String num) {
        Pattern p = Pattern.compile("^[0-9]+$");
        Matcher m = p.matcher(num);
        return m.matches();
    }

    /**
     * 文字列が数値であればInt型に変換し，それ以外なら例外をスローする
     * @param num
     * @return
     */
    public static int numOnlyString(String num) {
        //未入力の場合の処理
        if (num == null || num.equals("")) return 0;
        //文字を省く
        num.replaceAll("[^0-9]* ", "");
        int i;
        try {
            i = Integer.parseInt(num);
        }catch (NumberFormatException e){
            i = 0;
        }
        return i;
    }

}
