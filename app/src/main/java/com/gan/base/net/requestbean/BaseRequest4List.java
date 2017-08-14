package com.gan.base.net.requestbean;


import com.gan.base.util.Utils;

import java.util.Map;

/**
 * Created by gan on 2017/4/17.
 */

public class BaseRequest4List {

    private int start=1;
    private int count=10;

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, Object> toMap() {
        return Utils.objectToMap(this);
    }
}
