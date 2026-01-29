package ec.ups.edu.gproyectossb.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseInitializer {

	@Bean
    public FirebaseApp firebaseApp() throws IOException {
        // Verifica si ya existe una instancia para evitar errores al reiniciar
        if (FirebaseApp.getApps().isEmpty()) {
            
            // Busca el archivo en la carpeta 'resources'
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase-key.json");

            if (serviceAccount == null) {
                throw new IOException("❌ ERROR: No se encontró el archivo firebase-key.json en src/main/resources");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("✅ Firebase inicializado correctamente en Spring Boot");
            return app;
        }
        
        return FirebaseApp.getInstance();
    }
}
