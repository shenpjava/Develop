#### 表 Table
- 创建表
  ```SQL
      IF EXISTS(SELECT 1 FROM dbo.sysobjects WITH(NOLOCK) WHERE id=OBJECT_ID(N'[dbo].[Tab]') AND OBJECTPROPERTY(id,N'IsUserTable')=1)
      BEGIN
      	DROP TABLE [dbo].[Tab]
      END
      GO
      CREATE TABLE [dbo].[Tab]
      (
      	DIID			INT IDENTITY(1,1),
      	Remarks			NVARCHAR(500)

      	CONSTRAINT [PK_Tab] PRIMARY KEY CLUSTERED
      	(
      		DIID
      	)
      ) ON [PRIMARY]

  ```


- 表结构
  ```SQL
      SELECT
          QUOTENAME(B.name)+' '+QUOTENAME(UPPER(D.name))
              +CASE
                  WHEN D.name IN ('BigInt','Integer','Int','SmallInt','TinyInt',
                                  'Real','Bit','Text','NText','SysName',
                                  'Image','DateTime','SmallDateTime','Money','SmallMoney',
                                  'TimeStamp','SQL_Variant','Float','UniqueIdentifier'
                                  )
                      THEN ''
                  WHEN D.name IN ('Binary','VarBinary','Char','NChar','Varchar','NVarchar')
                      THEN '('+ CONVERT(VARCHAR(5),B.Length) + ')'
                  WHEN D.name IN ('Decimal','Numeric')
                      THEN '('+ CONVERT(VARCHAR(5),B.Prec) + ',' + CONVERT(VARCHAR(5),B.Scale) + ')'
                  ELSE ''
              END
      FROM
      	dbo.sysobjects AS A WITH(NOLOCK),
      	dbo.syscolumns AS B WITH(NOLOCK)
      		LEFT JOIN sys.syscomments AS C WITH(NOLOCK) ON C.id=B.cdefault,
      	dbo.systypes AS D WITH(NOLOCK)
      WHERE
      	A.id=B.id
      	AND B.xusertype=D.xusertype
      	AND A.id=OBJECT_ID(N'dbo.Tab')
      ORDER BY
      	A.name,B.colid
  ```

- 约束
  - 增加
  ```SQL
    ALTER TABLE dbo.Tab ADD CONSTRAINT PK_Tab PRIMARY KEY(DIID)
  ```

  - 删除
  ```SQL
    ALTER TABLE dbo.Tab DROP CONSTRAINT PK_Tab
  ```  

- 索引

- 临时表
  - 判别存在
  ```SQL
  SELECT
  	1
  FROM
  	tempdb.dbo.sysobjects WITH(NOLOCK)
  WHERE
  	id=OBJECT_ID(N'tempdb..#Tab')
  	--id=OBJECT_ID(N'tempdb..##Tab')
  	AND type='U'
  ```

  - 表结构
  ```SQL
  SELECT
  	QUOTENAME(B.name)+' '+QUOTENAME(UPPER(D.name))
  		+CASE
  			WHEN D.name IN ('BigInt','Integer','Int','SmallInt','TinyInt',
  							'Real','Bit','Text','NText','SysName',
  							'Image','DateTime','SmallDateTime','Money','SmallMoney',
  							'TimeStamp','SQL_Variant','Float','UniqueIdentifier'
  							)
  				THEN ''
  			WHEN D.name IN ('Binary','VarBinary','Char','NChar','Varchar','NVarchar')
  				THEN '('+ CONVERT(VARCHAR(5),B.Length) + ')'
  			WHEN D.name IN ('Decimal','Numeric')
  				THEN '('+ CONVERT(VARCHAR(5),B.Prec) + ',' + CONVERT(VARCHAR(5),B.Scale) + ')'
  			ELSE ''
  		END
  FROM
  	tempdb..sysobjects AS A WITH(NOLOCK),
  	tempdb..syscolumns AS B WITH(NOLOCK)
  		LEFT JOIN sys.syscomments AS C WITH(NOLOCK) ON C.id=B.cdefault,
  	tempdb..systypes AS D WITH(NOLOCK)
  WHERE
  	A.id=B.id
  	AND B.xusertype=D.xusertype
  	AND A.id=OBJECT_ID('tempdb..#Tab')
  	--AND A.id=OBJECT_ID('tempdb..##Tab')
  ORDER BY
  	A.name,B.colid
  ```


- 页
  - 页是SQL Server存储数据的基本单元,大小为8KB.
  - 可以包含表、索引数据、分配位图、可用空间信息等.
  - 页是SQL Server可以读写的最小I/O单位.即使只须访问一行,SQL Server也要把整个页加载到缓存,再从缓存中读取数据.

- 区
  - 区是由8个物理上连续的页组成的单元.
  - 页可以位于一个混合区内,混合区的8个页属于不同的对象.

- 表的组织方式(HOBT)
  - 堆、B树
  - 当在表上创建一个聚集索引时,表就组织为一个B树;否则就组织为一个堆.

- 堆
  - 堆是不含聚集索引的表.
  - 数据不按任何顺序进行组织,而是按分区组对数据进行组织.
  - 在一个堆中,用于保存数据之间关系的唯一结构是一个称为索引分配映射的位图页.


- 聚集索引
  - SQL Server中的所有索引都组织为B树结构,B树是平衡树的一种特例.
  - 平衡树:不存在叶子节点比其他叶子节点到根的距离要远得多的树
  - 在叶子节点中维护整个表的所有数据
  - 聚集索引不是数据的副本,而是数据本身.

***
- 逻辑顺序
  ```sql
  FROM > WHERE > GROUP BY > HAVING > SELECT > ORDER BY
  ```

***
- GROUP BY
  - 如果涉及到分组,GROUP BY阶段之后的所有阶段(包括HAVING、SELECT、ORDER BY)的操作对象将是组,而不是单独的行。
  - 所有的聚合函数都会忽略NULL,只有一个例外--COUNT(*)
  - 想处理不重复的已知值,可以在聚合函数的圆括号中指定DISTINCT关键字
    - COUNT(DISTINCT qty)
    - SUM(DISTINCT qty)
    - AVG(DISTINCT qty)

***
- HAVING
  - 用于指定对组进行过滤的谓词或逻辑表达式,与WHERE阶段对单独的行进行过滤相对应

***
- ORDER BY
  - 表是一个集合,集合是无序的
  - 指定DISTINCT以后,ORDER BY子句就被限制为只能选取在SELECT列表中出现的那些元素


   关键字 | 描述
  --|--
  ASC  |  升序(默认)
  DESC  |  降序

***
#### 谓词和运算符
- 谓词值(TRUE、FALSE、UNKNOWN)

***
#### 转换值的数据类型
  语法  |  描述
  --|--
  CAST(value AS datatype)  |  ANSI SQL
  CONVERT(datatype,value,[style])  |  使用样式值

***
#### 获取当前日期和时间
  函数  | 返回类型  |  描述
  --|---|--
  GETDATE()  |  DATETIME |  当前日期和时间
  CURRENT_TIMESTAMP |  DATETIME |  与GETDATE相同,而且是ANSI SQL

***
#### 目录视图
```sql
SELECT SCHEMA_NAME(schema_id) AS table_schema_name,name FROM sys.tables
--SCHEMA_NAME 把表示架构ID的整数转换成它的名称
```
table_schema_name  |  name
--|--
dbo  |  Tab

***
#### 视图
- 刷新视图
  ```sql
  EXEC sys.sp_refreshview @viewname = N''
  ```

***
#### 事务和并发
- 事务

- 锁定和阻塞

- 隔离级别
  - READ UNCOMMITTED(未提交读)
    - 最低级
    - 读操作不会请求共享锁,不会和持有排他锁的写操作发生冲突
    - 读操作可以读取未提交的修改(脏读)

  - READ COMMITTED(已提交读)默认
    - 读操作只能读取修改提交过的数据
    - 在READ COMMITTED隔离级别中,读操作一完成,就立即释放资源上的共享锁.
    - 其他事务可以在两个读操作之间更改数据资源,读操作因而可能每次得到不同的取值.(不可重复读或不一致的分析)

  - REPEATABLE READ(可重复读)
    - 两个事务在第一次读操作之后都将保留它们获得的共享锁,所以任何一个事务都不能获得为了更新数据而需要的排他锁(死锁),却避免了更新冲突.
    - 事务只锁定查询第一次运行时找到的那些数据资源(例如:行),而不会锁定查询结果范围以外的其他行.因此,在同一事务中进行第二次读取之前,如果其他事务插入了新行,而且新行也能满足读操作的查询过滤条件,那么这些新行也会出现在第二次读操作返回的结果中(幻影、幻读)

  - SERIALIZABLE(可序列化)


  - SNAPSHOT(快照)


  - READ COMMITTED SNAPSHOT



  隔离级别 |未提交读？ |不可重复读？ |丢失更新？ |幻读？ |检测更新冲突？ |使用行版本控制？
  --|---|---|---|---|---|--
  READ UNCOMMITTED |√ |√ |√ |√ |× |×
  READ COMMITTED |× |√ |√ |√ |× |×
  READ COMMITTED SNAPSHOT |× |√ |√ |√ |× |√
  REPEATABLE READ |× |× |× |√ |× |×
  SERIALIZABLE |× |× |× |× |× |×
  SNAPSHOT |× |× |× |× |√ |√

***
#### 循环(WHILE)
  ```sql
  DECLARE @Count		INT

  WHILE @Count>0
  BEGIN
  	--
  	SET @Count=@Count-1
  END
  ```

***
#### 游标
- 创建游标
  ```SQL
  DECLARE @Col		NVARCHAR(50)

  DECLARE Tab_TC CURSOR FOR
  SELECT Remarks FROM dbo.Tab WITH(NOLOCK) WHERE 1=1 ORDER BY ISNULL(DIID,0)
  OPEN Tab_TC
  FETCH NEXT FROM Tab_TC INTO @Col
  WHILE @@FETCH_STATUS=0
  BEGIN
  --
  FETCH NEXT FROM Tab_TC INTO @Col
  END
  CLOSE Tab_TC
  DEALLOCATE Tab_TC
  ```

***
#### 存储过程
  - 创建存储过程
  ```SQL
    IF EXISTS(SELECT 1 FROM dbo.sysobjects WITH(NOLOCK) WHERE id=OBJECT_ID(N'[dbo].[SP_Event]') AND OBJECTPROPERTY(id,N'IsProcedure')=1)
  BEGIN
  	--BeforeInsert,AfterInsert,BeforePost,AfterPost,BeforeDelete,AfterDelete,BeforeConfirm,AfterConfirm
  	DROP PROCEDURE [dbo].[SP_Event]
  END
  GO

  CREATE PROCEDURE [dbo].[SP_Event]
  	@Params		NVARCHAR(2000)='',
  	@Msg			NVARCHAR(500)='' OUTPUT,
  	@Type			INT=0	OUTPUT
  WITH ENCRYPTION
  AS
  BEGIN
  	--
  END
  ```

***
#### 函数
- 创建函数
  ```SQL
  IF EXISTS(SELECT 1 FROM dbo.sysobjects WITH(NOLOCK) WHERE id=OBJECT_ID(N'[dbo].[FT_]') AND xtype IN (N'FN',N'IF',N'TF'))
  BEGIN
  	DROP FUNCTION [dbo].[FT_]
  END
  GO

  CREATE FUNCTION [dbo].[FT_]
  (
  	@A		NVARCHAR(50)
  )
  RETURNS @ATab TABLE
  (
  	AA		NVARCHAR(50)
  )
  --RETURNS DECIMAL(18,6),NVARCHAR(50),INT..
  WITH ENCRYPTION
  AS
  BEGIN
  	--
  END
  ```

***
#### 触发器
- 创建函数
  ```SQL
  IF EXISTS(SELECT 1 FROM dbo.sysobjects WITH(NOLOCK) WHERE id=OBJECT_ID(N'[dbo].[TR_IUD_Tab]') AND OBJECTPROPERTY(id,N'IsTrigger')=1)
  BEGIN
  	--TR_I,TR_U,TR_D,TR_IU,TR_ID,TR_UD,TR_IUD
  	DROP TRIGGER [dbo].[TR_IUD_Tab]
  END
  GO

  CREATE TRIGGER [dbo].[TR_IUD_Tab]
  ON dbo.Tab FOR INSERT,UPDATE,DELETE
  AS
  BEGIN
  	--记录Insert、Update之后的内容
  	SELECT * FROM Inserted

  	--记录Delete、Update之前的内容
  	SELECT * FROM Deleted

  	IF UPDATE(Remarks)	--Remarks为Tab表中字段
  	BEGIN
  		------
  	END
  END
  ```

- 注:不会引发触发器
  ```SQL
  TRUNCATE TABLE dbo.Tab
  ```

***
