package mosqueira.pureStream.Paneles;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * LoginPanel
 * 
 * @author Romina
 */
public class LoginPanel extends javax.swing.JPanel{

    // Componentes de login
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JCheckBox chkRemember;

    public LoginPanel() {

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

