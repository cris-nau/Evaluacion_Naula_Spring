package ec.ups.edu.gproyectossb.bussines;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.ups.edu.gproyectossb.dao.RolDAO;
import ec.ups.edu.gproyectossb.dao.UsuarioDAO;
import ec.ups.edu.gproyectossb.model.Rol;
import ec.ups.edu.gproyectossb.model.Usuario;
import jakarta.transaction.Transactional;

@Service
public class GestionUsuario {

	@Autowired
	private UsuarioDAO daoUsuario;
	
	@Autowired
    private RolDAO rolDAO;
	
    public List<Usuario> getUsuarios() {
        return daoUsuario.findAll();
    }
    
    public Usuario getUsuario(int id) throws Exception {
        if (id == 0) {
            throw new Exception("Parámetro vacío");
        }
        return daoUsuario.findById(id).orElse(null);
    }
    
    public Usuario crearUsuario(Usuario u) throws Exception {
        if (u.getNombre() == null || u.getNombre().isEmpty()) {
            throw new Exception("El nombre es obligatorio");
        }
        if (u.getEmail() == null || u.getEmail().isEmpty()) {
            throw new Exception("El email es obligatorio");
        }
        return daoUsuario.save(u);
    }
    
    public Usuario actualizarUsuario(Usuario u) throws Exception {
        if (u.getId() == 0) {
            throw new Exception("Para actualizar, el usuario debe tener un ID");
        }
        if (!daoUsuario.existsById(u.getId())) {
            throw new Exception("No se puede actualizar: El usuario no existe");
        }
        return daoUsuario.save(u); // .save() hace update si el ID existe
    }
    
    public void eliminarUsuario(int id) throws Exception {
        if (id == 0) {
            throw new Exception("ID inválido para eliminar");
        }
        daoUsuario.deleteById(id);
    }

    // ==========================================
    //            LÓGICA DE LOGIN
    // ==========================================
    
    public Usuario validarLogin(String email, String password) throws Exception {
        // 1. Buscamos al usuario por email
        Usuario u = daoUsuario.findByEmail(email);

        // 2. Validaciones de credenciales
        if (u == null) {
            return null; // No existe el correo
        }

        if (!u.getPassword().equals(password)) {
            return null; // Contraseña incorrecta
        }

        // NOTA IMPORTANTE:
        // Aquí borramos la llamada a "sincronizarDatosProgramador(u)"
        // porque la tabla de Programadores NO EXISTE en este proyecto.
        // Si necesitas esa lógica, debes llamar al servicio de Jakarta vía HTTP (REST Template).
        
        return u;
    }
    
    public Usuario buscarUsuarioPorEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("El email es necesario para buscar");
        }
        return daoUsuario.findByEmail(email);
    }

    // ==========================================
    //          LOGIN CON FIREBASE / GOOGLE
    // ==========================================
    
    @Transactional
    public Usuario loginConTokenFirebase(String token) throws Exception {
        
        // 1. VERIFICAMOS EL TOKEN CON GOOGLE
        // Si el token es falso o expiró, esta línea lanza error y detiene todo.
        com.google.firebase.auth.FirebaseToken decodedToken = 
                com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(token);

        // 2. OBTENEMOS EL EMAIL REAL DESDE GOOGLE (FUENTE DE VERDAD)
        String email = decodedToken.getEmail();
        String nombre = decodedToken.getName(); // Kevin ...

        // 3. BUSCAMOS O CREAMOS AL USUARIO EN NUESTRA BD
        Usuario u = daoUsuario.findByEmail(email);

        // CASO 1: USUARIO NUEVO
        if (u == null) {
            u = new Usuario();
            u.setEmail(email);
            
            // Separamos el nombre (Google lo da junto)
            if (nombre != null) {
                String[] partes = nombre.split(" ");
                u.setNombre(partes[0]);
                u.setApellido(partes.length > 1 ? partes[1] : "");
            } else {
                u.setNombre("Usuario");
                u.setApellido("Google");
            }
            
            u.setPassword("GOOGLE_AUTH"); // Contraseña dummy

            // Asignamos Rol CLIENTE (ID 1)
            Rol rolDefecto = rolDAO.findById(1).orElse(null);
            if(rolDefecto == null) {
                // Si no existe rol, lanza error o créalo aquí
                throw new Exception("Error: No existe el rol CLIENTE en la BD"); 
            }
            
            u.setRol(rolDefecto);
            daoUsuario.save(u);
        }

        // CASO 2: YA EXISTE -> Retornamos el usuario
        return u;
    }
}
