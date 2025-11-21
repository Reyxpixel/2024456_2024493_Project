package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean clicked;

    private int currentRow;
    private JTable currentTable;
    private RowButtonClickListener listener;

    public ButtonEditor(JCheckBox checkBox, RowButtonClickListener listener) {
        super(checkBox);
        this.listener = listener;

        button = new JButton();
        button.setOpaque(true);

        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {

        label = (value == null) ? "" : value.toString();
        button.setText(label);

        clicked = true;
        currentRow = row;
        currentTable = table;

        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (clicked) {
            // notify listener with row index
            listener.onButtonClick(currentRow);
        }
        clicked = false;
        return label;
    }

    public static interface RowButtonClickListener {
        void onButtonClick(int row);
    }
}
