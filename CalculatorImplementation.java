package assignment1;
	
import java.rmi.registry.Registry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class CalculatorImplementation implements Calculator {

    //The client stacks, and the ID to assign to the next new client
    public static ArrayList<Stack<Integer>> stacks;
    public int nextClientId = 0;
    
    //Initialises the client stacks
    public void init() throws RemoteException {
        System.out.println("Initialising Calculator");
        stacks = new ArrayList<Stack<Integer>>();
        return;
    }

    //Initialises a client and returns its client ID
    public int initClient() throws RemoteException {
        Stack<Integer> newStack = new Stack<Integer>();
        int clientId = nextClientId;
        nextClientId++;

        stacks.add(newStack);
        return clientId;
    }

    //Pushes an integer value onto the client's stack
    public void pushValue(int clientId, int val) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);
        System.out.println("Pushing value: " + val);
        stack.push(val);
    }

    //Executes an operation on client's stack
    public void pushOperation(int clientId, String operator) throws RemoteException {

        System.out.println("Pushing operation: " + operator);
        switch (operator) {
            case "min": {
                handleMinOp(clientId);
                break;
            }
            case "max": {
                handleMaxOp(clientId);
                break;
            }
            case "lcm": {
                handleLCMOp(clientId);
                break;
            }
            case "gcd": {
                handleGCDOp(clientId);
                break;
            }
            default: return;
        }
    }

    //Returns the top value of the client's stack
    public int pop(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);

        int poppedVal = stack.pop();
        System.out.println("Popping value for clientId " + clientId + ": " + poppedVal);
        return poppedVal;
    }

    //Checks if the client's stack has any values in it
    public boolean isEmpty(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);

        if (stack.size() <= 0) {
            return true;
        } else { 
            return false;
        }
    }
  
    //Waits 'millis' time in milliseconds, then returns the top value of the client's stack
    public int delayPop(int clientId, int millis) throws RemoteException, InterruptedException {
        System.out.println("Popping value for clientId " + clientId + " with delay " + millis);
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch(InterruptedException e) {
            System.out.println("delayPop error: Interrupted");
            System.out.println(e);
            throw(e);
        }

        return pop(clientId);
    }

    //Clears all values from the client's stack without returning them
    public void clearStack(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);
        int size = stack.size();
        for (int i = 0; i < size; i++) {
            pop(clientId);
        }
        return;
    }

    //Clears all values from all client stacks without returning them
    //(Just used for clearing up stacks between automated tests)
    public void clearAllStacks() throws RemoteException {
        int numStacks = stacks.size();
        for (int i = 0; i < numStacks; i++) {
            if (!stacks.get(i).isEmpty()) {
                clearStack(i);
            }
        }

        return;
    }

    //HELPER FUNCTIONS

    //Pops all values from the stack and pushes the smallest value
    private void handleMinOp(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);
        int size = stack.size();
        int min = pop(clientId);

        for (int i = 1; i < size; i++) {
            int num = pop(clientId);
            if (num < min) {
                min = num;
            }
        }

        pushValue(clientId, min);
    }

    //Pops all values from the stack and pushes the largest value
    private void handleMaxOp(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);

        int size = stack.size();
        int max = pop(clientId);
        for (int i = 1; i < size; i++) {
            int num = pop(clientId);
            if (num > max) max = num;
        }
        pushValue(clientId, max);
    }


    //Returns the least common multiple of a pair of integers
    private int lcm(int a, int b) {
        return (a * b) / gcd(a, b);
    }

    //Pops all values from the stack and pushes the least common multiple
    private void handleLCMOp(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);

        int size = stack.size();
        int lcm = pop(clientId);

        for(int i = 1; i < size; i++) {
            lcm = lcm(lcm, pop(clientId));
        }

        pushValue(clientId, lcm);
    }

    //Returns the lowest common multiple of a pair of integers
    private int gcd(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    //Pops all values from the stack and pushes the greatest common denominator
    private void handleGCDOp(int clientId) throws RemoteException {
        Stack<Integer> stack = stacks.get(clientId);
        int gcd = Integer.MIN_VALUE;
        int size = stack.size();

        for(int i = 0; i < size; i++) {
            gcd = gcd(gcd, pop(clientId));
        }

        pushValue(clientId, gcd);
    }
}