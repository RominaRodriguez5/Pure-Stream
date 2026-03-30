package mosqueira.pureStream.ControladorInterno;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Paneles.MainPanel;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * SwingWorker that executes {@code yt-dlp} in the background and streams its output to a log area.
 *
 * <p>The worker builds the command using {@link CommandBuilder}, starts the process,
 * parses progress updates, tries to detect the final output file, and notifies the UI when finished.</p>
 *
 * @author Romina
 * @version 1.0
 */
public class DownloadTask extends SwingWorker<Void, String> {

    /** URL to download. */
    private final String url;

    /** User preferences (yt-dlp path, speed limit, etc.). */
    private final PreferencesPanel preferencesPanel;

    /** Log area where output is appended. */
    private final JTextArea logArea;

    /** Selected output format (mp3/mp4). */
    private final JComboBox<String> format;

    /** Selected quality (360p, 720p, etc.). */
    private final JComboBox<String> quality;

    /** Reference to main panel to notify when the download finishes. */
    private final MainPanel parentPanel;

    /** Detected final output file path (if available). */
    private String detectedFile;

    /** Last progress percentage sent to SwingWorker progress. */
    private int lastProgress = -1;

    /**
     * Creates a background download task executed with {@link SwingWorker}.
     *
     * <p>The task launches yt-dlp using the current user preferences and updates
     * the provided log area with progress/output. When the download finishes,
     * it detects the final file and notifies the {@link MainPanel}.</p>
     *
     * @param url video/audio URL to download
     * @param preferencesPanel user preferences (yt-dlp path, M3U, speed limit, etc.)
     * @param logArea text area where process output and status messages are appended
     * @param format combo box that contains the selected output format (e.g. mp3/mp4)
     * @param quality combo box that contains the selected quality (e.g. 360p/720p)
     * @param parentPanel main panel that receives completion notifications
     */
    public DownloadTask(String url,
            PreferencesPanel preferencesPanel,
            JTextArea logArea,
            JComboBox<String> format,
            JComboBox<String> quality,
            MainPanel parentPanel) {

        this.url = url;
        this.preferencesPanel = preferencesPanel;
        this.logArea = logArea;
        this.format = format;
        this.quality = quality;
        this.parentPanel = parentPanel;
    }

    /**
     * Runs yt-dlp on a background thread, publishing output lines to {@link #process(java.util.List)}.
     *
     * @return null when finished
     * @throws Exception if process execution fails
     */
    @Override
    protected Void doInBackground() throws Exception {

        var mainFrame = parentPanel.getMainFrame();

    String executablePath = preferencesPanel.getExecutablePath();
    if (executablePath == null || executablePath.isBlank()) {
        mainFrame.initializeBundledExecutables();
        executablePath = mainFrame.getExecutablePath();

        if (executablePath != null && !executablePath.isBlank()) {
            preferencesPanel.getTxtExecutable().setText(executablePath);
        }
    }

    if (executablePath == null || executablePath.isBlank()) {
        publish("ERROR: yt-dlp executable not found.\n");
        return null;
    }

    File ytDlpExe = new File(executablePath);
    if (!ytDlpExe.exists() || !ytDlpExe.isFile()) {
        publish("ERROR: The configured yt-dlp executable does not exist.\n");
        return null;
    }

    List<String> builtCommand = CommandBuilder.buildCommand(
            url,
            preferencesPanel,
            format.getSelectedItem().toString(),
            quality.getSelectedItem().toString()
    );

    if (builtCommand == null || builtCommand.isEmpty()) {
        return null;
    }

    ProcessBuilder pb = new ProcessBuilder(builtCommand);
    pb.redirectErrorStream(true);

    publish("__START__");

    Process process = pb.start();

    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

        String line;
        while ((line = reader.readLine()) != null) {
            publish(line + "\n");
            detectDownloadedFile(line);
        }
    }

    process.waitFor();
    return null;
    }

    /**
     * Receives lines published from the background thread and updates the UI log and progress.
     *
     * @param chunks output lines produced by yt-dlp
     */
    @Override
    protected void process(List<String> chunks) {
        for (String raw : chunks) {
            if ("__START__".equals(raw)) {
                logArea.append("Starting download...\n");
                continue;
            }

            String line = raw.trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.startsWith("[download]")) {
                var match = java.util.regex.Pattern
                        .compile("(\\d+(?:\\.\\d+)?)%")
                        .matcher(line);

                if (match.find()) {
                    int p = (int) Double.parseDouble(match.group(1));
                    if (p != lastProgress) {
                        lastProgress = p;
                        setProgress(p);
                    }
                }
                continue;
            }

            if (line.startsWith("Deleting original file")) {
                continue;
            }

            if (line.contains("Merging formats into")) {
                logArea.append("Merging audio and video...\n");
                continue;
            }

            if (line.contains("Destination:") || line.contains("ExtractAudio] Destination:")) {
                logArea.append("Saving file...\n");
                continue;
            }

            if (line.contains("has already been downloaded")) {
                logArea.append("File already downloaded.\n");
                continue;
            }

            if (line.startsWith("WARNING:")) {
                logArea.append("Some formats may be limited.\n");
                continue;
            }

            if (line.startsWith("ERROR:")) {
                logArea.append("Error: " + line + "\n");
            }
        }
    }

    /**
     * Called on the EDT when the background work finishes.
     *
     * <p>Detects the final file, verifies it exists (or finds a close match),
     * and notifies the UI of the downloaded {@link MediaFile}.</p>
     */
    @Override
    protected void done() {
        // If doInBackground threw an exception, rethrow it here to handle/log it.
        try {
            get();
        } catch (Exception ex) {
            logArea.append("Download failed: " + ex.getMessage() + "\n");
            return;
        }

        if (detectedFile == null) {
            logArea.append("No final file detected.\n");
            return;
        }

        String normalized = detectedFile.replace("\"", "").trim();
        File file = new File(normalized);

        String rutaDescargas = parentPanel.getMainFrame().getRutaDescargas();
        File carpeta = new File(rutaDescargas);

        if (!file.exists()) {
            final String base = new File(normalized).getName().replaceAll("\\.f\\d+.*", "");
            final String baseName = base.contains(".")
                    ? base.substring(0, base.lastIndexOf('.'))
                    : base;

            File[] matches = carpeta.listFiles((d, name)
                    -> name.toLowerCase().startsWith(baseName.toLowerCase())
                    && (name.endsWith(".mp4") || name.endsWith(".mp3")
                    || name.endsWith(".m4a") || name.endsWith(".webm"))
            );

            if (matches != null && matches.length > 0) {
                file = matches[0];
            }
        }

        if (!file.exists()) {
            logArea.append("No final file found.\n");
            return;
        }

        MediaFile media = new MediaFile(file, new Date());

        parentPanel.notifyDownloaded(media);
       

        logArea.append("Download completed: " + file.getName() + "\n");
        logArea.append("Saved in: " + file.getParent() + "\n");
    }

    /**
     * Parses a yt-dlp output line to detect the final output file path.
     *
     * @param line a single output line produced by yt-dlp
     */
    private void detectDownloadedFile(String line) {

        if (line.contains("ExtractAudio] Destination:")) {
            detectedFile = line.substring(line.indexOf("Destination:") + 12).trim();
            return;
        }

        if (line.contains("Merging formats into")) {
            detectedFile = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
            return;
        }

        if (line.contains("Destination:")) {
            String f = line.substring(line.indexOf("Destination:") + 12).trim();

            if (!f.contains(".webm") && !f.contains(".f") && !f.endsWith(".m4a")) {
                detectedFile = f;
            }
            return;
        }

        if (line.contains("has already been downloaded")) {
            detectedFile = line.replace("[download]", "")
                    .replace("has already been downloaded", "")
                    .trim();
        }
    }
}