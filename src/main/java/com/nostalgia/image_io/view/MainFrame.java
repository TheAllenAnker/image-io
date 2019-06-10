package com.nostalgia.image_io.view;

import com.nostalgia.image_io.process.Compression;
import com.nostalgia.image_io.process.DCTTransformation;
import com.nostalgia.image_io.process.ModeConversion;
import com.nostalgia.image_io.util.BMPImage;
import com.nostalgia.image_io.util.BMPReader;
import com.nostalgia.image_io.process.Histogram;

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
        fileMenu.add(selectFile);

        final JMenuItem g2bConversion = new JMenuItem("Gray to Binary Threshold");
        final JMenuItem g2bDitherConversion = new JMenuItem("Gray to Binary Dither");
        final JMenuItem g2bOrderedDitherConversion = new JMenuItem("Gray to Binary Ordered Dither");
        final JMenuItem colorHSIConversion = new JMenuItem("RGB -- HSI");
        final JMenuItem colorYCbCrConversion = new JMenuItem("Color -- YCbCr");
        modeConversionMenu.add(g2bConversion);
        modeConversionMenu.add(g2bDitherConversion);
        modeConversionMenu.add(g2bOrderedDitherConversion);
        modeConversionMenu.add(colorHSIConversion);
        modeConversionMenu.add(colorYCbCrConversion);

        final JMenuItem histogramEqualization = new JMenuItem("Histogram Equalization for Greyscale Image");
        final JMenuItem rgbHSIEqualization = new JMenuItem("RGB -- HSI Equalization");
        final JMenuItem rgbYCbCrEqualization = new JMenuItem("RGB -- YCbCr Equalization");
        imageEnhancementMenu.add(histogramEqualization);
        imageEnhancementMenu.add(rgbHSIEqualization);
        imageEnhancementMenu.add(rgbYCbCrEqualization);

        final JMenuItem losslessEncoding = new JMenuItem("Lossless Predictive Coding");
        final JMenuItem uniformQuantization = new JMenuItem("Uniform Quantization");
        final JMenuItem DCTTransform = new JMenuItem("DCT Transformation");
        final JMenuItem DCTInverseTransform = new JMenuItem("DCT Inverse Transformation");
        final JMenuItem DCTInverseTransformDrop50p = new JMenuItem("DCT Inverse Transformation Drop 50%");
        imageCompression.add(losslessEncoding);
        imageCompression.add(uniformQuantization);
        imageCompression.add(DCTTransform);
        imageCompression.add(DCTInverseTransform);
        imageCompression.add(DCTInverseTransformDrop50p);

        g2bConversion.setEnabled(false);
        g2bDitherConversion.setEnabled(false);
        g2bOrderedDitherConversion.setEnabled(false);
        colorHSIConversion.setEnabled(false);
        colorYCbCrConversion.setEnabled(false);
        histogramEqualization.setEnabled(false);
        rgbHSIEqualization.setEnabled(false);
        rgbYCbCrEqualization.setEnabled(false);
        losslessEncoding.setEnabled(false);
        uniformQuantization.setEnabled(false);
        DCTTransform.setEnabled(false);
        DCTInverseTransform.setEnabled(false);
        DCTInverseTransformDrop50p.setEnabled(false);

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
                        g2bConversion.setEnabled(isGrey);
                        g2bDitherConversion.setEnabled(isGrey);
                        g2bOrderedDitherConversion.setEnabled(isGrey);
                        colorHSIConversion.setEnabled(!isGrey);
                        colorYCbCrConversion.setEnabled(!isGrey);
                        histogramEqualization.setEnabled(isGrey);
                        rgbHSIEqualization.setEnabled(!isGrey);
                        rgbYCbCrEqualization.setEnabled(!isGrey);
                        losslessEncoding.setEnabled(true);
                        uniformQuantization.setEnabled(true);
                        DCTTransform.setEnabled(true);
                        DCTInverseTransform.setEnabled(true);
                        DCTInverseTransformDrop50p.setEnabled(true);
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

        g2bConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("单阈值法参数设置");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = ModeConversion.g2bThreshold(img,
                                Integer.parseInt(thresholdValue.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "threshold_result")) {
                            String dstImg = savePath + "/threshold_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(thresholdInputLabel);
                tmpFrame.add(thresholdValue);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        g2bDitherConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("Dither 参数设置");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                // dither matrix
                final JTextField ditherMatrixTextField = new JTextField();
                ditherMatrixTextField.setBounds(100, 40, 100, 20);
                JLabel ditherMatrixLabel = new JLabel("输入抖动矩阵的大小:");
                ditherMatrixLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = ModeConversion.g2bDither(img,
                                Integer.parseInt(ditherMatrixTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "dither_result")) {
                            String dstImg = savePath + "/dither_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(ditherMatrixLabel);
                tmpF.add(ditherMatrixTextField);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        g2bOrderedDitherConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("Ordered Dither 参数设置");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                // dither matrix
                final JTextField orderedDitherMatrixTextField = new JTextField();
                orderedDitherMatrixTextField.setBounds(100, 40, 100, 20);
                JLabel ditherMatrixLabel = new JLabel("输入有序抖动矩阵的大小:");
                ditherMatrixLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = ModeConversion.g2bOrderDither(img,
                                Integer.parseInt(orderedDitherMatrixTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "ordered_dither_result")) {
                            String dstImg = savePath + "/ordered_dither_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(ditherMatrixLabel);
                tmpF.add(orderedDitherMatrixTextField);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        colorHSIConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("RGB -- HSI");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = ModeConversion.rgb2Hsi(img, 3);
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "HSI_result")) {
                            String dstImg = savePath + "/HSI_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        colorYCbCrConversion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("RGB -- YCbCr");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = ModeConversion.rgb2YCbCr(img);
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "YCbCr_result")) {
                            String dstImg = savePath + "/YCbCr_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        histogramEqualization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("Histogram Equalization for Grey Image");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = Histogram.histogram(img, 0);
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "histogram_result")) {
                            String dstImg = savePath + "/histogram_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        rgbHSIEqualization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("Histogram Equalization for Color Image HSI");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = Histogram.histogram(img, 1);
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "rgb_hsi_eql_result")) {
                            String dstImg = savePath + "/rgb_hsi_eql_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        rgbYCbCrEqualization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame tmpF = new JFrame("Histogram Equalization for Color Image YCbCr");
                tmpF.setLayout(null);
                tmpF.setBounds(0, 0, 200, 150);

                // path chooser
                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = Histogram.histogram(img, 2);
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "rgb_ycbcr_eql_result")) {
                            String dstImg = savePath + "/rgb_ycbcr_eql_result.bmp";
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

                tmpF.add(pathChooseLabel);
                tmpF.add(choosePathBtn);
                tmpF.add(confirmBtn);

                tmpF.setSize(300, 100);
                tmpF.setLocationRelativeTo(null);
                tmpF.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                tmpF.setVisible(true);
            }
        });

        losslessEncoding.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("Lossless Predictive Encoding");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField predictionCoefficientTextField = new JTextField();
                predictionCoefficientTextField.setBounds(100, 40, 100, 20);
                JLabel predictionCoefficientLabel = new JLabel("请输入预测系数:");
                predictionCoefficientLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = Compression.predictiveEncoding(img,
                                Integer.parseInt(predictionCoefficientTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "predictive_encoding_result")) {
                            String dstImg = savePath + "/predictive_encoding_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(predictionCoefficientLabel);
                tmpFrame.add(predictionCoefficientTextField);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        uniformQuantization.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("Uniform Quantization");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField compressionRateTextField = new JTextField();
                compressionRateTextField.setBounds(100, 40, 100, 20);
                JLabel compressionRateLabel = new JLabel("请输入压缩比:");
                compressionRateLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = Compression.uniformQuantization(img,
                                Integer.parseInt(compressionRateTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "uniform_quantization_result")) {
                            String dstImg = savePath + "/uniform_quantization_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(compressionRateLabel);
                tmpFrame.add(compressionRateTextField);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        DCTTransform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("DCT Transform");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField sectionSizeTextField = new JTextField();
                sectionSizeTextField.setBounds(100, 40, 100, 20);
                JLabel sectionSizeLabel = new JLabel("请输入分块大小:");
                sectionSizeLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = DCTTransformation.DCTTransform(img,
                                Integer.parseInt(sectionSizeTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "DCT_transform_result")) {
                            String dstImg = savePath + "/DCT_transform_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(sectionSizeLabel);
                tmpFrame.add(sectionSizeTextField);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        DCTInverseTransform.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("DCT Transform");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField sectionSizeTextField = new JTextField();
                sectionSizeTextField.setBounds(100, 40, 100, 20);
                JLabel sectionSizeLabel = new JLabel("请输入分块大小:");
                sectionSizeLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = DCTTransformation.inverseDCTTransform(img,
                                Integer.parseInt(sectionSizeTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath, "DCT_inverse_transform_result")) {
                            String dstImg = savePath + "/DCT_inverse_transform_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(sectionSizeLabel);
                tmpFrame.add(sectionSizeTextField);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        DCTInverseTransformDrop50p.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final JFrame tmpFrame = new JFrame("DCT Transform");
                tmpFrame.setLayout(null);
                tmpFrame.setBounds(0, 0, 200, 150);

                JLabel pathChooseLabel = new JLabel("请选择存储地址:");
                pathChooseLabel.setBounds(0, 10, 100, 20);
                JButton choosePathBtn = new JButton("选择路径");
                choosePathBtn.setBounds(100, 10, 100, 20);

                final JTextField sectionSizeTextField = new JTextField();
                sectionSizeTextField.setBounds(100, 40, 100, 20);
                JLabel sectionSizeLabel = new JLabel("请输入分块大小:");
                sectionSizeLabel.setBounds(0, 40, 100, 20);

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
                JButton confirmBtn = new JButton("确认");
                confirmBtn.setBounds(210, 40, 50, 20);

                confirmBtn.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BMPImage img = BMPReader.readBmp(selectedBmpPath);
                        BMPImage newImg = DCTTransformation.inverseDCTTransformDrop50Percent(img,
                                Integer.parseInt(sectionSizeTextField.getText()));
                        Image resultImg = null;
                        if (savePath != null && BMPReader.writeBmp(newImg, savePath,
                                "DCT_inverse_transform_drop50_result")) {
                            String dstImg = savePath + "/DCT_inverse_transform_drop50_result.bmp";
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

                tmpFrame.add(pathChooseLabel);
                tmpFrame.add(choosePathBtn);
                tmpFrame.add(sectionSizeLabel);
                tmpFrame.add(sectionSizeTextField);
                tmpFrame.add(confirmBtn);

                tmpFrame.setSize(300, 100);
                tmpFrame.setLocationRelativeTo(null);
                tmpFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                tmpFrame.setVisible(true);
            }
        });

        setVisible(true);
    }
}
