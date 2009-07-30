#! /bin/bash 

echo "Running tests";
ant test;
ANT_STATUS=$?;

echo "Removing temp files."
for X in `ls /tmp | grep raptor`; do rm -frv /tmp/$X; done;

exit $ANT_STATUS;
