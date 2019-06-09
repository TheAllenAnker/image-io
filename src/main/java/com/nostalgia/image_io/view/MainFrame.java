package com.nostalgia.image_io.view;

import com.nostalgia.image_io.process.ModeConversion;
import com.nostalgia.image_io.util.BMPImage;
import com.nostalgia.image_io.util.BMPReader;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

public class MainFrame extends JFrame {
    private JFileChooser fileChooser;
    private JLabel originalImageLabel;
    private JLabel processedImageLabel;
    private Image selectedImage;
    private String selectedBmpPath;
    private String savePath;
    private boolean isGrey;

    public MainFrame() {
        super("Image IO");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBounds(300, 200, 1600, 1200);
        isGrey = false;

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

        final JMenuItem selectFile = new JMenuItem("Select a BMP File");
        final JMenuItem saveFile = new JMenuItem("Save the Processed Image As...");
        fileMenu.add(selectFile);
        fileMenu.add(saveFile);

        final JMenuItem g2bConversion = new JMenuItem("Gray to Binary");
        final JMenuItem color2gConversion = new JMenuItem("Color to Gray");
        modeConversionMenu.add(g2bConversion);
        modeConversionMenu.add(color2gConversion);

        final JMenuItem histogramEqualization = new JMenuItem("Histogram Equalization");
        imageEnhancementMenu.add(histogramEqualization);

        final JMenuItem losslessCoding = new JMenuItem("Lossless Predictive Coding");
        final JMenuItem uniformQuantization = new JMenuItem("Uniform Quantization");
        final JMenuItem DCTTransform = new JMenuItem("DCT Transformation");
        final JMenuItem DCTInverseTransform = new JMenuItem("DCT Inverse Transformation");
        imageCompression.add(losslessCoding);
        imageCompression.add(uniformQuantization);
        imageCompression.add(DCTTransform);
        imageCompression.add(DCTInverseTransform);

        saveFile.setEnabled(false);
        g2bConversion.setEnabled(false);
        color2gConversion.setEnabled(false);
        histogramEqualization.setEnabled(false);
        losslessCoding.setEnabled(false);
        uniformQuantization.setEnabled(false);
        DCTTransform.setEnabled(false);
        DCTInverseTransform.setEnabled(false);

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
                    selectedBmpPath = fileChooser.getSelectedFile().getPath();
                    File imageFile = new File(selectedBmpPath);
                    try {
                        selectedImage = ImageIO.read(imageFile);
                        // determine if the selected image is a greyscale image of not
                        Raster raster = ImageIO.read(imageFile).getRaster();
                        isGrey = raster.getNumDataElements() == 1;
                        saveFile.setEnabled(true);
                        g2bConversion.setEnabled(isGrey);
                        color2gConversion.setEnabled(!isGrey);
                        histogramEqualization.setEnabled(true);
                        losslessCoding.setEnabled(true);
                        uniformQuantization.setEnabled(true);
                        DCTTransform.setEnabled(true);
                        DCTInverseTransform.setEnabled(true);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    ImageIcon imageIcon = null;
                    if (selectedImage != null) {
                        imageIcon = new ImageIcon(selectedImage);
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
                final JFrame tmpFrame = new JFrame("单阈值法参数选项");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                final JTextField textField = new JTextField();
                textField.setBounds(100, 10, 100, 20);
                JLabel saveLabel = new JLabel("请选择存储地址:");
                saveLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField thresholdValue = new JTextField();
                thresholdValue.setBounds(100, 40, 100, 20);
                JLabel thresholdInputLabel = new JLabel("请输入阈值参数:");
                thresholdInputLabel.setBounds(0, 40, 100, 20);

                choosePathBtn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // choose a local path to output the processed image
                        JFileChooser pathChooser = new JFileChooser();
                        pathChooser.setCurrentDirectory(new File("."));
                        pathChooser.setDialogTitle("请选择图片存储路径");
                        pathChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        pathChooser.setAcceptAllFileFilterUsed(false);
                        if (pathChooser.showOpenDialog(pathChooser) == JFileChooser.APPROVE_OPTION) {
                            savePath = pathChooser.getCurrentDirectory().getPath();
                        }
                    }
                });

                // confirm button
                JButton btn = new JButton("确认");
                btn.setBounds(210, 40, 50, 20);

                btn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        String matrixSizeText = thresholdValue.getText();
                        BMPImage newImg = ModeConversion.g2bThreshold(img, Integer.parseInt(matrixSizeText));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "result")) {
                            String dstImg = savePath + "/result.bmp";
                            File file = new File(dstImg);
                            try {
                                resultImg = ImageIO.read(file);
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        ImageIcon icon = null;
                        if (resultImg != null) {
                            icon = new ImageIcon(resultImg);
                        }
                        processedImageLabel.setIcon(icon);
                    }
                });

                tmpFrame.add(saveLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(thresholdInputLabel);
                tmpFrame.add(thresholdValue);
                tmpFrame.add(btn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
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
