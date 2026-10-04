package pe.pcsac.modelo;

import java.util.Objects;

/** Trabajador de una empresa minera que recibe la capacitacion (RF-01). */
public class Alumno {

    private int idAlumno;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correo;
    private String telefono;
    private String area;
    private Empresa empresa;

    public Alumno() { }

    public Alumno(String dni, String nombres, String apellidos, String correo,
                  String telefono, String area, Empresa empresa) {
        setDni(dni);
        setNombres(nombres);
        setApellidos(apellidos);
        setCorreo(correo);
        this.telefono = telefono;
        this.area = area;
        setEmpresa(empresa);
    }

    public int getIdAlumno() { return idAlumno; }

    public void setIdAlumno(int idAlumno) { this.idAlumno = idAlumno; }

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

    public String getCorreo() { return correo; }

    public void setCorreo(String correo) {
        if (correo != null && !correo.isBlank() && !correo.contains("@")) {
            throw new IllegalArgumentException("El correo no tiene un formato valido.");
        }
        this.correo = correo;
    }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getArea() { return area; }

    public void setArea(String area) { this.area = area; }

    public Empresa getEmpresa() { return empresa; }

    public void setEmpresa(Empresa empresa) {
        if (empresa == null) {
            throw new IllegalArgumentException("El alumno debe pertenecer a una empresa.");
        }
        this.empresa = empresa;
    }

    public String getNombreCompleto() { return nombres + " " + apellidos; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alumno)) return false;
        return Objects.equals(dni, ((Alumno) o).dni);
    }

    @Override
    public int hashCode() { return Objects.hash(dni); }

    @Override
    public String toString() { return getNombreCompleto() + " (DNI " + dni + ")"; }
}
