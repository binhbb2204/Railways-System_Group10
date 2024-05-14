
package main;
import java.awt.*;

import javax.swing.JComponent;
import javax.swing.*;
import event.EventMenuSelected;
import form.Form_Coach;
import form.Form_CoachType;
import form.Form_Journey;
import form.Form_3;
import form.Form_Admin;
import form.Form_Schedule;
import form.Form_Seat;
import form.Form_Station;
import form.Form_Track;
import form.Form_Train;
import form.Form_Passenger;
import form.Form_Ticket;


public class Dashboard extends javax.swing.JFrame {

    private Form_Schedule schedule;
    private Form_Coach coach;
    private Form_Train train;
    private Form_3 form3;
    private Form_Passenger passenger;
    private Form_Station station;
    private Form_CoachType coachType;
    private Form_Seat seat;
    private Form_Track track;
    private Form_Ticket ticket;
    private Form_Journey journey;

    public Dashboard() {
        initComponents();
        setBackground(new Color(0, 0, 0, 0));
        schedule = new Form_Schedule();
        coach = new Form_Coach();
        train = new Form_Train();
        form3 = new Form_3();
        passenger = new Form_Passenger();
        station = new Form_Station();
        coachType = new Form_CoachType();
        seat = new Form_Seat();
        track = new Form_Track();

        ticket = new Form_Ticket();
        

        journey = new Form_Journey();
        menu.initMoving(Dashboard.this);
        menu.addEventMenuSelected(new EventMenuSelected() {
            @Override
            public void selected(int index) {
                if(index == 1){
                    setForm(train);
                }
                else if(index == 2){
                    setForm(schedule);
                }
                else if(index == 3){
                    setForm(station);
                }
                else if(index == 4){
                    setForm(coach);
                }
                else if(index == 5){
                    setForm(coachType);
                }
                else if(index == 6){
                    setForm(track);
                }
                else if(index == 7){
                    setForm(journey);
                }
                else if(index == 9){
                    setForm(seat);
                }
                else if(index == 11){
                    setForm(passenger);
                }
                else if(index == 14){
                    //System.exit(0);
                    Dashboard.this.dispose();
                    //LoginSignup loginSignup = new LoginSignup();
                    CustomerDashboard customerDashboard = new CustomerDashboard();
                    customerDashboard.setVisible(true);

                }
            }
        });
        //set the system is started/open, it starts at Schedule Form
        setForm(new Form_Schedule());
        
    }
    private void setForm(JComponent com){
        mainPanel.removeAll();
        mainPanel.add(com);
        mainPanel.repaint();
        mainPanel.revalidate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new swing.PanelBorder();
        menu = new component.Menu();
        header1 = new component.Header();
        mainPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        mainPanel.setOpaque(false);
        mainPanel.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header1, javax.swing.GroupLayout.DEFAULT_SIZE, 923, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(header1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Header header1;
    private javax.swing.JPanel mainPanel;
    private component.Menu menu;
    private swing.PanelBorder panelBorder1;
    // End of variables declaration//GEN-END:variables
}
