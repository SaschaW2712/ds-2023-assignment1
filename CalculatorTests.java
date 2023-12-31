package assignment1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOError;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;  // Import the Scanner class
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class CalculatorTests {

    private CalculatorTests() {}

    public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException {
        try {
            String[] singleClientTests = {
                "unitPushValue",
                "unitPop",
                "unitPushOperationMin",
                "unitPushOperationMax",
                "unitPushOperationLCM",
                "unitPushOperationGCD",
                "unitIsEmptyFalse",
                "unitIsEmptyTrue",
                "unitDelayPop",
                "integrationMultipleMinMaxOperations",
                "integrationOperationsAndIsEmpty",
                "integrationPopAndDelayPop",
            };

            String[] concurrentClientsTests = {
                "unitPushValue",
                "unitDelayPop",
            };

            int passCount = 0;
            int numTests = singleClientTests.length + concurrentClientsTests.length;

            //SINGLE CLIENT TESTS
            System.out.println("Single client tests:");
            for (String test : singleClientTests) {
                boolean currentTestPassed = runSingleClientTest(test);
                if (currentTestPassed) {
                    passCount++;
                }
            }

            System.out.println();

            //CONCURRENT MULTIPLE CLIENT TESTS
            System.out.println("Multiple client tests:");
            for (String test : concurrentClientsTests) {
                boolean passed = runConcurrentClientsTest(test);
                if (passed) {
                    passCount++;
                }
            }

            System.out.println();
            System.out.println(passCount + "/" + numTests + " Tests Passed");
        } catch(Exception e) {
            System.out.println("CalculatorTests exception:");
            System.out.print(e);
            return;
        }
    }

    //Generate arguments to pass to client
    //Takes a string for input and output file names (can be "sysout"), and whether to append "clearall" to the end of the arguments
    public static String[] getClientArgsFromFile(
        String inputFileName,
        String outputFileName,
        boolean appendClearAll
    ) throws IOException {
        try {
            String[] inputFileLines = getFileLinesAsArray(inputFileName);

            ArrayList<String> clientArgs = new ArrayList<String>();

            clientArgs.add(outputFileName);

            for (String line : inputFileLines) {
                clientArgs.add(line);
            }

            if (appendClearAll) {
                clientArgs.add("clearall");
            }

            return clientArgs.toArray(new String[clientArgs.size()]);
        } catch(Exception e) {
            System.out.println("getClientArgsFromFile error:");
            System.out.print(e);
            throw(e);
        }
    }

    //Given an input file name, returns an array of the file's lines as Strings
    public static String[] getFileLinesAsArray(String filename) throws IOException {
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            List<String> lines = new ArrayList<String>();

            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }

            bufferedReader.close();

            return lines.toArray(new String[lines.size()]);            
        } catch(Exception e) {
            System.out.println("getFileLinesAsArray error:");
            System.out.print(e);
            throw(e);
        }

    }

    //Runs a given test on a single client
    //Takes input for the client from the input file corresponding to testName, and compares observed output against expected outputs.
    //Returns "true" or "false" depending on whether the test passed.
    public static boolean runSingleClientTest(String testName) {
        try {
            String inputDirectory = "testInputs/singleClient/";
            String expectedOutputsDirectory = "testExpectedOutputs/singleClient/";
            String observedOutputsDirectory = "testObservedOutputs/singleClient/";

            String[] clientArgs = getClientArgsFromFile(inputDirectory + testName, observedOutputsDirectory + testName, true);
            
            String[] expectedOutput = getFileLinesAsArray(expectedOutputsDirectory + testName);
            
            CalculatorClient.main(clientArgs);

            String[] clientOutput = getFileLinesAsArray(observedOutputsDirectory + testName);

            if (Arrays.equals(clientOutput, expectedOutput)) {
                System.out.println(testName + ": PASS");
                return true;
            } else {
                System.out.println(testName + ": FAIL (see below for details)");
                System.out.println("Expected: " + Arrays.toString(expectedOutput) + "");
                System.out.println("Observed: " + Arrays.toString(clientOutput));
                return false;
            }
        } catch(Exception e) {
            System.out.println("Test error:");
            System.out.print(e);
            return false;
        }
    }

    //Runs a given test on a four clients at once.
    //Takes input for the clients from the input files corresponding to testName, and compares observed output against expected outputs.
    //Returns "true" or "false" depending on whether all clients passed.
    public static boolean runConcurrentClientsTest(String testName) throws FileNotFoundException, IOException {
        String inputDirectory = "testInputs/multipleClients/";
        String expectedOutputsDirectory = "testExpectedOutputs/multipleClients/";
        String observedOutputsDirectory = "testObservedOutputs/multipleClients/";

        String[][] clientArgs = {
            getClientArgsFromFile(inputDirectory + testName + "_1", observedOutputsDirectory + testName + "_1", false),
            getClientArgsFromFile(inputDirectory + testName + "_2", observedOutputsDirectory + testName + "_2", false),
            getClientArgsFromFile(inputDirectory + testName + "_3", observedOutputsDirectory + testName + "_3", false),
            getClientArgsFromFile(inputDirectory + testName + "_4", observedOutputsDirectory + testName + "_4", false),
        };

        String[][] expectedOutputs = {
            getFileLinesAsArray(expectedOutputsDirectory + testName + "_1"),
            getFileLinesAsArray(expectedOutputsDirectory + testName + "_2"),
            getFileLinesAsArray(expectedOutputsDirectory + testName + "_3"),
            getFileLinesAsArray(expectedOutputsDirectory + testName + "_4"),
        };  
        
        for (String[] argList : clientArgs) {
            CalculatorClient.main(argList);
        }

        CalculatorClient.main(new String[] { "sysout", "clearall" });

        String[][] clientOutputs = {
            getFileLinesAsArray(observedOutputsDirectory + testName + "_1"),
            getFileLinesAsArray(observedOutputsDirectory + testName + "_2"),
            getFileLinesAsArray(observedOutputsDirectory + testName + "_3"),
            getFileLinesAsArray(observedOutputsDirectory + testName + "_4"),
        };

        boolean passed = true;

        System.out.println(testName + ":");
        for (int i = 0; i < clientOutputs.length; i++) {
            if (Arrays.equals(clientOutputs[i], expectedOutputs[i])) {
                System.out.println("    Client " + i + ": PASS");
            } else {
                System.out.println("    Client " + i + ": FAIL (see below for details)");
                System.out.println("        Expected: " + Arrays.toString(expectedOutputs[i]) + "");
                System.out.println("        Observed: " + Arrays.toString(clientOutputs[i]));
                passed = false;
            }
        }

        return passed;
    }
}