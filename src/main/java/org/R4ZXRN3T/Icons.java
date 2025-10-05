package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

class Icons {

	public static final ImageIcon FIRSTPASS_ICON = new ImageIcon(getFile("assets/firstpass_icon.png"));
	public static final ImageIcon ADD_ICON = new ImageIcon(getFile("assets/add_icon.png"));
	public static final ImageIcon ADD_ICON_SCALED = new ImageIcon(ADD_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon ADD_ICON_WHITE = new ImageIcon(getFile("assets/add_icon_white.png"));
	public static final ImageIcon ADD_ICON_WHITE_SCALED = new ImageIcon(ADD_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon REMOVE_ICON = new ImageIcon(getFile("assets/remove_icon.png"));
	public static final ImageIcon REMOVE_ICON_SCALED = new ImageIcon(REMOVE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon REMOVE_ICON_WHITE = new ImageIcon(getFile("assets/remove_icon_white.png"));
	public static final ImageIcon REMOVE_ICON_WHITE_SCALED = new ImageIcon(REMOVE_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EDIT_ICON = new ImageIcon(getFile("assets/edit_icon.png"));
	public static final ImageIcon EDIT_ICON_SCALED = new ImageIcon(EDIT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EDIT_ICON_WHITE = new ImageIcon(getFile("assets/edit_icon_white.png"));
	public static final ImageIcon EDIT_ICON_WHITE_SCALED = new ImageIcon(EDIT_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon UNDO_ICON = new ImageIcon(getFile("assets/undo_icon.png"));
	public static final ImageIcon UNDO_ICON_SCALED = new ImageIcon(UNDO_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon UNDO_ICON_WHITE = new ImageIcon(getFile("assets/undo_icon_white.png"));
	public static final ImageIcon UNDO_ICON_WHITE_SCALED = new ImageIcon(UNDO_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EXIT_ICON = new ImageIcon(getFile("assets/exit_icon.png"));
	public static final ImageIcon EXIT_ICON_SCALED = new ImageIcon(EXIT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EXIT_ICON_WHITE = new ImageIcon(getFile("assets/exit_icon_white.png"));
	public static final ImageIcon EXIT_ICON_WHITE_SCALED = new ImageIcon(EXIT_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon SEARCH_ICON = new ImageIcon(getFile("assets/search_icon.png"));
	public static final ImageIcon SEARCH_ICON_SCALED = new ImageIcon(SEARCH_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon SEARCH_ICON_WHITE = new ImageIcon(getFile("assets/search_icon_white.png"));
	public static final ImageIcon SEARCH_ICON_WHITE_SCALED = new ImageIcon(SEARCH_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon APPLY_ICON = new ImageIcon(getFile("assets/apply_icon.png"));
	public static final ImageIcon APPLY_ICON_SCALED = new ImageIcon(APPLY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon APPLY_ICON_WHITE = new ImageIcon(getFile("assets/apply_icon_white.png"));
	public static final ImageIcon APPLY_ICON_WHITE_SCALED = new ImageIcon(APPLY_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon CANCEL_ICON = new ImageIcon(getFile("assets/cancel_icon.png"));
	public static final ImageIcon CANCEL_ICON_SCALED = new ImageIcon(CANCEL_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon CANCEL_ICON_WHITE = new ImageIcon(getFile("assets/cancel_icon_white.png"));
	public static final ImageIcon CANCEL_ICON_WHITE_SCALED = new ImageIcon(CANCEL_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon SAVE_ICON = new ImageIcon(getFile("assets/save_icon.png"));
	public static final ImageIcon SAVE_ICON_SCALED = new ImageIcon(SAVE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon SAVE_ICON_WHITE = new ImageIcon(getFile("assets/save_icon_white.png"));
	public static final ImageIcon SAVE_ICON_WHITE_SCALED = new ImageIcon(SAVE_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EXPORT_ICON = new ImageIcon(getFile("assets/export_icon.png"));
	public static final ImageIcon EXPORT_ICON_SCALED = new ImageIcon(EXPORT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon EXPORT_ICON_WHITE = new ImageIcon(getFile("assets/export_icon_white.png"));
	public static final ImageIcon EXPORT_ICON_WHITE_SCALED = new ImageIcon(EXPORT_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon IMPORT_ICON = new ImageIcon(getFile("assets/import_icon.png"));
	public static final ImageIcon IMPORT_ICON_SCALED = new ImageIcon(IMPORT_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon IMPORT_ICON_WHITE = new ImageIcon(getFile("assets/import_icon_white.png"));
	public static final ImageIcon IMPORT_ICON_WHITE_SCALED = new ImageIcon(IMPORT_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon COPY_ICON = new ImageIcon(getFile("assets/copy_icon.png"));
	public static final ImageIcon COPY_ICON_SCALED = new ImageIcon(COPY_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon COPY_ICON_WHITE = new ImageIcon(getFile("assets/copy_icon_white.png"));
	public static final ImageIcon COPY_ICON_WHITE_SCALED = new ImageIcon(COPY_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon GENERATE_ICON = new ImageIcon(getFile("assets/generate_icon.png"));
	public static final ImageIcon GENERATE_ICON_SCALED = new ImageIcon(GENERATE_ICON.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));
	public static final ImageIcon GENERATE_ICON_WHITE = new ImageIcon(getFile("assets/generate_icon_white.png"));
	public static final ImageIcon GENERATE_ICON_WHITE_SCALED = new ImageIcon(GENERATE_ICON_WHITE.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH));

	// All icons ever used in the program
	// The icons are stored in the assets folder
	// non-scaled versions are 128x128, but might be irrelevant as they are not used in the program yet
	private static URL getFile(String path) {
		// Check if resource exists as portable version
		if (Icons.class.getResource("/" + path) != null) return Icons.class.getResource("/" + path);
		try {
			return new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
}