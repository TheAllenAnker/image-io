package com.nostalgia.image_io.process;

import com.nostalgia.image_io.util.BMPImage;
import com.nostalgia.image_io.util.Utils;

public class Compression {
    public static BMPImage predictiveEncoding(BMPImage srcBmp, double coefficient) {
        BMPImage rawBmp = srcBmp.copy();
        if (rawBmp.getBitCount() != 8) {
            throw new IllegalArgumentException("Target is not a 8-bit image.");
        }
        int num = (int) (1 / coefficient);
        int height = rawBmp.getHeight();
        int width = rawBmp.getWidth();

        int temp = 0;
        for (int i = 0; i < height; i++) {
            for (int j = num; j < width; j++) {
                for (int k = 1; k <= num; k++) {
                    temp += Utils.int2Rgb(Utils.getPixelValue(rawBmp, i, j - k)).getR();
                }
                int value = Utils.int2Rgb(Utils.getPixelValue(rawBmp, i, j)).getR();
                Utils.setPixelValue(rawBmp, i, j, value - temp / num);
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int value = Utils.int2Rgb(Utils.getPixelValue(rawBmp, i, j)).getR();
                Utils.setPixelValue(rawBmp, i, j, (byte) ((value + 255) / 2));
            }
        }

        return rawBmp;
    }

    public static BMPImage uniformQuantization(BMPImage srcBmp, int coefficient) {
        BMPImage bmpFile = srcBmp.copy();
        if (bmpFile.getBitCount() != 8 || coefficient < 1 || coefficient > 7) {
            return null;
        }
        int height = bmpFile.getHeight();
        int width = bmpFile.getWidth();
        for (int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                int value = Utils.int2Rgb(Utils.getPixelValue(bmpFile, i, j)).getR();
                Utils.setPixelValue(bmpFile, i, j, (value >> coefficient) << coefficient);
            }
        }
        return bmpFile;
    }
}
