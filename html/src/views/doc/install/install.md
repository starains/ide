### 打包

进入工程目录
使用 ` mvn install ` 安装
执行后在工程目录生成` release/版本号 `目录，复制版本目录下的文件到服务器。

**环境变量**

系统需要配置环境变量TEAMIDE_HOME指向工程安装目录，及 `TEAMIDE_HOME = /安装目录`

启动后将会生成目录
> + `$TEAMIDE_HOME/conf` //配置文件，端口，访问路径，JDBC等都在这个目录
> + `$TEAMIDE_HOME/conf/ide.conf`
> + `$TEAMIDE_HOME/log` //日志目录
> + `$TEAMIDE_HOME/conf/ide.log`
> + `$TEAMIDE_HOME/plugins` //插件
> + `$TEAMIDE_HOME/workspaces` //工作区
> + `$TEAMIDE_HOME/spaces` //空间，这个很重要，源码将会存储在这个目录

**启动&停止**

执行 `bin/start.sh` 启动Team IDE服务

执行 `bin/stop.sh` 停止Team IDE服务

直接jar启动

` java -jar ide.jar`

或启动带入安装目录和端口等

` java -jar ide.jar --TEAMIDE_HOME=/data/ide --port=8080`

- TEAMIDE_HOME //不配置环境变量则需要指定目录
- port=8080    //可以指定启动端口

**后台启动**

` nohup java -Dfile.encoding=UTF-8 -jar $TEAMIDE_HOME/ide.jar >$TEAMIDE_HOME/logs/start.log 2>&1 & echo $! > $TEAMIDE_HOME/ide.pid`
