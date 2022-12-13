package com.example.mongodb.gui;

import com.example.mongodb.base.Libro;
import com.example.mongodb.util.Constantes;
import com.example.mongodb.util.Util;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VentanaModel {

    private MongoClient mongoClient;
    private MongoDatabase db;

    public void conectar() {
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase(Constantes.NOMBRE_BASEDEDATOS);
    }

    public void desconectar() {
        mongoClient.close();
    }

    public void anadirLibro(Libro libro) {

        Document documento = new Document()
                .append("titulo", libro.getTitulo())
                .append("descripcion", libro.getDescripcion())
                .append("autor", libro.getAutor())
                .append("fecha", Util.formatFecha(libro.getFecha()));
        db.getCollection(Libro.COLECCION).insertOne(documento);

    }


    public void modificarLibro(Libro libro) {

        db.getCollection(Libro.COLECCION).replaceOne(new Document("_id", libro.getId()),
                new Document()
                        .append("titulo", libro.getTitulo())
                        .append("descripcion", libro.getDescripcion())
                        .append("autor", libro.getAutor())
                        .append("fecha", Util.formatFecha(libro.getFecha())));
    }

    public void eliminarLibro(String titulo) {
        db.getCollection(Libro.COLECCION).deleteOne(new Document("titulo", titulo));
    }

    public List<Libro> buscarLibro(String busqueda) throws ParseException {

        List<Libro> libros = new ArrayList<>();
        BasicDBObject documento = new BasicDBObject();
        documento.put("titulo", new BasicDBObject("$regex", "/*" + busqueda + "/*"));

        FindIterable findIterable = db.getCollection(Libro.COLECCION)
                .find(documento)
                .sort(new Document("titulo", 1));
        return getListaLibros(findIterable);

    }

    private List<Libro> getListaLibros(FindIterable<Document> findIterable) throws ParseException {

        List<Libro> libros = new ArrayList<>();
        Libro libro = null;
        Iterator<Document> iter = findIterable.iterator();

        while (iter.hasNext()) {
            Document documento = iter.next();
            libro = new Libro();
            libro.setId(documento.getObjectId("_id"));
            libro.setTitulo(documento.getString("titulo"));
            libro.setDescripcion(documento.getString("descripcion"));
            libro.setAutor(documento.getString("autor"));
            libro.setFecha(Util.parseFecha(documento.getString("fecha")));
            libros.add(libro);
        }

        return libros;
    }

    private Libro getLibro(Document documento) throws ParseException {
        Libro libro = new Libro();
        libro.setId(documento.getObjectId("_id"));
        libro.setTitulo(documento.getString("titulo"));
        libro.setDescripcion(documento.getString("descripcion"));
        libro.setAutor(documento.getString("autor"));
        libro.setFecha(Util.parseFecha(documento.getString("fecha")));

        return libro;
    }

    public List<Libro> getLibros() throws ParseException {

        FindIterable<Document> findIterable = db.getCollection(Libro.COLECCION).find();
        return getListaLibros(findIterable);
    }

    public Libro getLibro(String titulo) throws ParseException {

        FindIterable<Document> findIterable = db.getCollection(Libro.COLECCION).find(new Document("titulo", titulo));
        Document documento = findIterable.first();
        return getLibro(documento);
    }

    public List<Libro> getLibros(String autor) throws ParseException {

        FindIterable<Document> findIterable = db.getCollection(Libro.COLECCION).find(new Document("autor", autor));
        return getListaLibros(findIterable);
    }
}

