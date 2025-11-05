package mosqueira.pureStream.ControladorInterno;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * Clase encargada de construir el comando yt-dlp según las preferencias del usuario.
 */
public class CommandBuilder {

    public static List<String> construirComando(String url, PreferencesPanel panelPreferencias,
         JTextArea jTxtLog, JRadioButton jrbSelectionMp3, JRadioButton jrbSelectionMp4) {

        List<String> cmd = new ArrayList<>();
        String ytDlpPath = "C:\\Program Files\\yt-dlp\\yt-dlp.exe";

        if (!jrbSelectionMp3.isSelected() && !jrbSelectionMp4.isSelected()) {
            jTxtLog.append("Debes seleccionar un formato (MP3 o MP4).\n");
            return null;
        }

        cmd.add(ytDlpPath);

        // Selección del formato
        if (jrbSelectionMp4.isSelected()) {
            cmd.add("-f");
            cmd.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4");
        } else {
            cmd.add("--extract-audio");
            cmd.add("--audio-format");
            cmd.add("mp3");
        }

        // Carpeta destino con nombre del vídeo (limpio)
        if (panelPreferencias.getRutaDescargas() != null && !panelPreferencias.getRutaDescargas().isEmpty()) {
            String outputPath = panelPreferencias.getRutaDescargas() + File.separator + "%(title)s.%(ext)s";
            cmd.add("-o");
            cmd.add(outputPath);

            // Evita símbolos raros en el nombre del archivo
            cmd.add("--restrict-filenames");

            jTxtLog.append("Los archivos se guardarán en: " + panelPreferencias.getRutaDescargas() + "\n");
        }

        // Opciones adicionales según preferencias del usuario
        if (panelPreferencias.isCrearM3U()) {
            cmd.add("--write-playlist-metafiles");
        }

        if (panelPreferencias.isLimitarVelocidad()) {
            cmd.add("--limit-rate");
            cmd.add("1M");
        }

        cmd.add(url);
        return cmd;
    }
}
