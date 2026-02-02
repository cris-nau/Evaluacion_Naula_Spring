package ec.ups.edu.gproyectossb.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;

import ec.ups.edu.gproyectossb.dao.ProgramadorDAO;
import ec.ups.edu.gproyectossb.dao.ProyectoDAO;
import ec.ups.edu.gproyectossb.dao.SolicitudDAO;
import ec.ups.edu.gproyectossb.model.Programador;
import ec.ups.edu.gproyectossb.model.Proyecto;
import ec.ups.edu.gproyectossb.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private SolicitudDAO solicitudDAO;

    @Autowired
    private ProyectoDAO proyectoDAO;
    
    @Autowired
    private ProgramadorDAO programadorDAO;

    // =========================================================
    // 1. DASHBOARD
    // =========================================================
    @GetMapping("/dashboard-admin")
    public ResponseEntity<Map<String, Object>> obtenerDashboard(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Integer idProgramador,
            @RequestParam(required = false) String estado
    ) {
        List<Solicitud> solicitudes = solicitudDAO.findAll();
        List<Proyecto> proyectos = proyectoDAO.findAll();

        solicitudes = filtrarSolicitudes(solicitudes, fechaInicio, fechaFin, idProgramador, estado);
        
        if (idProgramador != null && idProgramador != 0) {
            proyectos = filtrarProyectos(proyectos, idProgramador);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalPendientes", contarPorEstado(solicitudes, "PENDIENTE"));
        response.put("totalAceptadas", contarPorEstado(solicitudes, "ACEPTADA"));
        response.put("totalRechazadas", contarPorEstado(solicitudes, "RECHAZADA"));

        // Datos para Gráfico de Barras
        List<Integer> conteoPorMes = calcularSolicitudesPorMes(solicitudes);
        response.put("meses", Arrays.asList("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio"));
        response.put("asesoriasPorMes", conteoPorMes);
        // Enviamos el total de proyectos filtrados (puedes ajustar esta lógica si quieres proyectos por mes)
        response.put("proyectosPorMes", Arrays.asList(0, 0, 0, 0, 0, proyectos.size())); 

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // 2. REPORTES PDF - ASESORIAS
    // =========================================================
    @GetMapping("/asesorias/pdf-admin")
    public ResponseEntity<byte[]> descargarReporteAsesoriasAdmin(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Integer idProgramador,
            @RequestParam(required = false) String estado
    ) {
        try {
            List<Solicitud> lista = solicitudDAO.findAll();
            lista = filtrarSolicitudes(lista, fechaInicio, fechaFin, idProgramador, estado);
            return generarPdfAsesorias(lista, "REPORTE DE ASESORÍAS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // =========================================================
    // 3. REPORTES PDF - PROYECTOS (ESTE ERA EL QUE FALTABA)
    // =========================================================
    @GetMapping("/proyectos/pdf-admin")
    public ResponseEntity<byte[]> descargarReporteProyectosAdmin(
            @RequestParam(required = false) Integer idProgramador
    ) {
        try {
            List<Proyecto> lista = proyectoDAO.findAll();
            // Filtramos si hay un programador seleccionado
            if (idProgramador != null && idProgramador != 0) {
                lista = filtrarProyectos(lista, idProgramador);
            }
            return generarPdfProyectos(lista, "REPORTE DE PROYECTOS");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    // =========================================================
    // LÓGICA DE FILTRADO
    // =========================================================

    private List<Solicitud> filtrarSolicitudes(List<Solicitud> lista, String fInicio, String fFin, Integer idProg, String estado) {
        return lista.stream().filter(s -> {
            boolean cumple = true;

            // Filtro Fecha
            if (fInicio != null && !fInicio.isEmpty() && s.getFechaSolicitud() != null) {
                cumple = cumple && s.getFechaSolicitud().toString().compareTo(fInicio) >= 0;
            }
            if (fFin != null && !fFin.isEmpty() && s.getFechaSolicitud() != null) {
                cumple = cumple && s.getFechaSolicitud().toString().compareTo(fFin) <= 0;
            }

            // Filtro Estado
            if (estado != null && !estado.isEmpty()) {
                cumple = cumple && s.getEstado().equalsIgnoreCase(estado);
            }

            // Filtro Programador (Usando el ID directo)
            if (idProg != null && idProg != 0) {
                // Como dijiste que getProgramador devuelve un entero (el ID):
                // Asegúrate de que tu modelo Solicitud tenga: public int getProgramador() { return this.programador; }
                Integer idEnSolicitud = s.getProgramador(); 
                cumple = cumple && (idEnSolicitud != null && idEnSolicitud.equals(idProg));
            }

            return cumple;
        }).collect(Collectors.toList());
    }
    
    private List<Proyecto> filtrarProyectos(List<Proyecto> lista, Integer idProg) {
         return lista.stream().filter(p -> {
             return p.getId() == idProg; 
         }).collect(Collectors.toList());
    }

    private long contarPorEstado(List<Solicitud> lista, String estado) {
        return lista.stream().filter(s -> s.getEstado() != null && s.getEstado().equalsIgnoreCase(estado)).count();
    }

    private List<Integer> calcularSolicitudesPorMes(List<Solicitud> lista) {
        Integer[] conteo = {0, 0, 0, 0, 0, 0}; 
        for (Solicitud s : lista) {
            if (s.getFechaSolicitud() != null) {
                try {
                    LocalDate fecha = LocalDate.parse(s.getFechaSolicitud().toString());
                    int mes = fecha.getMonthValue();
                    if (mes >= 1 && mes <= 6) conteo[mes - 1]++;
                } catch (Exception e) {}
            }
        }
        return Arrays.asList(conteo);
    }

    // =========================================================
    // GENERADORES PDF
    // =========================================================

    private ResponseEntity<byte[]> generarPdfAsesorias(List<Solicitud> lista, String tituloDoc) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
        Paragraph p = new Paragraph(tituloDoc, fontTitulo);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.addCell("ID Solicitud");
        tabla.addCell("Programador"); 
        tabla.addCell("Fecha");
        tabla.addCell("Estado");

        for (Solicitud s : lista) {
            tabla.addCell(String.valueOf(s.getId()));

            // --- LÓGICA PARA OBTENER EL NOMBRE DEL PROGRAMADOR POR ID ---
            String nombreProgramador = "ID: " + s.getProgramador(); // Valor por defecto
            
            if (s.getProgramador() != null) {
                // Buscamos en la BD usando el ID que está en la solicitud
                Programador prog = programadorDAO.findById(s.getProgramador()).orElse(null);
                if (prog != null) {
                    nombreProgramador = prog.getNombre(); // Aquí obtienes el nombre real
                }
            }
            tabla.addCell(nombreProgramador);
            // -------------------------------------------------------------

            tabla.addCell(s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : "");
            tabla.addCell(s.getEstado());
        }

        document.add(tabla);
        document.close();

        byte[] pdfBytes = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_asesorias.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    private ResponseEntity<byte[]> generarPdfProyectos(List<Proyecto> lista, String tituloDoc) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph p = new Paragraph(tituloDoc, fontTitulo);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
        document.add(new Paragraph(" "));

        PdfPTable tabla = new PdfPTable(3); // Ajusta columnas según tu modelo
        tabla.setWidthPercentage(100);
        tabla.addCell("Título");
        tabla.addCell("Tipo");
        tabla.addCell("Tecnologías");

        for (Proyecto proy : lista) {
            tabla.addCell(proy.getTitulo() != null ? proy.getTitulo() : "");
            tabla.addCell(proy.getTipo() != null ? proy.getTipo() : "");
            tabla.addCell(proy.getTecnologias() != null ? proy.getTecnologias() : "");
        }

        document.add(tabla);
        document.close();

        byte[] pdfBytes = out.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_proyectos.pdf");

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}