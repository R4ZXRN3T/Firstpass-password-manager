package org.R4ZXRN3T;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AccountTable extends JTable {

	private final String[] columns = {"Provider", "Username", "Password", "URL", "Comment"};
	private String[][] data;


	public AccountTable(ArrayList<Account> accounts) {
		super();
		data = AccountArrayListToArray(accounts);

		// make table uneditable, unfortunately this is the only way to do it
		DefaultTableModel model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		// set a lot of shit up :)
		setModel(model);
		setShowGrid(true);
		setFocusable(true);
		setRequestFocusEnabled(true);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setFillsViewportHeight(true);
		getTableHeader().setBackground(Main.darkMode ? new Color(48, 48, 48) : new Color(200, 200, 200));

		// Add MouseListener to the table header for sorting
		getTableHeader().addMouseListener(new MouseAdapter() {
			private boolean sortingReversed = false;
			private int prevCol = 0;
			int currentCol = 0;

			@Override
			public void mouseClicked(MouseEvent e) {
				currentCol = columnAtPoint(e.getPoint()) + 1;
				if (currentCol != prevCol) {
					sortingReversed = false;
					prevCol = currentCol;
				}

				if (sortingReversed) {
					Main.sort(currentCol, false);
					sortingReversed = false;
				} else {
					Main.sort(currentCol, true);
					sortingReversed = true;
				}
			}
		});
	}

	// puts the table into a JScrollPane
	public JScrollPane getScrollPane() {

		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setFocusable(true);
		scrollPane.setRequestFocusEnabled(true);

		// add rounded corners via a border. If not too thick, it actually looks good
		Border roundedBorder = new LineBorder(this.getBackground(), 8, true);
		scrollPane.setBorder(roundedBorder);

		return scrollPane;
	}

	// get whether a row is selected
	public boolean isRowSelected() {
		return getSelectedRow() != -1;
	}

	// row selection listener
	public void addRowSelectionListener(ListSelectionListener listener) {
		getSelectionModel().addListSelectionListener(listener);
	}

	// get whether the table is in focus
	public boolean isFocused() {
		return this.isFocusOwner();
	}

	// set the content of the table
	public void setContent(ArrayList<Account> accounts) {
		data = AccountArrayListToArray(accounts);
		DefaultTableModel model = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		setModel(model);
	}

	// returns the content of a given row as an Account object
	public Account getAccountAt(int rowIndex) {
		return new Account(data[rowIndex][0], data[rowIndex][1], data[rowIndex][2], data[rowIndex][3], data[rowIndex][4]);
	}

	// converts an ArrayList of Account objects to a 2D String array, only used in this class for conversion
	// maybe I'll move this to the Tools class if I need it elsewhere
	private static String[][] AccountArrayListToArray(ArrayList<Account> inputArrayList) {

		String[][] finalArray = new String[inputArrayList.size()][5];

		for (int i = 0; i < inputArrayList.size(); i++) {
			finalArray[i] = inputArrayList.get(i).toArray();
		}
		return finalArray;
	}
}