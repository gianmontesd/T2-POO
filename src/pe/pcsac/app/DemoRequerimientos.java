package pe.pcsac.app;

import pe.pcsac.dao.*;
import pe.pcsac.modelo.*;
import pe.pcsac.servicio.CertificadoServicio;
import pe.pcsac.servicio.InscripcionServicio;
import pe.pcsac.servicio.ServicioException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Escenario que demuestra por consola los requerimientos funcionales del
 * sistema con datos reales de la base. Se ejecuta con: Main demo
 */
public class DemoRequerimientos {

    private static final EmpresaDAO EMPRESA = new EmpresaDAO();
    private static final CursoDAO CURSO = new CursoDAO();
    private static final InstructorDAO INSTRUCTOR = new InstructorDAO();
    private static final AlumnoDAO ALUMNO = new AlumnoDAO();
    private static final CapacitacionDAO CAPACITACION = new CapacitacionDAO();
    private static final InscripcionServicio INSCRIPCIONES = new InscripcionServicio();
    private static final CertificadoServicio CERTIFICADOS = new CertificadoServicio();

    public static void ejecutar() {
        if (!ConexionDB.probar()) {
            System.out.println("No hay conexion con MySQL. Ejecute antes sql/01_esquema.sql.");
            return;
        }
        try {
            titulo("RF-03  Registro de la empresa minera cliente");
            Empresa minera = EMPRESA.buscarPorRuc("20481234567");
            if (minera == null) {
                minera = new Empresa("20481234567", "Minera Las Bambas S.A.",
                        "Jefatura de SSOMA", "084-321456");
                EMPRESA.insertar(minera);
            }
            System.out.println("   Empresa: " + minera.getRazonSocial() + " (RUC " + minera.getRuc() + ")");

            titulo("RF-02  Registro del curso en el catalogo");
            Curso curso = buscarCurso("SSO-201");
            if (curso == null) {
                curso = new Curso("SSO-201", "Trabajos en altura y uso de arnes", 16, 13.0);
                CURSO.insertar(curso);
            }
            System.out.println("   Curso: " + curso + "  |  " + curso.getDuracionHoras()
                    + " horas  |  nota minima " + curso.getNotaMinima());

            Instructor instructor = buscarInstructor("41235678");
            if (instructor == null) {
                instructor = new Instructor("41235678", "Ricardo", "Peralta Yupanqui",
                        "Ingeniero de Seguridad Industrial");
                INSTRUCTOR.insertar(instructor);
            }

            titulo("RF-01  Registro de los trabajadores a capacitar");
            Alumno a1 = registrarAlumno("76543210", "Elena", "Ccahuana Ribera", "Operaciones mina", minera);
            Alumno a2 = registrarAlumno("76543211", "Victor", "Palomino Rojas", "Mantenimiento", minera);
            System.out.println("   " + a1);
            System.out.println("   " + a2);

            Capacitacion cap = buscarCapacitacion("CAP-DEMO-01");
            if (cap == null) {
                cap = new Capacitacion("CAP-DEMO-01", curso, minera, instructor,
                        LocalDate.now().minusDays(7), 13.0);
                CAPACITACION.insertar(cap);
            }
            System.out.println("   Capacitacion programada: " + cap.getCodigo()
                    + " del " + cap.getFecha());

            titulo("RF-04 y RF-05  Inscripcion con voucher y control de duplicados");
            inscribirSiHaceFalta(a1, cap, "BV-00123");
            inscribirSiHaceFalta(a2, cap, "BV-00124");
            try {
                INSCRIPCIONES.inscribir(a1.getIdAlumno(), cap.getIdCapacitacion(), "BV-00125");
                System.out.println("   [ERROR] se permitio un duplicado.");
            } catch (ServicioException e) {
                System.out.println("   [VALIDACION] " + e.getMessage());
            }

            titulo("RF-06 y RF-07  Registro de notas y estado automatico");
            Inscripcion i1 = INSCRIPCIONES.buscarPorDniYCapacitacion(a1.getDni(), cap.getIdCapacitacion());
            Inscripcion i2 = INSCRIPCIONES.buscarPorDniYCapacitacion(a2.getDni(), cap.getIdCapacitacion());
            i1 = INSCRIPCIONES.registrarNota(i1.getIdInscripcion(), 17.5);
            i2 = INSCRIPCIONES.registrarNota(i2.getIdInscripcion(), 11.0);
            System.out.printf("   %-28s nota %5.2f  ->  %s%n",
                    i1.getAlumno().getNombreCompleto(), i1.getNotaFinal(), i1.getEstado());
            System.out.printf("   %-28s nota %5.2f  ->  %s%n",
                    i2.getAlumno().getNombreCompleto(), i2.getNotaFinal(), i2.getEstado());
            System.out.println("   (la nota minima de la capacitacion es " + cap.getNotaMinima() + ")");

            titulo("RF-09  Emision del certificado solo a los aprobados");
            try {
                CERTIFICADOS.emitir(i2.getIdInscripcion());
                System.out.println("   [ERROR] se certifico a un desaprobado.");
            } catch (ServicioException e) {
                System.out.println("   [VALIDACION] " + e.getMessage());
            }
            List<Certificado> lote = CERTIFICADOS.emitirLote(cap.getIdCapacitacion());
            for (Certificado c : lote) {
                System.out.println("   Emitido " + c.getCodigo() + "  ->  " + c.getRutaArchivo());
            }
            if (lote.isEmpty()) System.out.println("   (los certificados de esta capacitacion ya existian)");

            titulo("RF-08  Consulta de todos los certificados emitidos");
            for (Certificado c : CERTIFICADOS.listar()) {
                System.out.printf("   %-34s %-26s %-22s %s%n", c.getCodigo(),
                        c.getInscripcion().getAlumno().getNombreCompleto(),
                        c.getInscripcion().getAlumno().getEmpresa().getRazonSocial(),
                        c.getFechaEmision());
            }

            titulo("RF-10  Certificados filtrados por empresa minera");
            List<Certificado> deLaMinera = CERTIFICADOS.listarPorEmpresa(minera.getIdEmpresa());
            System.out.println("   " + minera.getRazonSocial() + ": "
                    + deLaMinera.size() + " certificado(s) disponibles para su area de RRHH.");

            System.out.println("\n== Fin de la demostracion ==");
        } catch (SQLException | ServicioException e) {
            System.out.println("Error durante la demostracion: " + e.getMessage());
        }
    }

    private static void inscribirSiHaceFalta(Alumno a, Capacitacion c, String voucher)
            throws ServicioException {
        Inscripcion existente = INSCRIPCIONES.buscarPorDniYCapacitacion(a.getDni(), c.getIdCapacitacion());
        if (existente == null) {
            INSCRIPCIONES.inscribir(a.getIdAlumno(), c.getIdCapacitacion(), voucher);
            System.out.println("   Inscrito " + a.getNombreCompleto() + " con voucher " + voucher);
        } else {
            System.out.println("   " + a.getNombreCompleto() + " ya estaba inscrito.");
        }
    }

    private static Alumno registrarAlumno(String dni, String nombres, String apellidos,
                                          String area, Empresa e) throws SQLException {
        Alumno a = ALUMNO.buscarPorDni(dni);
        if (a == null) {
            a = new Alumno(dni, nombres, apellidos,
                    dni + "@lasbambas.pe", "9" + dni, area, e);
            ALUMNO.insertar(a);
        }
        return a;
    }

    private static Curso buscarCurso(String codigo) throws SQLException {
        for (Curso c : CURSO.listar()) if (c.getCodigo().equals(codigo)) return c;
        return null;
    }

    private static Instructor buscarInstructor(String dni) throws SQLException {
        for (Instructor i : INSTRUCTOR.listar()) if (i.getDni().equals(dni)) return i;
        return null;
    }

    private static Capacitacion buscarCapacitacion(String codigo) throws SQLException {
        for (Capacitacion c : CAPACITACION.listar()) if (c.getCodigo().equals(codigo)) return c;
        return null;
    }

    private static void titulo(String texto) {
        System.out.println("\n" + "=".repeat(78));
        System.out.println(" " + texto);
        System.out.println("=".repeat(78));
    }
}
