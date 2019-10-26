package asdytablam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author josue sarabia
 */
public class ASDyTablaMGr extends javax.swing.JFrame {

    Map<String, ArrayList<String>> gramaticas = new LinkedHashMap<>();
    HashMap<String, String> primeros = new HashMap<>();
    HashMap<String, String> s = new HashMap<>();
    String Terminales="";
 
    
    String S;

    public ASDyTablaMGr() {
        initComponents();
        String v="+&";
        System.out.println( v.contains("+("));
                
    }

    int contLetra = 0;

    // ver como hacer que no se repitan las letras
    public String obtenerLetra(ArrayList<String> gramatica) {
        boolean usada = true;
        String abecedario = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int i = 0, tam = gramatica.size();
        while (usada) {
            if (i < tam && gramatica.get(i).charAt(0) == abecedario.charAt(contLetra)) {
                contLetra++;
                i = 0;
            }
            if (i == tam) {
                usada = false;
            }
            i++;
        }
        return abecedario.substring(contLetra, contLetra + 1);
    }

    public void quitarRecursividad(ArrayList<String> gramatica) {
        int tam = gramatica.size();
        int i = 0;
        while (i < tam) {
            String p = gramatica.get(i);
            String noTerminal = p.substring(0, 1);
            String nuevoNoTerminal = obtenerLetra(gramatica);
            int j = 0;
            int agregarEpsilon = 0;
            while (j < tam) {
                String pTemp = gramatica.get(j);
                String noTerminalTemp = pTemp.substring(0, 1);
                if (noTerminal.equals(noTerminalTemp)) {
                    String primerSimbGram = pTemp.substring(3, 4);
                    if (noTerminalTemp.equals(primerSimbGram)) {
                        String nuevaProd = nuevoNoTerminal + pTemp.substring(1, 3) + pTemp.substring(4) + nuevoNoTerminal;
                        gramatica.set(j, nuevaProd);
                        agregarEpsilon = 1;
                    }
                }
                j++;
            }
            if (agregarEpsilon == 1) {
                gramatica.add(nuevoNoTerminal + "->&");
                for (int k = 0; k < tam; k++) {
                    String pTemp2 = gramatica.get(k);
                    String terminalTemp = pTemp2.substring(0, 1);
                    if (terminalTemp.equals(noTerminal) && !pTemp2.contains(nuevoNoTerminal)) {
                        pTemp2 = pTemp2 + nuevoNoTerminal;
                        gramatica.set(k, pTemp2);
                    }
                }
            }
            i++;
        }

    }

    //TODO: (opcional) verificar si funciona mas de una vez
    public void quitarFactorizacion(ArrayList<String> gramatica) {
        int tam = gramatica.size();
        int sw = 0, sw1 = 0, i = 0;
        while (i < tam) {
            String prefijo = buscarFactorizacion(gramatica, i);
            String cabezote = gramatica.get(i).split("->")[0];
            char factorizar = prefijo.charAt(0);
            prefijo = prefijo.substring(1);
            if (factorizar == '1') {
                i = 0;
                String nuevoNoTerminal = obtenerLetra(gramatica);
                while (sw == 0) {//TODO: cuando falle borrar el ciclo
                    int j = 0;
                    sw = 0;
                    sw1 = 0;
                    while (j < gramatica.size()) {
                        String p = gramatica.get(j);
                        if (p.contains(prefijo) && cabezote.equals(p.split("->")[0])) {
                            p = gramatica.remove(j);
                            String[] pVector = p.split("->" + prefijo);

                            if (sw1 == 0) {
                                String nuevaProd = pVector[0] + "->" + prefijo + nuevoNoTerminal;
                                gramatica.add(j, nuevaProd);
                                sw1 = 1;
                            }
                            if (pVector.length == 1) {
                                gramatica.add(nuevoNoTerminal + "->" + "&");
                            } else {
                                gramatica.add(nuevoNoTerminal + "->" + pVector[1]);
                            }
                            sw = 1;
                        }
                        j++;
                    }
                }
            }
            i++;
        }
    }

    public String buscarFactorizacion(ArrayList<String> gramatica, int i) {
        int index = 1, contIguales = 0, antContIguales = 0;
        int tam = gramatica.size();
        String subCuerpo = "", antSubCuerpo = "";
        String prod = gramatica.get(i);
        String cabezote = prod.split("->")[0];
        String cuerpo = prod.split("->")[1];
        int j = 0;
        while (j < tam) {
            String prodTemp = gramatica.get(j);
            if (!prod.equals(prodTemp)) {
                String cabezoteTemp = prodTemp.split("->")[0];
                String cuerpoTemp = prodTemp.split("->")[1];
                if (index <= cuerpoTemp.length() && index <= cuerpo.length()) {
                    subCuerpo = cuerpo.substring(0, index);
                    String subCuerpoTemp = cuerpoTemp.substring(0, index);
                    if (subCuerpoTemp.equals(subCuerpo) && cabezote.equals(cabezoteTemp)) {
                        contIguales++;
                    }
                }
            }
            j++;
            if (j == tam && (contIguales > 0 || antContIguales > 0)) {
                if (antContIguales != 0 && antContIguales != contIguales) {
                    return "1" + antSubCuerpo;
                } else {
                    antContIguales = contIguales;
                    antSubCuerpo = subCuerpo;
                    index++;
                    contIguales = 0;
                    j = 0;
                }
            }
        }
        return "0";
    }

    public void CalculoPrimeros() {
        for (String i : gramaticas.keySet()) {
            String primero = "";
            for (String j : gramaticas.get(i)) {
                Terminales+=j;
                primero +=  primero(j);
            }
            primeros.put(i, primero);
        }

    }

    public boolean EsNoTerminal(String producion) {

        Boolean sw = (producion.substring(0, 1).matches("[A-Z]") || producion.substring(0, 1).equals("Ñ")) ? true : false;
        return sw;
    }

    public String primero(String producion) {
        if (!EsNoTerminal(producion.substring(0, 1))) {
            return producion.substring(0,1);
        } else {
            String p = "";
            for (String j : gramaticas.get(producion.substring(0, 1))) {

                p += primero(j.substring(0, 1)) ;
            }
            return p ;

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

        fc = new javax.swing.JFileChooser();
        cargarArchivoButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jTextField1 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cargarArchivoButton.setText("Cargar Archivo");
        cargarArchivoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarArchivoButtonActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setText("Tabla M");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pila", "Entrada", "Salida"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton1.setText("Reconocer");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(cargarArchivoButton)
                                .addGap(231, 231, 231)
                                .addComponent(jLabel1)
                                .addGap(212, 212, 212))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addComponent(jScrollPane2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cargarArchivoButton)
                    .addComponent(jLabel1))
                .addGap(27, 27, 27)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 418, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
 public void calculars(){
     
      for (String i : gramaticas.keySet()) {
            String produccion = i;
    
          calculosiguiente(produccion);
        }
 
 
 
 
 
 }
 public String  calculosiguiente(String produccion  ){
  
     if (!s .get(produccion).equals("")) {
         return s.get(produccion);
     }else{
         for (String j : gramaticas.keySet()) {
             romperciclo:
              for (String k : gramaticas.get(j)) {
                  
                  if (k.contains(produccion) ) {
                      if (produccion.equals(S) && !s.get(produccion).contains("$")) {
                          s.put(produccion,s.get(produccion)+"$");
                      }
                 
                      /* Verifico si la produccion string es mas grande que la posicion del terminal +1 
                       caso  bbeta cuando beta es terminal  y si ya no fue agregado a s del terminal*/
                      int posT=k.indexOf(produccion);
                      int posST=k.indexOf(produccion)+1;
                      /* evito bucle */
                   
                          /* segunda regla si es un terminal agrega el primero del terminal encontrado sino agrega el primero del no terminal */
                          if ( posT<k.length()-1&& ( EsNoTerminal(""+k.charAt(posST)) && !s.get(produccion).contains( primeros.get(""+k.charAt(posST)))  )) {
                          s.put(produccion,s.get(produccion)+primeros.get(""+k.charAt(posST)))   ;
                      }else if( posT<k.length()-1 &&  ( !EsNoTerminal(""+k.charAt(posST)) && !s.get(produccion).contains(""+k.charAt(posST)) && !(""+k.charAt(posST)).equals("&"))){
                     
                        s.put(produccion,s.get(produccion)+k.charAt(posST))   ;
                      }
                           /* tercera regla si Beta contiene epsilon y no se conoce el  del cabezote*/
                      if ((s.get(produccion).contains("&") ||posT==k.length()-1)   ) {
                          
                          s.put(produccion,s.get(produccion).replace("&", ""));
                          if (j.charAt(0)==k.charAt(posT)) {
                              continue romperciclo;
                          }
                            s.put(produccion,s.get(produccion)+calculosiguiente(j));
                           
                          
                          
                      } 
                      
                      
                         
                     
                      
                      
                       
                  }
            }
          }
         s.put(produccion,s.get(produccion).replaceAll("(.)(?=.*\\1)", ""));
         
     return s.get(produccion);
     }
     
     
     }
     
 
     
 
 
 
 
  
    private void cargarArchivoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarArchivoButtonActionPerformed
        // Agregar filtro a FileChooser
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos .txt", "txt", "texto");
        fc.setFileFilter(filter);

        // Mostrar el FileChooser
        int opcion = fc.showOpenDialog(this);

        // Si el usuario escogió abrir
        if (opcion == JFileChooser.APPROVE_OPTION) {

            ArrayList<String> gramatica = new ArrayList<>();

            // Asignar archivo y nombre.
            File archivo = fc.getSelectedFile();

            try (Scanner lector = new Scanner(archivo)) {
                // Mientras el archivo tenga otra línea.
                int sw=0;
                while (lector.hasNextLine()) {
                    // Pedir la linea
                 // Pedir la linea
                    String linea = lector.nextLine();
                    gramatica.add(linea);
                    if (sw == 0) {
                        S = linea.split("->")[0];
                        sw =1;
                    }
                }
                quitarRecursividad(gramatica);
                //System.out.println("recur");
                //System.out.println("" + gramatica);
                quitarFactorizacion(gramatica);
                //System.out.println("fact");

                String agrupados = "";
                for (int i=0; i<gramatica.size();i++) {
                    String cabezote = gramatica.get(i).substring(0, 1);
                    ArrayList<String> produciones = new ArrayList<String>();
                    if (!agrupados.contains(cabezote)) {

                        for (int j=i; j<gramatica.size();j++) {
                            String producion=gramatica.get(j);
                            if (cabezote.equals(producion.substring(0, 1))) {
                                produciones.add(producion.substring(3, producion.length()));
 
                            }
                        }
                        
                        gramaticas.put(cabezote, produciones);
                        s.put(cabezote, "");
                        agrupados += cabezote;
                    }
                }
               
               
                System.out.println("" + gramatica);
                System.out.println(gramaticas.toString());
                
                CalculoPrimeros();
                 System.out.println("Primeros:"+primeros.toString());
                calculars();
               Terminales=Terminales. replaceAll("[A-Z]", "").replaceAll("(.)(?=.*\\1)", "").replaceAll("&", "")+"$";
               
                 System.out.println("Siguientes:"+s.toString());
                 System.out.println("Terminales"+Terminales);
              
                 DefaultTableModel m = new DefaultTableModel();
                 for (int i = 0; i < Terminales.length()+1; i++) {
                     if (i!=0) {
                         m.addColumn(Terminales.charAt(i-1));
                     }else{
                     m.addColumn("No Terminales");
                     }
                     
                }
                 for (String i :gramaticas.keySet()) {
                     String  []row= new String[Terminales.length()+1];
                    row[0]=i;
                     
                         for (String k : gramaticas.get(i) ) {
                             String primero=primero(k);
                             for (int j = 1; j < Terminales.length()+1; j++) {
                                 if (primero.contains(""+Terminales.charAt(j-1))) {
                                     row[j]=i+"->"+k;
                                 }
                                 if (primero.contains("&") && s.get(i).contains(""+Terminales.charAt(j-1))) {
                                     row[j]=i+"->"+k;
                                 }
                                 if (primero.contains("&") && s.get(i).contains("$")) {
                                     row[Terminales.length()]=i+"->"+k;
                                 }
                             }
                         }
                         m.addRow(row);
                      
                    
                }
                 
                 
                jTable1.setModel(m);
                

            } catch (FileNotFoundException ex) {
                // TODO enviar mensaje al usuario
            } catch (NumberFormatException ex) {
                // TODO enviar mensaje al usuario
            }
        }
    }//GEN-LAST:event_cargarArchivoButtonActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    
    }//GEN-LAST:event_jTextField1ActionPerformed

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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ASDyTablaMGr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ASDyTablaMGr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ASDyTablaMGr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ASDyTablaMGr.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ASDyTablaMGr().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cargarArchivoButton;
    private javax.swing.JFileChooser fc;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
