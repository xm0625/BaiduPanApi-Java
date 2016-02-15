package com.baidupanapi;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidupanapi.util.HttpClientHelper;
import cz.msebera.android.httpclient.annotation.NotThreadSafe;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    private String tempSavePath;
    private int fileSize;
    private List<Integer> downloadPartStartPositionList;
    private List<Integer> downloadingPartStartPositionList;
    private ReentrantLock lock = new ReentrantLock();

    public Downloader(BaiduPanService baiduPanService,String remotePath,String saveTo) throws IOException {
        //TODO 尚未处理好线程安全问题,BaiduPanService不是线程安全的

        this.baiduPanService = baiduPanService;
        this.saveTo = saveTo;
        this.tempSavePath = saveTo+".download";
        this.remotePath = remotePath;

        deleteDir(new File(tempSavePath));
        new File(tempSavePath).mkdirs();
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
        ArrayList<Thread> threadPools = new ArrayList<>();
        for(int i=0;i<5;i++){
            Thread thread = new Thread(new DownloadWorker());
            thread.start();
            threadPools.add(thread);
        }
        for(Thread thread:threadPools){
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        exportSingleFile(tempSavePath);
        deleteDir(new File(tempSavePath));

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
                    baiduPanService.download(remotePath, keyValueMap).getEntity().writeTo(new FileOutputStream(new File(String.format("%s"+File.separator+"tmp-%d",tempSavePath,startPosition))));
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




    private void exportSingleFile(String filePath) {

        //验证文件是否能够导出
        File file=new File(filePath);
        String[] filmNameArray= file.list();
        int fileAmount=filmNameArray.length;
        if(fileAmount==0){
            System.out.println(filePath+"下无文件");
            return;
        }
        List<String> filmNameList = Arrays.asList(filmNameArray);
        for(int i=0;i<fileAmount;i++){
            String fromFileName="tmp-"+i*PART_SIZE;
            if(!filmNameList.contains(fromFileName)){
                System.out.println("下载不完整");
                return;
            }
        }
        System.out.println("开始导出");

        double secondProgress=0.0;
        double secondProgressStep=100.0/fileAmount;
        File outputFile=new File(saveTo);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        if(outputFile.exists()){
            outputFile.delete();
        }
        if(!outputFile.exists()) {
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }
        File fromFile;
        FileOutputStream outputFileStream = null;
        FileInputStream fromFileStream = null;
        try {
            outputFileStream = new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        FileChannel fcout = outputFileStream.getChannel();
        FileChannel fcin = null;
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        long allStartTime=System.currentTimeMillis();
        for(int i=0;i<fileAmount;i++){
            String fromFileName="tmp-"+i*PART_SIZE;
            fromFile=new File(filePath+File.separator+fromFileName);
            try {
                fromFileStream=new FileInputStream(fromFile);
                fcin=fromFileStream.getChannel();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            while (true) {
                // clear方法重设缓冲区，使它可以接受读入的数据
                buffer.clear();
                // 从输入通道中将数据读到缓冲区
                int r = -1;
                try {
                    r = fcin.read(buffer);
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                // read方法返回读取的字节数，可能为零，如果该通道已到达流的末尾，则返回-1
                if (r == -1) {
                    break;
                }
                // flip方法让缓冲区可以将新读入的数据写入另一个通道
                buffer.flip();
                // 从输出通道中将数据写入缓冲区
                try {
                    fcout.write(buffer);
                } catch (IOException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
            try {
                fcin.close();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            try {
                fromFileStream.close();
            } catch (IOException e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
            secondProgress+=secondProgressStep;
            System.out.println("secondProgress:"+secondProgress);
        }
        try {
            fcout.close();
            outputFileStream.close();
        } catch (IOException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        System.out.println("总耗时：" + (System.currentTimeMillis() - allStartTime) / 1000 + "s" + "\n导出成功");
    }


    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

}
