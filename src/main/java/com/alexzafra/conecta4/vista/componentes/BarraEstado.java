package com.alexzafra.conecta4.vista.componentes;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

/**
 * Barra de estado que muestra mensajes sobre el estado del juego.
 * Informa sobre el turno actual, ganador o empate.
 */
public class BarraEstado extends BorderPane {
    private Label etiquetaEstado;
    private Timeline animacionMensaje;

    /**
     * Constructor de la barra de estado.
     */
    public BarraEstado() {
        // Configuración del panel
        setStyle("-fx-background-color: #323264; -fx-padding: 10; -fx-background-radius: 5;");
        setPadding(new Insets(10, 15, 10, 15));

        // Inicializar componentes
        inicializarComponentes();
    }

    /**
     * Inicializa los componentes de la barra de estado.
     */
    private void inicializarComponentes() {
        // Etiqueta de estado
        etiquetaEstado = new Label("Bienvenido a Conecta 4");
        etiquetaEstado.setStyle("-fx-font-family: Arial; -fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        etiquetaEstado.getStyleClass().add("mensaje-estado");

        // Configurar la animación para restaurar el color
        animacionMensaje = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    etiquetaEstado.getStyleClass().remove("mensaje-exito");
                    etiquetaEstado.getStyleClass().remove("mensaje-error");
                })
        );

        // Centrar en el panel
        setCenter(etiquetaEstado);
        BorderPane.setAlignment(etiquetaEstado, Pos.CENTER);
    }

    /**
     * Establece un nuevo mensaje de estado.
     * @param mensaje Mensaje a mostrar
     */
    public void establecerMensajeEstado(String mensaje) {
        // Detener animación si está en curso
        animacionMensaje.stop();

        // Eliminar estilos de mensajes especiales
        etiquetaEstado.getStyleClass().remove("mensaje-exito");
        etiquetaEstado.getStyleClass().remove("mensaje-error");

        // Establecer el nuevo mensaje
        etiquetaEstado.setText(mensaje);
    }

    /**
     * Establece un mensaje de error (en rojo).
     * @param mensajeError Mensaje de error a mostrar
     */
    public void establecerMensajeError(String mensajeError) {
        // Detener animación si está en curso
        animacionMensaje.stop();

        // Aplicar estilo de error
        etiquetaEstado.getStyleClass().remove("mensaje-exito");
        if (!etiquetaEstado.getStyleClass().contains("mensaje-error")) {
            etiquetaEstado.getStyleClass().add("mensaje-error");
        }

        // Establecer el mensaje
        etiquetaEstado.setText(mensajeError);

        // Iniciar animación para restaurar el color después de un tiempo
        animacionMensaje.playFromStart();
    }

    /**
     * Establece un mensaje de éxito (en verde).
     * @param mensajeExito Mensaje de éxito a mostrar
     */
    public void establecerMensajeExito(String mensajeExito) {
        // Detener animación si está en curso
        animacionMensaje.stop();

        // Aplicar estilo de éxito
        etiquetaEstado.getStyleClass().remove("mensaje-error");
        if (!etiquetaEstado.getStyleClass().contains("mensaje-exito")) {
            etiquetaEstado.getStyleClass().add("mensaje-exito");
        }

        // Establecer el mensaje
        etiquetaEstado.setText(mensajeExito);

        // Iniciar animación para restaurar el color después de un tiempo
        animacionMensaje.playFromStart();
    }
}