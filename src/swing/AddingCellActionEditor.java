
package swing;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;

public class AddingCellActionEditor extends DefaultCellEditor{
    private AddingActionEvent event;
    public AddingCellActionEditor(AddingActionEvent event){
        super(new JCheckBox());
        this.event = event;
    }
    
}
