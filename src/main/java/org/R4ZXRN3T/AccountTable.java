package org.R4ZXRN3T;

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
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AccountTable extends JTable {

	private final String[] columns = {"Provider", "Username", "Password", "URL", "Comment"};
	private String[][] data;
	private Firstpass firstpass;
	private int hoverRow = -1;
	private int hoverCol = -1;

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
			public void mouseExited(MouseEvent e) {
				hoverRow = -1;
				hoverCol = -1;
				repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				Point p = e.getPoint();
				int r = rowAtPoint(p);
				int c = columnAtPoint(p);

				// If clicked inside a valid cell, check if the click is within the "button" area (right edge)
				if (r != -1 && c != -1) {
					Rectangle cellRect = getCellRect(r, c, false);
					int buttonWidth = 20; // pixels reserved for the fake button area
					if (p.x >= cellRect.x + cellRect.width - buttonWidth) {
						copyToClipboard(getValueAt(r, c));
						return;
					}
				}

				// existing double-click edit behavior
				if (e.getClickCount() == 2 && getSelectedRow() >= 0 && firstpass != null) {
					firstpass.editAccount(getSelectedRow());
				}
			}
		});
	}

	private DefaultTableModel createModel(String[][] tableData) {
		return new DefaultTableModel(tableData, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
	}

	private static String[][] AccountArrayListToArray(ArrayList<Account> inputArrayList) {
		String[][] finalArray = new String[inputArrayList.size()][5];
		for (int i = 0; i < inputArrayList.size(); i++) finalArray[i] = inputArrayList.get(i).toArray();
		return finalArray;
	}

	public void setMain(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	public JScrollPane getScrollPane() {
		JScrollPane scrollPane = new JScrollPane(this);
		scrollPane.setFocusable(true);
		scrollPane.setRequestFocusEnabled(true);
		Border roundedBorder = new LineBorder(this.getBackground(), 8, true);
		scrollPane.setBorder(roundedBorder);
		return scrollPane;
	}

	public boolean isRowSelected() {
		return getSelectedRow() != -1;
	}

	public void addRowSelectionListener(ListSelectionListener listener) {
		getSelectionModel().addListSelectionListener(listener);
	}

	public boolean isFocused() {
		return this.isFocusOwner();
	}

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
	}

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

	// file: 'src/main/java/org/R4ZXRN3T/AccountTable.java'
	private class HoverCopyRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			JPanel panel = new JPanel(new BorderLayout());
			panel.setOpaque(true);
			panel.setBackground(label.getBackground());
			panel.add(label, BorderLayout.CENTER);

			if (row == hoverRow && column == hoverCol) {
				JButton btn = new JButton("⧉");
				btn.setBorderPainted(false);
				btn.setFocusPainted(true);
				btn.setMargin(new Insets(0, 0, 0, 0));
				btn.addActionListener(_ -> copyToClipboard(value));
				panel.add(btn, BorderLayout.EAST);
			}
			return panel;
		}
	}

	private void copyToClipboard(Object value) {
		String text = value == null ? "" : value.toString();
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
		Tools.showToast(SwingUtilities.getWindowAncestor(this), "Copied to clipboard", 1500, Config.getDarkMode());
	}
}