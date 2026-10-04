package pe.pcsac.servicio;

/** Error de negocio que la vista muestra al usuario tal cual. */
public class ServicioException extends Exception {
    public ServicioException(String mensaje) { super(mensaje); }
    public ServicioException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
