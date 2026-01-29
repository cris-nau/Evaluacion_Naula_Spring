package ec.ups.edu.gproyectossb.bussines;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ec.ups.edu.gproyectossb.service.EmailWS;
import ec.ups.edu.gproyectossb.dao.SolicitudDAO;
import ec.ups.edu.gproyectossb.model.Solicitud;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class RecordatorioTarea {

    @Autowired
    private SolicitudDAO solicitudDAO;

    @Autowired
    private EmailWS emailService;

    // Se ejecuta cada hora (al minuto 0 de cada hora)
    @Scheduled(cron = "0 0 * * * *") 
    public void enviarRecordatoriosAutomaticos() {
        System.out.println("⏰ Tarea Programada: Iniciando envío de recordatorios...");

        // 1. Definir el rango de tiempo (Próximas 2 horas)
        Calendar cal = Calendar.getInstance();
        Date ahora = cal.getTime();
        
        cal.add(Calendar.HOUR, 2); // Sumamos 2 horas al reloj actual
        Date enDosHoras = cal.getTime();

        // 2. Buscar en la base de datos
        List<Solicitud> proximas = solicitudDAO.findProximas(ahora, enDosHoras);

        // 3. Procesar y enviar correos
        if (proximas.isEmpty()) {
            System.out.println("ℹ️ No hay asesorías programadas en las próximas 2 horas.");
        } else {
            for (Solicitud s : proximas) {
                try {
                    String destinatario = s.getUsuario().getEmail();
                    String asunto = "⏰ Recordatorio: Tu asesoría comienza pronto";
                    String cuerpo = "Hola " + s.getUsuario().getNombre() + ",\n\n" +
                                    "Te recordamos que tienes una asesoría programada para hoy.\n" +
                                    "Detalle: " + s.getMensaje() + "\n" +
                                    "Estado: " + s.getEstado() + "\n\n" +
                                    "¡No faltes!";
                    
                    emailService.enviarCorreo(destinatario, asunto, cuerpo);
                    System.out.println("✅ Recordatorio enviado a: " + destinatario);
                } catch (Exception e) {
                    System.err.println("❌ Error enviando recordatorio a " + s.getUsuario().getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}