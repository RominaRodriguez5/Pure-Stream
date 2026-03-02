package mosqueira.pureStream.ControladorInterno;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to validate M3U playlists and extract valid HTTP/HTTPS URLs.
 *
 * @author Romina
 * @version 1.0
 */
public final class PlaylistValidator {

    /**
     * Utility class. Not meant to be instantiated.
     */
    private PlaylistValidator() {
        // Prevent instantiation
    }

    /**
     * Reads an M3U file and returns a list of valid HTTP/HTTPS URLs.
     *
     * @param m3uFilePath full path to the .m3u file
     * @return list of valid URLs (empty if file not found or invalid)
     */
    public static List<String> validateM3U(String m3uFilePath) {

        List<String> urls = new ArrayList<>();

        File m3uFile = new File(m3uFilePath);
        if (!m3uFile.exists()) {
            System.out.println("M3U file not found: " + m3uFilePath);
            return urls;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(m3uFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("http://") || line.startsWith("https://")) {
                    urls.add(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading M3U file: " + e.getMessage());
        }

        return urls;
    }
}