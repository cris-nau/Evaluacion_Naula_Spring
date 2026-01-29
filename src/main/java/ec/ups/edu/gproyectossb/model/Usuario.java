package ec.ups.edu.gproyectossb.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
@Table(name="TBL_USUARIO")
public class Usuario {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="usu_id")
    private int id;
    
    @Column(name="usu_email", unique=true)
    private String email;
    
    @Column(name="usu_passwrod")
    private String password;
    
    @Column(name="usu_nombre")
    private String nombre;
    
    @Column(name="usu_apellido")
    private String apellido;
    
    @Column(name="usu_nombre_completo")
    private String nombreCompleto;
    
    @Column(name="usu_foto")
    private String foto;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id_fk")    
    private Rol rol;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Solicitud> solicitudesRealizadas;
    
	//ad
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}
	
	public Rol getRol() {
	    return rol;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getNombreCompleto() {
		return nombreCompleto;
	}

	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

    
}
