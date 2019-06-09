package com.nostalgia.image_io.util;

import java.util.Arrays;

public class Utils {
    public static final int toInt(byte byte_0, byte byte_1, byte byte_2, byte byte_3) {
        return (byte_0 & 255) << 24 | (byte_1 & 255) << 16 | (byte_2 & 255) << 8 | byte_3 & 255;
    }

    public static final long toLong(byte byte_0, byte byte_1, byte byte_2, byte byte_3, byte byte_4, byte byte_5, byte byte_6, byte byte_7) {
        return ((long) byte_0 & 255L) << 56 | ((long) byte_1 & 255L) << 48 | ((long) byte_2 & 255L) << 40 | ((long) byte_3 & 255L) << 32 | ((long) byte_4 & 255L) << 24 | ((long) byte_5 & 255L) << 16 | ((long) byte_6 & 255L) << 8 | (long) byte_7 & 255L;
    }

    public static final short toShort(byte byte_0, byte byte_1) {
        return (short) ((byte_0 & 255) << 8 | byte_1 & 255);
    }

    public static final int getIntFromArray(byte[] array, int startIndex) {
        return toInt(array[startIndex + 3], array[startIndex + 2], array[startIndex + 1], array[startIndex]);
    }

    public static final int getIntFromArrayWithoutHigh(byte[] array, int startIndex) {
        return toInt((byte) 0, array[startIndex + 2], array[startIndex + 1], array[startIndex]);
    }

    public static final long getLongFromArray(byte[] array, int startIndex) {
        return toLong((byte) 0, (byte) 0, (byte) 0, (byte) 0, array[startIndex + 3], array[startIndex + 2], array[startIndex + 1], array[startIndex]);
    }

    public static final short getShortFromArray(byte[] array, int startIndex) {
        return toShort(array[startIndex + 1], array[startIndex]);
    }

    public static final byte getByteFromInt(int value, int index) {
        return (byte) ((value & 255 << (index << 3)) >>> (index << 3));
    }

    public static final void putIntToByteArray(byte[] array, int value, int startIndex) {
        array[startIndex] = (byte) (value & 255);
        array[startIndex + 1] = (byte) ((value & 0xFF00) >>> 8);
        array[startIndex + 2] = (byte) ((value & 0xFF0000) >>> 16);
        array[startIndex + 3] = (byte) ((value & 0xFF000000) >>> 24);
    }

    public static final void putIntToByteArrayWithoutHigh(byte[] array, int value, int startIndex) {
        array[startIndex] = (byte) (value & 255);
        array[startIndex + 1] = (byte) ((value & 0xFF00) >>> 8);
        array[startIndex + 2] = (byte) ((value & 0xFF0000) >>> 16);
    }

    public static final void putShortToByteArray(byte[] array, short value, int startIndex) {
        array[startIndex] = (byte) (value & 255);
        array[startIndex + 1] = (byte) ((value & 0xFF00) >>> 8);
    }

    public static final RGBPixel int2Rgb(int value) {
        return new RGBPixel(getByteFromInt(value, 3), getByteFromInt(value, 2), getByteFromInt(value, 1), getByteFromInt(value, 0));
    }

    public static final int rgb2Int(RGBPixel rgbPixel) {
        return toInt(rgbPixel.getA(), rgbPixel.getR(), rgbPixel.getG(), rgbPixel.getB());
    }

    public static final boolean isGray(int value) {
        RGBPixel pixel = int2Rgb(value);
        return pixel.getR() == pixel.getG() || pixel.getG() == pixel.getB() || pixel.getB() == pixel.getG();
    }

    public static final boolean isGray(RGBPixel pixel) {
        return pixel.getR() == pixel.getG() || pixel.getG() == pixel.getB() || pixel.getB() == pixel.getG();
    }

    public static final byte setBit(byte value, int bitIndex) {
        return (byte) (value & 255 | 1 << bitIndex);
    }

    public static final byte[] getPalette(int bitCount) {
        int size = (int) Math.pow(2, bitCount);
        int base = 255 / (size - 1);
        byte[] res = new byte[size * 4];
        for (int i = 0; i < size; i++) {
            res[i * 4] = (byte) (base * i);
            res[i * 4 + 1] = (byte) (base * i);
            res[i * 4 + 2] = (byte) (base * i);
            res[i * 4 + 3] = (byte) 0;
        }
        return res;
    }

    public static final int getPixelValue(BMPImage bmpImage, int row, int col) {
        int bitCount = bmpImage.getBitCount();
        byte[] palette = bmpImage.getRawPalette();
        byte[][] data = bmpImage.getRawData();
        if (bitCount == 1) {
            return getIntFromArray(palette, ((data[row][col / 8] & (0x80 >>> (col % 8))) >>> (7 - (col % 8))) * 4);
        } else if (bitCount == 4) {
            return getIntFromArray(palette, ((data[row][col / 2] & (0xF0 >>> (4 * (col % 2)))) >>> (4 - 4 * (col % 2))) * 4);
        } else if (bitCount == 8) {
            return getIntFromArray(palette, (data[row][col] & 0xFF) * 4);
        } else if (bitCount == 24) {
            return (getIntFromArrayWithoutHigh(data[row], col * 3));
        } else if (bitCount == 32) {
            return (getIntFromArrayWithoutHigh(data[row], col * 4));
        }
        throw new UnsupportedOperationException("Unsupport bitcount : $bitCount");
    }

    public static final void setPixelValue(BMPImage bmpFile, int row, int col, int value) {
        int bitCount = bmpFile.getBitCount();
        byte[][] data = bmpFile.getRawData();
        if (bitCount == 1) {
            if (value != 0) {
                data[row][col / 8] = setBit(data[row][col / 8], col % 8);
            } else {
                data[row][col / 8] = 0;
            }
        } else if (bitCount == 4) {
            data[row][col / 2] = (byte) ((data[row][col / 2] & (0x0F << (col % 2))) | (value & (0xF0 >> (col % 2))));
        } else if (bitCount == 8) {
            data[row][col] = (byte) (value & 0xFF);
        } else if (bitCount == 24) {
            putIntToByteArrayWithoutHigh(data[row], value, col * 3);
        } else if (bitCount == 32) {
            putIntToByteArray(data[row], value, col * 4);
        }
    }

    public static final BMPImage generateRawBmp(BMPImage bmpFile, byte[] palette, byte[][] newData, int bitCount, int width,
                                                int height) {
        int paletteSize = palette != null ? palette.length : 0;
        int newBfSize = 54 + paletteSize + newData.length * newData[0].length;
        int newBfOffsets = 54 + paletteSize;
        int newImageSize = newData.length * newData[0].length;
        byte[] fileHeader = bmpFile.getRawFileHeader();

        byte[] newFileHeader = Arrays.copyOf(fileHeader, fileHeader.length);
        putIntToByteArray(newFileHeader, newBfSize, 2);
        putIntToByteArray(newFileHeader, newBfOffsets, 10);

        byte[] infoHeader = bmpFile.getRawInfoHeader();
        byte[] newInfoHeader = Arrays.copyOf(infoHeader, infoHeader.length);
        putIntToByteArray(newInfoHeader, width, 4);
        putIntToByteArray(newInfoHeader, height, 8);
        putShortToByteArray(newInfoHeader, (short) bitCount, 14);
        putIntToByteArray(newInfoHeader, newImageSize, 20);
        putIntToByteArray(newInfoHeader, (int) Math.pow(2.0D, (double) bitCount), 32);
        return new BMPImage(newFileHeader, newInfoHeader, palette, newData);
    }
}
