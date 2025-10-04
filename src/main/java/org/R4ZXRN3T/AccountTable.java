package org.R4ZXRN3T;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.RowSorterEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AccountTable extends JTable {

	private final String[] columns = {"Provider", "Username", "Password", "URL", "Comment"};
	private String[][] data;
	private Main main;

	public AccountTable(ArrayList<Account> accounts) {
		this(accounts, null);
	}

	public AccountTable(ArrayList<Account> accounts, Main main) {
		super();
		this.main = main;
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
		getTableHeader().setBackground((main != null && main.isDarkMode()) ? new Color(48, 48, 48) : new Color(200, 200, 200));
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(this.getModel());
		setRowSorter(sorter);

		sorter.addRowSorterListener(e -> {
			if (e.getType() == RowSorterEvent.Type.SORTED) {
				setMainData();
			}
		});

		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getClickCount() == 2 && getSelectedRow() >= 0 && main != null) {
					main.editAccount(getSelectedRow());
				}
			}
		});
	}

	public void setMain(Main main) {
		this.main = main;
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
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(this.getModel());
		setRowSorter(sorter);

		sorter.addRowSorterListener(e -> {
			if (e.getType() == RowSorterEvent.Type.SORTED) {
				// boolean reversed = sorter.getSortKeys().get(0).getSortOrder() == SortOrder.DESCENDING;
				setMainData();
			}
		});
	}

	private void setMainData() {
		if (main == null) {
			return;
		}
		main.getAccountList().clear();
		main.getAccountList().ensureCapacity(this.getRowCount());

		// Iterate through rows and directly use view indices
		for (int i = 0; i < this.getRowCount(); i++) {
			main.getAccountList().add(new Account(
					getValueAt(i, 0).toString(),
					getValueAt(i, 1).toString(),
					getValueAt(i, 2).toString(),
					getValueAt(i, 3).toString(),
					getValueAt(i, 4).toString()
			));
		}
		main.refreshIndices();
		main.setChangeMade(true);
	}

	// returns the content of a given row as an Account object
	public Account getAccount(int rowIndex) {
		return new Account(data[rowIndex][0], data[rowIndex][1], data[rowIndex][2], data[rowIndex][3], data[rowIndex][4]);
	}
}