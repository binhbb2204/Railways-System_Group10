/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
/**
 *
 * @author binhb
 */
public class Main {
    
//     private static JFrame frame;
//     private static JPanel table;
//     private static GridLayout gridLayout;
    
//     public static void main(String[] args) throws SQLException {
//         Database database = new Database();
//         frame = new JFrame("Railway Management System");

//         frame.setSize(1050, 650);
//         frame.getContentPane().setLayout(new BorderLayout());
//         frame.getContentPane().setBackground(Color.decode("#EBFFD8"));
//         frame.setLocationRelativeTo(null);
        
//         JPanel panel = new JPanel(new BorderLayout(20, 20));
//         panel.setBackground(null);
//         panel.setBorder(BorderFactory.createEmptyBorder(50, 50 , 30, 40));
        
//         JLabel title = new JLabel("Welcome to Railway Management System");
//         title.setForeground(Color.decode("#012030"));
//         title.setFont(new Font(null, Font.BOLD, 35));
//         title.setHorizontalAlignment(SwingConstants.CENTER);
//         panel.add(title, BorderLayout.NORTH);
        
//         gridLayout = new GridLayout(6, 1);
//         table = new JPanel(gridLayout);
//         table.setBackground(Color.decode("#EBFFD8"));
        
//         ArrayList<Trip> trips = new ArrayList<>();
//         for(int i = 0; i < 8; i++){
//             Trip t = new Trip();
//             t.setArrivalTime("00:00");
//             t.setDepartureTime("00:00");
//             t.setDate("2023-03-15");
//             t.setDestination("Destination");
//             t.setStart("Start");
//             Train tr = new Train();
//             tr.setDescription("Train 1 VIP");
//             t.setTrain(tr);
//             t.setPrice(50);
//             trips.add(t);
//         }
//         refreshTable(trips);
        
//         JScrollPane sp = new JScrollPane(table);
//         panel.add(sp, BorderLayout.CENTER);
//         JButton modify = new JButton("Modify");
//         modify.setBackground(Color.decode("#45C4B0"));
//         modify.setForeground(Color.white);
//         modify.setFont(new Font(null, Font.BOLD, 22));
//         modify.addActionListener(new ActionListener() {
//             @Override
//             public void actionPerformed(ActionEvent e){
//                  new ModifyList(frame, database);
// //                try {
// //                    new AddTrain(oldFrame, database);
// //                } catch (SQLException e1) {
// //                    // TODO: handle exception
// //                    e1.printStackTrace();
// //                }
//             }
            
//         });
//         panel.add(modify, BorderLayout.SOUTH);
        
//         frame.add(panel, BorderLayout.CENTER );
//         frame.setVisible(true);
//         /* Set the Nimbus look and feel */
//         //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//         /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//          * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//          */
//         try {
//             for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                 if ("Nimbus".equals(info.getName())) {
//                     javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                     break;
//                 }
//             }
//         } catch (ClassNotFoundException ex) {
//             java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//         } catch (InstantiationException ex) {
//             java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//         } catch (IllegalAccessException ex) {
//             java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//         } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//             java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//         }
//     }
    
//     private static void refreshTable(ArrayList<Trip> trips){
//         int rows = trips.size() + 1;
//         if (rows < 6) rows = 6;
//         gridLayout.setRows(rows);
//         table.add(row(0, null));
//         for(int i  = 0; i < trips.size(); i++){
//             JPanel trip = row(i + 1, trips.get(i));
//             table.add(trip);
//         }
//     } 
    
//     private static JPanel row(int index, Trip trip){
//         JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
//         if(index % 2 == 0) row.setBackground(Color.decode("#e5e5e5"));
//         else row.setBackground(Color.decode("#EEEEEE"));
        
//         String trainS, startS, destS, dateS, deptS, arrS, priceS, statusS;
        
//         if(trip != null){
//             trainS = trip.getTrain().getDescription();
//             startS = trip.getStart();
//             destS = trip.getDestination();
//             dateS = trip.getDate();
//             deptS = trip.getDepartureTime();
//             arrS = trip.getArrivalTime();
//             priceS = trip.getPrice() + "$"; 
//             statusS = "Booked";
//             if(trip.getTrain().getCapacity() > trip.getBookedSeats()) statusS = "Available";
//             row.setCursor(new Cursor(Cursor.HAND_CURSOR));
//         } else {
//             trainS = "Train";
//             startS = "From";
//             destS = "To";
//             dateS = "Date";
//             deptS = "Dept";
//             arrS = "Arr";
//             priceS = "Price";
//             statusS = "Status";
//         }
               
//         JLabel train = JLabel(trainS, 100);
//         row.add(train);
        
//         JLabel start = JLabel(startS, 100);
//         row.add(start);
        
//         JLabel dest = JLabel(destS, 100);
//         row.add(dest);
        
//         JLabel date = JLabel(dateS, 150);
//         row.add(date);
        
//         JLabel deptTime = JLabel(deptS, 65);
//         row.add(deptTime);
        
//         JLabel arrTime = JLabel(arrS, 65);
//         row.add(arrTime);
        
//         JLabel price = JLabel(priceS, 60);
//         row.add(price);
     
//         JLabel status = JLabel(statusS, 100);
//         row.add(status);
          
//         return row;
//     }
    
//     private static JLabel JLabel(String text,int width){
//         JLabel label = new JLabel(text);
//         label.setPreferredSize(new Dimension(width, 20));
//         label.setFont(new Font(null, Font.PLAIN, 20));
//         label.setForeground(Color.decode("#13678A"));
//         label.setHorizontalAlignment(SwingConstants.CENTER);
//         return label;
//     }
}
