package mosqueira.pureStream.Paneles;

import java.awt.Color;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.plaf.basic.BasicProgressBarUI;
import mosqueira.pureStream.ControladorInterno.DownloadTask;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Modelo.MediaTableModel;
import mosqueira.pureStream.diseñoApp.IconUtils;
import mosqueira.pureStream.diseñoApp.MainPanelLayout;

/**
 * Main panel that handles URL input, download actions, and interaction with
 * Preferences and Library panels.
 *
 * @author Romina
 */
public class MainPanel extends javax.swing.JPanel {

    // Reference to the preferences panel where the user sets the download folder
    private PreferencesPanel preferencesPanel;

    // Stores the path of the last downloaded file
    private String lastDownloadedFile;

    // Reference to the main frame to switch between panels
    private MainFrame mainFrame;

    private JProgressBar progressBar;

    /**
     * Constructs the main download panel. Initializes references to the
     * PreferencesPanel, MainFrame, and shared MediaTableModel.
     *
     * @param panelPref Reference to the preferences configuration panel
     * @param mainFrame Reference to the main window for switching panels
     * @param tableModel Shared model containing downloaded media
     */
    public MainPanel(PreferencesPanel panelPref, MainFrame mainFrame, MediaTableModel tableModel) {
        initComponents();
        this.preferencesPanel = panelPref;
        this.mainFrame = mainFrame;
        new MainPanelLayout(this).apply();
        setOpaque(false);
        javax.swing.UIManager.put("ProgressBar.selectionForeground", java.awt.Color.BLUE);
        javax.swing.UIManager.put("ProgressBar.selectionBackground", java.awt.Color.BLUE);
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setString("0%");

        progressBar.setUI(new BasicProgressBarUI());
        progressBar.setForeground(Color.GRAY);

        jScrollPane1.setColumnHeaderView(progressBar);
        progressBar.setPreferredSize(new java.awt.Dimension(0, 18));

        // Show current download path in the text field
        jtxtFolderDownload.setText(mainFrame.getRutaDescargas());

        btnDownload.setIcon(IconUtils.load("/images/download.png", 20));
        btnReproducir.setIcon(IconUtils.load("/images/play.png", 20));
        btnOpenLibrary.setIcon(IconUtils.load("/images/folder.png", 20));

        lblUrl.setIcon(IconUtils.load("/images/url.png", 20));
        lblSelectFolderDownload.setIcon(IconUtils.load("/images/folder.png", 20));
        btnSearchFolderDownload.setIcon(IconUtils.load("/images/search.png", 20));

        btnDownload.setToolTipText("Start download");
        btnReproducir.setToolTipText("Play last downloaded file");
        btnOpenLibrary.setToolTipText("View all downloaded media details");
        btnSearchFolderDownload.setToolTipText("Choose download folder");
        jtxtInsertUrl.setToolTipText("Paste a media URL to download");
        comboFormat.setToolTipText("Select output format");
        comboQuality.setToolTipText("Select output quality");
        updateDownloadEnabled();
        jtxtInsertUrl.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }
        });

        jtxtFolderDownload.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateDownloadEnabled();
            }
        });

    }

    private void updateDownloadEnabled() {
        String url = jtxtInsertUrl.getText().trim();
        String folder = jtxtFolderDownload.getText().trim();

        boolean ok = !url.isEmpty() && !folder.isEmpty() && new java.io.File(folder).isDirectory();
        btnDownload.setEnabled(ok);

        if (!ok) {
            btnDownload.setToolTipText("Enter URL and select a valid folder to enable download");
        } else {
            btnDownload.setToolTipText("Start download");
        }
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void resetProgress() {
        progressBar.setValue(0);
        progressBar.setString("0%");
    }

    public void setProgressValue(int value) {
        progressBar.setValue(value);
        progressBar.setString(value + "%");
    }

    /**
     * Called when a file is successfully downloaded. Updates log and stores
     * last downloaded file path.
     */
    public void notifyDownloaded(MediaFile mf) {
        lastDownloadedFile = mf.getFile().getAbsolutePath();
        mainFrame.notifyDownloadedMedia(mf);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblUrl = new javax.swing.JLabel();
        btnDownload = new javax.swing.JButton();
        lblQuality = new javax.swing.JLabel();
        btnReproducir = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTxtLog = new javax.swing.JTextArea();
        btnOpenLibrary = new javax.swing.JButton();
        comboFormat = new javax.swing.JComboBox<>();
        comboQuality = new javax.swing.JComboBox<>();
        lblFormatoSalida1 = new javax.swing.JLabel();
        jtxtInsertUrl = new javax.swing.JTextField();
        btnSearchFolderDownload = new javax.swing.JButton();
        jtxtFolderDownload = new javax.swing.JTextField();
        lblSelectFolderDownload = new javax.swing.JLabel();
        jslFormato1 = new javax.swing.JSeparator();
        jfFormato2 = new javax.swing.JSeparator();

        lblUrl.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblUrl.setForeground(new java.awt.Color(6, 6, 69));
        lblUrl.setText("Url");
        add(lblUrl);

        btnDownload.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnDownload.setForeground(new java.awt.Color(6, 6, 69));
        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadActionPerformed(evt);
            }
        });
        add(btnDownload);

        lblQuality.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblQuality.setForeground(new java.awt.Color(6, 6, 69));
        lblQuality.setText("Quality");
        add(lblQuality);

        btnReproducir.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnReproducir.setForeground(new java.awt.Color(6, 6, 69));
        btnReproducir.setText("Play");
        btnReproducir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReproducirActionPerformed(evt);
            }
        });
        add(btnReproducir);

        jTxtLog.setBackground(new java.awt.Color(204, 204, 204));
        jTxtLog.setColumns(20);
        jTxtLog.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jTxtLog.setRows(5);
        jTxtLog.setCaretColor(new java.awt.Color(102, 153, 255));
        jScrollPane1.setViewportView(jTxtLog);

        add(jScrollPane1);

        btnOpenLibrary.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnOpenLibrary.setForeground(new java.awt.Color(6, 6, 69));
        btnOpenLibrary.setText("View all media");
        btnOpenLibrary.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenLibraryActionPerformed(evt);
            }
        });
        add(btnOpenLibrary);

        comboFormat.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        comboFormat.setForeground(new java.awt.Color(6, 6, 69));
        comboFormat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "mp4", "mp3" }));
        comboFormat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboFormatActionPerformed(evt);
            }
        });
        add(comboFormat);

        comboQuality.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        comboQuality.setForeground(new java.awt.Color(6, 6, 69));
        comboQuality.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1080p", "720p", "480p", "360p" }));
        comboQuality.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboQualityActionPerformed(evt);
            }
        });
        add(comboQuality);

        lblFormatoSalida1.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblFormatoSalida1.setForeground(new java.awt.Color(6, 6, 69));
        lblFormatoSalida1.setText("Output Format");
        add(lblFormatoSalida1);

        jtxtInsertUrl.setBackground(new java.awt.Color(204, 204, 204));
        jtxtInsertUrl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtInsertUrlActionPerformed(evt);
            }
        });
        add(jtxtInsertUrl);

        btnSearchFolderDownload.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnSearchFolderDownload.setForeground(new java.awt.Color(6, 6, 69));
        btnSearchFolderDownload.setText("Browse..");
        btnSearchFolderDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchFolderDownloadActionPerformed(evt);
            }
        });
        add(btnSearchFolderDownload);

        jtxtFolderDownload.setBackground(new java.awt.Color(204, 204, 204));
        jtxtFolderDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtFolderDownloadActionPerformed(evt);
            }
        });
        add(jtxtFolderDownload);

        lblSelectFolderDownload.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        lblSelectFolderDownload.setForeground(new java.awt.Color(6, 6, 69));
        lblSelectFolderDownload.setText("Select download folder");
        add(lblSelectFolderDownload);
        add(jslFormato1);
        add(jfFormato2);
    }// </editor-fold>//GEN-END:initComponents
     /**
     * Starts a new download using DownloadTask.
     */
    private void btnDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadActionPerformed
        String downloadFolder = jtxtFolderDownload.getText().trim();
        if (downloadFolder.isBlank() || !new File(downloadFolder).isDirectory()) {
            jTxtLog.append("Select a valid download folder first.\n");
            jtxtFolderDownload.requestFocusInWindow();
            setCursor(java.awt.Cursor.getDefaultCursor());
            return;
        }
        mainFrame.setRutaDescargas(downloadFolder);

        String url = jtxtInsertUrl.getText().trim();
        if (url.isEmpty()) {
            jTxtLog.append("Enter a URL first.\n");
            jtxtInsertUrl.requestFocusInWindow();
            setCursor(java.awt.Cursor.getDefaultCursor());
            return;
        }
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
        jtxtInsertUrl.setEnabled(false);
        comboFormat.setEnabled(false);
        comboQuality.setEnabled(false);
        btnSearchFolderDownload.setEnabled(false);
        btnDownload.setEnabled(false);
        jtxtFolderDownload.setEditable(false);
        btnDownload.setText("Downloading...");
        resetProgress();

        DownloadTask task = new DownloadTask(
                url,
                preferencesPanel,
                jTxtLog,
                comboFormat,
                comboQuality,
                this
        );

        task.addPropertyChangeListener(event -> {
            if ("progress".equals(event.getPropertyName())) {
                int p = (int) event.getNewValue();
                setProgressValue(p);
            }
            if ("state".equals(event.getPropertyName())
                    && javax.swing.SwingWorker.StateValue.DONE == event.getNewValue()) {

                btnDownload.setText("Download");
                setCursor(java.awt.Cursor.getDefaultCursor());

                jtxtInsertUrl.setEnabled(true);
                comboFormat.setEnabled(true);
                comboQuality.setEnabled(true);
                btnSearchFolderDownload.setEnabled(true);
                jtxtFolderDownload.setEditable(true);

                updateDownloadEnabled();
            }
        });
        task.execute();
    }//GEN-LAST:event_btnDownloadActionPerformed
    /**
     * Opens the last downloaded file using the default system application.
     */
    private void btnReproducirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReproducirActionPerformed
        if (lastDownloadedFile == null) {
            jTxtLog.append("Download a file first to play it.\n");
            return;
        }
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(lastDownloadedFile);
            File file = path.toFile();

            if (!file.exists()) {
                jTxtLog.append("Downloaded file not found: " + lastDownloadedFile + "\n");
                return;
            }
            jTxtLog.append("Playing: " + file.getAbsolutePath() + "\n");
            java.awt.Desktop.getDesktop().open(file);

        } catch (Exception e) {
            jTxtLog.append("Error playing file: " + e.getMessage() + "\n");
        }
    }//GEN-LAST:event_btnReproducirActionPerformed
    /**
     * Opens the Library panel to show all downloaded media files.
     */
    private void btnOpenLibraryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenLibraryActionPerformed
        mainFrame.showLibraryPanel();
    }//GEN-LAST:event_btnOpenLibraryActionPerformed

    private void comboQualityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboQualityActionPerformed

    }//GEN-LAST:event_comboQualityActionPerformed

    private void comboFormatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboFormatActionPerformed
        String formato = comboFormat.getSelectedItem().toString();
        comboQuality.removeAllItems();
        if (formato.equalsIgnoreCase("mp4")) {
            comboQuality.addItem("1080p");
            comboQuality.addItem("720p");
            comboQuality.addItem("480p");
            comboQuality.addItem("360p");
        } else if (formato.equalsIgnoreCase("mp3")) {
            comboQuality.addItem("320 kbps");
            comboQuality.addItem("192 kbps");
            comboQuality.addItem("128 kbps");
        }
        updateDownloadEnabled();
    }//GEN-LAST:event_comboFormatActionPerformed
    /**
     * Lets the user choose a folder for saving downloads.
     */
    private void btnSearchFolderDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchFolderDownloadActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String ruta = chooser.getSelectedFile().getAbsolutePath();
            jtxtFolderDownload.setText(ruta);
            mainFrame.setRutaDescargas(ruta);
            updateDownloadEnabled();
        }
    }//GEN-LAST:event_btnSearchFolderDownloadActionPerformed

    private void jtxtFolderDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtFolderDownloadActionPerformed

    }//GEN-LAST:event_jtxtFolderDownloadActionPerformed

    private void jtxtInsertUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtInsertUrlActionPerformed
        updateDownloadEnabled();
        if (btnDownload.isEnabled())
            btnDownload.doClick();
    }//GEN-LAST:event_jtxtInsertUrlActionPerformed

    public javax.swing.JScrollPane getScrollLog() {
        return jScrollPane1;
    }

    public javax.swing.JLabel getLblUrl() {
        return lblUrl;
    }

    public javax.swing.JTextField getTxtUrl() {
        return jtxtInsertUrl;
    }

    public javax.swing.JButton getBtnDownload() {
        return btnDownload;
    }

    public javax.swing.JLabel getLblFormat() {
        return lblFormatoSalida1;
    }

    public javax.swing.JComboBox<String> getComboFormat() {
        return comboFormat;
    }

    public javax.swing.JLabel getLblQuality() {
        return lblQuality;
    }

    public javax.swing.JComboBox<String> getComboQuality() {
        return comboQuality;
    }

    public javax.swing.JLabel getLblFolder() {
        return lblSelectFolderDownload;
    }

    public javax.swing.JTextField getTxtFolder() {
        return jtxtFolderDownload;
    }

    public javax.swing.JButton getBtnBrowseFolder() {
        return btnSearchFolderDownload;
    }

    public javax.swing.JButton getBtnPlay() {
        return btnReproducir;
    }

    public javax.swing.JButton getBtnOpenLibrary() {
        return btnOpenLibrary;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownload;
    private javax.swing.JButton btnOpenLibrary;
    private javax.swing.JButton btnReproducir;
    private javax.swing.JButton btnSearchFolderDownload;
    private javax.swing.JComboBox<String> comboFormat;
    private javax.swing.JComboBox<String> comboQuality;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTxtLog;
    private javax.swing.JSeparator jfFormato2;
    private javax.swing.JSeparator jslFormato1;
    private javax.swing.JTextField jtxtFolderDownload;
    private javax.swing.JTextField jtxtInsertUrl;
    private javax.swing.JLabel lblFormatoSalida1;
    private javax.swing.JLabel lblQuality;
    private javax.swing.JLabel lblSelectFolderDownload;
    private javax.swing.JLabel lblUrl;
    // End of variables declaration//GEN-END:variables
}
