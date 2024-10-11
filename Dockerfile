# Usar una imagen base de Tomcat con soporte para Java 8 o 11
FROM tomcat:9.0-jdk11

# Establecer el directorio de trabajo dentro del contenedor
#WORKDIR /usr/local/tomcat/webapps/

# Copiar el archivo WAR generado por Maven al directorio webapps de Tomcat
COPY target/link-voyager-maven.war /usr/local/tomcat/webapps/

# Exponer el puerto 8080 para acceder a la aplicación
EXPOSE 8080

# Iniciar Tomcat
CMD ["catalina.sh", "run"]