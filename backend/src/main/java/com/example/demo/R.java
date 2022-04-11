package com.example.demo;

import java.util.HashMap;
import java.util.Map;

public class R extends HashMap<String, Object> {
    private static final long vid = 1L;

    public R() {
        put("code", 0);
    }

    public static R error(){
        return error(500,"Unknown error");
    }

    public static R error(String msg){
        return error(500, msg);}

    public static R error(int code,String msg){
        R rr = new R();
        rr.put("code",code);
        rr.put("msg",msg);
        return rr;
    }

    public static R ok(String msg){
        R rr = new R();
        rr.put("msg",msg);
        return rr;
    }

    public static R ok( Map<String, Object> k){
        R rr = new R();
        rr.putAll(k);
        return rr;
    }

    public static R ok(){return new R();}

    public R put(String index, Object zhi) {
        super.put(index, zhi);
        return this;
    }
}

