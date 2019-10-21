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

    //TODO: QUITAR RECURSIVIDAD MAS DE 1 VEZ  ( ES UN PROBLEMA? )
    //TODO: QUITAR FACTORIZACION MAS DE 1 VEZ
    //TODO: PROBLEMA DIFERENCIAR E' DE NOTERMINAL E ANTES DE UN TERMINAL ' ( ES UN PROBLEMA? )
    
    public void quitarRecursividad(ArrayList<String> gramatica) {
        int tam = gramatica.size();
        int i = 0;
        while (i < tam) {
            String p = gramatica.get(i);
            String terminal = p.substring(0, 1);
            String primerSimbGram = p.substring(3, 4);
            if (terminal.equals(primerSimbGram)) {
                String nuevoNoTerminal = terminal + "'";
                String nuevaProd = nuevoNoTerminal + p.substring(1, 3) + p.substring(4) + nuevoNoTerminal;
                gramatica.set(i, nuevaProd);
                //i = 0;
                gramatica.add(nuevoNoTerminal + "->&");
                for (int j = 0; j < tam; j++) {
                    String pTemp = gramatica.get(j);
                    String terminalTemp = pTemp.substring(0, 1);
                    if (terminalTemp.equals(terminal) && !pTemp.contains(nuevoNoTerminal)) {
                        pTemp = pTemp + nuevoNoTerminal;
                        gramatica.set(j, pTemp);
                    }
                }

            }

            i++;
        }

    }

    public void quitarFactorizacion(ArrayList<String> gramatica) {
        int tam = gramatica.size(), i = 0;
        while (i < tam) {
            int sw=0, index = 1, contIguales = 0, antContIguales = 0;
            String subCuerpo = "", antSubCuerpo = "", prodIguales = "", antProdIguales = "";
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
                            //el separador debe ser algo unico que no este en la prod
                            prodIguales = prodIguales + prodTemp + "josue";
                        }
                    }
                }
                j++;
                if (j == tam && (contIguales > 0 || antContIguales > 0)) { // al menos hay un igual
                    if (antContIguales != 0 && antContIguales != contIguales) {
                        //facotrizo
                        String p = gramatica.remove(i);
                        String[] pVector = p.split("->");
                        String nuevaProd = pVector[0] + "->" + antSubCuerpo + pVector[0] + "'";
                        gramatica.add(i, nuevaProd);
                        String nuevasProds = pVector[0] + "'" + "->";
                        String[] pvector1 = p.split(antSubCuerpo);
                        if (pvector1.length < 2) {
                            gramatica.add(nuevasProds + "&");
                        } else {
                            gramatica.add(nuevasProds + pvector1[1]);
                        }
                        //vector con los iguales
                        String[] vProdIguales = antProdIguales.split("josue");
                        for (int k = 0; k < vProdIguales.length; k++) {
                            //buscar los iguales y retornar la posicion en el array
                            int posProd = buscarPosicionProd(vProdIguales[k], gramatica);
                            String igual = gramatica.remove(posProd);
                            pvector1 = igual.split(antSubCuerpo);
                            if (pvector1.length < 2) {
                                gramatica.add(nuevasProds + "&");
                            } else {
                                gramatica.add(nuevasProds + pvector1[1]);
                            }
                        }
                        sw = 1;
                    } else {
                        antContIguales = contIguales;
                        antSubCuerpo = subCuerpo;
                        antProdIguales = prodIguales;
                        index++;
                        contIguales = 0;
                        prodIguales = "";
                        j = 0;
                    }
                }
            }
            if (sw == 1) {
                //empiezo a analizar desde el prinicipio
                i = 0;
            } else {
                //avanzo con la busqueda a la siguiente produccion
                index = 1;
                i++;
            }

        }
        for (int j = 0; j < 1; j++) {
            System.out.println("" + gramatica);
        }
    }

    public static int buscarPosicionProd(String prodIgual, ArrayList<String> gramatica) {
        int tam = gramatica.size();
        int i = 0;
        while (i < tam) {
            if (gramatica.get(i).equals(prodIgual)) {
                return i;
            }
            i++;
        }

        return i;
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
                quitarFactorizacion(gramatica);
                System.out.println("termine");
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
