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
public class TablaSimbolos {
    
    private static final Map<String, SimboloTabla> TABLAS = new HashMap<>();
    
    private TablaSimbolos() {
    }
    
    public static void registrarTabla(SimboloTabla tabla){
        TABLAS.put(tabla.getNombre().toLowerCase(), tabla);
    }
 
    public static boolean existeTabla(String nombreTabla){
        return TABLAS.containsKey(nombreTabla.toLowerCase());
    }
    
    public static SimboloTabla obtenerTabla(String nombreTabla){
        return TABLAS.get(nombreTabla.toLowerCase());
    }
    
    public static void limpiar(){
        TABLAS.clear();
    }
}
