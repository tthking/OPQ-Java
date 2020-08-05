# IOTQQ-Java

[IOTQQ项目地址](https://github.com/IOTQQ/IOTQQ/)


这个项目在于让使用者更方便的使用IOTQQ进行插件开发

功能暂未完全实现


## 食用方法

```Java
IOTQQ iotqq = IOTQQBuilder.builder()
                .setURL("IOTQQ地址,例如127.0.0.1:8888,不带http")
                .setQQ(1234567890L)// 机器人QQ号
                .build();
        iotqq.init("cn.lliiooll");// 开发包名, 用于自动注册指令和监听器用
```
