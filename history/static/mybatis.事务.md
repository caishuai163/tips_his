# mybatis同一方法中，执行多条操作  
测试内容：
```
public interface InfoMapper {
    //重复主键exception
    @Insert("INSERT INTO info (id, uid) VALUES (3, 4) ;UPDATE info SET id=3 WHERE id=2; ")
    int insertSelective();
}
```
结果：   

是否rollback/Exception |mysql(mysql-connector-java-6.0.4.jar) |MariaDb(mariadb-java-client-1.1.7.jar) |MariaDb(mariadb-java-client-1.5.9.jar) |sqlserver(jdbc-4.0.2206.100.jar)
---|---|---|---|---  
autoCommit(true)|false/true|false/false|false/true|false/false
autoCommit(false)|true/true|false/false|true/true|false/false

在发生异常的时候，使用autocommit(false)可以达到自动回滚的效果；而autocommit(true)并不能引起回滚效果。  
在一个方法中执行多条插入的时候，有一些jdbc貌似并不会给mybatis返回一个异常信息，然后就执行commit了，session中的dirty状态又变成了false，close时不会会滚。（这个时候感觉捕获异常手动rollback都没有用，因为就没有返回异常）。


mysql在允许一个方法中多行执行的时候要在配置上加`allowMultiQueries=true`


# batch 比较  


数据量 |1000 |2000 |3000 |8000 |16000  
----|----|----|----|----|----
程序foreach         |997571157|1872036264|2543747830|5433001023|
程序foreach + batch |326815462|587140857|892238559|2232722403|
xml foreach         |178760021|362974185|416361099|959453593|
xml foreach + batch |96062142|105649515|178214248|347373072|
10         
50227985
43254792

5
for:		28415508
forbatch:	51468006
for:		28218907
forbatch:	29102076
for:		49171255
forbatch:	29155834
for:		27669038
forbatch:	20816160
for:		28967937
forbatch:	20938525
