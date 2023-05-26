package com.codeclub.burgerking.vc;

import javax.swing.*;


public class main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                vistacontrolador ventana = new vistacontrolador();
                ventana.setVisible(true);
            }
        });
    }
}
