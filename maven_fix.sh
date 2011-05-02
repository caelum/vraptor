#!/bin/sh

mvn install:install-file -DgroupId=org.hibernate -DartifactId=hibernate-validator -Dversion=4.0.2.GA -Dclassifier=new -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hibernate/hibernate-validator-4.0.2.GA.jar

mvn install:install-file -DgroupId=org.hamcrest -DartifactId=hamcrest-all -Dversion=1.2RC3 -Dpackaging=jar -Dfile=vraptor-core/lib/optional/hamcrest-all-1.2RC3.jar

mvn install:install-file -DgroupId=br.com.caelum -DartifactId=iogi -Dversion=0.8.2 -Dpackaging=jar -Dfile=vraptor-core/lib/optional/iogi/iogi-0.8.2.jar

mvn install:install-file -DgroupId=jersey -DartifactId=jersey-core -Dversion=1.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dfile=vraptor-core/lib/jersey/jersey-core-1.5-SNAPSHOT.jar

mvn install:install-file -DgroupId=jersey -DartifactId=jersey-server -Dversion=1.5-SNAPSHOT -Dpackaging=jar -DgeneratePom=true -Dfile=vraptor-core/lib/jersey/jersey-server-1.5-SNAPSHOT.jar




