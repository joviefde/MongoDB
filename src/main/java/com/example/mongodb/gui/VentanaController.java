package com.example.mongodb.gui;

import com.example.mongodb.base.Libro;
import com.example.mongodb.util.Util;
import com.mongodb.client.MongoDatabase;

import javax.swing.*;
import java.awt.event.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class VentanaController implements ActionListener, MouseListener, KeyListener,
        FocusListener {

    private Ventana view;
    private VentanaModel model;
    private MongoDatabase db;

    private DefaultListModel dlmLibros;

    private boolean nuevoLibro;
    private Libro libroSeleccionado;

    public VentanaController(Ventana view, VentanaModel model) {
        this.view = view;
        this.model = model;

        model.conectar();
        addListeners();
        inicializar();
    }

    private void addListeners() {

        view.btNuevo.addActionListener(this);
        view.btCancelar.addActionListener(this);
        view.btEliminar.addActionListener(this);
        view.btGuardar.addActionListener(this);
        view.btModificar.addActionListener(this);
        view.tfBuscar.addFocusListener(this);
        view.tfBuscar.addKeyListener(this);

        view.lLibros.addMouseListener(this);

        dlmLibros = new DefaultListModel();
        view.lLibros.setModel(dlmLibros);
    }

    private void inicializar() {

        listar();
        modoEdicion(false);
    }

    private void listar() {
        try {
            List<Libro> libros = model.getLibros();

            dlmLibros.removeAllElements();
            for (Libro libro : libros)
                dlmLibros.addElement(libro);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        String actionCommand = e.getActionCommand();
        Libro libro = null;

        switch (actionCommand) {
            case "nuevo":
                nuevoLibro = true;
                view.tfTitulo.setText("");
                view.tfDescripcion.setText("");
                view.tfAutor.setText("");
                view.dcFecha.setDate(null);
                modoEdicion(true);
                break;
            case "modificar":
                nuevoLibro = false;
                modoEdicion(true);
                listar();
                break;
            case "guardar":
                if (nuevoLibro)
                    libro = new Libro();
                else
                    libro = libroSeleccionado;

                libro.setTitulo(view.tfTitulo.getText());
                libro.setDescripcion(view.tfDescripcion.getText());
                libro.setAutor(view.tfAutor.getText());
                libro.setFecha(view.dcFecha.getDate());

                if (nuevoLibro)
                    model.anadirLibro(libro);
                else
                    model.modificarLibro(libro);

                modoEdicion(false);
                listar();
                break;
            case "eliminar":
                if ((Util.mensajeConfirmacion("¿Está seguro?", "Eliminar Libro")) == JOptionPane.NO_OPTION)
                    return;
                libro = (Libro) view.lLibros.getSelectedValue();
                model.eliminarLibro(libro.getTitulo());
                listar();
                break;
            case "cancelar":
                modoEdicion(false);
                break;
            default:
                break;
        }
    }

    private void modoEdicion(boolean edicion) {
        view.tfTitulo.setEditable(edicion);
        view.tfDescripcion.setEditable(edicion);
        view.tfAutor.setEditable(edicion);
        view.dcFecha.setEnabled(edicion);

        view.btCancelar.setEnabled(edicion);
        view.btGuardar.setEnabled(edicion);
        view.btEliminar.setEnabled(!edicion);
        view.btModificar.setEnabled(!edicion);
        view.btNuevo.setEnabled(!edicion);
    }

    private void cargar() {
        view.tfTitulo.setText(libroSeleccionado.getTitulo());
        view.tfDescripcion.setText(libroSeleccionado.getDescripcion());
        view.tfAutor.setText(libroSeleccionado.getAutor());
        view.dcFecha.setDate(libroSeleccionado.getFecha());
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        libroSeleccionado = (Libro) view.lLibros.getSelectedValue();
        if (libroSeleccionado != null)
            cargar();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getSource() == view.tfBuscar) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                if (view.tfBuscar.getText().equals("")) {
                    listar();
                    return;
                }

                List<Libro> listaLibros = new ArrayList<>();
                try {
                    listaLibros = model.buscarLibro(view.tfBuscar.getText());
                } catch (ParseException pe) {
                    Util.mensajeError("Algún dato no se puede cargar", "Error al cargar");
                }
                dlmLibros.removeAllElements();
                for (Libro libro : listaLibros)
                    dlmLibros.addElement(libro);
            }
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
        view.tfBuscar.selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {

    }
}
