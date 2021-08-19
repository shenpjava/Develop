#### Connection
- 创建一个Statement对象,执行不带参数的sql查询和更新
```java
Statement stat=conn.createStatement();
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
Integer     stat.executeUpdate(sql);
```

- 执行任意的sql语句,可能会产生多个结果集和更新计数
```java
Boolean     stat.execute(sql);
```

- 返回前一条查询语句的结果集
```java
ResultSet   stat.getResultSet();
```

- 返回前一条更新语句影响的行数
```java
Integer     stat.getUpdateCount();
```

- 关闭Statement对象以及它对应的结果集
```java
void  stat.close();
```

#### ResultSet
- 将结果集中当前行向前移动一行.如果已经到达最后一行的后面,则返回false。注意,初始情况下必须调用该方法才能转到第一行.
```java
ResultSet   resultSet.next()
```

#### PreparedStatement
