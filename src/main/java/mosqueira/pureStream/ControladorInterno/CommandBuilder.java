package mosqueira.pureStream.ControladorInterno;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * Builds yt-dlp commands based on user preferences and manages .m3u playlist.
 * @author Romina
 */
public class CommandBuilder {

    /**
     * Constructs the yt-dlp command for downloading media.
     *
     * @param url               Video/Audio URL
     * @param preferencesPanel  User preferences (path, format, quality, speed)
     * @param format            Output format ("mp4" or "mp3")
     * @param quality           Desired quality ("1080p", "720p", etc.)
     * @return List of command arguments
     */
    public static List<String> buildCommand(String url, PreferencesPanel preferencesPanel, String format, String quality) {

        List<String> command = new ArrayList<>();
        String ytDlpPath = ConfigProperties.get("yt-dlp.path");
        command.add(ytDlpPath);

        // Download folder
        String downloadPath = preferencesPanel.getRutaDescargas();
        if (downloadPath == null || downloadPath.isEmpty()) {
            downloadPath = ConfigProperties.get("downloads.path");
        }

        // Format and quality
        if (format.equalsIgnoreCase("mp4")) {
            command.add("-f");
            if (quality != null && !quality.isEmpty()) {
                String height = quality.replaceAll("[^0-9]", "");
                command.add("bestvideo[height<=" + height + "]+bestaudio/best[height<=" + height + "]");
            } else {
                command.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4");
            }
        } else {
            command.add("--extract-audio");
            command.add("--audio-format");
            command.add("mp3");
        }

        // Output path
        String outputPath = downloadPath + File.separator + "%(title)s.%(ext)s";
        command.add("-o");
        command.add(outputPath);
        command.add("--restrict-filenames");

        // Speed limit
        if (preferencesPanel.isLimitarVelocidad()) {
            int limiteKBs = preferencesPanel.getLimiteVelocidad();
            if (limiteKBs > 0) {
                command.add("--limit-rate");
                command.add(limiteKBs + "K");
            }
        }

        // Final URL
        command.add(url);

        return command;
    }

    /**
     * Checks if the playlist (.m3u) exists, creates it if missing.
     */
    public static void verificarM3U(String rutaDescargas) {
        if (rutaDescargas == null || rutaDescargas.isEmpty()) return;

        File carpeta = new File(rutaDescargas);
        if (!carpeta.exists() || !carpeta.isDirectory()) return;

        File m3uFile = new File(carpeta, "playlist.m3u");

        if (!m3uFile.exists()) {
            try {
                m3uFile.createNewFile();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Error creating .m3u file: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
