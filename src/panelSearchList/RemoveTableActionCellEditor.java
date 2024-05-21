
package panelSearchList;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;

public class RemoveTableActionCellEditor extends DefaultCellEditor{
    private RemoveTableActionEvent event;
    public RemoveTableActionCellEditor(RemoveTableActionEvent event){
        super(new JCheckBox());
        this.event = event;
    }
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        RemoveSearchPanelAction action = new RemoveSearchPanelAction();
        action.initEvent(event, row);
        action.setBackground(table.getSelectionBackground());
        return action;
    }
}
