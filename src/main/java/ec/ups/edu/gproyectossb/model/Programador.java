package ec.ups.edu.gproyectossb.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(name="TBL_PROGRAMADOR")
public class Programador {

	@Id
    @Column(name="prog_id")
    private Integer id;

    @Column(name="prog_nombre")
    private String nombre; 

    @Column(name="prog_contacto")
    private String contacto;
    
    @Column(name="prog_email", unique=true)
    private String email;

    @Column(name="prog_especialidad")
    private String especialidad;
    
    @Column(name="prog_descripcion")
    private String descripcion;
    
    @Column(name="prog_activo")
    private boolean activo;
    
    @Column(name="prog_foto")
    private String foto;
    
    
	public Integer getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getContacto() {
		return contacto;
	}

	public void setContacto(String contacto) {
		this.contacto = contacto;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}
    
    
}
