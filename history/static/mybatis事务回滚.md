# 事务回滚机制  
对于单纯的mybaits使用`SqlSession session=DefaultSqlSessionFactory.openSession();`获取到的session，如果其属性`autoCommit==false`,且执行了非select操作，
在`session.close()`的时候，如果没有发生`session.commit();`，默认是不提交，会回滚的。  

#### 测试源码  
```
public static void main(String[] args) {

    /**
     * 这里获取一个session，默认是不自动提交的
     */
    try (SqlSession sqlSession = MyBatisConnectionFactory.getFactory()
            .openSession()) {
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);

        System.out.println(mapper.fuzzySearchName("%kai%"));
        // 插入
        mapper.insertSelective(new Employee(1, "kai", "kai"));
        System.out.println(mapper.fuzzySearchName("%kai%"));
        // 制造空指针异常，看是否commit
        Map<String, Employee> map = new HashMap<>();
        String chineseName = map.get("exception").getChineseName();
        System.out.println(chineseName);
        sqlSession.commit();
    }
}
```
#### 运行结果：   
![resultOne](http://git.gyyx.cn/caishuai/static/raw/master/mybatis/resultOne.jpg)  
```
Rolling back JDBC Connection [org.sqlite.Conn@5594a1b5]
Resetting autocommit to true on JDBC Connection [org.sqlite.Conn@5594a1b5]
Closing JDBC Connection [org.sqlite.Conn@5594a1b5]
Returned connection 1435804085 to pool.
Exception in thread "main" java.lang.NullPointerException
	at asas2.Test.main(Test.java:47)
```
很明显我们的事务被rollback了。  

#### 原因分析：  
首先来看一下`close()`方法。  

![codeOne](http://git.gyyx.cn/caishuai/static/raw/master/mybatis/codeOne.jpg)  

  上面使用的是DefaultSqlSession。  
```
// close源码
public void close() {
  try {
    // 这里会对执行器进行Close，获取这次的session是否需要commit或者rollback的结果
    executor.close(isCommitOrRollbackRequired(false));
    dirty = false;
  } finally {
    ErrorContext.instance().reset();
  }
}


//session是否需要commit或者rollback
private boolean isCommitOrRollbackRequired(boolean force) {
   return (!autoCommit && dirty) || force;
}

```
这里面的`autoCommit`和`dirty`都是`DefaultSqlSession`的private成员，`autoCommit`是否自动提交，`dirty`字面意思是脏的，构造的时候默认是false。
```
public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
    this.configuration = configuration;
    this.executor = executor;
    this.dirty = false;
    this.autoCommit = autoCommit;
  }
```
但是在我们执行insert的时候，`dirty`发生了改变。  
```
public int update(String statement, Object parameter) {
  try {
    dirty = true;
    MappedStatement ms = configuration.getMappedStatement(statement);
    return executor.update(ms, wrapCollection(parameter));
  } catch (Exception e) {
    throw ExceptionFactory.wrapException("Error updating database.  Cause: " + e, e);
  } finally {
    ErrorContext.instance().reset();
  }
}
```  
在mybatis的`DefaultSqlSession`源码中，所有的`insert`,`update`，`delete` 方法其实走的都是这一个`update`方法。在这里，`dirty`被赋值为`true`。回过头来看` return (!autoCommit && dirty) || force;`的值为`true`。

这时executor.close()的参数为true，进入方法再去看在`org.apache.ibatis.executor.CachingExecutor`中的`close()`方法。（实际上就是先清了缓存的）  
```
public void close(boolean forceRollback) {
    try {
      //issues #499, #524 and #573
      if (forceRollback) {
        //这里的tcm对应的是TransactionalCacheManager，删除了缓存中的
        tcm.rollback();
      } else {
        tcm.commit();
      }
    } finally {
      //实际的rollback在这里，delegate还是executor，走到了BaseExecutor的close();
      delegate.close(forceRollback);
    }
  }
```
BaseExecutor的close();  
```
public void close(boolean forceRollback) {
    try {
      try {
        //实际的rollback
        rollback(forceRollback);
      } finally {
        if (transaction != null) transaction.close();
      }
    } catch (SQLException e) {
      // Ignore.  There's nothing that can be done at this point.
      log.warn("Unexpected exception on closing transaction.  Cause: " + e);
    } finally {
      transaction = null;
      deferredLoads = null;
      localCache = null;
      localOutputParameterCache = null;
      closed = true;
    }
  }

  public void rollback(boolean required) throws SQLException {
    if (!closed) {
      try {
        //清缓存
        clearLocalCache();
        flushStatements(true);
      } finally {
        if (required) {
          //这里是真的transaction，rollback了，required参数就是一开始传进来的通过dirty，autocommit，focus计算出来的值
          transaction.rollback();
        }
      }
    }
  }

```
transaction的rollback
```
public void rollback() throws SQLException {
   if (connection != null && !connection.getAutoCommit()) {
     //这里判定的connection.getAutoCommit()又一次判定了一次是不是自动提交的，不是才rollback
     if (log.isDebugEnabled()) {
       log.debug("Rolling back JDBC Connection [" + connection + "]");
     }
     connection.rollback();
   }
 }
```

但是，为什么会有`Resetting autocommit to true on JDBC Connection [org.sqlite.Conn@5594a1b5]`这个转换自动提交的日志。  
再来看一下BaseExecutor的close();  
```
public void close(boolean forceRollback) {
    try {
      try {
        //实际的rollback
        rollback(forceRollback);
      } finally {
        //resetautocommit在这里
        if (transaction != null) transaction.close();
      }
    } catch (SQLException e) {
      // Ignore.  There's nothing that can be done at this point.
      log.warn("Unexpected exception on closing transaction.  Cause: " + e);
    } finally {
      transaction = null;
      deferredLoads = null;
      localCache = null;
      localOutputParameterCache = null;
      closed = true;
    }
  }
```
close到最后有一个finally`if (transaction != null) transaction.close();`, 里面先执行了resetAutoCommit() ,然后connection.close();  
```
protected void resetAutoCommit() {
    try {
      if (!connection.getAutoCommit()) {
        // MyBatis does not call commit/rollback on a connection if just selects were performed.
        // Some databases start transactions with select statements
        // and they mandate a commit/rollback before closing the connection.
        // A workaround is setting the autocommit to true before closing the connection.
        // Sybase throws an exception here.
        if (log.isDebugEnabled()) {
          log.debug("Resetting autocommit to true on JDBC Connection [" + connection + "]");
        }
        connection.setAutoCommit(true);
      }
    } catch (SQLException e) {
      log.debug("Error resetting autocommit to true "
          + "before closing the connection.  Cause: " + e);
    }
  }
```
so,那些单纯的查询语句，在没有commit的时候，dirty属性一直是false，也就rollback中执行transaction的rollback之前，其中的判定required为false，so就，没有transaction的rollback，最后转为autocommit，返回了结果。
