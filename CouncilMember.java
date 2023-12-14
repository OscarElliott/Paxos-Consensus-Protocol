import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class CouncilMember implements Runnable
{
    private String Role;
    private Socket socket;
    private Role role;
    private String name;
    private int proposal_no;
    private int Port;
    private int acceptedProposalNumber;
    private String acceptedProposalValue;
    private boolean consensusReached;
    private Timer proposalTimer;
    private int delay;
    private int offline;

    /* for messages from server */
    private String response;
    private int response_IDp;
    private String response_value;
    
    public CouncilMember(String Role, int i, int timeDelay, int offline)
    {
        this.Role = Role;
        this.offline = offline; //if offline > 0 the council member will go offline after proposing
        this.delay = timeDelay; // this value is either a delay in milliseconds or if delay < 0 it specifies unresponsive
        this.name = Config.Names[i];
        this.Port = Config.Server_Port;
        this.acceptedProposalNumber = 0;
        this.proposal_no = 0;
        this.acceptedProposalValue = "";
        this.proposalTimer = new Timer();
        if (this.Role == Config.Proposer) 
        {
            this.role = new Proposer();
            this.acceptedProposalValue = name;
            this.proposal_no = i+1;
        } 
        else 
        {
            this.role = new Acceptor();
        }
    }

    private boolean isBlankString(String str) 
    {
        return str == null || str.trim().isEmpty();
    }

    private void sendNewProposal() 
    {
        if (!this.consensusReached)
        {
            try 
            {
                this.proposal_no += 3;
                ((Proposer) role).Propose(this.acceptedProposalValue, this.proposal_no, this.socket, this.name);
                scheduleNewProposal(); // schedule new proposal
            } catch (Exception e) 
            {
                System.out.println("An error occurred (sendNewProposal): " + this.name + ": " + e.getMessage());
            }
        }
    }

    private void scheduleNewProposal() 
    {
        if (this.offline <= 0) 
        {
            this.proposalTimer.schedule(new TimerTask() 
            {
                @Override
                public void run() 
                {
                    // Send a new proposal
                    sendNewProposal();
                }
            }, (12000+(this.delay*22)+ (this.proposal_no*1000))); // 15 seconds + delay*(steps*2) + proposal_no
        }
    }

    public void parseRequest(String Line)
    {
        Scanner sc = new Scanner(Line);
        sc.next().trim(); // discard unused value
        this.response = sc.next().trim();
        this.response_IDp = Integer.parseInt(sc.next().trim());
        if (!this.response.equals(Config.Promise) && !this.response.equals(Config.Prepare))
        {
            this.response_value = sc.next().trim();
        }
        else if (this.response.equals(Config.Promise) && sc.hasNext())
        {
            //do nothing as not used
        }
        sc.close();
    }

    public void run()
    {
        try
        {
            Socket socket = new Socket(Config.serverAddress, this.Port);
            this.socket = socket;
            String nextLine;
            role.Connect(this.proposal_no, this.socket, this.name);
            Thread.sleep(3000); // sleeps for 3 seconds so everyone can connect before begining PAXOS
            if (this.Role == Config.Proposer)
            {
                ((Proposer) role).Propose(this.acceptedProposalValue, this.proposal_no, this.socket, this.name);
                scheduleNewProposal();
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            while(true)
            {
                nextLine = reader.readLine();
                if(!isBlankString(nextLine))
                {
                    if (this.delay < 0 || this.offline > 0)
                    {
                        break; // delay -1 symbolises no response, offline > 0 symbolises offline user
                    }
                    Thread.sleep(this.delay);
                    //System.out.println(name + " recived: " + nextLine);
                    parseRequest(nextLine);
                    //System.out.println(name + " parsed line: " + nextLine);
                    
                    switch (this.response)
                    {
                        case (Config.Prepare):
                            int accepted = role.Promise(this.response_IDp, this.acceptedProposalNumber, this.acceptedProposalValue, this.socket, this.name);
                            this.acceptedProposalNumber = accepted;
                            break;
                        case (Config.Promise):
                            ((Proposer) role).Accept_Request(this.response_IDp, this.socket, this.name);
                            break;
                        case (Config.Accept_Request):
                            this.acceptedProposalValue = role.Accept(this.acceptedProposalNumber, this.response_IDp, this.acceptedProposalValue, this.response_value, this.socket, this.name);
                            break;
                        case (Config.Accept):
                            this.consensusReached = ((Proposer) role).CountAccepts(this.acceptedProposalNumber, this.acceptedProposalValue, this.socket, this.name);
                            break;
                        case (Config.Finished):
                            this.acceptedProposalValue = this.response_value;
                            this.consensusReached = true;
                            break;
                        default:
                        System.out.println("Recived String: " + nextLine);
                        break;
                    }
                    if (this.consensusReached)
                    {
                        this.acceptedProposalValue = this.response_value;
                        System.out.println("Consensus reached: " + this.acceptedProposalValue);
                        this.socket.close();
                        break;
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("An error occurred (CouncilMember): " + this.name + ": " + e.getMessage());
        }
    }
}
