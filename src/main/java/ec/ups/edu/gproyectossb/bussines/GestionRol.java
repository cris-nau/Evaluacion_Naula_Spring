package ec.ups.edu.gproyectossb.bussines;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ec.ups.edu.gproyectossb.dao.RolDAO;
import ec.ups.edu.gproyectossb.model.Rol;
import jakarta.transaction.Transactional;

@Service
public class GestionRol {

	@Autowired
    private RolDAO rolDAO;

    @Transactional
    public List<Rol> getRoles() {
        return rolDAO.findAll();
    }

    @Transactional
    public Rol getRol(int id) throws Exception {
        if (id == 0) {
            throw new Exception("El ID del rol no puede ser 0");
        }
        return rolDAO.findById(id).orElse(null);
    }
    
    @Transactional
    public Rol buscarPorNombre(String nombre) throws Exception {
        if (nombre == null || nombre.isEmpty()) {
            throw new Exception("El nombre es necesario para buscar");
        }
        return rolDAO.findByNombre(nombre);
    }

}
