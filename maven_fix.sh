#!/bin/sh

mvn install:install-file -DgroupId=org.hibernate -DartifactId=hibernate-validator -Dversion=4.2.0.Final -Dclassifier=new -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hibernate/hibernate-validator-4.2.0.Final.jar

