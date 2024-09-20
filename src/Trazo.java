import java.awt.Point;
import java.util.List;

public class Trazo {

    private final String SEPARADOR = "; ";
    private List<Point> puntos; // Puntos del trazo
    private Herramienta herramienta; // Herramienta utilizada para el trazo

    public Trazo(List<Point> puntos, Herramienta herramienta) {
        this.puntos = puntos;
        this.herramienta = herramienta;
    }

    public List<Point> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<Point> puntos) {
        this.puntos = puntos;
    }

    public Herramienta getHerramienta() {
        return herramienta;
    }

    public void setHerramienta(Herramienta herramienta) {
        this.herramienta = herramienta;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Point p : puntos) {
            sb.append(p.x).append(SEPARADOR).append(p.y).append(SEPARADOR);
        }
        return sb.toString() + "Herramienta: " + herramienta;
    }
}
