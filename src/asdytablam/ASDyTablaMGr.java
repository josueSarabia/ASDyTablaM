package asdytablam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author josue sarabia
 */
public class ASDyTablaMGr extends javax.swing.JFrame {

    /**
     * Creates new form ASDyTablaMGr
     */
    public ASDyTablaMGr() {
        initComponents();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        cargarArchivoButton.setText("Cargar Archivo");
        cargarArchivoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cargarArchivoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cargarArchivoButton)
                .addContainerGap(355, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cargarArchivoButton)
                .addContainerGap(300, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

            try ( Scanner lector = new Scanner(archivo)) {
                // Mientras el archivo tenga otra línea.
                while (lector.hasNextLine()) {
                    // Pedir la linea
                    gramatica.add(lector.nextLine());
                }
                quitarRecursividad(gramatica);
                //System.out.println("recur");
                //System.out.println("" + gramatica);
                quitarFactorizacion(gramatica);
                //System.out.println("fact");
                System.out.println("" + gramatica);
            } catch (FileNotFoundException ex) {
                // TODO enviar mensaje al usuario
            } catch (NumberFormatException ex) {
                // TODO enviar mensaje al usuario
            }
        }
    }//GEN-LAST:event_cargarArchivoButtonActionPerformed

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
    // End of variables declaration//GEN-END:variables
}
