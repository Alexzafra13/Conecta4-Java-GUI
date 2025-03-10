package com.alexzafra.conecta4.vista.componentes;

import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

/**
 * Barra superior para ventanas sin bordes que permite arrastrar la ventana
 * y contiene botones de control para minimizar, maximizar y cerrar
 */
public class BarraArrastre extends HBox {

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Constructor de la barra de arrastre
     * @param stage Ventana a la que pertenece la barra
     * @param titulo Título a mostrar en la barra
     */
    public BarraArrastre(Stage stage, String titulo) {
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(5, 10, 5, 10));
        setMinHeight(35);

        // Estilo de la barra
        setStyle("-fx-background-color: #14328c; -fx-border-color: #0a2882; -fx-border-width: 0 0 1 0;");

        // Título
        Label lblTitulo = new Label(titulo);
        lblTitulo.setTextFill(Color.WHITE);
        lblTitulo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Espacio flexible
        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        // Botones de control
        Button btnMinimizar = crearBotonControl("—", "-fx-text-fill: white;");
        Button btnMaximizar = crearBotonControl("□", "-fx-text-fill: white;");
        Button btnCerrar = crearBotonControl("✕", "-fx-text-fill: white;");

        // Configurar acciones de los botones
        btnMinimizar.setOnAction(e -> stage.setIconified(true));

        btnMaximizar.setOnAction(e -> {
            if (stage.isMaximized()) {
                stage.setMaximized(false);
            } else {
                stage.setMaximized(true);
            }
        });

        btnCerrar.setOnAction(e -> stage.close());

        // Añadir componentes a la barra
        getChildren().addAll(lblTitulo, espaciador, btnMinimizar, btnMaximizar, btnCerrar);

        // Configurar eventos de arrastre
        configurarArrastre(stage);
    }

    /**
     * Crea un botón para la barra de control (minimizar, maximizar, cerrar)
     * @param texto Texto del botón
     * @param estilo Estilo CSS adicional
     * @return Botón configurado
     */
    private Button crearBotonControl(String texto, String estilo) {
        Button boton = new Button(texto);
        boton.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; " + estilo);
        boton.setPrefSize(30, 25);

        // Efectos al pasar el ratón
        boton.setOnMouseEntered(e -> {
            boton.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-font-weight: bold; " + estilo);
        });

        boton.setOnMouseExited(e -> {
            boton.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; " + estilo);
        });

        return boton;
    }

    /**
     * Configura los eventos para permitir arrastrar la ventana
     * @param stage Ventana a arrastrar
     */
    private void configurarArrastre(Stage stage) {
        // Cambiar el cursor al pasar sobre la barra
        setOnMouseEntered(e -> setCursor(Cursor.HAND));
        setOnMouseExited(e -> setCursor(Cursor.DEFAULT));

        // Guardar la posición inicial al presionar
        setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        // Mover la ventana al arrastrar
        setOnMouseDragged(e -> {
            // No mover si está maximizada
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
            }
        });

        // Doble clic para maximizar/restaurar
        setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                if (stage.isMaximized()) {
                    stage.setMaximized(false);
                } else {
                    stage.setMaximized(true);
                }
            }
        });
    }
}