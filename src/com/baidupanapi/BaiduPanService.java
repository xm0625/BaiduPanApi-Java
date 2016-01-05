package com.baidupanapi;

import com.baidupanapi.runnable.base.BaseRunnable;
import com.baidupanapi.util.RandomStringGenerator;
import com.baidupanapi.util.TimeUtil;
import cz.msebera.android.httpclient.client.methods.CloseableHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by xm on 15/11/21.
 * @author  xm
 */
public class BaiduPanService extends BaseClass{
    BaiduPanService(String username, String password, BaseRunnable captchaRunnable) throws Exception {
        super(username, password, BaseData.apiTemplate, captchaRunnable);
    }

    /**
     * 获得配额信息
     * @return
     * 返回请求返回的字符串
     *
     * 返回正确时返回字符串中的数据结构
     * {"errno":0,"total":配额字节数,"used":已使用字节数,"request_id":请求识别号}
     * */
    public String quota(Map<String,Object> keyValueArgs) throws IOException {
        return request("quota",null,null,null,null,null,null,keyValueArgs);
    }

    /**
     * 获取目录下的文件列表
     *
     *@param remotePath
     * 网盘中目录的路径，必须以 / 开头。
     * Warning:
     * 路径长度限制为1000；
     * 径中不能包含以下字符：``\\\\ ? | " > < : *``；
     * 文件名或路径名开头结尾不能是 ``.``或空白字符，空白字符包括：``\\r, \\n, \\t, 空格, \\0, \\x0B`` 。
     *
     * @param orderBy
     * 排序字段，缺省根据文件类型排序 可选: time（修改时间）、name（文件名）、size（大小，注意目录无大小）.
     *
     *@param orderType
     * “asc”或“desc”，缺省采用desc。
     *
     *@param startIndex
     * 返回条目数量控制,首条序号,从0开始。返回结果集的[startIndex, endIndex)之间的条目，缺省返回所有条目；
     *
     *@param endIndex
     * 返回条目数量控制,尾条序号。返回结果集的[startIndex, endIndex)之间的条目，缺省返回所有条目；
     *
     *@return
     * 返回请求返回的字符串
     *
     * 返回正确时返回字符串中的数据结构
     *{"errno":0,"list":[{"local_mtime":1436591022,"size":0,"category":6,"fs_id":8003570172228,"path":"\/\u6211\u7684\u8d44\u6e90\/\u5206\u6b67\u80052","local_ctime":1436591022,"isdir":1,"server_ctime":1436591022,"server_mtime":1438861057,"server_filename":"\u5206\u6b67\u80052"},{"local_mtime":1435597875,"size":0,"category":6,"fs_id":563220016636297,"path":"\/\u6211\u7684\u8d44\u6e90\/\u7ec8\u7ed3\u80055\uff1a\u521b\u4e16\u7eaa (2015)\u9ad8\u6e05\u7535\u5f71\u7248","local_ctime":1435597875,"isdir":1,"server_ctime":1435597875,"server_mtime":1438947957,"server_filename":"\u7ec8\u7ed3\u80055\uff1a\u521b\u4e16\u7eaa (2015)\u9ad8\u6e05\u7535\u5f71\u7248"},{"server_mtime":1429343067,"category":4,"fs_id":289993814634795,"server_ctime":1397193508,"local_mtime":1397193508,"size":198637850,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/Spring 3.0\u5c31\u8fd9\u4e48\u7b80\u5355.pdf","local_ctime":1397193508,"md5":"dac5745d3e97150c5a22947bf0b2c2ee","server_filename":"Spring 3.0\u5c31\u8fd9\u4e48\u7b80\u5355.pdf"},{"server_mtime":1429343829,"category":4,"fs_id":131386538923398,"server_ctime":1380419983,"local_mtime":1380419981,"size":97381564,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/[www.java1234.com]Spring 3.x\u4f01\u4e1a\u5e94\u7528\u5f00\u53d1\u5b9e\u6218\uff08\u9ad8\u6e05\u7248\uff09.pdf","local_ctime":1380419981,"md5":"6cd932cba3e07ac5f893cc3700308e95","server_filename":"[www.java1234.com]Spring 3.x\u4f01\u4e1a\u5e94\u7528\u5f00\u53d1\u5b9e\u6218\uff08\u9ad8\u6e05\u7248\uff09.pdf"},{"server_mtime":1430185796,"category":6,"fs_id":657248952967289,"server_ctime":1417609601,"local_mtime":1417609601,"size":35718,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/VMware.Workstation.v11.0.0.Incl.Keymaker-EMBRACE.rar","local_ctime":1417609601,"md5":"27b8ad4eb46ca51ddfc43cd018f9b3eb","server_filename":"VMware.Workstation.v11.0.0.Incl.Keymaker-EMBRACE.rar"},{"server_mtime":1435953497,"category":6,"fs_id":858048201335917,"server_ctime":1435953497,"local_mtime":1435953497,"size":4130,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/jennifer.ovpn","local_ctime":1435953497,"md5":"9a4baeb5e5d331edfd74de392da8bbe3","server_filename":"jennifer.ovpn"},{"server_mtime":1436720432,"category":4,"fs_id":410246317449529,"server_ctime":1365773406,"local_mtime":1365773405,"size":52198955,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/Git\u6743\u5a01\u6307\u5357.pdf","local_ctime":1365773405,"md5":"6bfaf57228e59098150c4e41575f09bb","server_filename":"Git\u6743\u5a01\u6307\u5357.pdf"},{"server_mtime":1438859077,"category":1,"fs_id":1123162186822579,"server_ctime":1427971686,"local_mtime":1427971681,"size":1107784078,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/FQZ2.mp4","local_ctime":1427971681,"md5":"a644dd29434521256015ad8bd3438b5e","server_filename":"FQZ2.mp4"},{"server_mtime":1438860942,"category":1,"fs_id":135626887013087,"server_ctime":1428140141,"local_mtime":1428140138,"size":1107824938,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/\u5206\u671f\u52192 \u7ffb\u76d8\u8005www.565k.com \u9ad8\u6e05\u4e91\u5f71\u89c6.mkv","local_ctime":1428140138,"md5":"f6f74229ad434f47791dad6b6523b79a","server_filename":"\u5206\u671f\u52192 \u7ffb\u76d8\u8005www.565k.com \u9ad8\u6e05\u4e91\u5f71\u89c6.mkv"},{"server_mtime":1443721281,"category":6,"fs_id":801453533140931,"server_ctime":1442198590,"local_mtime":1442198590,"size":774759442,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/miui_HMNote2_V6.7.5.0.LHMCNCH_7b45a9a562_5.0.zip","local_ctime":1442198590,"md5":"2e5657fa28748524d228408d32d274fd","server_filename":"miui_HMNote2_V6.7.5.0.LHMCNCH_7b45a9a562_5.0.zip"},{"server_mtime":1449235296,"category":6,"fs_id":381138983436276,"server_ctime":1438535416,"local_mtime":1438535416,"size":1048142,"isdir":0,"path":"\/\u6211\u7684\u8d44\u6e90\/KMS10.rar","local_ctime":1438535416,"md5":"d3b2c1fcbfe1c7be24234850163e769f","server_filename":"KMS10.rar"}],"request_id":104506677824701356}
     * */
    public String listFiles(String remotePath,String orderBy,String orderType,Integer startIndex,Integer endIndex, Map<String,Object> keyValueArgs) throws IOException {
        //设置默认值
        if(orderBy == null){
            orderBy = "name";
        }
        if(orderType == null){
            orderType = "desc";
        }
        if("desc".equals(orderType)){
            orderType = "1";
        }else{
            orderType = "0";
        }
        Map<String,String> params = new HashMap<>();
        params.put("dir",remotePath);
        params.put("order",orderBy);
        params.put("desc",orderType);

        return request("list","list",null,params,null,null,null,keyValueArgs);
    }

    /**
     * 上传单个文件（<2G）.
     * 百度PCS服务目前支持最大2G的单个文件上传。如需支持超大文件（>2G）的断点续传，请参考下面的“分片文件上传”方法。
     *
     * @param dir
     * 网盘中文件的保存路径（不包含文件名）。
     * 必须以 / 开头。
     * Warning:
     * 路径长度限制为1000；
     * 径中不能包含以下字符：``\\\\ ? | " > < : *``；
     * 文件名或路径名开头结尾不能是 ``.``或空白字符，空白字符包括：``\\r, \\n, \\t, 空格, \\0, \\x0B`` 。
     *
     * @param file
     * 上传文件对象
     *
     * @param fileName
     * 文件名称
     *
     * @param onDuplicate
     * 当文件已存在时如何处理（可选）
     * 'overwrite'：表示覆盖同名文件；
     * 'newcopy'：表示生成文件副本并进行重命名，命名规则为“文件名_日期.后缀”。
     *
     * @param callback
     *  上传进度回调函数 需要包含 size 和 progress 名字的参数
     *
     * @return
     * 返回请求返回的字符串
     *
     * 返回正确时返回的 Reponse 对象 content 中的数据结构
     *{"path":"服务器文件路径","size":文件大小,"ctime":创建时间,"mtime":修改时间,"md5":"文件md5值","fs_id":服务器文件识别号,"isdir":是否为目录,"request_id":请求识别号}
     *
     * */
    public String upload(String dir,File file,String fileName,String onDuplicate,BaseRunnable callback, Map<String,Object> keyValueArgs) throws IOException {
        //设置默认值
        if(onDuplicate == null){
            onDuplicate = "newcopy";
        }

        Map<String,String> params = new HashMap<>();
        params.put("dir",dir);
        params.put("ondup",onDuplicate);
        params.put("filename",fileName);

        String tmpFilename = RandomStringGenerator.getRandomStringByLength(10);
        Map<String,Object> files = new HashMap<>();
        files.put("file",file);

        String url = String.format("https://%s/rest/2.0/pcs/file",BaseData.BAIDUPCS_SERVER);
        return request("file","upload",url,params,null,files,callback,keyValueArgs);
    }
}
