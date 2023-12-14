/*
 * main classs to run Paxos protocol it takes three arguments:
 * java main <no. of proposers> <time delay> <no. of offline proposers>
 * 
 * <no. of proposers> - control number of proposers on the council
 * 
 * <time delay> - control maximum time delay for councilor response
 * 
 * <no. of offline proposers> - number of proposers who go offline 
 * at some point during the vote
 */
public class Main
{
    private static void run_paxos(int proposers, int timeDelay, int offlineCouncilors)
    {
        Server server = new Server();
        Thread sthread = new Thread(server);
        sthread.start();
        try 
        {
            Thread.sleep(2000); //give server time to start up 
        } catch (InterruptedException e) 
        {
            System.out.println("Error: Server Launch was interrupted");
        }

        for (int i = proposers; i < Config.Acceptors; i++)
        {
            CouncilMember member = new CouncilMember(Config.Acceptor, i, timeDelay, 0);
            timeDelay++;
            Thread mthread = new Thread(member);
            mthread.start();
        }
        for (int i = Config.Proposers-1; i >= 0 ; i--) // counts down so that M2 and M3 get marked as offline not M1
        {
            CouncilMember proposer = new CouncilMember(Config.Proposer, i, timeDelay, offlineCouncilors);
            offlineCouncilors--;
            Thread pthread = new Thread(proposer);
            pthread.start();
        }
    }

    public static void main(String args[])
    {
        if (args.length != 3)
        {
            System.out.println("Usage: java Main <int(no. of proposers)> <int(time delay)> <int(no. of offline proposers)>");
            System.out.println("Program Terminating...");
            return;
        }
        int proposers = Integer.parseInt(args[0]);
        int timeDelay = Integer.parseInt(args[1]);
        int offlineCouncilors = Integer.parseInt(args[2]);
        if (offlineCouncilors > 2)
        {
            offlineCouncilors = 2;
        }
        Config.Acceptors += proposers;
        Config.Proposers += proposers;
        run_paxos(proposers, timeDelay, offlineCouncilors);
    }
}

