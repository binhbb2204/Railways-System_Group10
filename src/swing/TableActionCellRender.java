
package swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Panel;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableActionCellRender extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        PanelAction action = new PanelAction();
        if(isSelected == false){
            action.setBackground(Color.WHITE);
        }
        else{
            action.setBackground(getBackground());
        }
        
        return action;
    }
}
