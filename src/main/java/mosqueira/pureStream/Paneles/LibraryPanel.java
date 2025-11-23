package mosqueira.pureStream.Paneles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Modelo.MediaTableModel;

/**
 * LibraryPanel displays all downloaded media files (audio/video) and allows the
 * user to search, filter, and delete them from the library.
 *
 * @author Romina
 *
 */
public class LibraryPanel extends javax.swing.JPanel {

    // Name of the serialized library file
    private static final String BIBLIOTECA_FILE = "mediaLibrary.dat";

    // Table model for detailed view
    private MediaTableModel tableModel;

    // List model for compact list view
    private DefaultListModel<MediaFile> listModel;

    // Full library of media files (unfiltered)
    private List<MediaFile> allMediaFiles = new ArrayList<>();

    // Reference to MainFrame for navigation
    private MainFrame mainFrame;

    /**
     * Constructor: initializes UI components and loads saved library.
     */
    public LibraryPanel(MainFrame mainFrame, MediaTableModel tableModel) {
        this.mainFrame = mainFrame;
        this.tableModel = tableModel;

        initComponents();
        setSize(800, 800);

        jTblDetails.setModel(tableModel);
        listModel = new DefaultListModel<>();
        jListDownloads.setModel(listModel);

        cargarBiblioteca();
        jTblDetails.setAutoCreateRowSorter(true);

        conectarEventos();
    }

    /**
     * Connects selection events between JTable and JList.
     */
    private void conectarEventos() {
        jListDownloads.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    MediaFile selected = jListDownloads.getSelectedValue();
                    if (selected != null) {
                        int modelIndex = tableModel.indexOf(selected);
                        if (modelIndex != -1) {
                            int viewIndex = jTblDetails.convertRowIndexToView(modelIndex);
                            if (viewIndex != -1) {
                                jTblDetails.setRowSelectionInterval(viewIndex, viewIndex);
                            }
                        }
                    }
                }
            }
        });
        // Sync from JTable to JList
        jTblDetails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewRow = jTblDetails.getSelectedRow();
                    if (viewRow != -1) {
                        int modelRow = jTblDetails.convertRowIndexToModel(viewRow);
                        MediaFile mf = tableModel.getMediaFileAt(modelRow);
                        if (mf != null) {
                            jListDownloads.setSelectedValue(mf, true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Add a new media file to library
     */
    public void addMediaFile(MediaFile media) {

        for (MediaFile mf : allMediaFiles) {
            if (mf.getFile().getAbsolutePath().equals(media.getFile().getAbsolutePath())) {
                return; // evitar duplicado
            }
        }

        allMediaFiles.add(media);
        tableModel.addMediaFile(media);
        listModel.addElement(media);
        guardarBiblioteca(); // save updated library
    }

    /**
     * Saves the full media library via serialization.
     */
    private void guardarBiblioteca() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIBLIOTECA_FILE))) {
            oos.writeObject(allMediaFiles);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads the serialized media library if the file exists.
     */
    private void cargarBiblioteca() {
        File f = new File(BIBLIOTECA_FILE);
        if (!f.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {

            List<MediaFile> files = (List<MediaFile>) ois.readObject();
            allMediaFiles.clear();
            allMediaFiles.addAll(files);

            receiveFiles(allMediaFiles);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Update view and internal list with provided files
     */
    public void receiveFiles(List<MediaFile> lista) {
        // actualizar tabla
        tableModel.setMediaFiles(lista);

        // actualizar lista
        listModel.clear();
        for (MediaFile mf : lista) {
            listModel.addElement(mf);
        }
    }

    /**
     * Filters library by search text and selected type.
     */
    private void filtrarLista() {
        String searchText = jtxtSearch.getText().toLowerCase().trim();
        String filter = jcbFiltrados.getSelectedItem().toString();

        List<MediaFile> filtered = allMediaFiles.stream()
                .filter(f -> f.getFileName().toLowerCase().contains(searchText))
                .filter(f -> switch (filter) {
            case "Video" ->
                f.getMimeType().contains("video");
            case "Audio" ->
                f.getMimeType().contains("audio");
            default ->
                true;
        })
                .collect(Collectors.toList());

        receiveFiles(filtered);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollTable = new javax.swing.JScrollPane();
        jTblDetails = new javax.swing.JTable();
        btnSearch = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jSeparatorListDownload = new javax.swing.JSeparator();
        jtxtSearch = new javax.swing.JTextField();
        jcbFiltrados = new javax.swing.JComboBox<>();
        btnBack = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListDownloads = new javax.swing.JList<>();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Details Download", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 1, 18), new java.awt.Color(0, 0, 153))); // NOI18N
        setLayout(null);

        jTblDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollTable.setViewportView(jTblDetails);

        add(jScrollTable);
        jScrollTable.setBounds(20, 40, 520, 300);

        btnSearch.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnSearch.setText("Buscar");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        add(btnSearch);
        btnSearch.setBounds(550, 50, 72, 27);

        btnDelete.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        add(btnDelete);
        btnDelete.setBounds(550, 310, 90, 27);

        jSeparatorListDownload.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "List Download", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI Light", 1, 18), new java.awt.Color(0, 0, 153))); // NOI18N
        add(jSeparatorListDownload);
        jSeparatorListDownload.setBounds(0, 380, 820, 40);

        jtxtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSearchActionPerformed(evt);
            }
        });
        add(jtxtSearch);
        jtxtSearch.setBounds(550, 100, 220, 40);

        jcbFiltrados.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jcbFiltrados.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Video", "Audio" }));
        jcbFiltrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFiltradosActionPerformed(evt);
            }
        });
        add(jcbFiltrados);
        jcbFiltrados.setBounds(550, 180, 120, 26);

        btnBack.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnBack.setText("Go back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack);
        btnBack.setBounds(670, 310, 110, 30);

        jScrollPane1.setViewportView(jListDownloads);

        add(jScrollPane1);
        jScrollPane1.setBounds(30, 440, 680, 240);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Executes the search and filter logic when the user types or changes
     * filters.
     */
    private void jtxtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSearchActionPerformed
        filtrarLista();
    }//GEN-LAST:event_jtxtSearchActionPerformed
    /**
     * Handles combo box filter selection (Audio, Video, All).
     */
    private void jcbFiltradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFiltradosActionPerformed
        filtrarLista();
    }//GEN-LAST:event_jcbFiltradosActionPerformed
    
    /**
     * Handles the deletion of a selected file from the table and disk.
     */
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Select a file first.");
            return;
        }

        selected = jTblDetails.convertRowIndexToModel(selected);

        MediaFile mf = tableModel.getMediaFileAt(selected);
        File file = mf.getFile();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this file?\n" + file.getName(),
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Delete file from disk
        boolean deleted = !file.exists() || file.delete();
        if (!deleted) {
            JOptionPane.showMessageDialog(this, "Failed to delete the file.");
            return;
        }

        // Remove from internal lists
        allMediaFiles.remove(mf);
        tableModel.removeMediaFile(selected);
        listModel.removeElement(mf);

        guardarBiblioteca();

        JOptionPane.showMessageDialog(this, "File deleted successfully.");

    }//GEN-LAST:event_btnDeleteActionPerformed
    /**
     * Triggers the search manually when pressing the Search button.
     */
    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        filtrarLista();
    }//GEN-LAST:event_btnSearchActionPerformed
    /**
     * Returns to the main panel.
     */
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        mainFrame.mostrarPanelPrincipal();
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnSearch;
    private javax.swing.JList<MediaFile> jListDownloads;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollTable;
    private javax.swing.JSeparator jSeparatorListDownload;
    private javax.swing.JTable jTblDetails;
    private javax.swing.JComboBox<String> jcbFiltrados;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}
