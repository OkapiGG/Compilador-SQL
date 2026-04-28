/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

/**
 *
 * @author alancervantes
 */
public class SimboloColumna {
    
    private final String nombre;
    private final TipoToken tipoDato;
    
    private boolean esLlavePrimaria;
    private boolean esNoNulo;
    private boolean esUnico;
    private boolean esAutoIncremento;
    
    public SimboloColumna(String nombre, TipoToken tipoDato){
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        
        this.esLlavePrimaria = false;
        this.esNoNulo = false;
        this.esUnico = false;
        this.esAutoIncremento = false;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoToken getTipoDato() {
        return tipoDato;
    }

    public boolean isEsLlavePrimaria() {
        return esLlavePrimaria;
    }

    public void setEsLlavePrimaria(boolean esLlavePrimaria) {
        this.esLlavePrimaria = esLlavePrimaria;
    }

    public boolean isEsNoNulo() {
        return esNoNulo;
    }

    public void setEsNoNulo(boolean esNoNulo) {
        this.esNoNulo = esNoNulo;
    }

    public boolean isEsUnico() {
        return esUnico;
    }

    public void setEsUnico(boolean esUnico) {
        this.esUnico = esUnico;
    }

    public boolean isEsAutoIncremento() {
        return esAutoIncremento;
    }

    public void setEsAutoIncremento(boolean esAutoIncremento) {
        this.esAutoIncremento = esAutoIncremento;
    }
    
}
