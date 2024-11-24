import javax.swing.*;
import java.awt.*; // Add this import for Component
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.TableCellEditor;

public class ButtonEditor extends DefaultCellEditor {

    private String label;
    private JButton button;
    private boolean isPushed;
    private int row;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
                isPushed = true;
            }
        });
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
            isPushed = false;
        }
        return label;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.row = row;
        if (value == null) {
            button.setText("");
        } else {
            button.setText("Remove");
        }
        return button;
    }
}
