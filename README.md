# Notification Monitor
find a way to take a shit
## 需要提到的事情
食用时尽量给手机后台运行的权限，不然黑屏和放后台的时候监控不到也发送不了  
不过开启之后想关闭需要进应用详情页才能强制关闭
## 运行
电脑端 -> ServerSocket  
手机端 -> Socket  

打开app后主页面是灰色的，最下方有一个长不溜的按钮，按下去进通知权限页面开启权限就行，这时再回去主页就变成黄色背景的了  
每个通知被抓下来之后会在主页放着，抓取时会自动发送，也可以长按被抓下来的通知重新发送一次(测试时用)
按setip按钮可以设置ip和端口

电脑端运行:  
```shell
java -jar NotifySend.jar
```
