package mosqueira.pureStream.Paneles;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.DesignApp.IconUtils;
import tools.jackson.databind.ObjectMapper;

/**
 * Login screen panel for PureStream.
 *
 * <p>
 * This panel allows the user to authenticate against the remote service,
 * optionally persist credentials/token locally ("Remember me"), and navigate to
 * the main application screen on success.</p>
 *
 * <p>
 * It also supports an automatic login attempt using previously saved data.</p>
 *
 * @author Romina
 * @version 1.0
 */
public class LoginPanel extends javax.swing.JPanel {

    /**
     * Text field where the user enters the email address.
     */
    private JTextField txtEmail;

    /**
     * Password input field for the user credentials.
     */
    private JPasswordField txtPassword;

    /**
     * Button that triggers the login action.
     */
    private JButton btnLogin;

    /**
     * Checkbox that enables persisting session data on this device.
     */
    private JCheckBox chkRemember;

    /**
     * Reference to the main application frame (used for navigation and token
     * storage).
     */
    private MainFrame main;

    /**
     * JSON file used to persist remembered credentials/token.
     */
    private final File jsonFile;

    /**
     * JSON serializer/deserializer for {@link RememberData}.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates the login panel and initializes the UI components and listeners.
     *
     * <p>
     * The constructor loads any previously stored "remember me" data and sets
     * the login button as the default button of the root pane.</p>
     *
     * @param main the main frame used to store the JWT token and navigate to
     * the main panel
     */
    public LoginPanel(MainFrame main) {
        this.main = main;
        jsonFile = new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "remember.json");
        setOpaque(false);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(JLabel.CENTER);
        lblLogo.setIcon(IconUtils.load("/images/iconApp.png", 100));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblLogo, gbc);
        gbc.gridwidth = 1;

        // -------- EMAIL --------
        JLabel lblEmail = new JLabel();
        lblEmail.setIcon(IconUtils.load("/images/email.png", 20));
        lblEmail.setIconTextGap(10);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblEmail, gbc);

        txtEmail = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtEmail, gbc);

        // -------- PASSWORD --------
        JLabel lblPassword = new JLabel();
        lblPassword.setIcon(IconUtils.load("/images/password.png", 20));
        lblPassword.setIconTextGap(10);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(txtPassword, gbc);
        final char defaultEcho = txtPassword.getEchoChar();

        javax.swing.event.DocumentListener listener = new javax.swing.event.DocumentListener() {
            private void update() {
                checkFields();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        };

        txtEmail.getDocument().addDocumentListener(listener);
        txtPassword.getDocument().addDocumentListener(listener);

        JCheckBox chkShow = new JCheckBox("Show password");
        chkShow.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 14));
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(chkShow, gbc);

        chkShow.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtPassword.setEchoChar(chkShow.isSelected() ? (char) 0 : defaultEcho);
            }
        });

        // -------- REMEMBER ME --------
        chkRemember = new JCheckBox("Remember me");
        chkRemember.setFont(new java.awt.Font("Serif", java.awt.Font.BOLD, 14));
        chkRemember.setToolTipText("Keep session data on this device");
        gbc.gridx = 1;
        gbc.gridy = 4;
        add(chkRemember, gbc);

        // -------- LOGIN BUTTON --------
        btnLogin = new JButton("Login");
        btnLogin.setIcon(IconUtils.load("/images/login.png", 20));
        btnLogin.setToolTipText("Sign in to start using PureStream");
        gbc.gridx = 1;
        gbc.gridy = 5;
        add(btnLogin, gbc);

        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login();
            }
        });

        loadRemember();
        checkFields();
        main.getRootPane().setDefaultButton(btnLogin);
    }

    /**
     * Checks whether the required fields are filled and updates the Login
     * button state.
     */
    private void checkFields() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        btnLogin.setEnabled(!email.isEmpty() && !pass.isEmpty());
    }

    /**
     * Performs a login request using the entered email and password.
     *
     * <p>
     * On success, the JWT token is stored in the {@link MainFrame} and the
     * application navigates to the main panel. If "Remember me" is enabled,
     * credentials/token are saved locally.</p>
     */
    private void Login() {

        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        btnLogin.setEnabled(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        try {
            String token = MainFrame.COMPONENT.login(email, pass);

            setCursor(java.awt.Cursor.getDefaultCursor());
            btnLogin.setEnabled(true);

            main.setJwtToken(token);
            MainFrame.COMPONENT.setToken(token);
            MainFrame.COMPONENT.setRunning(true);

            main.cargarPanelPrincipal();

            if (chkRemember.isSelected()) {
                saveRemember(email, pass, token);
            } else {
                deleteRemember();
            }

        } catch (Exception ex) {
            btnLogin.setEnabled(true);
            setCursor(java.awt.Cursor.getDefaultCursor());

            JOptionPane.showMessageDialog(
                    this,
                    "Login failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Attempts to authenticate automatically using persisted "remember me"
     * data.
     *
     * <p>
     * If {@code remember.json} does not exist or the stored credentials are
     * invalid, this method returns silently.</p>
     */
    public void tryAutoLogin() {
        try {
            if (!jsonFile.exists()) {
                return;
            }

            RememberData data = mapper.readValue(jsonFile, RememberData.class);
            String token = MainFrame.COMPONENT.login(data.email(), data.password());

            main.setJwtToken(token);
            MainFrame.COMPONENT.setToken(token);
            MainFrame.COMPONENT.setRunning(true);

            main.cargarPanelPrincipal();

        } catch (Exception ex) {
            System.out.println("AutoLogin failed: " + ex.getMessage());
        }
    }

    /**
     * Persists the provided login/session data as JSON in
     * {@code remember.json}.
     *
     * @param email user email
     * @param password user password
     * @param token JWT token returned by the server
     */
    private void saveRemember(String email, String password, String token) {
        try {
            RememberData data = new RememberData(email, password, token);
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, data);
        } catch (Exception ignored) {
        }
    }

    /**
     * Loads persisted "remember me" data (if present) and pre-fills the UI
     * fields.
     */
    private void loadRemember() {
        try {
            if (!jsonFile.exists()) {
                return;
            }

            RememberData data = mapper.readValue(jsonFile, RememberData.class);
            txtEmail.setText(data.email());
            txtPassword.setText(data.password());
            chkRemember.setSelected(true);

        } catch (Exception ignored) {
        }
    }

    /**
     * Deletes the persisted "remember me" file from disk, if it exists.
     */
    private void deleteRemember() {
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    /**
     * DTO used to persist "remember me" session data locally.
     *
     * @param email user email
     * @param password user password (stored locally)
     * @param token JWT token
     */
    public static record RememberData(String email, String password, String token) {

    }
}
