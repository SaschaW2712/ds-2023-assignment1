package assignment1;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Stack;

public interface Calculator extends Remote {

    //Initialises the client stacks
    void init() throws RemoteException;

    //Initialises a client and returns its client ID
    int initClient() throws RemoteException;

    //Pushes an integer value onto the client's stack
    void pushValue(int clientId, int val) throws RemoteException;

    //Executes an operation on client's stack
    void pushOperation(int clientId, String operator) throws RemoteException;

    //Returns the top value of the client's stack
    int pop(int clientId) throws RemoteException;

    //Checks if the client's stack has any values in it
    boolean isEmpty(int clientId) throws RemoteException;

    //Waits 'millis' time in milliseconds, then returns the top value of the client's stack
    int delayPop(int clientId, int millis) throws RemoteException, InterruptedException;

    //Clears all values from the client's stack without returning them
    void clearStack(int clientId) throws RemoteException;

    //Clears all values from all client stacks without returning them
    //(Just used for clearing up stacks between automated tests)
    void clearAllStacks() throws RemoteException;
}