package mx.fei.gui.views;

import mx.fei.gui.controllers.ButtonsRegisterEnterprise;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

public class GUIRegisterEnterprise extends JFrame {

    private JTextField addressTextField;
    private JTextField nameTextField;
    private JTextField phoneNumberTextField;
    private JTextField emailTextField;
    private JTextField sectorTextField;
    private JTextField directUsersTextField;
    private JTextField indirectUsersTextField;
    private JButton registerButton;
    private JButton cancelButton;

    public GUIRegisterEnterprise() {
        setTitle("ButtonsRegisterEnterprise");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Datos de la Organización Vinculada:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 12));

        nameTextField = new JTextField();
        addressTextField = new JTextField();
        phoneNumberTextField = new JTextField();
        emailTextField = new JTextField();
        sectorTextField = new JTextField();
        directUsersTextField = new JTextField();
        indirectUsersTextField = new JTextField();

        String[] labels = {"Nombre:", "Dirección:", "Telefono:", "Correo Electrónico:", "Sector:", "No. de Usuarios directos:", "No. de Usuarios indirectos:"};
        JComponent[] fields = {nameTextField, addressTextField, phoneNumberTextField, emailTextField, sectorTextField, directUsersTextField, indirectUsersTextField};

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            panel.add(label);
            panel.add(fields[i]);
        }

        mainPanel.add(panel, BorderLayout.CENTER);

        registerButton = new JButton("Registrar");
        cancelButton = new JButton("Cancelar");

        registerButton.setBackground(new Color(30, 30, 35));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);

        cancelButton.setBackground(new Color(30, 30, 35));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);

        ButtonsRegisterEnterprise listener = new ButtonsRegisterEnterprise(this);
        registerButton.addActionListener(listener);
        cancelButton.addActionListener(listener);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setPreferredSize(new Dimension(580, 440));
        pack();
        setLocationRelativeTo(null);
    }

    public boolean validateFields() {
        boolean validated = true;
        java.util.List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(nameTextField.getText().isEmpty(), "El campo Nombre es obligatorio"),
                Map.entry(addressTextField.getText().isEmpty(),"El campo Direccion es obligatorio."),
                Map.entry(phoneNumberTextField.getText().isEmpty(),"El campo Telefono es obligatorio."),
                Map.entry(emailTextField.getText().isEmpty(),"El campo correo es obligatorio."),
                Map.entry(sectorTextField.getText().isEmpty(),"El campo Sector es obligatorio."),
                Map.entry(directUsersTextField.getText().isEmpty(),"El campo Usuarios Directos es obligatorio."),
                Map.entry(indirectUsersTextField.getText().isEmpty(),"El campo Usuarios Indirectos es obligatorio.")
        );
        for (Map.Entry<Boolean, String> validation : validations) {
            if (validation.getKey()) {
                showError(validation.getValue());
                validated = false;
                break;
            }
        }
        return validated;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Campo requerido", JOptionPane.WARNING_MESSAGE);
    }

    public JTextField getAddressTextField() {
        return addressTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public JTextField getPhoneNumberTextField() {
        return phoneNumberTextField;
    }

    public JTextField getEmailTextField() {
        return emailTextField;
    }

    public JTextField getSectorTextField() {
        return sectorTextField;
    }

    public JTextField getDirectUsersTextField() {
        return directUsersTextField;
    }

    public JTextField getIndirectUsersTextField() {
        return indirectUsersTextField;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JButton getCancelButton() {
        return cancelButton;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIRegisterEnterprise().setVisible(true));
    }
}