开发文档

# rpc-http

## 问题1

### 简述

在连接池的注册时候，通过List直接存不同的端点的connection，在随机取得时候并不能保证取到的connection包含
了将要调用的服务。

### 解决方案

### 时间线

2023/4/12-

## 问题1

### 简述

RpcClientHandler 中 send进行远程调用的时候，channel空指针

### 解决方案

发现 channelRegistered 时候注册的channel，在后续客户端invokeRemote的时候，new 了一个新的RpcClientHandler，导致最后没有触发channelRegistered
channel也就没有保存.

### 时间线

2023/4/12-

## 问题2

### 简述

性能差，单线程阻塞执行

如果是在同一个线程中执行，channelRead0返回前会一直阻塞；如果是在不同的线程中执行，channelWrite会把请求发送到发送缓冲区，然后立即返回，不会阻塞，而channelRead0在收到响应后才会返回。一般而言，Netty使用的是事件驱动模型，在一个线程中执行，因此channel.writeAndFlush()会一直阻塞到channelRead0返回。但是，如果你在代码中使用了异步IO或者采用了不同的事件驱动模型，那么这个问题的答案就会有所不同。

### 解决方案

异步执行

### 时间线

2023/4/12-

## 问题3

### 简述

客户端请求发出之后，服务器解码，始终报错空指针

### 解决方案

kryo反序列化的时候 readObjectOrNull 换成 readObject

### 时间线

2023/4/13 - 2023/4/13

## 问题4

### 简述

Kryo序列化的时候，如果并发量很大，频发的new尤其需要注意OOM

### 解决方案

### 时间线

2023/4/13 - 
