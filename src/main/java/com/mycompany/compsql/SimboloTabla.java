/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author alancervantes
 */
public class SimboloTabla {
    
    private final String nombre;
    private final Map<String, SimboloColumna> columnas;
    
    public SimboloTabla(String nombre){
        this.nombre = nombre;
        this.columnas = new LinkedHashMap<>();
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void agregarColumna(SimboloColumna col){
        columnas.put(col.getNombre().toLowerCase(), col);
    }
    
    public boolean existeColumna(String nombreColumna){
        return columnas.containsKey(nombreColumna.toLowerCase());
    }
    
    public SimboloColumna obtenerColumna(String nombreColumna){
        return columnas.get(nombreColumna.toLowerCase());
    }
    
    public int getCantidadColumnas(){
        return columnas.size();
    }
    
    public SimboloColumna obtenerColumnaPorIndice(int indice){
        return columnas.values().stream().skip(indice).findFirst().orElse(null);
    }
    
}
