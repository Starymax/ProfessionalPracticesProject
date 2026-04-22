package mx.fei.gui;

import mx.fei.logic.guibuttons.ButtonsRegisterProfessor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GUIRegisterProfessor extends JFrame {

    private JTextField textFieldPersonalNumber;
    private JTextField textFieldName;
    private JTextField textFieldLastName;
    private JTextField textFieldGender;
    private JComboBox<String> comboBoxShift;
    private JTextField textFieldEmail;
    private JPasswordField textFieldPassword;
    private JCheckBox checkBoxIsCoordinator;
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
        textFieldGender = new JTextField();
        comboBoxShift = new JComboBox<>(new String[]{"Matutino", "Vespertino", "Nocturno"});
        textFieldEmail = new JTextField();
        textFieldPassword = new JPasswordField();

        String[] labels = {"No. de personal:", "Nombre:", "Apellidos:", "Género:", "Turno:", "Correo:", "Contraseña:"};
        JComponent[] fields = {textFieldPersonalNumber, textFieldName, textFieldLastName, textFieldGender, comboBoxShift, textFieldEmail, textFieldPassword};

        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
            formPanel.add(lbl);
            formPanel.add(fields[i]);
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);

        checkBoxIsCoordinator = new JCheckBox("Coordinador");
        checkBoxIsCoordinator.setFont(new Font("SansSerif", Font.PLAIN, 14));

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

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(checkBoxIsCoordinator, BorderLayout.WEST);
        bottomRow.add(btnPanel, BorderLayout.EAST);

        mainPanel.add(bottomRow, BorderLayout.SOUTH);

        setContentPane(mainPanel);
        setPreferredSize(new Dimension(520, 420));
        pack();
        setLocationRelativeTo(null);
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

    public JTextField getTextFieldGender() {
        return textFieldGender;
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

    public JButton getButtonRegister() {
        return jButtonRegister;
    }

    public JButton getButtonCancel() {
        return jButtonCancel;
    }
}