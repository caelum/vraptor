---
title: Tratamento de Exceções
layout: page
section: 19
category: [pt, docs]
---

A partir da versão 3.2, o VRaptor possui um Exception Handler, que captura as exceções não tratadas em sua aplicação. O funcionamento do Exception Handler é muito semelhante ao funcionamento do VRaptor Validator.

No exemplo abaixo, se o método _addCustomer(Customer)_ lançar uma _CustomerAlreadyExistsException_ ou qualquer exceção filha, o usuário será redirecionado para o método _addCustomer()_.


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
