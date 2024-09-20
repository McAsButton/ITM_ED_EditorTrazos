import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class PanelDibujo extends JPanel {
    private Point inicio, fin;
    private ArrayList<Point> puntosMano;
    private Dibujo dibujo;
    private Trazo trazoSeleccionado;

    Herramienta herramienta = Herramienta.LINEA;
    Estado estado = Estado.TRAZO;

    public PanelDibujo() {
        setBackground(Color.BLACK);
        puntosMano = new ArrayList<>();
        dibujo = new Dibujo();

        // Agregar los listeners para el mouse
        addMouseListener(new MouseAdapter() {
            // Listener para detectar cuando se presiona el click del mouse
            @Override
            public void mousePressed(MouseEvent e) {
                if (estado == Estado.TRAZO) {
                    inicio = e.getPoint();
                }
                if (herramienta == Herramienta.MANO) {
                    puntosMano.clear();
                    puntosMano.add(e.getPoint());
                }
            }

            // Listener para detectar cuando se suelta el click del mouse
            @Override
            public void mouseReleased(MouseEvent e) {
                if (estado == Estado.TRAZO) {
                    fin = e.getPoint();
                    agregarTrazo();
                }
                repaint();
            }

            // Listener para detectar cuando se hace click con el mouse
            @Override
            public void mouseClicked(MouseEvent e) {
                if (estado == Estado.BORRAR) {
                    detectarTrazo(e.getX(), e.getY());
                    if (trazoSeleccionado != null) {
                        dibujo.eliminar(trazoSeleccionado);
                        trazoSeleccionado = null;
                        repaint();
                    }
                }
            }
        });

        // Listener para detectar el movimiento del mouse
        addMouseMotionListener(new MouseMotionListener() {
            // Listener para detectar cuando se arrastra el mouse (click sostenido)
            @Override
            public void mouseDragged(MouseEvent e) {
                if (estado == Estado.TRAZO) {
                    fin = e.getPoint();
                }
                if (herramienta == Herramienta.MANO) {
                    puntosMano.add(e.getPoint());
                }
                repaint();
            }

            // Listener para detectar cuando se mueve el mouse
            @Override
            public void mouseMoved(MouseEvent e) {
                if (estado == Estado.BORRAR) {
                    detectarTrazo(e.getX(), e.getY());
                }
            }
        });
    }

    public void setHerramienta(Herramienta herramienta) {
        this.herramienta = herramienta;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    private void agregarTrazo() {
        if (inicio != null && fin != null) {
            List<Point> puntos = new ArrayList<>();
            if (herramienta == Herramienta.MANO) {
                // Para herramienta de mano, agregar todos los puntos de puntosMano
                if (puntosMano.size() > 1) {
                    puntos.addAll(puntosMano);
                }
            } else {
                // Para otras herramientas, agregar solo el trazo actual (línea, rectángulo, o
                // elíptico)
                puntos.add(inicio);
                puntos.add(fin);
            }
            if (!puntos.isEmpty()) {
                Trazo trazo = new Trazo(puntos, herramienta); // Crear el objeto Trazo
                // System.out.println(trazo); // Imprimir el objeto Trazo en consola
                dibujo.agregar(trazo); // Pasar la herramienta actual
            }
            // Reiniciar el estado de los puntos de inicio y fin
            inicio = null;
            fin = null;
            puntosMano.clear(); // Limpiar los puntos de la mano para el siguiente trazo
        }
    }

    public void limpiarDibujo() {
        dibujo = new Dibujo();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibujar todos los trazos
        for (Trazo trazo : dibujo.getTrazos()) {
            List<Point> puntos = trazo.getPuntos();
            Herramienta herramienta = trazo.getHerramienta();

            // Si el trazo es el seleccionado, cambiar a amarillo
            if (trazo == trazoSeleccionado) {
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE); // Color para los trazos no seleccionados
            }

            if (puntos.size() > 1) {
                switch (herramienta) {
                    case LINEA:
                        for (int i = 0; i < puntos.size() - 1; i++) {
                            Point p1 = puntos.get(i);
                            Point p2 = puntos.get(i + 1);
                            g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }
                        break;
                    case RECTANGULO:
                        Point p1Rect = puntos.get(0);
                        Point p2Rect = puntos.get(1);
                        int xRect = Math.min(p1Rect.x, p2Rect.x);
                        int yRect = Math.min(p1Rect.y, p2Rect.y);
                        int widthRect = Math.abs(p2Rect.x - p1Rect.x);
                        int heightRect = Math.abs(p2Rect.y - p1Rect.y);
                        g.drawRect(xRect, yRect, widthRect, heightRect);
                        break;
                    case OVALO:
                        Point p1Oval = puntos.get(0);
                        Point p2Oval = puntos.get(1);
                        int xOval = Math.min(p1Oval.x, p2Oval.x);
                        int yOval = Math.min(p1Oval.y, p2Oval.y);
                        int widthOval = Math.abs(p2Oval.x - p1Oval.x);
                        int heightOval = Math.abs(p2Oval.y - p1Oval.y);
                        g.drawOval(xOval, yOval, widthOval, heightOval);
                        break;
                    case MANO:
                        for (int i = 0; i < puntos.size() - 1; i++) {
                            Point p1Hand = puntos.get(i);
                            Point p2Hand = puntos.get(i + 1);
                            g.drawLine(p1Hand.x, p1Hand.y, p2Hand.x, p2Hand.y);
                        }
                        break;
                    case BORRADOR:
                        // No es necesario dibujar el borrador
                        break;
                }
            }
        }

        // Dibujar la figura actual (mientras arrastramos el mouse)
        if (inicio != null && fin != null) {
            g.setColor(Color.WHITE); // Color para la figura actual
            switch (herramienta) {
                case LINEA:
                    g.drawLine(inicio.x, inicio.y, fin.x, fin.y);
                    break;
                case RECTANGULO:
                    int x = Math.min(inicio.x, fin.x);
                    int y = Math.min(inicio.y, fin.y);
                    int width = Math.abs(fin.x - inicio.x);
                    int height = Math.abs(fin.y - inicio.y);
                    g.drawRect(x, y, width, height);
                    break;
                case OVALO:
                    int xOval = Math.min(inicio.x, fin.x);
                    int yOval = Math.min(inicio.y, fin.y);
                    int widthOval = Math.abs(fin.x - inicio.x);
                    int heightOval = Math.abs(fin.y - inicio.y);
                    g.drawOval(xOval, yOval, widthOval, heightOval);
                    break;
                case MANO:
                    for (int i = 0; i < puntosMano.size() - 1; i++) {
                        Point p1 = puntosMano.get(i);
                        Point p2 = puntosMano.get(i + 1);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                    break;
                case BORRADOR:
                    // No es necesario dibujar el borrador
                    break;
            }
        }
    }

    // Metodo para detectar el trazo bajo el mouse
    public void detectarTrazo(int x, int y) {
        final int RADIO_DE_TOLERANCIA = 5;
        trazoSeleccionado = null;

        for (Trazo trazo : dibujo.getTrazos()) {
            List<Point> puntos = trazo.getPuntos();

            switch (trazo.getHerramienta()) {
                case LINEA:
                    detectarLinea(x, y, puntos, RADIO_DE_TOLERANCIA, trazo);
                    break;

                case RECTANGULO:
                    detectarRectangulo(x, y, puntos, RADIO_DE_TOLERANCIA, trazo);
                    break;

                case OVALO:
                    detectarOvalo(x, y, puntos, RADIO_DE_TOLERANCIA, trazo);
                    break;

                case MANO:
                    detectarLinea(x, y, puntos, RADIO_DE_TOLERANCIA, trazo);
                    break;

                default:
                    break;
            }

            if (trazoSeleccionado != null) {
                break;
            }
        }
        repaint();
    }

    private void detectarLinea(int x, int y, List<Point> puntos, int tolerancia, Trazo trazo) {
        for (int i = 0; i < puntos.size() - 1; i++) {
            Point p1 = puntos.get(i);
            Point p2 = puntos.get(i + 1);
            if (puntoCercano(x, y, p1.x, p1.y, p2.x, p2.y, tolerancia)) {
                trazoSeleccionado = trazo;
                break;
            }
        }
    }

    private void detectarRectangulo(int x, int y, List<Point> puntos, int tolerancia, Trazo trazo) {
        Point p1 = puntos.get(0);
        Point p2 = puntos.get(1);
        int minX = Math.min(p1.x, p2.x);
        int maxX = Math.max(p1.x, p2.x);
        int minY = Math.min(p1.y, p2.y);
        int maxY = Math.max(p1.y, p2.y);

        if (puntoCercano(x, y, minX, minY, maxX, minY, tolerancia)
                || puntoCercano(x, y, maxX, minY, maxX, maxY, tolerancia)
                || puntoCercano(x, y, maxX, maxY, minX, maxY, tolerancia)
                || puntoCercano(x, y, minX, maxY, minX, minY, tolerancia)) {
            trazoSeleccionado = trazo;
        }
    }

    private void detectarOvalo(int x, int y, List<Point> puntos, int tolerancia, Trazo trazo) {
        Point p1 = puntos.get(0);
        Point p2 = puntos.get(1);
        int centerX = (p1.x + p2.x) / 2;
        int centerY = (p1.y + p2.y) / 2;
        int radiusX = Math.abs(p2.x - p1.x) / 2;
        int radiusY = Math.abs(p2.y - p1.y) / 2;

        // Ecuación elíptica para calcular la distancia del punto al borde del óvalo
        double elipseEquation = Math.pow((x - centerX) / (double) radiusX, 2)
                + Math.pow((y - centerY) / (double) radiusY, 2);

        // Calcular la distancia del punto al borde del óvalo
        double distanceToEdge = Math.abs(1.0 - elipseEquation) * Math.max(radiusX, radiusY);

        if (distanceToEdge <= tolerancia) {
            trazoSeleccionado = trazo;
        }
    }

    // Método para verificar si un punto (x, y) está cerca de una línea entre dos
    // puntos (x1, y1) y (x2, y2)
    private boolean puntoCercano(int x, int y, int x1, int y1, int x2, int y2, int tolerancia) {
        double distancia = distanciaAlSegmento(x, y, x1, y1, x2, y2);
        return distancia <= tolerancia;
    }

    // Método para calcular la distancia desde un punto a un segmento de línea
    private double distanciaAlSegmento(int x, int y, int x1, int y1, int x2, int y2) {
        double px = x2 - x1;
        double py = y2 - y1;
        double temp = (px * px) + (py * py);
        double u = ((x - x1) * px + (y - y1) * py) / temp;

        if (u > 1)
            u = 1;
        else if (u < 0)
            u = 0;

        double xCerca = x1 + u * px;
        double yCerca = y1 + u * py;

        double dx = x - xCerca;
        double dy = y - yCerca;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public Dibujo getDibujo() {
        return dibujo;
    }

    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
        repaint();
    }
}
