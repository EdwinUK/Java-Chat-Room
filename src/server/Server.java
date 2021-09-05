package server;

import java.net.*;
import java.util.*;

public class Server {

    private static DatagramSocket socket;
    private static boolean running;
    public static int ClientID;
    private static ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
    public static ArrayList<String> userlist = new ArrayList<String>();
    public static String coordinatorRole = " you have been assigned as the coordinator (/help for commands).";
    public static String coordinatorName = "";


    public static void start(int port) {

        try {

            socket = new DatagramSocket(port);

            running = true;
            listen();
            System.out.println("Server is running on port " + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void memberlist() {
        for (ClientInfo username : clients) {
            if (!userlist.contains(username.getName())) {
                userlist.add(username.getName());
            }
        }
    }


    private static void broadcast(String message) {
        for (ClientInfo info : clients) {
            send(message, info.getAddress(), info.getPort());
        }
    }

    private static void send(String message, InetAddress address, int port) {
        try {
            message += "\\e";
            byte[] data = message.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
            socket.send(packet);
            System.out.println("Sent Message to " + address.getHostAddress() + ":" + port);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void listen() {
        Thread listenThread = new Thread("ChatProgram Listener") {
            public void run() {
                try {
                    while (running) {

                        byte[] data = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(data, data.length);
                        socket.receive(packet);
                        String message = new String(data);
                        message = message.substring(0, message.indexOf("\\e"));

                        // MANAGE MESSAGE
                        processMessage(message, packet);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        listenThread.start();
    }


    public static void isCommandToConnect(String message, DatagramPacket packet) {
        if (message.startsWith("\\command") && commandType(message).equals("connect")) {
            String userName = message.substring(message.lastIndexOf(" ") + 1);
            connectUser(userName, packet);
            if (ClientID == 1) {
                broadcast("User " + userName + " has connected" + coordinatorRole);
                coordinatorName = userName;
                memberlist();
            } else {
                broadcast("User " + userName + " has connected (/help for commands).");
                memberlist();
            }
        }
    }

    public static void processMessage(String message, DatagramPacket packet) {
        isCommandToConnect(message, packet);

        if (isUserCommand(message)) {
            String userName = message.substring(0, message.indexOf(":"));
            String commandType = commandType(message);
            if (isCoordinator(userName) && commandType == "kick") {
                String userToKick = message.substring(message.lastIndexOf(" ") + 1);
                disconnectUser(userToKick);
            } else if (commandType == "disconnect") {
                connectUser(userName, packet);
            } else if (commandType == "users") {
                showUsers(userlist);
            } else if (commandType == "leave") {
                leaveServer(userName);
            } else if (commandType == "help") {
                helpInfo();
            }
        } else {
            String issuingUser = message.substring(0, message.lastIndexOf(":"));
            boolean existingUser = clients.stream().filter(clientInfo -> clientInfo.getName().equals(issuingUser)).findAny().isPresent();
            if (existingUser) {
                broadcast(message);
            }
        }

    }

    public static boolean isCoordinator(String username) {
        if (username.equals(coordinatorName)) {
            return true;
        }
        return false;
    }

    public static boolean isUserCommand(String message) {
        if (message.substring(message.indexOf(":")).contains("/")) {
            return true;
        } else {
            return false;
        }
    }


    public static void connectUser(String username, DatagramPacket packet) {
        clients.add(new ClientInfo(username, ClientID++, packet.getAddress(), packet.getPort()));
    }

    public static void disconnectUser(String username) {
        Optional<ClientInfo> client = clients.stream().filter(clientInfo -> clientInfo.getName().equals(username)).findAny();
        if (client.isPresent()) {
            clients.remove(client.get());
            removeUser(username);
            broadcast(username + " has been kicked from the server!");
        } else {
            System.out.println("This user does not exist.");
        }
        if (isTheNewCoordinatorNeeded(username)) {
            pickNewCoordinator();
        }
    }

    public static void leaveServer(String username) {
        Optional<ClientInfo> client = clients.stream().filter(clientInfo -> clientInfo.getName().equals(username)).findAny();
        if (client.isPresent()) {
            clients.remove(client.get());
            removeUser(username);
            broadcast(username + " has left the server!");
        }
        if (isTheNewCoordinatorNeeded(username)) {
            pickNewCoordinator();
            broadcast("The coordinator " + username + " has left, so the new assigned coordinator is " + coordinatorName);
        }
    }

    public static void removeUser(String username) {
        userlist.remove(username);
    }

    public static void showUsers(ArrayList<String> userlist) {
        broadcast("The active users in this chat are " + userlist);
    }

    public static void helpInfo() {
        broadcast("Here are all the commands: \n /users - Shows all active users in the chatroom \n /leave - To leave the chatroom \n /help - Shows all of the current commands \n /kick (coordinator only) - Kicks whichever user they choose");
    }


    public static String commandType(String command) {
        if (command.contains("connect")) {
            return "connect";
        } else if (command.contains("kick")) {
            return "kick";
        } else if (command.contains("users")) {
            return "users";
        } else if (command.contains("leave")) {
            return "leave";
        } else if (command.contains("help")) {
            return "help";
        }
        return "not found";
    }

    public static void pickNewCoordinator() {
        coordinatorName = clients.stream().findFirst().get().getName();
    }

    public static boolean isTheNewCoordinatorNeeded(String disconnectingUserName) {
        if (disconnectingUserName.equals(coordinatorName)) {
            return true;
        }
        return false;
    }
}