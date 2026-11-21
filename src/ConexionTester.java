import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import javax.swing.*;

public class ConexionTester extends JPanel {
    // Guardo los campos como atributos para poder leerlos cuando el usuario pulse el botón.
    private final JTextField campoUrl = new JTextField("jdbc:sqlite:inventario.db");
    private final JTextField campoUsuario = new JTextField();
    private final JPasswordField campoClave = new JPasswordField();
    private final JButton boton = new JButton("Probar conexión");
    private final JTextArea resultado = new JTextArea(5, 50);
    private final PruebaJdbc prueba = new PruebaJdbc();

    public ConexionTester() {
        super(new BorderLayout(10, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        add(new JLabel("Configuración de la base de datos"), BorderLayout.NORTH);

        // Uso dos columnas para que cada etiqueta quede alineada con su campo.
        JPanel nombres = new JPanel(new GridLayout(3, 1, 0, 6));
        JPanel campos = new JPanel(new GridLayout(3, 1, 0, 6));
        agregarCampo("URL JDBC", campoUrl, nombres, campos);
        agregarCampo("Usuario", campoUsuario, nombres, campos);
        agregarCampo("Contraseña", campoClave, nombres, campos);
        JPanel formulario = new JPanel(new BorderLayout(12, 8));
        formulario.add(nombres, BorderLayout.WEST);
        formulario.add(campos, BorderLayout.CENTER);
        formulario.add(new JLabel("SQLite permite dejar usuario y contraseña vacíos."),
                BorderLayout.SOUTH);
        add(formulario, BorderLayout.CENTER);

        JPanel salida = new JPanel(new BorderLayout(0, 8));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        acciones.add(boton);
        salida.add(acciones, BorderLayout.NORTH);
        // Dejo el resultado en modo de lectura y agrego desplazamiento para los errores largos.
        resultado.setEditable(false);
        resultado.setLineWrap(true);
        resultado.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(resultado);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultado de la prueba"));
        salida.add(scroll, BorderLayout.CENTER);
        add(salida, BorderLayout.SOUTH);

        // Al pulsar el botón, se revisan los datos y comienza la prueba de conexión.
        boton.addActionListener(evento -> probarConexion());

    }

    // Uso el mismo método para agregar cada etiqueta junto con su campo.
    private void agregarCampo(String nombre, JTextField campo,
                              JPanel nombres, JPanel campos) {
        JLabel etiqueta = new JLabel(nombre);
        etiqueta.setLabelFor(campo);
        nombres.add(etiqueta);
        campos.add(campo);
    }

    // Muestro todos los resultados en la misma área y cambio el color según lo que ocurrió.
    private void mostrar(String mensaje, Color color) {
        resultado.setForeground(color);
        resultado.setText(mensaje);
        resultado.setCaretPosition(0);
    }

    private void probarConexion() {
        // Quito los espacios de los extremos y compruebo que haya una dirección antes de conectar.
        String url = campoUrl.getText().trim();
        if (url.isEmpty()) {
            mostrar("Escribe la URL JDBC antes de probar la conexión.", Color.RED.darker());
            campoUrl.requestFocusInWindow();
            return;
        }
        String usuario = campoUsuario.getText();
        char[] clave = campoClave.getPassword();
        // Desactivo el botón para evitar que otro clic inicie una segunda prueba mientras esta sigue en curso.
        boton.setEnabled(false);
        mostrar("Probando conexión…", Color.DARK_GRAY);

        // Abro la conexión en segundo plano para que la ventana siga respondiendo durante la prueba.
        new SwingWorker<String, Void>() {
            protected String doInBackground() throws Exception {
                try {
                    return prueba.conectar(url, usuario, new String(clave));
                } finally {
                    // Al terminar el intento, borro los caracteres del arreglo que usé para leer la contraseña.
                    Arrays.fill(clave, '\0');
                }
            }

            // Cuando termina la tarea, muestro el resultado y vuelvo a habilitar el botón.
            protected void done() {
                try {
                    mostrar(get(), new Color(20, 110, 55));
                } catch (ExecutionException error) {
                    // Recupero la causa original para saber si faltó el controlador o si falló la conexión.
                    mostrarError(error.getCause());
                } catch (InterruptedException error) {
                    Thread.currentThread().interrupt();
                    mostrar("La prueba se interrumpió.", Color.RED.darker());
                } finally {
                    boton.setEnabled(true);
                }
            }
        }.execute();
    }

    private void mostrarError(Throwable causa) {
        String mensaje;
        if (causa instanceof ClassNotFoundException) {
            mensaje = "Falta el controlador JDBC. Revisa la carpeta lib.";
        } else if (causa instanceof SQLException) {
            mensaje = "Fallo de conexión. Revisa la URL y los datos de acceso.";
        } else {
            mensaje = "La prueba terminó con un error inesperado.";
        }
        // Debajo del aviso agrego el detalle técnico para que se pueda identificar el problema.
        mostrar(mensaje + "\nDetalle: " + causa.getMessage(), Color.RED.darker());
    }

    public static void main(String[] args) {
        // Creo la ventana en el hilo que usa Swing para mostrar y actualizar sus componentes.
        SwingUtilities.invokeLater(() -> {
            ConexionTester panel = new ConexionTester();
            JFrame ventana = new JFrame("Prueba de conexión JDBC");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setContentPane(panel);
            ventana.getRootPane().setDefaultButton(panel.boton);
            ventana.pack();
            ventana.setMinimumSize(ventana.getSize());
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}
