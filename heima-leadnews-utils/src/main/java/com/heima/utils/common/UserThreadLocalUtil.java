package com.heima.utils.common;

public class UserThreadLocalUtil {

    static ThreadLocal<Long> userId = new ThreadLocal<>();

    public static void set(Long id){
        userId.set(id);
    }

    public static Long get(){
        return userId.get();
    }

    public static void remove(){
        userId.remove();
    }


}
