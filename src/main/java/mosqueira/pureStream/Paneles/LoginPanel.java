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
        // Diseño sin NetBeans Designer
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // -------- EMAIL --------
        JLabel lblEmail = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblEmail, gbc);

        txtEmail = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(txtEmail, gbc);

        // -------- PASSWORD --------
        JLabel lblPassword = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(txtPassword, gbc);

        // -------- REMEMBER ME --------
        chkRemember = new JCheckBox("Remember me");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(chkRemember, gbc);

        // -------- LOGIN BUTTON --------
        btnLogin = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(btnLogin, gbc);

        // Acción del botón
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login();
            }
        });
        // Cargar remember.json si existe
        loadRemember();
    }

    private void Login() {

        String email = txtEmail.getText();
        String pass = new String(txtPassword.getPassword());

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes rellenar todos los campos");
            return;
        }

        try {
            //Login usando solo el componente
            String token = MainFrame.COMPONENT.login(email, pass);
            main.setJwtToken(token);

            //Activar polling
            MainFrame.COMPONENT.setToken(token);
            MainFrame.COMPONENT.setRunning(true);

            //Cambiar a pantalla principal
            main.cargarPanelPrincipal();

            if (chkRemember.isSelected()) {
                saveRemember(email, pass, token);
            } else {
                deleteRemember();
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Login incorrecto: " + ex.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void tryAutoLogin() {
        try {
            if (!jsonFile.exists()) {
                return; // No hay remember.json → no hacer nada
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
            // NO borramos remember.json; solo mostramos login normal
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
