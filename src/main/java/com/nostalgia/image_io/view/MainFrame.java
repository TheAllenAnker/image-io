package com.nostalgia.image_io.view;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private JFileChooser fileChooser;
    private JLabel originalImageLabel;
    private JLabel processedImageLabel;

    public MainFrame() {
        super("Image IO");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBounds(300, 200, 1600, 1200);


        JPanel menuPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        FileNameExtensionFilter fileNameFilter = new FileNameExtensionFilter("BMP Images", "bmp");
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(fileNameFilter);
        originalImageLabel = new JLabel();
        processedImageLabel = new JLabel();

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        setJMenuBar(menuBar);

        JMenuItem selectFile = new JMenuItem("Select BMP File");
        JMenuItem saveFile = new JMenuItem("Save As...");
        fileMenu.add(selectFile);
        fileMenu.add(saveFile);

        menuBar.add(fileMenu);
        menuPanel.add(menuBar);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(originalImageLabel, BorderLayout.WEST);
        centerPanel.add(processedImageLabel, BorderLayout.EAST);
        add(menuPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        selectFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String bmpPath = fileChooser.getSelectedFile().getPath();
                    File imageFile = new File(bmpPath);
                    Image bmpImage = null;
                    try {
                        bmpImage = ImageIO.read(imageFile);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ImageIcon imageIcon = null;
                    if (bmpImage != null) {
                        imageIcon = new ImageIcon(bmpImage);
                    }
                    originalImageLabel.setIcon(imageIcon);
                    processedImageLabel.setIcon(imageIcon);
                }
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
