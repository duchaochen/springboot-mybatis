## springboot整合mybatis

    现在有一个department表,字段为id，name，现在开始整合的一些准备工作
    1.创建一个springboot项目
    2.必备的maven依赖代码：
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>1.3.2</version>
            </dependency>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>1.1.10</version>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
         </dependencies>
         
    3.在全局的application.yml文件中配置以下代码：
        spring:
          datasource:
            driver-class-name: com.mysql.jdbc.Driver
            username: root
            password: root
            url: jdbc:mysql://192.168.25.144:3306/test?characterEncoding=utf-8
            type: com.alibaba.druid.pool.DruidDataSource
        
            initialSize: 5
            minIdle: 5
            maxActive: 20
            maxWait: 60000
            timeBetweenEvictionRunsMillis: 60000
            minEvictableIdleTimeMillis: 300000
            validationQuery: SELECT 1 FROM DUAL
            testWhileIdle: true
            testOnBorrow: false
            testOnReturn: false
            poolPreparedStatements: true
        #   配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
        #   不注释回报错，目前没有去找原因
        #    filters: stat,wall,log4j
            maxPoolPreparedStatementPerConnectionSize: 20
            useGlobalDataSourceStat: true
            connectionProperties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500
            
    4.由于上面的部分配置需要使用自定义配置类才能生效，所以需要创建一个自定义配置类
        代码：
            /**
             * 数据源配置类
             */
            @Configuration
            public class DruidConfig {
            
                @ConfigurationProperties(prefix = "spring.datasource")
                @Bean
                public DataSource dataSource() {
                    return new DruidDataSource();
                }
            
                /**
                 * 配置Druid的监控
                 * 1、配置一个管理后台的Servlet
                 * @return
                 */
                @Bean
                public ServletRegistrationBean statViewServlet(){
                    ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
                    Map<String,String> initParams = new HashMap<>();
            
                    initParams.put("loginUsername","admin");
                    initParams.put("loginPassword","123456");
                    //默认就是允许所有访问
                    initParams.put("allow","");
                    initParams.put("deny","192.168.105.105");
            
                    bean.setInitParameters(initParams);
                    return bean;
                }
            
            
                /**
                 * 2.配置一个web监控的filter
                 * @return
                 */
                @Bean
                public FilterRegistrationBean webStatFilter(){
                    FilterRegistrationBean bean = new FilterRegistrationBean();
                    bean.setFilter(new WebStatFilter());
            
                    Map<String,String> initParams = new HashMap<>(10);
                    initParams.put("exclusions","*.js,*.css,/druid/*");
            
                    bean.setInitParameters(initParams);
            
                    bean.setUrlPatterns(Arrays.asList("/*"));
            
                    return  bean;
                }
            }
            
    5.创建一个DepartmentPojo类，写上id和name属性，加上getset方法
    6.需要在启动类加上一个扫描注解，value的值是要扫描的mapper类的包
        代码：
        @MapperScan(value = "com.adu.mybatis.springboot.mapper")
        @SpringBootApplication
        public class SpringbootMybatisApplication {
        
        	public static void main(String[] args) {
        		SpringApplication.run(SpringbootMybatisApplication.class, args);
        	}
        }

### 注解方式
    
    1.创建一个DepartmentMapper类，写上添加和删除的方法,并且加上注解.
        代码：
            @Select("select * from department where id=#{id}")
            DepartmentPojo getById01(Integer id);
            
            /**
            * 使用驼峰命名法,keyProperty返回主键
            * @param dept
            */
            @Options(useGeneratedKeys = true,keyProperty = "id")
            @Insert("insert into department(name) values(#{name})")
            void insertDept01(DepartmentPojo dept);
    2.创建一个DepartmentController调用以上2个方法
        代码：
            @RestController
            public class DepartmentController {
            
                @Autowired
                private DepartmentMapper departmentMapper;
            
                @GetMapping("/dept/{id}")
                public DepartmentPojo getById01(@PathVariable("id") Integer id) {
                    DepartmentPojo byId01 = departmentMapper.getById01(id);
                    return byId01;
                }
            
                @PostMapping("/dept")
                public DepartmentPojo insertDept01(DepartmentPojo departmentPojo) {
                    departmentMapper.insertDept01(departmentPojo);
                    return departmentPojo;
                }
             }
        
    3.启动之后调用
        根据id查询方法链接：http://localhost:8080/dept/1
        添加方法使用的是post方式调用，所以我使用的是Postman这个软件模仿post提交方式
        链接调用：http://localhost:8080/dept
        
### 配置方法
        
    1.直接在刚刚创建的DepartmentMapper类中添加2个无注解的方法
        代码：
             //根据id查询
             DepartmentPojo getById02(Integer id);
             //添加部门
             void insertDept02(DepartmentPojo dept);
        
    2.创建一个mybatis的全局配置文件mybatis-config.xml,路径为类路径下
        代码如下：
            <?xml version="1.0" encoding="UTF-8" ?>
            <!DOCTYPE configuration
                    PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
                    "http://mybatis.org/dtd/mybatis-3-config.dtd">
            <configuration>
            
            </configuration>
            
    3.创建一个mapper配置文件dept.xml，路径为类路径下的mybatis文件夹下
        代码如下；
            <?xml version="1.0" encoding="UTF-8" ?>
            <!DOCTYPE mapper
                    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
                    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
            <mapper namespace="com.adu.mybatis.springboot.mapper.DepartmentMapper">
                <select id="getById02" parameterType="int" resultType="com.adu.mybatis.springboot.pojo.DepartmentPojo">
                    select * from department where id=#{id}
                </select>
                
                <insert id="insertDept02" parameterType="com.adu.mybatis.springboot.pojo.DepartmentPojo">
                    <selectKey keyProperty="id" resultType="int" order="AFTER">
                        select LAST_INSERT_ID();
                    </selectKey>
                    insert into department(name) values(#{name})
                </insert>
            </mapper>
            
    4.在全局的application.yml文件中加上以下代码：
        mybatis:
          # 指定全局配置文件位置
          config-location: classpath:mybatis/mybatis-config.xml
          # 指定sql映射文件位置
          mapper-locations: classpath:mybatis/mapper/*.xml
     
    5.启动之后调用
          根据id查询方法链接：http://localhost:8080/dept2/1
          
          添加方法使用的是post方式调用，所以我使用的是Postman这个软件模仿post提交方式
          链接调用：http://localhost:8080/dept2
      
       