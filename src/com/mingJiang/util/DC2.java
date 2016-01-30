/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mingJiang.util;

/**
 *
 * @author Ming Jiang
 */
public class DC2 {
    static{
        System.loadLibrary("dc");
    }
      public native  String GetUserInfo(String UserName, String passWord);

    /**
     * 通过上传验证码图片字节到服务器进行验证码识别，方便多线程发送,这个函数可以保护作者的收入，一定要提交软件ID
     *
     * @param imgByte 验证码图片字节集
     * @param len 字节集长度
     * @param username QQ超人账号
     * @param password QQ超人密码
     * @param softId 软件ID
     * @return 成功返回->验证码结果|!|打码工人
     *
     * 后台没点数了返回:No Money! 未注册返回:No Reg! 上传验证码失败:Error:Put Fail!
     * 识别超时了:Error:TimeOut! 上传无效验证码:Error:empty picture!
     */
    public native String RecByte_A(byte[] imgByte, int len, String username, String password, String softId);

    /**
     * 命令名称:RecYZM_A 命令功能:通过发送验证码本地图片到服务器识别,这个函数可以保护作者的收入，一定要提交软件ID
     *
     * @param path 验证码本地路径，例如（c:\1.jpg)
     * @param UserName QQ超人账号
     * @param passWord QQ超人密码
     * @param softId 软件ID
     * @return 返回值类型:文本型 成功返回->验证码结果|!|打码工人
     *
     * 后台没点数了返回:No Money! 未注册返回:No Reg! 上传验证码失败:Error:Put Fail!
     * 识别超时了:Error:TimeOut! 上传无效验证码:Error:empty picture!
     */
    public native String RecYZM_A(String path, String UserName, String passWord, String softId);

    /**
     * 无返回值 对打错的验证码进行报告
     *
     * @param codeUser QQ超人账号
     * @param daMaWorker 打码工人
     */
    public native void ReportError(String codeUser, String daMaWorker);

    /**
     * 命令名称:Reglz 命令功能:通过作者的下线注册QQ超人账号
     *
     * @param userName QQ超人账号
     * @param passWord QQ超人密码
     * @param email QQ邮箱
     * @param qq QQ号
     * @param agentid 作者的推广id
     * @param agentName 作者账号
     * @return 返回值类型:整数型 成功返回1
     *
     * 注册失败返回-1 网络传输异常 返回0 未知异常
     */
    public native int Reglz(String userName, String passWord, String email, String qq, String agentid, String agentName);

}
