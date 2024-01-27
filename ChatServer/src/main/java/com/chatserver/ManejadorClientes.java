package com.chatserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ManejadorClientes implements Runnable {

    private Socket skCliente;
    private Servidor skServidor;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private Map<ManejadorClientes, String> mapClientesNombres = new HashMap<>();

    public ManejadorClientes(Socket cliente, Servidor chatServer) {
        this.skCliente = cliente;
        this.skServidor = chatServer;
        // Asocia el cliente con un nombre de usuario predeterminado al inicio
        mapClientesNombres.put(this, "Nobody");
        try {
            this.outputStream = new ObjectOutputStream(this.skCliente.getOutputStream());
            this.inputStream = new ObjectInputStream(this.skCliente.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Envía un mensaje de bienvenida al skCliente
            sendMessage("Bienvenido a WASAAAA!");
            skServidor.broadcastMessage(getNombreUsuario(this)+" se ha unido a WASAAAA", this);

            while (true) {
                // Lee el mensaje del skCliente
                Object messageObject = inputStream.readObject();

                // Verifica si el mensaje es un cambio de nombre
                if (messageObject instanceof String) {
                    String message = (String) messageObject;

                    // Verifica si es un cambio de nombre
                    if (message.startsWith("CambioNombre:")) {
                        String newUserName = message.substring("CambioNombre:".length());
                        // Actualiza el nombre de usuario asociado a este cliente
                        actualizarNombreUsuario(newUserName);
                    } else {
                        // Envía el mensaje a todos los clientes conectados
                        skServidor.broadcastMessage(getNombreUsuario(this)+": "+message, this);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // Manejar la excepción cuando el cliente se desconecta
            System.out.println("Cliente desconnectado: " + skCliente);
            skServidor.broadcastMessage(getNombreUsuario(this)+" se ha desconectado", this);
            System.out.println(getNombreUsuario(this)+" se ha desconectado");
        } finally {
            // Se desconectó el skCliente
            skServidor.removeClient(this);
            try {
                if (!skCliente.isClosed()) {
                    skCliente.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Envía un mensaje al skCliente
    public void sendMessage(String message) {
        try {
            outputStream.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Devuelve el socket del skCliente
    public Socket getSkCliente() {
        return skCliente;
    }

    // Método para actualizar el nombre de usuario asociado a este cliente
    private void actualizarNombreUsuario(String newUserName) {
        // Obtén el nombre actual del cliente
        String currentUserName = mapClientesNombres.get(this);

        // Actualiza el nombre de usuario asociado a este cliente
        mapClientesNombres.put(this, newUserName);

        // Envía un mensaje al chat indicando el cambio de nombre
        skServidor.broadcastMessage(currentUserName + " se ha cambiado el nombre a " + newUserName, this);
    }

    public String getNombreUsuario(ManejadorClientes cliente) {
        return mapClientesNombres.get(cliente);
    }
}
