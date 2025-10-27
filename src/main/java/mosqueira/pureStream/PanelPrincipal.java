package mosqueira.pureStream;

import java.io.File;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mosqueira
 */
public class PanelPrincipal extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private PanelPreferencias panelPreferencias;
    private String url;
    private String lastDownloadedFile;

    public PanelPrincipal(MainFrame mainFrame, PanelPreferencias panelPref) {
        this.mainFrame = mainFrame;
        this.panelPreferencias = panelPref;
        initComponents();
        setSize(800, 900);
        buttonGroupFormato.add(jrbSelectionMp3);
        buttonGroupFormato.add(jrbSelectionMp4);

    }

    private void exetDOnwloadSecondPlane(String url) {
        jTxtLog.append("Iniciando descarga...\n");

        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<String> comando = construirComando(url);

                ProcessBuilder pb = new ProcessBuilder(comando);
                pb.redirectErrorStream(true);  // mezcla salida estándar y errores
                Process proceso = pb.start();

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(proceso.getInputStream(), StandardCharsets.UTF_8))) {

                    String line;
                    while ((line = reader.readLine()) != null) {
                        publish(line + "\n");
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

                    line = line.trim();

                    // Detectamos la línea que indica el archivo final fusionado
                    if (line.startsWith("[Merger] Merging formats into") || line.contains("has already been downloaded")) {
                        int start = line.indexOf("\"");
                        int end = line.lastIndexOf("\"");
                        if (start != -1 && end != -1 && end > start) {
                            lastDownloadedFile = line.substring(start + 1, end);
                            jTxtLog.append("Archivo final detectado: " + lastDownloadedFile + "\n");
                        }
                    }
                }
            }

            @Override
            protected void done() {
                jTxtLog.append("\nDescarga terminada\n");

                if (lastDownloadedFile != null) {
                    File f = new File(lastDownloadedFile);
                    if (f.exists()) {
                        jTxtLog.append("Archivo final listo para reproducir: " + f.getAbsolutePath() + "\n");
                    } else {
                        jTxtLog.append("No se encontró el archivo final.\n");
                    }
                } else {
                    jTxtLog.append("No se pudo determinar el archivo final.\n");
                }
            }
        };

        worker.execute();
    }

    private List<String> construirComando(String url) {
        List<String> cmd = new ArrayList<>();
        String ytDlpPath = "C:\\Program Files\\yt-dlp\\yt-dlp.exe";

        if (!jrbSelectionMp3.isSelected() && !jrbSelectionMp4.isSelected()) {
            jTxtLog.append(" Debes seleccionar un formato (MP3 o MP4).\n");
            return null;
        }

        cmd.add(ytDlpPath);

        if (jrbSelectionMp4.isSelected()) {
            cmd.add("-f");
            cmd.add("bestvideo[ext=mp4]+bestaudio[ext=m4a]/mp4");
        } else {
            cmd.add("--extract-audio");
            cmd.add("--audio-format");
            cmd.add("mp3");
        }

        // Definir carpeta de destino con -o
        if (panelPreferencias.getRutaDescargas() != null && !panelPreferencias.getRutaDescargas().isEmpty()) {
            String outputPath = panelPreferencias.getRutaDescargas() + File.separator + "%(title)s.%(ext)s";
            cmd.add("-o");
            cmd.add(outputPath);
            jTxtLog.append("Los archivos se guardarán en: " + outputPath + "\n");
        }

        // Crear archivo M3U
        if (panelPreferencias.isCrearM3U()) {
            cmd.add("--write-playlist-metafiles");
        }

        // Limitar velocidad
        if (panelPreferencias.isLimitarVelocidad()) {
            cmd.add("--limit-rate");
            cmd.add("1M"); 
        }

        cmd.add(url);
        return cmd;
    }

    public void setPanelPreferencias(PanelPreferencias panelPreferencias) {
        this.panelPreferencias = panelPreferencias;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupFormato = new javax.swing.ButtonGroup();
        lblUrl = new javax.swing.JLabel();
        jtfEntrada = new java.awt.TextField();
        btnDonwload = new javax.swing.JButton();
        jrbSelectionMp4 = new javax.swing.JRadioButton();
        jrbSelectionMp3 = new javax.swing.JRadioButton();
        lblFormatoSalida = new javax.swing.JLabel();
        jslFormato = new javax.swing.JSeparator();
        jslOptionA = new javax.swing.JSeparator();
        btnReproducir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTxtLog = new javax.swing.JTextArea();

        setLayout(null);

        lblUrl.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lblUrl.setForeground(new java.awt.Color(0, 0, 0));
        lblUrl.setText("Url");
        add(lblUrl);
        lblUrl.setBounds(100, 70, 50, 20);

        jtfEntrada.setBackground(new java.awt.Color(204, 204, 204));
        jtfEntrada.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jtfEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtfEntradaActionPerformed(evt);
            }
        });
        add(jtfEntrada);
        jtfEntrada.setBounds(160, 70, 380, 30);

        btnDonwload.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnDonwload.setForeground(new java.awt.Color(0, 0, 153));
        btnDonwload.setText("Donwload");
        btnDonwload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDonwloadActionPerformed(evt);
            }
        });
        add(btnDonwload);
        btnDonwload.setBounds(560, 70, 100, 24);

        jrbSelectionMp4.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jrbSelectionMp4.setForeground(new java.awt.Color(0, 0, 153));
        jrbSelectionMp4.setText("Video(Mp4)");
        jrbSelectionMp4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbSelectionMp4ActionPerformed(evt);
            }
        });
        add(jrbSelectionMp4);
        jrbSelectionMp4.setBounds(150, 200, 120, 25);

        jrbSelectionMp3.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jrbSelectionMp3.setForeground(new java.awt.Color(0, 0, 153));
        jrbSelectionMp3.setText("Audio(Mp3)");
        jrbSelectionMp3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jrbSelectionMp3ActionPerformed(evt);
            }
        });
        add(jrbSelectionMp3);
        jrbSelectionMp3.setBounds(410, 200, 110, 25);

        lblFormatoSalida.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        lblFormatoSalida.setForeground(new java.awt.Color(0, 0, 0));
        lblFormatoSalida.setText("Formato de Salida ");
        add(lblFormatoSalida);
        lblFormatoSalida.setBounds(270, 160, 160, 20);
        add(jslFormato);
        jslFormato.setBounds(0, 130, 790, 10);
        add(jslOptionA);
        jslOptionA.setBounds(10, 260, 780, 10);

        btnReproducir.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnReproducir.setForeground(new java.awt.Color(0, 0, 153));
        btnReproducir.setText("Reproducir");
        btnReproducir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReproducirActionPerformed(evt);
            }
        });
        add(btnReproducir);
        btnReproducir.setBounds(290, 610, 120, 27);

        jTxtLog.setBackground(new java.awt.Color(204, 204, 204));
        jTxtLog.setColumns(20);
        jTxtLog.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jTxtLog.setRows(5);
        jScrollPane1.setViewportView(jTxtLog);

        add(jScrollPane1);
        jScrollPane1.setBounds(0, 290, 790, 290);
    }// </editor-fold>//GEN-END:initComponents

    private void jtfEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtfEntradaActionPerformed

    }//GEN-LAST:event_jtfEntradaActionPerformed

    private void btnDonwloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDonwloadActionPerformed
        url = jtfEntrada.getText().trim();
        if (url.isEmpty()) {
            jTxtLog.append("Por favor, introduce una URL válida.\n");
            return;
        }
        exetDOnwloadSecondPlane(url);
    }//GEN-LAST:event_btnDonwloadActionPerformed

    private void jrbSelectionMp4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbSelectionMp4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jrbSelectionMp4ActionPerformed

    private void jrbSelectionMp3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jrbSelectionMp3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jrbSelectionMp3ActionPerformed

    private void btnReproducirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReproducirActionPerformed
        if (lastDownloadedFile != null) {
            java.io.File file = new java.io.File(lastDownloadedFile);
            if (file.exists()) {
                try {
                    jTxtLog.append("Reproduciendo: " + file.getName() + "\n");
                    java.awt.Desktop.getDesktop().open(file);
                } catch (Exception e) {
                    jTxtLog.append("ERROR al reproducir el archivo: " + e.getMessage() + "\n");
                }
            } else {
                jTxtLog.append("No se encontró el archivo descargado.\n");
            }
        } else {
            jTxtLog.append("Primero descarga un archivo para poder reproducirlo.\n");
        }

    }//GEN-LAST:event_btnReproducirActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDonwload;
    private javax.swing.JButton btnReproducir;
    private javax.swing.ButtonGroup buttonGroupFormato;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTxtLog;
    private javax.swing.JRadioButton jrbSelectionMp3;
    private javax.swing.JRadioButton jrbSelectionMp4;
    private javax.swing.JSeparator jslFormato;
    private javax.swing.JSeparator jslOptionA;
    private java.awt.TextField jtfEntrada;
    private javax.swing.JLabel lblFormatoSalida;
    private javax.swing.JLabel lblUrl;
    // End of variables declaration//GEN-END:variables
}
