import java.awt.Point;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Dibujo {
    private List<Trazo> trazos;

    public Dibujo() {
        trazos = new ArrayList<>();
    }

    public void agregar(Trazo t) {
        if (t != null) {
            trazos.add(t);
        }
    }

    public void eliminar(Trazo t) {
        trazos.remove(t);
    }

    public List<Trazo> getTrazos() {
        return trazos;
    }

    public int getLongitud() {
        return trazos.size();
    }

    public Trazo getTrazo(int posicion) {
        return (posicion >= 0 && posicion < trazos.size()) ? trazos.get(posicion) : null;
    }    

    // Método para leer los trazos desde un archivo
    public void desdeArchivo(String nombreArchivo) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(nombreArchivo));
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("; ");
                if (datos.length > 0) {
                    // Obtener la herramienta (asumiendo que la primera parte es el tipo)
                    Herramienta herramienta = Herramienta.valueOf(datos[0].trim());

                    // Crear una lista de puntos para el trazo
                    List<Point> puntos = new ArrayList<>();
                    for (int i = 1; i < datos.length; i++) {
                        String[] coords = datos[i].split(",");
                        if (coords.length == 2) {
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            puntos.add(new Point(x, y));
                        }
                    }

                    // Crear el trazo y agregarlo a la lista
                    Trazo trazo = new Trazo(puntos, herramienta);
                    agregar(trazo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Metodo para guardar los trazos en un archivo
    public boolean haciaArchivo(String nombreArchivo) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(nombreArchivo));
            for (Trazo trazo : trazos) {
                writer.write(trazoToString(trazo));
                writer.newLine();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Método para convertir un trazo a una cadena separada por ;
    private String trazoToString(Trazo trazo) {
        StringBuilder sb = new StringBuilder();
        sb.append(trazo.getHerramienta().name()).append("; ");
        List<Point> puntos = trazo.getPuntos();
        for (Point p : puntos) {
            sb.append(p.x).append(",").append(p.y).append("; ");
        }
        return sb.toString();
    }    
}
