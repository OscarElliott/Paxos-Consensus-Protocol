import java.io.PrintWriter;
import java.net.Socket;

public class Acceptor implements Role
{
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
    // Assuming Prepare has been recived
    public int Promise(int ID_p, int acceptedProposalNumber, String Value, Socket socket, String name)
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
                System.out.println("Error: Could not connect to the server. Please confirm the address and port are correct.");
            }
        }
        return biggerID;
    }

    public String Accept(int acceptedProposalNumber, int ID_p, String acceptedProposalValue, String value, Socket socket, String name)
    {
        if (acceptedProposalNumber <= ID_p)
        {
            System.out.println(acceptedProposalValue + " < " + value);
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
}