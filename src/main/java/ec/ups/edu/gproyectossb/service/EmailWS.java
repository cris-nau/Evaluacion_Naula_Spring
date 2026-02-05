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
        try {
            this.enviarCorreo(body.get("destinatario"), body.get("asunto"), body.get("cuerpo"));
            return ResponseEntity.ok("✅ Correo enviado con éxito");
        } catch (Exception e) {
            // AHORA SÍ VERÁS EL ERROR EN HOPPSCOTCH
            e.printStackTrace();
            return ResponseEntity.status(500).body("❌ ERROR REAL: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. ESTO ES PARA TUS CLASES JAVA (Recordatorio, Solicitud)
    // ¡NO BORRES ESTE MÉTODO PUBLIC!
    // ==========================================
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        // ELIMINAMOS EL TRY-CATCH DE AQUÍ PARA QUE EL ERROR SUBA
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(remitente); 
        message.setTo(destinatario);
        message.setSubject(asunto);
        message.setText(cuerpo);
        
        mailSender.send(message); // Si esto falla, ahora explotará y lo verás
        System.out.println("✅ Correo enviado a: " + destinatario);
    }
}