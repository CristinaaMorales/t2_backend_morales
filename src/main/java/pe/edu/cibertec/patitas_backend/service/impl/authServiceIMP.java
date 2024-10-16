package pe.edu.cibertec.patitas_backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import pe.edu.cibertec.patitas_backend.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_backend.dto.LogoutRequestDTO;
import pe.edu.cibertec.patitas_backend.service.AuthService;

import java.io.*;
import java.time.LocalDateTime;

@Service
public class authServiceIMP implements AuthService {

    @Autowired
    ResourceLoader resourceLoader;

    private static final String RUTA_ARCHIVO = "src/main/resources/auditoria.txt";

    @Override
    public String[] validarUsuario(LoginRequestDTO loginRequestDTO) throws IOException {

        String[] datosUsuario = null;
        Resource resource = resourceLoader.getResource("classpath:usuarios.txt");

        try (BufferedReader br = new BufferedReader(new FileReader(resource.getFile()))) {

            String linea;
            while ((linea = br.readLine()) != null) {

                String[] datos = linea.split(";");
                if (loginRequestDTO.tipoDocumento().equals(datos[0]) &&
                        loginRequestDTO.numeroDocumento().equals(datos[1]) &&
                        loginRequestDTO.password().equals(datos[2])) {

                    datosUsuario = new String[2];
                    datosUsuario[0] = datos[3]; // Recuperar nombre
                    datosUsuario[1] = datos[4]; // Recuperar email

                    // Registrar el inicio de sesión
                    registrarLogin(datos[0], datos[1]);

                }

            }

        } catch (IOException e) {
            datosUsuario = null;
            throw new IOException(e);
        }

        return datosUsuario;
    }

    @Override
    public void registrarLogin(String tipoDocumento, String numeroDocumento) throws IOException {
        String registro = String.format("INICIO SESIÓN - Tipo Documento: %s, Número: %s, Fecha y Hora: %s%n",
                tipoDocumento, numeroDocumento, LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            writer.write(registro);
        }
    }

    @Override
    public void registrarLogout(LogoutRequestDTO logoutRequestDTO) throws IOException {
        String registro = String.format("CERRAR SESIÓN - Tipo Documento: %s, Número: %s, Fecha y Hora: %s%n",
                logoutRequestDTO.tipoDocumento(),
                logoutRequestDTO.numeroDocumento(),
                LocalDateTime.now());

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO, true))) {
            writer.write(registro);
        }
    }
}
