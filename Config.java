/*
 * This class functions as a utility class providing 
 * configuration settings for running/testing diffrenet 
 * scenarios.
 */
public class Config
{
    /*
     * Server infomation accessible across all files
     */
    public static final int Server_Port = 5001;                   // This may need to be changed depending on which ports are currently used on your PC
    public static final String serverAddress = "127.0.0.1";
    

    /* 
     * Names representing each council mebmber
     */
    
    public static final String[] Names = { "M1", "M2", "M3",
        "M4", "M5", "M6", "M7", "M8", "M9"};


    /* Test Related Settings */
    public static int Acceptors = 6;
    public static int Proposers = 0;


    /*
     * String Messages for communication
     */
    public static final String Connect = "Connect";
    public static final String Proposer = "Proposer";
    public static final String Acceptor = "Acceptor";
    public static final String Prepare = "Prepare";
    public static final String Promise = "Promise";
    public static final String Accept_Request = "Accept_Request";
    public static final String Accept = "Accept";
    public static final String Accepted = "Accepted";
    public static final String Finished = "Finished";
}
