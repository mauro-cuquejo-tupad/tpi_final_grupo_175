/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestorenvios.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Grupo 175
 */
public class ApplicationConfig {
    
    private static Properties properties;
    
    private ApplicationConfig() {
    }
    
    //Se genera un método getProperties aplicando patrón Singleton, pero de forma
    //privada. Este sólo se podrá acceder mediante el método estático get().
    private static Properties getProperties(){
        if(properties == null){
            Properties propiedades = new Properties();
            try (InputStream stream = 
                    ApplicationConfig.class
                            .getResourceAsStream(
                                    "/resources/application.properties")){
                propiedades.load(stream);
                properties = propiedades;
            } catch(IOException e) {
                e.printStackTrace();
            }            
        }
        return properties;
        
    }
    
    public static String get(String valor) {
        return getProperties().getProperty(valor);
    }
}
