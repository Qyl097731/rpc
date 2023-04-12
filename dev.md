# 开发文档

## 问题1

### 简述

RpcClientHandler 中 send进行远程调用的时候，channel空指针

### 解决方案

发现 channelRegistered 时候注册的channel，在后续客户端invokeRemote的时候，new 了一个新的RpcClientHandler，导致最后没有触发channelRegistered
channel也就没有保存.

- 问题2