package ec.ups.edu.gproyectossb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.ups.edu.gproyectossb.bussines.GestionSolicitud;
import ec.ups.edu.gproyectossb.model.Solicitud;

@RestController
@RequestMapping("api/solicitud")
@CrossOrigin(origins = "*")
public class SolicitudWS {

	@Autowired
    private GestionSolicitud gs;

    @GetMapping
    public ResponseEntity<List<Solicitud>> getSolicitudes() {
        return ResponseEntity.ok(gs.getSolicitudes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSolicitud(@PathVariable("id") int id) {
        try {
            Solicitud s = gs.getSolicitud(id);
            if (s == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError("No encontrado", "Solicitud con ID " + id + " no existe"));
            }
            return ResponseEntity.ok(s);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error interno", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud s) {
        try {
            Solicitud creada = gs.crearSolicitud(s);
            return ResponseEntity.status(HttpStatus.CREATED).body(creada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al crear solicitud", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSolicitud(@PathVariable("id") int id, @RequestBody Solicitud s) {
        try {
            s.setId(id);
            Solicitud actualizada = gs.actualizarSolicitud(s);
            return ResponseEntity.ok(actualizada);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al actualizar", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSolicitud(@PathVariable("id") int id) {
        try {
            gs.eliminarSolicitud(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al eliminar", e.getMessage()));
        }
    }

    // --- ENDPOINTS ESPECÍFICOS PARA FILTRAR ---

    @GetMapping("/usuario")
    public ResponseEntity<?> listarPorCliente(@RequestParam("id") int idUsuario) {
        try {
            List<Solicitud> lista = gs.listarPorCliente(idUsuario);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al listar", e.getMessage()));
        }
    }

    @GetMapping("/programador")
    public ResponseEntity<?> listarPorProgramador(@RequestParam("id") int idProgramador) {
        try {
            List<Solicitud> lista = gs.listarPorProgramador(idProgramador);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al listar", e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticas(@RequestParam int idUsuario) {
        try {
            // Llamamos a la GESTIÓN
            Map<String, Long> stats = gs.obtenerEstadisticas(idUsuario);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/stats/programador")
    public ResponseEntity<Map<String, Long>> obtenerEstadisticasProg(@RequestParam("id") int idProgramador) {
        try {
            Map<String, Long> stats = gs.obtenerEstadisticasProgramador(idProgramador);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private Map<String, Object> generarError(String titulo, String detalle) {
        Map<String, Object> error = new HashMap<>();
        error.put("titulo", titulo);
        error.put("mensaje", detalle);
        return error;
    }
    
}
