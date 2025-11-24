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

            // Extract numeric part from quality string
            String height = quality.replaceAll("[^0-9]", "");

            // Best video + best audio combination
            cmd.add("bestvideo[ext=mp4][height<=" + height + "]+bestaudio[ext=m4a]");

            cmd.add("--merge-output-format");
            cmd.add("mp4");

        } else if (format.equalsIgnoreCase("mp3")) {
            cmd.add("-x"); // Extract audio
            cmd.add("--audio-format");
            cmd.add("mp3");
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
