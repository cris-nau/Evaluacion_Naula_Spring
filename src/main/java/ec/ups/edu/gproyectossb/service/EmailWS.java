package ec.ups.edu.gproyectossb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailWS {

	@Autowired
    private JavaMailSender mailSender;

    // Lee el correo desde application.properties automáticamente
    @Value("${spring.mail.username}") 
    private String remitente;

    public void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom(remitente); // <--- Usa la variable, no texto fijo
            message.setTo(destinatario);
            message.setSubject(asunto);
            message.setText(cuerpo);
            
            mailSender.send(message);
            System.out.println("✅ Correo enviado a: " + destinatario);
        } catch (Exception e) {
            System.err.println("❌ Error enviando correo: " + e.getMessage());
        }
    }
}
