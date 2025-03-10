package com.alexzafra.conecta4;

import com.alexzafra.conecta4.vista.PantallaInicio;
import com.alexzafra.conecta4.util.ConfiguracionVentana;
import javafx.application.Application;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación JavaFX Conecta 4
 */
public class Conecta4Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Obtener la configuración guardada
            ConfiguracionVentana config = ConfiguracionVentana.getInstancia();
            Dimension2D resolucion = config.getResolucion();
            boolean pantallaCompleta = config.isPantallaCompleta();

            // Configurar la ventana principal
            primaryStage.setTitle("Conecta 4");
            primaryStage.setFullScreen(pantallaCompleta);

            if (!pantallaCompleta) {
                primaryStage.setWidth(resolucion.getWidth());
                primaryStage.setHeight(resolucion.getHeight());
            }

            primaryStage.setMinWidth(700);
            primaryStage.setMinHeight(600);

            // Intentar cargar un icono (opcional)
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/imagenes/icono.png")));
            } catch (Exception e) {
                System.out.println("No se pudo cargar el icono de la aplicación: " + e.getMessage());
            }

            // Crear y mostrar la pantalla de inicio
            PantallaInicio pantallaInicio = new PantallaInicio(primaryStage);
            Scene scene = new Scene(pantallaInicio, primaryStage.getWidth(), primaryStage.getHeight());

            // Agregar escuchador para cambios de resolución de pantalla
            primaryStage.widthProperty().addListener((obs, oldVal, newVal) -> {
                // Actualizar la configuración global
                if (!primaryStage.isFullScreen()) {
                    config.setResolucion(new Dimension2D(
                            newVal.doubleValue(),
                            primaryStage.getHeight()
                    ));
                }

                // Ajustar el tamaño de los componentes de la pantalla de inicio
                if (scene.getRoot() instanceof PantallaInicio) {
                    ((PantallaInicio) scene.getRoot()).ajustarTamaño(
                            newVal.doubleValue(), primaryStage.getHeight());
                }
            });

            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                // Actualizar la configuración global
                if (!primaryStage.isFullScreen()) {
                    config.setResolucion(new Dimension2D(
                            primaryStage.getWidth(),
                            newVal.doubleValue()
                    ));
                }

                // Ajustar el tamaño de los componentes de la pantalla de inicio
                if (scene.getRoot() instanceof PantallaInicio) {
                    ((PantallaInicio) scene.getRoot()).ajustarTamaño(
                            primaryStage.getWidth(), newVal.doubleValue());
                }
            });

            // Escuchar cambios en el modo de pantalla completa
            primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                // Actualizar la configuración global
                config.setPantallaCompleta(newVal);
            });

            // También detectar cambios en modo pantalla completa
            primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                // Ajustar el tamaño después de un pequeño retraso para permitir
                // que la ventana se redimensione completamente
                javafx.animation.PauseTransition pausa = new javafx.animation.PauseTransition(
                        javafx.util.Duration.millis(100));
                pausa.setOnFinished(e -> {
                    if (scene.getRoot() instanceof PantallaInicio) {
                        ((PantallaInicio) scene.getRoot()).ajustarTamaño(
                                primaryStage.getWidth(), primaryStage.getHeight());
                    }
                });
                pausa.play();
            });

            // Cargar estilos CSS
            try {
                scene.getStylesheets().add(getClass().getResource("/css/estilos.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("No se pudo cargar el archivo CSS: " + e.getMessage());
            }

            primaryStage.setScene(scene);
            primaryStage.show();

            // Ajustar tamaños iniciales después de mostrar la ventana
            pantallaInicio.ajustarTamaño(primaryStage.getWidth(), primaryStage.getHeight());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método main que se usa al ejecutar desde el IDE
     */
    public static void main(String[] args) {
        launch(args);
    }
}