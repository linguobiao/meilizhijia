package com.winmobi.helper;

import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by luocan on 2016/7/24.
 */
public class SignHelper {

    public static String createSign(String characterEncoding,SortedMap<Object,Object> parameters,String key){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + key);
        Log.i("winmobi-----sb:", sb.toString());
        String sign = MD5Helper.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }
}
