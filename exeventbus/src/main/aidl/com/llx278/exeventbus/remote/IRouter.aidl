// IRouter.aidl
package com.llx278.exeventbus.remote;

// Declare any non-default types here with import statements
import android.os.Bundle;
import com.llx278.exeventbus.remote.IReceiver;

interface IRouter {

    //向其他进程发送消息 throws UnExceptedAddressException
    // 当指定的address还没有被添加
    void send(String addrss, in Bundle msg);

    //添加一个用来接收消息的回调接口
    void addReceiver(in IReceiver receiver);

    //移除一个用来接收消息的回调接口
    void removeReceiver();

    //获得连接到服务端的所有的进程
    List<String> getAliveClient();

}
