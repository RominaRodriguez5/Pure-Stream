# PureStream
Multimedia downloader with ad removal (DI01 Project)

## 👩‍💻 Author
- Name: Romina Rodríguez  
- Course: Desarrollo de Interfaces (DI01)  
- Center: CIFP Pau Casesnoves  

## 🎯 Description
**PureStream** is a Java Swing application that allows downloading videos and audios from YouTube and other platforms compatible with **yt-dlp**, automatically removing ads.  
It features a user-friendly GUI built with NetBeans Designer and allows managing download options, user preferences, and playback of the last downloaded file.

## ⚙️ Main Features
- GUI created with NetBeans Designer.  
- Download videos and audios in MP4 or MP3 format.  
- Automatic ad removal using yt-dlp.  
- Automatic playback of the last downloaded file.  
- User preferences:
  - Download folder.  
  - Creation of .m3u playlists.  
  - Download speed limit.  
  - Paths for binaries (yt-dlp, FFmpeg, ffprobe).  
- Asynchronous download using `SwingWorker`.  
- Progress and error logging in the interface.  

## 💻 System Requirements
- JDK 24  
- NetBeans 27  
- OS: Windows recommended (Full HD resolution)  
- External dependencies:
  - **yt-dlp**  
  - **FFmpeg**  
  - **FFprobe**  

## 📺 Videos and References

During the development of **PureStream**, the following videos and resources were used:

### Week 1
- [Course Material](https://fpadistancia.caib.es/pluginfile.php/93626/mod_resource/content/2/index.html)  
- GUI overview: [Graphical User Interface](https://en.wikipedia.org/wiki/Graphical_user_interface)  
- Maven Swing project creation:
  - [Create a Maven project with NetBeans 15 and JDK 17](https://youtu.be/JLnld38AzUE?si=xgls1k1XCVuJkkIi)  
- Swing Containers and components:
  - JFrame, JDialog, JPanel  
  - Naming conventions: [StackOverflow](https://stackoverflow.com/questions/4770174/name-convention-on-java-swing-componentsprefix), [MIT Java tutorial](https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html)  
- Dialogs (modal and non-modal): [Playlist videos 1–8](https://www.youtube.com/playlist?list=PLIfP1vJ2qakli4Z_-yVZV-rq_hQeHQzUb)  
- JOptionPane: Video 11 from the same playlist  

### Week 2
- JDBC Swing tutorial: [UBC Article](https://www.cs.ubc.ca/~laks/cpsc304/Swing/jdbc_swing.html)  
- Practiced components: JFrame, JLabel, JButton, JTextField, JTextArea, JCheckBox, JRadioButton, ButtonGroup, JSlider, JProgressBar, JSpinner, JOptionPane, JFileChooser, JMenuBar, JMenu, JMenuItem  

### Week 3
- JList and JComboBox of objects: Tutoria 2022-11-11  
- JTable: [Playlist videos 9–20 (exclude 13 & 17)](https://www.youtube.com/playlist?list=PLIfP1vJ2qakli4Z_-yVZV-rq_hQeHQzUb)  

### Week 4
- Finish DI01 tasks.  
- GUI concepts, NetBeans IDE, layout types (null layout used for this project).  

### Additional Resources
- OOP concepts: inheritance, composition, abstract classes & interfaces: [Video](https://www.youtube.com/watch?v=HvPlEJ3LHgE)  
- Adding images/icons in Java Swing Maven applications  

## 📝 Prompts to LLM
- “Explain how to create a Swing GUI in Java with NetBeans for video download application.”  
- “Provide a SwingWorker implementation to handle asynchronous video downloads in Java.”  
- “Show how to use JList, JTable, JComboBox, and filters for a media library in Java Swing.”  
- “How to serialize and deserialize a list of media files in Java.”  
- “Give an example of using yt-dlp with command line options from Java.”  

## ⚠️ Problems Encountered
1. **Detecting the last downloaded file:**  
   - Solved by parsing the console output of yt-dlp and checking the download folder.  

2. **M3U playlist creation and validation:**  
   - Solved by creating `PlaylistValidator` and checking/creating the file if missing.  

3. **File deletion and library synchronization:**  
   - Solved by updating both the `MediaTableModel` and the internal `allMediaFiles` list after deletion.  

4. **Preferences management (paths, limits, etc.):**  
   - Solved with `ConfigProperties` class, replacing `${user.home}` dynamically.  

5. **GUI responsiveness during downloads:**  
   - Solved using `SwingWorker` for asynchronous background tasks.  

## 🔗 References
- [yt-dlp documentation](https://github.com/yt-dlp/yt-dlp)  
- [FFmpeg documentation](https://ffmpeg.org/documentation.html)  
- [Java Swing tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)  
- [NetBeans Swing Designer Guide](https://netbeans.apache.org/kb/docs/java/gui-builder.html)  

---

This README contains all required links, references, prompts, and a summary of the problems solved during the development of PureStream.
