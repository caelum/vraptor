#! /bin/bash 

echo "Running tests";
ant test;

echo "Removing temp files."
for X in `ls | grep raptor`; do rm -f $X; done;
