####  log4j2日志
- Log4j2 支持完全异步模式，也支持异步/同步混合模式，它们性能梯度为：完全异步模式 > 混合模式 > 同步模式。
  - 需要Log4j2的AsyncLogger的异步日志实现方式,需要引入Disruptor
- 需要发送邮件,需要引入spring-boot-starter-mail
- pom.xml
  ```xml
    <disruptor.version>3.4.2</disruptor.version>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <!--排除logging-->
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <!--log4j2 依赖-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>

    <!-- 引入disruptor并发框架 -->
    <dependency>
        <groupId>com.lmax</groupId>
        <artifactId>disruptor</artifactId>
        <version>${disruptor.version}</version>
    </dependency>

    <!--  SMTP  -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
  ```


- application.properties
  ```properties
  logging.config=classpath:log4j2.xml
  ```


---
- 完全异步模式
- 方式一
  ```java
  import org.springframework.boot.SpringApplication;
  import org.springframework.boot.autoconfigure.SpringBootApplication;

  @SpringBootApplication
  public class Application {

      public static void main(String[] args) {
          //使得日志输出使用异步处理，减小输出日志对性能的影响
          System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
          SpringApplication.run(Application.class, args);
      }
  }
  ```

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!--设置log4j2的自身log级别为warn-->
  <!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
  <!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，
      当设置成trace时，会看到log4j2内部各种详细输出-->
  <!--monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数-->
  <configuration status="warn" monitorInterval="5">
      <Properties>
          <!-- 日志模板 MM-dd HH:mm:ss:SSS-->
          <Property name="log_pattern" value="[%d{MM-dd HH:mm:ss}] [%p] - %l - %m%n"/>
          <!-- 存储日志文件路径 ${sys:user.home} 用户目录-->
          <Property name="file_path" value="./logs"/>
          <!-- 日志文件的最大容量，超过该值就进行备份 -->
          <Property name="file_max_size" value="1MB"/>
          <!-- 备份的文件夹名称 -->
          <Property name="backup_folder" value="$${date:yyyy-MM}"/>
          <!-- 备份文件的后缀 -->
          <Property name="backup_file_suffix" value="-%d{yyyy-MM-dd}-%i.log"/>

          <Property name="logFileName">test.log</Property>
      </Properties>

      <!--定义appender-->
      <appenders>
          <!--console :控制台输出的配置-->
          <console name="Console" target="SYSTEM_OUT">
              <!--PatternLayout :输出日志的格式,LOG4J2定义了输出代码,详见第二部分-->
              <PatternLayout pattern="${log_pattern}"/>
          </console>
          <!--File :同步输出日志到本地文件-->
          <!--append="false" :根据其下日志策略,每次清空文件重新输入日志,可用于测试-->
          <File name="log" fileName="${file_path}/${logFileName}" append="false">
              <PatternLayout pattern="${log_pattern}"/>
          </File>

          <!--
          该RollingFile存储INFO级别的日志，
          默认存储到 fileName 文件中
          超过SizeBasedTriggeringPolicy的设定值，则存储到 filePattern 文件中
          -->
          <!-- ${sys:user.home} :项目路径 fileName="${sys:user.home}/logs/info.log"-->
          <RollingFile name="RollingFileInfo" fileName="${file_path}/info.log"
                      filePattern="${file_path}/${backup_folder}/info${backup_file_suffix}">
              <Filters>
                  <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
                  <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
                  <ThresholdFilter level="WARN" onMatch="DENY" onMismatch="NEUTRAL"/>
              </Filters>
              <!-- 写入日志文件的模板 -->
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <!-- TimeBasedTriggeringPolicy :时间滚动策略,默认0点小时产生新的文件,interval="6" : 自定义文件滚动时间间隔,每隔6小时产生新文件, modulate="true" : 产生文件是否以0点偏移时间,即6点,12点,18点,0点-->
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
              <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件，超过该数量，会滚动删除前面的记录 -->
              <DefaultRolloverStrategy max="20"/>
          </RollingFile>

          <RollingFile name="RollingFileWarn" fileName="${file_path}/warn.log"
                      filePattern="${file_path}/${backup_folder}/warn${backup_file_suffix}">
              <Filters>
                  <ThresholdFilter level="WARN" onMatch="ACCEPT" onMismatch="DENY"/>
                  <ThresholdFilter level="ERROR" onMatch="DENY" onMismatch="NEUTRAL"/>
              </Filters>
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
          </RollingFile>

          <RollingFile name="RollingFileError" fileName="${file_path}/error.log"
                      filePattern="${file_path}/${backup_folder}/error${backup_file_suffix}">
              <ThresholdFilter level="ERROR"/>
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
          </RollingFile>

      </appenders>

      <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
      <loggers>
          <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
          <!--Logger节点用来单独指定日志的形式，name为包路径,比如要为org.springframework包下所有日志指定为INFO级别等。 -->
          <logger name="org.springframework" level="INFO" additivity="false">
              <AppenderRef ref="Console"/>
          </logger>
          <logger name="org.mybatis" level="INFO"  additivity="false">
              <AppenderRef ref="Console"/>
          </logger>
          <!-- Root节点用来指定项目的根日志，如果没有单独指定Logger，那么就会默认使用该Root日志输出 -->
          <root level="INFO"  includeLocation="true">
              <appender-ref ref="Console"/>
              <appender-ref ref="RollingFileInfo"/>
              <appender-ref ref="RollingFileWarn"/>
              <appender-ref ref="RollingFileError"/>
          </root>
      </loggers>
  </configuration>
  ```

---
- 方式二
  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <!--设置log4j2的自身log级别为warn-->
  <!--日志级别以及优先级排序: OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL -->
  <!--Configuration后面的status，这个用于设置log4j2自身内部的信息输出，可以不设置，
      当设置成trace时，会看到log4j2内部各种详细输出-->
  <!--monitorInterval：Log4j能够自动检测修改配置 文件和重新配置本身，设置间隔秒数-->
  <configuration status="warn" monitorInterval="5">
      <Properties>
          <!-- 日志模板 MM-dd HH:mm:ss:SSS-->
          <Property name="log_pattern" value="[%d{MM-dd HH:mm:ss}] [%p] - %l - %m%n"/>
          <!-- 存储日志文件路径 ${sys:user.home} 用户目录-->
          <Property name="file_path" value="./logs"/>
          <!-- 日志文件的最大容量，超过该值就进行备份 -->
          <Property name="file_max_size" value="1MB"/>
          <!-- 备份的文件夹名称 -->
          <Property name="backup_folder" value="$${date:yyyy-MM}"/>
          <!-- 备份文件的后缀 -->
          <Property name="backup_file_suffix" value="-%d{yyyy-MM-dd}-%i.log"/>

          <Property name="logFileName">test.log</Property>
      </Properties>

      <!--定义appender-->
      <appenders>
          <!--console :控制台输出的配置-->
          <console name="Console" target="SYSTEM_OUT">
              <!--PatternLayout :输出日志的格式,LOG4J2定义了输出代码,详见第二部分-->
              <PatternLayout pattern="${log_pattern}"/>
          </console>
          <!--File :同步输出日志到本地文件-->
          <!--append="false" :根据其下日志策略,每次清空文件重新输入日志,可用于测试-->
          <File name="log" fileName="${file_path}/${logFileName}" append="false">
              <PatternLayout pattern="${log_pattern}"/>
          </File>

          <!--
          该RollingFile存储INFO级别的日志，
          默认存储到 fileName 文件中
          超过SizeBasedTriggeringPolicy的设定值，则存储到 filePattern 文件中
          -->
          <!-- ${sys:user.home} :项目路径 fileName="${sys:user.home}/logs/info.log"-->
          <RollingFile name="RollingFileInfo" fileName="${file_path}/info.log"
                      filePattern="${file_path}/${backup_folder}/info${backup_file_suffix}">
              <Filters>
                  <!--控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->
                  <ThresholdFilter level="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
                  <ThresholdFilter level="WARN" onMatch="DENY" onMismatch="NEUTRAL"/>
              </Filters>
              <!-- 写入日志文件的模板 -->
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <!-- TimeBasedTriggeringPolicy :时间滚动策略,默认0点小时产生新的文件,interval="6" : 自定义文件滚动时间间隔,每隔6小时产生新文件, modulate="true" : 产生文件是否以0点偏移时间,即6点,12点,18点,0点-->
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
              <!-- DefaultRolloverStrategy属性如不设置，则默认为最多同一文件夹下7个文件，超过该数量，会滚动删除前面的记录 -->
              <DefaultRolloverStrategy max="20"/>
          </RollingFile>

          <RollingFile name="RollingFileWarn" fileName="${file_path}/warn.log"
                      filePattern="${file_path}/${backup_folder}/warn${backup_file_suffix}">
              <Filters>
                  <ThresholdFilter level="WARN" onMatch="ACCEPT" onMismatch="DENY"/>
                  <ThresholdFilter level="ERROR" onMatch="DENY" onMismatch="NEUTRAL"/>
              </Filters>
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
          </RollingFile>

          <RollingFile name="RollingFileError" fileName="${file_path}/error.log"
                      filePattern="${file_path}/${backup_folder}/error${backup_file_suffix}">
              <ThresholdFilter level="ERROR"/>
              <PatternLayout pattern="${log_pattern}"/>
              <Policies>
                  <TimeBasedTriggeringPolicy/>
                  <SizeBasedTriggeringPolicy size="${file_max_size}"/>
              </Policies>
          </RollingFile>
      </appenders>

      <!--然后定义logger，只有定义了logger并引入的appender，appender才会生效-->
      <loggers>
          <!--过滤掉spring和mybatis的一些无用的DEBUG信息-->
          <!--Logger节点用来单独指定日志的形式，name为包路径,比如要为org.springframework包下所有日志指定为INFO级别等。 -->
          <logger name="org.springframework" level="INFO" additivity="false">
              <AppenderRef ref="Console"/>
          </logger>
          <logger name="org.mybatis" level="INFO"  additivity="false">
              <AppenderRef ref="Console"/>
          </logger>
          <!-- Root节点用来指定项目的根日志，如果没有单独指定Logger，那么就会默认使用该Root日志输出 -->
          <root level="INFO"  includeLocation="true">
              <appender-ref ref="Console"/>
              <appender-ref ref="RollingFileInfo"/>
              <appender-ref ref="RollingFileWarn"/>
              <appender-ref ref="RollingFileError"/>
          </root>

          <!--AsyncLogger :异步日志,LOG4J有三种日志模式,全异步日志,混合模式,同步日志,性能从高到底,线程越多效率越高,也可以避免日志卡死线程情况发生-->
          <!--additivity="false" : additivity设置事件是否在root logger输出，为了避免重复输出，可以在Logger 标签下设置additivity为”false”-->
          <AsyncLogger name="AsyncLogger" level="info" includeLocation="true" additivity="false">
              <appender-ref ref="Console"/>
            <appender-ref ref="RollingFileInfo"/>
              <appender-ref ref="RollingFileWarn"/>
              <appender-ref ref="RollingFileError"/>
          </AsyncLogger>

      </loggers>
  </configuration>
  ```
