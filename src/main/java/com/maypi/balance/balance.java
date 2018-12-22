/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.balance;

import com.fazecast.jSerialComm.SerialPort;
import com.sun.glass.events.KeyEvent;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import static javax.swing.JFileChooser.SAVE_DIALOG;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


/**
 *
 * @author Roycer
 */
public class balance extends javax.swing.JFrame {

    ArrayList<Weight> arrayWeight = new ArrayList();
    String tempDir = System.getProperty("java.io.tmpdir");
    Boolean b_jButton_connect;
    int nro = 0;
    /**
     * Creates new form balance
     */
    public balance() {
        initComponents();
        
        Image i;
        try {
            i = ImageIO.read(getClass().getResource("/logo.png"));
            this.setIconImage(i);
        } catch (IOException ex) {
            System.out.println("error image");
            Logger.getLogger(balance.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        DefaultTableModel model = (DefaultTableModel) jTable_weight.getModel();

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(tempDir+"balance.bin"));
            arrayWeight = (ArrayList<Weight>)objectInputStream.readObject();
            objectInputStream.close();
            if(arrayWeight.size() != 0){
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog (null, "Desea cargar registro temporal?","Warning",dialogButton);
                if(dialogResult == JOptionPane.YES_OPTION){
                    for (Weight w : arrayWeight){
                        model.addRow(new Object[]{w.nro, w.weight, w.unit, w.observation});
                        nro = w.nro;
                    }
                }else{
                    arrayWeight.clear();
                }
            }
            
        } catch (IOException e) {
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(balance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.loadport();
        this.jButton_load.setMnemonic(KeyEvent.VK_F9);
        this.jButton_download.setMnemonic(KeyEvent.VK_F12);
        this.b_jButton_connect = true;
        jTable_weight.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTable_weight.getColumnModel().getColumn(1).setPreferredWidth(7);
        jTable_weight.getColumnModel().getColumn(2).setPreferredWidth(7);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_weight.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        jTable_weight.setDefaultRenderer(String.class, centerRenderer);
    }
    
     public void response(String data){
         jTextField_weight.setText(data);
     }
     
     private void loadport(){
        jComboBox_port.removeAllItems();
        jComboBox_port.addItem("");
        for(SerialPort i : SerialPort.getCommPorts()){
            jComboBox_port.addItem(i.getSystemPortName());
        }
     }
     
     private void loadreg(){
        nro++;
        String weight = "";
        String unit = "";
        
        String[] splited = jTextField_weight.getText().toString().split("\\s+");
        
        if(splited.length == 4 && splited[1].equals("+")){
            weight = splited[2];
            unit = splited[3];
        }
        else if(splited.length == 3){       
            if(splited[1].equals("+")){
                weight = splited[2];
            }
            else {
                weight = splited[1];
                unit = splited[2];
            }
        }
        else if(splited.length == 2){
            if(splited[0].equals("+") || splited[0].equals("")){
                weight = splited[1];
            }
            else{
                weight = splited[0];
                unit = splited[1];
            }
        }
        else if(splited.length == 1){
            weight = splited[0];
        }
        
        String observation = jTextField_observation.getText().toString();
        DefaultTableModel model = (DefaultTableModel) jTable_weight.getModel();
        
        Weight weightobject = new Weight(nro, weight, unit, observation);
        arrayWeight.add(weightobject);
        
        saveWeight(arrayWeight);
        model.addRow(new Object[]{nro, weight, unit, observation});
     }

     private void cleanreg(){
        arrayWeight.clear();
        saveWeight(arrayWeight);
        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Desea limpiar registro?","Warning",dialogButton);
        if(dialogResult == JOptionPane.YES_OPTION){
            DefaultTableModel model = (DefaultTableModel) jTable_weight.getModel();
            int rowCount = model.getRowCount();
            //Remove rows one by one from the end of the table
            for (int i = rowCount - 1; i >= 0; i--) model.removeRow(i);
            jTextField_observation.setText("");
            this.nro = 0;
        }
     }
     private void download(){
         
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Specificar archivos a guardar");
        int userSelection = chooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            TableModel model = jTable_weight.getModel();
            
            String filename = fileToSave.getAbsoluteFile().toString();
            System.out.println(filename.indexOf(".csv"));
            if(filename.indexOf(".csv")<0){
                filename = filename + ".csv";
            }
            
            File file = new File(filename);
            Boolean flag = true;
            
            if(file.exists()){
                int result = JOptionPane.showConfirmDialog(this,"El archivo existe,  ¿Desea sobrescribir?","Archivo existente",JOptionPane.YES_NO_CANCEL_OPTION);
                if(result != JOptionPane.YES_OPTION) flag = false;
            }
            
            if(flag){
                FileWriter out;

                try {
                    out = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(out);
                    for (int i=0;i<model.getColumnCount();i++){
                      bw.write(model.getColumnName(i)+",");
                    }
                    bw.write("\n");
                    for (int i=0;i<model.getRowCount();i++){
                      for (int j=0;j<model.getColumnCount();j++){
                        bw.write(model.getValueAt(i,j).toString()+",");
                      }
                      bw.write("\n");
                    }
                    bw.close();
                    JOptionPane.showMessageDialog(this, "Se descargo exitosamente en:\n"+file.getAbsolutePath(),"DESCARGA",JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(balance.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
            else{
                this.download();
            }
            
        }
     }
     
     private void saveWeight(ArrayList<Weight> arrayWeight){
         try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(tempDir+"balance.bin"));
            objectOutputStream.writeObject(arrayWeight);
            objectOutputStream.close();
        } catch (IOException ex) {
            Logger.getLogger(balance.class.getName()).log(Level.SEVERE, null, ex);
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

        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jPanel2 = new javax.swing.JPanel();
        jComboBox_port = new javax.swing.JComboBox<>();
        jTextField_weight = new javax.swing.JTextField();
        jButton_load = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton_download = new javax.swing.JButton();
        jButton_connect = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_weight = new javax.swing.JTable();
        jButton_clean = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField_observation = new javax.swing.JTextField();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TELECOM - WEIGHING SYSTEM v1.0.1");
        setExtendedState(6);
        setForeground(java.awt.Color.red);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jComboBox_port.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jComboBox_portMouseClicked(evt);
            }
        });
        jComboBox_port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_portActionPerformed(evt);
            }
        });

        jTextField_weight.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jTextField_weight.setText("0.00 mg");
        jTextField_weight.setEnabled(false);

        jButton_load.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton_load.setText("Cargar (Alt + F9)");
        jButton_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_loadActionPerformed(evt);
            }
        });
        jButton_load.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton_loadKeyPressed(evt);
            }
        });

        jLabel3.setText("Peso Balanza");

        jButton_download.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton_download.setText("Descargar (Alt + F12)");
        jButton_download.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_downloadActionPerformed(evt);
            }
        });
        jButton_download.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton_downloadKeyPressed(evt);
            }
        });

        jButton_connect.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton_connect.setText("Conectar");
        jButton_connect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_connectActionPerformed(evt);
            }
        });

        jTable_weight.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jTable_weight.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nro", "Peso", "Unidad", "Observacion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable_weight);
        if (jTable_weight.getColumnModel().getColumnCount() > 0) {
            jTable_weight.getColumnModel().getColumn(2).setResizable(false);
        }

        jButton_clean.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton_clean.setText("Limpiar");
        jButton_clean.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_cleanActionPerformed(evt);
            }
        });
        jButton_clean.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jButton_cleanKeyPressed(evt);
            }
        });

        jLabel1.setText("Observacion");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_download, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_clean, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_connect, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_load, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_observation, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox_port, javax.swing.GroupLayout.PREFERRED_SIZE, 213, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(632, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(241, 241, 241)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox_port, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jButton_connect, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_weight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_observation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_load, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 151, Short.MAX_VALUE)
                .addComponent(jButton_clean, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_download, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox_portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_portActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox_portActionPerformed

    private void jComboBox_portMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jComboBox_portMouseClicked
        // TODO add your handling code here:
        this.loadport();
    }//GEN-LAST:event_jComboBox_portMouseClicked

    private void jButton_connectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_connectActionPerformed
        // TODO add your handling code here:
        String port = jComboBox_port.getSelectedItem().toString();
        Reading reading = new Reading(port,this);
        if(port.length() != 0){
            try {    
                reading.start();
                this.b_jButton_connect = false;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
        else
            JOptionPane.showMessageDialog(this, "Port unknow","ERROR",JOptionPane.ERROR_MESSAGE);
    }//GEN-LAST:event_jButton_connectActionPerformed

    private void jButton_downloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_downloadActionPerformed
        this.download();
    }//GEN-LAST:event_jButton_downloadActionPerformed

    private void jButton_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_loadActionPerformed
        // TODO add your handling code here:
        this.loadreg();
        jTable_weight.scrollRectToVisible(jTable_weight.getCellRect(jTable_weight.getRowCount()-1, 0, true));

    }//GEN-LAST:event_jButton_loadActionPerformed

    private void jButton_cleanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_cleanActionPerformed
        // TODO add your handling code here:
        this.cleanreg();
        
    }//GEN-LAST:event_jButton_cleanActionPerformed

    private void jButton_loadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton_loadKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.loadreg();
        }
    }//GEN-LAST:event_jButton_loadKeyPressed

    private void jButton_downloadKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton_downloadKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.download();
        }
    }//GEN-LAST:event_jButton_downloadKeyPressed

    private void jButton_cleanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jButton_cleanKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyCode() == KeyEvent.VK_ENTER){
            this.cleanreg();
        }
    }//GEN-LAST:event_jButton_cleanKeyPressed

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
            java.util.logging.Logger.getLogger(balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new balance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton_clean;
    private javax.swing.JButton jButton_connect;
    private javax.swing.JButton jButton_download;
    private javax.swing.JButton jButton_load;
    private javax.swing.JComboBox<String> jComboBox_port;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable_weight;
    private javax.swing.JTextField jTextField_observation;
    private javax.swing.JTextField jTextField_weight;
    // End of variables declaration//GEN-END:variables
}
