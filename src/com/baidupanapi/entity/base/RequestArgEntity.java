package com.baidupanapi.entity.base;

import com.baidupanapi.runnable.base.BaseRunnable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xm on 15/11/21.
 */
public class RequestArgEntity {
    private String method = null;
    private String url = null;
    private Map<String,String> extraParams = null;
    private Map<String,String> data = null;
    private Map<String,File> files = null;
    private BaseRunnable callback = null;
    private Map<String,Object> argMap = new HashMap<>();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getExtraParams() {
        return extraParams;
    }

    public void setExtraParams(Map<String, String> extraParams) {
        this.extraParams = extraParams;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }

    public Map<String, File> getFiles() {
        return files;
    }

    public void setFiles(Map<String, File> files) {
        this.files = files;
    }

    public BaseRunnable getCallback() {
        return callback;
    }

    public void setCallback(BaseRunnable callback) {
        this.callback = callback;
    }

    public Map<String, Object> getArgMap() {
        return argMap;
    }

    public void setArgMap(Map<String, Object> argMap) {
        this.argMap = argMap;
    }
}
