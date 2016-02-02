package com.baidupanapi;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidupanapi.util.HttpClientHelper;
import cz.msebera.android.httpclient.annotation.NotThreadSafe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author xm
 * @version [版本号, 2016年02月02日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class Downloader {
    /**
     * 日志实例
     */

    private static final int PART_SIZE = 10000000;//10m

    private BaiduPanService baiduPanService;
    private String remotePath;
    private String saveTo;
    private int fileSize;
    private List<Integer> downloadPartStartPositionList;
    private List<Integer> downloadingPartStartPositionList;
    private ReentrantLock lock = new ReentrantLock();

    public Downloader(BaiduPanService baiduPanService,String remotePath,String saveTo) throws IOException {
        //TODO 尚未处理好线程安全问题,BaiduPanService不是线程安全的

        this.baiduPanService = baiduPanService;
        this.saveTo = saveTo;
        this.remotePath = remotePath;
        downloadPartStartPositionList = new ArrayList<>();
        downloadingPartStartPositionList = new ArrayList<>();

        String metaInfoString = HttpClientHelper.getResponseString(baiduPanService.getMetaInfo(Collections.singletonList(remotePath), null));
        JSONObject metaInfoJSONObject = JSON.parseObject(metaInfoString);
        fileSize = ((JSONObject)metaInfoJSONObject.getJSONArray("info").get(0)).getInteger("size");

        for(int currentPositon=0;currentPositon<fileSize;currentPositon+=PART_SIZE){
            downloadPartStartPositionList.add(currentPositon);
        }

    }

    public void start(){
        new Thread(new DownloadWorker()).start();
        new Thread(new DownloadWorker()).start();
        new Thread(new DownloadWorker()).start();
        new Thread(new DownloadWorker()).start();
        new Thread(new DownloadWorker()).start();
    }

    private class DownloadWorker implements Runnable{

        @Override
        public void run() {
            while (true){
                lock.lock();
                if(downloadPartStartPositionList.size() == 0 ){
                    lock.unlock();
                    return;
                }
                int startPosition = downloadPartStartPositionList.get(0);
                downloadingPartStartPositionList.add(startPosition);
                downloadPartStartPositionList.remove(new Integer(startPosition));
                lock.unlock();
                try{
                    int endPosition = startPosition+PART_SIZE-1;

                    Map<String,Object> keyValueMap = new HashMap<>();
                    Map<String,String> headers = new HashMap<>();
                    headers.put("Range",String.format("bytes=%s-%s",startPosition,endPosition));
                    keyValueMap.put("headers",headers);
                    baiduPanService.download(remotePath, keyValueMap).getEntity().writeTo(new FileOutputStream(new File(String.format("%s.tmp-%d",saveTo,startPosition))));
                    lock.lock();
                    downloadingPartStartPositionList.remove(new Integer(startPosition));
                    lock.unlock();
                }catch (Exception e){
                    lock.lock();
                    downloadingPartStartPositionList.remove(new Integer(startPosition));
                    downloadPartStartPositionList.add(startPosition);
                    lock.unlock();
                }

            }

        }
    }


}
