# smartExcelToTable

## This  Java API helps developer to insert data from any excel sheet to a specified table without worrying about column name mismatch. 

## Basic Example
consider Excel table

| first |    second |    third |
|---| -----------------|----------------|
|1    |  hii     |  bye |
|2    |  type           |  john |
|3    |  categories     |  miller |
|4    |  arrayofobject  |  simple |
|5    |  simpleobject   |  api |     

And your traget mysql table has columns

| fir_st |    secooond |    TThird |

To insert data from example excel file to example table ,you need a programe with column names hard coded, but this API enables you to not worry about column names in excel or table. it smartly finds the most matched column and push data into it.

If  source and dest column names are completly different , you can explicitly map the column names using a simple property file or a table

| idExcelFileName |    ExcelColumn |    TableColumn |
|------------------|---------------|------|
|1    |  first     |  SSFFRRSSTt |

## How to Use
CASE 1-   if you want explicit mapping also to be performed.
Create a table excelfilename

| id |    ExcelFileName |    tablename |
|---| -----------------|----------------|

Create a table exceltablemapper

| id |    excelColumn |    tableColumn | idExcelfilename
|---| -----------------|----------------|----------------|

use below method API

```java
 SmartExcelToTable.kickStartSmartExcelToTable(String filename,String filepath);
```    
example -- 

```java
kickStartSmartExcelToTable("AssetData.xlsx",
				"D:/MyWs/Dashboard/exceltotable/AssetData.xlsx");
``` 
