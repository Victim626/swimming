# MyBatis XML方式 vs 注解方式对比说明

## 一、主要区别

### 1. 代码结构对比

#### 注解方式（原代码）
```java
@Mapper
public interface ServiceWorkerMapper {
    @Insert("insert into service_workers(worker_no,real_name,phone,service_level,create_time) values(#{workerNo},#{realName},#{phone},#{serviceLevel},#{createTime})")
    int insert(ServiceWorker serviceWorker);
    
    @Update("update service_workers set worker_no=#{workerNo},real_name=#{realName},phone=#{phone},service_level=#{serviceLevel} where id=#{id}")
    int update(ServiceWorker serviceWorker);
}
```

**特点：**
- SQL语句直接写在Java代码中
- 简洁，适合简单SQL
- SQL和Java代码耦合在一起
- 复杂SQL不易维护

#### XML方式（新代码）
```java
// Mapper接口 - 只有方法声明
@Mapper
public interface ServiceWorkerMapper {
    int insert(ServiceWorker serviceWorker);
    int update(ServiceWorker serviceWorker);
}
```

```xml
<!-- ServiceWorkerMapper.xml - SQL在XML中 -->
<mapper namespace="com.usbtj.springboot.springbootdemo.mapper.ServiceWorkerMapper">
    <insert id="insert" parameterType="...ServiceWorker" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO service_workers(worker_no, real_name, phone, service_level, create_time)
        VALUES(#{workerNo}, #{realName}, #{phone}, #{serviceLevel}, #{createTime})
    </insert>
</mapper>
```

**特点：**
- SQL与Java代码分离
- 适合复杂SQL（动态SQL、多表关联等）
- 便于DBA审查和优化SQL
- 修改SQL不需要重新编译Java代码

---

## 二、XML方式的优势

### 1. **动态SQL支持**
XML方式可以使用MyBatis强大的动态SQL标签：

```xml
<select id="selectByCondition" resultType="...ServiceWorker">
    SELECT * FROM service_workers
    <where>
        <if test="workerNo != null">
            AND worker_no = #{workerNo}
        </if>
        <if test="realName != null">
            AND real_name LIKE CONCAT('%', #{realName}, '%')
        </if>
        <if test="serviceLevel != null">
            AND service_level = #{serviceLevel}
        </if>
    </where>
</select>
```

### 2. **复杂SQL更易维护**
- 多表关联查询
- 嵌套结果映射
- SQL格式化清晰

### 3. **团队协作更好**
- 开发人员写Java代码
- DBA可以独立优化SQL
- 代码审查更方便

### 4. **热部署友好**
- 修改XML文件不需要重新编译Java代码
- 某些情况下可以直接生效

---

## 三、测试步骤

### 启动项目后，按顺序测试：

#### 1. 新增服务人员（POST）
```
POST http://localhost:8081/api/workers
Content-Type: application/json

{
  "workerNo": "SW001",
  "realName": "张三",
  "phone": "13800138001",
  "serviceLevel": "高级"
}
```

#### 2. 查询所有（GET）
```
GET http://localhost:8081/api/workers
```

#### 3. 更新服务人员（PUT）
```
PUT http://localhost:8081/api/workers/1
Content-Type: application/json

{
  "workerNo": "SW001",
  "realName": "张三(已更新)",
  "phone": "13800138001",
  "serviceLevel": "特级"
}
```

#### 4. 删除服务人员（DELETE）
```
DELETE http://localhost:8081/api/workers/1
```

---

## 四、提交的文件清单

1. **Controller**: `ServiceWorkerController.java`
   - 路径: `src/main/java/com/usbtj/springboot/springbootdemo/controller/ServiceWorkerController.java`
   
2. **Mapper XML**: `ServiceWorkerMapper.xml`
   - 路径: `src/main/resources/mapper/ServiceWorkerMapper.xml`

3. **Mapper接口**: `ServiceWorkerMapper.java` (已修改为XML方式)
   - 路径: `src/main/java/com/usbtj/springboot/springbootdemo/mapper/ServiceWorkerMapper.java`

---

## 五、核心配置

application.yml中需要添加：
```yaml
mybatis:
  configuration:
    map-underscore-to-camel-case: true  # 下划线转驼峰
  mapper-locations: classpath:mapper/*.xml  # XML文件位置
```

---

## 六、注意事项

1. **XML文件位置**: 必须放在 `src/main/resources/mapper/` 目录下
2. **namespace**: XML中的namespace必须与Mapper接口的全限定名一致
3. **id对应**: XML中的id必须与Mapper接口中的方法名一致
4. **参数类型**: insert/update需要指定parameterType
5. **主键回填**: insert时使用 `useGeneratedKeys="true" keyProperty="id"` 可以自动回填主键
