package com.nostalgia.image_io.process;

import com.nostalgia.image_io.util.*;

import java.util.Arrays;

public class ModeConversion {
    public static final int TARGET_H = 1;
    public static final int TARGET_S = 2;
    public static final int TARGET_I = 3;

    public static BMPImage g2bThreshold(BMPImage BMPImage, int threshold) {
        byte[][] data = BMPImage.getRawData();
        int height = BMPImage.getHeight();
        int width = BMPImage.getWidth();
        int bitCount = BMPImage.getBitCount();
        int rowByteCount = ((width + 7) / 8 + 3) / 4 * 4;
        Object[] result = new byte[height][];
        int i = 0;
        for (int j = result.length; i < j; ++i) {
            byte[] var = new byte[rowByteCount];
            result[i] = var;
        }

        byte[][] newData = (byte[][]) result;
        if (bitCount == 1) {
            Object[] var23 = Arrays.copyOf((Object[]) data, data.length);
            newData = (byte[][]) var23;
        } else {
            int row = 0;

            for (i = height; row < i; ++row) {
                byte[] rowData = newData[row];
                int col = 0;

                for (; col < width; ++col) {
                    int byteIndex = col / 8;
                    int bitIndex = col % 8;
                    RGBPixel pixel = Utils.int2Rgb(Utils.getPixelValue(BMPImage, row, col));
                    if (!Utils.isGray(pixel)) {
                        throw (new IllegalArgumentException("Target is not a gray bmp."));
                    }
                    int value = pixel.getR() & 255;
                    if (value > threshold) {
                        rowData[byteIndex] = Utils.setBit(rowData[byteIndex], bitIndex);
                    }
                }
            }
        }

        return Utils.generateRawBmp(BMPImage, Utils.getPalette(1), newData, 1, BMPImage.getWidth(), BMPImage.getHeight());

    }

    public static BMPImage g2bDither(BMPImage BMPImage, int matrixSize) {
        int[][] matrix = getDitherMatrix(matrixSize);
        int width = BMPImage.getWidth();
        int height = BMPImage.getHeight();
        int bitCount = BMPImage.getBitCount();
        int base = (int) (Math.pow(2, Math.min(bitCount, 8)) / (double) (matrixSize * matrixSize + 1));
        int newWidth = width * matrixSize;
        int newHeight = height * matrixSize;
        int rowByteCount = ((newWidth + 7) / 8 + 3) / 4 * 4;
        Object[] result = new byte[newHeight][];
        int i = 0;

        int rowBase;
        for (rowBase = result.length; i < rowBase; ++i) {
            byte[] var27 = new byte[rowByteCount];
            result[i] = var27;
        }

        byte[][] newData = (byte[][]) result;
        int row = 0;

        for (i = height; row < i; ++row) {
            rowBase = row * matrixSize;
            int col = 0;

            for (; col < width; ++col) {
                RGBPixel pixel = Utils.int2Rgb(Utils.getPixelValue(BMPImage, row, col));
                if (!Utils.isGray(pixel)) {
                    throw new IllegalArgumentException("Target is not a gray bmp.");
                }

                int value = (pixel.getR() & 255) / base;
                int i2 = 0;

                for (; i2 < matrixSize; ++i2) {
                    int s = 0;
                    for (; s < matrixSize; ++s) {
                        int byteIndex = (col * matrixSize + s) / 8;
                        int bitIndex = (col * matrixSize + s) % 8;
                        if (value > matrix[i2][s]) {
                            newData[rowBase + i2][byteIndex] = Utils.setBit(newData[rowBase + i2][byteIndex], bitIndex);
                        }
                    }
                }
            }
        }

        return Utils.generateRawBmp(BMPImage, Utils.getPalette(1), newData, 1, newWidth, newHeight);

    }

    public static BMPImage g2bOrderDither(BMPImage bmpFile, int matrixSize) {
        int[][] matrix = getDitherMatrix(matrixSize);
        int width = bmpFile.getWidth();
        int height = bmpFile.getHeight();
        int bitCount = bmpFile.getBitCount();
        byte[][] var10000 = bmpFile.getRawData();

        byte[][] data = var10000;
        int base = (int) Math.max(Math.pow(2.0D, (double) (Math.min(bitCount, 8) - matrixSize)) - (double) 1, 1.0D);
        int rowByteCount = ((width + 7) / 8 + 3) / 4 * 4;
        Object[] result$iv = new byte[height][];
        int i$iv = 0;

        for (int var13 = result$iv.length; i$iv < var13; ++i$iv) {
            byte[] var22 = new byte[rowByteCount];
            result$iv[i$iv] = var22;
        }

        byte[][] newData = (byte[][]) result$iv;
        if (bitCount == 1) {
            Object[] var25 = Arrays.copyOf((Object[]) data, data.length);
            newData = (byte[][]) var25;
        } else {
            int row = 0;

            for (i$iv = height; row < i$iv; ++row) {
                byte[] rowData = newData[row];
                int col = 0;

                for (; col < width; ++col) {
                    int byteIndex = col / 8;
                    int bitIndex = col % 8;
                    RGBPixel pixel = Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col));
                    if (!Utils.isGray(pixel)) {
                        throw new IllegalArgumentException("Target is not a gray bmp.");
                    }

                    int value = pixel.getR() & 255;
                    if (value > matrix[row % matrixSize][col % matrixSize] * base) {
                        rowData[byteIndex] = Utils.setBit(rowData[byteIndex], bitIndex);
                    }
                }
            }
        }

        return Utils.generateRawBmp(bmpFile, Utils.getPalette(1), newData, 1, bmpFile.getWidth(), bmpFile.getHeight());
    }

    private static int[][] getDitherMatrix(int size) {
        int matrixSize;
        for (matrixSize = 2; matrixSize << 1 <= size; matrixSize <<= 1) {
        }

        Object[] result$iv = new int[matrixSize][];
        int row = 0;

        int var7;
        for (var7 = result$iv.length; row < var7; ++row) {
            int[] var13 = new int[matrixSize];
            result$iv[row] = var13;
        }

        int[][] result = (int[][]) result$iv;
        result[0][0] = 0;
        result[0][1] = 2;
        result[1][0] = 3;
        result[1][1] = 1;

        for (int i = 4; i <= matrixSize; i <<= 1) {
            int halfSize = i / 2;
            row = 0;

            for (var7 = halfSize; row < var7; ++row) {
                int col = 0;

                for (; col < halfSize; ++col) {
                    result[row + halfSize][col + halfSize] = result[row][col] * 4 + 1;
                    result[row + halfSize][col] = result[row][col] * 4 + 3;
                    result[row][col + halfSize] = result[row][col] * 4 + 2;
                    result[row][col] = result[row][col] * 4;
                }
            }
        }

        return result;
    }

    public static BMPImage rgb2Hsi(BMPImage bmpFile, int target) {
        int size = bmpFile.getHeight();
        Object[] result = new byte[size][];
        int i = 0;

        int col;
        for (col = result.length; i < col; ++i) {
            byte[] var = new byte[bmpFile.getWidth()];
            result[i] = var;
        }

        byte[][] newData = (byte[][]) result;
        byte[] newPalette = Utils.getPalette(8);
        int row = 0;

        for (i = bmpFile.getHeight(); row < i; ++row) {
            col = 0;

            for (int var8 = bmpFile.getWidth(); col < var8; ++col) {
                RGBPixel rgbPixel = Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col));
                HSIPixel hsiPixel = rgbPixel.toHSIPixel();
                switch (target) {
                    case 1:
                        hsiPixel.setS(0.0D);
                        hsiPixel.setI(0.0D);
                        break;
                    case 2:
                        hsiPixel.setH(0.0D);
                        hsiPixel.setI(0.0D);
                        break;
                    case 3:
                        hsiPixel.setH(0.0D);
                        hsiPixel.setS(0.0D);
                }

                byte res = hsiPixel.toRGBPixel().getR();
                newData[row][col] = res;
            }
        }

        return Utils.generateRawBmp(bmpFile, newPalette, newData, 8, bmpFile.getWidth(), bmpFile.getHeight());
    }

    public static BMPImage rgb2YCbCr(BMPImage bmpFile) {
        int size = bmpFile.getHeight();
        Object[] result = new byte[size][];
        int i = 0;

        int col;
        for (col = result.length; i < col; ++i) {
            byte[] var = new byte[bmpFile.getWidth()];
            result[i] = var;
        }

        byte[][] newData = (byte[][]) result;
        byte[] newPalette = Utils.getPalette(8);
        int row = 0;

        for (i = bmpFile.getHeight(); row < i; ++row) {
            col = 0;

            for (int var7 = bmpFile.getWidth(); col < var7; ++col) {
                RGBPixel rgbPixel = Utils.int2Rgb(Utils.getPixelValue(bmpFile, row, col));
                YCCPixel yccPixel = rgbPixel.toYCCPixel();
                yccPixel.setCb(128.0D);
                yccPixel.setCr(128.0D);
                byte res = yccPixel.toRGBPixel().getR();
                newData[row][col] = res;
            }
        }

        return Utils.generateRawBmp(bmpFile, newPalette, newData, 8, bmpFile.getWidth(), bmpFile.getHeight());
    }

    private static boolean checkBmp(BMPImage bmpImage) {
        return bmpImage.getRawData() != null && bmpImage.getRawFileHeader() != null && bmpImage.getRawInfoHeader() != null;
    }
}
