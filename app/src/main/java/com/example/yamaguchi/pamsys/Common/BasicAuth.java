package com.example.yamaguchi.pamsys.Common;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/**
 * Created by yamaguchi on 2017/11/18.
 */

public class BasicAuth extends Authenticator {
    final String userName = "android";
    final String passWord = "pamsys2017";

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName,passWord.toCharArray());
    }

}
