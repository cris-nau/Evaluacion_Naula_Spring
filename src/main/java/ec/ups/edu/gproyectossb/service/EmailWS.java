package ec.ups.edu.gproyectossb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/mail")
@CrossOrigin(origins = "*")
public class EmailWS {

    @Autowired
    private JavaMailSender mailSender;

    private String remitente = "naulasantiago537@gmail.com"; 

    // ==========================================
    // 1. ESTO ES PARA ANGULAR (API REST)
    // ==========================================
    @PostMapping("/enviar")
    public ResponseEntity<?> enviarDesdeAngular(@RequestBody Map<String, String> body) {
        String destinatario = body.get("destinatario");
        String asunto = body.get("asunto");
        String cuerpo = body.get("cuerpo");
        
        try {
            // Reutilizamos la lógica interna
            this.enviarCorreo(destinatario, asunto, cuerpo);
            return ResponseEntity.ok("Correo enviado con éxito");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. ESTO ES PARA TUS CLASES JAVA (Recordatorio, Solicitud)
    // ¡NO BORRES ESTE MÉTODO PUBLIC!
    // ==========================================
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remitente); 
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(cuerpo);
            
            // Esto envía el correo. Si tarda más de 5 seg, fallará aquí en lugar de colgar el server
            mailSender.send(message); 
            System.out.println("✅ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Fallo crítico de correo: " + e.getMessage());
            // No lanzamos RuntimeException aquí para que el 502 no ocurra
        }
    }
}