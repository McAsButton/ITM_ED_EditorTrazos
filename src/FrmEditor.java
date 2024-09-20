import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

public class FrmEditor extends JFrame {
    private JToolBar tbMenu;
    private JButton btnTrazo, btnMano, btnBorrar, btnLimpiar, btnAbrir, btnGuardar;
    private JComboBox<String> cmbFiguras;
    private PanelDibujo pnlDibujo;
    private Dibujo dibujo;

    public FrmEditor() {

        // Establecer el look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setTitle("Editor de Trazos");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tbMenu = new JToolBar();
        btnTrazo = new JButton();
        btnMano = new JButton();
        btnBorrar = new JButton();
        btnLimpiar = new JButton();
        btnAbrir = new JButton();
        btnGuardar = new JButton();
        cmbFiguras = new JComboBox<>();
        pnlDibujo = new PanelDibujo();
        dibujo = new Dibujo();

        // Crear y configurar los botones
        btnTrazo.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/Img/lapiz-3d.png")).getImage()
                .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnTrazo.setToolTipText("Trazar");
        btnTrazo.setBorderPainted(false); // Quitar el borde del botón
        btnTrazo.setContentAreaFilled(false);

        btnMano.setIcon(new ImageIcon(new ImageIcon(getClass().getResource("/Img/mano.png")).getImage()
                .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnMano.setToolTipText("Dibujar a mano alzada");
        btnMano.setBorderPainted(false);
        btnMano.setContentAreaFilled(false);

        btnBorrar.setIcon(new ImageIcon(
                new ImageIcon(getClass().getResource("/Img/goma-de-borrar.png")).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnBorrar.setToolTipText("Borrar");
        btnBorrar.setBorderPainted(false);
        btnBorrar.setContentAreaFilled(false);

        btnLimpiar.setIcon(new ImageIcon(
                new ImageIcon(getClass().getResource("/Img/escoba.png")).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnLimpiar.setToolTipText("Limpiar");
        btnLimpiar.setBorderPainted(false);
        btnLimpiar.setContentAreaFilled(false);

        btnAbrir.setIcon(new ImageIcon(
                new ImageIcon(getClass().getResource("/Img/carpeta-abierta.png")).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnAbrir.setToolTipText("Abrir");
        btnAbrir.setBorderPainted(false);
        btnAbrir.setContentAreaFilled(false);

        btnGuardar.setIcon(new ImageIcon(
                new ImageIcon(getClass().getResource("/Img/disco-flexible.png")).getImage()
                        .getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
        btnGuardar.setToolTipText("Guardar");
        btnGuardar.setBorderPainted(false);
        btnGuardar.setContentAreaFilled(false);

        // Agregar opciones al JComboBox
        cmbFiguras.addItem("Línea");
        cmbFiguras.addItem("Rectángulo");
        cmbFiguras.addItem("Círculo");
        cmbFiguras.setMaximumSize(new Dimension(100, cmbFiguras.getPreferredSize().height));

        // Agregar los botones al JToolBar
        tbMenu.add(cmbFiguras);
        tbMenu.add(btnTrazo);
        tbMenu.add(btnMano);
        tbMenu.add(btnBorrar);
        tbMenu.add(btnLimpiar);
        tbMenu.add(btnAbrir);
        tbMenu.add(btnGuardar);

        // Agregar barra de herramientas y panel de dibujo a la ventana
        getContentPane().add(tbMenu, BorderLayout.NORTH);
        getContentPane().add(pnlDibujo, BorderLayout.CENTER);

        // Cambiar herramienta al seleccionar un elemento del JComboBox
        cmbFiguras.addActionListener(e -> {
            btnTrazo.doClick();
        });

        // Agregar eventos a los botones
        btnLimpiar.addActionListener(e -> {
            pnlDibujo.limpiarDibujo();
            pnlDibujo.setEstado(Estado.TRAZO);
            btnTrazo.requestFocusInWindow();
            btnTrazo.doClick();
        });

        btnMano.addActionListener(e -> {
            pnlDibujo.setHerramienta(Herramienta.MANO);
            pnlDibujo.setEstado(Estado.TRAZO);
            btnMano.requestFocusInWindow();
        });

        btnTrazo.addActionListener(e -> {
            int elegido = cmbFiguras.getSelectedIndex();
            if (elegido >= 0 && elegido < Herramienta.values().length) {
                Herramienta herramientaSeleccionada = Herramienta.values()[elegido];
                setHerramienta(herramientaSeleccionada);
            }
            pnlDibujo.setEstado(Estado.TRAZO);
            btnTrazo.requestFocusInWindow();
        });

        btnBorrar.addActionListener(e -> {
            pnlDibujo.setHerramienta(Herramienta.BORRADOR);
            pnlDibujo.setEstado(Estado.BORRAR);
            btnBorrar.requestFocusInWindow();
        });

        btnGuardar.addActionListener(e -> guardarDibujo());

        btnAbrir.addActionListener(e -> cargarDibujo());
    }

    // Metodo para actualizar la herramienta seleccionada
    public void setHerramienta(Herramienta herramienta) {
        pnlDibujo.setHerramienta(herramienta);
    }

    // Metodo para guardar el dibujo en un archivo
    private void guardarDibujo() {
        dibujo = pnlDibujo.getDibujo();

        // Crear un JFileChooser para seleccionar la ubicación y nombre del archivo
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar dibujo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Trazos (*.dbj)", "dbj"));
        int resultado = fileChooser.showSaveDialog(this);

        // Si el usuario selecciona un archivo
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();

            // Agregar la extensión .txt si no está presente
            if (!rutaArchivo.toLowerCase().endsWith(".dbj")) {
                rutaArchivo += ".dbj";
            }

            // Guardar el dibujo en el archivo seleccionado
            if (dibujo.haciaArchivo(rutaArchivo)) {
                JOptionPane.showMessageDialog(this, "Dibujo guardado exitosamente.");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar el dibujo.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Metodo para cargar un dibujo desde un archivo
    private void cargarDibujo() {

        // Crear un JFileChooser para seleccionar el archivo a cargar
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir dibujo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de Trazos (*.dbj)", "dbj"));
        int resultado = fileChooser.showOpenDialog(this);

        // Si el usuario selecciona un archivo
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            String rutaArchivo = archivoSeleccionado.getAbsolutePath();

            // Cargar el dibujo desde el archivo seleccionado
            dibujo.desdeArchivo(rutaArchivo);
            pnlDibujo.setDibujo(dibujo); // Actualizar el dibujo en el panel

            JOptionPane.showMessageDialog(this, "Dibujo cargado exitosamente.");

            // Cambiar a la herramienta de trazo
            btnTrazo.doClick();
        }
    }
}
