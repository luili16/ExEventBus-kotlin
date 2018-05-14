// IDebug.aidl
package com.llx278.exeventbus;

// Declare any non-default types here with import statements

interface IDebug {
    void sendMsg(String address,String msg);
    String receivedMsg();
    int thisPid();
}
