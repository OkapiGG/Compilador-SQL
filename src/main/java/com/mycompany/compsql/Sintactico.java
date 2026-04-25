/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alancervantes
 */
public class Sintactico {

    private List<Token> tokens;
    private int posicionActual;
    private Token tokenActual;

    public Sintactico(List<Token> tokens) {
        this.tokens = tokens;
        this.posicionActual = 0;
        if (!tokens.isEmpty()) {
            this.tokenActual = tokens.get(0);
        }
    }

    private void avanzar() {
        posicionActual++;
        if (posicionActual < tokens.size()) {
            tokenActual = tokens.get(posicionActual);
        } else {
            tokenActual = null;
        }
    }

    private void emparejar(TipoToken tipoEsperado) {
        if (tokenActual != null && tokenActual.getTipo() == tipoEsperado) {
            avanzar();
        } else {
            String lexema = (tokenActual != null) ? tokenActual.getLexema() : "Fin de codigo";
            throw new RuntimeException("Error Sintactico, se esperaba " + tipoEsperado + " pero se encontro '" + lexema + "'");
        }
    }

    public void analizarPrograma() {
        if (tokenActual == null) {
            System.out.println("No hay tokens para analizar.");
            return;
        }

        while (tokenActual != null) {
            if (tokenActual.getTipo() == TipoToken.Insertar) {
                analizarInsertar();
            } else if (tokenActual.getTipo() == TipoToken.Crear) {
                analizarCrear();
            } else if (tokenActual.getTipo() == TipoToken.Seleccionar) {
                analizarSeleccionar();
            } else if (tokenActual.getTipo() == TipoToken.Actualizar) {
                analizarActualizar();
            } else if (tokenActual.getTipo() == TipoToken.Eliminar) {
                analizarEliminar();
            } else if (tokenActual.getTipo() == TipoToken.Truncar) {
                analizarTruncar();
            } else {
                throw new RuntimeException("Comando principal no reconocido o falta implementarlo: " + tokenActual.getLexema());
            }
        }
        System.out.println("Analisis sintactico correcto.");
    }

    private void analizarInsertar() {
        emparejar(TipoToken.Insertar);
        emparejar(TipoToken.En);
        emparejar(TipoToken.Tabla);
        
        String nombreTabla = tokenActual.getLexema();
  
        emparejar(TipoToken.Identificador);
        
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla " + nombreTabla + " no existe en la base de datos.");
        }
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        
        emparejar(TipoToken.Valores);
        emparejar(TipoToken.ParentesisAbre);

        analizarListaValores(tablaDestino);

        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
        
        System.out.println("Semantica: Insercion validada correctamente para la tabla " + nombreTabla);
    }

    private void analizarListaValores(SimboloTabla tablaDestino) {
        int indice = 0;
        int totalColumnas = tablaDestino.getCantidadColumnas();
        
        if(indice >= totalColumnas){
            throw new RuntimeException("Error semántico: Se enviaron más valores de los que la tabla '" + tablaDestino.getNombre() + "' soporta.");
        }
        
        SimboloColumna columnaActual = tablaDestino.obtenerColumnaPorIndice(indice);
        analizarValor(columnaActual);
        
        indice++;
        
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            
            if (indice >= totalColumnas) {
                throw new RuntimeException("Error semántico: Se enviaron más valores de los que la tabla '" + tablaDestino.getNombre() + "' soporta.");
            }
            
            columnaActual = tablaDestino.obtenerColumnaPorIndice(indice);
            
            analizarValor(columnaActual);
            
            indice++;            
        }
        if (indice < totalColumnas) {
            throw new RuntimeException("Error semántico: Faltan valores en el INSERT. La tabla '" + tablaDestino.getNombre() + "' requiere " + totalColumnas + " datos, pero se dieron " + indice + ".");
        }
    }

    private void analizarValor(SimboloColumna columnaEsperada) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Fin inesperado");
        }
        
        TipoToken tipoRecibido = tokenActual.getTipo();
        TipoToken tipoEsperado = columnaEsperada.getTipoDato();
        String nombreCol = columnaEsperada.getNombre();
        
        if(tipoRecibido == TipoToken.Cadena){
            if(tipoEsperado != TipoToken.Texto && tipoEsperado != TipoToken.Fecha){
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió texto.");
            }
            emparejar(TipoToken.Cadena);
        }
        else if(tipoRecibido == TipoToken.NumeroEntero){
            if (tipoEsperado != TipoToken.Entero && tipoEsperado != TipoToken.Decimal) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un número entero.");
            }
            emparejar(TipoToken.NumeroEntero);
        }
        else if (tipoRecibido == TipoToken.NumeroDecimal) {
            if (tipoEsperado != TipoToken.Decimal) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un número decimal.");
            }
            emparejar(TipoToken.NumeroDecimal);
        }         
        else if (tipoRecibido == TipoToken.Verdadero || tipoRecibido == TipoToken.Falso) {
            if (tipoEsperado != TipoToken.Booleano) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un booleano.");
            }
            emparejar(tipoRecibido); 
        }         
        else if (tipoRecibido == TipoToken.Nulo) {
            emparejar(TipoToken.Nulo);
        }         
        else {
            throw new RuntimeException("Error Sintáctico: Se esperaba un valor pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarValor() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Fin inesperado");
        }

        if (tokenActual.getTipo() == TipoToken.NumeroEntero
                || tokenActual.getTipo() == TipoToken.NumeroDecimal
                || tokenActual.getTipo() == TipoToken.Cadena
                || tokenActual.getTipo() == TipoToken.Verdadero
                || tokenActual.getTipo() == TipoToken.Falso
                || tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(tokenActual.getTipo());
        } else {
            throw new RuntimeException("Error Sintáctico: Se esperaba un valor literal pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarCrear() {
        emparejar(TipoToken.Crear);

        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba 'tabla' o 'base'");
        }

        if (tokenActual.getTipo() == TipoToken.Tabla) {
            analizarCrearTabla();
        } else if (tokenActual.getTipo() == TipoToken.Base_de_Datos
                || tokenActual.getTipo() == TipoToken.Base
                || tokenActual.getTipo() == TipoToken.BaseDeDatos) {
            analizarCrearBase();
        } else {
            throw new RuntimeException("Error: después de 'crear' se esperaba 'tabla' o 'base' pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarSeleccionar() {
        emparejar(TipoToken.Seleccionar);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Distinto) {
            analizarDistinto();
        }

        List<String> columnasPerdidas = analizarListaItemsSelect();
        emparejar(TipoToken.De);
        
        String nombreTabla = tokenActual.getLexema();
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semantico: La tabla " + nombreTabla + " no existe");
        }
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        
        for(String nombreColumna : columnasPerdidas){
            if(!tablaDestino.existeColumna(nombreColumna)){
                throw new RuntimeException("Error semántico: La columna '" + nombreColumna + "' no existe en la tabla '" + nombreTabla + "'.");
            }
        }
        
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere(tablaDestino);
        }

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.PuntoComa) {
            emparejar(TipoToken.PuntoComa);
        }
        
        System.out.println("Semántica: Consulta SELECT validada correctamente.");
    }

    private void analizarActualizar() {
        emparejar(TipoToken.Actualizar);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Establecer);

        analizarListaAsignaciones();

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere(null);
        }
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarEliminar() {
        emparejar(TipoToken.Eliminar);
        emparejar(TipoToken.De);
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            analizarOpcWhere(null);
        }
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarCrearTabla() {
        emparejar(TipoToken.Tabla);
        
        String nombreTabla = tokenActual.getLexema();
        emparejar(TipoToken.Identificador);
        
        if(TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla '" + nombreTabla + "' ya existe.");
        }
        
        SimboloTabla nuevaTabla = new SimboloTabla(nombreTabla);
 
        emparejar(TipoToken.ParentesisAbre);

        analizarDefinicionesCol(nuevaTabla);

        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
        
        TablaSimbolos.registrarTabla(nuevaTabla);
        System.out.println("Semántica: Tabla " + nombreTabla + " registrada exitosamente con sus columnas");
    }

    private void analizarCrearBase() {
        if (tokenActual.getTipo() == TipoToken.Base_de_Datos) {
            emparejar(TipoToken.Base_de_Datos);
        } else if (tokenActual.getTipo() == TipoToken.Base) {
            emparejar(TipoToken.Base);
        } else if (tokenActual.getTipo() == TipoToken.BaseDeDatos) {
            emparejar(TipoToken.BaseDeDatos);
        } else {
            throw new RuntimeException("Error: se esperaba 'base'");
        }

        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarTruncar() {
        emparejar(TipoToken.Truncar);
        emparejar(TipoToken.Tabla);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
    }

    private void analizarDistinto() {
        emparejar(TipoToken.Distinto);
    }

    private List<String> analizarColumnas() {
        ArrayList<String> lista = new ArrayList<>();
        lista.add(tokenActual.getLexema());
        emparejar(TipoToken.Identificador);
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            
            lista.add(tokenActual.getLexema());
            emparejar(TipoToken.Identificador);
        }
        return lista;
    }

    private List<String> analizarListaItemsSelect() {
        return analizarColumnas();
    }

    private void analizarItemSelect() {
        analizarExpresionCol();
        analizarAlias();
    }

    private void analizarExpresionCol() {
        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en EXPRESION_COL");
        }

        if (tokenActual.getTipo() == TipoToken.Identificador) {
            emparejar(TipoToken.Identificador);
        } else if (tokenActual.getTipo() == TipoToken.Contar
                || tokenActual.getTipo() == TipoToken.Suma
                || tokenActual.getTipo() == TipoToken.Promedio
                || tokenActual.getTipo() == TipoToken.Maximo
                || tokenActual.getTipo() == TipoToken.Minimo) {
            analizarAgregacion();
            emparejar(TipoToken.ParentesisAbre);
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.ParentesisCierra);
        } else {
            throw new RuntimeException("EXPRESION_COL invalida: " + tokenActual.getLexema());
        }
    }

    private void analizarAgregacion() {
        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en AGREGACION");
        }

        switch (tokenActual.getTipo()) {
            case Contar:
            case Suma:
            case Promedio:
            case Maximo:
            case Minimo:
                emparejar(tokenActual.getTipo());
                break;
            default:
                throw new RuntimeException("Agregacion inválida: " + tokenActual.getLexema());
        }
    }

    private void analizarAlias() {
        if (tokenActual != null && (tokenActual.getTipo() == TipoToken.Como
                || tokenActual.getTipo() == TipoToken.As
                || tokenActual.getTipo() == TipoToken.Alias)) {
            emparejar(tokenActual.getTipo());
            emparejar(TipoToken.Identificador);
        }
    }

    private void analizarJoin() {
        if (tokenActual == null) {
            return;
        }
        if (tokenActual.getTipo() == TipoToken.UnirInterno
                || tokenActual.getTipo() == TipoToken.UnirIzquierdo
                || tokenActual.getTipo() == TipoToken.UnirDerecho
                || tokenActual.getTipo() == TipoToken.Unir) {
            analizarTipoJoin();
            emparejar(TipoToken.Unir);
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.En);
            analizarCondicion(null);
        }
        throw new UnsupportedOperationException("Falta implementar: analizarJoin");
    }

    private void analizarTipoJoin() {
        if (tokenActual == null) {
            return;
        }

        switch (tokenActual.getTipo()) {
            case UnirInterno:
            case UnirIzquierdo:
            case UnirDerecho:
                emparejar(tokenActual.getTipo());
                break;
            default:
                break;
        }
        throw new UnsupportedOperationException("Falta implementar: analizarTipoJoin");
    }

    private void analizarListaAsignaciones() {
        analizarAsignacion();
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            analizarAsignacion();
        }
    }

    private void analizarAsignacion() {
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Igual);

        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en ASIGNACION");
        }

        if (tokenActual.getTipo() == TipoToken.NumeroEntero
                || tokenActual.getTipo() == TipoToken.NumeroDecimal
                || tokenActual.getTipo() == TipoToken.Cadena
                || tokenActual.getTipo() == TipoToken.Verdadero
                || tokenActual.getTipo() == TipoToken.Falso
                || tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(tokenActual.getTipo());
        } else {
            throw new RuntimeException("Valor inválido en ASIGNACION: " + tokenActual.getLexema());
        }
    }

    // Ese es mio
    private void analizarDefinicionesCol(SimboloTabla tablaActual) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba definición de columna");
        }

        if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
            analizarFk();
        } else {
            SimboloColumna col = analizarColumnaDef();
            tablaActual.agregarColumna(col);
        }

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);

            if (tokenActual == null) {
                throw new RuntimeException("Error: se esperaba otra definición después de la coma");
            }

            if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
                analizarFk();
            } else {
                SimboloColumna col = analizarColumnaDef();
                if(tablaActual.existeColumna(col.getNombre())){
                    throw new RuntimeException("Error semántico: la columna '" + col.getNombre() + "' fue declarada mas de una vez en la tabla.");     
                }
                tablaActual.agregarColumna(col);
            }
        }
    }

    private SimboloColumna analizarColumnaDef() {
        String nombreColumna = tokenActual.getLexema();
        emparejar(TipoToken.Identificador);
        
        TipoToken tipoDato = analizarTipoDato();
        SimboloColumna nuevaColumna = new SimboloColumna(nombreColumna, tipoDato);
        
        analizarRestricciones();
        
        return nuevaColumna;
    }

    private TipoToken analizarTipoDato() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba un tipo de dato pero se encontró fin de código");
        }
        
        TipoToken tipoEncontrado = tokenActual.getTipo();

        if (tipoEncontrado == TipoToken.Entero) {
            emparejar(TipoToken.Entero);
        } else if (tipoEncontrado == TipoToken.Texto) {
            emparejar(TipoToken.Texto);
        } else if (tipoEncontrado == TipoToken.Decimal) {
            emparejar(TipoToken.Decimal);
        } else if (tipoEncontrado == TipoToken.Fecha) {
            emparejar(TipoToken.Fecha);
        } else if (tipoEncontrado == TipoToken.Booleano) {
            emparejar(TipoToken.Booleano);
        } else {
            throw new RuntimeException("Error sintáctico: tipo de dato no válido: " + tokenActual.getLexema());
        }
        
        return tipoEncontrado;
    }

    private void analizarRestricciones() {
        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.NoNulo
                || tokenActual.getTipo() == TipoToken.Nulo
                || tokenActual.getTipo() == TipoToken.Unico
                || tokenActual.getTipo() == TipoToken.LlavePrimaria
                || tokenActual.getTipo() == TipoToken.AutoIncremento
                || tokenActual.getTipo() == TipoToken.Defecto)) {
            analizarRestriccion();
        }
    }

    private void analizarRestriccion() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba una restricción pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.NoNulo) {
            emparejar(TipoToken.NoNulo);
        } else if (tokenActual.getTipo() == TipoToken.Nulo) {
            emparejar(TipoToken.Nulo);
        } else if (tokenActual.getTipo() == TipoToken.Unico) {
            emparejar(TipoToken.Unico);
        } else if (tokenActual.getTipo() == TipoToken.LlavePrimaria) {
            emparejar(TipoToken.LlavePrimaria);
        } else if (tokenActual.getTipo() == TipoToken.AutoIncremento) {
            emparejar(TipoToken.AutoIncremento);
        } else if (tokenActual.getTipo() == TipoToken.Defecto) {
            emparejar(TipoToken.Defecto);
            //Falta el paso 4
            //analizarValor();
        } else {
            throw new RuntimeException("Error sintáctico: restricción no válida: " + tokenActual.getLexema());
        }
    }

    private void analizarFk() {
        emparejar(TipoToken.LlaveForanea);
        emparejar(TipoToken.ParentesisAbre);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.Referencia);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisAbre);
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
    }

    private void analizarOpcWhere(SimboloTabla tabla) {
        emparejar(TipoToken.Donde);
        analizarExpresionLogica(tabla);
    }

    private void analizarExpresionLogica(SimboloTabla tabla) {
        analizarCondicion(tabla);

        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.Y
                || tokenActual.getTipo() == TipoToken.O
                || tokenActual.getTipo() == TipoToken.AND
                || tokenActual.getTipo() == TipoToken.OR)) {
            analizarOperadorLogico();
            analizarCondicion(tabla);
        }
    }

    private void analizarOperadorLogico() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba operador lógico pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.Y) {
            emparejar(TipoToken.Y);
        } else if (tokenActual.getTipo() == TipoToken.O) {
            emparejar(TipoToken.O);
        } else if (tokenActual.getTipo() == TipoToken.AND) {
            emparejar(TipoToken.AND);
        } else if (tokenActual.getTipo() == TipoToken.OR) {
            emparejar(TipoToken.OR);
        } else {
            throw new RuntimeException("Error sintáctico: se esperaba operador lógico y/o pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarCondicion(SimboloTabla tabla) {
        String nombreColumna = tokenActual.getLexema();
        
        if(!tabla.existeColumna(nombreColumna)){
            throw new RuntimeException("Error semántico: La columna " + nombreColumna + "no existe en la tabla " + tabla.getNombre());
        }
        
        SimboloColumna columnaCondicion = tabla.obtenerColumna(nombreColumna);
        
        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Entre) {
            emparejar(TipoToken.Entre);
            analizarValor(columnaCondicion);
            emparejar(TipoToken.Y);
            analizarValor(columnaCondicion);
        } else {
            analizarOpRel();
            analizarValor(columnaCondicion);
        }
    }

    private void analizarOpRel() {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Se esperaba un operador relacional pero se encontro fin de codigo");
        }

        if (tokenActual.getTipo() == TipoToken.Igual) {
            emparejar(TipoToken.Igual);
        } else if (tokenActual.getTipo() == TipoToken.Diferente) {
            emparejar(TipoToken.Diferente);
        } else if (tokenActual.getTipo() == TipoToken.MayorQue) {
            emparejar(TipoToken.MayorQue);
        } else if (tokenActual.getTipo() == TipoToken.MenoQue) {
            emparejar(TipoToken.MenoQue);
        } else if (tokenActual.getTipo() == TipoToken.MayorIgualQue) {
            emparejar(TipoToken.MayorIgualQue);
        } else if (tokenActual.getTipo() == TipoToken.MenorIgualQue) {
            emparejar(TipoToken.MenorIgualQue);
        } else {
            throw new RuntimeException("Error Sintactico: Se esperaba un operador relacional (=, >, <, etc) pero se encontro " + tokenActual.getLexema());
        }
    }

    private void analizarGroupBy() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Grupo) {
            emparejar(TipoToken.Grupo);
            emparejar(TipoToken.Por);

            analizarListaId();
            analizarHaving();
        }

        throw new UnsupportedOperationException("Falta implementar: analizarGroupBy");
    }

    private void analizarHaving() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Tener) {
            emparejar(TipoToken.Tener);
            analizarExpresionLogica(null);
        }

        throw new UnsupportedOperationException("Falta implementar: analizarHaving");
    }

    private void analizarOrderBy() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Ordernar) {
            emparejar(TipoToken.Ordernar);
            emparejar(TipoToken.Por);

            analizarListaOrden();

        }
        throw new UnsupportedOperationException("Falta implementar: analizarOrderBy");
    }

    private void analizarListaOrden() {

        emparejar(TipoToken.Identificador);

        analizarSentido();

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            emparejar(TipoToken.Identificador);
            analizarSentido();
        }

        throw new UnsupportedOperationException("Falta implementar: analizarListaOrden");
    }

    private void analizarSentido() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Asc) {
            emparejar(TipoToken.Asc);
        } else if (tokenActual != null && tokenActual.getTipo() == TipoToken.Desc) {
            emparejar(TipoToken.Desc);
        }

        throw new UnsupportedOperationException("Falta implementar: analizarSentido");
    }

    private void analizarLimit() {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Limite) {
            emparejar(TipoToken.Limite);

            emparejar(TipoToken.NumeroEntero);
        }
        throw new UnsupportedOperationException("Falta implementar: analizarLimit");
    }

    private void analizarListaId() {

        emparejar(TipoToken.Identificador);

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            emparejar(TipoToken.Coma);
            emparejar(TipoToken.Identificador);
        }
    }
}
