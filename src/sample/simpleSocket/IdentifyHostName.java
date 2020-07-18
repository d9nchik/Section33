package sample.simpleSocket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IdentifyHostName {
    public static void main(String[] args) {
        for (String arg : args) {
            try {
                InetAddress address = InetAddress.getByName(arg);
                System.out.print("Host name: " + address.getHostName() + " ");
                System.out.println("IP address: " + address.getHostAddress());
            } catch (UnknownHostException ex) {
                System.err.println("Unknown host or IP address " + arg);
            }
        }
    }
}
