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
        JMenu modeConversionMenu = new JMenu("Conversion");
        JMenu imageEnhancementMenu = new JMenu("Enhancement");
        JMenu imageCompression = new JMenu("Compression");
        setJMenuBar(menuBar);

        JMenuItem selectFile = new JMenuItem("Select a BMP File");
        JMenuItem saveFile = new JMenuItem("Save the Processed Image As...");
        fileMenu.add(selectFile);
        fileMenu.add(saveFile);

        JMenuItem g2bConversion = new JMenuItem("Gray to Binary");
        JMenuItem color2gConversion = new JMenuItem("Color to Gray");
        modeConversionMenu.add(g2bConversion);
        modeConversionMenu.add(color2gConversion);

        JMenuItem histogramEqualization = new JMenuItem("Histogram Equalization");
        imageEnhancementMenu.add(histogramEqualization);

        JMenuItem losslessCoding = new JMenuItem("Lossless Predictive Coding");
        JMenuItem uniformQuantization = new JMenuItem("Uniform Quantization");
        JMenuItem DCTTransform = new JMenuItem("DCT Transformation");
        JMenuItem DCTInverseTransform = new JMenuItem("DCT Inverse Transformation");
        imageCompression.add(losslessCoding);
        imageCompression.add(uniformQuantization);
        imageCompression.add(DCTTransform);
        imageCompression.add(DCTInverseTransform);

        menuBar.add(fileMenu);
        menuBar.add(modeConversionMenu);
        menuBar.add(imageEnhancementMenu);
        menuBar.add(imageCompression);
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

        saveFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        g2bConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        color2gConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
