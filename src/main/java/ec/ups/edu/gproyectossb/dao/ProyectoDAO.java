package ec.ups.edu.gproyectossb.dao;

import ec.ups.edu.gproyectossb.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProyectoDAO extends JpaRepository<Proyecto, Integer> {
	
    List<Proyecto> findByProgramadorId(int programadorId);
}