---
title: Spring, Joda Time e Hibernate
layout: page
language: pt
section: 13
category: docs
---

<h3>Integração com Hibernate ou JPA</h3>

Existem ComponentFactories implementadas para Session, SessionFactory, EntityManager e EntityManagerFactory. Você pode usá-las ou se basear nelas para criar sua própria ComponentFactory para essas classes.
Além disso existem interceptors implementados que controlam as transações tanto na JPA quanto com o Hibernate.
Para saber como fazer usar esses componentes veja o capítulo de utils.

<h3>Integração com Spring</h3>

O VRaptor roda dentro do Spring, e usa o ApplicationContext da sua aplicação se disponível. Logo todas as funcionalidades e módulos do Spring funcionam com o VRaptor sem nenhuma configuração da parte do VRaptor!

<h3>Conversores para Joda Time</h3>

A api de datas do Java é bem ruim, e por esse motivo existe o projeto <a href="http://joda-time.sourceforge.net/">Joda Time</a> que tem uma API bem mais agradável para trabalhar com datas. Se o jar do joda-time estiver no classpath, o VRaptor registra automaticamente os conversores para os tipos LocalDate, LocalTime e LocalDateTime, logo você pode recebê-los como parâmetro sem problemas.
