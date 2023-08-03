package assignment1;
	
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class CalculatorServer {
	
    public CalculatorServer() {}

    //Instantiates a calculator implementation and makes the server available for clients to access
    public static void main(String args[]) {
        try {
            CalculatorImplementation obj = new CalculatorImplementation();
            obj.init();

            Calculator stub = (Calculator) UnicastRemoteObject.exportObject(obj, 0);

            // Bind the remote object's stub in the registry
            Registry registry = LocateRegistry.getRegistry();
            registry.bind("Calculator", stub);

            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}