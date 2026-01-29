package ec.ups.edu.gproyectossb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "TBL_PROYECTO") // Nombre exacto de tu tabla en Jakarta
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proy_id")
    private int id;

    @Column(name = "proy_titulo")
    private String titulo;

    @Column(name = "proy_tipo")
    private String tipo;

    @Column(name = "proy_tecnologias")
    private String tecnologias;

    // Solo mapeamos el ID del programador para filtrar fácilmente
    @Column(name = "prog_id_fk")
    private int programadorId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTecnologias() {
		return tecnologias;
	}

	public void setTecnologias(String tecnologias) {
		this.tecnologias = tecnologias;
	}

	public int getProgramadorId() {
		return programadorId;
	}

	public void setProgramadorId(int programadorId) {
		this.programadorId = programadorId;
	}

    
}