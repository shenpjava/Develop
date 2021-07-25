#### 第一章:逻辑查询处理
  ```sql
  SET NOCOUNT ON

  IF OBJECT_ID('dbo.Orders','U') IS NOT NULL
  BEGIN
  	DROP TABLE dbo.Orders
  END

  IF OBJECT_ID('dbo.Customers','U') IS NOT NULL
  BEGIN
  	DROP TABLE dbo.Customers
  END

  CREATE TABLE dbo.Customers
  (
  	customerid	CHAR(5)	NOT NULL	PRIMARY KEY,
  	city		VARCHAR(10)	NOT NULL
  )

  CREATE TABLE dbo.Orders
  (
  	orderid		INT	NOT NULL	PRIMARY KEY,
  	customerid	CHAR(5)	NULL	REFERENCES dbo.Customers(customerid)
  )
  GO

  INSERT INTO dbo.Customers( customerid, city )VALUES  ( 'FISSA', 'Madrid')
  INSERT INTO dbo.Customers( customerid, city )VALUES  ( 'FRNDO', 'Madrid')
  INSERT INTO dbo.Customers( customerid, city )VALUES  ( 'KRLOS', 'Madrid')
  INSERT INTO dbo.Customers( customerid, city )VALUES  ( 'MRPHS', 'Zion')

  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 1,'FRNDO')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 2,'FRNDO')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 3,'KRLOS')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 4,'KRLOS')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 5,'KRLOS')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 6,'MRPHS')
  INSERT INTO dbo.Orders( orderid, customerid )VALUES  ( 7,NULL)

  SELECT * FROM dbo.Customers

  SELECT * FROM dbo.Orders
  ```

***
#### 第四章:查询优化
- 4.1
  - 数据库:Performance
    - 表:dbo.Nums、dbo.Orders、dbo.Customers、dbo.Employees、dbo.Shippers
    - 视图:dbo.EmpOrders
```sql
SET NOCOUNT ON
USE master
IF DB_ID('Performance') IS NULL
BEGIN
	CREATE DATABASE Performance
END
GO

USE Performance;
GO

--创建和填充数字的辅助表
SET NOCOUNT ON;
IF OBJECT_ID('dbo.Nums','U') IS NOT NULL
BEGIN
	DROP TABLE dbo.Nums
END

CREATE TABLE dbo.Nums
(
	n	INT NOT NULL PRIMARY KEY
)

DECLARE @max INT
DECLARE @rc	INT

SET @max=1000000
SET @rc=1

INSERT INTO dbo.Nums( n ) VALUES ( 1 )

WHILE @rc*2<=@max
BEGIN
	INSERT INTO dbo.Nums( n )SELECT n+@rc FROM dbo.Nums
	SET @rc=@rc*2
END

INSERT INTO dbo.Nums( n ) SELECT n+@rc FROM dbo.Nums WHERE n+@rc<=@max

--如果数据表存在,则先删除
IF OBJECT_ID('dbo.EmpOrders','V') IS NOT NULL
BEGIN
	DROP VIEW dbo.EmpOrders
END

IF OBJECT_ID('dbo.Orders','U') IS NOT NULL
BEGIN
	DROP TABLE dbo.Orders
END

IF OBJECT_ID('dbo.Customers','U') IS NOT NULL
BEGIN
	DROP TABLE dbo.Customers
END

IF OBJECT_ID('dbo.Employees','U') IS NOT NULL
BEGIN
	DROP TABLE dbo.Employees
END
IF OBJECT_ID('dbo.Shippers','U') IS NOT NULL
BEGIN
	DROP TABLE dbo.Shippers
END

--数据分布设置
DECLARE @numorders		INT
DECLARE @numcusts		INT
DECLARE @numemps		INT
DECLARE @numshippers	INT
DECLARE @numyears		INT
DECLARE @startdate		DATETIME

SELECT
	@numorders=1000000,
	@numcusts=20000,
	@numemps=500,
	@numshippers=5,
	@numyears=4,
	@startdate='20050101'

--创建和填充Customers
CREATE TABLE dbo.Customers
(
	custid		CHAR(11)	NOT NULL,
	custname	NVARCHAR(50)	NOT NULL
)

INSERT INTO dbo.Customers
(	custid, custname )
SELECT
	'C'+RIGHT('000000000'+CAST(n AS VARCHAR(10)),10) AS custid,
	N'Cust_'+CAST(n AS VARCHAR(10)) AS custname
FROM
	dbo.Nums
WHERE
	n<=@numcusts

ALTER TABLE dbo.Customers ADD CONSTRAINT PK_Customers PRIMARY KEY(custid)

--创建和填充Employees
CREATE TABLE dbo.Employees
(
	empid	INT	NOT NULL,
	firstname	NVARCHAR(25)	NOT NULL,
	lastname	NVARCHAR(25)	NOT NULL
)

INSERT INTO dbo.Employees
(
	empid,		firstname,		lastname
)
SELECT
	n AS empid,
	N'Fname_'+CAST(n AS NVARCHAR(10)) AS firstname,
	N'Lname_'+CAST(n AS NVARCHAR(10)) AS lastname
FROM
	dbo.Nums WHERE n<=@numemps

ALTER TABLE dbo.Employees ADD CONSTRAINT PK_Employees PRIMARY KEY(empid)

--创建和填充Shippers
CREATE TABLE dbo.Shippers
(
	shipperid		VARCHAR(5)	NOT NULL,
	shippername		NVARCHAR(50)	NOT NULL
)

INSERT INTO dbo.Shippers
( shipperid, shippername )
SELECT
	D.shipperid,
	N'Shipper_'+D.shipperid AS shippername
FROM
	(
		SELECT
			CHAR(ASCII('A')-2+2*n) AS shipperid
		FROM
			dbo.Nums
		WHERE
			n<=@numshippers
	) AS D

ALTER TABLE dbo.Shippers ADD CONSTRAINT PK_Shippers PRIMARY KEY(shipperid)

--创建和填充Orders
CREATE TABLE dbo.Orders
(
	orderid		INT		NOT NULL,
	custid		CHAR(11)	NOT NULL,
	empid		INT			NOT NULL,
	shipperid	VARCHAR(5)	NOT NULL,
	orderdate	DATETIME	NOT NULL,
	filler		CHAR(155)	NOT NULL DEFAULT('a')
)

INSERT INTO dbo.Orders
(
	orderid ,
	custid ,
	empid ,
	shipperid ,
	orderdate
)
SELECT
	n AS  orderid,
	'C'+RIGHT('000000000'+CAST(1+ABS(CHECKSUM(NEWID())) %@numcusts AS VARCHAR(10)),10) AS custid,
	1+ABS(CHECKSUM(NEWID()))%@numemps AS empid,
	CHAR(ASCII('A')-2+2*(1+ABS(CHECKSUM(NEWID()))%@numshippers )) AS shipperid,
	DATEADD(DAY,n/(@numorders/(@numyears*365.25)),@startdate) AS orderdate
FROM
	dbo.Nums
WHERE
	n<=@numorders
ORDER BY CHECKSUM(NEWID())

--基于orderdate的聚集索引
CREATE CLUSTERED INDEX idx_cl_od ON dbo.Orders(orderdate)
--基于shipperid、orderdate的非聚集索引,并具有包含列custid
CREATE NONCLUSTERED INDEX idx_nc_sid_od_i_cid ON dbo.Orders(shipperid,orderdate) INCLUDE(custid)
--基于orderdate、orderid的非聚集唯一索引,并具有包含列custid、empid
CREATE UNIQUE INDEX idx_unc_od_oid_i_cid_eid ON dbo.Orders(orderdate,orderid) INCLUDE(custid,empid)

ALTER TABLE dbo.Orders ADD
	--基于orderid的非聚集唯一索引,由主键隐式创建
	CONSTRAINT PK_Orders PRIMARY KEY NONCLUSTERED(orderid),
	CONSTRAINT PK_Orders_Customers	FOREIGN KEY(custid) REFERENCES dbo.Customers(custid),
	CONSTRAINT PK_Orders_Employees	FOREIGN KEY(empid)	REFERENCES dbo.Employees(empid),
	CONSTRAINT PK_Orders_Shippers	FOREIGN KEY(shipperid)	REFERENCES dbo.Shippers(shipperid)
GO
```

- 4.2优化方法论
```sql
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderid=3
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderid=5
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderid=7
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate='20080212'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate='20080118'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate='20080828'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate>='20080101' AND orderdate<='20080201'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate>='20080401' AND orderdate<='20080501'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate>='20080201' AND orderdate<='20090301'
GO
SELECT orderid,custid,empid,shipperid,orderdate,filler FROM dbo.Orders WHERE orderdate>='20080501' AND orderdate<='20080601'
GO
```

- 4.2.1分析实例级别的等待
```SQL
WITH waits AS
(
	SELECT
		wait_type,
		wait_time_ms/1000.0 AS  wait_time_s,
		100.0*wait_time_ms/SUM(wait_time_ms) OVER() AS pct,
		ROW_NUMBER() OVER(ORDER BY wait_time_ms DESC) AS rn,
		100.0*signal_wait_time_ms/wait_time_ms AS signal_pct
	FROM
		sys.dm_os_wait_stats
	WHERE
		wait_time_ms>0
		AND wait_type NOT LIKE '%SLEEP%'
		AND wait_type NOT LIKE '%%IDLE'
		AND wait_type NOT LIKE '%QUEUE%'
		AND wait_type NOT IN ('CLR_AUTO_EVENT','REQUEST_FOR_DEADLOCK_SEARCH','SQLTRACE_BUFFER_FLUSH')
)

SELECT
	w1.wait_type,
	CAST(w1.wait_time_s AS NUMERIC(12,2)) AS  wait_time_s,
	CAST(w1.pct AS NUMERIC(5,2)) AS pct,
	CAST(SUM(w2.pct) AS NUMERIC(5,2)) AS running_pct,
	CAST(w1.signal_pct AS NUMERIC(5,2)) AS signal_pct
FROM
	waits AS w1
JOIN
	waits AS w2
ON
	w2.rn = w1.rn
GROUP BY
	w1.rn,w1.wait_type,w1.wait_time_s,w1.pct,w1.signal_pct
HAVING
	SUM(w2.pct)-w1.pct<80
	OR w1.rn<=5
ORDER BY w1.rn
```

  - 表:dbo.WaitStats
```SQL
IF OBJECT_ID('dbo.WaitStats','U') IS NOT NULL
	DROP TABLE dbo.WaitStats
GO

CREATE TABLE dbo.WaitStats
(
	dt			 DATETIME	NOT NULL DEFAULT(CURRENT_TIMESTAMP),
	wait_type		 NVARCHAR(60)	NOT NULL,
	waiting_tasks_count	 BIGINT		NOT NULL,
	wait_time_ms		 BIGINT		NOT NULL,
	max_wait_time_ms	 BIGINT		NOT NULL,
	signal_wait_time_ms	 BIGINT		NOT NULL
)
```

```sql
IF OBJECT_ID('dbo.IntervalWaits','IF') IS NOT NULL
BEGIN
	DROP FUNCTION [dbo].[IntervalWaits]
END
GO

CREATE FUNCTION [dbo].[IntervalWaits]
(
	@fromdt		DATETIME,
	@todt		DATETIME
)
RETURNS TABLE
WITH ENCRYPTION
AS
	RETURN
		WITH waits AS
		(
			SELECT
				dt,
				wait_type,
				wait_time_ms,
				ROW_NUMBER() OVER(PARTITION BY wait_type ORDER BY dt) AS rn
			FROM
				dbo.WaitStats
		)
		SELECT
			Prv.wait_type,
			Prv.dt AS start_time,
			CAST((Cur.wait_time_ms-Prv.wait_time_ms)/1000.0 AS numeric(12,2)) AS interval_wait_s
		FROM
			waits AS Cur
		JOIN
			waits AS Prv
		ON
			Cur.wait_type=Prv.wait_type
			AND Cur.rn=Prv.rn+1
			AND Prv.dt>=@fromdt
			AND Prv.dt<DATEADD(DAY,1,@todt)
```

  - 视图:IntervalWaitsSample
  ```SQL
  IF OBJECT_ID('dbo.IntervalWaitsSample','V') IS NOT NULL
  	DROP VIEW dbo.IntervalWaitsSample
  GO

  CREATE VIEW dbo.IntervalWaitsSample
  AS
  	SELECT wait_type,start_time,interval_wait_s FROM dbo.IntervalWaits('20210709','20210810') AS F
  GO
  ```

***
