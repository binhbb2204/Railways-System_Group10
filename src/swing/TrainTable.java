package swing;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import model.TrainType;

public class TrainTable extends JTable {
    public TrainTable() {
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);

        // DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        // centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // // Apply the default renderer to the header and all cell types
        // getTableHeader().setDefaultRenderer(centerRenderer);
        // setDefaultRenderer(Object.class, centerRenderer);
        // setDefaultRenderer(TrainType.class, centerRenderer);

        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                TableHeader header = new TableHeader(value + "");
                if (column == 3) {
                    header.setHorizontalAlignment(JLabel.CENTER);
                }
                return header;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                try{
                    if (column != 3) {
                        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                row, column);
                        component.setBackground(Color.WHITE);
                        setBorder(noFocusBorder);
                        if (isSelected) {
                            component.setForeground(new Color(15, 89, 140));
                        } else {
                            component.setForeground(new Color(102, 102, 102));
                        }
                        setHorizontalAlignment(JLabel.CENTER);
                        return component;
                    } else {
                        TrainType type = (TrainType) value;
                        CellType cell = new CellType(type);
        
                        return cell;
                    }
                }
                catch (Exception e) {
                    // Handle rendering exception
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                    row, column);
                }
            
            }
        });
    }

    public void addRow(Object[] row) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.addRow(row);
    }
}
