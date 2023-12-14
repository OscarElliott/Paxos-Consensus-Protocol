import java.net.Socket;

public interface Role 
{
    void Connect(int ID_p, Socket socket, String name);
    int Promise(int ID_p, int acceptedProposalNumber, String Value, Socket socket, String name);
    String Accept(int acceptedProposalNumber, int ID_p, String acceptedProposalValue, String value, Socket socket, String name);
}
