package com.baidupanapi.runnable.base;

import java.util.List;

/**
 * Created by xm on 15/11/21.
 */
public abstract class BaseRunnable {
    public abstract List<Object> execute(Object... paramList) throws Exception;
}