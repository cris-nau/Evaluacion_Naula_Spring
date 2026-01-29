package ec.ups.edu.gproyectossb.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import ec.ups.edu.gproyectossb.model.Rol;

public interface RolDAO extends JpaRepository<Rol, Integer>{

	Rol findByNombre(String nombre);
}
