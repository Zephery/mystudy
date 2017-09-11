## Quartz
先看一下Quartz的架构图：  
<div align="center">

![](http://ohlrxdl4p.bkt.clouddn.com/11627468_1438829844Zbz8.png)

</div>

### 一.特点：
1. 强大的调度功能，例如支持丰富多样的调度方法，可以满足各种常规及特殊需求；
2. 灵活的应用方式，例如支持任务和调度的多种组合方式，支持调度数据的多种存储方式；
3. 分布式和集群能力。

### 二.主要组成部分
1. JobDetail：需实现该接口定义的人物，其中JobExecutionContext提供了上下文的各种信息。
2. JobDetail：QUartz的执行任务的类，通过newInstance的反射机制实例化Job。
3. Trigger： Job的时间触发规则。主要有SimpleTriggerImpl和CronTriggerImpl两个实现类。
4. Calendar：org.quartz.Calendar和java.util.Calendar不同，它是一些日历特定时间点的集合（可以简单地将org.quartz.Calendar看作java.util.Calendar的集合——java.util.Calendar代表一个日历时间点，无特殊说明后面的Calendar即指org.quartz.Calendar）。
5. Scheduler：由上图可以看出，Scheduler是Quartz独立运行的容器。其中，Trigger和JobDetail可以注册到Scheduler中。
6. ThreadPool：Scheduler使用一个线程池作为任务运行的基础设施，任务通过共享线程池中的线程提高运行效率。
### 三、Quartz设计
1. properties file  
[官网](http://www.quartz-scheduler.org/documentation/quartz-2.2.x/quick-start.html)中表明：quartz中使用了quartz.properties来对quartz进行配置，并保留在其jar包中，如果没有定义则默认使用改文件。
2.Trigger的实现类
（1）SimpleTrigger  
指某一个时间开始，或者从现在开始以一定的间隔执行任务。
其属性有
```java
private Date startTime;
private Date endTime;
private Date nextFireTime;
private Date previousFireTime;
private int repeatCount;  //重复次数
private long repeatInterval;//重复间隔
private int timesTriggered;
private boolean complete;
```
（2）CronTrigger  
类似于linux cron中的语法，能够设置任何时间，重复次数等运行。
详情请使用[cron表达式](http://cron.qqe2.com/)来查询。  
3. Job并发

4. JobListener
先看一下JobListener的源码：
```java
public interface JobListener {
    String getName();//某个joblistener的名字
    void jobToBeExecuted(JobExecutionContext var1);//即将运行
    void jobExecutionVetoed(JobExecutionContext var1);//运行后
    void jobWasExecuted(JobExecutionContext var1, JobExecutionException var2);//取消
}
```
无非就是对某个任务的运行前、运行后、以及取消的侦听。次数使用观察者模式，对人物进行广播侦听。


### 四、使用
1. hello world！代码[在这]()
（1）定义一个job
```java
public class HelloJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("Hello World! - " + new Date());
    }
}
```
（2）运行（来自官网修改的例子，官网那个例子需要等90秒）。。。坑
```java
public class SimpleExample {
    public static void main(String[] args) throws Exception {
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        JobDetail job = JobBuilder.newJob(HelloJob.class)
                .withIdentity("job1", "group1")
                .build();
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1")
                .startAt(DateTime.now().plusMillis(4).toDate())//固定一个时间，现在设置为程序启动后4秒
                .build();
        sched.start();
        sched.scheduleJob(job, trigger);
        //启动后要sleep一下，不然立即关闭
        Thread.sleep(6);
        sched.shutdown(true);
    }
}
```
（3）运行结果：
```html
Hello World! - Mon Sep 11 15:55:53 CST 2017
```
2. 自定义监听器
（1）自定义listener
```java
public class MyJobListener implements JobListener {
    @Override//相当于为我们的监听器命名
    public String getName() {
        return "myJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        System.out.println(getName() + "触发对" + context.getJobDetail().getJobClass() + "的开始执行的监听工作，这里可以完成任务前的一些资源准备工作或日志记录");
    }

    @Override//“否决JobDetail”是在Triiger被其相应的监听器监听时才具备的能力
    public void jobExecutionVetoed(JobExecutionContext context) {
        System.out.println("被否决执行了，可以做些日志记录。");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context,
                               JobExecutionException jobException) {
        System.out.println(getName() + "触发对" + context.getJobDetail().getJobClass() + "结束执行的监听工作，这里可以进行资源销毁工作");

    }

}
```
（2）跟上例一样，只需在Scheduler上增加监听器即可。
```java
JobListener myJobListener = new MyJobListener();
KeyMatcher<JobKey> keyMatcher = KeyMatcher.keyEquals(job.getKey());
sched.getListenerManager().addJobListener(myJobListener, keyMatcher);
```
（3）结果：
```html
myJobListener触发对class com.quartz.hello.HelloJob的开始执行的监听工作，这里可以完成任务前的一些资源准备工作或日志记录
Hello World! - Mon Sep 11 16:57:56 CST 2017
myJobListener触发对class com.quartz.hello.HelloJob结束执行的监听工作，这里可以进行资源销毁工作或做一些新闻扒取结果的统计工作
```

3. 本网站中使用quartz来对数据库进行备份，与Spring结合
（1）导入spring的拓展包，其协助spring集成第三方库：邮件服务、定时任务、缓存等。。。
```html
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>4.2.6.RELEASE</version>
</dependency>
```
（2）导入quartz包
```html
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
    <version>2.3.0</version>
</dependency>
```
（3）mysql远程备份
使用命令行工具仅仅需要一行：
```html
mysqldump -u [username] -p[password] -h [hostip] database > file
```
但是java不能直接执行linux的命令，仍旧需要依赖第三方库ganymed
```html
<dependency>
    <groupId>ch.ethz.ganymed</groupId>
    <artifactId>ganymed-ssh2</artifactId>
    <version>262</version>
</dependency>
```
完整代码如下：
```java
@Component("mysqlService")//在spring中注册一个mysqlService的Bean
public class MysqlUtil {
    ...
    StringBuffer sb = new StringBuffer();
    sb.append("mysqldump -u " + username + " -p" + password + " -h " + host + " " +
            database + " >" + file);
    String sql = sb.toString();
    Connection connection = new Connection(s_host);
    connection.connect();
    boolean isAuth = connection.authenticateWithPassword(s_username, s_password);//进行远程服务器登陆认证
    if (!isAuth) {
        logger.error("server login error");
    }
    Session session = connection.openSession();
    session.execCommand(sql);//执行linux语句
    InputStream stdout = new StreamGobbler(session.getStdout());
    BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
}
```
（4）spring中配置quartz
```xml
<bean id="jobDetail" class="org.springframework.scheduling.quartz.MethodInvokingJobDetailFactoryBean">
    <property name="targetObject" ref="mysqlService"/>
    <property name="targetMethod" value="exportDataBase"/>
</bean>
<!--定义触发时间  -->
<bean id="myTrigger" class="org.springframework.scheduling.quartz.CronTriggerFactoryBean">
    <property name="jobDetail" ref="jobDetail"/>
    <!-- cron表达式，每周五2点59分运行-->
    <property name="cronExpression" value="0 59 2 ? * FRI"/>
</bean>
<!-- 总管理类 如果将lazy-init='false'那么容器启动就会执行调度程序 -->
<bean id="scheduler" class="org.springframework.scheduling.quartz.SchedulerFactoryBean">
    <property name="triggers">
        <list>
            <ref bean="myTrigger"/>
        </list>
    </property>
</bean>
```
（5）java完整文件[在这](https://github.com/Zephery/newblog/blob/master/src/main/java/com/myblog/util/MysqlUtil.java)

## Spring的高级特性之定时任务  
java ee项目的定时任务中除了运行quartz之外，spring3+还提供了task，可以看做是一个轻量级的Quartz，而且使用起来比Quartz简单的多。

1. spring配置文件中配置：
```html
<task:annotation-driven/>
```
2. 最简单的例子，在所需要的函数上添加定时任务即可运行
```java
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        System.out.println("每隔5秒运行一次" + sdf.format(new Date()));
    }
```
3. 运行的时候会报错：
```html
org.springframework.beans.factory.NoSuchBeanDefinitionException: No qualifying bean of type [org.springframework.scheduling.TaskScheduler] is defined
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:372)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.getBean(DefaultListableBeanFactory.java:332)
	at org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.finishRegistration(ScheduledAnnotationBeanPostProcessor.java:192)
	at org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.onApplicationEvent(ScheduledAnnotationBeanPostProcessor.java:171)
	at org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor.onApplicationEvent(ScheduledAnnotationBeanPostProcessor.java:86)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:163)
	at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:136)
	at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:380)
```
Spring的定时任务调度器会尝试获取一个注册过的 task scheduler来做任务调度，它会尝试通过BeanFactory.getBean的方法来获取一个注册过的scheduler bean，获取的步骤如下：
(1) 尝试从配置中找到一个TaskScheduler Bean  
(2) 寻找ScheduledExecutorService Bean  
(3) 使用默认的scheduler  
修改log4j.properties即可：
log4j.logger.org.springframework.scheduling=INFO  
其实这个功能不影响定时器的功能。  
（4）结果：
```html
每隔5秒运行一次14:44:34
每隔5秒运行一次14:44:39
每隔5秒运行一次14:44:44
```

参考：  
(1) [http://blog.csdn.net/oarsman/article/details/52801877](http://blog.csdn.net/oarsman/article/details/52801877)  
(2) [http://stackoverflow.com/questions/31199888/spring-task-scheduler-no-qualifying-bean-of-type-org-springframework-scheduli](http://stackoverflow.com/questions/31199888/spring-task-scheduler-no-qualifying-bean-of-type-org-springframework-scheduli)
(3)[http://blog.csdn.net/qwe6112071/article/details/50991531](http://blog.csdn.net/qwe6112071/article/details/50991531)