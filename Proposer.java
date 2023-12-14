import java.io.PrintWriter;
import java.net.Socket;


public class Proposer implements Role
{
    private int count;
    private int consensusCount;
    private String Value;
    private int ID_p;

    public Proposer() 
    {
        this.count = 0;
    }

    public void Connect(int ID_p, Socket socket, String name)
    {
        String Proposal;
        while(true)
        {
            try
            {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                Proposal = name + " " + Config.Connect + " " + ID_p; 
                //System.out.println(name + ": " + Proposal); 
                writer.println(Proposal);
                writer.flush();
                break;
            }
            catch (Exception e)
            {
                //retry
            }
        }
    }

    public void Propose(String value, int proposal_no, Socket socket, String name)
    {
        this.ID_p = proposal_no;
        this.Value = value;
        this.count = 0;
        this.consensusCount = 0;
        try
        {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            String Proposal = name + " " + Config.Prepare + " " + proposal_no;
            //System.out.println(name + ": " + Proposal);
            writer.println(Proposal);
            writer.flush(); 
        }
        catch (Exception e)
        {
            System.out.println("An error occurred: " + e.getMessage());
            System.out.println("Error: Could not connect to the server during Propose. Please confirm the address and port are correct.");
        }
    }

    public int Promise(int ID_p, int acceptedProposalNumber, String Value, Socket socket, String name) // curently the same as acceptors
    {
        int biggerID = acceptedProposalNumber;
        if (acceptedProposalNumber < ID_p)
        {
            biggerID = ID_p;
            String Proposal;
            try
            {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                if (acceptedProposalNumber != 0)
                {
                    Proposal = name + " " + Config.Promise + " " + biggerID + " " + Config.Accepted + " " + acceptedProposalNumber + " " + Value;  
                }
                else
                {
                    Proposal = name + " " + Config.Promise + " " + biggerID;  
                }
                System.out.println(name + ": " + Proposal);
                writer.println(Proposal);
                writer.flush(); 
            }
            catch (Exception e)
            {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Error: Could not connect to the server during promise. Please confirm the address and port are correct.");
            }
        }
        return biggerID;
    }

    public void Accept_Request(int proposal_no, Socket socket, String name)
    {
        //System.out.println(proposal_no + " != " + this.ID_p);
        if (proposal_no == this.ID_p)
        {   
            this.count++;
            //System.out.println("Added to count: " + count);
        }

        if (this.count > (Config.Acceptors/2))
        {
            try
            {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                String Proposal = name + " " + Config.Accept_Request + " " + proposal_no + " " + name;
                System.out.println(name + ": " + Proposal);
                writer.println(Proposal);
                writer.flush(); 
            }
            catch (Exception e)
            {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Error: Could not connect to the server. Please confirm the address and port are correct.");
            }
        }
    }

    public String Accept(int acceptedProposalNumber, int ID_p, String acceptedProposalValue, String value, Socket socket, String name)
    {
        if (acceptedProposalNumber <= ID_p)
        {
            //System.out.println(acceptedProposalValue + " < " + value);
            acceptedProposalValue = value;
            String Proposal;
            try
            {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                Proposal = name + " " + Config.Accept + " " + ID_p + " " + acceptedProposalValue;
                System.out.println(name + ": " + Proposal);
                writer.println(Proposal);
                writer.flush(); 
            }
            catch (Exception e)
            {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Error: Could not connect to the server. Please confirm the address and port are correct.");
            }
        }
        return acceptedProposalValue;
    }

    public boolean CountAccepts(int acceptedProposalNumber, String acceptedProposalValue, Socket socket, String name)
    {
        //System.out.println(acceptedProposalNumber + " == " + this.ID_p + " and " + acceptedProposalValue + " == " + this.Value );
        if (acceptedProposalNumber == this.ID_p && acceptedProposalValue.equals(this.Value))
        {
            this.consensusCount++;
            //System.out.println("Added to count: " + consensusCount);
        }

        if (this.consensusCount > (Config.Acceptors/2))
        {
            String END;
            try
            {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                END = name + " " + Config.Finished + " " + ID_p + " " + acceptedProposalValue;
                System.out.println(name + ": " + END);
                writer.println(END);
                writer.flush(); 
            }
            catch (Exception e)
            {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Error: Could not connect to the server. Please confirm the address and port are correct.");
            }
            return true;
        }
        return false;
    }
}
