package mx.fei.gui;

import mx.fei.logic.guibuttons.ButtonsRegisterProfessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class GUIRegisterProfessor extends JFrame {

    private JTextField textFieldPersonalNumber;
    private JTextField textFieldName;
    private JTextField textFieldLastName;
    private JComboBox<String> comboBoxGender;
    private JComboBox<String> comboBoxShift;
    private JTextField textFieldEmail;
    private JPasswordField textFieldPassword;
    private JCheckBox checkBoxIsCoordinator;
    private JCheckBox checkBoxIsAdministrator;
    private JButton jButtonRegister;
    private JButton jButtonCancel;

    public GUIRegisterProfessor() {
        setTitle("GUIRegisterProfessor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Datos del Profesor:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 12));

        textFieldPersonalNumber = new JTextField();
        textFieldName = new JTextField();
        textFieldLastName = new JTextField();
        comboBoxGender = new JComboBox<>(new String[]{"Masculino", "Femenino"});
        comboBoxShift = new JComboBox<>(new String[]{"Matutino", "Vespertino", "Mixto"});
        textFieldEmail = new JTextField();
        textFieldPassword = new JPasswordField();

        String[] labels = {"No. de personal:", "Nombre:", "Apellidos:", "Género:", "Turno:", "Correo:", "Contraseña:"};
        JComponent[] fields = {textFieldPersonalNumber, textFieldName, textFieldLastName, comboBoxGender, comboBoxShift, textFieldEmail, textFieldPassword};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            formPanel.add(lbl);
            formPanel.add(fields[i]);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        checkBoxIsCoordinator = new JCheckBox("Coordinador");
        checkBoxIsCoordinator.setFont(new Font("SansSerif", Font.PLAIN, 14));

        checkBoxIsAdministrator = new JCheckBox("Administrador");
        checkBoxIsAdministrator.setFont(new Font("SansSerif", Font.PLAIN, 14));

        jButtonRegister = new JButton("Registrar");
        jButtonCancel = new JButton("Cancelar");

        jButtonRegister.setBackground(new Color(30, 30, 35));
        jButtonRegister.setForeground(Color.WHITE);
        jButtonRegister.setFocusPainted(false);

        jButtonCancel.setBackground(new Color(30, 30, 35));
        jButtonCancel.setForeground(Color.WHITE);
        jButtonCancel.setFocusPainted(false);

        ButtonsRegisterProfessor buttonsRegisterProfessor = new ButtonsRegisterProfessor(this);
        jButtonRegister.addActionListener(buttonsRegisterProfessor);
        jButtonCancel.addActionListener(buttonsRegisterProfessor);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.add(jButtonRegister);
        btnPanel.add(jButtonCancel);

        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        checkBoxPanel.add(checkBoxIsCoordinator);
        checkBoxPanel.add(checkBoxIsAdministrator);
        
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(checkBoxPanel, BorderLayout.WEST);
        bottomRow.add(btnPanel, BorderLayout.EAST);

        mainPanel.add(bottomRow, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setPreferredSize(new Dimension(570, 420));
        pack();
        setLocationRelativeTo(null);
    }

    public boolean validateFields() {
        boolean validated = true;
        java.util.List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(textFieldPersonalNumber.getText().isEmpty(), "El campo No. de personal es obligatorio"),
                Map.entry(textFieldName.getText().isEmpty(),"El campo nombre es obligatorio."),
                Map.entry(textFieldLastName.getText().isEmpty(),"El campo apellidos es obligatorio."),
                Map.entry(textFieldEmail.getText().isEmpty(),"El campo correo es obligatorio."),
                Map.entry(textFieldPassword.getPassword().length == 0,"El campo contraseña es obligatorio."),
                Map.entry(comboBoxGender.getSelectedItem() == null, "El campo genero es obligatorio.")
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

    public JTextField getTextFieldPersonalNumber() {
        return textFieldPersonalNumber;
    }

    public JTextField getTextFieldName() {
        return textFieldName;
    }

    public JTextField getTextFieldLastName() {
        return textFieldLastName;
    }

    public JComboBox<String> getComboBoxGender() {
        return comboBoxGender;
    }

    public JComboBox<String> getComboBoxShift() {
        return comboBoxShift;
    }

    public JTextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public JPasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public JCheckBox getCheckBoxIsCoordinator() {
        return checkBoxIsCoordinator;
    }

    public JCheckBox getCheckBoxIsAdministrator() {
        return checkBoxIsAdministrator;
    }

    public JButton getButtonRegister() {
        return jButtonRegister;
    }

    public JButton getButtonCancel() {
        return jButtonCancel;
    }
}