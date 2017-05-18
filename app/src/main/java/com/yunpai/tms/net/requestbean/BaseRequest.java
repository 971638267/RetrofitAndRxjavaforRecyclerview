package com.yunpai.tms.net.requestbean;


import com.yunpai.tms.util.Utils;

import java.util.Map;

/**
 * Created by gan on 2017/4/18.
 */
public class BaseRequest {
    public Map<String, Object> toMap() {
        return Utils.objectToMap(this);
    }
}
