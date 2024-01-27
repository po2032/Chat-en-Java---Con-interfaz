package com.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    private ServerSocket serverSocket;
    private List<ManejadorClientes> clients = new ArrayList<>();

    public Servidor(int port) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        System.out.println("Servidor iniciado en el puerto: " + serverSocket.getLocalPort());

        while (true) {
            try {
                // Espera a que un cliente se conecte y crea un nuevo hilo para manejarlo
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket);
                ManejadorClientes clientHandler = new ManejadorClientes(clientSocket, this);
                clients.add(clientHandler);

                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMessage(String message, ManejadorClientes sender) {
        for (ManejadorClientes client : clients) {
            // Evita enviar el mensaje de vuelta al remitente
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ManejadorClientes clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Cliente descanectado: " + clientHandler.getSkCliente());
    }

    public static void main(String[] args) {
        // Puedes cambiar el número de puerto según tus necesidades
        Servidor chatServer = new Servidor(6060);
        chatServer.startServer();
    }
}