package com.alexzafra.conecta4.vista.dialogos;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;

/**
 * Diálogo que muestra los créditos del juego
 */
public class DialogoCreditos extends Dialog<Void> {

    /**
     * Constructor del diálogo
     */
    public DialogoCreditos() {
        // Configurar el diálogo
        setTitle("Créditos del Juego");
        initStyle(StageStyle.UTILITY);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Crear el contenido del diálogo
        VBox contenido = crearContenido();

        // Añadir a un ScrollPane para permitir desplazamiento si es necesario
        ScrollPane scrollPane = new ScrollPane(contenido);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.setStyle("-fx-background: #28284A; -fx-background-color: #28284A;");

        getDialogPane().setContent(scrollPane);

        // Aplicar estilos
        getDialogPane().getStyleClass().add("dialogo-creditos");
        getDialogPane().setStyle("-fx-background-color: #28284A;");

        // Configurar botón de cerrar
        getDialogPane().lookupButton(ButtonType.CLOSE).setStyle("-fx-base: #5078a0;");
    }

    /**
     * Crea el contenido del diálogo
     * @return Panel con el contenido
     */
    private VBox crearContenido() {
        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #28284A;");
        panel.setAlignment(Pos.CENTER);

        // Título
        Label lblTitulo = new Label("CONECTA 4");
        lblTitulo.setFont(Font.font("System", FontWeight.BOLD, 24));
        lblTitulo.setTextFill(Color.WHITE);

        // Versión
        Label lblVersion = new Label("Versión 1.0");
        lblVersion.setFont(Font.font("System", FontWeight.NORMAL, 16));
        lblVersion.setTextFill(Color.LIGHTGRAY);

        // Desarrollador
        Label lblDesarrollo = crearSeccionCreditos("Desarrollo", "Alejandro Osuna");

        // Gráficos
        Label lblGraficos = crearSeccionCreditos("Diseño Gráfico", "Alejandro Osuna");

        // Sonidos
        Label lblSonidos = crearSeccionCreditos("Efectos de Sonido",
                "Sonidos creados con herramientas libres\n" +
                        "Biblioteca de sonidos: FreeSound.org");


        // Derechos
        Label lblDerechos = new Label("© 2025 Alejandro Osuna. Todos los derechos reservados.");
        lblDerechos.setFont(Font.font("System", FontWeight.NORMAL, 12));
        lblDerechos.setTextFill(Color.LIGHTGRAY);
        lblDerechos.setTextAlignment(TextAlignment.CENTER);
        lblDerechos.setWrapText(true);

        // Añadir componentes al panel principal
        panel.getChildren().addAll(
                lblTitulo,
                lblVersion,
                crearSeparador(),
                lblDesarrollo,
                lblGraficos,
                lblSonidos,
                crearSeparador(),
                crearSeparador(),
                lblDerechos);

        return panel;
    }

    /**
     * Crea una etiqueta de sección para los créditos
     * @param titulo Título de la sección
     * @param contenido Contenido de la sección
     * @return Etiqueta configurada
     */
    private Label crearSeccionCreditos(String titulo, String contenido) {
        String texto = titulo + "\n\n" + contenido;

        Label label = new Label(texto);
        label.setFont(Font.font("System", FontWeight.NORMAL, 14));
        label.setTextFill(Color.WHITE);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setWrapText(true);

        return label;
    }

    /**
     * Crea un separador visual
     * @return Etiqueta que actúa como separador
     */
    private Label crearSeparador() {
        Label separador = new Label("• • • • • • • • • •");
        separador.setFont(Font.font("System", FontWeight.NORMAL, 14));
        separador.setTextFill(Color.GRAY);
        separador.setTextAlignment(TextAlignment.CENTER);

        return separador;
    }
}