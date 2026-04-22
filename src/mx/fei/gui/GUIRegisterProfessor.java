package mx.fei.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import mx.fei.logic.dao.ProfessorDAO;
import mx.fei.logic.exceptions.DataOperationException;
import mx.fei.logic.guibuttons.ButtonsRegisterProfessor;

public class GUIRegisterProfessor extends JFrame {

    private JTextField textFieldName;
    private JTextField textFieldPersonalNumber;
    private JTextField textFieldEmail;
    private JPasswordField textFieldPassword;
    private JTextField textFieldNRC;
    private JCheckBox checkBoxCoordinator;
    private JButton jButtonRegister;
    private JButton jButtonCancel;

    public GUIRegisterProfessor() throws DataOperationException {
        setTitle("GUIRegisterProfessor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(24, 32, 24, 32));

        JLabel title = new JLabel("Datos del Profesor:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 12));

        textFieldName = new JTextField();
        textFieldPersonalNumber = new JTextField();
        textFieldEmail = new JTextField();
        textFieldPassword = new JPasswordField();
        textFieldNRC = new JTextField();

        String[] labels = {"Nombre:", "No. de personal:", "Correo:", "Contraseña:", "NRC de EE:"};
        JComponent[] fields = {textFieldName, textFieldPersonalNumber, textFieldEmail, textFieldPassword, textFieldNRC};
        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            formPanel.add(label);
            formPanel.add(fields[i]);
        }
        mainPanel.add(formPanel, BorderLayout.CENTER);
        checkBoxCoordinator = new JCheckBox("Coordinador");
        checkBoxCoordinator.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ProfessorDAO professorDAO = new ProfessorDAO();
        if (professorDAO.existsCoordinator()) {
            checkBoxCoordinator.setEnabled(false);
        }
        jButtonRegister = new JButton("Registrar");
        jButtonCancel = new JButton("Cancelar");
        jButtonRegister.setBackground(new Color(30, 30, 35));
        jButtonRegister.setForeground(Color.WHITE);
        jButtonCancel.setBackground(new Color(30, 30, 35));
        jButtonCancel.setForeground(Color.WHITE);
        ButtonsRegisterProfessor listener = new ButtonsRegisterProfessor(this);
        jButtonRegister.addActionListener(listener);
        jButtonCancel.addActionListener(listener);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.add(jButtonRegister);
        buttonsPanel.add(jButtonCancel);
        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.add(checkBoxCoordinator, BorderLayout.WEST);
        bottomRow.add(buttonsPanel, BorderLayout.EAST);
        mainPanel.add(bottomRow, BorderLayout.SOUTH);
        setContentPane(mainPanel);
        setPreferredSize(new Dimension(520, 340));
        pack();
        setLocationRelativeTo(null);
    }

    public JTextField getTextFieldName() {
        return textFieldName;
    }

    public JTextField getTextFieldPersonalNumber() {
        return textFieldPersonalNumber;
    }

    public JTextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public JPasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public JTextField getTextFieldNRC() {
        return textFieldNRC;
    }

    public JCheckBox getCheckBoxCoordinator() {
        return checkBoxCoordinator;
    }

    public JButton getButtonRegister() {
        return jButtonRegister;
    }

    public JButton getButtonCancel() {
        return jButtonCancel;
    }
}