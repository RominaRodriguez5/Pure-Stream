package mosqueira.pureStream.ControladorInterno;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppPaths {

    private static final String APP_SUPPORT_DIR = "PureStream";
    

    private AppPaths() {
    }

    public static Path getAppBaseDir(Class<?> clazz) {
        try {
            Path location = Paths.get(
                    clazz.getProtectionDomain()
                            .getCodeSource()
                            .getLocation()
                            .toURI()
            );

            if (Files.isRegularFile(location)) {
                return location.getParent();
            }
            return location;
        } catch (Exception ex) {
            return Paths.get(System.getProperty("user.dir"));
        }
    }

    public static Path findBundledTool(Class<?> clazz, String fileName) {
        Path appBaseDir = getAppBaseDir(clazz);
        Path userDir = Paths.get(System.getProperty("user.dir"));

        Path[] candidates = new Path[]{
            appBaseDir.resolve("tools").resolve(fileName), // instalación final
            userDir.resolve("tools").resolve(fileName) // proyecto en desarrollo
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isRegularFile(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }

        return null;
    }

    public static Path findBundledYtDlp(Class<?> clazz) {
        return findBundledTool(clazz, "yt-dlp.exe");
    }

    public static Path findBundledFfmpeg(Class<?> clazz) {
        return findBundledTool(clazz, "ffmpeg.exe");
    }

    public static Path findBundledFfprobe(Class<?> clazz) {
        return findBundledTool(clazz, "ffprobe.exe");
    }

    public static Path getBundledToolsDirectory(Class<?> clazz) {
        Path appBaseDir = getAppBaseDir(clazz);
        Path userDir = Paths.get(System.getProperty("user.dir"));

        Path[] candidates = new Path[]{
            appBaseDir.resolve("tools"),
            userDir.resolve("tools")
        };

        for (Path candidate : candidates) {
            if (Files.exists(candidate) && Files.isDirectory(candidate)) {
                return candidate.toAbsolutePath().normalize();
            }
        }

        return null;
    }

    public static Path getInstalledHelpBaseDir() {
        String localAppData = System.getenv("LOCALAPPDATA");

        if (localAppData != null && !localAppData.isBlank()) {
            return Paths.get(localAppData, APP_SUPPORT_DIR);
        }

        return Paths.get(System.getProperty("user.home"), APP_SUPPORT_DIR);
    }

    public static Path findApiDocs() {
        Path installed = getInstalledHelpBaseDir()
                .resolve("apidocs")
                .resolve("index.html");

        if (Files.exists(installed)) {
            return installed;
        }

        Path dev = Paths.get("doc", "apidocs", "index.html");
        if (Files.exists(dev)) {
            return dev.toAbsolutePath().normalize();
        }

        return null;
    }

    public static Path findUserManual() {
        Path installed = getInstalledHelpBaseDir()
                .resolve("UserManual.pdf");

        if (Files.exists(installed)) {
            return installed;
        }

        Path dev = Paths.get("doc", "manual", "UserManual.pdf");
        if (Files.exists(dev)) {
            return dev.toAbsolutePath().normalize();
        }

        return null;
    }
}
