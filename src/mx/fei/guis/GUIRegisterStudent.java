package mx.fei.guis;

import mx.fei.logic.guibuttons.ButtonsRegisterStudent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Map;

public class GUIRegisterStudent extends JFrame {
    private JTextField textFieldNames;
    private JTextField textFieldLastName;
    private JTextField textFieldMail;
    private JPasswordField textFieldPassword;
    private JTextField textFieldEnrollment;
    private JTextField textFieldPeriod;
    private JRadioButton RadioButtonMan;
    private JRadioButton radioButtonWoman;
    private JRadioButton radioButtonSpeakIndigenousLanguage;
    private JRadioButton radioButtonDontSpeakIndigenousLanguage;
    private JToggleButton toggleState;
    private JButton buttonConfirm;
    private JButton buttonCancel;

    public GUIRegisterStudent() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Registrar alumno");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(200, 200, 200));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(220, 220, 220));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        gridBagConstraints.anchor = GridBagConstraints.WEST;

        JLabel labelTitle = new JLabel("Registrar alumno");
        labelTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(5, 5, 15, 5);
        formPanel.add(labelTitle, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        formPanel.add(new JLabel("Nombres:"), gridBagConstraints);
        textFieldNames = new JTextField(25);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldNames, gridBagConstraints);
        gridBagConstraints.gridwidth = 1; gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        formPanel.add(new JLabel("Apellidos:"), gridBagConstraints);
        textFieldLastName = new JTextField(25);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldLastName, gridBagConstraints);
        gridBagConstraints.gridwidth = 1; gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 3;
        formPanel.add(new JLabel("Correo:"), gridBagConstraints);
        textFieldMail = new JTextField(25);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldMail, gridBagConstraints);
        gridBagConstraints.gridwidth = 1; gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 4;
        formPanel.add(new JLabel("Contraseña:"), gridBagConstraints);
        textFieldPassword = new JPasswordField(25);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldPassword, gridBagConstraints);
        gridBagConstraints.gridwidth = 1; gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 5;
        formPanel.add(new JLabel("Matricula:"), gridBagConstraints);
        textFieldEnrollment = new JTextField(25);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldEnrollment, gridBagConstraints);
        gridBagConstraints.gridwidth = 1; gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 6;
        formPanel.add(new JLabel("Periodo:"), gridBagConstraints);
        textFieldPeriod = new JTextField(8);
        gridBagConstraints.gridx = 1;
        formPanel.add(textFieldPeriod, gridBagConstraints);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 7;
        formPanel.add(new JLabel("Genero:"), gridBagConstraints);
        RadioButtonMan = new JRadioButton("Hombre");
        radioButtonWoman = new JRadioButton("Mujer");
        ButtonGroup buttonGroupGender = new ButtonGroup();
        buttonGroupGender.add(RadioButtonMan);
        buttonGroupGender.add(radioButtonWoman);
        RadioButtonMan.setBackground(new Color(220, 220, 220));
        radioButtonWoman.setBackground(new Color(220, 220, 220));
        JPanel panelGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelGender.setBackground(new Color(220, 220, 220));
        panelGender.add(RadioButtonMan);
        panelGender.add(radioButtonWoman);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2;
        formPanel.add(panelGender, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 8;
        formPanel.add(new JLabel("Lengua indigena:"), gridBagConstraints);
        radioButtonSpeakIndigenousLanguage = new JRadioButton("Habla");
        radioButtonDontSpeakIndigenousLanguage = new JRadioButton("No habla");
        ButtonGroup buttonGroupLanguage = new ButtonGroup();
        buttonGroupLanguage.add(radioButtonSpeakIndigenousLanguage);
        buttonGroupLanguage.add(radioButtonDontSpeakIndigenousLanguage);
        radioButtonSpeakIndigenousLanguage.setBackground(new Color(220, 220, 220));
        radioButtonDontSpeakIndigenousLanguage.setBackground(new Color(220, 220, 220));
        JPanel panelLanguage = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelLanguage.setBackground(new Color(220, 220, 220));
        panelLanguage.add(radioButtonSpeakIndigenousLanguage);
        panelLanguage.add(radioButtonDontSpeakIndigenousLanguage);
        gridBagConstraints.gridx = 1; gridBagConstraints.gridwidth = 2;
        formPanel.add(panelLanguage, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 9;
        formPanel.add(new JLabel("Estado:"), gridBagConstraints);
        toggleState = new JToggleButton("Inactivo");
        toggleState.setPreferredSize(new Dimension(100, 25));
        toggleState.addActionListener(e ->
                toggleState.setText(toggleState.isSelected() ? "Activo" : "Inactivo")
        );
        gridBagConstraints.gridx = 1;
        formPanel.add(toggleState, gridBagConstraints);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonsPanel.setBackground(new Color(220, 220, 220));
        ButtonsRegisterStudent buttonsRegisterStudent = new ButtonsRegisterStudent(this);
        buttonConfirm = new JButton("Confirmar");
        buttonConfirm.addActionListener(buttonsRegisterStudent);
        buttonCancel = new JButton("Cancelar");
        buttonCancel.addActionListener(buttonsRegisterStudent);

        buttonConfirm.setPreferredSize(new Dimension(110, 30));
        buttonCancel.setPreferredSize(new Dimension(110, 30));
        buttonConfirm.setBackground(new Color(50, 50, 50));
        buttonConfirm.setForeground(Color.WHITE);
        buttonCancel.setBackground(new Color(50, 50, 50));
        buttonCancel.setForeground(Color.WHITE);
        buttonsPanel.add(buttonConfirm);
        buttonsPanel.add(buttonCancel);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 10;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(15, 5, 5, 5);
        formPanel.add(buttonsPanel, gridBagConstraints);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(textFieldNames.getText().trim().isEmpty(),"El campo nombres es obligatorio."),
                Map.entry(textFieldLastName.getText().trim().isEmpty(),"El campo apellidos es obligatorio."),
                Map.entry(textFieldMail.getText().trim().isEmpty(),"El campo correo es obligatorio."),
                Map.entry(textFieldPassword.getPassword().length == 0,"El campo contraseña es obligatorio."),
                Map.entry(textFieldEnrollment.getText().trim().isEmpty(),"El campo matricula es obligatorio."),
                Map.entry(textFieldPeriod.getText().trim().isEmpty(),"El campo periodo es obligatorio."),
                Map.entry(!RadioButtonMan.isSelected() && !radioButtonWoman.isSelected(),
                        "Selecciona un genero."),
                Map.entry(!radioButtonSpeakIndigenousLanguage.isSelected() && !radioButtonDontSpeakIndigenousLanguage.isSelected(),
                        "Selecciona si el alumno habla lengua indigena.")
        );
        for (Map.Entry<Boolean, String> validation : validations) {
            if (validation.getKey()) {
                showError(validation.getValue());
                fieldsValidated = false;
                break;
            }
        }
        return fieldsValidated;
    }

    public boolean validateFieldPassword() {
        boolean passwordsValidated = true;
        String password = textFieldPassword.getText().trim();
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.,]).{8,}$";
        if (!password.matches(regex)) {
            showError("Favor de que su contraseña tenga minimo un caracter especial, una mayuscula, una minuscula, un numero y que sea de 8 digitos");
            passwordsValidated = false;
        }
        return passwordsValidated;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Campo requerido", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIRegisterStudent().setVisible(true));
    }

    public JTextField getTextFieldNames() {
        return textFieldNames;
    }

    public JTextField getTextFieldLastName() {
        return textFieldLastName;
    }

    public JTextField getTextFieldMail() {
        return textFieldMail;
    }

    public JPasswordField getTextFieldPassword() {
        return textFieldPassword;
    }

    public JTextField getTextFieldEnrollment() {
        return textFieldEnrollment;
    }

    public JTextField getTextFieldPeriod() {
        return textFieldPeriod;
    }

    public JRadioButton getRadioButtonMan() {
        return RadioButtonMan;
    }

    public JRadioButton getRadioButtonWoman() {
        return radioButtonWoman;
    }

    public JRadioButton getRadioButtonSpeakIndigenousLanguage() {
        return radioButtonSpeakIndigenousLanguage;
    }

    public JRadioButton getRadioButtonDontSpeakIndigenousLanguage() {
        return radioButtonDontSpeakIndigenousLanguage;
    }

    public JToggleButton getToggleState() {
        return toggleState;
    }

    public JButton getButtonConfirm() {
        return buttonConfirm;
    }

    public JButton getButtonCancel() {
        return buttonCancel;
    }
}
