
package swing;

import java.awt.Color;
import java.awt.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.table.*;

import model.StatusType;

public class ScheduleTable extends JTable {

    public ScheduleTable() {
        setShowHorizontalLines(true);
        setGridColor(new Color(230, 230, 230));
        setRowHeight(40);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object o, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                TableHeader header = new TableHeader(o + "");
                if (column == 6) {
                    header.setHorizontalAlignment(JLabel.CENTER);
                }
                return header;
            }
        });

        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean selected,
                    boolean bln1, int i, int i1) {
                try {
                    if (i1 != 6) {
                        Component com = super.getTableCellRendererComponent(jtable, o, selected, bln1, i, i1);
                        com.setBackground(Color.WHITE);
                        setBorder(noFocusBorder);
                        if (selected) {
                            com.setForeground(new Color(15, 89, 140));
                        } else {
                            com.setForeground(new Color(102, 102, 102));
                        }
                        return com;
                    } 
                    else {
                        StatusType type = (StatusType) o;
                        CellStatus cell = new CellStatus(type);
                        
                        return cell;
                        
                    }
                } catch (Exception e) {
                    // Handle rendering exception
                    return super.getTableCellRendererComponent(jtable, o, selected, bln1, i, i1);
                }
            }
        });

        // Set combo box for column 6
        
    }

    public void addRow(Object[] row) {
        DefaultTableModel model = (DefaultTableModel) getModel();
        model.addRow(row);
    }

    public void saveToDatabase() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Step 1: Establish a connection
            conn = DriverManager.getConnection("jdbc:mysql://myDB", "root", "12342204");

            // Step 2: Create a SQL statement string
            String sql = "INSERT INTO train_schedule (Train, Origin, Destination, DepartureTime, ArrivalTime, DayOperation, Status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
            // Step 3: Prepare the statement
            pstmt = conn.prepareStatement(sql);

            // Step 4: Set the values from each row of the table's model
            for (int i = 0; i < getModel().getRowCount(); i++) {
            pstmt.setObject(1, getModel().getValueAt(i, 0));
            pstmt.setObject(2, getModel().getValueAt(i, 1));
            pstmt.setObject(3, getModel().getValueAt(i, 2));
            pstmt.setObject(4, getModel().getValueAt(i, 3));
            pstmt.setObject(5, getModel().getValueAt(i, 4));
            pstmt.setObject(6, getModel().getValueAt(i, 5));
            pstmt.setObject(7, getModel().getValueAt(i, 6));
            
            // Step 5: Execute the statement
            pstmt.executeUpdate();
        }
        
            // Step 6: Commit if all inserts are successful
            conn.commit();
        } 
        catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    // Rollback any changes if there was an exception
                    conn.rollback();
                } 
                catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        } 
        finally {
            // Step 7: Close resources
            if (pstmt != null) {
                try {
                    pstmt.close();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } 
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
