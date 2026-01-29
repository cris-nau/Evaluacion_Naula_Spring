package ec.ups.edu.gproyectossb.service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.ups.edu.gproyectossb.bussines.GestionUsuario;
import ec.ups.edu.gproyectossb.model.Usuario;

class TokenDTO {
    public String token;
}

@RestController
@RequestMapping("api/persona")
@CrossOrigin(origins = "*")
public class UsuarioWS {

	@Autowired
	private GestionUsuario gu;
	
	@GetMapping
    public ResponseEntity<List<Usuario>> getUsuarios() {
        return ResponseEntity.ok(gu.getUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable("id") int id) {
        try {
            Usuario u = gu.getUsuario(id);
            if (u == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError("No encontrado", "Usuario con ID " + id + " no existe"));
            }
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error interno", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario u) {
        try {
            Usuario creado = gu.crearUsuario(u);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al crear", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable("id") int id, @RequestBody Usuario u) {
        try {
            u.setId(id);
            Usuario actualizado = gu.actualizarUsuario(u);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al actualizar", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable("id") int id) {
        try {
            // Verificamos si existe antes de borrar para devolver 404 si es necesario
            Usuario temp = gu.getUsuario(id);
            if (temp == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError("No encontrado", "No se puede eliminar: El usuario no existe"));
            }
            
            gu.eliminarUsuario(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error al eliminar", e.getMessage()));
        }
    }

    // ==========================================
    //            MÉTODOS ESPECIALES
    // ==========================================

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario credenciales) {
        try {
            Usuario u = gu.validarLogin(credenciales.getEmail(), credenciales.getPassword());

            if (u != null) {
                return ResponseEntity.ok(u);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(generarError("No autorizado", "Credenciales incorrectas"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error interno", e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorEmail(@RequestParam("email") String email) {
        try {
            Usuario usuario = gu.buscarUsuarioPorEmail(email);

            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generarError("No encontrado", "Usuario no encontrado con ese email"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(generarError("Error del servidor", e.getMessage()));
        }
    }

    @PostMapping("/login-firebase")
    public ResponseEntity<?> loginFirebase(@RequestBody TokenDTO request) {
        try {
            // Llamamos al método seguro enviando el token
            Usuario u = gu.loginConTokenFirebase(request.token);
            return ResponseEntity.ok(u);

        } catch (Exception e) {
            e.printStackTrace();
            // Si falla la verificación del token, devolvemos 401 No Autorizado
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error de autenticación con Google: " + e.getMessage());
        }
    }

    // ==========================================
    //            UTILITARIO
    // ==========================================
    
    // Método helper para crear el JSON de error sin necesitar una clase 'Error' aparte
    private Map<String, Object> generarError(String titulo, String detalle) {
        Map<String, Object> error = new HashMap<>();
        error.put("titulo", titulo);
        error.put("mensaje", detalle);
        return error;
    }
}
