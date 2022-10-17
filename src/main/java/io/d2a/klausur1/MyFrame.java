package io.d2a.klausur1;

import io.d2a.ahpe.AhpeMisc;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {

    public static void main(String[] args) {
        new MyFrame();
    }

    public MyFrame() throws HeadlessException {
        this.setTitle("Hello World!");
        AhpeMisc.visible(this, 10, 10);
    }

}