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
    
    private static Map<String, SimboloTabla> tablas = new HashMap<>();
    
    public static void registrarTabla(SimboloTabla tabla){
        tablas.put(tabla.getNombre().toLowerCase(), tabla);
    }
 
    public static boolean existeTabla(String nombreTabla){
        return tablas.containsKey(nombreTabla.toLowerCase());
    }
    
    public static SimboloTabla obtenerTabla(String nombreTabla){
        return tablas.get(nombreTabla.toLowerCase());
    }
    
    public static void limpiar(){
        tablas.clear();
    }
}
