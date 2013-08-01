VRaptor MyDVDs
==============

Importing this project on Eclipse
=================================

In order to import this project on Eclipse, you'll need:
  * Run <code>mvn eclipse:clean eclipse:eclipse -Declipse.useProjectReferences=false -Dwtpversion=1.5</code>.
  * Choose menu file/import existent projects.
  * Choose the directory that contains <code>vraptor-mydvds</code>.
  * After importing, if there is any problems prohibiting the project to be built, right click in the project, choose validate if.
  * Just right click on the project, run as, run on server. Choose a servlet container or configure a new one.
  * Done! Check the log files. Access http://localhost:8080/vraptor-mydvds/ (or change 8080 to the appropriate port).
