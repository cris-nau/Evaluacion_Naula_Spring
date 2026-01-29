package ec.ups.edu.gproyectossb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.ups.edu.gproyectossb.model.Usuario;

public interface UsuarioDAO extends JpaRepository<Usuario, Integer>{

	Usuario findByEmail(String email);
}
