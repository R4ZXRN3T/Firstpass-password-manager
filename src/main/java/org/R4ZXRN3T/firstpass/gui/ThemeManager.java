package org.R4ZXRN3T.firstpass.gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;

import org.R4ZXRN3T.firstpass.Config;
import org.R4ZXRN3T.firstpass.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Objects;

public class ThemeManager {

	private static final Logger logger = new org.R4ZXRN3T.firstpass.Logger(Config.getLogFilePath().toString());

	// sets the look and feel of the program. Also returns whether the look and feel is a dark mode theme
	public boolean setLookAndFeel(String LaFIndex) {
		boolean isDarkTheme = false;
		System.out.println("\nSetting Look and Feel...");
		try {
			switch (LaFIndex) {
				case "0" -> UIManager.setLookAndFeel(new FlatLightLaf());
				case "1" -> {
					UIManager.setLookAndFeel(new FlatDarkLaf());
					isDarkTheme = true;
				}
				case "2" -> UIManager.setLookAndFeel(new FlatMacLightLaf());
				case "3" -> {
					UIManager.setLookAndFeel(new FlatMacDarkLaf());
					isDarkTheme = true;
				}
				case "4" -> UIManager.setLookAndFeel(new FlatIntelliJLaf());
				case "5" -> {
					UIManager.setLookAndFeel(new FlatDarculaLaf());
					isDarkTheme = true;
				}
				case "6" -> UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
				default -> UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			UIManager.put("Component.focusWidth", 1);
			UIManager.put("Button.background", isDarkTheme ? new Color(50, 50, 50) : Color.lightGray);
			UIManager.put("Button.disabledBackground", new Color(0, 0, 0, 0));
			UIManager.getDefaults().put("defaultFont", getFont());
			System.out.println("Look and Feel successfully set and custom settings applied.");
			return isDarkTheme;
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
				 IllegalAccessException e) {
			logger.error("Error setting Look and Feel: " + e.getMessage());
			System.out.println("Error setting Look and Feel: " + e.getMessage() + "Exiting program...");
			System.exit(1);
		}
		return false;
	}

	private Font getFont() {
		Font finalFont;

		try {
			if (ThemeManager.class.getResource("/assets/font_inter_variable.ttf") != null) {
				Font baseFont = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(ThemeManager.class.getResourceAsStream("/assets/font_inter_variable.ttf")));
				finalFont = baseFont.deriveFont(12f);
			} else if (new File("assets/font_inter_variable.ttf").exists()) {
				Font baseFont = Font.createFont(Font.TRUETYPE_FONT, new FileInputStream("assets/font_inter_variable.ttf"));
				finalFont = baseFont.deriveFont(12f);
			} else throw new IOException("Font file not found in both classpath and filesystem.");
		} catch (IOException | FontFormatException e) {
			finalFont = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
			System.err.println("Font file not found in both classpath and filesystem. Falling back to system font.");
			logger.warn("Font file not found in both classpath and filesystem. Falling back to system font.");
		}
		GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(finalFont);
		return finalFont;
	}
}