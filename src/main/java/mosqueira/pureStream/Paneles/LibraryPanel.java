package mosqueira.pureStream.Paneles;

import mosqueira.mediaPollingClientComponent.model.Media;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mosqueira.pureStream.ControladorInterno.IconUtils;
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
    private static String BIBLIOTECA_FILE;

    // Table model for detailed view
    private MediaTableModel tableModel;

    // List model for compact list view
    private DefaultListModel<MediaFile> listModel;

    // Full library of media files (unfiltered)
    private List<MediaFile> allMediaFiles = new ArrayList<>();

    // Reference to MainFrame for navigation
    private MainFrame mainFrame;
    // NETWORK LISTS
    private List<MediaFile> netWorkMedia = new ArrayList<>();
    private List<MediaFile> botMedia = new ArrayList<>();

    /**
     * Constructor: initializes UI components and loads saved library.
     */
    public LibraryPanel(MainFrame mainFrame, MediaTableModel tableModel) {
        this.mainFrame = mainFrame;
        this.tableModel = tableModel;
        BIBLIOTECA_FILE = mainFrame.getRutaDescargas() + File.separator + "mediaLibrary.dat";

        initComponents();
        setSize(800, 800);
        jtabSources.addTab("Local", null);
        jtabSources.addTab("Network", null);
        jtabSources.addTab("Both", null);

        jTblDetails.setModel(tableModel);
        listModel = new DefaultListModel<>();
        jListDownloads.setModel(listModel);

        cargarBiblioteca();

        jTblDetails.setAutoCreateRowSorter(true);

        conectarEventos();

        tableModel.setMediaFiles(allMediaFiles);
        tableModel.fireTableDataChanged();

        listModel.clear();
        for (MediaFile mf : allMediaFiles) {
            listModel.addElement(mf);
        }

        jtabSources.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {

                int index = jtabSources.getSelectedIndex();

                List<MediaFile> listaActual;

                if (index == 0) {
                    listaActual = allMediaFiles;
                } else if (index == 1) {
                    listaActual = netWorkMedia;
                } else {
                    listaActual = botMedia;
                }

                // Update table
                tableModel.setMediaFiles(listaActual);
                tableModel.fireTableDataChanged();

                // Update JList 
                listModel.clear();
                for (MediaFile mf : listaActual) {
                    listModel.addElement(mf);
                }
            }
        });

        btnSearch.setToolTipText("Search in the library");
        btnDelete.setToolTipText("Delete selected file");
        btnBack.setToolTipText("Return to main screen");
        btnUploadtoCloud.setToolTipText("Upload selected local file to cloud");
        btnDownloadFromCloud.setToolTipText("Download selected cloud file");

        btnSearch.setIcon(IconUtils.load("/images/search.png", 20));
        btnDelete.setIcon(IconUtils.load("/images/delete.png", 20));
        btnBack.setIcon(IconUtils.load("/images/back.png", 20));
        btnUploadtoCloud.setIcon(IconUtils.load("/images/upload.png", 20));
        btnDownloadFromCloud.setIcon(IconUtils.load("/images/download.png", 20));
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

        media.setNetworkState("LOCAL");
        allMediaFiles.add(media);

        tableModel.addMediaFile(media);
        listModel.addElement(media);
        guardarBiblioteca(); // save updated library
    }

    /**
     * Saves the full media library via serialization.
     */
    private void guardarBiblioteca() {
        try {

            File f = new File(BIBLIOTECA_FILE);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIBLIOTECA_FILE))) {
                oos.writeObject(allMediaFiles);
            }
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
        // Update table
        tableModel.setMediaFiles(lista);

        // Update list
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

        List<MediaFile> sourceList;

        int index = jtabSources.getSelectedIndex();
        if (index == 0) {
            sourceList = allMediaFiles;
        } else if (index == 1) {
            sourceList = netWorkMedia;
        } else {
            sourceList = botMedia;
        }

        List<MediaFile> filtered = new ArrayList<>();
        for (MediaFile f : sourceList) {

            // Filtro por texto
            boolean matchesText = f.getFileName().toLowerCase().contains(searchText);
            if (!matchesText) {
                continue;
            }
            boolean matchesType = true;

            if (filter.equals("Video")) {
                matchesType = f.getMimeType() != null && f.getMimeType().contains("video");
            } else if (filter.equals("Audio")) {
                matchesType = f.getMimeType() != null && f.getMimeType().contains("audio");
            }

            if (matchesType) {
                filtered.add(f);
            }
        }

        receiveFiles(filtered);
    }

    public void loadNetworkMedia() throws Exception {
        netWorkMedia.clear();
        botMedia.clear();

        List<Media> mediaFromNet = MainFrame.COMPONENT.getAllMedia(mainFrame.getJwtToken());

        for (Media m : mediaFromNet) {

            File fakeRemote = new File(m.mediaFileName);
            MediaFile mf = new MediaFile(fakeRemote, new Date());

            mf.setRemoteId(m.id);
            mf.setNetworkState("NETWORK");

            netWorkMedia.add(mf);
        }

        for (MediaFile local : allMediaFiles) {

            boolean existsRemote = false;

            for (MediaFile remote : netWorkMedia) {
                if (remote.getFileName().equals(local.getFileName())) {
                    existsRemote = true;
                    break;
                }
            }

            if (existsRemote) {
                local.setNetworkState("BOTH");
                botMedia.add(local);
            } else {
                local.setNetworkState("LOCAL");
            }
        }

        // 4) Mostrar LOCAL por defecto
        if (jtabSources.getSelectedIndex() == 0) {
            tableModel.setMediaFiles(allMediaFiles);
            tableModel.fireTableDataChanged();

            listModel.clear();
            for (MediaFile mf : allMediaFiles) {
                listModel.addElement(mf);
            }

        }
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
        jScrollPane1 = new javax.swing.JScrollPane();
        jListDownloads = new javax.swing.JList<>();
        jtabSources = new javax.swing.JTabbedPane();
        btnBack = new javax.swing.JButton();
        btnUploadtoCloud = new javax.swing.JButton();
        btnDownloadFromCloud = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Download details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Serif", 1, 18), new java.awt.Color(0, 0, 153))); // NOI18N
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
        jScrollTable.setBounds(10, 70, 760, 290);

        btnSearch.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        add(btnSearch);
        btnSearch.setBounds(160, 390, 110, 30);

        btnDelete.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        add(btnDelete);
        btnDelete.setBounds(580, 390, 110, 30);

        jSeparatorListDownload.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Downloads list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Serif", 1, 18), new java.awt.Color(0, 0, 153))); // NOI18N
        add(jSeparatorListDownload);
        jSeparatorListDownload.setBounds(10, 460, 790, 40);

        jtxtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSearchActionPerformed(evt);
            }
        });
        add(jtxtSearch);
        jtxtSearch.setBounds(300, 390, 240, 30);

        jcbFiltrados.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        jcbFiltrados.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Video", "Audio" }));
        jcbFiltrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFiltradosActionPerformed(evt);
            }
        });
        add(jcbFiltrados);
        jcbFiltrados.setBounds(10, 390, 120, 26);

        jScrollPane1.setViewportView(jListDownloads);

        add(jScrollPane1);
        jScrollPane1.setBounds(10, 510, 480, 240);
        add(jtabSources);
        jtabSources.setBounds(10, 40, 760, 30);

        btnBack.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack);
        btnBack.setBounds(500, 720, 190, 30);

        btnUploadtoCloud.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnUploadtoCloud.setText("Upload to Cloud");
        btnUploadtoCloud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadtoCloudActionPerformed(evt);
            }
        });
        add(btnUploadtoCloud);
        btnUploadtoCloud.setBounds(500, 620, 190, 30);

        btnDownloadFromCloud.setFont(new java.awt.Font("Segoe UI Light", 1, 14)); // NOI18N
        btnDownloadFromCloud.setText("Download from cloud");
        btnDownloadFromCloud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadFromCloudActionPerformed(evt);
            }
        });
        add(btnDownloadFromCloud);
        btnDownloadFromCloud.setBounds(500, 530, 190, 30);
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
                "Confirm deletion",
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

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        mainFrame.showPanelPrincipal();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUploadtoCloudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadtoCloudActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file from the table.");
            return;
        }

        selected = jTblDetails.convertRowIndexToModel(selected);
        MediaFile mf = tableModel.getMediaFileAt(selected);
        File file = mf.getFile();

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file does not exist on disk.");
            return;
        }

        try {
            // Subida simple
            MainFrame.COMPONENT.uploadFileMulti(
                    file,
                    null, // downloadedFromUrl
                    mainFrame.getJwtToken() // token JWT
            );

            JOptionPane.showMessageDialog(this, "File uploaded successfully.");

            // Recargar datos de la nube
            loadNetworkMedia();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Upload failed:\n" + ex.getMessage());
        }

    }//GEN-LAST:event_btnUploadtoCloudActionPerformed

    private void btnDownloadFromCloudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadFromCloudActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file in the Network tab.");
            return;
        }

        selected = jTblDetails.convertRowIndexToModel(selected);
        MediaFile remoteMF = tableModel.getMediaFileAt(selected);

        if (remoteMF.getRemoteId() == null) {
            JOptionPane.showMessageDialog(this, "This file has no remote ID.");
            return;
        }
        try {
            File destino = new File(mainFrame.getRutaDescargas(), remoteMF.getFileName());
            MainFrame.COMPONENT.download(
                    remoteMF.getRemoteId(),
                    destino,
                    mainFrame.getJwtToken()
            );

            MediaFile localMF = new MediaFile(destino, new Date());
            localMF.setRemoteId(remoteMF.getRemoteId());
            localMF.setNetworkState("BOTH");

            allMediaFiles.add(localMF);

            botMedia.add(localMF);

            guardarBiblioteca();

            JOptionPane.showMessageDialog(this, "File downloaded successfully.");

            loadNetworkMedia();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Download failed:\n" + ex.getMessage());
        }
    }//GEN-LAST:event_btnDownloadFromCloudActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDownloadFromCloud;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUploadtoCloud;
    private javax.swing.JList<MediaFile> jListDownloads;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollTable;
    private javax.swing.JSeparator jSeparatorListDownload;
    private javax.swing.JTable jTblDetails;
    private javax.swing.JComboBox<String> jcbFiltrados;
    private javax.swing.JTabbedPane jtabSources;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}
