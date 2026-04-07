package org.firstinspires.ftc.teamcode.hardware;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// if your curious this is just for basic stuff im tired of writing every five seconds
public class Utilities {
    public static Map<String, Supplier<Boolean>> timers = new HashMap<>();
    public Utilities(){}
    // checks if second parameter is within range of the first parameter
    public static boolean isWithin(double check, double range, double target){
        return check < target + range && check > target - range;
    }
    // same as above but at a set .01 for small double comparisons
    public static boolean isClose(double check, double target){
        return check < target + .01 && check > target - .01;
    }
    public static Supplier<Boolean> startWait(int length){
        return new Supplier<Boolean>() {
            final long start = System.currentTimeMillis();
            final int wait = length;
            @Override
            public Boolean get() {
                return System.currentTimeMillis() > start + wait;
            }
        };
    }
    public static boolean holdDelay(String key, int length){
        if (timers.containsKey(key)){
            return timers.get(key).get();
        }
        timers.put(key, startWait(length));
        return false;
    }
    public static boolean hasTimer(String key){
        return timers.containsKey(key);
    }
    public static void addGlobalBool(String key, Supplier<Boolean> condition){
        timers.put(key,condition);
    }
    public static boolean arrayContains(Object[] objects, Object target){
        for (Object object : objects){
            if(object.equals(target)){
                return true;
            }
        }
        return false;
    }
}
