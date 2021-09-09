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
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-tomcat</artifactId>
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
    mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
    ```
