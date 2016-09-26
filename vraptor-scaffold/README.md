VRaptor Scaffod (JRuby Embedded)
================================

USE ONLY IF YOU CAN'T (or won't) INSTALL RUBY
---------------------------------------------

This is a JRuby complete JAR file with the **vraptor-scaffold** gem properly embedded.


The *vraptor.sh way*
--------------------

### View usage and options:

    $> ./vraptor.sh

### Create a new project:

    $> ./vraptor.sh new yourProjectName
    $> cd yourProjectName

### VRaptor Scaffold:

    $> ./vraptor.sh scaffold model attribute:type [attribute:type]

The *Windows way*
-----------------

### View usage and options:

    $> java -jar vraptor-scaffold.jar -S vraptor

### Create a new project:

    $> java -jar vraptor-scaffold.jar -S vraptor new yourProjectName
    $> move vraptor-scaffold.jar yourProjectName
    $> cd yourProjectName

### VRaptor Scaffold:

    $> java -jar vraptor-scaffold.jar -S vraptor scaffold model attribute:type [attribute:type]

Need help?
----------

Visit the [VRaptor Scaffold project](https://github.com/caelum/vraptor-scaffold).
