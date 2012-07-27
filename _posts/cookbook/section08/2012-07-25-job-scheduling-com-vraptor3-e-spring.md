---
section: 8
title: Job scheduling com VRaptor e Spring
language: pt
category: cookbook
layout: page
---

<h4>por Mário Peixoto no <a href="http://mariopeixoto.wordpress.com/2009/11/17/job-scheduling-com-vraptor3-e-spring3/">blog</a></h4>

Este artigo tem como objetivo mostrar um caminho simples para fazer job scheduling no vRaptor3 quando o Spring for o seu container IoC sem que seja necessário o uso de XML para configuração.

Versões utilizadas: vRaptor 3.0.2, Spring 3.0.0.RC2.

<h1>O Task Scheduler</h1>

O Spring já vem com suporte a task scheduling/executing, ele precisa apenas que uma implementação de org.springframework.scheduling.TaskScheduler esteja disponível no escopo de aplicação (singleton no Spring). Para isso precisamos implementar um ComponentFactory:

{% highlight java %}
@Component
@ApplicationScoped
public class TaskSchedulerFactory implements ComponentFactory<TaskScheduler> {

    private ThreadPoolTaskScheduler scheduler;
     
    @PostConstruct
    public void create() {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
    }
     
    public TaskScheduler getInstance() {
        return scheduler;
    }
     
    @PreDestroy
    public void destroy() {
        scheduler.destroy();
    }
     
}
{% endhighlight %}

Neste exemplo utilizaremos o ThreadPoolTaskScheduler como implementação para o TaskScheduler, porém várias outras classes vem com o Spring e podem ser usadas da mesma maneira (documentação).

<h1>As Tasks/Jobs</h1>

Vamos criar interfaces para nossas tasks.

{% highlight java %}
public interface Task extends Runnable {
    void schedule(TaskScheduler scheduler);
}

public interface ApplicationTask extends Task {
}

public interface RequestTask extends Task {
}
Registrando tasks em escopo de application
Para registrar esse tipo de task, precisaremos de um bean em escopo de application:
@Component
@ApplicationScoped
public class MyFirstApplicationTask implements ApplicationTask {
    public MyFirstApplicationTask(TaskScheduler scheduler) { 
        //Aqui você poderá receber componentes que não estejam em 
        //escopo de request ou session
        ...
        this.schedule(scheduler);
    }
    public void schedule(TaskScheduler scheduler) {
        scheduler.schedule(this, new CronTrigger("0 0 23 * * *")); 
        //Neste caso, a task rodará sempre às 23h0min0s
    }
}
{% endhighlight %}

<h1>Registrando tasks em escopo de request</h1>

Para registrar esse tipo de task, basta receber o scheduler no construtor do seu Resource e registrar a task no método adequado.

{% highlight java %}
@Component
@RequestScoped
public class MyFirstRequestTask implements RequestTask {
    public MyFirstApplicationTask() { 
        //Aqui você poderá receber qualquer componente que precisar
        ...
    }
    public void schedule(TaskScheduler schedule) {
        Calendar now = GregorianCalendar.getInstance();
        now.add(Calendar.MINUTE,5);
        scheduler.schedule(this, now.getTime()); 
        //Neste caso, a task rodará apenas uma vez, 5 min depois da 
        //execução deste método
    }
}
@Resource
public class MyResource {
    private MyFirstRequestTask task;
    private TaskScheduler scheduler;
    public MyResource(MyFirstRequestTask task, TaskScheduler scheduler) {
        this.task = task;
        this.scheduler = scheduler;
    }
    ...
    public void taskInit() {
        task.schedule(scheduler);
    }
    ...
}
{% endhighlight %}

<h1>Observações</h1>

Criamos 3 interfaces, pois deste modo ficará mais fácil injetar diferentes tipos de tasks no construtor, por exemplo podemos receber todas as tasks em escopo de request dentro de um resource assim:

{% highlight java %}
@Resource
public class MyResource {
    private List<RequestTask> tasks;
    private TaskScheduler scheduler;
    public MyResource(List<RequestTask> tasks, TaskScheduler scheduler) {
        this.tasks = tasks;
        this.scheduler = scheduler;
    }
    ...
    public void tasksInit() {
        for (RequestTask task : tasks) {
            task.schedule(scheduler);
        }
    }
    ...
}
{% endhighlight %}

<h1>O escopo Prototype</h1>

O Spring possui um escopo chamado prototype que define que o componente terá uma nova instância sempre que for requisitada, este escopo ainda não existe no VRaptor3 mas será implementado em breve. Este escopo servirá para definir compenentes utilizados por tasks em escopo de application que naturalmente não são singletons, como DAOs.
Você não poderá receber a Session no construtor do seu DAO prototyped pois geralmente a SessionFactory está em escopo de request, e o vRaptor ainda não possui suporte às scoped-proxies do Spring. Então precisará fazer assim:

{% highlight java %}
@Component
@PrototypeScoped
public class MyPrototypedDAO extends ... implements ... {

    private Session session;

    public MyPrototypedDAO(SessionFactory sessionFactory) {
        this.session = sessionFactory.openSession();
    }

    ...
}
{% endhighlight %}

Deste modo, será criado uma instância do seu DAO para cada requisição de instância (lê-se: sempre que você receber uma instância pelo construtor, será uma nova), assim como uma Session separada.
