package mosqueira.pureStream.ControladorInterno;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * Builds the yt-dlp command based on user preferences and selected options.
 * Returns a list of arguments that will be executed as a process.
 *
 * @author Romina
 */
public class CommandBuilder {

    public static List<String> buildCommand(
            String url,
            PreferencesPanel preferencesPanel,
            String format,
            String quality
    ) {

        List<String> cmd = new ArrayList<>();

        // Path to yt-dlp executable
        String ytDlpPath = preferencesPanel.getExecutablePath();
        if (ytDlpPath == null || ytDlpPath.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debes seleccionar la ruta de yt-dlp en Preferences.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        cmd.add(ytDlpPath);

        // Restrict filenames to avoid special characters
        cmd.add("--restrict-filenames");

        MainFrame main = preferencesPanel.getMainFrame();

        // Download directory
        String downloadPath = main.getRutaDescargas();
        if (downloadPath == null || downloadPath.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Debes seleccionar la carpeta de descargas.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        // FORMAT selection
        if (format.equalsIgnoreCase("mp4")) {

            cmd.add("-f");

            // Extract numeric height from quality (e.g., "720p" → "720")
            String height = quality.replaceAll("[^0-9]", "");

            // Fallback format chain 
            String fallback
                    = "bv*[height<=" + height + "][ext=mp4]+ba[ext=m4a]/"
                    + // prefer mp4+m4a
                    "bv*[height<=" + height + "]+ba/"
                    + // cualquier vídeo + cualquier audio
                    "best";  // fallback final

            cmd.add(fallback);

            cmd.add("--merge-output-format");
            cmd.add("mp4");
        } else if (format.equalsIgnoreCase("mp3")) {

            // Mejor flujo para mp3
            cmd.add("-f");
            cmd.add("bestaudio/best");  // fallback automático

            cmd.add("--extract-audio");
            cmd.add("--audio-format");
            cmd.add("mp3");

            switch (quality) {
                case "128 kbps":
                    cmd.add("--audio-quality");
                    cmd.add("5");
                    break;
                case "192 kbps":
                    cmd.add("--audio-quality");
                    cmd.add("2");
                    break;
                case "320 kbps":
                default:
                    cmd.add("--audio-quality");
                    cmd.add("0");
                    break;
            }
        }

        // Output template (escaped properly)
        String outputTemplate = downloadPath + File.separator + "%(title)s.%(ext)s";
        String safeOutput = "\"" + outputTemplate.replace("\"", "\\\"") + "\"";

        cmd.add("-o");
        cmd.add(safeOutput);

        // Speed limit if enabled
        if (preferencesPanel.isLimitarVelocidad()) {
            cmd.add("--limit-rate");
            cmd.add(preferencesPanel.getLimiteVelocidad() + "K");
        }

        // URL to download
        cmd.add(url);

        return cmd;
    }
}
