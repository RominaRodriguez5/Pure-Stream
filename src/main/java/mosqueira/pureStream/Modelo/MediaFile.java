package mosqueira.pureStream.Modelo;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a downloaded media file and its metadata.
 *
 * <p>This class encapsulates information about a local file (name, MIME type,
 * absolute path, size, and download date). It is mainly used to populate UI
 * components such as tables and lists within the PureStream application.</p>
 *
 * <p>It also contains optional fields related to synchronization with a remote
 * library (e.g. remote identifier, uploader, and network state).</p>
 *
 * @author Romina
 * @version 1.0
 */
public class MediaFile implements Serializable {

    /**
     * Serialization identifier for compatibility.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Username (or identifier) of the user who uploaded the file to the remote library.
     * This value may be {@code null} for local-only files.
     */
    private String uploadedBy;

    /**
     * The name of the media file (e.g., {@code "video.mp4"}).
     */
    private String fileName;

    /**
     * The MIME type of the file (e.g., {@code "video/mp4"}, {@code "audio/mpeg"}).
     */
    private String mimeType;

    /**
     * Absolute file path on disk.
     */
    private String filePath;

    /**
     * File size in bytes.
     */
    private long fileSizeBytes;

    /**
     * Date when the file was downloaded (or registered in the library).
     */
    private Date dateDownloaded;

    /**
     * Network status of the media file.
     *
     * <p>Typical values are: {@code "LOCAL"}, {@code "NETWORK"} or {@code "BOTH"}.</p>
     */
    private String networkState = "LOCAL";

    /**
     * Remote identifier of this media file (if it exists in the remote library).
     * May be {@code null} when the file is local-only.
     */
    private Integer remoteId = null;

    /**
     * Creates a new {@code MediaFile} from a local {@link File} and a download date.
     *
     * <p>This constructor extracts metadata such as name, size and MIME type from the
     * file system. If the MIME type cannot be detected, it will be set to {@code "unknown"}.</p>
     *
     * @param file local file representing the media
     * @param downloadDate date when the file was downloaded (or added to the library)
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
     * Returns the media file name (e.g. {@code "video.mp4"}).
     *
     * @return file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Returns the detected MIME type of the file.
     *
     * @return MIME type, or {@code "unknown"} if it cannot be detected
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Returns the file as a {@link File} instance using the stored absolute path.
     *
     * @return local file reference
     */
    public File getFile() {
        return new File(filePath);
    }

    /**
     * Updates the absolute path of the file.
     *
     * @param newPath new absolute path
     */
    public void setFilePath(String newPath) {
        this.filePath = newPath;
    }

    /**
     * Returns the file size in bytes.
     *
     * @return file size in bytes
     */
    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    /**
     * Returns the download date formatted for display in the UI.
     *
     * @return formatted date string (pattern {@code "dd/MM/yyyy HH:mm"})
     */
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(dateDownloaded);
    }

    /**
     * Returns the file size expressed in megabytes (MB).
     *
     * @return size in MB
     */
    public double getSizeInMB() {
        return fileSizeBytes / (1024.0 * 1024.0);
    }

    /**
     * Returns the raw {@link Date} when the file was downloaded (or added).
     *
     * @return download date
     */
    public Date getDateDownloaded() {
        return dateDownloaded;
    }

    /**
     * Returns the network state of this media file.
     *
     * @return network state (e.g. {@code "LOCAL"}, {@code "NETWORK"}, {@code "BOTH"})
     */
    public String getNetworkState() {
        return networkState;
    }

    /**
     * Updates the network state of this media file.
     *
     * @param networkState new network state value
     */
    public void setNetworkState(String networkState) {
        this.networkState = networkState;
    }

    /**
     * Returns the remote identifier, if the media file exists in the remote library.
     *
     * @return remote id, or {@code null} if not assigned
     */
    public Integer getRemoteId() {
        return remoteId;
    }

    /**
     * Sets the remote identifier of this file.
     *
     * @param remoteId remote id to assign
     */
    public void setRemoteId(Integer remoteId) {
        this.remoteId = remoteId;
    }

    /**
     * Returns the uploader identifier associated with this media file, if any.
     *
     * @return uploader id/name, or {@code null} if not set
     */
    public String getUploadedBy() {
        return uploadedBy;
    }

    /**
     * Returns a user-friendly representation for list renderers.
     *
     * @return file name
     */
    @Override
    public String toString() {
        return fileName;
    }

    /**
     * Checks equality based on the absolute file path.
     *
     * @param obj object to compare
     * @return {@code true} if both objects refer to the same file path
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
     * Generates a hash code using the file path, consistent with {@link #equals(Object)}.
     *
     * @return hash code for this media file
     */
    @Override
    public int hashCode() {
        return filePath.hashCode();
    }
}