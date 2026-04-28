/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

/**
 *
 * @author alancervantes
 */
public class ItemSelect {
    
    private final String nombreColumna;
    private final TipoToken operacionAgregacion;

    public ItemSelect(String nombreColumna, TipoToken operacionAgregacion) {
        this.nombreColumna = nombreColumna;
        this.operacionAgregacion = operacionAgregacion;
    }

    public String getNombreColumna() {
        return nombreColumna;
    }

    public TipoToken getOperacionAgregacion() {
        return operacionAgregacion;
    }
}
