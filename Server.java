import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;



public class Server implements Runnable
{
    private static HashMap<String, Socket> PsocketMap = new HashMap<>();
    private static HashMap<String, Socket> AsocketMap = new HashMap<>();
    private Lock queueLock = new ReentrantLock();

    private synchronized static void handleClientRequest(Tuple<String, Socket> request) throws IOException
    {
        String requestline = request.getFirst();
        Socket clientSocket = request.getSecond();
        try
        {
            Scanner scanner = new Scanner(requestline);
            String name = scanner.next().trim();
            String type = scanner.next().trim();
            String value;
            switch (type)
            {
                case (Config.Connect):
                    //System.out.println("Added Socket " + name + " to Amap : " + type);
                    AsocketMap.put(name, clientSocket);
                    break;
                case (Config.Prepare):
                    value = scanner.next().trim();
                    //System.out.println("Added Socket " + value + " to Pmap : " + type);
                    PsocketMap.put(value, clientSocket);
                    for (int i = 0; i < Config.Acceptors; i++)
                    {
                        try
                        {
                            String Name = Config.Names[i];
                            if (AsocketMap.containsKey(Name))
                            {
                                //System.out.println("Name: " + Name + " is has socket");
                                Socket cSocket = AsocketMap.get(Name);
                                PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                                writer.println(requestline);
                                writer.flush();
                            }
                        }
                        catch (Exception e)
                        {
                            // try again for next i
                        }
                    }
                    break;
                case (Config.Promise):
                    try
                    {
                        value = scanner.next().trim();
                        if(PsocketMap.containsKey(value))
                        {
                            Socket cSocket = PsocketMap.get(value);
                            PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                            //System.out.println("Server sending to proposer: " + requestline);
                            writer.println(requestline);
                            writer.flush();
                        }
                        else
                        {
                            scanner.next().trim();
                            value = scanner.next().trim();
                            if (PsocketMap.containsKey(value))
                            {
                                Socket cSocket = AsocketMap.get(value);
                                PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                                writer.println(requestline);
                                writer.flush();
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        // try again for next i
                    }
                    break;
                case (Config.Accept_Request):
                    for (int i = 0; i < Config.Acceptors; i++)
                    {
                        try
                        {
                            String Name = Config.Names[i];
                            Socket cSocket = AsocketMap.get(Name);
                            PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                            writer.println(requestline);
                            writer.flush();
                        }
                        catch (Exception e)
                        {
                            // try again for next i
                        }
                    }
                    break;
                case (Config.Accept):
                    scanner.next().trim(); // remove IDp not used by server
                    value = scanner.next().trim();
                    try
                    {
                        Socket cSocket = AsocketMap.get(value);
                        PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                        writer.println(requestline);
                        writer.flush();
                    }
                    catch (Exception e)
                    {
                        // try again for next i
                    }
                    break;
                case (Config.Finished):
                    value = scanner.next().trim();
                    System.out.println("Consensus reached: " + name);
                    for (int i = 0; i < Config.Acceptors; i++)
                    {
                        try
                        {
                            String Name = Config.Names[i];
                            if (AsocketMap.containsKey(Name))
                            {
                                Socket cSocket = AsocketMap.get(Name);
                                PrintWriter writer = new PrintWriter(cSocket.getOutputStream(), true);
                                writer.println(requestline);
                                writer.flush();
                            }
                        }
                        catch (Exception e)
                        {
                            // try again for next i
                        }
                    }
                    break;
                default:
                    System.out.println("Error: recived an invalid type for a request: " + type);
                    System.out.println("For: " + requestline);
            }
            scanner.close();
        }
        catch (Exception e)
        {
            System.out.println("An error occurred on Server: " + e.getMessage());
        }
    }

    public void run()
    {
        int portNumber = Config.Server_Port;
        Queue<Tuple<String, Socket>> requestQueue = new LinkedList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(1);

        try 
        {
            ServerSocket serverSocket = new ServerSocket(portNumber);

            Runnable openServer = new RequestEnqueuer(requestQueue, serverSocket, queueLock);
            executorService.execute(openServer);

            while (true) 
            {
                queueLock.lock();
                if (!(requestQueue.isEmpty())) 
                {
                    
                    handleClientRequest(requestQueue.poll());
                    queueLock.unlock();
                } 
                else 
                {
                    queueLock.unlock();
                    try 
                    {
                        Thread.sleep(1000);
                    } 
                    catch (InterruptedException e) 
                    {
                        System.out.println("Interrupted!");
                    }
                }
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }

}
