package com.pedrodev.jgol.util

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JOptionPane

class NotificationUtil {
    companion object {
        fun showNotification(message: String, title: String) {
            if (SystemTray.isSupported()) {
                val tray = SystemTray.getSystemTray()

                val image: Image = try {
                    // TODO buscar imagem da pasta resource
                    ImageIO.read(File("logo.webp"))
                } catch (e: Exception) {
                    BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB)
                }

                val trayIcon = TrayIcon(image, "Desktop Notification")
                trayIcon.isImageAutoSize = true

                try {
                    tray.add(trayIcon)
                    trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
                } catch (e: AWTException) {
                    e.printStackTrace()
                }
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    message,
                    title,
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
        }
    }
}