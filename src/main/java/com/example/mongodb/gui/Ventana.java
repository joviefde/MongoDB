package com.example.mongodb.gui;


import com.toedter.calendar.JDateChooser;

import javax.swing.*;

public class Ventana {
    public JPanel panel1;
    public JTextField tfAutor;
    public JTextField tfDescripcion;
    public JTextField tfTitulo;
    public JDateChooser dcFecha;
    public JButton btCancelar;
    public JButton btNuevo;
    public JButton btGuardar;
    public JButton btModificar;
    public JButton btEliminar;
    public JList lLibros;
    public JTextField tfBuscar;

    public Ventana() {

        JFrame frame = new JFrame("Ejemplo MongoDB");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
