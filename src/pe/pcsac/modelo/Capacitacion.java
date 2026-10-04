package pe.pcsac.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Dictado concreto de un curso para una empresa en una fecha, a cargo de un
 * instructor. Es la entidad que agrupa las inscripciones.
 */
public class Capacitacion {

    private int idCapacitacion;
    private String codigo;
    private Curso curso;
    private Empresa empresa;
    private Instructor instructor;
    private LocalDate fecha;
    private double notaMinima;

    public Capacitacion() { }

    public Capacitacion(String codigo, Curso curso, Empresa empresa,
                        Instructor instructor, LocalDate fecha, double notaMinima) {
        setCodigo(codigo);
        setCurso(curso);
        setEmpresa(empresa);
        setInstructor(instructor);
        setFecha(fecha);
        setNotaMinima(notaMinima);
    }

    public int getIdCapacitacion() { return idCapacitacion; }

    public void setIdCapacitacion(int idCapacitacion) { this.idCapacitacion = idCapacitacion; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo de la capacitacion es obligatorio.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public Curso getCurso() { return curso; }

    public void setCurso(Curso curso) {
        if (curso == null) throw new IllegalArgumentException("La capacitacion necesita un curso.");
        this.curso = curso;
    }

    public Empresa getEmpresa() { return empresa; }

    public void setEmpresa(Empresa empresa) {
        if (empresa == null) throw new IllegalArgumentException("La capacitacion necesita una empresa.");
        this.empresa = empresa;
    }

    public Instructor getInstructor() { return instructor; }

    public void setInstructor(Instructor instructor) {
        if (instructor == null) throw new IllegalArgumentException("La capacitacion necesita un instructor.");
        this.instructor = instructor;
    }

    public LocalDate getFecha() { return fecha; }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) throw new IllegalArgumentException("La fecha es obligatoria.");
        this.fecha = fecha;
    }

    public double getNotaMinima() { return notaMinima; }

    public void setNotaMinima(double notaMinima) {
        if (notaMinima < 0 || notaMinima > 20) {
            throw new IllegalArgumentException("La nota minima debe estar entre 0 y 20.");
        }
        this.notaMinima = notaMinima;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Capacitacion)) return false;
        return Objects.equals(codigo, ((Capacitacion) o).codigo);
    }

    @Override
    public int hashCode() { return Objects.hash(codigo); }

    @Override
    public String toString() {
        return codigo + " - " + (curso != null ? curso.getNombre() : "") +
               " (" + (empresa != null ? empresa.getRazonSocial() : "") + ")";
    }
}
