package com.nostalgia.image_io.util;

public class Histogram {
    public static final BMPImage histogram(BMPImage bmpFile, int type) {
        int bitCount = bmpFile.getBitCount();
        int width = bmpFile.getWidth();
        int height = bmpFile.getHeight();
        double totalCount = (double) width * (double) height;
        int k = bitCount != 24 && bitCount != 32 ? (int) Math.pow(2.0D, (double) bitCount) - 1 : 255;
        int[] nk = new int[k + 1];
        int row = 0;

        int var11;
        int col;
        int var13;
        int value;
        for (var11 = height; row < var11; ++row) {
            col = 0;
            for (var13 = width; col < var13; ++col) {
                value = getValue(bmpFile, row, col, type);
                ++nk[value];
            }
        }

        row = 1;
        var11 = k;
        if (row <= k) {
            while (true) {
                nk[row] += nk[row - 1];
                if (row == var11) {
                    break;
                }
                ++row;
            }
        }

        row = 0;
        var11 = k;
        if (row <= k) {
            while (true) {
                nk[row] = (int) ((double) (k * nk[row]) / totalCount);
                if (row == var11) {
                    break;
                }
                ++row;
            }
        }

        row = 0;

        for (var11 = height; row < var11; ++row) {
            col = 0;
            for (var13 = width; col < var13; ++col) {
                value = getValue(bmpFile, row, col, type);
                setValue(bmpFile, row, col, nk[value], type);
            }
        }

        return bmpFile;
    }

    private static final int getValue(BMPImage bmpFile, int row, int col, int type) {
        int bitCount = bmpFile.getBitCount();
        return bitCount != 24 && bitCount != 32 ? Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col)).getR() & 255 : (type == 1 ? (int) Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col)).toHSIPixel().getI() : (int) Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col)).toYCCPixel().getY());
    }

    private static final void setValue(BMPImage bmpFile, int row, int col, int value, int type) {
        int bitCount = bmpFile.getBitCount();
        if (bitCount != 24 && bitCount != 32) {
            Utils.setPixelValue(bmpFile, row, col, value);
        } else if (type == 1) {
            int oldValue = Utils.getPixelValue(bmpFile, row, col);
            RGBPixel oldRgbPixel = Utils.int2Rgb(oldValue);
            HSIPixel oldHsiPixel = oldRgbPixel.toHSIPixel();
            oldHsiPixel.setI((double) value);
            Utils.setPixelValue(bmpFile, row, col, Utils.rgb2Int(oldHsiPixel.toRGBPixel()));
        } else {
            YCCPixel pixel = Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col)).toYCCPixel();
            pixel.setY((double) value);
            Utils.setPixelValue(bmpFile, row, col, Utils.rgb2Int(pixel.toRGBPixel()));
        }

    }
}
