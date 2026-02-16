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
import mosqueira.pureStream.diseñoApp.IconUtils;
import tools.jackson.databind.ObjectMapper;

public class LoginPanel extends javax.swing.JPanel {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRemember;
    private MainFrame main;
    private final File jsonFile;
    private final ObjectMapper mapper = new ObjectMapper();

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

        javax.swing.event.DocumentListener listener
                = new javax.swing.event.DocumentListener() {
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
                txtPassword.setEchoChar(
                        chkShow.isSelected() ? (char) 0 : defaultEcho
                );
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

        // Acción del botón
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

    private void checkFields() {
        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        btnLogin.setEnabled(!email.isEmpty() && !pass.isEmpty());
    }

    private void Login() {

        String email = txtEmail.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields.");
            return;
        }

        btnLogin.setEnabled(false); // evitar doble click
        setCursor(java.awt.Cursor.getPredefinedCursor(
                java.awt.Cursor.WAIT_CURSOR));

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

            JOptionPane.showMessageDialog(this,
                    "Login failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public void tryAutoLogin() {
        try {
            if (!jsonFile.exists()) {
                return; // No hay remember.json 
            }

            RememberData data = mapper.readValue(jsonFile, RememberData.class);

            // Intento de login con email/contraseña guardados
            String token = MainFrame.COMPONENT.login(data.email(), data.password());

            // Guardamos y activamos el componente
            main.setJwtToken(token);
            MainFrame.COMPONENT.setToken(token);
            MainFrame.COMPONENT.setRunning(true);

            // Cambiamos directamente al panel principal
            main.cargarPanelPrincipal();

        } catch (Exception ex) {
            System.out.println("AutoLogin falló: " + ex.getMessage());

        }
    }

    private void saveRemember(String email, String password, String token) {
        try {
            RememberData data = new RememberData(email, password, token);
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, data);
        } catch (Exception ignored) {
        }
    }

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

    private void deleteRemember() {
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    public static record RememberData(String email, String password, String token) {

    }
}
