#!/bin/sh

mvn install:install-file -DgroupId=org.hibernate -DartifactId=hibernate-validator -Dversion=4.0.2.GA -Dclassifier=new -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hibernate/hibernate-validator-4.0.2.GA.jar

mvn install:install-file -DgroupId=org.hamcrest -DartifactId=hamcrest-all -Dversion=1.2RC3 -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hamcrest-all-1.2RC3.jar

mvn install:install-file -DgroupId=br.com.caelum -DartifactId=iogi -Dversion=0.8.1 -Dpackaging=jar -Dfile=vraptor-core/lib/optional/iogi/iogi-0.8.1.jar


