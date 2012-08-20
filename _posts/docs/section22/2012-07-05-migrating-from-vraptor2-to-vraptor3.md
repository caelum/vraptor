---
title: Migrating from VRaptor2 to VRaptor3
layout: page
section: 22
category: [en, docs]
---

<h3>web.xml</h3>

In order to migrate, in small steps, you'll only need to put on your web.xml:

{% highlight xml %}
<context-param>
    <param-name>br.com.caelum.vraptor.provider</param-name>
    <param-value>br.com.caelum.vraptor.vraptor2.Provider</param-value>
</context-param>

<context-param>
    <param-name>br.com.caelum.vraptor.packages</param-name>
    <!-- Your base package here -->
    <param-value>com.companyname.projectname</param-value>
</context-param>
<filter>
    <filter-name>vraptor</filter-name>
    <filter-class>br.com.caelum.vraptor.VRaptor</filter-class>
</filter>

<filter-mapping>
    <filter-name>vraptor</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
{% endhighlight %}

and add all jars from lib/mandatory and lib/containers/<one of the containers> from vraptor zip on your classpath.

Don't forget to remove the old VRaptorServlet declaration from VRaptor2, and its respective servlet-mapping.


<h3>Migration from @org.vraptor.annotations.Component to @br.com.caelum.vraptor.Resource</h3>

VRaptor2's @Component correspondent in VRaptor3 is @Resource. Therefore, in order to make logic classes accessible, just annotate them with @Resource (removing the @Component).

The conventions used are slightly different:

In VRaptor2:
{% highlight java %}
@Component
public class ClientsLogic {
   
    public void form() {
   
    }
   
}
{% endhighlight %}

In VRaptor 3:
{% highlight java %}
@Resource
public class ClientsController {

   public void form() {
  
   }
}
{% endhighlight %}

The form method will be accessible from the URI: "/clients/form", and the default view will be WEB-INF/jsp/clients/form.jsp.

Which means, the suffix Controller is removed from the class name and there is no more .logic at the end of the URI. Also, the result jsp doesn't have either "ok" or "invalid" on its name.


<h3>@In</h3>

VRaptor3 manages the dependencies for you, so, what you were used to annotate with @In on VRaptor2, you'll only need to receive as constructor arguments:

In VRaptor 2:
{% highlight java %}
@Component
public class ClientsLogic {
    @In
    private ClientDao dao;
       
    public void form() {
   
    }
   
}
{% endhighlight %}

In VRaptor 3:
{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    public ClientsController(ClientDao dao) {
        this.dao = dao;
    }
   
    public void form() {
  
    }
}
{% endhighlight %}

In order for this to work, you only need that your ClientDao is annotated with VRaptor3's @br.com.caelum.vraptor.ioc.Component.


<h3>@Out and getters</h3>

In VRaptor2 you used either the @Out annotation or a getter method to make and object accessible to the view. In VRaptor3 you only need to return the specified object, if it's only one, or make use of a special object which exposes your objects to the view. This object is the Result.

In VRaptor 2:
{% highlight java %}
@Component
public class ClientsLogic {
    private Collection<Client> list;
   
    public void list() {
        this.list = dao.list();
    }
   
    public Collection<Client> getClientList() {
        return this.list;
    }

    @Out
    private Client client;
   
    public void show(Long id) {
        this.client = dao.load(id);
    }
   
}
{% endhighlight %}

In VRaptor 3:
{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
   
    public ClientsController(ClientDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }
   
    public Collection<Client> list() {
        return dao.list(); // the name will be "clientList"
    }
   
    public void anotherList() {
        result.include("clients", dao.list());
    }
   
    public Client show(Long id) {
        return dao.load(id); // the name will be "client"
    }
}
{% endhighlight %}

When your method's return type isn't void, VRaptor uses that type to find out which will be the object's name on the view. When not using the Result object, the name of the exposed object depends on the method's return type. If the return type is a Collection, the object name will be the name of the object contained by the Collection followed by the word List. In the above example, the object would be named 'clientList'. Otherwise, if the return type is a single object, the exposed object's name will be the name of the class with lowercase characters.


<h3>views.properties</h3>

In VRaptor3 there's no views.properties file, although it is supported when using VRaptor3's compatibility mode. Thus, all redirections are made on the underlying logic, using the Result object.
{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
   
    public ClientsController(ClientDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }

    public Collection<Client> list() {
        return dao.list();
    }

    public void save(Client client) {
        dao.save(client);
       
        result.redirectTo(ClientsController.class).list();
    }
}
{% endhighlight %}

If it's redirection to a logic, you can refer to it directly, and the given parameters will be passed to the called logic.

If you want to forward to a JSP page, you can use:
{% highlight java %}
result.forwardTo("/WEB-INF/jsp/clients/save.ok.jsp");
{% endhighlight %}


<h3>Validation</h3>

You don't need to create a method called validateLogicName in order to do the validation, you only need to receive the br.com.caelum.vraptor.Validator object in your logic's constructor, and use it to do your validation, specifying which logic to go when your validation fails.
{% highlight java %}
@Resource
public class ClientsController {

    private final ClientDao dao;
    private final Result result;
    private final Validator validator;
   
    public ClientsController(ClientDao dao, Result result, Validator validator) {
        this.dao = dao;
        this.result = result;
        this.validator = validator;
    }

    public void form() {
   
    }
   
    public void save(Client client) {
        if (client.getName() == null) {
            validator.add(new ValidationMessage("error","invalidName"));
        }
        validator.onErrorUse(Results.page()).of(ClientsController.class).form();
        dao.save(client);
    }
}
{% endhighlight %}

<h3>Putting objects on Session</h3>

On VRaptor2 it was enough an @Out(ScopeType.SESSION) for putting an object on HttpSession. It doesn't work on VRaptor3, because this way you lose control on your variables. So in VRaptor3 you have to do one of these two approaches:

Your object will be accessed only by components and controllers, not by jsps:

{% highlight java %}
    @Component
    @SessionScoped
    public class SessionMyObject {
        private MyObject myobject;
        //getter and setter
    }
{% endhighlight %}

And you receive on your classes constructors a SessionMyObject, and use getters and setters to handle the MyObject on session.

The object will be accessed in jsps too:

{% highlight java %}
    @Component
    @SessionScoped
    public class SessionMyObject {
        private HttpSession session;
        public SessionMyObject(HttpSession session) {
            this.session = session;
        }
        public void setMyObject(MyObject object) {
            this.session.setAttribute("object", object);
        }
        public MeuObjeto getMyObject() {
            return this.session.getAttribute("object");
        }
    }

{% endhighlight %}

When you need an instance of SessionMyObject in any of your classes just add a parameter of the type SessionMyObject in your classe's constructor.
