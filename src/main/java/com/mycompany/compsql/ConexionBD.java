/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.compsql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author alancervantes
 */
public class ConexionBD {
    
    private static final String URL = "jdbc:postgresql://localhost:5432/compsql";
    private static final String USER = "postgres";
    private static final String PASS = "152800";
    
    private static Connection conexion = null;
    
    private ConexionBD(){
    
    }
    
    public static Connection getInstancia(){
        try{
            if(conexion == null || conexion.isClosed()){
                Class.forName("org.postgresql.Driver");
                conexion = DriverManager.getConnection(URL, USER, PASS);
                System.out.println("Conexion establecida");
            }
        } catch (ClassNotFoundException e) {
            System.err.println(":No se encontro el driver de postgres");
        } catch (SQLException e) {
            System.err.println("Error de sql: " + e.getMessage());
        }
        return conexion;
    }
    
    public static void ejecutar(String sql) {
        Connection conn = getInstancia();
        if (conn == null) {
            System.err.println("No hay conexión disponible para ejecutar sentencias.");
            return;
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Comando ejecutado en PostgreSQL");
        } catch (SQLException e) {
            System.err.println("Error al ejecutar en Postgres: " + e.getMessage());
        }
    }

    public static void ejecutarConsulta(String sql) {
        try {
            Connection conn = getInstancia();
            if (conn != null) {
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int totalColumnas = metaData.getColumnCount();

                    for (int i = 1; i <= totalColumnas; i++) {
                        System.out.print(metaData.getColumnName(i));
                        if (i < totalColumnas) {
                            System.out.print("\t|\t");
                        }
                    }
                    System.out.println();

                    while (rs.next()) {
                        for (int i = 1; i <= totalColumnas; i++) {
                            System.out.print(rs.getString(i));
                            if (i < totalColumnas) {
                                System.out.print("\t|\t");
                            }
                        }
                        System.out.println();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al ejecutar consulta en Postgres: " + e.getMessage());
        }
    }
    
    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
                System.out.println("Conexion cerrada");
            } catch (SQLException e) {
                System.err.println("No se pudo cerrar: " + e.getMessage());
            }
        }
    }
}
