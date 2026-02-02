package ec.ups.edu.gproyectossb.bussines;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ec.ups.edu.gproyectossb.dao.ProgramadorDAO;
import ec.ups.edu.gproyectossb.dao.SolicitudDAO;
import ec.ups.edu.gproyectossb.dao.UsuarioDAO;
import ec.ups.edu.gproyectossb.model.*;
import ec.ups.edu.gproyectossb.model.Usuario;
import ec.ups.edu.gproyectossb.service.EmailWS;

@Service
public class GestionSolicitud {

	@Autowired
    private SolicitudDAO solicitudDAO;
    
    @Autowired
    private UsuarioDAO usuarioDAO; // Para verificar que el cliente existe
    
    @Autowired
    private EmailWS emailService;
    
    @Autowired
    private ProgramadorDAO proDAO;

    @Transactional
    public Solicitud actualizarEstado(int idSolicitud, String nuevoEstado) throws Exception {
        Solicitud s = solicitudDAO.findById(idSolicitud)
            .orElseThrow(() -> new Exception("Solicitud no encontrada"));
        
        s.setEstado(nuevoEstado);
        Solicitud actualizada = solicitudDAO.save(s);

        // Notificar al CLIENTE sobre el cambio (Aceptada/Rechazada)
        String asunto = "Actualización de tu tutoría";
        String cuerpo = "Hola " + s.getUsuario().getNombre() + ",\n\n" +
                        "Tu solicitud ha sido: " + nuevoEstado + ".\n" +
                        "Revisa los detalles en la plataforma.";
        
        emailService.enviarCorreo(s.getUsuario().getEmail(), asunto, cuerpo);
        
        return actualizada;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> getSolicitudes() {
        return solicitudDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Solicitud getSolicitud(int id) throws Exception {
        return solicitudDAO.findById(id).orElse(null);
    }

    @Transactional
    public Solicitud crearSolicitud(Solicitud s) throws Exception {
        // 1. Validaciones existentes
        if (s.getUsuario() == null) {
            throw new Exception("La solicitud debe tener un Usuario Cliente asociado");
        }
        
        if (!usuarioDAO.existsById(s.getUsuario().getId())) {
            throw new Exception("El usuario cliente no existe en la base de datos");
        }

        if (s.getProgramador() == null || s.getProgramador() == 0) {
            throw new Exception("Se debe especificar el ID del Programador");
        }

        // 2. NUEVO: Buscar los datos del Programador para obtener su correo
        // Asumiendo que usas el mismo usuarioDAO o uno específico para programadores
        Programador programador = proDAO.findById(s.getProgramador())
                .orElseThrow(() -> new Exception("El programador especificado no existe"));

        if (s.getMensaje() == null || s.getMensaje().isEmpty()) {
            throw new Exception("El mensaje de la solicitud es obligatorio");
        }

        // 3. Valores por defecto
        if (s.getEstado() == null || s.getEstado().isEmpty()) {
            s.setEstado("PENDIENTE");
        }
        
        if (s.getFechaSolicitud() == null) {
            s.setFechaSolicitud(new java.util.Date());
        }

        // 4. Guardar la solicitud
        Solicitud nuevaSolicitud = solicitudDAO.save(s);

        // 5. NUEVO: Enviar el correo al programador
        try {
            String destinatario = programador.getEmail(); // Asegúrate de que tu clase Usuario tenga getCorreo()
            String asunto = "Nueva Solicitud de Asesoría";
            String cuerpo = "Hola " + programador.getNombre() + ",\n\n" +
                            "Has recibido una nueva solicitud de asesoría.\n" +
                            "Cliente: " + s.getUsuario().getNombre() + "\n" +
                            "Mensaje: " + s.getMensaje() + "\n\n" +
                            "Por favor, ingresa al sistema para aceptarla o rechazarla.";
            
            emailService.enviarCorreo(destinatario, asunto, cuerpo);
        } catch (Exception e) {
            // Usamos un print para que si falla el correo, no se caiga toda la transacción de la base de datos
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }

        return nuevaSolicitud;
    }

    @Transactional
    public Solicitud actualizarSolicitud(Solicitud s) throws Exception {
        if (s.getId() == 0) {
            throw new Exception("La solicitud debe tener un ID para actualizar");
        }
        
        // Verificamos existencia
        if (!solicitudDAO.existsById(s.getId())) {
            throw new Exception("No existe la solicitud con ID: " + s.getId());
        }

        return solicitudDAO.save(s);
    }

    @Transactional
    public void eliminarSolicitud(int id) throws Exception {
        if (!solicitudDAO.existsById(id)) {
            throw new Exception("No existe la solicitud a eliminar");
        }
        solicitudDAO.deleteById(id);
    }

    // ==========================================
    //          CONSULTAS ESPECÍFICAS
    // ==========================================

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorCliente(int idUsuario) {
        // Usa el método del DAO: findByUsuarioId
        return solicitudDAO.findByClienteId(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorProgramador(int idProgramador) {
        // Usa el método del DAO: findByIdProgramador
        return solicitudDAO.findByIdProgramador(idProgramador);
    }
    
    public Map<String, Long> obtenerEstadisticas(int idUsuario) {
        // 1. Llamamos al DAO (Tu consulta @Query)
        List<Object[]> resultados = solicitudDAO.contarPorEstadoUsuario(idUsuario);
        
        // 2. Preparamos un mapa vacío
        Map<String, Long> estadisticas = new HashMap<>();
        
        // 3. Inicializamos en 0 por si no hay datos de algún tipo
        estadisticas.put("PENDIENTE", 0L);
        estadisticas.put("ACEPTADA", 0L);
        estadisticas.put("RECHAZADA", 0L);
        estadisticas.put("FINALIZADA", 0L);

        // 4. Llenamos con los datos reales de la BD
        if (resultados != null) {
            for (Object[] fila : resultados) {
                String estado = (String) fila[0]; // La columna 0 es el estado
                Long cantidad = (Long) fila[1];   // La columna 1 es el conteo
                
                estadisticas.put(estado, cantidad);
            }
        }

        return estadisticas;
    }
    
    public Map<String, Long> obtenerEstadisticasProgramador(int idProgramador) {
        // Llamamos al nuevo método del DAO
        List<Object[]> resultados = solicitudDAO.contarPorEstadoProgramador(idProgramador);
        
        Map<String, Long> estadisticas = new HashMap<>();
        estadisticas.put("PENDIENTE", 0L);
        estadisticas.put("ACEPTADA", 0L);
        estadisticas.put("RECHAZADA", 0L);

        if (resultados != null) {
            for (Object[] fila : resultados) {
                String estado = (String) fila[0];
                Long cantidad = (Long) fila[1];
                estadisticas.put(estado, cantidad);
            }
        }
        return estadisticas;
    }
}
