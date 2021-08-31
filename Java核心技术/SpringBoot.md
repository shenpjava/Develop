### SpringBoot
File->New->Project..->Spring Initializr->在Custom中输入:https://start.aliyun.com
- Spring Boot 
  - <version>2.3.7.RELEASE</version>
- 开发工具
  - Spring Boot DevTools
- Web
  - Spring Web
- 关系型数据库
  - MyBatis Framework


---
- pom.xml
```xml
  <mybatis-spring.version>2.1.4</mybatis-spring.version>

  <dependency>
      <groupId>org.mybatis.spring.boot</groupId>
      <artifactId>mybatis-spring-boot-starter</artifactId>
      <version>${mybatis-spring.version}</version>
  </dependency>

  <!--SpringBoot Web功能起步依赖-->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
      <exclusions>
          <!--排除tomcat依赖-->
          <exclusion>
              <artifactId>spring-boot-starter-tomcat</artifactId>
              <groupId>org.springframework.boot</groupId>
          </exclusion>
      </exclusions>
  </dependency>

  <!--undertow容器(替换tomcat的web容器)-->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-undertow</artifactId>
  </dependency>
```

- application.properties
```properties
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://127.0.0.1:1433;DatabaseName=xx
spring.datasource.username=xx
spring.datasource.password=xx

#mybatis
mybatis.mapper-locations=classpath:mappers/*xml
mybatis.type-aliases-package=com.xx.mapper
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

- MyBatisConfig
```java
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.xx.mapper")
public class MyBatisConfig {

}
```


---
### 数据库连接池
#### druid
- pom.xml
  ```xml
    <druid.version>1.1.22</druid.version>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>${druid.version}</version>
    </dependency>
  ```

- application.properties
  ```properties
  spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
  #druid
  # 指定数据源类型
  spring.datasource.druid.db-type=com.alibaba.druid.pool.DruidDataSource
  # 配置初始化大小、最小、最大
  spring.datasource.druid.initial-size=5
  spring.datasource.druid.min-idle=10
  spring.datasource.druid.max-active=20
  # 配置获取连接等待超时的时间(单位：毫秒)
  spring.datasource.druid.max-wait=10000
  # 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
  spring.datasource.druid.time-between-eviction-runs-millis=2000
  # 配置一个连接在池中最小生存的时间，单位是毫秒
  spring.datasource.druid.min-evictable-idle-time-millis=600000
  spring.datasource.druid.max-evictable-idle-time-millis=900000
  # 用来测试连接是否可用的SQL语句,默认值每种数据库都不相同
  spring.datasource.druid.validation-query=select 1
  # 应用向连接池申请连接，并且testOnBorrow为false时，连接池将会判断连接是否处于空闲状态，如果是，则验证这条连接是否可用
  spring.datasource.druid.test-while-idle=true
  # 如果为true，默认是false，应用向连接池申请连接时，连接池会判断这条连接是否是可用的
  spring.datasource.druid.test-on-borrow=false
  # 如果为true（默认false），当应用使用完连接，连接池回收连接的时候会判断该连接是否还可用
  spring.datasource.druid.test-on-return=false
  # 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle
  spring.datasource.druid.pool-prepared-statements=true
  # 要启用PSCache，必须配置大于0，当大于0时， poolPreparedStatements自动触发修改为true，
  # 在Druid中，不会存在Oracle下PSCache占用内存过多的问题，
  # 可以把这个数值配置大一些，比如说100
  spring.datasource.druid.max-open-prepared-statements=20
  # 连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作
  spring.datasource.druid.keep-alive=true
  # Spring 监控，利用aop 对指定接口的执行时间，jdbc数进行记录
  spring.datasource.druid.aop-patterns="com.shenp.entity.*"
  ########### 启用内置过滤器（第一个 stat必须，否则监控不到SQL）##########
  spring.datasource.druid.filters=stat,wall,log4j2
  #spring.datasource.druid.filters=stat
  # 自己配置监控统计拦截的filter
  # 开启druiddatasource的状态监控
  spring.datasource.druid.filter.stat.enabled=true
  spring.datasource.druid.filter.stat.db-type=sqlserver
  # 开启慢sql监控，超过2s 就认为是慢sql，记录到日志中
  spring.datasource.druid.filter.stat.log-slow-sql=true
  spring.datasource.druid.filter.stat.slow-sql-millis=2000
  # 日志监控，使用slf4j 进行日志输出
  spring.datasource.druid.filter.slf4j.enabled=true
  spring.datasource.druid.filter.slf4j.statement-log-error-enabled=true
  spring.datasource.druid.filter.slf4j.statement-create-after-log-enabled=false
  spring.datasource.druid.filter.slf4j.statement-close-after-log-enabled=false
  spring.datasource.druid.filter.slf4j.result-set-open-after-log-enabled=false
  spring.datasource.druid.filter.slf4j.result-set-close-after-log-enabled=false
  ########## 配置WebStatFilter，用于采集web关联监控的数据 ##########
  # 启动 StatFilter
  spring.datasource.druid.web-stat-filter.enabled=true
  # 过滤所有url
  spring.datasource.druid.web-stat-filter.url-pattern=/*
  # 排除一些不必要的url
  spring.datasource.druid.web-stat-filter.exclusions="*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
  # 开启session统计功能
  spring.datasource.druid.web-stat-filter.session-stat-enable=true
  # session的最大个数,默认100
  spring.datasource.druid.web-stat-filter.session-stat-max-count=1000
  ########## 配置StatViewServlet（监控页面），用于展示Druid的统计信息 ##########
  # 启用StatViewServlet
  spring.datasource.druid.stat-view-servlet.enabled=true
  # 访问内置监控页面的路径，内置监控页面的首页是/druid/index.html
  #spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
  # 不允许清空统计数据,重新计算
  spring.datasource.druid.stat-view-servlet.reset-enable=false
  # 配置监控页面访问密码
  spring.datasource.druid.stat-view-servlet.login-username=root
  spring.datasource.druid.stat-view-servlet.login-password=root
  # 允许访问的地址，如果allow没有配置或者为空，则允许所有访问
  spring.datasource.druid.stat-view-servlet.allow=127.0.0.1
  # 拒绝访问的地址，deny优先于allow，如果在deny列表中，就算在allow列表中，也会被拒绝
  spring.datasource.druid.stat-view-servlet.deny=
  ```

- DruidConfig
  ```java
  import java.sql.SQLException;
  import javax.sql.DataSource;

  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.boot.web.servlet.FilterRegistrationBean;
  import org.springframework.boot.web.servlet.ServletRegistrationBean;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.context.annotation.Primary;
  import com.alibaba.druid.pool.DruidDataSource;
  import com.alibaba.druid.support.http.StatViewServlet;
  import com.alibaba.druid.support.http.WebStatFilter;

  @SuppressWarnings("all")
  @Configuration
  public class DruidConfig {
      private static final Logger log = LoggerFactory.getLogger(DruidConfig.class);

      @Value("${spring.datasource.url}")
      private String dbUrl;

      @Value("${spring.datasource.username}")
      private String username;

      @Value("${spring.datasource.password}")
      private String password;

      @Value("${spring.datasource.driver-class-name}")
      private String driverClassName;

      @Value("${spring.datasource.druid.initial-size}")
      private int initialSize;

      @Value("${spring.datasource.druid.min-idle}")
      private int minIdle;

      @Value("${spring.datasource.druid.max-active}")
      private int maxActive;

      @Value("${spring.datasource.druid.max-wait}")
      private int maxWait;

      @Value("${spring.datasource.druid.time-between-eviction-runs-millis}")
      private int timeBetweenEvictionRunsMillis;

      @Value("${spring.datasource.druid.min-evictable-idle-time-millis}")
      private int minEvictableIdleTimeMillis;

      @Value("${spring.datasource.druid.validation-query}")
      private String validationQuery;

      @Value("${spring.datasource.druid.test-while-idle}")
      private boolean testWhileIdle;

      @Value("${spring.datasource.druid.test-on-borrow}")
      private boolean testOnBorrow;

      @Value("${spring.datasource.druid.test-on-return}")
      private boolean testOnReturn;

      @Value("${spring.datasource.druid.pool-prepared-statements}")
      private boolean poolPreparedStatements;

      @Value("${spring.datasource.druid.max-open-prepared-statements}")
      private int maxPoolPreparedStatementPerConnectionSize;

      @Value("${spring.datasource.druid.filters}")
      private String filters;

  //    @Value("{spring.datasource.connectionProperties}")
  //    private String connectionProperties;

      @Bean(initMethod = "init", destroyMethod = "close")
      @Primary
      public DataSource dataSource() {
          DruidDataSource datasource = new DruidDataSource();

          datasource.setUrl(this.dbUrl);
          datasource.setUsername(username);
          datasource.setPassword(password);
          datasource.setDriverClassName(driverClassName);

          /** configuration */
          datasource.setInitialSize(initialSize);
          datasource.setMinIdle(minIdle);
          datasource.setMaxActive(maxActive);
          datasource.setMaxWait(maxWait);
          datasource
                  .setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
          datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
          datasource.setValidationQuery(validationQuery);
          datasource.setTestWhileIdle(testWhileIdle);
          datasource.setTestOnBorrow(testOnBorrow);
          datasource.setTestOnReturn(testOnReturn);
          datasource.setPoolPreparedStatements(poolPreparedStatements);
          datasource
                  .setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
          try {
              datasource.setFilters(filters);
          } catch (SQLException e) {
              log.error("druid configuration initialization filter", e);
          }
  //        datasource.setConnectionProperties(connectionProperties);
          return datasource;
      }

      /**
      * http://127.0.0.1:8080/monitor/druid/login.html
      *
      * @Title: druidServlet
      * @Description: 注册一个StatViewServlet 相当于在web.xml中声明了一个servlet
      * @return: ServletRegistrationBean
      */
      @Bean
      public ServletRegistrationBean druidServlet() {
          ServletRegistrationBean reg = new ServletRegistrationBean();
          reg.setServlet(new StatViewServlet());
          reg.addUrlMappings("/monitor/druid/*");
          /** 白名单 */
          reg.addInitParameter("allow", "127.0.0.1");
          /** IP黑名单(共同存在时，deny优先于allow) */
          // reg.addInitParameter("deny", "192.168.2.105");
          /** /druid/login.html登录时账号密码 */
          reg.addInitParameter("loginUsername", "root");
          reg.addInitParameter("loginPassword", "root");
          /** 是否能够重置数据 禁用HTML页面上的“Reset All”功能 */
          reg.addInitParameter("resetEnable", "false");
          return reg;
      }

      /**
      * 注册一个：filterRegistrationBean 相当于在web.xml中声明了一个Filter
      */
      @Bean
      public FilterRegistrationBean druidStatFilter() {
          FilterRegistrationBean druidStatFilter = new FilterRegistrationBean();
          druidStatFilter.setFilter(new WebStatFilter());
          /** 添加过滤规则. */
          druidStatFilter.addUrlPatterns("/*");
          /** 监控选项滤器 */
          druidStatFilter.addInitParameter("DruidWebStatFilter", "/*");
          /** 添加不需要忽略的格式信息. */
          druidStatFilter.addInitParameter("exclusions",
                  "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/monitor/druid/*");
          /** 配置profileEnable能够监控单个url调用的sql列表 */
          druidStatFilter.addInitParameter("profileEnable", "true");
          /** 当前的cookie的用户 */
          druidStatFilter.addInitParameter("principalCookieName", "USER_COOKIE");
          /** 当前的session的用户 */
          druidStatFilter
                  .addInitParameter("principalSessionName", "USER_SESSION");
          return druidStatFilter;
      }
  }
  ```

#### HikariCP(推荐)




---
### Swagger-UI
- pom.xml
  ```xml
    <springfox.version>3.0.0</springfox.version>

    <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-boot-starter</artifactId>
        <version>${springfox.version}</version>
    </dependency>
  ```

- SwaggerConfig
  ```java
  import io.swagger.annotations.ApiOperation;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import springfox.documentation.builders.ApiInfoBuilder;
  import springfox.documentation.builders.PathSelectors;
  import springfox.documentation.builders.RequestHandlerSelectors;
  import springfox.documentation.oas.annotations.EnableOpenApi;
  import springfox.documentation.service.ApiInfo;
  import springfox.documentation.spi.DocumentationType;
  import springfox.documentation.spring.web.plugins.Docket;

  @Configuration
  @EnableOpenApi
  public class SwaggerConfig {
      @Bean
      public Docket createRestApi() {
          return new Docket(DocumentationType.OAS_30)
                  .apiInfo(apiInfo())
                  .select()
                  .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                  .paths(PathSelectors.any())
                  .build();
      }

      private ApiInfo apiInfo() {
          return new ApiInfoBuilder()
                  .title("Restful API接口")
                  .description("")
                  .version("1.0")
                  .build();
      }

  }
  ```

- 地址
  - http://localhost:8080/swagger-ui/index.html


---
### 日志
#### log4j日志
- pom.xml
  ```xml
  <spring-boot-log4j.version>1.3.8.RELEASE</spring-boot-log4j.version>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <!--排除logback-->
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j</artifactId>
        <version>${spring-boot-log4j.version}</version>
    </dependency>
  ```


  - application.properties
  ```properties
  mybatis.configuration.log-impl=org.apache.ibatis.logging.log4j.Log4jImpl
  ```


  - log4j.properties
  ```properties
  #定义根节点
  log4j.rootLogger=DEBUG,error,CONSOLE,info
  #设置控制台打印
  log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
  #设置为格式化打印 PatternLayout
  log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
  log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss,SSS} %p [%t] %C.%M(%L) | %m%n
  #设置info级别的日志
  log4j.logger.info=info
  #输出到日志文件
  log4j.appender.info=org.apache.log4j.DailyRollingFileAppender
  log4j.appender.info.layout=org.apache.log4j.PatternLayout
  log4j.appender.info.layout.ConversionPattern=[%d{MM-dd HH:mm:ss}] [%p] [%c:%L] %m%n
  #日期文件名格式化
  log4j.appender.info.datePattern='.'yyyy-MM-dd
  log4j.appender.info.Threshold=info
  #是否追加
  log4j.appender.info.append=true
  #文件存放位置
  log4j.appender.info.File=./log/info.log

  #error
  log4j.logger.error=error
  log4j.appender.error=org.apache.log4j.DailyRollingFileAppender
  log4j.appender.error.layout=org.apache.log4j.PatternLayout
  log4j.appender.error.layout.ConversionPattern=[%d{MM-dd HH:mm:ss}] [%p] [%c:%L] %m%n
  log4j.appender.error.datePattern='.'yyyy-MM-dd
  log4j.appender.error.Threshold=error
  log4j.appender.error.append=true
  log4j.appender.error.File=./log/error.log

  #DEBUG
  log4j.logger.DEBUG=DEBUG
  log4j.appender.DEBUG=org.apache.log4j.DailyRollingFileAppender
  log4j.appender.DEBUG.layout=org.apache.log4j.PatternLayout
  log4j.appender.DEBUG.layout.ConversionPattern=[%d{MM-dd HH:mm:ss}] [%p] [%c:%L] %m%n
  log4j.appender.DEBUG.datePattern='.'yyyy-MM-dd
  log4j.appender.DEBUG.Threshold=DEBUG
  log4j.appender.DEBUG.append=true
  log4j.appender.DEBUG.File=./log/dubug.log
  ```

####  log4j2日志(推荐)
  - 需要Log4j2的AsyncLogger的异步日志实现方式，需要引入Disruptor
  - pom.xml
    ```xml
      <disruptor.version>3.4.2</disruptor.version>

      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
          <exclusions>
              <!--排除logback-->
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

      <dependency>
          <groupId>com.lmax</groupId>
          <artifactId>disruptor</artifactId>
          <version>${disruptor.version}</version>
      </dependency>
    ```

  - log4j2.xml
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
        </Properties>

        <!--定义appender-->
        <appenders>

            <!--控制台的输出配置-->
            <console name="Console" target="SYSTEM_OUT">
                <!--输出日志的格式-->
                <PatternLayout pattern="${log_pattern}"/>
            </console>

            <!-- 所有级别的日志会存入该文件，当append属性设置为false时，每次启动程序会自动清空 -->
            <!--        <File name="AllLog" fileName="${file_path}/all_log.log" append="false">
                        &lt;!&ndash; <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/> &ndash;&gt;
                        <PatternLayout pattern="${log_pattern}"/>
                    </File>-->

            <!--
            该RollingFile存储INFO级别的日志，
            默认存储到 fileName 文件中
            超过SizeBasedTriggeringPolicy的设定值，则存储到 filePattern 文件中
            -->
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

        <!-- 只有定义了logger并使用appender-ref，appender才会生效 -->
        <loggers>
            <!--过滤掉spring和hibernate的一些无用的debug信息-->
            <logger name="org.springframework" level="INFO"/>
            <logger name="org.mybatis" level="INFO">
                <!-- 添加如下设置，控制台会再打印一次 -->
                <AppenderRef ref="Console"/>
            </logger>
            <root level="INFO">
                <appender-ref ref="Console"/>
                <appender-ref ref="RollingFileInfo"/>
                <appender-ref ref="RollingFileWarn"/>
                <appender-ref ref="RollingFileError"/>
                <!--            <appender-ref ref="AllLog"/>-->
            </root>
        </loggers>
    </configuration>
    ```

  - application.properties
    ```properties
    logging.config=classpath:log4j2.xml
    ```


---
### Redis
- pom.xml
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
  ```

- application.properties
  ```properties
  #redis
  spring.redis.host=localhost
  spring.redis.database=0
  spring.redis.port=6379
  spring.redis.password=null
  spring.redis.jedis.pool.max-active=8
  spring.redis.jedis.pool.max-wait=-1ms
  spring.redis.jedis.pool.max-idle=8
  spring.redis.jedis.pool.min-idle=0
  spring.redis.timeout=3000ms
  ```

- RedisService
  ```java
  /**
  * redis操作Service,
  * 对象和数组都以json形式进行存储
  */
  public interface RedisService {
      /**
      * 存储数据
      */
      void set(String key, String value);

      /**
      * 获取数据
      */
      String get(String key);

      /**
      * 设置超期时间
      */
      boolean expire(String key, long expire);

      /**
      * 删除数据
      */
      void remove(String key);

      /**
      * 自增操作
      *
      * @param delta 自增步长
      */
      Long increment(String key, long delta);
  }
  ```

- RedisServiceImpl
  ```java
  import com.shenp.service.RedisService;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.data.redis.core.StringRedisTemplate;
  import org.springframework.stereotype.Service;

  import java.util.concurrent.TimeUnit;

  @Service
  public class RedisServiceImpl implements RedisService {

      @Autowired
      private StringRedisTemplate stringRedisTemplate;

      @Override
      public void set(String key, String value) {
          stringRedisTemplate.opsForValue().set(key, value);
      }

      @Override
      public String get(String key) {
          return stringRedisTemplate.opsForValue().get(key);
      }

      @Override
      public boolean expire(String key, long expire) {
          return stringRedisTemplate.expire(key, expire, TimeUnit.SECONDS);
      }

      @Override
      public void remove(String key) {
          stringRedisTemplate.delete(key);
      }

      @Override
      public Long increment(String key, long delta) {
          return stringRedisTemplate.opsForValue().increment(key, delta);
      }
  }
  ```

---
### SpringSecurity




---
### 定时任务工具
#### SpringTask
- SpringTaskConfig
  ```java
  import org.springframework.context.annotation.Configuration;
  import org.springframework.scheduling.annotation.EnableScheduling;

  /**
  * 定时任务配置
  */
  @Configuration
  //开启异步事件的支持
  @EnableAsync
  @EnableScheduling
  public class SpringTaskConfig {
  }
  ```

- OrderTimeOutCancelTask
  ```java
  import org.slf4j.Logger;
  import org.slf4j.LoggerFactory;
  import org.springframework.scheduling.annotation.Scheduled;
  import org.springframework.stereotype.Component;

  /**
  * 订单超时取消并解锁库存的定时器
  */
  @Component
  public class OrderTimeOutCancelTask {
      private Logger logger = LoggerFactory.getLogger(OrderTimeOutCancelTask.class);

      /**
      * cron表达式：Seconds Minutes Hours DayofMonth Month DayofWeek [Year]
      * 每10分钟扫描一次，扫描设定超时时间之前下的订单，如果没支付则取消该订单
      */
      // 异步执行
      @Async
      @Scheduled(cron = "0 0/10 * ? * ?")
      private void cancelTimeOutOrder() {
          // TODO: 2019/5/3 此处应调用取消订单的方法，具体查看mall项目源码
          logger.info("取消订单，并根据sku编号释放锁定库存");
      }
  }
  ```


####  Quartz(推荐)
- pom.xml
  ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-quartz</artifactId>
    </dependency>
  ```

- TestTask1
  ```java
  import org.quartz.JobExecutionContext;
  import org.quartz.JobExecutionException;
  import org.springframework.scheduling.quartz.QuartzJobBean;

  import java.text.SimpleDateFormat;
  import java.util.Date;


  public class TestTask1 extends QuartzJobBean {

      @Override
      protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          System.out.println("TestQuartz01----" + sdf.format(new Date()));
      }
  }
  ```

- TestTask2
  ```java
  import org.quartz.JobExecutionContext;
  import org.quartz.JobExecutionException;
  import org.springframework.scheduling.quartz.QuartzJobBean;

  import java.text.SimpleDateFormat;
  import java.util.Date;


  public class TestTask2 extends QuartzJobBean {
      @Override
      protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
          System.out.println("TestQuartz02----" + sdf.format(new Date()));
      }
  }
  ```

- QuartzConfig
  ```java
  import org.quartz.CronScheduleBuilder;
  import org.quartz.JobBuilder;
  import org.quartz.JobDetail;
  import org.quartz.SimpleScheduleBuilder;
  import org.quartz.Trigger;
  import org.quartz.TriggerBuilder;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;


  @Configuration
  public class QuartzConfig {

      @Bean
      public JobDetail testQuartz1() {
          return JobBuilder.newJob(TestTask1.class).withIdentity("testTask1").storeDurably().build();
      }

      @Bean
      public Trigger testQuartzTrigger1() {
          //5秒执行一次
          SimpleScheduleBuilder scheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                  .withIntervalInSeconds(5)
                  .repeatForever();
          return TriggerBuilder.newTrigger().forJob(testQuartz1())
                  .withIdentity("testTask1")
                  .withSchedule(scheduleBuilder)
                  .build();
      }

      @Bean
      public JobDetail testQuartz2() {
          return JobBuilder.newJob(TestTask2.class).withIdentity("testTask2").storeDurably().build();
      }

      @Bean
      public Trigger testQuartzTrigger2() {
          //cron方式，每隔5秒执行一次
          return TriggerBuilder.newTrigger().forJob(testQuartz2())
                  .withIdentity("testTask2")
                  .withSchedule(CronScheduleBuilder.cronSchedule("*/5 * * * * ?"))
                  .build();
      }
      
  }
  ```