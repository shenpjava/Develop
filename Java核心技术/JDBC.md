#### Connection
- 创建一个Statement对象,执行不带参数的sql查询和更新
```java
Statement stat=conn.createStatement();
```

- 返回一个含预编译语句的PreparedStatement对象
```java
//Sql语句可以包含一个或者多个由?字符指明的参数占位符
PreparedStatement preStat=conn.prepareStatement(sql);
```

- 立即关闭当前的连接,并释放由它所创建的JDBC资源
```java
conn.close();
```

#### Statement
- 执行SELECT语句,并返回一个用于查看查询结果的ResultSet对象
```java
ResultSet   stat.executeQuery(sql);
```

- 执行CREATE、DROP TABLE或INSERT、UPDATE、DELETE,返回受影响的行数,如果是没有更新计数的语句,则返回0
```java
Integer   stat.executeUpdate(sql);
```

- 执行任意的sql语句,可能会产生多个结果集和更新计数
```java
Boolean   stat.execute(sql);
```

- 返回前一条查询语句的结果集
```java
ResultSet   stat.getResultSet();
```

- 返回前一条更新语句影响的行数
```java
Integer   stat.getUpdateCount();
```

- 关闭Statement对象以及它对应的结果集
```java
void   stat.close();
```

#### ResultSet
- 将结果集中当前行向前移动一行.如果已经到达最后一行的后面,则返回false。注意,初始情况下必须调用该方法才能转到第一行.
```java
ResultSet   resultSet.next()
```

- 用给定的列序号或列标签返回或更新该列的值,并将值转换成指定的类型.列标签是Sql的AS子句中指定的标签,在没有使用AS时,它就是列名
```java
//Xxx指数据类型,int、double、String、Date、BigDecimal等
//columnLabel 列名不区分大小写
Xxx   getXxx(int columnNamber)
Xxx   getXxx(String columnLabel)
```

- 立即关闭当前的结果集
```java
void   close();
```

#### PreparedStatement
- 设置第n个参数值为x
```java
//Xxx指int、double、String、Date、BigDecimal等数据类型
void   preStat.setXxx(int n,Xxx x);
```

- 清除预备语句中的所有当前参数
```java
void   preStat.clearParameters();
```

- 执行预备Sql查询,并返回一个ResultSet对象
```java
ResultSet   preStat.executeQuery();
```

- 执行预备Sql语句INSERT、UPDATE、DELETE,这些语句由PreparedStatement对象表示.返回受影响的行数.如果执行的是数据定义语言(DDL)中的语言,如CREATE、DROP TABLE,则该方法返回0
```java
Integer   preStat.executeUpdate();
```

#### SQLWarning
- 返回未处理警告中的第一个,或者在没有未处理警告时返回null
```java
SQLWarning   getWarnings();
```

- 返回链接到该警告的下一个警告,或者在到达链尾时返回null
```java
SQLWarning   getNextWarning();
```


#### 读写LOB
- 二进制大对象称为BLOB
- 字符型大对象称为CLOB



#### 事务
- 获取该连接中的自动提交模式
```java
boolean   conn.getAutoCommit();
```

- 设置自动提交模式,如果自动更新为true,那么所有语句将在执行结束后立即被提交
```java
void   conn.setAutoCommit(boolean b);
```

- 提交自上次提交以来所有执行过的语句
```java
void   conn.commit();
```

- 撤销自上次提交以来所有执行过的语句所产生的影响
```java
void   conn.rollback();
```

- 设置一个匿名或具体名字的保存点
```java
Savepoint   conn.setSavepoint();
Savepoint   conn.setSavepoint(String name);
```

- 回滚到给定保存点
```java
void   conn.rollback(Savepoint svpt);
```

- 释放给定的保存点
```java
void   conn.releaseSavepoint(Savepoint svpt);
```

---
---
# druid+SqlServer实例
### pom.xml
```xml
    <druid-version>1.1.12</druid-version>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid</artifactId>
        <version>${druid-version}</version>
    </dependency
```

### jdbc.properties
```properties
driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
url=jdbc:sqlserver://localhost:1433;DatabaseName=mall
username=sa
password=root
```

### JdbcUtils
```java
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class JdbcUtils {

    private static DruidDataSource dataSource;

    static {
        Properties properties = new Properties();
        InputStream inputStream = JdbcUtils.class.getClassLoader().getResourceAsStream("jdbc.properties");
        try {
            properties.load(inputStream);
            try {
                dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 获取数据库连接池中的连接
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
```

### JdbcUtilsTest
```java
import java.sql.*;

public class JdbcUtilsTest {

    public static void main(String[] args) {
        Connection conn = JdbcUtils.getConnection();
        String sql = "SELECT * FROM dbo.ums_admin WITH(NOLOCK) WHERE id=?";
        try {
            PreparedStatement preStat = conn.prepareStatement(sql);
            preStat.setString(1, "4");

            ResultSet resultSet = preStat.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JdbcUtils.close(conn);
        }
    }

}
```
