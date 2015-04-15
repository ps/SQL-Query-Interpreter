# simple makefile for compiling three java classes
#
## define a makefile variable for the java compiler
#
JCC = javac

# define a makefile variable for compilation flags
# the -g flag compiles with debugging information
 
JFLAGS = -g

# typing 'make' will invoke the first target entry in the makefile 
# (the default one in this case)
# 
#default: SampleQueryUsage.class QueryTest.class
default: QueryTest.class


#SampleQueryUsage.class: SampleQueryUsage.java
#	$(JCC) $(JFLAGS) SampleQueryUsage.java

QueryTest.class: QueryTest.java
	$(JCC) $(JFLAGS) QueryTest.java


clean:
	$(RM) *.class

