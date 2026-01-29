package ec.ups.edu.gproyectossb.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.ups.edu.gproyectossb.bussines.GestionRol;
import ec.ups.edu.gproyectossb.model.Rol;

@RestController
@RequestMapping("api/rol")
@CrossOrigin(origins = "*")
public class RolWS {

	@Autowired
    private GestionRol gr;

    @GetMapping
    public ResponseEntity<List<Rol>> getRoles() {
        return ResponseEntity.ok(gr.getRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRol(@PathVariable("id") int id) {
        try {
            Rol r = gr.getRol(id);
            if (r == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError("No encontrado", "Rol con ID " + id + " no existe"));
            }
            return ResponseEntity.ok(r);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error interno", e.getMessage()));
        }
    }

    private Map<String, Object> generarError(String titulo, String detalle) {
        Map<String, Object> error = new HashMap<>();
        error.put("titulo", titulo);
        error.put("mensaje", detalle);
        return error;
    }
}
