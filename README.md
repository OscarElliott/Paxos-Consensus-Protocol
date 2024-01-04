# Paxos Consensus Mechanism
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white) ![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)

This project implements the Paxos consensus mechanism in Java, providing a distributed algorithm for achieving consensus among a network of CouncilMembers.

## Overview

The implementation follows the Paxos algorithm's key phases, including the connect phase, prepare phase, accept phase, and consensus phase. The system utilizes a central server to facilitate communication among CouncilMembers, which can act as both Proposers and Acceptors.

## Features

- **Connect Phase:** CouncilMembers establish connections to the central server.
- **Prepare Phase:** Proposers send prepare messages, and Acceptors respond with promises.
- **Accept Phase:** Proposers send Accept_Request messages, and Acceptors accept based on previous promises.
- **Consensus Phase:** Once a proposer receives a majority of Accept messages, consensus is reached.

## Design Choices

- The system employs a central server for communication (Server.java).
- CouncilMembers include both Proposers (Proposer.java) and Acceptors (Acceptor.java).
- Back-off mechanisms and message delay handling contribute to the system's robustness.

## Getting Started

### Prerequisites

- Java Development Kit (JDK)

### Building & Running

The code can be built with: make run_Main

Further Builds and tests are covered in [Testing](./Testing.md)

## Contributors

- [Oscar Elliott](https://github.com/OscarElliott)

## Liscence 
This project is MIT licensed. View full [License](https://en.wikipedia.org/wiki/MIT_License)
