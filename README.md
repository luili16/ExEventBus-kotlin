# ExEventBus-kotlin  

ExEventBus是一个轻量级的事件总线，但是它不仅可以在进程内发布，更可以将事件发布到其它进程去。  

发现了一个功能相似的项目(果然还是大厂的项目质量高): [Andromeda](https://github.com/iqiyi/Andromeda)

## 如何使用

添加一个转发消息的service
```
<service android:name="com.llx278.exeventbus.remote.RouterService"
  			 android:exported="false"
            android:process=":exeventbus"/>
```
目前此service需要在一个独立的进程里面运行，否则会产生bug(正在想办法解决)。

初始化
```
// 传入context，主要用来启动一个在进程间传递消息的RouterService
ExEventBus.init(context)
```

订阅事件：  
```
@Subscriber(tag = "remote_test", remote = true)
fun method() {
	...
}

@Subscriber(tag = "parameter_test_String", remote = true, type = Type.BLOCK)
    fun method2() : String {
        Log.d("main","RemoteService call method2")
        return "hello world"
    }
```

发布事件:  
```
val tag = "remote_test"
ExEventBus.remotePublish(tag = tag)

...

val tag = "parameter_test_String"
val returnType = String::class.qualifiedName!!
// 直接返回了订阅定法的返回值
// 这相当于将一个异步的操作变成了同步的操作
// 在远程方法未返回之前，会一直阻塞，直到超时
val retVal = ExEventBus.remotePublish(tag = tag, returnType = returnType，timeout = 1000)

```

## 实现原理
通过一个独立的第三方进程作为一个server端，负责其他进程间消息的转发服务。
