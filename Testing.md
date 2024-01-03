# Testing

Below are the proposed tests for the system. Breaking points include the situations where:
	1. No proposers are online to respond to acceptors (run_Test1_offline)
	2. Not enough Acceptors are responsive for the system to be able to come to a consensus (run_Test1_noResponse4)

These will be tested with tests: run_Test1_offline1 and run_Test1_noResponse4

Situations where the system will be able to handle but are more complicated include:
	1. Proposers with highest initial ID_p proposes then goes offline (run_Test2_offline1)
	2. The delay between messages is so large that a poorly thought out system might never reach consensus as new proposals will happen before consensus is reached causing the process to restart (run_largedelay)
	3. A significant number of acceptors are offline but not more then half, a poorly thought out system might never reach consensus if messages get lost. (run_Test1_noResponse3)

These will be tested with tests: run_Test2_offline1, run_largedelay, run_Test1_noResponse3

## Tests

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