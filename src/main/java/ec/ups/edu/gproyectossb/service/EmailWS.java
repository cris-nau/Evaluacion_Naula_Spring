package ec.ups.edu.gproyectossb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/mail") // AGREGUÉ LA BARRA INICIAL /
@CrossOrigin(origins = "*")
public class EmailWS {

    @Autowired
    private JavaMailSender mailSender;

    // Asegúrate de que este correo sea IDÉNTICO al del application.properties
    private String remitente = "naulasantiago537@gmail.com"; 

    @PostMapping("/enviar")
    public ResponseEntity<?> enviarDesdeAngular(@RequestBody Map<String, String> body) {
        String destinatario = body.get("destinatario");
        String asunto = body.get("asunto");
        String cuerpo = body.get("cuerpo");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente);
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(cuerpo);

            mailSender.send(message);
            System.out.println("✅ Correo enviado a: " + destinatario);
            
            // Si todo sale bien, retornamos 200 OK
            return ResponseEntity.ok("Correo enviado con éxito");

        } catch (Exception e) {
            // Si falla, imprimimos en consola Y retornamos error 500 al cliente
            e.printStackTrace();
            System.err.println("❌ Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error enviando correo: " + e.getMessage());
        }
    }
}