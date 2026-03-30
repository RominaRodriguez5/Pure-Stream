package mosqueira.pureStream.ControladorInterno;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * Builds the {@code yt-dlp} command based on the selected download options and
 * user preferences.
 *
 * <p>
 * This class generates a list of command-line arguments that can be executed
 * using {@link ProcessBuilder}. It supports downloading as MP4 (video) with
 * resolution constraints and as MP3 (audio) with different bitrate presets.</p>
 *
 * <p>
 * <strong>Note:</strong> If required configuration is missing (e.g. executable
 * path or download folder), this builder shows an error dialog and returns
 * {@code null}.</p>
 *
 * @author Romina
 * @version 1.0
 */
public class CommandBuilder {

    /**
     * Utility class. This class is not meant to be instantiated.
     */
    private CommandBuilder() {
        // Prevent instantiation
    }

    /**
     * Builds the command arguments for {@code yt-dlp} according to the current
     * UI selections.
     *
     * <p>
     * The returned list is intended to be passed directly to
     * {@link ProcessBuilder}.</p>
     *
     * @param url the media URL to download
     * @param preferencesPanel preferences panel that provides executable path,
     * download folder, speed limit configuration, and access to the
     * {@link MainFrame}
     * @param format desired output format (typically {@code "mp4"} or
     * {@code "mp3"})
     * @param quality quality selection (e.g. {@code "720p"} for video or
     * {@code "192 kbps"} for audio)
     * @return list of command-line arguments ready for {@link ProcessBuilder},
     * or {@code null} if configuration is invalid
     */
    public static List<String> buildCommand(
            String url,
            PreferencesPanel preferencesPanel,
            String format,
            String quality
    ) {

        List<String> cmd = new ArrayList<>();
        MainFrame main = preferencesPanel.getMainFrame();

        // Path to yt-dlp executable
        String ytDlpPath = preferencesPanel.getExecutablePath();
        if (ytDlpPath == null || ytDlpPath.isEmpty()) {
            main.initializeBundledExecutables();
            ytDlpPath = main.getExecutablePath();
        }
        if (ytDlpPath == null || ytDlpPath.isBlank()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debes seleccionar la ruta de yt-dlp en Preferences.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }
        cmd.add(ytDlpPath);

        // Use bundled ffmpeg/ffprobe if available
        String toolsDir = main.getBundledToolsDirectory();
        if (toolsDir != null && !toolsDir.isBlank()) {
            cmd.add("--ffmpeg-location");
            cmd.add(toolsDir);
        }

        // Restrict filenames to avoid special characters
        cmd.add("--restrict-filenames");

        // Download directory
        String downloadPath = main.getRutaDescargas();
        if (downloadPath == null || downloadPath.isEmpty()) {
            JOptionPane.showMessageDialog(
                    null,
                    "Debes seleccionar la carpeta de descargas.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return null;
        }

        // FORMAT selection
        if (format.equalsIgnoreCase("mp4")) {

            cmd.add("-f");

            // Extract numeric height from quality (e.g., "720p" -> "720")
            String height = quality.replaceAll("[^0-9]", "");

            // Fallback format chain
            String fallback
                    = "bv*[height<=" + height + "][ext=mp4]+ba[ext=m4a]/"
                    + // prefer mp4+m4a
                    "bv*[height<=" + height + "]+ba/"
                    + // any video + any audio
                    "best";                                               // final fallback

            cmd.add(fallback);

            cmd.add("--merge-output-format");
            cmd.add("mp4");

        } else if (format.equalsIgnoreCase("mp3")) {

            cmd.add("-f");
            cmd.add("bestaudio/best");

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
