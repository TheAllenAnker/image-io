package com.nostalgia.image_io.process;

import com.nostalgia.image_io.util.BMPImage;
import com.nostalgia.image_io.util.RGBPixel;
import com.nostalgia.image_io.util.Utils;

import java.util.Arrays;

public class ModeConversion {
    public static BMPImage g2bThreshold(BMPImage BMPImage, int threshold) {
        if (!checkBmp(BMPImage)) {
            return null;
        } else {
            byte[][] data = BMPImage.getRawData();
            int height = BMPImage.getHeight();
            int width = BMPImage.getWidth();
            int bitCount = BMPImage.getBitCount();
            int rowByteCount = ((width + 7) / 8 + 3) / 4 * 4;
            Object[] result$iv = new byte[height][];
            int i$iv = 0;

            for (int var11 = result$iv.length; i$iv < var11; ++i$iv) {
                byte[] var20 = new byte[rowByteCount];
                result$iv[i$iv] = var20;
            }

            byte[][] newData = (byte[][]) result$iv;
            if (bitCount == 1) {
                Object[] var23 = Arrays.copyOf((Object[]) data, data.length);
                newData = (byte[][]) var23;
            } else {
                int row = 0;

                for (i$iv = height; row < i$iv; ++row) {
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
    }

    public static BMPImage g2bDither(BMPImage BMPImage, int matrixSize) {
        if (!checkBmp(BMPImage)) {
            return null;
        } else {
            int[][] matrix = getDitherMatrix(matrixSize);
            int width = BMPImage.getWidth();
            int height = BMPImage.getHeight();
            int bitCount = BMPImage.getBitCount();
            int base = (int) (Math.pow(2, Math.min(bitCount, 8)) / (double) (matrixSize * matrixSize + 1));
            int newWidth = width * matrixSize;
            int newHeight = height * matrixSize;
            int rowByteCount = ((newWidth + 7) / 8 + 3) / 4 * 4;
            Object[] result$iv = new byte[newHeight][];
            int i$iv = 0;

            int rowBase;
            for (rowBase = result$iv.length; i$iv < rowBase; ++i$iv) {
                byte[] var27 = new byte[rowByteCount];
                result$iv[i$iv] = var27;
            }

            byte[][] newData = (byte[][]) result$iv;
            int row = 0;

            for (i$iv = height; row < i$iv; ++row) {
                rowBase = row * matrixSize;
                int col = 0;

                for (; col < width; ++col) {
                    RGBPixel pixel = Utils.int2Rgb(Utils.getPixelValue(BMPImage, row, col));
                    if (!Utils.isGray(pixel)) {
                        throw new IllegalArgumentException("Target is not a gray bmp.");
                    }

                    int value = (pixel.getR() & 255) / base;
                    int i = 0;

                    for (; i < matrixSize; ++i) {
                        int s = 0;
                        for (; s < matrixSize; ++s) {
                            int byteIndex = (col * matrixSize + s) / 8;
                            int bitIndex = (col * matrixSize + s) % 8;
                            if (value > matrix[i][s]) {
                                newData[rowBase + i][byteIndex] = Utils.setBit(newData[rowBase + i][byteIndex], bitIndex);
                            }
                        }
                    }
                }
            }

            return Utils.generateRawBmp(BMPImage, Utils.getPalette(1), newData, 1, newWidth, newHeight);
        }
    }

    public static BMPImage g2bOrderDither(BMPImage bmpFile, int matrixSize) {
        if (!checkBmp(bmpFile)) {
            return null;
        } else {
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

                    for (int var15 = width; col < var15; ++col) {
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
    }

    private static final int[][] getDitherMatrix(int size) {
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

                for (int var9 = halfSize; col < var9; ++col) {
                    result[row + halfSize][col + halfSize] = result[row][col] * 4 + 1;
                    result[row + halfSize][col] = result[row][col] * 4 + 3;
                    result[row][col + halfSize] = result[row][col] * 4 + 2;
                    result[row][col] = result[row][col] * 4 + 0;
                }
            }
        }

        return result;
    }

    private static boolean checkBmp(BMPImage bmpImage) {
        return bmpImage.getRawData() != null && bmpImage.getRawFileHeader() != null && bmpImage.getRawInfoHeader() != null;
    }
}
