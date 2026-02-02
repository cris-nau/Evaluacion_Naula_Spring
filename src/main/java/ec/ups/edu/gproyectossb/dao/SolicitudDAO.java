package ec.ups.edu.gproyectossb.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

import ec.ups.edu.gproyectossb.model.Solicitud;

public interface SolicitudDAO extends JpaRepository<Solicitud, Integer> {

	List<Solicitud> findByClienteId(int clienteId);
	
	@Query("SELECT s FROM Solicitud s WHERE s.idProgramador = :idProg")
	List<Solicitud> findByIdProgramador(@Param("idProg") int idProg);
	
	@Query("SELECT s.estado, COUNT(s) FROM Solicitud s WHERE s.cliente.id = :userId GROUP BY s.estado")
    List<Object[]> contarPorEstadoUsuario(@Param("userId") int userId);
    
    @Query("SELECT s.estado, COUNT(s) FROM Solicitud s WHERE s.idProgramador = :idProg GROUP BY s.estado")
    List<Object[]> contarPorEstadoProgramador(@Param("idProg") int idProg);
    
    @Query("SELECT s FROM Solicitud s WHERE s.estado = 'ACEPTADA' AND s.fechaSolicitud BETWEEN :inicio AND :fin")
    List<Solicitud> findProximas(@Param("inicio") Date inicio, @Param("fin") Date fin);
}
