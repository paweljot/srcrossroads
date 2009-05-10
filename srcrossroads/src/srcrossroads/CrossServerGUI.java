/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CrossServerGUI.java
 *
 * Created on 2009-05-10, 21:38:00
 */

package srcrossroads;

import javax.swing.JLabel;

/**
 *
 * @author paweljot
 */
public class CrossServerGUI extends javax.swing.JFrame {

    JLabel roadLabels[] = new JLabel[4];

    /** Creates new form CrossServerGUI */
    public CrossServerGUI() {
        roadLabels[0] = labelRoad0;
        roadLabels[1] = labelRoad1;
        roadLabels[2] = labelRoad2;
        roadLabels[3] = labelRoad3;
        initComponents();

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labelRoad0 = new javax.swing.JLabel();
        labelRoad1 = new javax.swing.JLabel();
        labelRoad2 = new javax.swing.JLabel();
        labelRoad3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        labelRoad0.setText("road0");

        labelRoad1.setText("road1");

        labelRoad2.setText("road2");

        labelRoad3.setText("road3");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(32, 32, 32)
                .add(labelRoad1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 231, Short.MAX_VALUE)
                .add(labelRoad2)
                .add(65, 65, 65))
            .add(layout.createSequentialGroup()
                .add(159, 159, 159)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(labelRoad3)
                    .add(labelRoad0))
                .addContainerGap(205, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(labelRoad0)
                .add(75, 75, 75)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(labelRoad1)
                    .add(labelRoad2))
                .add(73, 73, 73)
                .add(labelRoad3)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CrossServerGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel labelRoad0;
    public javax.swing.JLabel labelRoad1;
    public javax.swing.JLabel labelRoad2;
    public javax.swing.JLabel labelRoad3;
    // End of variables declaration//GEN-END:variables

}