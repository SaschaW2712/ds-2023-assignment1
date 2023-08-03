package assignment1;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;  // Import the Scanner class
import java.util.Stack;
import java.util.Arrays;


public class CalculatorClient {

    private CalculatorClient() {}

    public static void main(String[] args) {

	    if (args.length < 2) {
            System.out.println("Usage: enter space-separated arguments. The first must be the output file name, or 'sysout' for standard System output. Valid args are:");
            System.out.println(" • An integer value (will be pushed onto stack)");
            System.out.println(" • 'min', 'max', 'lcm', or 'gcd' (will pop all stack elements and push the operation's result)");
            System.out.println(" • 'pop' (will print the top element on the stack)");
            System.out.println(" • 'empty' (will print 'true' or 'false' whether the stack is empty)");
            System.out.println(" • 'delaypop' (will run pop, delayed in milliseconds following integer arg's milliseconds)");
            System.out.println();
            System.out.println("Provided args: " + Arrays.toString(args));
            return;
        }


        //Set output of this client based on first argument

        PrintStream outputStream;

        if (args[0].equals("sysout")) {
            outputStream = System.out;
        } else {
            try {
                outputStream = new PrintStream(new FileOutputStream(args[0]));
            } catch(FileNotFoundException e) {
                System.out.println("Couldn't find output file");
                return;
            }
        }

        try {
            //Set up server stub
            
            Registry registry = LocateRegistry.getRegistry();
            Calculator stub = (Calculator) registry.lookup("Calculator");

            int clientId = stub.initClient();


            //Parse command-line inputs
            boolean delayFlag = false;
            for (String input: args) {
                
                //Try to parse an integer input - if an exception is caught, it's a different operation
                try {
                    int integerInput = Integer.parseInt(input);
                    if (delayFlag) {
                        outputStream.println(stub.delayPop(clientId, integerInput));
                    } else {
                        stub.pushValue(clientId, integerInput);
                    }
                } catch(NumberFormatException e) {
                    switch (input) {
                        case "min":
                        case "max":
                        case "lcm":
                        case "gcd": {
                            stub.pushOperation(clientId, input);
                            break;
                        }
                        case "pop": {
                            outputStream.println(stub.pop(clientId));
                            break;
                        }
                        case "empty": {
                            outputStream.println(stub.isEmpty(clientId));
                            break;
                        }
                        case "delaypop": {
                            delayFlag = true;
                            break;
                        }
                        case "clearall": {
                            stub.clearAllStacks();
                            break;
                        }
                    }
                }
            }

            stub.clearStack(clientId);

        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}