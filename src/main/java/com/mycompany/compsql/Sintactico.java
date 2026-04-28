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
            switch (tokenActual.getTipo()) {
                case Insertar:
                    analizarInsertar();
                    break;
                case Crear:
                    analizarCrear();
                    break;
                case Seleccionar:
                    analizarSeleccionar();
                    break;
                case Actualizar:
                    analizarActualizar();
                    break;
                case Eliminar:
                    analizarEliminar();
                    break;
                case Truncar:
                    analizarTruncar();
                    break;
                default:
                    throw new RuntimeException("Comando principal no reconocido o falta implementarlo: " + tokenActual.getLexema());
            }
        }
        System.out.println("Analisis sintactico correcto.");
    }

    private void analizarInsertar() {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        
        emparejar(TipoToken.Insertar);
        emparejar(TipoToken.En);
        emparejar(TipoToken.Tabla);
        
        String nombreTabla = tokenActual.getLexema();
        
        sql.append(nombreTabla);
  
        emparejar(TipoToken.Identificador);
        
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla " + nombreTabla + " no existe en la base de datos.");
        }
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        
        sql.append(" VALUES ");
        
        emparejar(TipoToken.Valores);
        
        sql.append(" (");
        emparejar(TipoToken.ParentesisAbre);

        analizarListaValores(tablaDestino, sql);

        sql.append(" );");
        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
        
        System.out.println("Semantica: Insercion validada correctamente para la tabla " + nombreTabla);
        
        String consultaFinal = sql.toString();
        
        ConexionBD.ejecutar(consultaFinal);
    }

    private void analizarListaValores(SimboloTabla tablaDestino, StringBuilder sql) {
        int indice = 0;
        int totalColumnas = tablaDestino.getCantidadColumnas();
        
        if(indice >= totalColumnas){
            throw new RuntimeException("Error semántico: Se enviaron más valores de los que la tabla '" + tablaDestino.getNombre() + "' soporta.");
        }
        
        SimboloColumna columnaActual = tablaDestino.obtenerColumnaPorIndice(indice);
        analizarValor(columnaActual, sql);
        
        indice++;
        
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            
            sql.append(", ");
            
            emparejar(TipoToken.Coma);
            
            if (indice >= totalColumnas) {
                throw new RuntimeException("Error semántico: Se enviaron más valores de los que la tabla '" + tablaDestino.getNombre() + "' soporta.");
            }
                        
            columnaActual = tablaDestino.obtenerColumnaPorIndice(indice);
            
            analizarValor(columnaActual, sql);
            
            indice++;            
        }
        if (indice < totalColumnas) {
            throw new RuntimeException("Error semántico: Faltan valores en el INSERT. La tabla '" + tablaDestino.getNombre() + "' requiere " + totalColumnas + " datos, pero se dieron " + indice + ".");
        }
    }

    private void analizarValor(SimboloColumna columnaEsperada, StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Fin inesperado");
        }
        
        TipoToken tipoRecibido = tokenActual.getTipo();
        TipoToken tipoEsperado = columnaEsperada.getTipoDato();
        String nombreCol = columnaEsperada.getNombre();
        
        String valorLiteral = tokenActual.getLexema();
        
        if(tipoRecibido == TipoToken.Cadena){
            if(tipoEsperado != TipoToken.Texto && tipoEsperado != TipoToken.Fecha){
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió texto.");
            }
            String textoLimpio = valorLiteral.replace("\"", "");
            sql.append("'").append(textoLimpio).append("'");
            emparejar(TipoToken.Cadena);
        }
        else if(tipoRecibido == TipoToken.NumeroEntero){
            if (tipoEsperado != TipoToken.Entero && tipoEsperado != TipoToken.Decimal) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un número entero.");
            }
            sql.append(valorLiteral);
            emparejar(TipoToken.NumeroEntero);
        }
        else if (tipoRecibido == TipoToken.NumeroDecimal) {
            if (tipoEsperado != TipoToken.Decimal) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un número decimal.");
            }
            sql.append(valorLiteral);
            emparejar(TipoToken.NumeroDecimal);
        }         
        else if (tipoRecibido == TipoToken.Verdadero || tipoRecibido == TipoToken.Falso) {
            if (tipoEsperado != TipoToken.Booleano) {
                throw new RuntimeException("Error semántico: Incompatibilidad de tipos. La columna '" + nombreCol + "' espera " + tipoEsperado + " pero recibió un booleano.");
            }
            sql.append(valorLiteral.equalsIgnoreCase("verdadero") ? "TRUE" : "FALSE");
            emparejar(tipoRecibido); 
        }         
        else if (tipoRecibido == TipoToken.Nulo) {
            sql.append("NULL");
            emparejar(TipoToken.Nulo);
        }         
        else {
            throw new RuntimeException("Error Sintáctico: Se esperaba un valor pero se encontró " + tokenActual.getLexema());
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
        StringBuilder sql = new StringBuilder("SELECT ");
        
        emparejar(TipoToken.Seleccionar);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Distinto) {
            analizarDistinto(sql);
        }

        List<ItemSelect> columnasPerdidas = analizarListaItemsSelect(sql);
        sql.append(" FROM ");
        emparejar(TipoToken.De);
        
        
        String nombreTabla = tokenActual.getLexema();
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semantico: La tabla " + nombreTabla + " no existe");
        }
        
        sql.append(nombreTabla);
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        List<SimboloTabla> tablasConsulta = new ArrayList<>();
        tablasConsulta.add(tablaDestino);
        
        emparejar(TipoToken.Identificador);
        analizarJoin(sql, tablasConsulta);
        
        for(ItemSelect item : columnasPerdidas){
            String nombreColumna = item.getNombreColumna();
            SimboloColumna columna = resolverColumna(tablasConsulta, nombreColumna);
            if(columna == null){
                throw new RuntimeException("Error semántico: La columna " + nombreColumna + " no existe en las tablas de la consulta");
            }
            TipoToken operacion = item.getOperacionAgregacion();
            
            if(operacion == TipoToken.Suma || operacion == TipoToken.Promedio){
                TipoToken tipoDato = columna.getTipoDato();
                
                if(tipoDato != TipoToken.Entero && tipoDato != TipoToken.Decimal){
                    throw new RuntimeException("Error semantico: No se puede aplicar la funcion " + operacion + " a la columna " + nombreColumna + " porque es de tipo " + tipoDato);
                }
            }
            
        }

        analizarOpcWhere(tablasConsulta, sql);
        analizarGroupBy(sql, tablasConsulta);
        analizarOrderBy(sql, tablasConsulta);
        analizarLimit(sql);
        
        sql.append(";");
        emparejar(TipoToken.PuntoComa);
        
        ConexionBD.ejecutarConsulta(sql.toString());
        
        System.out.println("Semántica: Consulta SELECT validada correctamente.");
    }

    private void analizarActualizar() {
        StringBuilder sql = new StringBuilder("UPDATE ");
        emparejar(TipoToken.Actualizar);
        
        String nombreTabla = tokenActual.getLexema();
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla " + nombreTabla + " no existe.");
        }
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        sql.append(nombreTabla + " ");
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Establecer);
        sql.append("SET ");

        analizarListaAsignaciones(tablaDestino, sql);

        analizarOpcWhere(tablaDestino, sql);
        sql.append(";");
        emparejar(TipoToken.PuntoComa);
        ConexionBD.ejecutar(sql.toString());
        System.out.println("Semantica: Consulta UPDATE validada correctamente.");
    }

    private void analizarEliminar() {
        StringBuilder sql = new StringBuilder("DELETE FROM ");
        
        emparejar(TipoToken.Eliminar);
        emparejar(TipoToken.De);
        
        String nombreTabla = tokenActual.getLexema();
        if(!TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla " + nombreTabla + " no existe.");
        }
        
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(nombreTabla);
        
        sql.append(tokenActual.getLexema());
        
        emparejar(TipoToken.Identificador);

        analizarOpcWhere(tablaDestino, sql);
        
        sql.append(";");
        emparejar(TipoToken.PuntoComa);
        
        System.out.println("Semantica: Consulta DELETE validada correctamente");
        
        ConexionBD.ejecutar(sql.toString());
    }

    private void analizarCrearTabla() {
        StringBuilder sqlTraducido = new StringBuilder("CREATE TABLE ");
        
        emparejar(TipoToken.Tabla);
        
        String nombreTabla = tokenActual.getLexema();
        
        sqlTraducido.append(nombreTabla).append(" (");
        
        emparejar(TipoToken.Identificador);
        
        if(TablaSimbolos.existeTabla(nombreTabla)){
            throw new RuntimeException("Error semántico: La tabla " + nombreTabla + " ya existe.");
        }
        
        SimboloTabla nuevaTabla = new SimboloTabla(nombreTabla);
 
        emparejar(TipoToken.ParentesisAbre);

        analizarDefinicionesCol(nuevaTabla, sqlTraducido);

        emparejar(TipoToken.ParentesisCierra);
        emparejar(TipoToken.PuntoComa);
        
        sqlTraducido.append(");");
        
        TablaSimbolos.registrarTabla(nuevaTabla);   
        System.out.println("Semántica: Tabla " + nombreTabla + " registrada exitosamente con sus columnas");
        
        String consultaFinal = sqlTraducido.toString();
        System.out.println("Sentencia generada: " + consultaFinal);
        
        ConexionBD.ejecutar(consultaFinal);
    }

    private void analizarCrearBase() {
        StringBuilder sql = new StringBuilder("CREATE DATABASE ");
        
        if (tokenActual.getTipo() == TipoToken.Base_de_Datos) {
            emparejar(TipoToken.Base_de_Datos);
        } else if (tokenActual.getTipo() == TipoToken.Base) {
            emparejar(TipoToken.Base);
        } else if (tokenActual.getTipo() == TipoToken.BaseDeDatos) {
            emparejar(TipoToken.BaseDeDatos);
        } else {
            throw new RuntimeException("Error: se esperaba 'base'");
        }

        sql.append(tokenActual.getLexema()).append(";");
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
        
        ConexionBD.ejecutar(sql.toString());
    }

    private void analizarTruncar() {
        StringBuilder sql = new StringBuilder("TRUNCATE TABLE ");
        
        emparejar(TipoToken.Truncar);
        emparejar(TipoToken.Tabla);
        sql.append(tokenActual.getLexema()).append(";");
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.PuntoComa);
        
        ConexionBD.ejecutar(sql.toString());
    }

    private void analizarDistinto(StringBuilder sql) {
        sql.append("DISTINCT ");
        emparejar(TipoToken.Distinto);
    }

    private List<ItemSelect> analizarListaItemsSelect(StringBuilder sql) {
        
        List<ItemSelect> lista = new ArrayList<>();
        
        lista.add(analizarItemSelect(sql)); 

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            sql.append(", ");
            emparejar(TipoToken.Coma);
            
            lista.add(analizarItemSelect(sql));
        }
        
        return lista;
    }

    private ItemSelect analizarItemSelect(StringBuilder sql) {
        ItemSelect item = analizarExpresionCol(sql);
        analizarAlias(sql);
        return item;
    }

    private ItemSelect analizarExpresionCol(StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Fin inesperado en EXPRESION_COL");
        }

        if (tokenActual.getTipo() == TipoToken.Identificador) {
            String nombreColumna = tokenActual.getLexema();
            
            sql.append(nombreColumna);
            
            emparejar(TipoToken.Identificador);
            return new ItemSelect(nombreColumna, null);
        } else if (tokenActual.getTipo() == TipoToken.Contar
                || tokenActual.getTipo() == TipoToken.Suma
                || tokenActual.getTipo() == TipoToken.Promedio
                || tokenActual.getTipo() == TipoToken.Maximo
                || tokenActual.getTipo() == TipoToken.Minimo) {
            TipoToken operacionAgregacion = tokenActual.getTipo();
            analizarAgregacion(sql);
            
            sql.append("(");
            emparejar(TipoToken.ParentesisAbre);
            
            String nombreColumna = tokenActual.getLexema();
            sql.append(nombreColumna);
            
            emparejar(TipoToken.Identificador);
            
            sql.append(")");
            emparejar(TipoToken.ParentesisCierra);
            return new ItemSelect(nombreColumna, operacionAgregacion);
        } else {
            throw new RuntimeException("EXPRESION_COL invalida: " + tokenActual.getLexema());
        }
    }

    private void analizarAgregacion(StringBuilder sql) {
        if (tokenActual == null){
            throw new RuntimeException("Fin inesperado en AGREGACION");
        }
        
        switch (tokenActual.getTipo()) {
            case Contar:
                sql.append("COUNT");
                emparejar(TipoToken.Contar);
                break;
            case Suma:
                sql.append("SUM");
                emparejar(TipoToken.Suma);
                break;
            case Promedio:
                sql.append("AVG");
                emparejar(TipoToken.Promedio);
                break;
            case Maximo:
                sql.append("MAX");
                emparejar(TipoToken.Maximo);
                break;
            case Minimo:
                sql.append("MIN");
                emparejar(TipoToken.Minimo);
                break;
            default:
                throw new RuntimeException("Agregacion inválida: " + tokenActual.getLexema());
        }
    }

    private void analizarAlias(StringBuilder sql) {
        if (tokenActual != null && (tokenActual.getTipo() == TipoToken.Como
                || tokenActual.getTipo() == TipoToken.As
                || tokenActual.getTipo() == TipoToken.Alias)) {
            sql.append(" AS ");
            emparejar(tokenActual.getTipo());
            
            sql.append(tokenActual.getLexema());
            
            emparejar(TipoToken.Identificador);
        }
    }

    private void analizarJoin(StringBuilder sql, List<SimboloTabla> tablasConsulta) {
        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.UnirInterno
                || tokenActual.getTipo() == TipoToken.UnirIzquierdo
                || tokenActual.getTipo() == TipoToken.UnirDerecho
                || tokenActual.getTipo() == TipoToken.Unir)) {
            analizarTipoJoin(sql);
            
            if (tokenActual.getTipo() == TipoToken.Unir) {
                emparejar(TipoToken.Unir);
            }
            
            String nombreTablaJoin = tokenActual.getLexema();
            if (!TablaSimbolos.existeTabla(nombreTablaJoin)) {
                throw new RuntimeException("Error semántico: La tabla " + nombreTablaJoin + " no existe.");
            }
            
            tablasConsulta.add(TablaSimbolos.obtenerTabla(nombreTablaJoin));
            sql.append(nombreTablaJoin).append(" ON ");
            
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.En);
            
            analizarExpresionLogicaJoin(tablasConsulta, sql);
        }
    }

    private void analizarTipoJoin(StringBuilder sql) {
        if (tokenActual == null) {
            return;
        }

        switch (tokenActual.getTipo()) {
            case UnirInterno:
                sql.append(" INNER JOIN ");
                emparejar(tokenActual.getTipo());
                break;
            case UnirIzquierdo:
                sql.append(" LEFT JOIN ");
                emparejar(tokenActual.getTipo());
                break;
            case UnirDerecho:
                sql.append(" RIGHT JOIN ");
                emparejar(tokenActual.getTipo());
                break;
            case Unir:
                sql.append(" JOIN ");
                emparejar(tokenActual.getTipo());
                break;
            default:
                throw new RuntimeException("Error sintáctico: tipo de join no válido: " + tokenActual.getLexema());
        }
    }

    private void analizarListaAsignaciones(SimboloTabla tabla, StringBuilder sql) {
        analizarAsignacion(tabla, sql);
        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            sql.append(", ");
            emparejar(TipoToken.Coma);
            analizarAsignacion(tabla, sql);
        }
    }

    private void analizarAsignacion(SimboloTabla tabla, StringBuilder sql) {
        
        String nombreColumna = tokenActual.getLexema();
        
        if(!tabla.existeColumna(nombreColumna)){
            throw new RuntimeException("Error semantico: La columna " + nombreColumna + " no existe en la tabla " + tabla.getNombre());
        }
        
        SimboloColumna columnaDestino = tabla.obtenerColumna(nombreColumna);
        sql.append(nombreColumna).append(" = ");
        
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.Igual);
        
        analizarValor(columnaDestino, sql);

    }

    // Ese es mio
    private void analizarDefinicionesCol(SimboloTabla tablaActual, StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba definición de columna");
        }

        if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
            analizarFk(tablaActual, sql);
        } else {
            SimboloColumna col = analizarColumnaDef(sql);
            tablaActual.agregarColumna(col);
        }

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            sql.append(", ");
            emparejar(TipoToken.Coma);

            if (tokenActual == null) {
                throw new RuntimeException("Error: se esperaba otra definición después de la coma");
            }

            if (tokenActual.getTipo() == TipoToken.LlaveForanea) {
                analizarFk(tablaActual, sql);
            } else {
                SimboloColumna col = analizarColumnaDef(sql);
                if(tablaActual.existeColumna(col.getNombre())){
                    throw new RuntimeException("Error semántico: la columna '" + col.getNombre() + "' fue declarada mas de una vez en la tabla.");     
                }
                tablaActual.agregarColumna(col);
            }
        }
    }

    private SimboloColumna analizarColumnaDef(StringBuilder sql) {
        String nombreColumna = tokenActual.getLexema();
        
        sql.append(nombreColumna).append(" ");
        
        emparejar(TipoToken.Identificador);
        
        TipoToken tipoDato = analizarTipoDato(sql);
        SimboloColumna nuevaColumna = new SimboloColumna(nombreColumna, tipoDato);
        
        analizarRestricciones(nuevaColumna, sql);
        
        return nuevaColumna;
    }

    private TipoToken analizarTipoDato(StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba un tipo de dato pero se encontró fin de código");
        }
        
        TipoToken tipoEncontrado = tokenActual.getTipo();

        if (tipoEncontrado == TipoToken.Entero) {
            sql.append("INT");
            emparejar(TipoToken.Entero);
        } else if (tipoEncontrado == TipoToken.Texto) {
            sql.append("VARCHAR(255)");
            emparejar(TipoToken.Texto);
        } else if (tipoEncontrado == TipoToken.Decimal) {
            sql.append("DECIMAL(10,2)");
            emparejar(TipoToken.Decimal);
        } else if (tipoEncontrado == TipoToken.Fecha) {
            sql.append("DATE");
            emparejar(TipoToken.Fecha);
        } else if (tipoEncontrado == TipoToken.Booleano) {
            sql.append("BOOLEAN");
            emparejar(TipoToken.Booleano);
        } else {
            throw new RuntimeException("Error sintáctico: tipo de dato no válido: " + tokenActual.getLexema());
        }
        
        return tipoEncontrado;
    }

    private void analizarRestricciones(SimboloColumna columna, StringBuilder sql) {
        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.NoNulo
                || tokenActual.getTipo() == TipoToken.Nulo
                || tokenActual.getTipo() == TipoToken.Unico
                || tokenActual.getTipo() == TipoToken.LlavePrimaria
                || tokenActual.getTipo() == TipoToken.AutoIncremento
                || tokenActual.getTipo() == TipoToken.Defecto)) {
            analizarRestriccion(columna, sql);
        }
    }

    private void analizarRestriccion(SimboloColumna columna, StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba una restricción pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.NoNulo) {
            columna.setEsNoNulo(true);
            sql.append(" NOT NULL");
            emparejar(TipoToken.NoNulo);
        } else if (tokenActual.getTipo() == TipoToken.Nulo) {
            sql.append(" NULL");
            emparejar(TipoToken.Nulo);
        } else if (tokenActual.getTipo() == TipoToken.Unico) {
            columna.setEsUnico(true);
            sql.append(" UNIQUE");
            emparejar(TipoToken.Unico);
        } else if (tokenActual.getTipo() == TipoToken.LlavePrimaria) {
            columna.setEsLlavePrimaria(true);
            sql.append(" PRIMARY KEY");
            emparejar(TipoToken.LlavePrimaria);
        } else if (tokenActual.getTipo() == TipoToken.AutoIncremento) {
            columna.setEsAutoIncremento(true);
            sql.append(" GENERATED BY DEFAULT AS IDENTITY");
            emparejar(TipoToken.AutoIncremento);
        } else if (tokenActual.getTipo() == TipoToken.Defecto) {
            sql.append(" DEFAULT ");
            emparejar(TipoToken.Defecto);
            analizarValor(columna, sql);
        } else {
            throw new RuntimeException("Error sintáctico: restricción no válida: " + tokenActual.getLexema());
        }
    }

    private void analizarFk(SimboloTabla tablaActual, StringBuilder sql) {
        sql.append("FOREIGN KEY (");
        emparejar(TipoToken.LlaveForanea);
        emparejar(TipoToken.ParentesisAbre);
        
        String columnaLocal = tokenActual.getLexema();
        sql.append(columnaLocal);
        
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
        sql.append(") REFERENCES ");
        emparejar(TipoToken.Referencia);
        
        String tablaRef = tokenActual.getLexema();
        sql.append(tablaRef).append("(");
        
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisAbre);
        
        String columnaRef = tokenActual.getLexema();
        sql.append(columnaRef);
        
        emparejar(TipoToken.Identificador);
        emparejar(TipoToken.ParentesisCierra);
        sql.append(")");
        
        if(!tablaActual.existeColumna(columnaLocal)){
            throw new RuntimeException("Error semantico: La columna local " + columnaLocal + " no ha sido definida en la tabla " + tablaActual.getNombre());
        }
        if(!TablaSimbolos.existeTabla(tablaRef)){
            throw new RuntimeException("Error semantico: La tabla referenciada " + tablaRef + " no existe en la base de datos");
        }
        SimboloTabla tablaDestino = TablaSimbolos.obtenerTabla(tablaRef);
        if(!tablaDestino.existeColumna(columnaRef)){
            throw new RuntimeException("Error semantico: La columna referenciada " + columnaRef + " no existe en la tabla " + tablaRef);
        }
        
        TipoToken tipoLocal = tablaActual.obtenerColumna(columnaLocal).getTipoDato();
        TipoToken tipoRef = tablaDestino.obtenerColumna(columnaRef).getTipoDato();
        
        if (tipoLocal != tipoRef) {
            throw new RuntimeException("Error semantico: Incompatibilidad de tipos en la llave foránea, " + columnaLocal + " es " + tipoLocal + ", pero '" + tablaRef + "." + columnaRef + " es " + tipoRef);
        }
        
        System.out.println("Semantica: Llave foránea validada (" + columnaLocal + " -> " + tablaRef + "." + columnaRef + ")");
        
    }

    private void analizarOpcWhere(SimboloTabla tabla, StringBuilder sql) {
        List<SimboloTabla> tablas = new ArrayList<>();
        tablas.add(tabla);
        analizarOpcWhere(tablas, sql);
    }

    private void analizarOpcWhere(List<SimboloTabla> tablas, StringBuilder sql) {
        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Donde) {
            sql.append(" WHERE ");
            emparejar(TipoToken.Donde);
            analizarExpresionLogica(tablas, sql);
        }
    }

    private void analizarExpresionLogica(List<SimboloTabla> tablas, StringBuilder sql) {
        analizarCondicion(tablas, sql);

        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.Y
                || tokenActual.getTipo() == TipoToken.O
                || tokenActual.getTipo() == TipoToken.AND
                || tokenActual.getTipo() == TipoToken.OR)) {
            analizarOperadorLogico(sql);
            analizarCondicion(tablas, sql);
        }
    }

    private void analizarOperadorLogico(StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: se esperaba operador lógico pero se encontró fin de código");
        }

        if (tokenActual.getTipo() == TipoToken.Y) {
            sql.append(" AND ");
            emparejar(TipoToken.Y);
        } else if (tokenActual.getTipo() == TipoToken.O) {
            sql.append(" OR ");
            emparejar(TipoToken.O);
        } else if (tokenActual.getTipo() == TipoToken.AND) {
            sql.append(" AND ");
            emparejar(TipoToken.AND);
        } else if (tokenActual.getTipo() == TipoToken.OR) {
            sql.append(" OR ");
            emparejar(TipoToken.OR);
        } else {
            throw new RuntimeException("Error sintáctico: se esperaba operador lógico y/o pero se encontró " + tokenActual.getLexema());
        }
    }

    private void analizarCondicion(List<SimboloTabla> tablas, StringBuilder sql) {
        String nombreColumna = tokenActual.getLexema();
        
        sql.append(nombreColumna + " ");
        
        SimboloColumna columnaCondicion = resolverColumna(tablas, nombreColumna);
        if(columnaCondicion == null){
            throw new RuntimeException("Error semántico: La columna " + nombreColumna + " no existe en las tablas de la consulta");
        }

        emparejar(TipoToken.Identificador);

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Entre) {
            sql.append("BETWEEN ");
            emparejar(TipoToken.Entre);
            analizarValor(columnaCondicion, sql);
            sql.append(" AND ");
            emparejar(TipoToken.Y);
            analizarValor(columnaCondicion, sql);
        } else {
            analizarOpRel(sql);
            analizarValor(columnaCondicion, sql);
        }
    }

    private void analizarOpRel(StringBuilder sql) {
        if (tokenActual == null) {
            throw new RuntimeException("Error: Se esperaba un operador relacional pero se encontro fin de codigo");
        }
        
        sql.append("").append(tokenActual.getLexema()).append(" ");

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

    private void analizarGroupBy(StringBuilder sql, List<SimboloTabla> tablas) {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Grupo) {
            sql.append(" GROUP BY ");
            emparejar(TipoToken.Grupo);
            emparejar(TipoToken.Por);

            analizarListaId(sql, tablas);
            analizarHaving(sql, tablas);
        }

    }

    private void analizarHaving(StringBuilder sql, List<SimboloTabla> tablas) {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Tener) {
            sql.append(" HAVING ");
            emparejar(TipoToken.Tener);
            analizarExpresionHaving(tablas, sql);
        }

    }

    private void analizarOrderBy(StringBuilder sql, List<SimboloTabla> tablas) {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Ordernar) {
            sql.append(" ORDER BY ");
            emparejar(TipoToken.Ordernar);
            emparejar(TipoToken.Por);

            analizarListaOrden(sql, tablas);

        }
    }

    private void analizarListaOrden(StringBuilder sql, List<SimboloTabla> tablas) {

        validarIdentificadorEnConsulta(tablas, tokenActual.getLexema());
        sql.append(tokenActual.getLexema());
        emparejar(TipoToken.Identificador);

        analizarSentido(sql);

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            sql.append(", ");
            emparejar(TipoToken.Coma);
            validarIdentificadorEnConsulta(tablas, tokenActual.getLexema());
            sql.append(tokenActual.getLexema());
            emparejar(TipoToken.Identificador);
            analizarSentido(sql);
        }

    }

    private void analizarSentido(StringBuilder sql) {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Asc) {
            sql.append(" ASC");
            emparejar(TipoToken.Asc);
        } else if (tokenActual != null && tokenActual.getTipo() == TipoToken.Desc) {
            sql.append(" DESC");
            emparejar(TipoToken.Desc);
        }

    }

    private void analizarLimit(StringBuilder sql) {

        if (tokenActual != null && tokenActual.getTipo() == TipoToken.Limite) {
            emparejar(TipoToken.Limite);
            sql.append(" LIMIT ").append(tokenActual.getLexema());
            emparejar(TipoToken.NumeroEntero);
        }
    }

    private void analizarListaId(StringBuilder sql, List<SimboloTabla> tablas) {

        validarIdentificadorEnConsulta(tablas, tokenActual.getLexema());
        sql.append(tokenActual.getLexema());
        emparejar(TipoToken.Identificador);

        while (tokenActual != null && tokenActual.getTipo() == TipoToken.Coma) {
            sql.append(", ");
            emparejar(TipoToken.Coma);
            validarIdentificadorEnConsulta(tablas, tokenActual.getLexema());
            sql.append(tokenActual.getLexema());
            emparejar(TipoToken.Identificador);
        }
    }

    private void analizarExpresionLogicaJoin(List<SimboloTabla> tablas, StringBuilder sql) {
        analizarCondicionJoin(tablas, sql);

        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.Y
                || tokenActual.getTipo() == TipoToken.O
                || tokenActual.getTipo() == TipoToken.AND
                || tokenActual.getTipo() == TipoToken.OR)) {
            analizarOperadorLogico(sql);
            analizarCondicionJoin(tablas, sql);
        }
    }

    private void analizarCondicionJoin(List<SimboloTabla> tablas, StringBuilder sql) {
        String columnaIzquierda = tokenActual.getLexema();
        validarIdentificadorEnConsulta(tablas, columnaIzquierda);
        sql.append(columnaIzquierda).append(" ");
        emparejar(TipoToken.Identificador);

        analizarOpRel(sql);

        String columnaDerecha = tokenActual.getLexema();
        validarIdentificadorEnConsulta(tablas, columnaDerecha);
        sql.append(columnaDerecha);
        emparejar(TipoToken.Identificador);
    }

    private void analizarExpresionHaving(List<SimboloTabla> tablas, StringBuilder sql) {
        analizarCondicionHaving(tablas, sql);

        while (tokenActual != null
                && (tokenActual.getTipo() == TipoToken.Y
                || tokenActual.getTipo() == TipoToken.O
                || tokenActual.getTipo() == TipoToken.AND
                || tokenActual.getTipo() == TipoToken.OR)) {
            analizarOperadorLogico(sql);
            analizarCondicionHaving(tablas, sql);
        }
    }

    private void analizarCondicionHaving(List<SimboloTabla> tablas, StringBuilder sql) {
        SimboloColumna columnaComparacion;
        TipoToken tipoComparacion;

        if (tokenActual.getTipo() == TipoToken.Identificador) {
            String nombreColumna = tokenActual.getLexema();
            columnaComparacion = resolverColumna(tablas, nombreColumna);
            if (columnaComparacion == null) {
                throw new RuntimeException("Error semántico: La columna " + nombreColumna + " no existe en las tablas de la consulta");
            }
            tipoComparacion = columnaComparacion.getTipoDato();
            sql.append(nombreColumna).append(" ");
            emparejar(TipoToken.Identificador);
        } else if (tokenActual.getTipo() == TipoToken.Contar
                || tokenActual.getTipo() == TipoToken.Suma
                || tokenActual.getTipo() == TipoToken.Promedio
                || tokenActual.getTipo() == TipoToken.Maximo
                || tokenActual.getTipo() == TipoToken.Minimo) {
            TipoToken agregacion = tokenActual.getTipo();
            analizarAgregacion(sql);
            sql.append("(");
            emparejar(TipoToken.ParentesisAbre);

            String nombreColumna = tokenActual.getLexema();
            columnaComparacion = resolverColumna(tablas, nombreColumna);
            if (columnaComparacion == null) {
                throw new RuntimeException("Error semántico: La columna " + nombreColumna + " no existe en las tablas de la consulta");
            }

            if ((agregacion == TipoToken.Suma || agregacion == TipoToken.Promedio)
                    && columnaComparacion.getTipoDato() != TipoToken.Entero
                    && columnaComparacion.getTipoDato() != TipoToken.Decimal) {
                throw new RuntimeException("Error semántico: No se puede aplicar la función " + agregacion + " a la columna " + nombreColumna);
            }

            if (agregacion == TipoToken.Contar) {
                tipoComparacion = TipoToken.Entero;
            } else if (agregacion == TipoToken.Promedio) {
                tipoComparacion = TipoToken.Decimal;
            } else {
                tipoComparacion = columnaComparacion.getTipoDato();
            }

            sql.append(nombreColumna).append(") ");
            emparejar(TipoToken.Identificador);
            emparejar(TipoToken.ParentesisCierra);
        } else {
            throw new RuntimeException("Error sintáctico: condición HAVING inválida en " + tokenActual.getLexema());
        }

        analizarOpRel(sql);
        analizarValor(new SimboloColumna("having", tipoComparacion), sql);
    }

    private void validarIdentificadorEnConsulta(List<SimboloTabla> tablas, String nombreColumna) {
        if (resolverColumna(tablas, nombreColumna) == null) {
            throw new RuntimeException("Error semántico: La columna " + nombreColumna + " no existe en las tablas de la consulta");
        }
    }

    private SimboloColumna resolverColumna(List<SimboloTabla> tablas, String nombreColumna) {
        SimboloColumna columnaEncontrada = null;

        for (SimboloTabla tabla : tablas) {
            if (tabla.existeColumna(nombreColumna)) {
                if (columnaEncontrada != null) {
                    throw new RuntimeException("Error semántico: La columna " + nombreColumna + " es ambigua en la consulta");
                }
                columnaEncontrada = tabla.obtenerColumna(nombreColumna);
            }
        }

        return columnaEncontrada;
    }
}
