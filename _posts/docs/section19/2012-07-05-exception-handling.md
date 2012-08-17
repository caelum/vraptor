---
title: Exception handling
layout: page
language: en
section: 19
category: [en, docs]
---

Since version 3.2, VRaptor has an Exception Handler, which handler all unhandled exceptions in your application. The Exception Handler has a very similar behaviour than VRaptor Validator.

In the example below, if the method _addCustomer(Customer)_ throws a _CustomerAlreadyExistsException_, the user will be redirected to the method _addCustomer()_.

{% highlight java %}

@Get
public void addCustomer() {
    // do something
}

@Post
public void addCustomer(Customer newCustomer) {
    result.on(CustomerAlreadyExistsException.class).forwardTo(this).addCustomer();

    customerManager.store(newCustomer);
}
{% endhighlight %}
