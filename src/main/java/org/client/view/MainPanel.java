package org.client.view;

import org.entities.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainPanel extends JPanel {
public MainFrame parentFrame;
    JButton sendButton;
    JTextArea textWindow;
    JTextField smallField;
    public MainPanel(MainFrame frame){
        parentFrame = frame;
        sendButton = new JButton("Отправить");
        smallField = new JTextField("", 15);
        textWindow = new JTextArea();
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();

        String name = JOptionPane.showInputDialog(this, "Введите свое имя");
        while (name == null){
            System.out.println(name);
            name = JOptionPane.showInputDialog(this, "Введите свое имя");
        }
        frame.setName(name);
        this.setLayout(layout);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               addText();
            }
        });

        smallField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addText();
            }
        });

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.gridheight = 1;
        constraints.weighty = 0.99f;
        constraints.gridx     = 0;    // нулевая ячейка по горизонтали
        constraints.gridy     = 0;    // нулевая ячейка по горизонтали
        this.add(textWindow, constraints);

        constraints.weighty = 0.01f;
        constraints.gridheight =  1;
        constraints.weightx = 0.7f;
        constraints.gridwidth = 2;    // размер кнопки в две ячейки
        constraints.gridx     = 0;    // нулевая ячейка по горизонтали
        constraints.gridy     = 3;    // нулевая ячейка по горизонтали
        this.add(smallField, constraints);

        constraints.weightx = 0.3f;
        constraints.gridx     = 2;    // нулевая ячейка по горизонтали
        constraints.gridy     = 3;    // нулевая ячейка по горизонтали
        this.add(sendButton, constraints);

        textWindow.setText("");
    }

    public void addText(){
        String str = smallField.getText()+'\n';
        textWindow.append(str);
        parentFrame.write(str);
        smallField.setText("");
    }

    public void updateChat(Message message){
        textWindow.append(message.getSenderNickname() + ":" + message.getText());
    }

}
