package ec.ups.edu.gproyectossb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*; // Importante para el Path
import java.util.Map;

@RestController // Cambiamos @Service por @RestController para que acepte rutas
@RequestMapping("api/mail") // Este es el PATH base
@CrossOrigin(origins = "*") // Para evitar errores de CORS desde Angular
public class EmailWS {

    @Autowired
    private JavaMailSender mailSender;

    private String remitente = "naulasantiago537@gmail.com"; 

    // Este método es el que consumirá Angular con el PATH /api/mail/enviar
    @PostMapping("/enviar")
    public void enviarDesdeAngular(@RequestBody Map<String, String> body) {
        String destinatario = body.get("destinatario");
        String asunto = body.get("asunto");
        String cuerpo = body.get("cuerpo");
        
        // Llamamos a la lógica de envío
        this.enviarCorreo(destinatario, asunto, cuerpo);
    }

    // Tu lógica original se queda igual
    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom(remitente); 
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(cuerpo);
            
            mailSender.send(message);
            System.out.println("✅ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            e.printStackTrace(); 
            System.err.println("❌ Error enviando correo: " + e.getMessage());
        }
    }
}