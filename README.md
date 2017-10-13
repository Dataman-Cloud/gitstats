# gitstats
git代码统计面板

## 依赖说明:
依赖 mongo ：需要mongo来存储数据

## 操作说明
> maven 打包后
  解压 zip文件 进入 bin目录 (需要 jdk1.8的环境)

### 直接运行
1.start.sh

### docker 运行的
> 1.先执行 bulid.sh 构建镜像
  2.执行 dockerRun.sh 运行镜像

## 注意

### 访问路径 
> 主页  域名:端口
  api页面 域名:端口/swagger/index.html

### 指定端口 
java -jar gitstats.tar --server.port=8888

### 指定mongoUrl
java -jar gitstats.tar --spring.data.mongodb.uri=mongodb://192.168.199.32:27027/gitstats
