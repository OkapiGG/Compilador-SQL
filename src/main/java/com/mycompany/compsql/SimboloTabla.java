/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alancervantes
 */
public class SimboloTabla {
    
    private String nombre;
    private Map<String, SimboloColumna> columnas;
    
    public SimboloTabla(String nombre){
        this.nombre = nombre;
        this.columnas = new HashMap<>();
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void agregarColumna(SimboloColumna col){
        columnas.put(col.getNombre(), col);
    }
    
    public boolean existeColumna(String nombreColumna){
        return columnas.containsKey(nombreColumna);
    }
    
    public SimboloColumna obtenerColumna(String nombreColumna){
        return columnas.get(nombreColumna);
    }
    
    public int getCantidadColumnas(){
        return columnas.size();
    }
    
    public SimboloColumna obtenerColumnaPorIndice(int indice){
        return (SimboloColumna) columnas.values().toArray()[indice];
    }
    
}
