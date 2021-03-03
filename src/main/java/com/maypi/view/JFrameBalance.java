/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maypi.view;

import com.maypi.util.Reading;
import com.maypi.service.ConfigService;
import com.maypi.service.ParamService;
import com.maypi.service.WeightService;
import com.maypi.service.response.ConfigResponse;
import com.maypi.service.response.UserResponse;
import com.maypi.service.response.WeightResponse;
import com.maypi.service.server.WeightServerService;
import com.sun.glass.events.KeyEvent;
import java.awt.Image;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;


/**
 *
 * @author Roycer
 */
public class JFrameBalance extends javax.swing.JFrame {

    WeightService weightService = new WeightService();
    
    ParamService paramService = new ParamService();
    ConfigService configService = new ConfigService();
    
    ConfigResponse configResponse;
    Reading reading;
    
    private ArrayList<WeightResponse> arrayWeight = new ArrayList();
    
    private int nro = 0;    
    private String code;
    private UserResponse userResponse;
    
    /**
     * Creates new form balance
     */
    public JFrameBalance() {
        
        initComponents();
        this.initImage();
        this.loadRegs();
        this.loadBalance();
        
        //this load port
        this.jButton_load.setMnemonic(KeyEvent.VK_F9);
        this.jButton_download.setMnemonic(KeyEvent.VK_F12);
        jTable_weight.getColumnModel().getColumn(0).setPreferredWidth(5);
        jTable_weight.getColumnModel().getColumn(1).setPreferredWidth(7);
        jTable_weight.getColumnModel().getColumn(2).setPreferredWidth(7);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        jTable_weight.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        jTable_weight.setDefaultRenderer(String.class, centerRenderer);
    }
    
    private void initImage(){
        Image i;
        try {
            i = ImageIO.read(getClass().getResource("/logo.png"));
            this.setIconImage(i);
            
            Image ida = ImageIO.read(getClass().getResource("/sync_on.png"));
            Icon icon = new ImageIcon(ida);
            
            jButton_sync.setIcon(icon);
        } catch (IOException ex) {
            Logger.getLogger(JFrameBalance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setUser(UserResponse userResponse){
        this.userResponse = userResponse;
    }
    
    public void setCode(String code){
        this.code = code;
    }
    
    public void onDataBalance(String data){
        jTextField_weight.setText(data);
    }
     
    public void onChangeConfig(){
        
        try {
            this.reading.closePort();
            this.loadBalance();
        } catch (Exception e) {
    
        }
        
        
    }
    
    private void loadBalance(){
          
        String port = paramService.getParam("port");
        this.configResponse = this.configService.getConfig();
        this.configResponse.setPort(port);
        
        if(this.configResponse.getStatus()){

            try {
                this.reading = new Reading(configResponse,this);
                this.reading.start();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e.getMessage(),"ERROR",JOptionPane.ERROR_MESSAGE);
            }
        }
        else {
            JOptionPane.showMessageDialog(this, "Configuracion no existe","INFO",JOptionPane.INFORMATION_MESSAGE);
        }
  
     }

     private void loadRegs(){
         
        DefaultTableModel model = (DefaultTableModel) jTable_weight.getModel();

        arrayWeight = weightService.getWeightsFromFileBin();
            
        if(arrayWeight.size() != 0){
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog (null, "Desea cargar registro temporal?","Warning",dialogButton);
            if(dialogResult == JOptionPane.YES_OPTION){
                for (WeightResponse w : arrayWeight){
                    model.addRow(new Object[]{w.getNro(), w.getWeight(), w.getUnit(), w.getObservation()});
                    nro = w.getNro();
                }
            }else{
                arrayWeight.clear();
            }
        }
            

    }
     
     private void loadreg(){

        nro++;
        
        String weight = "";
        String unit = "";
        String observation = jTextField_observation.getText().toString();
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
        
       
        DefaultTableModel model = (DefaultTableModel) jTable_weight.getModel();
        
        WeightResponse weightResponse = new WeightResponse(nro, weight, unit, observation, this.code);
        arrayWeight.add(weightResponse);
        
        this.weightService.saveWeightsInFileBin(arrayWeight);
        model.addRow(new Object[]{nro, weight, unit, observation});
        
        WeightService newWeightService = new WeightService();
        newWeightService.setWeightResponse(weightResponse);
        newWeightService.start();
        //this.weightService.saveWeight(weightResponse);
        
        
     }

     private void cleanreg(){

        int dialogButton = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog (null, "Desea limpiar registro?","Warning",dialogButton);
        
        if(dialogResult == JOptionPane.YES_OPTION){
            arrayWeight.clear();
            this.weightService.saveWeightsInFileBin(arrayWeight);
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
        chooser.setSelectedFile(new File(this.code));
        chooser.setDialogTitle("Specificar archivos a guardar");
        int userSelection = chooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = chooser.getSelectedFile();
            TableModel model = jTable_weight.getModel();
            int nRow = model.getRowCount();
            int nCol = model.getColumnCount();

            String filename = fileToSave.getAbsoluteFile().toString();
            System.out.println(filename.indexOf(".csv"));
            if(filename.indexOf(".csv")<0){
                filename = filename + ".csv";
            }
            
            File file = new File(filename);
            Boolean flag = true;
            
            if(file.exists()){
                JOptionPane.showMessageDialog(this,"El archivo existe,  Cambie el nombre","Archivo existente",JOptionPane.INFORMATION_MESSAGE);
                flag = false;
            }
            
            if(flag){
                FileWriter out;

                try {
                    out = new FileWriter(file);
                    BufferedWriter bw = new BufferedWriter(out);
                    for (int i=0;i<nCol;i++){
                      bw.write(model.getColumnName(i)+",");
                    }
                    bw.write("\n");
                    for (int i=0;i<nRow;i++){
                      for (int j=0;j<nCol;j++){
                        bw.write(model.getValueAt(i,j).toString()+",");
                      }
                      bw.write("\n");
                    }
                    bw.close();
                    
                    JOptionPane.showMessageDialog(this, "Se descargo exitosamente en:\n"+file.getAbsolutePath(),"DESCARGA",JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(JFrameBalance.class.getName()).log(Level.SEVERE, null, ex);
                }    
            }
            else{
                this.download();
            }
            
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
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_weight = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextField_weight = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton_sync = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField_observation = new javax.swing.JTextField();
        jButton_load = new javax.swing.JButton();
        jButton_clean = new javax.swing.JButton();
        jButton_download = new javax.swing.JButton();

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
        setTitle("MYSAC - WEIGHING SYSTEM");
        setExtendedState(6);
        setForeground(java.awt.Color.red);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

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

        jTextField_weight.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jTextField_weight.setEnabled(false);

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setText("Configuración");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton_sync.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton_sync.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_syncActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel1.setText("Observación");

        jTextField_observation.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton_load, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField_weight)
                    .addComponent(jButton_download, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton_clean, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField_observation)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                        .addComponent(jButton_sync, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                    .addComponent(jButton_sync, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_weight, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_observation, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton_load, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_clean, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton_download, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 467, Short.MAX_VALUE))
                .addContainerGap())
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

        jPanel2.getAccessibleContext().setAccessibleDescription("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFrameConfiguration jframeConfiguration = new JFrameConfiguration();
        jframeConfiguration.setJFrameBalance(this);
        jframeConfiguration.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton_syncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_syncActionPerformed
        // TODO add your handling code here:
        WeightServerService weightServerService = new WeightServerService();
        weightServerService.start();
       
    }//GEN-LAST:event_jButton_syncActionPerformed

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
            java.util.logging.Logger.getLogger(JFrameBalance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JFrameBalance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JFrameBalance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JFrameBalance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new JFrameBalance().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton_clean;
    private javax.swing.JButton jButton_download;
    private javax.swing.JButton jButton_load;
    private javax.swing.JButton jButton_sync;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable_weight;
    private javax.swing.JTextField jTextField_observation;
    private javax.swing.JTextField jTextField_weight;
    // End of variables declaration//GEN-END:variables
}
