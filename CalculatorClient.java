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
            System.out.println("Too few arguments provided. Please see README for information on running a client with custom input.");
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
            
            Registry registry = LocateRegistry.getRegistry(null, 9999);
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