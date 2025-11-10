package mosqueira.pureStream.ControladorInterno;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to load application properties from a configuration file.
 * Supports property placeholders like ${user.home}.
 * @author Romina
 */
public class ConfigProperties {

    private static final Properties props = new Properties();

    static {
        // Load properties from /properties/application.properties at class load
        try (InputStream input = ConfigProperties.class.getResourceAsStream("/properties/application.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves a property value by key. Replaces ${user.home} with the actual
     * user's home directory if present.
     *
     * @param key property key
     * @return resolved property value or null if key not found
     */
    public static String get(String key) {
        String value = props.getProperty(key);
        if (value != null && value.contains("${user.home}")) {
            value = value.replace("${user.home}", System.getProperty("user.home"));
        }
        return value;
    }
}
