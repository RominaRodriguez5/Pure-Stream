# PureStream  
> Descargador multimedia con eliminación de anuncios (Proyecto DI01)

## 👩‍💻 Autor  
- **Nombre:** Romina Rodríguez  
- **Curso:** Desarrollo de Interfaces (DI01)  
- **Centro:** CIFP Pau Casesnoves  

## 🎯 Descripción  
**PureStream** es una aplicación Java Swing que permite descargar vídeos y audios desde YouTube y otras plataformas compatibles con **yt-dlp**, eliminando automáticamente los anuncios.  
Incluye una interfaz gráfica amigable desarrollada con el **Diseñador de NetBeans**, y permite gestionar opciones de descarga, preferencias del usuario y reproducción del archivo descargado.

## ⚙️ Características principales  
- Interfaz gráfica creada con **NetBeans Designer**.  
- Descarga de vídeos y audios en formatos **MP4** o **MP3**.  
- Eliminación de anuncios usando `yt-dlp`.  
- Reproducción automática del último archivo descargado.  
- Configuración de preferencias:
  - Ruta de descarga.
  - Creación de listas `.m3u`.
  - Limitación de velocidad.
  - Ruta de los binarios (`yt-dlp`, `ffmpeg`, `ffprobe`).
- Descarga asíncrona mediante **SwingWorker**.
- Registro de progreso y errores en la interfaz.

## 💻 Requisitos del sistema  
- **JDK:** 24  
- **NetBeans:** 27  
- **Sistema operativo:** Windows (recomendado, resolución Full HD)  
- **Dependencias externas:**  
  - [yt-dlp](https://github.com/yt-dlp/yt-dlp)  
  - [FFmpeg](https://ffmpeg.org/)  
  - [FFprobe](https://ffmpeg.org/ffprobe.html)
