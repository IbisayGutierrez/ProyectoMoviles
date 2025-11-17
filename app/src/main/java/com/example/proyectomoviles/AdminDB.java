package com.example.proyectomoviles;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AdminDB extends SQLiteOpenHelper {
    public AdminDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table pelicula (codigo integer primary key , titulo text, duracion integer, genero text)");
        db.execSQL("create table cliente (cedula text primary key , nombre text, correo text, telefono text, contraseña text)");
        db.execSQL("create table funcion (id integer primary key autoincrement, fecha text, hora text, codigo_pelicula integer)");
        db.execSQL("create table detalle_funcion (id integer primary key autoincrement, idfuncion integer, cedula text, asiento text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public long insertarFuncion(String fecha, String hora, int codigo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("codigo_pelicula", codigo);
        return db.insert("funcion", null, values);
    }

    public void insertarDetalle(int idFuncion, String cedula, String asiento) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idfuncion", idFuncion);
        values.put("cedula", cedula);
        values.put("asiento", asiento);
        db.insert("detalle_funcion", null, values);
    }

}
