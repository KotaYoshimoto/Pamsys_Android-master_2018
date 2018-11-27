package com.example.yamaguchi.pamsys.CouponTicketSaleActivityHelperClasses;

/**
 * Created by yamaguchi on 2017/11/05.
 */

public enum CouponTicketType {
    Adult(1, "200円回数券"),
    Child(2, "100円回数券"),
    Handicapped(3, "100円回数券(障害者)");

    private int id;
    private String content;

    private CouponTicketType(int id, String content){
        this.id = id;
        this.content = content;
    }

    public int getId(){
        return this.id;
    }
    @Override
    public String toString(){
        return this.content;
    }
}
