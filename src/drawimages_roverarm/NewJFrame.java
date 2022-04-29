/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package drawimages_roverarm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
//import java.awt.event.KeyEvent;
//import javax.swing.KeyStroke;
import java.awt.event.MouseEvent;
import static java.lang.Math.max;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NewJFrame extends javax.swing.JFrame {
    int mouseX = 0;
    int mouseY = 0;
    int countSteps = 0;
    double angleRobotArm = 1;
    
    int[] startX = new int[8];  // count starts at 0.  rover 0 + wheels 1-4 + first link of arm 5 + second link of arm 6 + robotArm straight line from base to end effector 7
    int[] startY = new int[8];  // count starts at 0.  rover 0 + wheels 1-4 + first link of arm 5 + second link of arm 6
    int deviceNumber = 0;       // count starts at 0.  rover 0 + wheels 1-4 + first link of arm 5 + second link of arm 6
    int[] endX = new int[8];
    int[] endY = new int[8];
    
    double[] linkLength = new double[8];
    
    Point mousePos = null;
    
    double slopeRobotArm = 0;
            
    double deltaX = 0;
    double deltaY = 0;
    double hypotenuse = 0;
    double steps = 20;
    double incrementalStepSizeX = 1;
    double incrementalStepSizeY = 1;
    double targetX = 1;
    double targetY = 1;
    Graphics g=null;
    Graphics2D g2 = (Graphics2D) g;
    Rectangle bodyRover = null;
    Rectangle bodyArm1 = null;
        
    public NewJFrame() {
        initComponents();     
    }
        
     public void paint(Graphics g) {
                  
        super.paint(g);
        g2 = (Graphics2D) g;
                
        if(jTabbedPane1.getSelectedIndex()==1){ // show robot arm only when this tab is visible
            deviceNumber = 7;
            
            // start offset - how far from 0 0 is the starting point?
            int startX_Offset = 170;
            int startY_Offset = 100;
            
            startX_Offset = Integer.parseInt(jTextField8.getText());
            startY_Offset = Integer.parseInt(jTextField13.getText());;

            startX[deviceNumber] = 0+startX_Offset;
            startY[deviceNumber] = 0+startY_Offset;
            endX[deviceNumber] = mouseX;
            endY[deviceNumber] = mouseY;

            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(startX[deviceNumber], startY[deviceNumber], mouseX, mouseY); // straight line from base to end effector
            
            g2.setColor(Color.GREEN);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(mouseX-1200, mouseY, 4800, 20); // this looks like 2 lines on the screen but is actually a rectangle box

            deltaX = (startX[deviceNumber]-mouseX);
            deltaY = (startY[deviceNumber]-mouseY);
            hypotenuse = Math.pow((Math.pow(deltaX, 2) + Math.pow(deltaY,2)), 0.5);

            linkLength[0] = 375;
            linkLength[0] = Integer.parseInt(jTextField10.getText());
            linkLength[1] = 300;
            linkLength[1] = Integer.parseInt(jTextField11.getText());

            // circle showing full range of arm:
                g2.drawOval(startX[deviceNumber]-(int)(linkLength[0]+linkLength[1]), startY[deviceNumber]-(int)(linkLength[0]+linkLength[1]), (int) (linkLength[0]+linkLength[1])*2, (int) (linkLength[0]+linkLength[1])*2);

             jTextField5.setBackground(Color.green);
             jButton6.setBackground(Color.green);
             jButton6.setText("");
             if (hypotenuse > ((linkLength[0]+linkLength[1])*0.9)){
                jTextField5.setBackground(Color.yellow);
                jButton6.setBackground(Color.yellow);
                jButton6.setText("Arms almost too short to reach!");
             }
             if (hypotenuse > (linkLength[0]+linkLength[1])){
                jTextField5.setBackground(Color.red);
                jButton6.setBackground(Color.red);
                jButton6.setText("Arms too short to reach!");
             }
             if (hypotenuse < ((linkLength[0]-linkLength[1]))*1.45){
                jTextField5.setBackground(Color.yellow);
                jButton6.setBackground(Color.yellow);
                jButton6.setText("Arm 'A' almost too long relative to Arm 'B' to reach this point!");
             }
             if (hypotenuse < ((linkLength[0]-linkLength[1]))){
                jTextField5.setBackground(Color.red);
                jButton6.setBackground(Color.red);
                jButton6.setText("Arm 'A' too long relative to Arm 'B' to reach this point!");
             }
                
            if(deltaX==0) {
                slopeRobotArm = 1;
            }
            else
            {
                slopeRobotArm = (deltaY)/(deltaX);
                angleRobotArm = Math.toDegrees(Math.atan(slopeRobotArm));
            }

            double step1 = Math.toDegrees(Math.asin((mouseX-startX_Offset)/hypotenuse))+15; // has to be decreased by the same amount as the base is from 0 0
            jTextField20.setText("step1 "+String.format("%.1f",step1)+ " angleRobotArm "+String.format("%.1f",angleRobotArm));
            
            double step2 = Math.toDegrees(Math.acos((Math.pow(hypotenuse,2) + Math.pow(linkLength[0],2) - Math.pow(linkLength[1],2) )/(2*hypotenuse*linkLength[0])))+15;
            //double step2 = (Math.pow(hypotenuse,2) + Math.pow(linkLength[0],2) - Math.pow(linkLength[1],2) )/(2*hypotenuse*linkLength[0]);

            double step3 = step1-step2;  // angle of arm relative to straight line to end effector

            double step4 = Math.sin(Math.toRadians(step3))*linkLength[0]+startX_Offset;  // this has to be offset by the same amount as the base is from 0 0 

            double step5 = Math.pow((Math.pow(linkLength[0],2)-Math.pow(step4-startX_Offset,2)),0.5)+startY_Offset;  // this has to be offset by the same amount as the base is from 0 0 

            int labelX_hyp = (int) endX[deviceNumber]+(int)deltaX/2+4;
            int labelY_hyp = (int) endY[deviceNumber]+(int)deltaY/2+4;
            g2.drawString("step1 "+String.format("%.1f", step1)+" step2 "+String.format("%.1f", step2)+" step3 "+String.format("%.1f", step3)+" step4 "+String.format("%.1f", step4)+" radius "+String.format("%.1f", hypotenuse)+" startX "+startX[deviceNumber] +" startY "+startY[deviceNumber]+" endX "+endX[deviceNumber] +" endY "+endY[deviceNumber]+" slope "+ slopeRobotArm+" angle "+String.format("%.1f",Math.toDegrees(Math.atan(slopeRobotArm))), labelX_hyp, labelY_hyp-10);
            
            // first link of arm from base to elbow
                deviceNumber = 5;
                g2.setColor(Color.GREEN);
                g2.setStroke(new BasicStroke(2));
                  
                jTextField5.setText("elbow x = "+String.format("%.1f",  step4)+"");
                jTextField6.setText("elbow y = "+String.format("%.1f",  step5)+" step5 endY link 0");
                
                endX[deviceNumber] = (int)step4;
                endY[deviceNumber] = (int)step5;
                
                g2.drawLine(startX[7], startY[7], endX[deviceNumber], endY[deviceNumber]); // start of straight line (device 7) is also the start of the first link
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("TimesRoman", Font.BOLD, 16)); 
                g2.drawString("elbow x "+endX[deviceNumber]+" y "+endY[deviceNumber], endX[deviceNumber], endY[deviceNumber]);
                
                deltaX = (startX[7]-endX[deviceNumber]);
                deltaY = (startY[7]-endY[deviceNumber]);
                hypotenuse = Math.pow((Math.pow(deltaX, 2) + Math.pow(deltaY,2)), 0.5);
                jTextField7.setText(String.format("%.1f",  hypotenuse)+" length of base to elbow");
                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(1));
            
            // second link of arm from elbow to end effector
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(endX[5], endY[5], mouseX, mouseY); // end of first link is start of second link; end of second link is also the end of the straight line (device 7)
                deltaX = (endX[5]-mouseX);
                deltaY = (endY[5]-mouseY);
                hypotenuse = Math.pow((Math.pow(deltaX, 2) + Math.pow(deltaY,2)), 0.5);
                jTextField9.setText(String.format("%.1f",  hypotenuse)+" length of elbow to end effector");
                g2.drawOval(endX[5]-(int)hypotenuse, endY[5]-(int)hypotenuse, (int) hypotenuse*2, (int) hypotenuse*2);
             
                g2.drawString("x "+mouseX+" y "+mouseY, mouseX, mouseY);
        }
        
        repaint();        
        countSteps = countSteps + 1;
        try {
            sleep(140);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        PanelRoverSteering = new javax.swing.JPanel();
        PanelRobotArm = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField20 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPane1.setForeground(new java.awt.Color(255, 153, 51));

        javax.swing.GroupLayout PanelRoverSteeringLayout = new javax.swing.GroupLayout(PanelRoverSteering);
        PanelRoverSteering.setLayout(PanelRoverSteeringLayout);
        PanelRoverSteeringLayout.setHorizontalGroup(
            PanelRoverSteeringLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1419, Short.MAX_VALUE)
        );
        PanelRoverSteeringLayout.setVerticalGroup(
            PanelRoverSteeringLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 884, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Steering Mode", PanelRoverSteering);

        PanelRobotArm.setMinimumSize(new java.awt.Dimension(1, 1));

        jPanel4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(255, 0, 0), java.awt.Color.red));
        jPanel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                jPanel4MouseMoved(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 956, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 730, Short.MAX_VALUE)
        );

        jTextField5.setText("jTextField5");

        jTextField6.setText("jTextField5");

        jTextField7.setText("jTextField5");

        jTextField9.setText("jTextField5");

        jTextField10.setText("350");

        jTextField11.setText("300");

        jButton6.setText("jButton6");

        jLabel3.setText("Arm 2 Length (Elbow to End)");

        jLabel4.setText("Arm 1 Length (Base to Elbow)");

        jTextField8.setText("170");

        jTextField13.setText("100");

        jLabel5.setText("Base X position");

        jLabel6.setText("Base Y position");

        jTextField20.setText("jTextField20");

        jButton1.setText("jButton1");

        javax.swing.GroupLayout PanelRobotArmLayout = new javax.swing.GroupLayout(PanelRobotArm);
        PanelRobotArm.setLayout(PanelRobotArmLayout);
        PanelRobotArmLayout.setHorizontalGroup(
            PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRobotArmLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelRobotArmLayout.createSequentialGroup()
                        .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelRobotArmLayout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelRobotArmLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 164, Short.MAX_VALUE)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(PanelRobotArmLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelRobotArmLayout.createSequentialGroup()
                                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(PanelRobotArmLayout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(85, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelRobotArmLayout.createSequentialGroup()
                                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelRobotArmLayout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PanelRobotArmLayout.createSequentialGroup()
                                        .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel6))
                                        .addGap(105, 105, 105)
                                        .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(jTextField20, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRobotArmLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(611, 611, 611))
        );
        PanelRobotArmLayout.setVerticalGroup(
            PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRobotArmLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(25, 25, 25)
                .addGroup(PanelRobotArmLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(jTextField20, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(PanelRobotArmLayout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton1)
                .addGap(0, 97, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Robot Arm", PanelRobotArm);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1428, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(78, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 851, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel4MouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel4MouseMoved
        try {
            sleep(0);
        } catch (InterruptedException ex) {
            Logger.getLogger(NewJFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        mousePos = jPanel4.getMousePosition();
        if(mousePos != null){
            mouseX = max(mousePos.x,1);
            mouseY = max(mousePos.y,1);
        }
        else
        {
            mouseX=1;
            mouseY=1;
        }
    }//GEN-LAST:event_jPanel4MouseMoved

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewJFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel PanelRobotArm;
    private javax.swing.JPanel PanelRoverSteering;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField20;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    // End of variables declaration//GEN-END:variables
}
