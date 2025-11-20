package gestorenvios.config;

import gestorenvios.models.exceptions.ConfiguracionPropertiesException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/***
 * Configuración de la aplicación. Proporciona acceso a las propiedades definidas en application.properties.
 */
public class ApplicationConfig {
    /*** Instancia única de Properties con la configuración cargada. */
    private static Properties properties;

    /***
     * Constructor privado para evitar instanciación externa (patrón Singleton).
     */
    private ApplicationConfig() {}

    /***
     * Obtiene la instancia única de Properties cargando el archivo si es necesario.
     * @return Instancia de Properties con la configuración
     * @throws ConfiguracionPropertiesException si ocurre un error al cargar el archivo
     */
    private static Properties getProperties() {
        if (properties == null) {
            Properties propiedades = new Properties();
            try (InputStream stream = ApplicationConfig.class.getResourceAsStream("/resources/application.properties")) {
                propiedades.load(stream);
                properties = propiedades;
            } catch (IOException e) {
                throw new ConfiguracionPropertiesException("No se pudo cargar la configuración de la aplicación: " + e.getMessage());
            }
        }
        return properties;
    }

    /***
     * Obtiene el valor de una propiedad de configuración por su clave.
     * @param clave Clave de la propiedad
     * @return Valor de la propiedad o null si no existe
     */
    public static String get(String clave) {
        return getProperties().getProperty(clave);
    }
}
