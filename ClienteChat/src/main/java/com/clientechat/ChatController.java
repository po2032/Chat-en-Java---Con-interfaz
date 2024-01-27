package com.clientechat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ChatController implements Initializable {

    @FXML
    private Button btnEnviar;
    @FXML
    private TextField escribirMensaje;
    @FXML
    private TextArea cajaTexto;
    @FXML
    private Button btnSalir;
    @FXML
    private Label labelUser;

    private String userName;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    @FXML
    private TextField userField;
    @FXML
    private Button usuarioBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Crear el socket y establecer la conexión
            socket = new Socket("localhost", 6060);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            inputStream = new ObjectInputStream(socket.getInputStream());

            // Obtener el nombre de usuario desde el cuadro de diálogo o de alguna manera
            userName = "Nobody";

            // Actualizar la interfaz de usuario con el nombre de usuario
            Platform.runLater(() -> labelUser.setText(userName));

            // Iniciar el hilo para recibir mensajes
            Thread recibirMensajesThread = new Thread(this::recibirMensajes);
            recibirMensajesThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pressButton(ActionEvent event) throws IOException {
        // Manejar la acción del botón de enviar
        String mensaje = escribirMensaje.getText();
        outputStream.writeObject(mensaje);
        outputStream.flush();
        cajaTexto.appendText(labelUser.getText() + ": " + escribirMensaje.getText() + "\n");
        escribirMensaje.clear();
    }

    private void recibirMensajes() {
        try {
            while (true) {
                // Leer el mensaje del servidor
                Object mensajeObject = inputStream.readObject();

                // Comprobar si el mensaje es de tipo String
                if (mensajeObject instanceof String) {
                    String mensaje = (String) mensajeObject;

                    // Actualizar la caja de texto en la interfaz de usuario
                    cajaTexto.appendText(mensaje + "\n");

                    // Mensajes de depuración
                    System.out.println("Mensaje recibido: " + mensaje);
                    System.out.println("Caja de texto actualizada");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void pressButtonSalir(ActionEvent event) {
        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private void cambiarNombre() {
        String newUserName = userField.getText();
        labelUser.setText(newUserName);
        userField.setText("");
        try {
            outputStream.writeObject("CambioNombre:"+newUserName);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void pressButtonCambiar(ActionEvent event) {
        String nuevoUsuario = userField.getText();

        // Verificar si el campo de texto no está vacío y es diferente al usuario actual
        if (!nuevoUsuario.isEmpty() && !nuevoUsuario.isBlank()
                && !nuevoUsuario.equals(labelUser.getText())) {
            cambiarNombre();
        } else {
            mostrarAlerta("No puedes ingresar ese usuario");
        }
    }
    
    public static void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(AlertType.INFORMATION);
        alerta.setTitle("Alerta");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
