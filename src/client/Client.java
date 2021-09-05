package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private static DatagramSocket socket;
    public boolean running;
    private InetAddress address;
    private int port;
    public String name;

    public Client(String name, String address, int port) {
        try {
            this.name = name;
            this.address = InetAddress.getByName(address);
            this.port = port;
            socket = new DatagramSocket();
            running = true;
            listen();
            send("\\command: connect " + name);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void send(String message) {
        try {

            if (!message.startsWith("\\")) {
                message = name + ": " + message;
            }

            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to " + address.getHostAddress() + ":" + port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        Thread listenThread = new Thread("ChatProgram Listener") {
            public void run() {
                try {
                    while (running == true) {

                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);

                        String message = new String(data);
                        message = message.substring(0, message.indexOf("\\e"));

                        if (isCommandToDisconnect(message)) { // message needs to be equal con: disconnect change isCommand function
                            ClientGUI.printToConsole("You have been disconnected");
                        } else {
                            ClientGUI.printToConsole(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        listenThread.start();
    }

    public boolean isCommandToDisconnect(String message) {

        if (message.equals("disconnect" + name)) {


            return true;
        }


        return false;
    }
}
