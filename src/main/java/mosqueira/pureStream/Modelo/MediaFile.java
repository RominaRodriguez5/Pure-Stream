package mosqueira.pureStream.Modelo;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a downloaded media file with its metadata. This class stores
 * information about a specific file, including its name, size, MIME type, path,
 * and download date. It is mainly used to populate tables or lists of
 * downloaded media within the PureStream application.
 *
 * @author Romina
 *
 */
public class MediaFile implements Serializable {

    /**
     * The name of the media file (e.g., video.mp4).
     */
    private String fileName;

    /**
     * The MIME type of the file (e.g., video/mp4, audio/mpeg).
     */
    private String mimeType;

    /**
     * The absolute file path on disk.
     */
    private String filePath;

    /**
     * The file size in bytes.
     */
    private long fileSizeBytes;

    /**
     * The date when the file was downloaded or last modified.
     */
    private Date dateDownloaded;

    /**
     * Constructs a new MediaFile object using the provided File.
     * <p>
     * It extracts metadata such as name, size, MIME type, and last modification
     * date from the file system.
     * </p>
     *
     * @param file the File object representing the media file.
     */
    public MediaFile(File file, Date downloadDate) {
        this.fileName = file.getName();
        this.filePath = file.getAbsolutePath();
        this.fileSizeBytes = file.length();
        this.dateDownloaded = downloadDate;

        try {
            Path path = file.toPath();
            this.mimeType = Files.probeContentType(path);
            if (mimeType == null) {
                mimeType = "unknown";
            }
        } catch (Exception e) {
            mimeType = "unknown";
        }
    }

    /**
     * Gets the name of the file.
     *
     * @return the file name.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets the MIME type of the file.
     *
     * @return the MIME type string.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets the absolute path of the file.
     *
     * @return the file path.
     */
    public File getFile() {
        return new File(filePath);
    }

    public void setFilePath(String newPath) {
        this.filePath = newPath;
    }

    /**
     * Gets the file size in bytes.
     *
     * @return the file size in bytes.
     */
    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    /**
     * Gets the formatted date when the file was downloaded.
     *
     * @return the formatted download date as a string.
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(dateDownloaded);
    }

    /**
     * Converts the file size from bytes to megabytes.
     *
     * @return the file size in megabytes.
     */
    public double getSizeInMB() {
        return fileSizeBytes / (1024.0 * 1024.0);
    }

    /**
     * Gets the raw Date object for the download date.
     *
     * @return the Date of download.
     */
    public Date getDateDownloaded() {
        return dateDownloaded;
    }

    /**
     * Returns a string representation of this MediaFile object, including its
     * name and formatted size.
     *
     * @return a formatted string representing the media file.
     */
    @Override
    public String toString() {
        return fileName;  // SOLO el nombre
    }
    /**
     * Checks equality based on the absolute file path.
     *
     * <p>
     * Two MediaFile objects are considered equal if they reference the same
     * physical file on disk.
     * </p>
     *
     * @param obj object to compare
     * @return true if both refer to the same file path
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MediaFile other = (MediaFile) obj;
        return this.filePath.equals(other.filePath);
    }
    /**
     * Generates a hash code using the file path, ensuring consistency with equals().
     *
     * @return hash code for this MediaFile
     */
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}
