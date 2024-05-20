
package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.sql.SQLException;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;

import component.Message;
import component.PanelCover;
import component.PanelLoading;
import component.PanelLoginAndRegister;
import component.PanelVerifyCode;
import net.miginfocom.swing.MigLayout;
import service.ServiceMail;
import service.ServiceUser;
import model.ModelLogin;
import model.ModelMessage;
import model.ModelUser;
import connection.DatabaseConnection;

public class LoginSignup extends javax.swing.JFrame {
    private MigLayout layout;
    private PanelCover cover;
    private PanelLoginAndRegister loginAndRegister;
    private PanelLoading loading;
    private PanelVerifyCode verifyCode;
    private ServiceUser service;
    private boolean isLogin;
    private final double addSize = 30;
    private final double coverSize = 40;
    private final double loginSize = 60;

    private final DecimalFormat df = new DecimalFormat("##0.###");

    public LoginSignup() {
        initComponents();
        init();
    }

    private void init(){
        layout = new MigLayout("fill, insets 0");
        cover = new PanelCover();
        loading = new PanelLoading();
        verifyCode = new PanelVerifyCode();
        service = new ServiceUser();
        ActionListener eventRegister = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };
        ActionListener eventLogin = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        };
        loginAndRegister = new PanelLoginAndRegister(eventRegister, eventLogin);
        TimingTarget target = new TimingTargetAdapter(){
            @Override
            public void timingEvent(float fraction) {
                double fractionCover;
                double fractionLogin;
                double size = coverSize;
                if(fraction <= 0.5f){
                    size += fraction * addSize;
                }
                else{
                    size += addSize - fraction*addSize;
                }
                if(isLogin){
                    fractionCover = 1f - fraction;
                    fractionLogin = fraction;
                    if(fraction >= 0.5f){
                        cover.registerRight(fractionCover * 100);
                    }
                    else{
                        cover.loginRight(fractionLogin * 100);
                    }
                }
                else{
                    fractionCover = fraction;
                    fractionLogin = 1f - fraction;
                    if(fraction <= 0.5f){
                        cover.registerLeft(fraction * 100);
                    }
                    else{
                        cover.loginLeft((1f - fraction) * 100);
                    }
                }
                if(fraction >= 0.5f){
                    loginAndRegister.showRegister(isLogin);
                }
                fractionCover = Double.valueOf(df.format(fractionCover));
                fractionLogin = Double.valueOf(df.format(fractionLogin));
                layout.setComponentConstraints(cover, "width "+ size +"%, pos "+ fractionCover +"al 0 n 100%");
                layout.setComponentConstraints(loginAndRegister, "width "+ loginSize +"%, pos "+ fractionLogin +"al 0 n 100%");
                bg.revalidate();
            }
            @Override
            public void end() {
                isLogin = !isLogin;
            }
        };
        Animator animator = new Animator(1000, target);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.setResolution(0); // this is for the smooth movement of transition
        bg.setLayout(layout);
        bg.setLayer(loading, JLayeredPane.POPUP_LAYER);
        bg.setLayer(verifyCode, JLayeredPane.POPUP_LAYER);
        bg.add(loading, "pos 0 0 100% 100%");
        bg.add(verifyCode, "pos 0 0 100% 100%");
        bg.add(cover, "width "+ coverSize + "%, pos 0al 0 n 100%");
        bg.add(loginAndRegister, "width "+ loginSize +"%, pos 1al 0 n 100%");
        cover.addEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!animator.isRunning()){
                    animator.start();
                }
            }
        });
        verifyCode.addEventButtonOK(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try{
                    ModelUser user = loginAndRegister.getUser();
                    if(service.verifyCodeWithUser(user.getUserID(), verifyCode.getInputCode())){
                        // service.doneVerify(user.getUserID());
                        showMessage(Message.MessageType.SUCCESS, "Congratulations! You’ve successfully registered. Welcome aboard!");
                        verifyCode.setVisible(false);
                    }
                    else{
                        showMessage(Message.MessageType.ERROR, "Invalid verification code. Please try again.");
                    }
                }
                catch(SQLException e){
                    showMessage(Message.MessageType.ERROR, "Error");
                }
            }
        });
    }

    public void register() {
        ModelUser user = loginAndRegister.getUser();
        String userName = user.getUserName().trim();
        String email = user.getEmail().trim();
        String password = user.getPassword().trim();

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showMessage(Message.MessageType.ERROR, "Please fill in all required fields.");
        return; // Exit early if any field is empty
        }
        try {
            if (service.checkDuplicateUser(user.getUserName())) {
                showMessage(Message.MessageType.ERROR, "Username already taken. Please choose another.");
            } 
            else if (service.checkDuplicateEmail(user.getEmail())) {
                showMessage(Message.MessageType.ERROR, "Email already in use. Please try another.");
            } 
            else {
                service.insertUser(user);
                loading.setVisible(true);
                new Thread(() -> {
                    try {
                        // Wait for 3 to 5 seconds
                        Thread.sleep(3000 + new Random().nextInt(2000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Hide loading and show success message on the Swing event dispatch thread
                    SwingUtilities.invokeLater(() -> {
                    loading.setVisible(false);
                    showMessage(Message.MessageType.SUCCESS, "Congratulations! Your account has been created successfully.");
                    });
                }).start();
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            showMessage(Message.MessageType.ERROR, "Registration Error: Please check your details and try again.");
        }
    }

    private void login(){
        ModelLogin data = loginAndRegister.getDataLogin();
        try{
            ModelUser user = service.login(data);
            if(user != null){
               
                Dashboard dashboard = new Dashboard();
                loading.setVisible(true);
                new Thread(() -> {
                    try {
                        // Wait for 3 to 5 seconds
                        Thread.sleep(3000 + new Random().nextInt(2000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Hide loading and show success message on the Swing event dispatch thread
                    SwingUtilities.invokeLater(() -> {
                    loading.setVisible(false);
                    this.dispose();
                    dashboard.setVisible(true);
                    });
                }).start();
                
                
            }
            else{
                showMessage(Message.MessageType.ERROR, "Access denied. Ensure your login information is correct.");
            }
        }
        catch(Exception e){
            showMessage(Message.MessageType.ERROR, "Login error. Double-check your credentials.");
            e.printStackTrace();
        }

    }
    private void sendMain(ModelUser user){
        new Thread(new Runnable() {
            @Override
            public void run() {
                loading.setVisible(true);
                ModelUser user = loginAndRegister.getUser();
                ModelUser password = loginAndRegister.getUser();
                ModelMessage ms = new ServiceMail(user.getUserName(), password.getPassword()).sendMain(user.getEmail(), user.getVerifyCode());
                
                if(ms.isSuccess()){
                    loading.setVisible(false);
                    verifyCode.setVisible(true);
                }
                else{
                    loading.setVisible(false);
                    showMessage(Message.MessageType.ERROR, ms.getMessage());
                }
                
            }
        }).start();
    }
    private void showMessage(Message.MessageType messageType, String message){
        Message ms = new Message();
        ms.showMessage(messageType, message);
        TimingTarget target = new TimingTargetAdapter(){
            @Override
            public void begin() {
                if(!ms.isShow()){
                    bg.add(ms, "pos 0.5al -30", 0); //  Insert to bg fist index 0
                    ms.setVisible(true);
                    bg.repaint();
                }
            }
            @Override
            public void timingEvent(float fraction) {
                float f;
                if (ms.isShow()) {
                    f = 40 * (1f - fraction);
                } else {
                    f = 40 * fraction;
                }
                layout.setComponentConstraints(ms, "pos 0.5al " + (int) (f - 30));
                bg.repaint();
                bg.revalidate();
            }
            @Override
            public void end() {
                if (ms.isShow()) {
                    bg.remove(ms);
                    bg.repaint();
                    bg.revalidate();
                } else {
                    ms.setShow(true);
                }
            }
        };
        Animator animator = new Animator(300, target);
        animator.setResolution(0);
        animator.setAcceleration(0.5f);
        animator.setDeceleration(0.5f);
        animator.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    animator.start();
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }
        }).start();
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setBackground(new java.awt.Color(255, 255, 255));
        bg.setOpaque(true);

        javax.swing.GroupLayout bgLayout = new javax.swing.GroupLayout(bg);
        bg.setLayout(bgLayout);
        bgLayout.setHorizontalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1020, Short.MAX_VALUE)
        );
        bgLayout.setVerticalGroup(
            bgLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 537, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    // public static void main(String args[]) {
    //     /* Set the Nimbus look and feel */
    //     //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
    //     /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
    //      * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
    //      */
    //     try {
    //         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
    //             if ("Nimbus".equals(info.getName())) {
    //                 javax.swing.UIManager.setLookAndFeel(info.getClassName());
    //                 break;
    //             }
    //         }
    //     } catch (ClassNotFoundException ex) {
    //         java.util.logging.Logger.getLogger(LoginSignup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    //     } catch (InstantiationException ex) {
    //         java.util.logging.Logger.getLogger(LoginSignup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    //     } catch (IllegalAccessException ex) {
    //         java.util.logging.Logger.getLogger(LoginSignup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    //     } catch (javax.swing.UnsupportedLookAndFeelException ex) {
    //         java.util.logging.Logger.getLogger(LoginSignup.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
    //     }
    //     //</editor-fold>
    //     try {
    //         DatabaseConnection.getInstance().connectToDatabase();
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //     }
    //     /* Create and display the form */
    //     java.awt.EventQueue.invokeLater(new Runnable() {
    //         public void run() {
    //             new LoginSignup().setVisible(true);
    //         }
    //     });
    // }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane bg;
    // End of variables declaration//GEN-END:variables
}
