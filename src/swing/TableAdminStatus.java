package swing;
import javax.swing.*;
import combo_suggestion.ComboSuggestionUI;
import model.AdminStatus;
import java.awt.*;

public class TableAdminStatus extends JComboBox<AdminStatus>{
    private AdminStatus adminType;
    public TableAdminStatus() {
        adminType = AdminStatus.Denied;
        setUI(new ComboSuggestionUI());

        for (AdminStatus status : AdminStatus.values()) {
            this.addItem(status);
        }

        // Optionally, set a renderer to display the enum values as you wish
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof AdminStatus) {
                    setText(((AdminStatus) value).name());
                }
                return this;
            }
        });
    }
    public AdminStatus getType(){
        return this.adminType;
    }
    public void setType(AdminStatus adminType){
        this.adminType = adminType;
        setSelectedItem(adminType);
    }

}
