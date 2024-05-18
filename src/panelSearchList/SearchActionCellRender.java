package panelSearchList;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class SearchActionCellRender extends DefaultTableCellRenderer{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        SearchPanelAction action = new SearchPanelAction();
        if(isSelected == false){
            action.setBackground(Color.WHITE);
        }
        else{
            action.setBackground(com.getBackground());
        }
        
        return action;
    }
}
