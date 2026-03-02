
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to load application properties from a configuration file.
 * Supports property placeholders like ${user.home}.
 *
 * @author Romina
 * @version 1.0
 */
public final class ConfigProperties {

    private static final Properties props = new Properties();

    /**
     * Utility class. Not meant to be instantiated.
     */
    private ConfigProperties() {
        // Prevent instantiation
    }

    static {
        try (InputStream input =
                     ConfigProperties.class.getResourceAsStream("/properties/application.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Retrieves a property value by key.
     * Replaces ${user.home} with the actual user's home directory if present.
     *
     * @param key property key
     * @return resolved property value, or {@code null} if the key is not found
     */
    public static String get(String key) {
        String value = props.getProperty(key);
        if (value != null && value.contains("${user.home}")) {
            value = value.replace("${user.home}", System.getProperty("user.home"));
        }
        return value;
    }
}