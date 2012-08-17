---
title: Testing components and controllers
layout: page
language: en
section: 15
category: [en, docs]
---

VRaptor3 manages your class dependencies, so there is no need to worry about instantiating your components and controllers, you can just receive your dependencies on the constructor and VRaptor3 will locate them and instantiate your class.
You can take advantage of dependency injection when testing your classes: you can instantiate your class with fake implementations and unit test the class.
Nevertheless, there are two VRaptor3 components that are dependencies of most of your controllers: Result and Validator. Their fluent interfaces makes it difficult to create fake implementations or mocks. Therefore there are fake implementations for these components on VRaptor3: MockResult e MockValidator.

<h3>MockResult</h3>

MockResult ignores all redirects, and stores the included objects, so you can inspect them and make assertions.
This snippet shows you how you can use MockResult:

{% highlight java %}
MockResult result = new MockResult();
ClientController controller = new ClientController(..., result);
controller.list(); // will call result.include("clients", something);
List<Client> clients = result.included("clients"); // the cast is implicit
Assert.assertNotNull(clients);
// more assertions
{% endhighlight %}

Any calls to result.use(...) will be ignored.

<h3>MockValidator</h3>

MockValidator will store generated errors, so if there is any error when validator.onErrorUse is called, a ValidationError will be thrown. Therefore you can inspect the added errors, or simply check if there is any error.

{% highlight java %}
@Test(expected=ValidationException.class)
public void testThatAValidationErrorOccurs() {
    ClientController controller = new ClientController(..., new MockValidator());
    controller.add(new Client());
}
{% endhighlight %}

or

{% highlight java %}
@Test
public void testThatAValidationErrorOccurs() {
    ClientController controller = new ClientController(..., new MockValidator());
    try {
        controller.add(new Client());
        Assert.fail();
    } catch (ValidationException e) {
        List<Message> errors = e.getErrors();
        //assertions on errors
    }
}
{% endhighlight %}

If one uses Hibernate Validator, and calls validator.validate(object) on the controller, one can use the HibernateMockValidator class instead, which will validate the object with the defined rules from HV.
