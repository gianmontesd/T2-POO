package pe.pcsac.modelo;

import java.util.Objects;

/** Empresa minera cliente que contrata las capacitaciones (RF-03). */
public class Empresa {

    private int idEmpresa;
    private String ruc;
    private String razonSocial;
    private String contacto;
    private String telefono;

    public Empresa() { }

    public Empresa(String ruc, String razonSocial, String contacto, String telefono) {
        setRuc(ruc);
        setRazonSocial(razonSocial);
        this.contacto = contacto;
        this.telefono = telefono;
    }

    public int getIdEmpresa() { return idEmpresa; }

    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getRuc() { return ruc; }

    public void setRuc(String ruc) {
        if (ruc == null || !ruc.matches("\\d{11}")) {
            throw new IllegalArgumentException("El RUC debe tener 11 digitos.");
        }
        this.ruc = ruc;
    }

    public String getRazonSocial() { return razonSocial; }

    public void setRazonSocial(String razonSocial) {
        if (razonSocial == null || razonSocial.isBlank()) {
            throw new IllegalArgumentException("La razon social es obligatoria.");
        }
        this.razonSocial = razonSocial.trim();
    }

    public String getContacto() { return contacto; }

    public void setContacto(String contacto) { this.contacto = contacto; }

    public String getTelefono() { return telefono; }

    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Empresa)) return false;
        return Objects.equals(ruc, ((Empresa) o).ruc);
    }

    @Override
    public int hashCode() { return Objects.hash(ruc); }

    @Override
    public String toString() { return razonSocial; }
}
