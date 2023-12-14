import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.net.SocketException;

public class RequestEnqueuer implements Runnable 
{
    private ServerSocket serverSocket;
    private Queue<Tuple<String, Socket>> requestQueue;
    private Lock queueLock;

    public RequestEnqueuer(Queue<Tuple<String, Socket>> requestQueue2, ServerSocket serverSocket, Lock queueLock) 
    {
        this.requestQueue = requestQueue2;
        this.serverSocket = serverSocket;
        this.queueLock = queueLock;
    }

    @Override
    public void run() 
    {
        try
        {
            while(true)
            {
                System.out.println("Waiting for request...");
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClientRequest(clientSocket)).start();
            }
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        } 
    }

    private boolean isBlankString(String str) 
    {
        return str == null || str.trim().isEmpty();
    }
    

    private void handleClientRequest(Socket clientSocket) 
    {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while(true)
            {
                String requestLine = "";
                String line;
                line = reader.readLine();
                if (!(isBlankString(line)))
                {
        
                    requestLine += line + "\n";
                    System.out.print("Server Executes: " + requestLine);
                    Tuple<String, Socket> request = new Tuple<>(requestLine, clientSocket);
                    if (request != null) 
                    {
                        queueLock.lock();
                        requestQueue.add(request);
                        queueLock.unlock();
                    }
                }
            }
        }
        catch (NoSuchElementException e) 
        {
            System.err.println("Client disconnected while reading request.");
       
        }
        catch (SocketException e) 
        {
            // gracefully do nothing as client has closed
        }
        catch (Exception e) 
        {
            System.out.println("Request Queuer Crashed");
            e.printStackTrace();
        }
    }
}