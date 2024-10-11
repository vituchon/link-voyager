/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vituchon.linkexplorer.domain.model.procedure.unit;

import org.vituchon.linkexplorer.domain.model.procedure.DiscreteProcedureStatus;
import org.vituchon.linkexplorer.domain.model.procedure.GenericQueryableProcedure;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;

import javax.net.ssl.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Performs requets to specifics URL hidding comunication details.
 */
public class URLFetcher implements QueryableDiscreteProcedure, GenericQueryableProcedure<String, String> {

    private static final int BYTES_PER_READ = 1024;
    private static final Logger LOGGER = Logger.getLogger(URL.class.getName());
    private DiscreteProcedureStatusImpl status = new DiscreteProcedureStatusImpl();
    // User agent por defecto para enviar la petición
    private final static String USER_AGENT = "Mozilla/5.0";

    static {
        // Configurar el trust manager para aceptar todos los certificados
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null; // No es relevante para este caso.
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // No hacer nada, aceptamos todos los certificados.
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // No hacer nada, aceptamos todos los certificados.
                }
            }
        };

        // Verificador de hostnames para aceptar todos
        HostnameVerifier trustAllHostnames = (hostname, session) -> true;

        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCertificates, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(trustAllHostnames);
        } catch (GeneralSecurityException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public synchronized String retrieveContent(String spec) throws RetrieveException {
    	if (spec.contains("https")) {
    		return retrieveSecureContent(spec);
    	} else {
    		return retrieveUnsecureContent(spec);
    	}
    }
    public synchronized String retrieveSecureContent(String spec) throws RetrieveException {
        StringBuilder sb = new StringBuilder(256);
        try {
            // Crear la conexión HTTPS
            HttpsURLConnection con = (HttpsURLConnection) new URL(spec).openConnection();
            status.start(0);

            // Configurar método GET
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // Obtener el código de respuesta
            int responseCode = con.getResponseCode();
            System.out.println("Código de respuesta: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine);
            }
        } catch (Exception e) {
            throw new RetrieveException(e);
        } finally {
            status.end();
        }

        return sb.toString();
    }

    public synchronized String retrieveUnsecureContent(String spec) throws RetrieveException {
        StringBuilder sb = new StringBuilder(256);
        URL url = null;
        int contentLength = 0;
        try {
            try {
                url = new URL(spec);
                contentLength = getContentLength(url);
            } catch (MalformedURLException e) {
                throw new RetrieveException(e);
            }
            if (contentLength < 0) {
                LOGGER.log(Level.WARNING, "Content length for {0} is {1}, assuming cero.", new Object[]{url, contentLength});
                contentLength = 0;
            }
            status.start(contentLength);
            char[] buffer = new char[BYTES_PER_READ];
            try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));) {
                int read;
                read = br.read(buffer, 0, BYTES_PER_READ);
                while (read != -1) {
                    sb.append(buffer);
                    status.advance(read);
                    read = br.read(buffer, 0, BYTES_PER_READ);
                }
            } catch (IOException e) {
                throw new RetrieveException(e);
            }
        } finally {
            status.end();
        }
        return sb.toString();
    }

    private int getContentLength(URL url) throws RetrieveException {
        try {
            URLConnection conn = url.openConnection();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RetrieveException(e);
        }
    }

    @Override
    public DiscreteProcedureStatus getProcedureStatus() {
        return status;
    }

    @Override
    public String perform(String spec) throws RetrieveException {
        return this.retrieveContent(spec);

    }

    public static class RetrieveException extends Exception {

        public RetrieveException(Throwable cause) {
            super(cause);
        }
    }
}
