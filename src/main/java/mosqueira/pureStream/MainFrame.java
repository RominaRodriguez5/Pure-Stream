package mosqueira.pureStream;

import java.io.File;
import javax.swing.JOptionPane;
import mosqueira.pureStream.Dialogs.AboutDialog;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Modelo.MediaTableModel;
import mosqueira.pureStream.Paneles.LibraryPanel;
import mosqueira.pureStream.Paneles.PanelPrincipal;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * MainFrame represents the main application window for PureStream. It controls
 * navigation between different panels (Main, Preferences, and Library),
 * initializes the menu bar, and handles key user actions such as exiting,
 * opening preferences, and showing information about the app.
 *
 * This class serves as the central hub of the graphical user interface.
 *
 * @author Romina
 */
public class MainFrame extends javax.swing.JFrame {

    // Logger used for tracking general application events and errors
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    // Reference to the main download panel
    private PanelPrincipal panelPrincipal;

    // Reference to the preferences panel (for user configuration options)
    private PreferencesPanel preferencesPanel;

    // Reference to the library panel (used to display downloaded media)
    private LibraryPanel panelLibrary;

    // Default path for downloaded files
    private String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";

    // Shared data model used to synchronize the main panel and the library panel
    private MediaTableModel mediaTableModel;

    

    // User preferences
    private boolean crearM3U = false; // Whether to create an M3U playlist after downloads
    private boolean limitarVelocidad; // Whether to limit download speed
    private int limiteVelocidad = 0; // Speed limit in KB/s
    private String executablePath = ""; // Path to the yt-dlp executable

    /**
     * Constructor that initializes the main frame, all panels, and the menu
     * bar. It also sets up the general window configuration (title, size,
     * position, etc.)
     */
    public MainFrame() {
        initComponents();
        setTitle("YT Downloader");
        setResizable(false);
        setSize(800, 800);
        setLocationRelativeTo(null);

        // Initialize shared model and all panels
        mediaTableModel = new MediaTableModel();
        preferencesPanel = new PreferencesPanel(this);
        panelPrincipal = new PanelPrincipal(preferencesPanel, this, mediaTableModel);
        panelLibrary = new LibraryPanel(this, mediaTableModel);

        // Display the main panel by default
        setContentPane(panelPrincipal);
    }

    /**
     * Displays the main download panel (PanelPrincipal).
     */
    public void mostrarPanelPrincipal() {
        setContentPane(panelPrincipal);
        revalidate();
        repaint();
    }

    /**
     * Displays the user preferences panel (PreferencesPanel).
     */
    private void mostrarPanelPreferencias() {
        setContentPane(preferencesPanel);
        revalidate();
        repaint();
    }

    /**
     * Displays the library panel that shows all downloaded media files. This
     * method updates the LibraryPanel before showing it.
     */
    public void showLibraryPanel() {
        setContentPane(panelLibrary);
        revalidate();
        repaint();
    }

    /**
     * Sets the directory where downloaded files will be saved.
     */
    public void setRutaDescargas(String ruta) {
        this.rutaDescargas = ruta;
    }

    /**
     * Returns the current download directory path.
     */
    public String getRutaDescargas() {
        return rutaDescargas;
    }

    /**
     * Provides access to the LibraryPanel instance.
     */
    public LibraryPanel getLibraryPanel() {
        return panelLibrary;
    }

    /**
     * Enables or disables automatic M3U playlist creation.
     */
    public void setCrearM3U(boolean crear) {
        this.crearM3U = crear;
    }

    /**
     * Returns whether the app should create M3U playlists.
     */
    public boolean isCrearM3U() {
        return crearM3U;
    }

    /**
     * Returns whether download speed limiting is enabled.
     */
    public boolean isLimitarVelocidad() {
        return limitarVelocidad;
    }

    /**
     * Enables or disables speed limitation for downloads.
     */
    public void setLimitarVelocidad(boolean limitar) {
        this.limitarVelocidad = limitar;
    }

    /**
     * Returns the current download speed limit in KB/s.
     */
    public int getLimiteVelocidad() {
        return limiteVelocidad;
    }

    /**
     * Sets the maximum allowed download speed (in KB/s).
     */
    public void setLimiteVelocidad(int limite) {
        this.limiteVelocidad = limite;
    }

    /**
     * Sets the system path to the yt-dlp executable used for downloads.
     */
    public void setExecutablePath(String path) {
        this.executablePath = path;
    }

    /**
     * Returns the currently configured yt-dlp executable path.
     */
    public String getExecutablePath() {
        return executablePath;
    }
    /**
     * Notifies the application that a new media file has been downloaded.
     * Updates the library panel and optionally appends the file to an M3U playlist.
     *
     * @param media the downloaded MediaFile
     */
    public void notifyDownloadedMedia(MediaFile media) {

        // Añadir a LibraryPanel y a la tabla
        panelLibrary.addMediaFile(media);

        // Crear playlist M3U si es necesario
        if (crearM3U) {
            try {
                File m3u = new File(rutaDescargas, "playlist.m3u");

                if (!m3u.exists()) {
                    java.nio.file.Files.writeString(
                            m3u.toPath(),
                            "#EXTM3U\n",
                            java.nio.charset.StandardCharsets.UTF_8
                    );
                }

                java.nio.file.Files.writeString(
                        m3u.toPath(),
                        media.getFile().getAbsolutePath() + "\n",
                        java.nio.charset.StandardCharsets.UTF_8,
                        java.nio.file.StandardOpenOption.APPEND
                );

            } catch (Exception e) {
                System.err.println("Error al actualizar playlist M3U: " + e.getMessage());
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jmnMenu = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        itemExit = new javax.swing.JMenuItem();
        menuEdit = new javax.swing.JMenu();
        itemPreferences = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        itemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PureStream");
        setResizable(false);
        setSize(new java.awt.Dimension(800, 800));
        getContentPane().setLayout(null);

        jmnMenu.setBorder(null);
        jmnMenu.setForeground(new java.awt.Color(0, 102, 153));

        menuFile.setText("File");
        menuFile.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N

        itemExit.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        itemExit.setText("Exit");
        itemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemExitActionPerformed(evt);
            }
        });
        menuFile.add(itemExit);

        jmnMenu.add(menuFile);

        menuEdit.setText("Edit");
        menuEdit.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N

        itemPreferences.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        itemPreferences.setText("Preferences");
        itemPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemPreferencesActionPerformed(evt);
            }
        });
        menuEdit.add(itemPreferences);

        jmnMenu.add(menuEdit);

        menuHelp.setText("Help");
        menuHelp.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N

        itemAbout.setFont(new java.awt.Font("Segoe UI Light", 1, 12)); // NOI18N
        itemAbout.setText("About");
        itemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                itemAboutActionPerformed(evt);
            }
        });
        menuHelp.add(itemAbout);

        jmnMenu.add(menuHelp);

        setJMenuBar(jmnMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles the "Exit" menu action. Saves the current library before closing
     * the app, and asks the user for confirmation before exiting.
     */
    private void itemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemExitActionPerformed

        // Confirmamos salida
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres salir?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_itemExitActionPerformed
    /**
     * Handles the "About" menu item action. Opens a dialog displaying
     * application information.
     */
    private void itemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemAboutActionPerformed
        AboutDialog aboutD = new AboutDialog(this, true);
        aboutD.setLocationRelativeTo(this);
        aboutD.setVisible(true);
    }//GEN-LAST:event_itemAboutActionPerformed
    /**
     * Handles the "Preferences" menu item action. Opens the PreferencesPanel to
     * let the user modify settings.
     */
    private void itemPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_itemPreferencesActionPerformed
        mostrarPanelPreferencias();

    }//GEN-LAST:event_itemPreferencesActionPerformed

    /**
     * Main entry point of the application. Initializes the UI and sets the
     * Nimbus Look and Feel if available.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem itemAbout;
    private javax.swing.JMenuItem itemExit;
    private javax.swing.JMenuItem itemPreferences;
    private javax.swing.JMenuBar jmnMenu;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    // End of variables declaration//GEN-END:variables
}
