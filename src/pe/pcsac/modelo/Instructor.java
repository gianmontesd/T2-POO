package pe.pcsac.modelo;

import java.util.Objects;

/** Profesor que dicta la capacitacion; su nombre firma el certificado (RF-09). */
public class Instructor {

    private int idInstructor;
    private String dni;
    private String nombres;
    private String apellidos;
    private String especialidad;

    public Instructor() { }

    public Instructor(String dni, String nombres, String apellidos, String especialidad) {
        setDni(dni);
        setNombres(nombres);
        setApellidos(apellidos);
        setEspecialidad(especialidad);
    }

    public int getIdInstructor() { return idInstructor; }

    public void setIdInstructor(int idInstructor) { this.idInstructor = idInstructor; }

    public String getDni() { return dni; }

    public void setDni(String dni) {
        if (dni == null || !dni.matches("\\d{8}")) {
            throw new IllegalArgumentException("El DNI debe tener 8 digitos.");
        }
        this.dni = dni;
    }

    public String getNombres() { return nombres; }

    public void setNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new IllegalArgumentException("Los nombres son obligatorios.");
        }
        this.nombres = nombres.trim();
    }

    public String getApellidos() { return apellidos; }

    public void setApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new IllegalArgumentException("Los apellidos son obligatorios.");
        }
        this.apellidos = apellidos.trim();
    }

    public String getEspecialidad() { return especialidad; }

    public void setEspecialidad(String especialidad) {
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("La especialidad es obligatoria.");
        }
        this.especialidad = especialidad.trim();
    }

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Instructor)) return false;
        return Objects.equals(dni, ((Instructor) o).dni);
    }

    @Override
    public int hashCode() { return Objects.hash(dni); }

    @Override
    public String toString() { return getNombreCompleto(); }
}
