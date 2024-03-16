package main;
import java.awt.Frame;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
public class AddTrain {
	
	public AddTrain(Frame oldFrame, Database database) throws SQLException {
		
		JFrame frame = new JFrame(" Add Train ");
        frame.setSize(750, 350);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setLocationRelativeTo(oldFrame);
        frame.getContentPane().setBackground(Color.decode("#EBFFD8"));
        
        
        JPanel panel = new JPanel(new GridLayout(4, 2, 20, 20));
        panel.setBackground(null);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        
        panel.add(JLabel("ID: "));
        
        JLabel id = JLabel(String.valueOf(TrainsDatabase.getNextID(database)));
        panel.add(id);
        panel.add(JLabel("Capacity"));
        JTextField capacity = new JTextField();
        panel.add(capacity);
        

        
        panel.add(JLabel("Description: "));
        
        JTextField description = JTextField();
        panel.add(description);
        
        JButton cancel = JButton("Cancel");
        panel.add(cancel);
        
        JButton submit = JButton("Submit");
        submit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Train t = new Train();
                t.setID(Integer.parseInt(id.getText()));
                t.setCapacity(Integer.parseInt(capacity.getText()));
                t.setDescription(description.getText());
                
                try {
                    TrainsDatabase.AddTrain(t, database);
//                    TrainsDatabase.AddTrain(t, database);
                    JOptionPane.showMessageDialog(frame, "Train added successfully");
                    frame.dispose();
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(frame, "Operation Failed");
                }
            }
        });
        panel.add(submit);
        
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
	}
	private JLabel JLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.decode("#012030"));
        label.setFont(new Font(null, Font.BOLD, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }
	private JTextField JTextField() {
        JTextField textField = new JTextField();
        textField.setForeground(Color.decode("#012030"));
        textField.setFont(new Font(null, Font.BOLD, 20));
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        return textField;
    }
    private JButton JButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode("#45C4B0"));
        btn.setForeground(Color.white);
        btn.setFont(new Font(null, Font.BOLD, 22));
        return btn;
    }
    

}
