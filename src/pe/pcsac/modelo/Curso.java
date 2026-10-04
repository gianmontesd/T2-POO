package pe.pcsac.modelo;

import java.util.Objects;

/** Curso del catalogo de seguridad y salud ocupacional (RF-02). */
public class Curso {

    private int idCurso;
    private String codigo;
    private String nombre;
    private int duracionHoras;
    private double notaMinima;

    public Curso() { }

    public Curso(String codigo, String nombre, int duracionHoras, double notaMinima) {
        setCodigo(codigo);
        setNombre(nombre);
        setDuracionHoras(duracionHoras);
        setNotaMinima(notaMinima);
    }

    public int getIdCurso() { return idCurso; }

    public void setIdCurso(int idCurso) { this.idCurso = idCurso; }

    public String getCodigo() { return codigo; }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del curso es obligatorio.");
        }
        this.codigo = codigo.trim().toUpperCase();
    }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del curso es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public int getDuracionHoras() { return duracionHoras; }

    public void setDuracionHoras(int duracionHoras) {
        if (duracionHoras <= 0) {
            throw new IllegalArgumentException("La duracion debe ser mayor que cero.");
        }
        this.duracionHoras = duracionHoras;
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
        if (!(o instanceof Curso)) return false;
        return Objects.equals(codigo, ((Curso) o).codigo);
    }

    @Override
    public int hashCode() { return Objects.hash(codigo); }

    @Override
    public String toString() { return codigo + " - " + nombre; }
}
