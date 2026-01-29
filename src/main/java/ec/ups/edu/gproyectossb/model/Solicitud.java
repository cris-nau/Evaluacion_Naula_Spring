package ec.ups.edu.gproyectossb.model;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name="TBL_SOLICITUD")
public class Solicitud {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sol_id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="sol_fecha")
    private Date fechaSolicitud;
    
    @Column(name="sol_estado")
    private String estado;
    
    @Column(name="sol_mensaje")
    private String mensaje;
    
    
    @ManyToOne
    @JoinColumn(name = "usu_id_fk")
    private Usuario cliente;
    
    
    @Column(name = "id_programador", nullable = false)
    private Integer idProgramador;


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}


	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}


	public String getMensaje() {
		return mensaje;
	}


	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}


	public Usuario getUsuario() {
		return cliente;
	}


	public void setUsuario(Usuario usuario) {
		this.cliente = usuario;
	}


	public Integer getProgramador() {
		return idProgramador;
	}


	public void setProgramador(Integer programador) {
		this.idProgramador = programador;
	}
	
	
    
    
}
