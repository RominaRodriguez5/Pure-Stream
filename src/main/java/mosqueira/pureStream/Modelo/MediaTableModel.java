package mosqueira.pureStream.Modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Table model that manages a collection of {@link MediaFile} objects.
 *
 * <p>This model provides the data required by a {@link javax.swing.JTable}
 * to display downloaded media files. Each row represents a {@code MediaFile}
 * and each column corresponds to a specific attribute such as file name,
 * MIME type, size and download date.</p>
 *
 * <p>The model notifies the table automatically when data changes using
 * the appropriate {@code fireTable...} methods.</p>
 *
 * @author Romina
 * @version 1.0
 */
public class MediaTableModel extends AbstractTableModel {

    /**
     * Column titles displayed in the JTable header.
     */
    private final String[] columnNames = {
        "File Name",
        "MIME Type",
        "Size (MB)",
        "Date Downloaded"
    };

    /**
     * Internal storage for table rows.
     */
    private final List<MediaFile> mediaFiles;

    /**
     * Creates an empty table model.
     */
    public MediaTableModel() {
        mediaFiles = new ArrayList<>();
    }

    /**
     * Returns the number of rows currently stored in the model.
     *
     * @return total number of media files
     */
    @Override
    public int getRowCount() {
        return mediaFiles.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return number of defined columns
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the column name displayed in the table header.
     *
     * @param column column index
     * @return column title
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the value stored at a specific cell.
     *
     * @param rowIndex index of the row
     * @param columnIndex index of the column
     * @return value to be rendered in the table cell, or {@code null} if invalid
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MediaFile file = mediaFiles.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file.getFileName();
            case 1:
                return file.getMimeType();
            case 2:
                return String.format("%.2f", file.getSizeInMB());
            case 3:
                return file.getFormattedDate();
            default:
                return null;
        }
    }

    /**
     * Adds a new {@link MediaFile} to the model.
     *
     * @param mediaFile media file to add
     */
    public void addMediaFile(MediaFile mediaFile) {
        mediaFiles.add(mediaFile);
        fireTableRowsInserted(mediaFiles.size() - 1, mediaFiles.size() - 1);
    }

    /**
     * Removes the media file at the specified row index.
     *
     * @param rowIndex index of the row to remove
     */
    public void removeMediaFile(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            mediaFiles.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Returns the {@link MediaFile} stored at the specified row.
     *
     * @param rowIndex row index
     * @return the corresponding {@code MediaFile}, or {@code null} if the index is invalid
     */
    public MediaFile getMediaFileAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            return mediaFiles.get(rowIndex);
        }
        return null;
    }

    /**
     * Removes all media files from the model.
     */
    public void clearAll() {
        mediaFiles.clear();
        fireTableDataChanged();
    }

    /**
     * Returns the internal list of media files.
     *
     * @return list containing all stored media files
     */
    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    /**
     * Replaces the current list of media files with a new one.
     *
     * @param files list of media files to set
     */
    public void setMediaFiles(List<MediaFile> files) {
        mediaFiles.clear();
        mediaFiles.addAll(files);
        fireTableDataChanged();
    }

    /**
     * Returns the index of the specified media file.
     *
     * @param mf media file to search
     * @return index of the media file, or -1 if not found
     */
    public int indexOf(MediaFile mf) {
        return mediaFiles.indexOf(mf);
    }
}