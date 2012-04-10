#!/bin/sh

mvn install:install-file -DgroupId=org.hibernate -DartifactId=hibernate-validator -Dversion=4.0.2.GA -Dclassifier=new -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hibernate/hibernate-validator-4.0.2.GA.jar


