package mosqueira.pureStream.ControladorInterno;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import mosqueira.pureStream.Paneles.PreferencesPanel;
import mosqueira.pureStream.Paneles.PanelPrincipal;

public class DownloadTask extends SwingWorker<Void, String> {

    private final String url;
    private final PreferencesPanel panelPreferencias;
    private final JTextArea jTxtLog;
    private final JRadioButton jrbSelectionMp3;
    private final JRadioButton jrbSelectionMp4;
    private final PanelPrincipal parentPanel;

    private String lastDownloadedFile;

    public DownloadTask(String url, PreferencesPanel panelPref, JTextArea logArea,
            JRadioButton mp3, JRadioButton mp4, PanelPrincipal parentPanel) {
        this.url = url;
        this.panelPreferencias = panelPref;
        this.jTxtLog = logArea;
        this.jrbSelectionMp3 = mp3;
        this.jrbSelectionMp4 = mp4;
        this.parentPanel = parentPanel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        List<String> comando = CommandBuilder.construirComando(url, panelPreferencias, jTxtLog, jrbSelectionMp3, jrbSelectionMp4);
        if (comando == null) {
            publish("No se construyó el comando correctamente.\n");
            return null;
        }

        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        Process proceso = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(proceso.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                publish(line + "\n");

                // Detectar archivo descargado
                if (line.contains("has already been downloaded") || line.contains("[Merger] Merging formats into")) {
                    // Extraer la ruta del archivo si aparece entre comillas
                    int start = line.indexOf("\"");
                    int end = line.lastIndexOf("\"");
                    if (start != -1 && end != -1 && end > start) {
                        lastDownloadedFile = line.substring(start + 1, end);
                        publish("Archivo detectado: " + lastDownloadedFile + "\n");
                    }
                }
            }
        }

        int exitCode = proceso.waitFor();
        publish("\nProceso finalizado con código: " + exitCode + "\n");
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        for (String line : chunks) {
            jTxtLog.append(line);
        }
    }

    @Override
    protected void done() {
        jTxtLog.append("\nDescarga terminada\n");

        // Intentar detectar automáticamente el archivo en la carpeta destino
        if (parentPanel != null) {
            // Obtener la carpeta de destino desde las preferencias
            String carpetaDestino = panelPreferencias.getRutaDescargas(); // <-- asegúrate de que exista este método
            if (carpetaDestino != null) {
                File dir = new File(carpetaDestino);
                if (dir.exists() && dir.isDirectory()) {
                    // Buscar el archivo más reciente en la carpeta
                    File[] files = dir.listFiles();
                    if (files != null && files.length > 0) {
                        File ultimoArchivo = files[0];
                        for (File f : files) {
                            if (f.lastModified() > ultimoArchivo.lastModified()) {
                                ultimoArchivo = f;
                            }
                        }
                        lastDownloadedFile = ultimoArchivo.getAbsolutePath();
                        parentPanel.setLastDownloadedFile(lastDownloadedFile);
                        jTxtLog.append("Archivo final listo para reproducir: " + lastDownloadedFile + "\n");
                        return;
                    }
                }
            }
        }

        jTxtLog.append("No se pudo determinar el archivo final.\n");
    }

}
