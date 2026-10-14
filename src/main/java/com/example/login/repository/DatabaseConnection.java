package com.example.login.repository;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Conexión a PostgreSQL sin datos escritos en el código.
 *
 * Busca DB_URL, DB_USER y DB_PASSWORD en este orden:
 *   1. Variables de entorno (así funciona en Render).
 *   2. Archivo local ".env" en la carpeta del proyecto (así funciona en tu compu).
 */
public class DatabaseConnection {

    private static final Properties ENV_LOCAL = cargarEnvLocal();

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                obligatoria("DB_URL"),
                obligatoria("DB_USER"),
                obligatoria("DB_PASSWORD"));
    }

    private static Properties cargarEnvLocal() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(".env")) {
            props.load(in);
        } catch (IOException e) {
            // Si no existe .env, se usan solo las variables de entorno.
        }
        return props;
    }

    private static String obligatoria(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) {
            valor = ENV_LOCAL.getProperty(nombre);
        }
        if (valor == null || valor.isBlank()) {
            throw new IllegalStateException(
                    "Falta " + nombre + ". Ponela en el archivo .env (local) o en Environment de Render.");
        }
        return valor.trim();
    }
}