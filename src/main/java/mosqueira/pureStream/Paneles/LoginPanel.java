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
import mosqueira.pureStream.ControladorInterno.ApiClient;
import mosqueira.pureStream.Modelo.Usuari;
import tools.jackson.databind.ObjectMapper;

/**
 * LoginPanel
 *
 * @author Romina
 */
public class LoginPanel extends javax.swing.JPanel {

    // Componentes de login
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRemember;
    private final String API_URL = "https://dimedianetapi9.azurewebsites.net";
   private final File jsonFile;
    
    private final ObjectMapper mapper = new ObjectMapper();
    public LoginListener loginListener;

    public LoginPanel() {
        jsonFile= new File(System.getProperty("user.home") + File.separator + "Downloads" + File.separator + "remember.json");
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
        btnLogin.addActionListener((ActionEvent e) -> Login());
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

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email no válido");
            return;
        }

        try {
            ApiClient api = new ApiClient(API_URL);
            String token = api.login(email, pass);

            JOptionPane.showMessageDialog(this, "Login correcto");

            // Guardar o borrar remember.json
            if (chkRemember.isSelected()) {
                saveRemember(email, pass, token);
            } else {
                deleteRemember();
            }

            // Notificar a la app (opcional)
            if (loginListener != null) {
                Usuari me = api.getMe(token);
                loginListener.onLoginSuccess(token, me);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Login incorrecto: " + ex.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRemember(String email, String password, String token) {
        try {
            RememberData data = new RememberData(email, password, token);
            mapper.writerWithDefaultPrettyPrinter().writeValue(jsonFile, data);
        } catch (Exception ex) {
            System.out.println("No se pudo guardar remember.json");
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
        } catch (Exception ex) {
            System.out.println("No se pudo cargar remember.json");
        }
    }

    public static record RememberData(String email, String password, String token) {

    }

    private void deleteRemember() {
        if (jsonFile.exists()) {
            jsonFile.delete();
        }
    }

    public interface LoginListener {
         void onLoginSuccess(String token, Usuari user);
    }

    public void setLoginListener(LoginListener listener) {
        this.loginListener = listener;
    }

    public JTextField getTxtEmail() {
        return txtEmail;
    }

    public JPasswordField getTxtPassword() {
        return txtPassword;
    }

    public JButton getBtnLogin() {
        return btnLogin;
    }

    public JCheckBox getChkRemember() {
        return chkRemember;
    }
}
