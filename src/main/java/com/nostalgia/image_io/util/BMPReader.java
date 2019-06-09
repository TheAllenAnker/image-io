package com.nostalgia.image_io.util;

import java.io.*;

public class BMPReader {

    public static BMPImage readBmp(String src) {
        BMPImage result = null;
        BufferedInputStream bis = null;
        try {
            if (!src.isEmpty() && src.endsWith(".bmp")) {
                File file = new File(src);
                if (file.exists()) {
                    bis = new BufferedInputStream(new FileInputStream(file));
                    byte[] fileHeaderData = new byte[14];
                    bis.read(fileHeaderData);
                    byte[] infoHeaderData = new byte[40];
                    bis.read(infoHeaderData);

                    int bitCount = Utils.getShortFromArray(infoHeaderData, 14);
                    int clrUsed = Utils.getIntFromArray(infoHeaderData, 32);
                    int width = (int) Utils.getLongFromArray(infoHeaderData, 4);
                    int height = (int) Utils.getLongFromArray(infoHeaderData, 8);

                    boolean usePalette = (bitCount != 24 && bitCount != 32);

                    byte[] paletteData = null;
                    if (usePalette) {
                        int paletteSize = 0;
                        if (clrUsed == 0) {
                            paletteSize = (int) Math.pow(2, bitCount);
                        } else {
                            paletteSize = clrUsed;
                        }
                        paletteData = new byte[paletteSize << 2];
                        int count = bis.read(paletteData);
                        if (count != paletteData.length) {
                            throw new RuntimeException("Error when read palette.");
                        }
                    }
                    int rowDataSize = (width * bitCount / 8 + 3) / 4 * 4;
                    byte[][] imageData = new byte[height][rowDataSize];
                    for (int row = (height - 1); row >= 0; row--) {
                        int count = bis.read(imageData[row]);
                        if (count != rowDataSize) {
                            throw new RuntimeException("Error when read image data.");
                        }
                    }
                    result = new BMPImage(fileHeaderData, infoHeaderData, paletteData, imageData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static boolean writeBmp(BMPImage bmpImage, String path, String name) {
        boolean result = false;
        BufferedOutputStream bos = null;
        System.out.println(bmpImage);
        try {
            File file = new File(path + "/" + name + ".bmp");
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bmpImage.getRawFileHeader());
            bos.write(bmpImage.getRawInfoHeader());
            if (bmpImage.getRawPalette() != null) {
                bos.write(bmpImage.getRawPalette());
            }
            byte[][] imageData = bmpImage.getRawData();
            for (int row = bmpImage.getHeight() - 1; row >= 0; row--) {
                bos.write(imageData[row]);
            }
            bos.flush();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (Exception e) {

                }
            }
        }
        return result;
    }
}
