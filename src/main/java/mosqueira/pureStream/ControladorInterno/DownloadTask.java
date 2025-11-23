package mosqueira.pureStream.ControladorInterno;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Paneles.PanelPrincipal;
import mosqueira.pureStream.Paneles.PreferencesPanel;
/**
 * SwingWorker to handle yt-dlp downloads in background.
 * @author Romina
 */
public class DownloadTask extends SwingWorker<Void, String> {

    // URL to download
    private final String url;

    // User preferences (yt-dlp path, speed limit, etc.)
    private final PreferencesPanel preferencesPanel;

    // Log area where progress/output is displayed
    private final JTextArea logArea;

    // Selected output format (mp3/mp4)
    private final JComboBox<String> format;

    // Selected quality (360p, 720p, etc.)
    private final JComboBox<String> quality;

    // Reference to main panel to notify on completion
    private final PanelPrincipal parentPanel;

    // Stores the detected final output file
    private String detectedFile = null;

    public DownloadTask(String url,
                        PreferencesPanel preferencesPanel,
                        JTextArea logArea,
                        JComboBox<String> format,
                        JComboBox<String> quality,
                        PanelPrincipal parentPanel) {

        this.url = url;
        this.preferencesPanel = preferencesPanel;
        this.logArea = logArea;
        this.format = format;
        this.quality = quality;
        this.parentPanel = parentPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // Build yt-dlp command
        List<String> command = CommandBuilder.buildCommand(
                url,
                preferencesPanel,
                format.getSelectedItem().toString(),
                quality.getSelectedItem().toString()
        );

        if (command == null)
            return null;

        // Run process with merged stdout/stderr
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read yt-dlp output line-by-line
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                publish(line + "\n"); // send to process()
                detectDownloadedFile(line); // detect final file name
            }
        }

        process.waitFor();
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {

            // Append full yt-dlp output to log
            logArea.append(line);

            // Extract download percentage when available
            if (line.contains("[download]")) {

                var match = java.util.regex.Pattern
                        .compile("(\\d+(?:\\.\\d+)?)%")
                        .matcher(line);

                if (match.find()) {
                    String percent = match.group(1);
                    logArea.append("Progress: " + percent + "%\n");
                }
            }
        }
    }

    @Override
    protected void done() {

        // Check if any file was detected
        if (detectedFile == null) {
            logArea.append("No final file detected.\n");
            return;
        }

        File file = new File(detectedFile);

        // Download directory selected by the user
        String rutaDescargas = parentPanel.getMainFrame().getRutaDescargas();
        File carpeta = new File(rutaDescargas);

        // If file does not exist, try finding a close match (yt-dlp temp names)
        if (!file.exists()) {

            final String base = new File(detectedFile).getName().replaceAll("\\.f\\d+.*", "");
            final String baseName = base.contains(".")
                    ? base.substring(0, base.lastIndexOf('.'))
                    : base;

            // Look for matching files in the download folder
            File[] matches = carpeta.listFiles((d, name)
                    -> name.toLowerCase().startsWith(baseName.toLowerCase())
                    && (name.endsWith(".mp4") || name.endsWith(".mp3")
                    || name.endsWith(".m4a") || name.endsWith(".webm"))
            );

            if (matches != null && matches.length > 0) {
                file = matches[0];
            }
        }

        // Final check: file must exist
        if (!file.exists()) {
            logArea.append("No final file found.\n");
            return;
        }

        // Create metadata object
        MediaFile media = new MediaFile(file, new Date());

        // Notify PanelPrincipal
        parentPanel.notifyDownloaded(media);

        // Notify MainFrame (library + playlist)
        parentPanel.getMainFrame().notifyDownloadedMedia(media);

        logArea.append("File ready: " + file.getAbsolutePath() + "\n");
    }

    /**
     * Attempts to detect the final filename from yt-dlp logs.
     * Handles extract audio, merges, direct downloads and "already downloaded".
     */
    private void detectDownloadedFile(String line) {

        // ExtractAudio final output
        if (line.contains("ExtractAudio] Destination:")) {
            detectedFile = line.substring(line.indexOf("Destination:") + 12).trim();
            return;
        }

        // Merge output
        if (line.contains("Merging formats into")) {
            detectedFile = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
            return;
        }

        // Direct download output
        if (line.contains("Destination:")) {
            String f = line.substring(line.indexOf("Destination:") + 12).trim();

            // Skip temp/intermediate files
            if (!f.contains(".webm") && !f.contains(".f") && !f.endsWith(".m4a"))
                detectedFile = f;

            return;
        }

        // Already downloaded case
        if (line.contains("has already been downloaded")) {
            String f = line.replace("[download]", "")
                    .replace("has already been downloaded", "")
                    .trim();
            detectedFile = f;
        }
    }
}