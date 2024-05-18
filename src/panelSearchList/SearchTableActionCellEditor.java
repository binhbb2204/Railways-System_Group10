
package panelSearchList;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class SearchTableActionCellEditor extends DefaultCellEditor{
    private SearchTableActionEvent event;

    public SearchTableActionCellEditor(SearchTableActionEvent event){
        super(new JCheckBox());
        this.event = event;
    }
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        SearchPanelAction action = new SearchPanelAction();
        action.initEvent(event, row);
        action.setBackground(table.getSelectionForeground());
        return action;
    }
}
