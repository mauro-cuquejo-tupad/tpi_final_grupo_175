/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorenvios.config;

import gestorenvios.models.exceptions.ConfiguracionPropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Grupo 175
 */
public class ApplicationConfig {

    /**
     * Instancia única de Properties que contiene todas las configuraciones.
     * Se inicializa en la primera llamada a {@link #getProperties()}.
     */
    private static Properties properties;

    /**
     * Constructor privado para prevenir instanciación externa.
     * Implementa el patrón Singleton evitando que se creen instancias de esta clase.
     */
    private ApplicationConfig() {
    }

    /**
     * Obtiene la instancia única de Properties (patrón Singleton).
     *
     * <p>Este método implementa lazy initialization: las propiedades solo se cargan
     * la primera vez que se accede a ellas. Las llamadas subsecuentes retornan
     * la misma instancia.</p>
     *
     * <h3>Proceso de carga:</h3>
     * <ol>
     *   <li>Verifica si properties ya está inicializado</li>
     *   <li>Si no, crea una nueva instancia de Properties</li>
     *   <li>Carga el archivo desde /resources/application.properties</li>
     *   <li>Retorna la instancia cargada</li>
     * </ol>
     *
     * @return Instancia única de Properties con la configuración de la aplicación
     * @throws ConfiguracionPropertiesException si hay un error al cargar el archivo (encapsulada de IOException)
     */
    private static Properties getProperties() {
        if (properties == null) {
            Properties propiedades = new Properties();
            try (InputStream stream =
                         ApplicationConfig.class
                                 .getResourceAsStream(
                                         "/resources/application.properties")) {
                propiedades.load(stream);
                properties = propiedades;
            } catch (IOException e) {
                throw new ConfiguracionPropertiesException("No se pudo cargar la configuración de la aplicación: "
                        + e.getMessage());
            }
        }
        return properties;

    }

    /**
     * Obtiene el valor de una propiedad de configuración por su clave.
     *
     * <p>Este es el método principal de acceso a la configuración. Utiliza el patrón
     * Singleton internamente para garantizar una única carga del archivo.</p>
     *
     * <h3>Ejemplo de uso:</h3>
     * <pre>
     * String dbUrl = ApplicationConfig.get("db.url");
     * // Retorna: "jdbc:mysql://localhost:3306/tfi_bd_grupo175"
     *
     * String appName = ApplicationConfig.get("app.nombre");
     * // Retorna: "Gestor de Envíos"
     * </pre>
     *
     * @param valor Clave de la propiedad a buscar (sensible a mayúsculas/minúsculas)
     * @return Valor de la propiedad o null si no existe la clave
     * @see Properties#getProperty(String)
     */
    public static String get(String valor) {
        return getProperties().getProperty(valor);
    }
}
