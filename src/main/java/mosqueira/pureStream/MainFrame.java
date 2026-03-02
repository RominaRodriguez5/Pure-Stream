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
 * Main application window for PureStream.
 *
 * <p>
 * This frame is responsible for:</p>
 * <ul>
 * <li>Initializing the main UI panels (login, main downloader, preferences,
 * library).</li>
 * <li>Managing navigation between panels inside the root container.</li>
 * <li>Holding user preferences used by the download engine (yt-dlp path, speed
 * limit, playlist creation).</li>
 * <li>Coordinating cloud session state (JWT token, logged user) and media
 * polling events.</li>
 * </ul>
 *
 * @author Lulas
 * @version 1.0
 */
public class MainFrame extends javax.swing.JFrame {
    /**
     * Logger used to record application events and exceptions.
     */
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());

    // Reference to the main download panel
    private MainPanel mainPanel;

    // Reference to the preferences panel (for user configuration options)
    private PreferencesPanel preferencesPanel;

    // Reference to the library panel (used to display downloaded media)
    private LibraryPanel panelLibrary;

    private LoginPanel loginPanel;

    /**
     * Default download folder path.
     */
    private String rutaDescargas = System.getProperty("user.home") + File.separator + "Downloads";

    /**
     * Shared table model used by panels that display downloaded media.
     */
    private MediaTableModel mediaTableModel;

    /**
     * JWT token of the current cloud session (null when not logged in).
     */
    private String jwtToken = null;
    /**
     * Logged user information returned by the cloud service (null when not
     * logged in).
     */
    private Usuari loggedUser = null;
    /**
     * Media polling component used to synchronize with the remote library.
     */
    public static MediaPollingClientComponent COMPONENT;
    /**
     * If true, the application appends downloaded files to an M3U playlist.
     */
    private boolean crearM3U = false;
    /**
     * If true, download rate limiting is enabled.
     */
    private boolean limitarVelocidad;
    /**
     * Download speed limit in KB/s when rate limiting is enabled.
     */
    private int limiteVelocidad = 0;
    /**
     * Path to the yt-dlp executable configured by the user.
     */
    private String executablePath = "";
    /** Background container that paints the gradient theme and wraps the root panel. */
    private PanelUtils bg;
    /**
     * Creates the main application frame and initializes UI components.
     *
     * <p>
     * This constructor:
     * <ul>
     * <li>Initializes Swing components and icons.</li>
     * <li>Prepares the root container and background panel.</li>
     * <li>Starts in the login panel and attempts auto-login.</li>
     * </ul>
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

    /**
     * Replaces the content of the root container with the given panel.
     *
     * @param panel panel to display in the main window
     */
    public void showPanel(JPanel panel) {
        pnlRoot.setOpaque(false);
        pnlRoot.removeAll();
        pnlRoot.add(panel, BorderLayout.CENTER);
        pnlRoot.revalidate();
        pnlRoot.repaint();
    }

    /**
     * Displays the main download panel.
     */
    public void showMain() {
        showPanel(mainPanel);
    }

    /**
     * Displays the preferences panel.
     */
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

    /**
     * Initializes the main panels and shared model after a successful login.
     *
     * <p>
     * This method creates the {@link MediaTableModel} and the panels that
     * depend on it, then registers a polling listener to refresh remote media
     * when changes are detected.</p>
     */
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
     * Displays the library panel containing downloaded and/or cloud media.
     *
     * <p>
     * Before showing it, the method attempts to refresh remote media.</p>
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
     *
     * @param ruta absolute folder path for downloads
     */
    public void setRutaDescargas(String ruta) {
        this.rutaDescargas = ruta;
    }

    /**
     * Returns the current download directory path.
     *
     * @return download folder path
     */
    public String getRutaDescargas() {
        return rutaDescargas;
    }

    /**
     * Returns the library panel instance.
     *
     * @return library panel
     */
    public LibraryPanel getLibraryPanel() {
        return panelLibrary;
    }

    /**
     * Returns the preferences panel instance.
     *
     * @return preferences panel
     */
    public PreferencesPanel getPreferencesPanel() {
        return preferencesPanel;
    }

    /**
     * Enables or disables automatic M3U playlist creation.
     *
     * @param crear true to create/update an M3U playlist after downloads
     */
    public void setCrearM3U(boolean crear) {
        this.crearM3U = crear;
    }

    /**
     * Indicates whether the application creates M3U playlists after downloads.
     *
     * @return true if M3U playlist creation is enabled
     */
    public boolean isCrearM3U() {
        return crearM3U;
    }

    /**
     * Indicates whether download speed limiting is enabled.
     *
     * @return true if download speed is limited
     */
    public boolean isLimitarVelocidad() {
        return limitarVelocidad;
    }

    /**
     * Enables or disables download speed limitation.
     *
     * @param limitar true to enable rate limiting
     */
    public void setLimitarVelocidad(boolean limitar) {
        this.limitarVelocidad = limitar;
    }

    /**
     * Returns the current download speed limit in KB/s.
     *
     * @return speed limit in KB/s
     */
    public int getLimiteVelocidad() {
        return limiteVelocidad;
    }

    /**
     * Sets the maximum allowed download speed in KB/s.
     *
     * @param limite speed limit in KB/s
     */
    public void setLimiteVelocidad(int limite) {
        this.limiteVelocidad = limite;
    }

    /**
     * Sets the path to the yt-dlp executable used to perform downloads.
     *
     * @param path absolute path to the yt-dlp executable
     */
    public void setExecutablePath(String path) {
        this.executablePath = path;
    }

    /**
     * Returns the currently configured yt-dlp executable path.
     *
     * @return yt-dlp executable path
     */
    public String getExecutablePath() {
        return executablePath;
    }

    /**
     * Stores the JWT token for the current session.
     *
     * @param token JWT token returned by the cloud service
     */
    public void setJwtToken(String token) {
        this.jwtToken = token;
    }

    /**
     * Sets the current logged user information.
     *
     * @param user logged user data
     */
    public void setLoggedUser(Usuari user) {
        this.loggedUser = user;
    }

    /**
     * Returns the JWT token for the current session.
     *
     * @return JWT token, or {@code null} if not logged in
     */
    public String getJwtToken() {
        return jwtToken;
    }

    /**
     * Notifies the application that a new media file has been downloaded.
     *
     * <p>
     * The file is added to the library UI and, if enabled, appended to an M3U
     * playlist in the download folder.</p>
     *
     * @param media downloaded media file
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
     * Main entry point.
     *
     * @param args command line arguments
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
