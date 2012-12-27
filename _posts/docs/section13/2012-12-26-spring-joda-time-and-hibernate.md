---
title: Spring, Joda Time and Hibernate
layout: page
section: 13
category: [en, docs]
---

<h3>Hibernate or JPA Integration</h3>

There are ComponentFactories written for Session, SessionFactory, EntityManager and EntityManagerFactory which may either be used or help as creation base of your own ComponentFactory classes. Furthermore, there are written interceptors which control the JPA transactions and Hibernate as well. More informations about how to use them please take a look at the Utils issue.

<h3>Spring Integration</h3>

VRaptor works inside Spring and uses ApplicationContext from your application once available. Then, all the Spring functionalities and modules work with VRaptor without any VRaptor configuration.

<h3>Joda Time Conversors</h3>

The Java date API is weak. That's why the Joda Time project exists and offers a better and more comfortable API to work with dates. If the Joda Time JAR is in classpath the VRaptor will register the LocalDate, LocalTime and LocalDateTime conversors automatically and you will be able to receive them as parameters perfectly.
