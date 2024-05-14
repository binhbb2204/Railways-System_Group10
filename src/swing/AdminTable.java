package swing;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import model.CoachType;

import java.awt.*;

public class AdminTable extends JTable{
    public AdminTable(){
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);

        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object o, boolean isSelected,
                boolean hasFocus, int row, int column) {
                TableHeader header = new TableHeader(o + "");
                
                if(column == 4){
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
                    if (column != 4) {
                        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                                row, column);
                        component.setBackground(Color.WHITE);
                        setBorder(noFocusBorder);
                        Font boldFont = component.getFont().deriveFont(Font.BOLD);
                        component.setFont(boldFont);
                        if (isSelected) {
                            component.setForeground(new Color(15, 89, 140));
                        } else {
                            component.setForeground(new Color(102, 102, 102));
                        }
                        setHorizontalAlignment(JLabel.CENTER);
                        return component;
                    } else {
                        CoachType type = (CoachType) value;
                        CellCoachType cell = new CellCoachType(type);
                        setHorizontalAlignment(JLabel.CENTER);
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
