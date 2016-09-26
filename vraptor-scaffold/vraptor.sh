#!/bin/bash

if [ $# = 0 ]; then
  # Calling without parameters shows "Usage"
  java -jar vraptor-scaffold.jar -S vraptor
elif [ $# = 2 ] && [ $1 = "new" ]; then
  # Generate VRaptor project
  java -jar vraptor-scaffold.jar -S vraptor $@

  # Copy the vraptor-scaffold.jar and bash script to the project root
  cp vraptor-scaffold.jar $2
  cp vraptor.sh $2
elif [ $1 = "scaffold" ]; then
  # Generate VRaptor scaffold files
  java -jar vraptor-scaffold.jar -S vraptor $@
fi
