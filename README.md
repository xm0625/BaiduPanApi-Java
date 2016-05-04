# baidupanapi-java

## 百度网盘api(Java版)
======================
(网盘存放资料不够安全,停止维护)

###项目简介
-----------
项目基于latyas(懒)(latyas@gmail.com)的baidupcsapi项目(python).去除项目名称中的PCS改为baidupan,完成原作者改名的愿望

###原项目
-----------
https://github.com/ly0/baidupcsapi/

###文档
-----------
http://baidupcsapi.readthedocs.org/

###开发环境
-----------
IDEA

###随便说几句
-----------
1.针对Android和iOS

    移动端的百度云客户端在文件浏览和管理方面已经做的很好了, 能满足基本的需求, 缺少离线功能.
    
2.针对Linux

    没有同步,秒传,指定目录定向同步,多线程上传下载等功能
    
3.Mac&Win

    指定目录定向同步,多线程上传下载等功能
    

Java版主要用来理清API结构,梳理接口,作为Android版的基础

目前用HttpClient实现了登陆部分,后面考虑到HttpClient在Android的兼容性,可能会出更换为更底层的HttpURLConnection来实现.或者Volley+HttpURLConnection

ios将使用swift开发,配合AFNetworking等第三方框架进行实现(其实已经用UIWebView实现了离线功能)


Linux&Mac&Win可能以Python来做,毕竟Python的跨平台可能要比Java好,但是UI方面可能Java要好用一些
