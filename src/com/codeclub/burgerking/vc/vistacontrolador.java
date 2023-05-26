package com.codeclub.burgerking.vc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.PreparedStatement;
import java.sql.Connection;

import static com.codeclub.burgerking.model.connection.getConnection;

public class vistacontrolador extends JFrame  {
    private JPanel panelInsertar;
    private JPanel panelMostrar;
    File fitxerSeleccionat;
    JTextField txtBuscar;
    JButton btnBuscar;
    JPanel panelImatge;
    JLabel lblImatge;

    public vistacontrolador() {


        super("Menú BurguerKing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(700, 400));

        JTabbedPane pestanya = new JTabbedPane();

        panelInsertar = new JPanel();
        panelInsertar.setLayout(new GridBagLayout());
        panelMostrar = new JPanel();

        pestanya.addTab("Insertar", panelInsertar);
        pestanya.addTab("Mostrar", panelMostrar);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel lblNom = new JLabel();
        JTextField txtNom = new JTextField("Nom", 10);
        JLabel lblPreu = new JLabel();
        JTextField txtPreu = new JTextField("Preu", 10);
        JButton btnSeleccionarFitxer = new JButton("Seleccionar foto");
        JButton btnEnviar = new JButton("Enviar");
        btnEnviar.setPreferredSize(new Dimension(100, 40));

        btnSeleccionarFitxer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int seleccion = fileChooser.showOpenDialog(vistacontrolador.this);
                if (seleccion == JFileChooser.APPROVE_OPTION) {
                    fitxerSeleccionat = fileChooser.getSelectedFile();
                    System.out.println("Arxiu seleccionat: " + fitxerSeleccionat.getAbsolutePath());
                }
            }
        });

        btnEnviar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fitxerSeleccionat == null) {
                    JOptionPane.showMessageDialog(vistacontrolador.this, "No s'ha torbat cap arxiu",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String nombre = txtNom.getText();
                double precio = Double.parseDouble(txtPreu.getText());
                int id = getMaxId() + 1;

                try (Connection conn = getConnection()) {
                    String sql = "INSERT INTO menjar (id_menjar, nom, preu, foto) VALUES (?, ?, ?, ?)";
                    PreparedStatement statement = conn.prepareStatement(sql);

                    byte[] contenidoArchivo = new byte[(int) fitxerSeleccionat.length()];
                    try (FileInputStream fis = new FileInputStream(fitxerSeleccionat)) {
                        fis.read(contenidoArchivo);
                    }

                    statement.setInt(1, id);
                    statement.setString(2, nombre);
                    statement.setDouble(3, precio);
                    statement.setBytes(4, contenidoArchivo);

                    statement.executeUpdate();

                    JOptionPane.showMessageDialog(vistacontrolador.this, "Dades enviades a la base de dades");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(vistacontrolador.this, "Error al enviar dades a la base de dades:\n" + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panelMostrar.setLayout(new GridBagLayout());

        JLabel lblBuscar = new JLabel("Buscar nom:");
        txtBuscar = new JTextField(10);
        btnBuscar = new JButton("Buscar");
        panelImatge = new JPanel(new BorderLayout());
        lblImatge = new JLabel();

        btnBuscar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nombre = txtBuscar.getText();
                buscarPorNombre(nombre);
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        panelMostrar.add(lblBuscar, constraints);

        constraints.gridx = 1;
        panelMostrar.add(txtBuscar, constraints);

        constraints.gridx = 2;
        panelMostrar.add(btnBuscar, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 3;
        panelMostrar.add(panelImatge, constraints);

        panelImatge.add(lblImatge, BorderLayout.CENTER);

        constraints.gridx = 0;
        constraints.gridy = 0;
        panelInsertar.add(lblNom, constraints);

        constraints.gridx = 1;
        panelInsertar.add(txtNom, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        panelInsertar.add(lblPreu, constraints);

        constraints.gridx = 1;
        panelInsertar.add(txtPreu, constraints);

        constraints.gridwidth = 2;
        constraints.gridy = 2;
        panelInsertar.add(btnSeleccionarFitxer, constraints);

        constraints.gridy = 3;
        panelInsertar.add(btnEnviar, constraints);

        getContentPane().add(pestanya);
        pack();
    }

    public static int getMaxId() {
        int maxId = 0;
        try (Connection connection = getConnection()) {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(id_menjar) FROM menjar";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                maxId = resultSet.getInt(1);
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return maxId;
    }

    private void buscarPorNombre(String nombre) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM menjar WHERE nom = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nombre);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id_menjar");
                String nombreEncontrado = resultSet.getString("nom");
                double precio = resultSet.getDouble("preu");
                byte[] foto = resultSet.getBytes("foto");

                GridBagConstraints constraints = new GridBagConstraints();
                constraints.anchor = GridBagConstraints.WEST;
                constraints.insets = new Insets(5, 5, 5, 5);

                JLabel lblId = new JLabel("ID:");
                JLabel lblNombre = new JLabel("Nom:");
                JLabel lblPrecio = new JLabel("Preu:");

                JLabel lblIdValor = new JLabel(Integer.toString(id));
                JLabel lblNombreValor = new JLabel(nombreEncontrado);
                JLabel lblPrecioValor = new JLabel(Double.toString(precio));

                constraints.gridx = 0;
                constraints.gridy = 1;
                panelMostrar.add(lblId, constraints);

                constraints.gridx = 1;
                panelMostrar.add(lblIdValor, constraints);

                constraints.gridx = 0;
                constraints.gridy = 2;
                panelMostrar.add(lblNombre, constraints);

                constraints.gridx = 1;
                panelMostrar.add(lblNombreValor, constraints);

                constraints.gridx = 0;
                constraints.gridy = 3;
                panelMostrar.add(lblPrecio, constraints);

                constraints.gridx = 1;
                panelMostrar.add(lblPrecioValor, constraints);

                constraints.gridx = 0;
                constraints.gridy = 4;

                ImageIcon imageIcon = new ImageIcon(foto);
                Image image = imageIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                ImageIcon resizedIcon = new ImageIcon(image);
                lblImatge.setIcon(resizedIcon);

                lblImatge.setPreferredSize(new Dimension(200, 150));

                pack();
            } else {
                JOptionPane.showMessageDialog(vistacontrolador.this, "No s'ha trobat cap nom a la BBDD': " + nombre,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vistacontrolador.this, "Error al buscar dades en la base de dades:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
