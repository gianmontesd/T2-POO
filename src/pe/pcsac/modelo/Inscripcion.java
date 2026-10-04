package pe.pcsac.modelo;

import java.time.LocalDate;

/**
 * Participacion de un alumno en una capacitacion. Guarda el voucher de pago
 * (RF-05), la nota final (RF-06) y el estado derivado de ella (RF-07).
 */
public class Inscripcion {

    private int idInscripcion;
    private Alumno alumno;
    private Capacitacion capacitacion;
    private LocalDate fechaInscripcion;
    private String voucher;
    private Double notaFinal;                 // null mientras no se registre
    private EstadoInscripcion estado;

    public Inscripcion() {
        this.estado = EstadoInscripcion.PENDIENTE;
    }

    public Inscripcion(Alumno alumno, Capacitacion capacitacion,
                       LocalDate fechaInscripcion, String voucher) {
        setAlumno(alumno);
        setCapacitacion(capacitacion);
        setFechaInscripcion(fechaInscripcion);
        this.voucher = voucher;
        this.estado = EstadoInscripcion.PENDIENTE;
    }

    /**
     * RF-07: asigna la nota y deduce el estado comparandola con la nota minima
     * de la capacitacion. El estado nunca se fija a mano desde fuera.
     */
    public void registrarNota(double nota) {
        if (nota < 0 || nota > 20) {
            throw new IllegalArgumentException("La nota debe estar entre 0 y 20.");
        }
        if (capacitacion == null) {
            throw new IllegalStateException("La inscripcion no tiene capacitacion asociada.");
        }
        this.notaFinal = nota;
        this.estado = nota >= capacitacion.getNotaMinima()
                ? EstadoInscripcion.APROBADO
                : EstadoInscripcion.DESAPROBADO;
    }

    /** Solo un alumno aprobado puede recibir certificado (RF-09). */
    public boolean puedeCertificarse() {
        return estado == EstadoInscripcion.APROBADO;
    }

    public int getIdInscripcion() { return idInscripcion; }

    public void setIdInscripcion(int idInscripcion) { this.idInscripcion = idInscripcion; }

    public Alumno getAlumno() { return alumno; }

    public void setAlumno(Alumno alumno) {
        if (alumno == null) throw new IllegalArgumentException("La inscripcion necesita un alumno.");
        this.alumno = alumno;
    }

    public Capacitacion getCapacitacion() { return capacitacion; }

    public void setCapacitacion(Capacitacion capacitacion) {
        if (capacitacion == null) {
            throw new IllegalArgumentException("La inscripcion necesita una capacitacion.");
        }
        this.capacitacion = capacitacion;
    }

    public LocalDate getFechaInscripcion() { return fechaInscripcion; }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        if (fechaInscripcion == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getVoucher() { return voucher; }

    public void setVoucher(String voucher) { this.voucher = voucher; }

    public Double getNotaFinal() { return notaFinal; }

    /** Uso exclusivo del DAO al reconstruir el objeto desde la base de datos. */
    public void restaurarDesdeBD(Double notaFinal, EstadoInscripcion estado) {
        this.notaFinal = notaFinal;
        this.estado = estado != null ? estado : EstadoInscripcion.PENDIENTE;
    }

    public EstadoInscripcion getEstado() { return estado; }

    @Override
    public String toString() {
        return (alumno != null ? alumno.getNombreCompleto() : "?") + " - " +
               (capacitacion != null ? capacitacion.getCodigo() : "?") + " [" + estado + "]";
    }
}
