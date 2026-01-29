package ec.ups.edu.gproyectossb.model;

import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name="TBL_ROL")
public class Rol {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="rol_id")
    private int id;
    
    @Column(name="rol_nombre", unique=true) 
    private String nombre;
    
    @Column(name="rol_descripcion")
    private String descripcion;
    
    @OneToMany(mappedBy = "rol")
    private List<Usuario> usuarios;

	public int getId() {
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
    
    
}
