package com.alexzafra.conecta4.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Sistema de audio para el juego Conecta 4.
 * Gestiona la reproducción de efectos de sonido y música de fondo.
 * Implementa el patrón Singleton.
 */
public class SistemaAudio {
    // Instancia única (patrón Singleton)
    private static SistemaAudio instancia;

    // Mapa para almacenar los reproductores de efectos
    private Map<String, MediaPlayer> reproductoresEfectos;

    // Reproductor para la música de fondo
    private MediaPlayer reproductorMusica;

    // Lista de pistas de música disponibles
    private List<String> pistasMusicales;
    private Map<String, String> nombresCancionesLimpios;
    private String cancionActual;

    // Volumen (0.0 a 1.0)
    private double volumenEfectos = 0.7;
    private double volumenMusica = 0.5;

    // Estado para controlar si el audio está activado
    private boolean audioActivado = true;

    // Lista de observadores para notificar cambios en la música
    private List<CambioMusicaListener> observadores;

    /**
     * Interfaz para notificar cambios en la música
     */
    public interface CambioMusicaListener {
        void onCambioMusica(String cancion);
    }

    /**
     * Constructor privado para el patrón Singleton
     */
    private SistemaAudio() {
        reproductoresEfectos = new HashMap<>();
        pistasMusicales = new ArrayList<>();
        nombresCancionesLimpios = new HashMap<>();
        observadores = new ArrayList<>();

        cargarSonidos();
    }

    /**
     * Obtiene la instancia única del sistema de audio.
     * @return Instancia del sistema de audio
     */
    public static SistemaAudio getInstancia() {
        if (instancia == null) {
            instancia = new SistemaAudio();
        }
        return instancia;
    }

    /**
     * Registra un observador para recibir notificaciones de cambios en la música
     * @param observador Observador a registrar
     */
    public void registrarObservador(CambioMusicaListener observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    /**
     * Elimina un observador de la lista
     * @param observador Observador a eliminar
     */
    public void eliminarObservador(CambioMusicaListener observador) {
        observadores.remove(observador);
    }

    /**
     * Notifica a todos los observadores de un cambio en la música
     * @param cancion Nueva canción
     */
    private void notificarCambioMusica(String cancion) {
        for (CambioMusicaListener observador : observadores) {
            observador.onCambioMusica(cancion);
        }
    }

    /**
     * Carga los archivos de sonido para efectos y música.
     */
    private void cargarSonidos() {
        try {
            // Cargar efectos de sonido
            cargarEfecto("boton", "/audio/boton.mp3");
            cargarEfecto("ficha_colocada", "/audio/ficha_colocada.mp3");
            cargarEfecto("victoria", "/audio/victoria.mp3");
            cargarEfecto("empate", "/audio/empate.mp3");

            // Inicializar lista de pistas musicales
            registrarPistaMusical("/audio/musica_fondo.mp3", "Música de Fondo");
            registrarPistaMusical("/audio/Musica-conecta-4-relax2.mp3", "Relax 2");
            registrarPistaMusical("/audio/Musica-conecta-4-relax3.mp3", "Relax 3");
            registrarPistaMusical("/audio/Musica-conecta-4-relax4.mp3", "Relax 4");
            registrarPistaMusical("/audio/Musica-conecta-4-relax5.mp3", "Relax 5");

            // Cargar música inicial (la primera pista)
            if (!pistasMusicales.isEmpty()) {
                cancionActual = pistasMusicales.get(0);
                cargarMusica(cancionActual);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar los sonidos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Registra una pista musical en la lista de pistas disponibles
     * @param ruta Ruta de la pista
     * @param nombreLimpio Nombre para mostrar
     */
    private void registrarPistaMusical(String ruta, String nombreLimpio) {
        pistasMusicales.add(ruta);
        nombresCancionesLimpios.put(ruta, nombreLimpio);
    }

    /**
     * Carga un efecto de sonido en el mapa de reproductores.
     * @param nombre Nombre identificativo del efecto
     * @param ruta Ruta del archivo de sonido
     */
    private void cargarEfecto(String nombre, String ruta) {
        try {
            // Lista de posibles rutas para probar
            String[] posiblesRutas = {
                    ruta,                       // ruta original
                    ruta.replace("/audio/", "/"), // sin carpeta audio
                    "/audio/" + ruta.substring(ruta.lastIndexOf("/") + 1) // solo nombre de archivo con /audio/
            };

            for (String r : posiblesRutas) {
                URL efectoUrl = getClass().getResource(r);
                if (efectoUrl != null) {
                    Media efecto = new Media(efectoUrl.toString());
                    MediaPlayer reproductor = new MediaPlayer(efecto);
                    reproductor.setVolume(volumenEfectos);
                    reproductor.setOnEndOfMedia(() -> reproductor.stop());
                    reproductoresEfectos.put(nombre, reproductor);
                    System.out.println("Efecto " + nombre + " cargado desde: " + r);
                    return; // Salir si se encuentra el archivo
                }
            }

            System.err.println("No se pudo encontrar el archivo para el efecto " + nombre);
        } catch (Exception e) {
            System.err.println("Error al cargar efecto " + nombre + ": " + e.getMessage());
        }
    }

    /**
     * Carga la música de fondo
     * @param ruta Ruta del archivo de música
     */
    private void cargarMusica(String ruta) {
        try {
            URL musicaUrl = getClass().getResource(ruta);
            if (musicaUrl != null) {
                // Si ya hay un reproductor, detenerlo
                if (reproductorMusica != null) {
                    reproductorMusica.stop();
                    reproductorMusica.dispose(); // Liberar recursos
                }

                // Crear nuevo reproductor
                Media musica = new Media(musicaUrl.toString());
                reproductorMusica = new MediaPlayer(musica);
                reproductorMusica.setVolume(volumenMusica);
                reproductorMusica.setCycleCount(MediaPlayer.INDEFINITE);

                // Configurar evento cuando termina una canción (útil si se cambia a modo no-repetición)
                reproductorMusica.setOnEndOfMedia(() -> {
                    // Si quisiéramos pasar a la siguiente canción automáticamente:
                    // siguienteCancion();
                    reproductorMusica.seek(Duration.ZERO); // Reiniciar la canción
                });

                // Guardar la canción actual
                cancionActual = ruta;

                // Notificar a los observadores
                notificarCambioMusica(cancionActual);

                System.out.println("Música cargada: " + ruta);
            } else {
                System.err.println("No se pudo encontrar el archivo de música: " + ruta);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar música " + ruta + ": " + e.getMessage());
        }
    }

    /**
     * Cambia la música de fondo por otra pista
     * @param ruta Ruta del archivo de música
     */
    public void cambiarMusica(String ruta) {
        cargarMusica(ruta);
        if (audioActivado) {
            reproducirMusica();
        }
    }

    /**
     * Avanza a la siguiente canción de la lista
     */
    public void siguienteCancion() {
        if (pistasMusicales.isEmpty()) return;

        int indiceActual = pistasMusicales.indexOf(cancionActual);
        if (indiceActual == -1) indiceActual = 0;

        int siguienteIndice = (indiceActual + 1) % pistasMusicales.size();
        String siguienteCancion = pistasMusicales.get(siguienteIndice);

        cambiarMusica(siguienteCancion);
    }

    /**
     * Reproduce un efecto de sonido por su nombre.
     * @param nombre Nombre del efecto a reproducir
     */
    public void reproducirEfecto(String nombre) {
        if (!audioActivado) return;

        try {
            MediaPlayer reproductor = reproductoresEfectos.get(nombre);
            if (reproductor != null) {
                reproductor.seek(Duration.ZERO);
                reproductor.play();
            } else {
                System.err.println("Efecto no encontrado: " + nombre);
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir efecto " + nombre + ": " + e.getMessage());
        }
    }

    /**
     * Inicia la reproducción de la música de fondo.
     */
    public void reproducirMusica() {
        if (!audioActivado) return;

        try {
            if (reproductorMusica != null) {
                reproductorMusica.play();
            }
        } catch (Exception e) {
            System.err.println("Error al reproducir música de fondo: " + e.getMessage());
        }
    }

    /**
     * Pausa la reproducción de la música de fondo.
     */
    public void pausarMusica() {
        try {
            if (reproductorMusica != null) {
                reproductorMusica.pause();
            }
        } catch (Exception e) {
            System.err.println("Error al pausar música de fondo: " + e.getMessage());
        }
    }

    /**
     * Detiene la reproducción de la música de fondo.
     */
    public void detenerMusica() {
        try {
            if (reproductorMusica != null) {
                reproductorMusica.stop();
            }
        } catch (Exception e) {
            System.err.println("Error al detener música de fondo: " + e.getMessage());
        }
    }

    /**
     * Verifica si la música está reproduciendo actualmente
     * @return true si está reproduciendo, false si está detenida o pausada
     */
    public boolean isMusicaReproduciendo() {
        return reproductorMusica != null &&
                reproductorMusica.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Obtiene el nombre limpio de la canción actual
     * @return Nombre de la canción para mostrar
     */
    public String getNombreCancionActual() {
        if (cancionActual == null) return "Sin música";
        return nombresCancionesLimpios.getOrDefault(cancionActual,
                cancionActual.substring(cancionActual.lastIndexOf("/") + 1));
    }

    /**
     * Obtiene la ruta de la canción actual
     * @return Ruta completa de la canción actual
     */
    public String getRutaCancionActual() {
        return cancionActual;
    }

    /**
     * Establece el volumen de los efectos de sonido.
     * @param volumen Volumen (0.0 a 1.0)
     */
    public void setVolumenEfectos(double volumen) {
        this.volumenEfectos = volumen;
        for (MediaPlayer reproductor : reproductoresEfectos.values()) {
            reproductor.setVolume(volumen);
        }
    }

    /**
     * Obtiene el volumen actual de los efectos
     * @return Volumen (0.0 a 1.0)
     */
    public double getVolumenEfectos() {
        return volumenEfectos;
    }

    /**
     * Establece el volumen de la música de fondo.
     * @param volumen Volumen (0.0 a 1.0)
     */
    public void setVolumenMusica(double volumen) {
        this.volumenMusica = volumen;
        if (reproductorMusica != null) {
            reproductorMusica.setVolume(volumen);
        }
    }

    /**
     * Obtiene el volumen actual de la música
     * @return Volumen (0.0 a 1.0)
     */
    public double getVolumenMusica() {
        return volumenMusica;
    }

    /**
     * Activa o desactiva todo el audio del juego.
     * @param activado true para activar, false para desactivar
     */
    public void setAudioActivado(boolean activado) {
        this.audioActivado = activado;
        if (!activado) {
            detenerMusica();
        } else if (reproductorMusica != null) {
            reproducirMusica();
        }
    }

    /**
     * Verifica si el audio está activado.
     * @return true si el audio está activado, false en caso contrario
     */
    public boolean isAudioActivado() {
        return audioActivado;
    }

    /**
     * Libera los recursos cuando la aplicación se cierra
     */
    public void liberarRecursos() {
        try {
            // Detener y liberar reproductores de efectos
            for (MediaPlayer reproductor : reproductoresEfectos.values()) {
                reproductor.stop();
                reproductor.dispose();
            }
            reproductoresEfectos.clear();

            // Detener y liberar reproductor de música
            if (reproductorMusica != null) {
                reproductorMusica.stop();
                reproductorMusica.dispose();
                reproductorMusica = null;
            }

            System.out.println("Recursos de audio liberados correctamente");
        } catch (Exception e) {
            System.err.println("Error al liberar recursos de audio: " + e.getMessage());
        }
    }

    /**
     * Obtiene la lista de todas las pistas musicales disponibles
     * @return Lista de rutas de las pistas
     */
    public List<String> getPistasMusicales() {
        return new ArrayList<>(pistasMusicales);
    }

    /**
     * Obtiene el mapa de nombres limpios de las canciones
     * @return Mapa de ruta -> nombre limpio
     */
    public Map<String, String> getNombresCancionesLimpios() {
        return new HashMap<>(nombresCancionesLimpios);
    }
}