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
    
    private String nombreColumna;
    private TipoToken operacionAgregacion;
    private String alias;

    public ItemSelect(String nombreColumna, TipoToken operacionAgregacion, String alias) {
        this.nombreColumna = nombreColumna;
        this.operacionAgregacion = operacionAgregacion;
        this.alias = alias;
    }
    
    public ItemSelect(){
        
    }

    public String getNombreColumna() {
        return nombreColumna;
    }

    public void setNombreColumna(String nombreColumna) {
        this.nombreColumna = nombreColumna;
    }

    public TipoToken getOperacionAgregacion() {
        return operacionAgregacion;
    }

    public void setOperacionAgregacion(TipoToken operacionAgregacion) {
        this.operacionAgregacion = operacionAgregacion;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    
}
