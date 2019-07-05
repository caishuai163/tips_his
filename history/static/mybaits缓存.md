# mybatis缓存  
不适用于分布式服务器集群。
reason：二级缓存，是保存在服务器上的，对于不同服务器上，执行update后，清除的缓存只有本服务器的，其他服务器的数据没有变化，会出现脏读的情况。建议使用分布式缓存。
### 一级缓存  
一级缓存是基于单个SqlSession的，对于同一个sessuin，查询相同内容时，第二次查询的时候会从缓存中获取，默认是开启状态的，但是当这个session中的有更新语句时，缓存将会被清除，避免脏读的情况发生。缓存只限于这个session中，当session被释放，缓存将被清理。  
### 二级缓存  
二级缓存是可以存在session之间的，要在全局的xml中进行配置。
config.xml文件  
```
<!-- 全局配置参数，需要时再设置 -->
    <settings>
    <!-- 开启二级缓存  默认值为true -->
    <setting name="cacheEnabled" value="true"/>
    </settings>
```
对应的mapper的xml也要去配置：  
```
<mapper namespace="asas.EmployeeMapper">
 <cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true" />  
 ...
</mapper>
```
+ eviction是缓存的淘汰算法，可选值有"LRU"、"FIFO"、"SOFT"、"WEAK"，缺省值是LRU。  
+ LRU – 最近最少使用的:移除最长时间不被使用的对象。  
+ FIFO – 先进先出:按对象进入缓存的顺序来移除它们。  
+ SOFT – 软引用:移除基于垃圾回收器状态和软引用规则的对象。  
+ WEAK – 弱引用:更积极地移除基于垃圾收集器状态和弱引用规则的对象。  

+ flashInterval指缓存过期时间，单位为毫秒，60000即为60秒，缺省值为空，即只要容量足够，永不过期。  
+ size指缓存多少个对象，默认值为1024。  
+ readOnly是否只读，如果为true，则所有相同的sql语句返回的是同一个对象（有助于提高性能，但并发操作同一条数据时，可能不安全），如果设置为false，则相同的sql，后面访问的是cache的clone副本。  

上面这个是全局设置，在每条单独的sql语句上，还可以有局部设置，比如：
```
<select id="getOrder" parameterType="int" resultType="TOrder"  useCache="false">
<!-- 设置不使用二级缓存 -->
        ...
</select>
```
对更新语句限制不刷新缓存，flushCache 默认为true，刷新缓存
```
<insertid="insertUser" parameterType="cn.itcast.mybatis.po.User" flushCache="false">
```
