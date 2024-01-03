# Java tools
JAVAC = javac
JAVA = java
JFLAGS = -cp .:./lib/*
JF = -cp
PY = python3

# Directory paths
LIB_DIR = lib

# Path to support tools - you may need to change this to build
JAVA_HOME   = C:\Program Files\Java\jdk-20

# all - Perform all tasks for a complete build
all: Tuple Config Role Acceptor CouncilMember Proposer RequestEnqueuer Server Main

Tuple: Tuple.java
	@$(JAVAC) Tuple.java 

Acceptor: Acceptor.java
	@$(JAVAC) Acceptor.java

Config: Config.java
	@$(JAVAC) Config.java

CouncilMember: CouncilMember.java
	@$(JAVAC) CouncilMember.java	

Main: Main.java
	@$(JAVAC) Main.java

Proposer: Proposer.java
	@$(JAVAC) Proposer.java

RequestEnqueuer: RequestEnqueuer.java
	@$(JAVAC) RequestEnqueuer.java

Role: Role.java
	@$(JAVAC) Role.java

Server: Server.java
	@$(JAVAC) Server.java

# clean - Remove all compiled class files
clean:
	$(RM) *.class

# Run the program
run_Main: all
	$(JAVA) $(JFLAGS) Main 3 0 0
	
run_Test1: all
	$(JAVA) $(JFLAGS) Main 1 0 0

run_Test1_smalldelay: all
	$(JAVA) $(JFLAGS) Main 1 1000 0

run_Test1_largedelay: all
	$(JAVA) $(JFLAGS) Main 1 10000 0

run_Test1_noResponse1: all
	$(JAVA) $(JFLAGS) Main 1 -1 0

run_Test1_noResponse3: all
	$(JAVA) $(JFLAGS) Main 1 -3 0

run_Test1_noResponse4: all
	$(JAVA) $(JFLAGS) Main 1 -4 0

run_Test1_offline1: all
	$(JAVA) $(JFLAGS) Main 1 0 1

run_Test2_offline1: all
	$(JAVA) $(JFLAGS) Main 2 0 1

run_smalldelay: all
	$(JAVA) $(JFLAGS) Main 3 1000 0

run_largedelay: all
	$(JAVA) $(JFLAGS) Main 3 10000 0

run_noResponse1: all
	$(JAVA) $(JFLAGS) Main 3 -1 0

run_noResponse4: all
	$(JAVA) $(JFLAGS) Main 3 -4 0

run_offline1: all
	$(JAVA) $(JFLAGS) Main 3 0 1

run_offline2: all
	$(JAVA) $(JFLAGS) Main 3 0 2



