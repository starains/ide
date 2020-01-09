# 欢迎使用在线开发工具 TeamIDE

**介绍**

TeamIDE是团队在线开发工具，用户可以在线编辑、运行项目，可以通过Git管理代码库。


**环境变量**

系统需要配置环境变量TEAMIDE_HOME
启动后将会生成目录
- `$TEAMIDE_HOME/conf          //配置文件，端口，访问路径，JDBC等都在这个目录`
- `$TEAMIDE_HOME/conf/ide.conf`
- `$TEAMIDE_HOME/log           //日志目录`
- `$TEAMIDE_HOME/conf/ide.log`
- `$TEAMIDE_HOME/plugins       //插件`
- `$TEAMIDE_HOME/workspaces     //工作区`
- `$TEAMIDE_HOME/spaces         //空间，这个很重要，源码将会存储在这个目录`

**启动**

` java -jar ide.jar`


` java -jar ide.jar --TEAMIDE_HOME=/data/ide --port=8080`

- TEAMIDE_HOME //不配置环境变量则需要指定目录
- port=8080    //可以指定启动端口


**后台启动**

` nohup java -Dfile.encoding=UTF-8 -jar $TEAMIDE_HOME/ide.jar >$TEAMIDE_HOME/logs/main.log 2>&1 & echo $! > $TEAMIDE_HOME/ide.pid`

