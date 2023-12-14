# Paxos Consensus Mechanism

## Paxos Implementation
The implementation of Paxos in this program is as follows
1. Connect phase, all CouncilMembers connect to the server
2. Prepare phase, Prepare messages are sent by proposers, all CouncilMembers may respond with a promise to any given Prepare (promising to ignore any Accept_Requests with ID_p lower then the Preparer's ID_p)
3. Accept Phase, Proposers send Accept_Request if they received majority Promises, if a given CouncilMember has not promised to ignore Accept_Requests of this ID_p they will Accept.
4. Consensus phase, if a given proposer receives majority Accept messages they will reach consensus on their proposed value and hence inform all other council members that consensus has been reached. If Consensus is not reached during this phase a new Prepare message will be sent by a Proposer and the process will start again from step 2 (Prepare phase).
Note: majority in this context refers to n/2+1 where n is the number of CouncilMembers. 


## Design Choices:
Listeners are not implemented and neither is peer to peer communication as apose to the central node / server node implemented.

The system makes use a central node / server (Server.java) which handles the communication between CouncilMembers (CouncilMember.java). The system goes through several phases before reaching consensus, first all CouncilMembers establish a connection to the Server which records their Name and Socket Number for later use. Then the proposers all send the Propose requests to the Server which forwards the Proposal to all connected CouncilMembers, the proposers also schedule their next proposals. The CouncilMembers then get to promise to the Request with the highest ID_p they may promise to more then one Prepare request but once they accept a ID_p they will ignore any Accept_Request with an ID_p lower then the one they accepted. The Server will forward the Promises to the respective CouncilMember which the Promise is to. Then that CouncilMember once it has received n/2+1 Promises (where n = number of CouncilMembers) it will send an Accept_Request. Which will once again be forwarded to all CouncilMembers by the Server, as long as the ID_p of the Accept_Request is greater or equal to the current promised ID_p of a given CouncilMember they will accept the request and respond with an Accept message. Once again the server will forward the Accept message to the sender of the Accept_Request, who once receiving n/2+1 Accepts with have reached Consensus and will inform the Server and hence all other CouncilMembers that voting has concluded and consensus has been reached. Notably the process does not always run exactly as described above as not all proposals will be accepted, but Consensus will eventually be reached by a given proposal.



### Main.java
This is the test / application harness for the program, it takes three arguments in the form: 
Usage: java main <int(no. of proposers)> <int(time delay)> <int(no. of offline proposers)>
It then proceeds to launch the Server (Server.java), it waits for the Server to launch then proceeds to launch 6 Acceptors and the specified number of proposers. 
The delay argument can be used in two ways, first any value greater then 0 can be used to specify the time (milliseconds) the each CouncilMember should take to respond to a received message i.e 1000 would mean 1 second delay for each message. Any value less then 0 can be used to specify the number of Acceptors which are unresponsive i.e -2 would mean that M4 and M5 would be unable to respond to messages after they connect.
The no. of offline proposers is used to specify how many proposers connect, propose then go offline, if this value is set to 1, just M3 will go offline and if set to 2 both M3 and M2 will connect then go offline. In this scenario going offline is functionally very similar to being unresponsive. Values greater then 2 will be set to 2 as one proposers is needed in order to be able to reach consensus.

### Config.java
This file is used by Main.java to configure the test being run, it also holds String and arrays of string used by the system for naming messages and CouncilMembers.

### Server.java
The server functions to store hashmaps pairing the Names of the CouncilMembers to their Sockets as well as forwarding these messages to the clients. The Server makes use of a queue to decide which message to handle next, the queue is added to by the RequestEnqueuer (RequestEnqueuer.java) for which they both make use of a Lock to ensure that only one of them is accessing (Adding / removing) from the queue at a time to ensure robust concurrency.

### RequestEnqueuer
The request Enqueuer loops accepting new sockets, when it recives a new socket is creates a new handler thread which is able to read out the message from the socket and create a Tuple (Tuple.java) which is added to the shared queue (after the Lock is acquired, once the Tuple containing the message and Socket has been added the Lock is released).

### Tuple.java
This is a simple implementation of how tuples work in other programming languages, it is used by the RequestEnqueuer to store Messages and Sockets but is written so that it could be reused for other purposes.

### Role.java
This file provides an interference for the CouncilMember to make use of (CouncilMember.java) to simplify the process of handling incoming messages regardless of if it is a Proposer or not.

### CouncilMember.java
This file runs a given council member in the context of the system, they are created in the Main.java with a Role, number (1-9), timedelay and offline status. They send a Connect message to the Server first, then if they are a Proposer they will also send a Propose message and schedule a future propose message. They enter a loop of checking if there is a message to read from the socket, when there is they parse the message extracting out the information from it before handling it appropriately. Once they have finished the Paxos implementation and reached consensus they exit the loop. If the scheduled propose occurs before consensus a Propose can propose again, incrementing its original ID_p by 3 hence increasing its priority. The scheduled Proposes allow the system to reach consensus in more complicated circumstances as seen in the testing section. A back off system was implemented as well so that the system can reach consensus in more complicated situations, the back off system means that the larger a Proposers previous ID_p the longer they have to wait for their next proposal.

### Acceptor.java
The acceptor overrides Role, and implements its methods for Accept, Connect, Promise and Accept. Connect sends a Connect request to the server in the form "<name> Connect <proposal_no>" (Example: M1 Connect 1). Promise sends a Promise request to the server in the form "<name> Promise <proposal_no>" or optionally "<name> Promise <proposal_no> Accepted <previously_accepted_ID_p>". Accept sends a Accept request to the server in the form "<name> Accept <proposal_no> <value>"

### Proposer.java
The proposer overrides Role, it also implements its methods the same as Acceptor it also implements its own methods Prepare, Accept_Request and Count Accepts. Propose sends a propose request, Accept_Request sends a Accept_Request once enough promises have been received (count > n/2+1) and CountAccepts returns consensus reached = true when (consensusCount > n/2+1). Propose has the form: "<name> Prepare <proposal_no>". Accept_Request has the form: "<name> Accept_Request <proposal_no> <value>". CountAccepts will send Finished which has the form: "<name> Finished <proposal_no> <value>"


## Testing

Below are the proposed tests for the system. Breaking points include the situations where:
	1. No proposers are online to respond to acceptors (run_Test1_offline)
	2. Not enough Acceptors are responsive for the system to be able to come to a consensus (run_Test1_noResponse4)

These will be tested with tests: run_Test1_offline1 and run_Test1_noResponse4

Situations where the system will be able to handle but are more complicated include:
	1. Proposers with highest initial ID_p proposes then goes offline (run_Test2_offline1)
	2. The delay between messages is so large that a poorly thought out system might never reach consensus as new proposals will happen before consensus is reached causing the process to restart (run_largedelay)
	3. A significant number of acceptors are offline but not more then half, a poorly thought out system might never reach consensus if messages get lost. (run_Test1_noResponse3)

These will be tested with tests: run_Test2_offline1, run_largedelay, run_Test1_noResponse3


### run_Main
This test runs the system with 3 proposers and no delay or offline proposers, as ID_p is initially correlated to by name (M1 = 1, M2 = 2, M3 = 3) and there is no delay or offline proposers, the system should reach consensus with M3 being elected.
to run test: make run_Main
### run_Test1
This test runs the system with 1 proposers and no delay or offline proposers, the system should reach consensus with M1 being elected, as it is the only proposer.
to run test: make run_Test1
### run_Test1_smalldelay
This test runs the system with 1 proposers, a small delay and no offline proposers, the system should reach consensus with M1 being elected, as it is the only proposer.
to run test: make run_Test1_smalldelay
### run_Test1_largedelay
This test runs the system with 1 proposers, a large delay and no offline proposers, the system should reach consensus with M1 being elected, as it is the only proposer.
to run test: make run_Test1_largedelay
### run_Test1_noResponse1
This test runs the system with 1 proposers, no response from 1 acceptor and no offline proposers, the system should reach consensus with M1 being elected, as it is the only proposer and 1 unresponsive acceptor will not prevent M1 from receiving n/2+1 accepts.
to run test: make run_Test1_noResponse1
### run_Test1_noResponse3
This test runs the system with 1 proposers, no response from 3 acceptors and no offline proposers, the system should reach consensus with M1 being elected, as it is the only proposer and 3 unresponsive acceptors will not prevent M1 from receiving n/2+1 accepts (7 total, therefore needs 4);
to run test: make run_Test1_noResponse3
### run_Test1_noResponse4
Notable: This test is not expected to reach consensus.
This test runs the system with 1 proposers, no response from 4 acceptors and no offline proposers, the system should not reach consensus, as 4 unresponsive acceptors will prevent M1 from receiving n/2+1 accepts (7 total, therefore needs 4 only gets 3). Hence it will run forever as M1 continues to make new proposals but does not receive a majority consensus.
to run test: make run_Test1_noResponse4
### run_Test1_offline1
Notable: This test is not expected to reach consensus.
This test runs the system with 1 proposers, 6 acceptors and 1 offline proposers, the system should not reach consensus, as M1 will go offline after sending its first Prepare and hence will never respond to any promises. This test merely checks that offline works as intended within the system.
to run test: make run_Test1_offline1
### run_Test2_offline1
This test runs the system with 2 proposers, 6 acceptors and 1 offline proposers, the system should reach consensus, as although M2 will go offline after sending its first Prepare M1 will send its prepare which will be of a lower ID_p then M2's so it will not initially receive many promises, when M1 sends its second prepare with a larger ID_p it will receive its promises Hence the system will reach consensus M1.
to run test: make run_Test2_offline1
### run_smalldelay
This runs the system with 3 proposers, a small delay and no offline proposers, the system should reach consensus with M3 being elected, as it has the largest proposal number.
to run test: make run_smalldelay
### run_largedelay
This runs the system with 3 proposers, a large delay and no offline proposers, the system should reach consensus with M3 being elected, as it has the largest proposal number.
to run test: make run_largedelay
### run_offline1
This test runs the system with 3 proposers, 6 acceptors and 1 offline proposers, the system should reach consensus, as although M3 will go offline after sending their first Prepare M1 and M2 will send their Prepare which will be of a lower ID_p then M3's so they will not initially receive many promises, when M2 sends its second prepare with a larger ID_p it will receive its promises Hence the system will reach consensus M2.
to run test: make run_Test2_offline1
### run_offline2
This test runs the system with 3 proposers, 6 acceptors and 2 offline proposers, the system should reach consensus, as although M3 and M2 will go offline after sending their first Prepare M1 will send their Prepare which will be of a lower ID_p then M3's or M2's so they will not initially receive many promises, when M1 sends its second prepare with a larger ID_p it will receive its promises Hence the system will reach consensus M1.
to run test: make run_Test2_offline1