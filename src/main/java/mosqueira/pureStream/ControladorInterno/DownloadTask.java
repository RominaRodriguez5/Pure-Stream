package mosqueira.pureStream.ControladorInterno;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import mosqueira.pureStream.Paneles.PreferencesPanel;
import mosqueira.pureStream.Paneles.PanelPrincipal;

/**
 * SwingWorker to handle yt-dlp downloads in background.
 * @author Romina
 */
public class DownloadTask extends SwingWorker<Void, String> {

    private final String url;
    private final PreferencesPanel preferencesPanel;
    private final JTextArea logArea;
    private final JComboBox<String> format;
    private final JComboBox<String> quality;
    private final PanelPrincipal parentPanel;

    private String lastDownloadedFile;

    public DownloadTask(String url, PreferencesPanel panelPref, JTextArea logArea,
                        JComboBox<String> format, JComboBox<String> quality, PanelPrincipal parentPanel) {
        this.url = url;
        this.preferencesPanel = panelPref;
        this.logArea = logArea;
        this.format = format;
        this.quality = quality;
        this.parentPanel = parentPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // Get selected format and quality
        String selectedFormat = format.getSelectedItem() != null ? format.getSelectedItem().toString() : "mp4";
        String selectedQuality = quality.getSelectedItem() != null ? quality.getSelectedItem().toString() : "";

        // Build the command using CommandBuilder
        List<String> command = CommandBuilder.buildCommand(url, preferencesPanel, selectedFormat, selectedQuality);
        if (command == null) {
            publish("Error: Could not build download command.\n");
            return null;
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                publish(line + "\n");

                // Detect downloaded file path
                if (line.contains("has already been downloaded") || line.contains("[Merger] Merging formats into")) {
                    String regex = "[A-Z]:\\\\[^\\s]+\\.(mp4|webm|mkv|mp3)";
                    java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
                    java.util.regex.Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        lastDownloadedFile = matcher.group();
                        publish("Detected file: " + lastDownloadedFile + "\n");
                        if (parentPanel != null) {
                            parentPanel.setLastDownloadedFile(lastDownloadedFile);
                        }
                    }
                }
            }
        }

        int exitCode = process.waitFor();
        publish("\nProcess finished with exit code: " + exitCode + "\n");
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {
            logArea.append(line);
        }
    }

    @Override
    protected void done() {
        logArea.append("\nDownload completed.\n");

        // If file was not detected in output, get the latest file in the download folder
        if (lastDownloadedFile == null && parentPanel != null) {
            String downloadFolder = preferencesPanel.getRutaDescargas();
            if (downloadFolder != null) {
                File dir = new File(downloadFolder);
                if (dir.exists() && dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files != null && files.length > 0) {
                        File latestFile = files[0];
                        for (File f : files) {
                            if (f.lastModified() > latestFile.lastModified()) {
                                latestFile = f;
                            }
                        }
                        lastDownloadedFile = latestFile.getAbsolutePath();
                        parentPanel.setLastDownloadedFile(lastDownloadedFile);
                        logArea.append("File ready to play: " + lastDownloadedFile + "\n");
                        return;
                    }
                }
            }
        }

        if (lastDownloadedFile == null) {
            logArea.append("Could not determine the downloaded file.\n");
        }
    }
}
