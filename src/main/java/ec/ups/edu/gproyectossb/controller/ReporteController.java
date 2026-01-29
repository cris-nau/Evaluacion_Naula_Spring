package ec.ups.edu.gproyectossb.controller;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ec.ups.edu.gproyectossb.dao.ProyectoDAO;
import ec.ups.edu.gproyectossb.dao.SolicitudDAO;
import ec.ups.edu.gproyectossb.model.Proyecto;
import ec.ups.edu.gproyectossb.model.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*") // Para que Angular pueda acceder sin problemas
public class ReporteController {

    @Autowired
    private SolicitudDAO solicitudDAO;
    
    @Autowired
    private ProyectoDAO proyectoDAO;

    @GetMapping("/proyectos/pdf")
    public ResponseEntity<byte[]> descargarReporteProyectos(@RequestParam int idUsuario) {
        try {
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);
            document.open();

            // Título del PDF
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
            Paragraph p = new Paragraph("MIS PROYECTOS REGISTRADOS", fontTitulo);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            document.add(new Paragraph(" "));

            // Tabla con 3 columnas
            PdfPTable tabla = new PdfPTable(3);
            tabla.setWidthPercentage(100);
            tabla.addCell("Título del Proyecto");
            tabla.addCell("Tipo");
            tabla.addCell("Tecnologías");

            // --- CONSULTA REAL A TBL_PROYECTO ---
            List<Proyecto> misProyectos = proyectoDAO.findByProgramadorId(idUsuario);

            for (Proyecto proy : misProyectos) {
                tabla.addCell(proy.getTitulo());
                tabla.addCell(proy.getTipo());
                tabla.addCell(proy.getTecnologias());
            }

            document.add(tabla);
            document.close();

            byte[] pdfBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_proyectos.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/asesorias/pdf")
    public ResponseEntity<byte[]> descargarReporteAsesorias(@RequestParam int idUsuario) {
        try {
            // 1. Obtener los datos reales de la base de datos
            List<Solicitud> lista = solicitudDAO.findAll(); // Aquí puedes filtrar por idUsuario si prefieres

            // 2. Crear el documento PDF en memoria
            Document document = new Document();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, out);

            document.open();

            // --- DISEÑO DEL PDF ---
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY);
            Paragraph titulo = new Paragraph("HISTORIAL DE ASESORÍAS", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);
            document.add(new Paragraph(" ")); // Espacio en blanco

            // 3. Crear la Tabla
            PdfPTable tabla = new PdfPTable(4); // 4 columnas
            tabla.setWidthPercentage(100);
            
            // Encabezados
            tabla.addCell("ID");
            tabla.addCell("Fecha Solicitud");
            tabla.addCell("Mensaje");
            tabla.addCell("Estado");

            // 4. Llenar la tabla con los datos de la BD
            for (Solicitud s : lista) {
                tabla.addCell(String.valueOf(s.getId()));
                tabla.addCell(s.getFechaSolicitud() != null ? s.getFechaSolicitud().toString() : "N/A");
                tabla.addCell(s.getMensaje());
                tabla.addCell(s.getEstado());
            }

            document.add(tabla);
            document.close();

            // 5. Configurar la respuesta para que el navegador lo entienda como archivo
            byte[] pdfBytes = out.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_asesorias.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}