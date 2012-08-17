---
title: VRaptor3 - 10 minute guide
layout: page
language: en
section: 2
category: [en, docs]
---

<h3>Starting a project: an online store</h3>

Let's start by downloading the vraptor-blank-project from <a href="http://vraptor.caelum.com.br/download.jsp">http://vraptor.caelum.com.br/download.jsp</a>. This blank-project has the required configuration on web.xml and the dependencies on WEB-INF/lib that are needed to start using VRaptor.
As you can see, the only required configuration in web.xml is the VRaptor filter:

{% highlight xml %}
<filter>
    <filter-name>vraptor</filter-name>
    <filter-class>br.com.caelum.vraptor.VRaptor</filter-class>
</filter>

<filter-mapping>
    <filter-name>vraptor</filter-name>
    <url-pattern>/*</url-pattern>
    <dispatcher>FORWARD</dispatcher>
    <dispatcher>REQUEST</dispatcher>
</filter-mapping>
{% endhighlight %}

You can also easily import this project on Eclipse, and run it right clicking on it and choosing Run as... / Run on server.... Choose a servlet container (or setup a new one) and then just go for <a href="http://localhost:8080/vraptor-blank-project/">http://localhost:8080/vraptor-blank-project/</a>.
You can right click on the project name, go to Properties and in Web Project Settings you can change the context name to something better, like onlinestore. Now if you run this example you should be able to access <a href="http://localhost:8080/onlinestore">http://localhost:8080/onlinestore</a> and see <strong>It works!</strong> on the browser.

<div class="nota">
<h4>Note</h4>
If you are using a servlet 3.0 container, you dont even need the web.xml at all, because VRaptor will configure the filter through the new web-fragments feature.
</div>

<h3>Product registry</h3>

Let's start the system with a products registry. We need a class that will represent the products, and we'll use it to persist products on the database, with Hibernate:

{% highlight java %}
@Entity
public class Product {
    @Id
    @GeneratedValue
    private Long id;
    
    private String name;
    private String description;
    private Double price;
    //getter and setters
}
{% endhighlight %}

We also need a class that will control the products' register, handling web requests. This class will be the Products Controller:

{% highlight java %}
public class ProductsController {
}
{% endhighlight %}

ProductsController will expose URIs to be accessed through web, i.e, will expose resources of your application. And for indicate it, you must annotate it with @Resource:

{% highlight java %}
@Resource
public class ProductsController {
}
{% endhighlight %}

By using this annotation, all public methods of the annotated class will be reachable through web. For instance, if there is a list method on the class:

{% highlight java %}
@Resource
public class ProductsController {
    public List<Product> list() {
        return new ArrayList<Product>();
    }
}
{% endhighlight %}

Then, VRaptor will automatically redirect all requests to the URI /products/list to this method. The convention for URIs is: /<controller_name>/<method_name>.
At the end of method execution, VRaptor will dispatch the request to the jsp at /WEB-INF/jsp/products/list.jsp. The convention for the default view is /WEB-INF/jsp/<controller_name>/<method_name>.jsp.
The list method will return a product list, so how can I get it on jsp? On VRaptor, the method return value will be exported to the jsp by request attributes. In this case, the name of the exported attribute will be productList, holding the method returned value:
list.jsp

{% highlight jsp %}
<ul>
<c:forEach items="${productList}" var="product">
    <li> ${product.name} - ${product.description} </li>
</c:forEach>
</ul>
{% endhighlight %}

The convention for the attribute names is pretty intuitive: if it is a collection, as it is the case, the name will be <collection_type>List; if it is any other type, the name will be the class name with the first letter in lowercase, i.e, if the type is Product, the name will be product.
We will see further, in another chapter, that we can outject more than one object using Result, where we can also name each exposed variable to the JSP.

<h3>Creating ProductDao: Dependency Injection</h3>

VRaptor widely uses the Dependency Injection and Inversion of Control concept. The whole idea is simple: if you need a resource, you won't create it, but will have it ready for you when you ask for it. You can get more information about it on the Dependency Injection chapter.
We are returning a hard coded empty list on our list method. It would be more helpful if we return a real list, for example all of registered products of the system. In order to do that, let's create a product DAO, for listing the products:

{% highlight java %}
public class ProductDao {

    public List<Product> listAll() {
        return new ArrayList<Product>();
    }
}
{% endhighlight %}

And in the ProductsController we might use the dao for listing products:

{% highlight java %}
@Resource
public class ProductsController {
    
    private ProductDao dao;
    
    public List<Product> list() {
        return dao.listAll();
    }
}
{% endhighlight %}

We could create a new ProductDao inside the controller, but we can simply loose coupling by receiving it on the class constructor, and letting VRaptor do its Dependency Management Magic and provide an instance of ProductDao when creating our controller! And for enabling this behavior we only have to annotate the ProductDao class with @Component:

{% highlight java %}
@Component
public class ProductDao {
    //...
}

@Resource
public class ProductsController {
    
    private ProductDao dao;

    public ProductsController(ProductDao dao) {
        this.dao = dao;
    }
        
    public List<Product> list() {
        return dao.listAll();
    }
}
{% endhighlight %}

<h3>Add form: redirecting the request</h3>

We have a Products listing, but no way to register products. Thus, let's create a form for adding products. Since it is not a good idea to access the jsps directly, let's create an empty method that only redirects to a jsp:

{% highlight java %}
@Resource
public class ProductsController {
    //...
    public void form() {
    }
}
{% endhighlight %}

So we can access the form by URI /products/form, and the form will be at /WEB-INF/jsp/products/form.jsp:

{% highlight jsp %}
<form action="<c:url value='/products/adiciona'/>">
    Name:             <input type="text" name="product.name" /><br/>
    Description:<input type="text" name="product.description" /><br/>
    Price:            <input type="text" name="product.price" /><br/>
    <input type="submit" value="Save" />
</form>
{% endhighlight %}

This form will save a product using the URI /products/add, so we must create this method on the controller:

{% highlight java %}
@Resource
public class ProductsController {
    //...
    public void add() {
    }
}
{% endhighlight %}

Look at the input names: <strong>product.name</strong>, <strong>product.description</strong> and <strong>product.price</strong>. If we receive a Product named product as parameter on add method, VRaptor will set the fields <strong>name</strong>, <strong>description</strong> and <strong>price</strong> with the input values, using the corresponding setters on Product. The <strong>product.price</strong> parameter will also be converted into Double before being set on the product. More information on Converters chapter.
Thus, having the correct names on the form inputs, we can create the add method:

{% highlight java %}
@Resource
public class ProductsController {
    //...
    public void add(Product product) {
        dao.save(product);
    }
}
{% endhighlight %}

Right after saving something on a form we usually want to be redirected to the listing or back to the form. In this case we want to be redirected to the products listing. For this purpose there is a VRaptor component: the Result. It is responsible for adding attributes on the request, and for dispatching to a different view. To get a Result instance you must receive it as a constructor parameter:

{% highlight java %}
@Resource
public class ProductsController {
    public ProductsController(ProductDao dao, Result result) {
        this.dao = dao;
        this.result = result;
    }
}
{% endhighlight %}

In order to redirect to the listing, you can use the result object:

{% highlight java %}
result.redirectTo(ProductsController.class).list();
{% endhighlight %}

This code snippet can be read as: As result, redirect to the list method in ProductsController. All redirect configuration is 100% java code, with no strings involved! It's clear from the code that the result from your logic is not the default, and which one you're using. There is no need to worry about configuration files. Furthermore, if you need to rename the list method, there is no need to go through your entire application looking for redirects to this method, just use your usual refactoring IDE to do the rename.
Our add method would look like this:

{% highlight java %}
public void add(Product product) {
    dao.add(product);
    result.redirectTo(ProductsController.class).list();
}
{% endhighlight %}

You can get more info on Result at the Views and Ajax chapter.

<h3>Validation</h3>

It wouldn't make sense adding a nameless product in the system, nor a negative value for it's price. Before adding the product, we need to check if it is a valid product - which has a name and a positive price. In case it's not valid, we want to get back to the form and show error messages.
In order to do that, we can use a VRaptor component: the Validator. You can get it in your Controller's constructor and use it like this:

{% highlight java %}
@Resource
public class ProductsController {
    public ProductsController(ProductDao dao, Result result, Validator validator) {
        //...
        this.validator = validator;
    }
    
    public void add(Product product) {
        validator.checking(new Validations() { {
            that(!product.getName().isEmpty(), "product.name", "nome.empty");
            that(product.getPrice() > 0, "product.price", "price.invalid");
        } });
        validator.onErrorUsePageOf(ProductsController.class).form();
        
        dao.add(product);
        result.redirectTo(ProductsController.class).list();
    }
}
{% endhighlight %}

we can read the validation code as Validate that the name of the product is not empty and that the product's price is bigger than zero. If an error occur, use the ProductsController form page as the result. Therefore, if the product name is empty, the "name.empty" internationalized message will be added to the "product.name" field. If any error occurs, the system will get the user back to the form page, with all fields set, and error messages that can be accessed like this:

{% highlight jsp %}
<c:forEach var="error" items="${errors}">
    ${error.category}  ${error.message}<br />
</c:forEach>
{% endhighlight %}

More information on Validation on, well, Validations chapter.
If you learnt what we said so far, you're able to make 90% of your application. Next sessions on this tutorial show the solution for some of the most frequent problems that lay on that 10% left.
[sectin Using Hibernate to store Products]
Let's make a real implementation of ProductDao, now, using Hibernate to persist products. You'll need a Session in your ProductDao. Using injection of dependencies, you'll have to declare you'll receive a Session in your constructor.

{% highlight java %}
@Component
public class ProductDao {
    
    private Session session;
    
    public ProductDao(Session session) {
        this.session = session;
    }

    public void add(Product product) {
        session.save(product);
    }
    //...
}
{% endhighlight %}

But, wait, for VRaptor to know how to create that Session, and I can't simply put a @Component on the Session class because it is a Hibernate class. That's the reason why the ComponentFactory interface was created. More info on creating your own ComponentFactories can be found in Components chapter. You can also use the ComponentFactories available in VRaptor, as shown in the Utils chapter.

<h3>Controlling transactions: Interceptors</h3>

We often want to intercept as requests (or some of them) and execute a business logic, such as in a transaction control. That why VRaptor has interceptors. Learn more about them on the Interceptors' chapter. There is also an implemented TransactionInterceptor in VRaptor - learn how to use it on the Utils chapter.

<h3>Shopping Cart: session components</h3>

If we want to make a shopping cart in our system, we need some way to keep cart items in the user's session. In order to do it, we can create a session scoped component, i.e., a component that will last as long as the user session last. For that, simply create a component and annotate it with

{% highlight java %}
@SessionScoped:
@Component
@SessionScoped
public class ShoppingCart {
    private List<Product> items = new ArrayList<Product>();
    
    public List<Product> getAllItems() {
        return items;
    }
    
    public void addItem(Product item) {
        items.add(item);
    }
}
{% endhighlight %}

As this shopping cart is a component, we can receive it on the shopping cart's Controller's constructor:

{% highlight java %}
@Resource
public class ShoppingCartController {

    private final ShoppingCart cart;
    
    public ShoppingCartController(ShoppingCart cart) {
        this.cart = cart;
    }

    public void add(Product product) {
        cart.addItem(product);
    }
    
    public List<Product> listItems() {
        return cart.getAllItems();
    }
}
{% endhighlight %}

Besides session scope, there is also the application scope and the @ApplicationScoped annotation. Components annotated with @ApplicationScoped will be created only once for the whole application.

<h3>A bit of REST</h3>

On REST's ideal of URIs identifying resources on the web to make good use of the structural advantages the HTTP protocol provides us, observe how simple it is, in VRaptor, mapping the different HTTP methods in the same URI to invoke different Controllers' methods. Suppose we want to use the following URIs on the products' crud:

{% highlight jsp %}
	GET /products - list all products
	POST /products - insert a product
	GET /products/{id} - show product identified by id
	PUT /products/{id} - update product identified by id
	DELETE /products/{id} - delete product identified by id
{% endhighlight %}

In order to create a REST behaviour in VRaptor, we can use the @Path annotations - that changes the URI to access a given method. Also, we use the annotations that indicate which HTTP methods are allowed to call that logic - @Get, @Post, @Delete and @Put.
A REST version of our ProductsControler would be something like that:

{% highlight java %}
public class ProductsController {
    //...
    
    @Get @Path("/products")
    public List<Product> list() {...}
    
    @Post("/products")
    public void add(Product product) {...}

    @Get("/products/{product.id}")
    public void view(Product product) {...}
    
    @Put("/products/{product.id}")
    public void update(Product product) {...}
    
    @Delete("/products/{product.id}")
    public void remove(Product product) {...}
    
}
{% endhighlight %}

Note we can receive parameters on the URIs. For instance, if we can the <strong>GET /products/5</strong> URI, the view method will be invoked and the product parameter will have its id set as 5.
More info on that are on the REST Resources chapter.

<h3>Message bundle File</h3>

Internationalization (i18n) is a powerful feature present in almost all Web frameworks nowadays. And it's no different with VRaptor3. With i18n you can make your applications support several different languages (such as French, Portuguese, Spanish, English, etc) in a very easy way: simply translating the application messages.
In order to support i18n, you must create a file called messages.properties and make it available in your application classpath (WEB-INF/classes). That file contains lines which are a set of key/value entries, for example:

{% highlight jsp%}
field.userName = Username 
field.password = Password
{% endhighlight %}

So far, it's easy, but what if you want to create files containing messages in other languages, for example, Portuguese? Also easy. You just need to create another properties file called messages_pt_BR.properties. Notice the suffix _pt_BR on the file name. It indicates that when the user access your application from his computer configured with Brazilian Portuguese locale, the messages in this file will be used. The file contents would be:

{% highlight jsp %}
field.userName = Nome do Usuário
field.password = Senha
{% endhighlight %}

Notice that the keys are the same in both files, what changes is the value to the specific language.
In order to use those messages in your JSP files, you could use JSTL. The code would go as follows:

{% highlight jsp %}
<html> 
    <body> 
        <fmt:message key="field.userName" /> <input name="user.userName" /> 
         
        <br /> 
        
        <fmt:message key="field.password" /> <input type="password" name="user.password" /> 
        
        <input type="submit" /> 
    </body> 
</html>
{% endhighlight %}
