package mosqueira.pureStream;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import mosqueira.mediaPollingClientComponent.component.MediaPollingClientComponent;
import mosqueira.mediaPollingClientComponent.component.MediaPollingClientEvent;
import mosqueira.mediaPollingClientComponent.component.MediaPollingClientListener;
import mosqueira.mediaPollingClientComponent.model.Usuari;
import mosqueira.pureStream.Dialogs.AboutDialog;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Modelo.MediaTableModel;
import mosqueira.pureStream.Paneles.LibraryPanel;
import mosqueira.pureStream.Paneles.LoginPanel;
import mosqueira.pureStream.Paneles.PreferencesPanel;
import mosqueira.pureStream.DesignApp.IconUtils;
import mosqueira.pureStream.DesignApp.PanelUtils;
import mosqueira.pureStream.Paneles.MainPanel;

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
    private MainPanel mainPanel;

    // Reference to the preferences panel (for user configuration options)
    private PreferencesPanel preferencesPanel;

    // Reference to the library panel (used to display downloaded media)
    private LibraryPanel panelLibrary;

    private LoginPanel loginPanel;

    // Default path for downloaded files
    private String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";

    // Shared data model used to synchronize the main panel and the library panel
    private MediaTableModel mediaTableModel;

    // Cloud login data
    private String jwtToken = null;

    private Usuari loggedUser = null;

    public static MediaPollingClientComponent COMPONENT;
    // User preferences
    private boolean crearM3U = false; // Whether to create an M3U playlist after downloads
    private boolean limitarVelocidad; // Whether to limit download speed
    private int limiteVelocidad = 0; // Speed limit in KB/s
    private String executablePath = ""; // Path to the yt-dlp executable

    private PanelUtils bg;

    /**
     * Constructor that initializes the main frame, all panels, and the menu
     * bar. It also sets up the general window configuration (title, size,
     * position, etc.)
     */
    public MainFrame() {
        initComponents();
        applyIcons();
        IconUtils.applyFrameIcon(this, "/images/iconApp.png", 32);
        setSize(800, 900);

        setLocationRelativeTo(null);

        bg = new PanelUtils();
        bg.setLayout(new BorderLayout());
        bg.add(pnlRoot, BorderLayout.CENTER);

        setContentPane(bg);
        COMPONENT = mediaComponent1;
        mediaComponent1.setVisible(false);
        loginPanel = new LoginPanel(this);
        showPanel(loginPanel);
        loginPanel.tryAutoLogin();

    }

    public void showPanel(JPanel panel) {
        pnlRoot.setOpaque(false);
        pnlRoot.removeAll();
        pnlRoot.add(panel, BorderLayout.CENTER);
        pnlRoot.revalidate();
        pnlRoot.repaint();
    }

    public void showMain() {
        showPanel(mainPanel);
    }

    public void showPreferences() {
        showPanel(preferencesPanel);
    }

    private void applyIcons() {

        javax.swing.JLabel lblMenuIcon = new javax.swing.JLabel(
                mosqueira.pureStream.DesignApp.IconUtils.load("/images/menu.png", 20)
        );
        lblMenuIcon.setToolTipText("Menu");
        lblMenuIcon.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 8, 0, 8));
        jmnMenu.add(lblMenuIcon, 0);

        // JMenuItem icons
        mniExit.setIcon(IconUtils.load("/images/exit.png", 20));
        mniLogout.setIcon(IconUtils.load("/images/logout.png", 20));
        mniPreferences.setIcon(IconUtils.load("/images/edit.png", 20));
        mniAbout.setIcon(IconUtils.load("/images/about.png", 20));

        // Tooltips
        mniExit.setToolTipText("Close the application");
        mniLogout.setToolTipText("Log out of the current session");
        mnuFile.setToolTipText("Close the application and Log out of the current session");
        mnuEdit.setToolTipText("Open application preferences");
        mnuHelp.setToolTipText("About PureStream");
    }

    public void cargarPanelPrincipal() {
        // Initialize shared model and all panels
        mediaTableModel = new MediaTableModel();

        preferencesPanel = new PreferencesPanel(this);
        mainPanel = new MainPanel(preferencesPanel, this, mediaTableModel);
        panelLibrary = new LibraryPanel(this, mediaTableModel);

        COMPONENT.addMediaPollingListener(new MediaPollingClientListener() {
            @Override
            public void onNewMediaDetected(MediaPollingClientEvent event) {
                try {
                    panelLibrary.loadNetworkMedia();
                } catch (Exception ex) {
                    System.getLogger(MainFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }

            }
        });

        showPanel(mainPanel);

    }

    /**
     * Displays the library panel that shows all downloaded media files. This
     * method updates the LibraryPanel before showing it.
     */
    public void showLibraryPanel() {
        panelLibrary.setOpaque(false);
        try {

            panelLibrary.loadNetworkMedia();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showPanel(panelLibrary);
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

    public PreferencesPanel getPreferencesPanel() {
        return preferencesPanel;
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

    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    public void setLoggedUser(Usuari user) {
        this.loggedUser = user;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Notifies the application that a new media file has been downloaded.
     * Updates the library panel and optionally appends the file to an M3U
     * playlist.
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

        mediaComponent1 = new mosqueira.mediaPollingClientComponent.component.MediaPollingClientComponent();
        pnlRoot = new javax.swing.JPanel();
        jmnMenu = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mniExit = new javax.swing.JMenuItem();
        mniLogout = new javax.swing.JMenuItem();
        mnuEdit = new javax.swing.JMenu();
        mniPreferences = new javax.swing.JMenuItem();
        mnuHelp = new javax.swing.JMenu();
        mniAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PureStream ");
        setName("mainFrame"); // NOI18N
        setSize(new java.awt.Dimension(800, 800));

        mediaComponent1.setApiUrl("https://difreenet9.azurewebsites.net/");
        mediaComponent1.setPollingInterval(10);
        mediaComponent1.setRunning(true);
        getContentPane().add(mediaComponent1, java.awt.BorderLayout.CENTER);

        pnlRoot.setLayout(new java.awt.BorderLayout());
        getContentPane().add(pnlRoot, java.awt.BorderLayout.PAGE_START);

        jmnMenu.setBorder(null);
        jmnMenu.setForeground(new java.awt.Color(0, 0, 102));

        mnuFile.setForeground(new java.awt.Color(0, 51, 102));
        mnuFile.setText("File");
        mnuFile.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N

        mniExit.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        mniExit.setText("Exit");
        mniExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniExitActionPerformed(evt);
            }
        });
        mnuFile.add(mniExit);

        mniLogout.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        mniLogout.setText("Logout");
        mniLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniLogoutActionPerformed(evt);
            }
        });
        mnuFile.add(mniLogout);

        jmnMenu.add(mnuFile);

        mnuEdit.setForeground(new java.awt.Color(0, 51, 102));
        mnuEdit.setText("Edit");
        mnuEdit.setToolTipText("");
        mnuEdit.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N

        mniPreferences.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        mniPreferences.setText("Preferences");
        mniPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniPreferencesActionPerformed(evt);
            }
        });
        mnuEdit.add(mniPreferences);

        jmnMenu.add(mnuEdit);

        mnuHelp.setForeground(new java.awt.Color(0, 51, 102));
        mnuHelp.setText("Help");
        mnuHelp.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N

        mniAbout.setFont(new java.awt.Font("Serif", 1, 14)); // NOI18N
        mniAbout.setText("About");
        mniAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mniAboutActionPerformed(evt);
            }
        });
        mnuHelp.add(mniAbout);

        jmnMenu.add(mnuHelp);

        setJMenuBar(jmnMenu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Handles the "Exit" menu action. Saves the current library before closing
     * the app, and asks the user for confirmation before exiting.
     */
    private void mniExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniExitActionPerformed

        // Confirmamos salida
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?",
                "Exit",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }//GEN-LAST:event_mniExitActionPerformed
    /**
     * Handles the "About" menu item action. Opens a dialog displaying
     * application information.
     */
    private void mniAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniAboutActionPerformed
        AboutDialog aboutD = new AboutDialog(this, true);
        aboutD.setLocationRelativeTo(this);
        aboutD.setVisible(true);
    }//GEN-LAST:event_mniAboutActionPerformed
    /**
     * Handles the "Preferences" menu item action. Opens the PreferencesPanel to
     * let the user modify settings.
     */
    private void mniPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniPreferencesActionPerformed
        if (jwtToken == null || preferencesPanel == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please login first to access Preferences.",
                    "Login required",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        preferencesPanel.setOpaque(false);
        showPanel(preferencesPanel);

    }//GEN-LAST:event_mniPreferencesActionPerformed

    private void mniLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mniLogoutActionPerformed
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        this.jwtToken = null;
        this.loggedUser = null;

        COMPONENT.setRunning(false);
        COMPONENT.setToken(null);

        File jsonFile = new File(System.getProperty("user.home")
                + File.separator + "Downloads"
                + File.separator + "remember.json");
        if (jsonFile.exists()) {
            jsonFile.delete();
        }

        loginPanel = new LoginPanel(this);
        loginPanel.setOpaque(false);
        showPanel(loginPanel);
    }//GEN-LAST:event_mniLogoutActionPerformed

    /**
     * Main entry point of the application. Initializes the UI and sets the
     * Nimbus Look and Feel if available.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            try {
                File logFile = new File("crash.log");
                try (PrintWriter out = new PrintWriter(new FileWriter(logFile, true))) {
                    out.println("==== APPLICATION CRASH ====");
                    out.println(new java.util.Date());
                    throwable.printStackTrace(out);
                    out.println();
                }
            } catch (Exception ignored) {
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Unexpected error occurred. See crash.log",
                    "Application Error",
                    JOptionPane.ERROR_MESSAGE
            );
        });

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
    private javax.swing.JMenuBar jmnMenu;
    private mosqueira.mediaPollingClientComponent.component.MediaPollingClientComponent mediaComponent1;
    private javax.swing.JMenuItem mniAbout;
    private javax.swing.JMenuItem mniExit;
    private javax.swing.JMenuItem mniLogout;
    private javax.swing.JMenuItem mniPreferences;
    private javax.swing.JMenu mnuEdit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenu mnuHelp;
    private javax.swing.JPanel pnlRoot;
    // End of variables declaration//GEN-END:variables
}
