package asdytablam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import javax.swing.DefaultListModel;
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
    String Terminales = "";
    String S;

    public ASDyTablaMGr() {
        initComponents();
        String v = "+&";
        System.out.println(v.contains("+("));

    }

    int contLetra = 0;

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
                while (sw == 0) {
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
                Terminales += j;
                primero += primero(j);
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
            return producion.substring(0, 1);
        } else {
            String p = "";
            for (String j : gramaticas.get(producion.substring(0, 1))) {

                p += primero(j.substring(0, 1));
            }
            return p;

        }

    }

    public void calculars() {

        for (String i : gramaticas.keySet()) {
            String produccion = i;

            calculosiguiente(produccion);
        }

    }

    public String calculosiguiente(String produccion) {

        if (!s.get(produccion).equals("")) {
            return s.get(produccion);
        } else {
            for (String j : gramaticas.keySet()) {
                romperciclo:
                for (String k : gramaticas.get(j)) {

                    if (produccion.equals(S) && !s.get(produccion).contains("$")) {
                        s.put(produccion, s.get(produccion) + "$");
                    }

                    if (k.contains(produccion)) {


                        /* Verifico si la produccion string es mas grande que la posicion del terminal +1 
                       caso  bbeta cuando beta es terminal  y si ya no fue agregado a s del terminal*/
                        int posT = k.indexOf(produccion);
                        int posST = k.indexOf(produccion) + 1;
                        /* evito bucle */

 /* segunda regla si es un terminal agrega el primero del terminal encontrado sino agrega el primero del no terminal */
                        if (posT < k.length() - 1 && (EsNoTerminal("" + k.charAt(posST)) && !s.get(produccion).contains(primeros.get("" + k.charAt(posST))))) {
                            s.put(produccion, s.get(produccion) + primeros.get("" + k.charAt(posST)));
                        } else if (posT < k.length() - 1 && (!EsNoTerminal("" + k.charAt(posST)) && !s.get(produccion).contains("" + k.charAt(posST)) && !("" + k.charAt(posST)).equals("&"))) {

                            s.put(produccion, s.get(produccion) + k.charAt(posST));
                        }
                        /* tercera regla si Beta contiene epsilon y no se conoce el  del cabezote*/
                        if ((s.get(produccion).contains("&") || posT == k.length() - 1)) {

                            s.put(produccion, s.get(produccion).replace("&", ""));
                            if (j.charAt(0) == k.charAt(posT)) {
                                continue romperciclo;
                            }
                            s.put(produccion, s.get(produccion) + calculosiguiente(j));

                        }

                    }
                }
            }
            s.put(produccion, s.get(produccion).replaceAll("(.)(?=.*\\1)", ""));

            return s.get(produccion);
        }

    }

    public void llenarTablaM(String simbInicio, String entrada) {
        DefaultTableModel rmodel = new DefaultTableModel();
        rmodel.addColumn("pila");
        rmodel.addColumn("entrada");
        rmodel.addColumn("salida");
        String pila = "$" + simbInicio;
        entrada = entrada + "$";
        int fila = 0;
        String salida;
        boolean terminar = false;
        while (!terminar) {
            rmodel.addRow(new Object[]{pila, entrada, ""});
            String cimaPila = pila.substring(pila.length() - 1);
            String cimaEntrada = entrada.substring(0, 1);
            if (pila.length() == 1 && entrada.length() == 1 && pila.equals("$") && entrada.equals("$")) {
                salida = "aceptar";
                terminar = true;
            } else if (cimaPila.equals(cimaEntrada)) {
                pila = pila.substring(0, pila.length() - 1);
                entrada = entrada.substring(1);
                salida = "";
            } else {
                salida = buscarEnTablaM(cimaPila, cimaEntrada);
                if (salida == null || salida.equals("null")) {
                    salida = "rechazada";
                    terminar = true;
                } else {

                    if (salida.contains("&")) {
                        pila = pila.substring(0, pila.length() - 1);
                    } else {
                        String salidaInv = invertirSalida(salida);
                        pila = pila.substring(0, pila.length() - 1) + salidaInv;
                    }

                }
            }
            rmodel.setValueAt(salida, fila, 2);
            fila++;
        }
        reconocerTable.setModel(rmodel);
    }

    public String buscarEnTablaM(String cimaPila, String cimaEntrada) {
        DefaultTableModel modelM = (DefaultTableModel) jTable1.getModel();
        int filas = modelM.getRowCount();
        int columnas = modelM.getColumnCount();
        int i = 0;
        int j = 0;
        boolean encontroFila = false;
        while (i < filas && !encontroFila) {
            if (modelM.getValueAt(i, 0).equals(cimaPila)) {
                encontroFila = true;
            }
            i++;
        }
        boolean encontroColum = false;
        while (j < columnas && !encontroColum) {
            if (modelM.getColumnName(j).equals(cimaEntrada)) {
                encontroColum = true;
            }
            j++;
        }
        if (modelM.getValueAt(i - 1, j - 1) == null || !encontroFila || !encontroColum) {
            return "null";
        }
        
        return modelM.getValueAt(i - 1, j - 1).toString();
    }

    public String invertirSalida(String salida) {

        salida = salida.split("->")[1];
        int tam = salida.length();
        String inversa = "";
        for (int i = tam; i > 0; i--) {
            inversa = inversa + salida.substring(i - 1, i);
        }
        return inversa;
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
        reconocerTable = new javax.swing.JTable();
        reconocerTextField = new javax.swing.JTextField();
        reconocerCadenaButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

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

        reconocerTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Pila", "Entrada", "Salida"
            }
        ));
        jScrollPane2.setViewportView(reconocerTable);

        reconocerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconocerTextFieldActionPerformed(evt);
            }
        });

        reconocerCadenaButton.setText("Reconocer");
        reconocerCadenaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconocerCadenaButtonActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(jList1);

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setText("Gramatica Sin Vicios");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(jTable2);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel3.setText("Primero y Siguiente");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("Cadena:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(62, 62, 62)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 528, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(219, 219, 219))))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cargarArchivoButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 10, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 651, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(279, 279, 279)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(18, 18, 18)
                        .addComponent(reconocerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(reconocerCadenaButton)
                        .addGap(41, 41, 41))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cargarArchivoButton)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(reconocerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(reconocerCadenaButton)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void cargarArchivoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cargarArchivoButtonActionPerformed
        
        DefaultListModel list = new DefaultListModel();
        jList1.setModel(list);
        DefaultTableModel m = new DefaultTableModel();
        DefaultTableModel m2 = new DefaultTableModel();
        DefaultTableModel rmodel = new DefaultTableModel();
        jTable2.setModel(m2);
        jTable1.setModel(m);
        reconocerTable.setModel(rmodel);
        gramaticas = new LinkedHashMap<>();
        primeros = new HashMap<>();
        s = new HashMap<>();
        Terminales = "";
        reconocerTextField.setText("");
        
        // Agregar filtro a FileChooser
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos .txt", "txt", "texto");
        fc.setFileFilter(filter);

        // Mostrar el FileChooser
        int opcion = fc.showOpenDialog(this);

        // Si el usuario escogió abrir
        if (opcion == JFileChooser.APPROVE_OPTION) {

            ArrayList<String> gramatica = new ArrayList<>();
            contLetra = 0;
            // Asignar archivo y nombre.
            File archivo = fc.getSelectedFile();

            try ( Scanner lector = new Scanner(archivo)) {
                // Mientras el archivo tenga otra línea.
                int sw = 0;
                while (lector.hasNextLine()) {
                    // Pedir la linea
                    String linea = lector.nextLine();
                    gramatica.add(linea);
                    if (sw == 0) {
                        S = linea.split("->")[0];
                        sw = 1;
                    }
                }
                quitarRecursividad(gramatica);
                quitarFactorizacion(gramatica);

                String agrupados = "";
                for (int i = 0; i < gramatica.size(); i++) {
                    String cabezote = gramatica.get(i).substring(0, 1);
                    ArrayList<String> produciones = new ArrayList<String>();
                    if (!agrupados.contains(cabezote)) {

                        for (int j = i; j < gramatica.size(); j++) {
                            String producion = gramatica.get(j);
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
                System.out.println("Primeros:" + primeros.toString());
                calculars();
                Terminales = Terminales.replaceAll("[A-Z]", "").replaceAll("(.)(?=.*\\1)", "").replaceAll("&", "") + "$";

                System.out.println("Siguientes:" + s.toString());
                System.out.println("Terminales" + Terminales);

                
                m2.addColumn("gramatica");
                m2.addColumn("primero");
                m2.addColumn("siguiente");
                for (String i :gramaticas.keySet()) {
                    m2.addRow( new Object[]{i+"->",primeros.get(i),s.get(i)});
                }
                jTable2.setModel(m2);
                for (int i = 0; i < Terminales.length() + 1; i++) {
                    if (i != 0) {
                        m.addColumn(Terminales.charAt(i - 1));
                    } else {
                        m.addColumn("No Terminales");
                    }

                }
                for (String i : gramaticas.keySet()) {
                    String[] row = new String[Terminales.length() + 1];
                    row[0] = i;

                    for (String k : gramaticas.get(i)) {
                        String primero = primero(k);
                        for (int j = 1; j < Terminales.length() + 1; j++) {
                            if (primero.contains("" + Terminales.charAt(j - 1))) {
                                row[j] = i + "->" + k;
                            }
                            if (primero.contains("&") && s.get(i).contains("" + Terminales.charAt(j - 1))) {
                                row[j] = i + "->" + k;
                            }
                            if (primero.contains("&") && s.get(i).contains("$")) {
                                row[Terminales.length()] = i + "->" + k;
                            }
                        }
                    }
                    m.addRow(row);
                }

                jTable1.setModel(m);
                int tam  = gramatica.size();
                int index = 0;
                boolean encontro = false;
                while(!encontro){
                    if (gramatica.get(index).split("->")[0].equals(S)) {
                        String p = gramatica.remove(index);
                        gramatica.add(0, p);
                        encontro=true;
                    }
                    index++;
                }
                //DefaultListModel list = new DefaultListModel();
                for (String produccion : gramatica) {
                    list.addElement(produccion);
                }
                jList1.setModel(list);

            } catch (FileNotFoundException ex) {
                System.out.println("error file not found");
            } catch (NumberFormatException ex) {
                System.out.println("error numbe format exception");
            }
        }
    }//GEN-LAST:event_cargarArchivoButtonActionPerformed

    private void reconocerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconocerTextFieldActionPerformed

    }//GEN-LAST:event_reconocerTextFieldActionPerformed

    private void reconocerCadenaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconocerCadenaButtonActionPerformed
        String cad = reconocerTextField.getText();
        llenarTablaM(S, cad);
    }//GEN-LAST:event_reconocerCadenaButtonActionPerformed

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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList<String> jList1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JButton reconocerCadenaButton;
    private javax.swing.JTable reconocerTable;
    private javax.swing.JTextField reconocerTextField;
    // End of variables declaration//GEN-END:variables
}
