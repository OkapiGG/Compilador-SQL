/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.compsql;

import java.sql.Connection;

/**
 *
 * @author alancervantes
 */
public class CompSQL {

    public static void main(String[] args) {
        Lexico lexico = new Lexico();

        Connection conexion = ConexionBD.getInstancia();

        if (conexion != null) {
            System.out.println("Compilador conectado a postgres");
        } else {
            System.err.println("Fallo de conexion");
        }

        //creacion de tablas
        String codigoTabla = "crear tabla Productos ("
                + "  id entero primaria,"
                + "  nombre texto no_nulo,"
                + "  precio decimal,"
                + "  disponible booleano defecto verdadero"
                + ");";
        
        //inserciones
        String codigoInsert = "insertar en tabla Productos valores (101, \"Laptop Pro\", 1500.50, verdadero);";
        String codigoInsert2 = "insertar en tabla Productos valores (102, \"Ipad\", 2000.00, verdadero);";

        //consulta
        String codigoSelect = "seleccionar nombre, precio de Productos donde precio > 1000;";
        
        //update
        String codigoUpdate = "actualizar Productos establecer precio = 1250.00 donde id = 101;";

        ejecutarSentencia(lexico, codigoTabla);
        ejecutarSentencia(lexico, codigoInsert);
        ejecutarSentencia(lexico, codigoInsert2);
        ejecutarSentencia(lexico, codigoSelect);
        ejecutarSentencia(lexico, codigoUpdate);
        
        
        
        // creacion de tablas con relacion
        String crearCategorias = "crear tabla Categorias ("
                + "  id_cat entero primaria,"
                + "  nombre_cat texto no_nulo"
                + ");";

        String crearJuegos = "crear tabla Juegos ("
                + "  id_juego entero primaria,"
                + "  titulo texto no_nulo,"
                + "  precio decimal,"
                + "  fk_cat entero,"
                + "  foranea (fk_cat) referencia Categorias (id_cat)" // ¡Prueba de FK!
                + ");";

        // insercion
        String insertCat1 = "insertar en tabla Categorias valores (1, \"Accion\");";
        String insertCat2 = "insertar en tabla Categorias valores (2, \"RPG\");";
        
        String insertJuego1 = "insertar en tabla Juegos valores (10, \"Halo\", 500.50, 1);";
        String insertJuego2 = "insertar en tabla Juegos valores (11, \"Zelda\", 1200.00, 2);";
        String insertJuego3 = "insertar en tabla Juegos valores (12, \"Doom\", 800.00, 1);";

        // delete con where
        String borrarJuego = "eliminar de Juegos donde id_juego = 12;";

        // consulta usando join y un where
        String consultaJoin = "seleccionar titulo, nombre_cat, precio "
                + "de Juegos "
                + "unir_interno Categorias en fk_cat = id_cat "
                + "donde precio > 100.00 "
                + "ordenar por precio desc;";

        
        ejecutarSentencia(lexico, crearCategorias);
        ejecutarSentencia(lexico, crearJuegos);
        ejecutarSentencia(lexico, insertCat1);
        ejecutarSentencia(lexico, insertCat2);
        ejecutarSentencia(lexico, insertJuego1);
        ejecutarSentencia(lexico, insertJuego2);
        ejecutarSentencia(lexico, insertJuego3);
        ejecutarSentencia(lexico, borrarJuego);
        ejecutarSentencia(lexico, consultaJoin);
        
        
        System.out.println("\nSENTENCIA SELECT CON AGREGACIONES");
        String consultaAgregaciones = ("seleccionar suma(precio) como total de Productos;");
        ejecutarSentencia(lexico, consultaAgregaciones);
        
        System.out.println("\nSENTENCIA SELECT CON AGRUPACIONES");
        String consultaAgrupaciones = ("seleccionar fk_cat, contar(id_juego) de Juegos grupo por fk_cat limite 5;");
        ejecutarSentencia(lexico, consultaAgrupaciones);
        
        System.out.println("\nSENTENCIA SELECT CON RANGO");
        String consultaRango = ("seleccionar titulo de Juegos donde precio entre 500 y 1000;");
        ejecutarSentencia(lexico, consultaRango);
    }
    
    private static void ejecutarSentencia(Lexico lexico, String codigo) {
        new Sintactico(lexico.analizar(codigo)).analizarPrograma();
    }
}
