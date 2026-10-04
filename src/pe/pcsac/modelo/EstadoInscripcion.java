package pe.pcsac.modelo;

/** Resultado de un participante en una capacitacion (RF-07). */
public enum EstadoInscripcion {
    PENDIENTE("Pendiente"),
    APROBADO("Aprobado"),
    DESAPROBADO("Desaprobado");

    private final String descripcion;

    EstadoInscripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
