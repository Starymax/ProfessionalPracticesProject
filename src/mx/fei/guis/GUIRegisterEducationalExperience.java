package mx.fei.guis;

import mx.fei.logic.guibuttons.ButtonsRegisterEducationalExperience;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
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

public class GUIRegisterEducationalExperience extends JFrame {

    private JTextField textFieldNrc;
    private JTextField textFieldName;
    private JTextField textFieldCareer;
    private JTextField textFieldPeriod;
    private JButton buttonRegister;
    private JButton buttonCancel;

    public GUIRegisterEducationalExperience() {
        initComponents();
    }

    private void initComponents() {
        setTitle("RegistrarExperienciaEducativa");
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

        JLabel labelTitle = new JLabel("Registrar experiencia educativa");
        labelTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(5, 5, 20, 5);
        formPanel.add(labelTitle, gridBagConstraints);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(10, 5, 10, 5);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 1;
        formPanel.add(new JLabel("NRC:"), gridBagConstraints);
        textFieldNrc = new JTextField(20);
        gridBagConstraints.gridx = 1; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldNrc, gridBagConstraints);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 2;
        formPanel.add(new JLabel("Nombre:"), gridBagConstraints);
        textFieldName = new JTextField(20);
        gridBagConstraints.gridx = 1; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldName, gridBagConstraints);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 3;
        formPanel.add(new JLabel("Carrera:"), gridBagConstraints);
        textFieldCareer = new JTextField(20);
        gridBagConstraints.gridx = 1; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldCareer, gridBagConstraints);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 4;
        formPanel.add(new JLabel("Periodo:"), gridBagConstraints);
        textFieldPeriod = new JTextField(20);
        gridBagConstraints.gridx = 1; gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(textFieldPeriod, gridBagConstraints);
        gridBagConstraints.fill = GridBagConstraints.NONE;

        ButtonsRegisterEducationalExperience buttonsHandler = new ButtonsRegisterEducationalExperience(this);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonsPanel.setBackground(new Color(220, 220, 220));
        buttonRegister = new JButton("Registrar");
        buttonCancel = new JButton("Cancelar");
        buttonRegister.setPreferredSize(new Dimension(110, 30));
        buttonCancel.setPreferredSize(new Dimension(110, 30));
        buttonRegister.setBackground(new Color(50, 50, 50));
        buttonRegister.setForeground(Color.WHITE);
        buttonCancel.setBackground(new Color(50, 50, 50));
        buttonCancel.setForeground(Color.WHITE);
        buttonRegister.addActionListener(buttonsHandler);
        buttonCancel.addActionListener(buttonsHandler);
        buttonsPanel.add(buttonRegister);
        buttonsPanel.add(buttonCancel);

        gridBagConstraints.gridx = 0; gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(20, 5, 5, 5);
        formPanel.add(buttonsPanel, gridBagConstraints);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        pack();
        setLocationRelativeTo(null);
    }

    public boolean validateFields() {
        boolean fieldsValidated = true;
        List<Map.Entry<Boolean, String>> validations = List.of(
                Map.entry(textFieldNrc.getText().trim().isEmpty(),"El campo NRC es obligatorio."),
                Map.entry(textFieldName.getText().trim().isEmpty(),"El campo nombre es obligatorio."),
                Map.entry(textFieldCareer.getText().trim().isEmpty(),"El campo carrera es obligatorio."),
                Map.entry(textFieldPeriod.getText().trim().isEmpty(),"El campo periodo es obligatorio.")
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Campo requerido", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUIRegisterEducationalExperience().setVisible(true));
    }

    public JTextField getTextFieldNrc() { return textFieldNrc; }
    public JTextField getTextFieldName() { return textFieldName; }
    public JTextField getTextFieldCareer() { return textFieldCareer; }
    public JTextField getTextFieldPeriod() { return textFieldPeriod; }
    public JButton getButtonRegister() { return buttonRegister; }
    public JButton getButtonCancel() { return buttonCancel; }
}