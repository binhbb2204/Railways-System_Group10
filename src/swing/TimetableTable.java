package swing;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TimetableTable extends JTable{
    public TimetableTable(){
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);

        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                TableHeader header = new TableHeader(value + "");
                
                header.setHorizontalAlignment(JLabel.CENTER);
                
                return header;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                try{
                    
                    Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                row, column);
                    component.setBackground(Color.WHITE);
                    setBorder(noFocusBorder);
                    
                    if (isSelected) {
                        component.setForeground(new Color(15, 89, 140));
                    } 
                    else {
                        component.setForeground(new Color(102, 102, 102));
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    return component;
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
