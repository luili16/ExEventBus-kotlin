# ExEventBus-kotlin  

ExEventBus是一个轻量级的事件总线，但是它不仅可以在进程内发布，更可以将事件发布到其它进程去。  

## 如何使用

初始化
```
// 传入context，主要用来启动一个在进程间传递消息的service
ExEventBus.init(context)
```

订阅事件：  
```
@Subscriber(tag = "remote_test", remote = true)
fun method() {
	...
}
```

发布事件:  
```
val tag = "remote_test"
ExEventBus.remotePublish(tag = tag)
```

## 实现原理
通过一个独立的第三方进程作为一个server端，负责其他进程间消息的转发服务。
