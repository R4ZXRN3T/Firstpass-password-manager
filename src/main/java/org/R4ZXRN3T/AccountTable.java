package org.R4ZXRN3T;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.RowSorterEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;


public class AccountTable extends JTable {

	private final String[] columns = {"Provider", "Username", "Password", "URL", "Comment"};
	private String[][] data;
	private Firstpass firstpass;
	private int hoverRow = -1;
	private int hoverCol = -1;

	private final Set<Integer> revealedPasswordRows = new HashSet<>();

	/**
	 * Initializes the account table with given accounts and a reference to the main Firstpass instance.
	 *
	 * @param accounts  the list of accounts to display in the table
	 * @param firstpass the main Firstpass instance
	 *
	 */
	public AccountTable(ArrayList<Account> accounts, Firstpass firstpass) {
		super();
		this.firstpass = firstpass;
		data = AccountArrayListToArray(accounts);

		DefaultTableModel model = createModel(data);
		setModel(model);
		setShowGrid(true);
		setFocusable(true);
		setRequestFocusEnabled(true);
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		setFillsViewportHeight(true);
		getTableHeader().setBackground(Config.getDarkMode() ? new Color(48, 48, 48) : new Color(200, 200, 200));

		TableRowSorter<TableModel> sorter = new TableRowSorter<>(this.getModel());
		setRowSorter(sorter);
		sorter.addRowSorterListener(e -> {
			if (e.getType() == RowSorterEvent.Type.SORTED) setMainData();
		});

		setDefaultRenderer(Object.class, new HoverCopyRenderer());

		addMouseMotionListener(new MouseInputAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				if (r != hoverRow || c != hoverCol) {
					hoverRow = r;
					hoverCol = c;
					repaint();
				}
			}
		});

		addMouseListener(new MouseInputAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);
				if (r == -1 || c == -1) return;

				Rectangle cellRect = getCellRect(r, c, false);
				int buttonWidth = 20;

				if (c == 2) {
					int copyStart = cellRect.x + cellRect.width - buttonWidth;
					int eyeStart = copyStart - buttonWidth;
					if (p.x >= copyStart) {
						copyToClipboard(getValueAt(r, c));
						return;
					}
					if (p.x >= eyeStart) {
						int modelRow = convertRowIndexToModel(r);
						if (!revealedPasswordRows.add(modelRow)) revealedPasswordRows.remove(modelRow);
						repaint(cellRect);
						return;
					}
				} else if (p.x >= cellRect.x + cellRect.width - buttonWidth) {
					copyToClipboard(getValueAt(r, c));
					return;
				}

				if (e.getClickCount() == 2 && getSelectedRow() >= 0 && firstpass != null) {
					firstpass.editAccount(getSelectedRow());
				}
			}
		});
	}

	/**
	 * Creates a custom model from table data. Needed to make the cells non-editable
	 *
	 * @param tableData 2D array of table data
	 * @return DefaultTableModel with non-editable cells
	 *
	 */
	private DefaultTableModel createModel(String[][] tableData) {
		return new DefaultTableModel(tableData, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	/**
	 * Converts an ArrayList of Account objects to a 2D String array for table display.
	 *
	 * @param inputArrayList the list of Account objects
	 * @return 2D String array representing the account data
	 *
	 */
	private static String[][] AccountArrayListToArray(ArrayList<Account> inputArrayList) {
		String[][] finalArray = new String[inputArrayList.size()][5];
		for (int i = 0; i < inputArrayList.size(); i++) finalArray[i] = inputArrayList.get(i).toArray();
		return finalArray;
	}

	/**
	 * Sets the Firstpass instance for the object
	 *
	 * @param firstpass The Firstpass instance
	 */
	public void setMain(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	/**
	 * Wraps the table in a JScrollPane with rounded borders.
	 *
	 * @return JScrollPane containing the table
	 *
	 */
	public JScrollPane getScrollPane() {
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setFocusable(true);
		scrollPane.setRequestFocusEnabled(true);
		Border roundedBorder = new LineBorder(this.getBackground(), 8, true);
		scrollPane.setBorder(roundedBorder);
		return scrollPane;
	}

	/**
	 * Checks if a row is selected in the table.
	 *
	 * @return true if a row is selected, false otherwise
	 *
	 */
	public boolean isRowSelected() {
		return getSelectedRow() != -1;
	}

	/**
	 * Adds a ListSelectionListener to the table's selection model.
	 *
	 * @param listener the ListSelectionListener to add
	 *
	 */
	public void addRowSelectionListener(ListSelectionListener listener) {
		getSelectionModel().addListSelectionListener(listener);
	}

	/**
	 * Checks if the table is focused.
	 *
	 * @return true if the table is focused, false otherwise
	 *
	 */
	public boolean isFocused() {
		return this.isFocusOwner();
	}

	/**
	 * Sets the table content with a new list of accounts.
	 *
	 * @param accounts the list of accounts to display
	 *
	 */
	public void setContent(ArrayList<Account> accounts) {
		data = AccountArrayListToArray(accounts);
		DefaultTableModel model = createModel(data);
		setModel(model);
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(this.getModel());
		setRowSorter(sorter);
		sorter.addRowSorterListener(e -> {
			if (e.getType() == RowSorterEvent.Type.SORTED) setMainData();
		});
		setDefaultRenderer(Object.class, new HoverCopyRenderer());
		revealedPasswordRows.clear();
	}

	/**
	 * Updates the main Firstpass instance with the current table data.
	 */
	private void setMainData() {
		if (firstpass == null) return;
		firstpass.getAccountList().clear();
		firstpass.getAccountList().ensureCapacity(this.getRowCount());
		for (int i = 0; i < this.getRowCount(); i++)
			firstpass.getAccountList().add(new Account(
					getValueAt(i, 0).toString(),
					getValueAt(i, 1).toString(),
					getValueAt(i, 2).toString(),
					getValueAt(i, 3).toString(),
					getValueAt(i, 4).toString()
			));
		firstpass.refreshIndices();
		firstpass.setChangeMade(true);
	}

	/**
	 * Custom cell renderer that shows copy and reveal buttons on hover.
	 */
	private class HoverCopyRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
													   boolean isSelected, boolean hasFocus,
													   int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			JPanel panel = new JPanel(new BorderLayout());
			panel.setOpaque(true);
			panel.setBackground(label.getBackground());
			panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, table.getGridColor()));
			label.setOpaque(false);

			if (column == 2) {
				int modelRow = convertRowIndexToModel(row);
				String text = value == null ? "" : value.toString();
				if (!revealedPasswordRows.contains(modelRow)) {
					label.setText("⬤".repeat(Math.max(4, text.length())));
				} else {
					label.setText(text);
				}
			}


			panel.add(label, BorderLayout.CENTER);

			if (row == hoverRow && column == hoverCol) {
				JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
				buttonPanel.setOpaque(false);

				if (column == 2) {
					JButton eyeBtn = new JButton("\uD83D\uDC41");
					styleButton(eyeBtn);
					buttonPanel.add(eyeBtn);
				}

				JButton copyBtn = new JButton("⧉");
				styleButton(copyBtn);
				buttonPanel.add(copyBtn);

				panel.add(buttonPanel, BorderLayout.EAST);
			}
			return panel;
		}
	}

	/**
	 * Styles a button to be used in the cell renderer.
	 *
	 * @param btn the JButton to style
	 */
	private void styleButton(JButton btn) {
		btn.setBorderPainted(false);
		btn.setFocusPainted(false);
		btn.setMargin(new Insets(0, 0, 0, 0));
		btn.setEnabled(false); // renderer only paints; actual click handled in mousePressed
	}

	/**
	 * Copies the given value to the system clipboard and shows a toast notification.
	 *
	 * @param value the value to copy
	 */
	private void copyToClipboard(Object value) {
		String text = value == null ? "" : value.toString();
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
		Tools.showToast(SwingUtilities.getWindowAncestor(this), "Copied to clipboard", 1500, Config.getDarkMode());
	}
}