package com.nostalgia.image_io.util;

import java.util.Arrays;

public class BMPImage {
    private byte[] mRawFileHeader;
    private byte[] mRawInfoHeader;
    private byte[] mRawPalette;
    private byte[][] mRawData;
    private int mBitCount;
    private int mWidth;
    private int mHeight;

    public byte[] getRawFileHeader() {
        return mRawFileHeader;
    }

    public void setRawFileHeader(byte[] rawFileHeader) {
        mRawFileHeader = rawFileHeader;
    }

    public byte[] getRawInfoHeader() {
        return mRawInfoHeader;
    }

    public void setRawInfoHeader(byte[] rawInfoHeader) {
        mRawInfoHeader = rawInfoHeader;
    }

    public byte[] getRawPalette() {
        return mRawPalette;
    }

    public void setRawPalette(byte[] rawPalette) {
        mRawPalette = rawPalette;
    }

    public byte[][] getRawData() {
        return mRawData;
    }

    public void setRawData(byte[][] rawData) {
        mRawData = rawData;
    }

    public int getBitCount() {
        return mBitCount;
    }

    public void setBitCount(int bitCount) {
        mBitCount = bitCount;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public BMPImage copy() {
        byte[] fileHeader = Arrays.copyOf(mRawFileHeader, mRawFileHeader.length);
        byte[] infoHeader = Arrays.copyOf(mRawInfoHeader, mRawInfoHeader.length);
        byte[] palette = null;
        if (mRawPalette != null) {
            palette = Arrays.copyOf(mRawPalette, mRawPalette.length);
        }
        byte[][] data = new byte[mRawData.length][mRawData[0].length];
        for (int i = 0; i < mRawData.length; i++) {
            System.arraycopy(mRawData[i], 0, data[i], 0, mRawData[i].length);
        }
        return new BMPImage(fileHeader, infoHeader, palette, data);
    }

    public BMPImage(byte[] fileHeader, byte[] infoHeader, byte[] palette, byte[][] rawData) {
        this.mRawFileHeader = fileHeader;
        this.mRawInfoHeader = infoHeader;
        this.mRawPalette = palette;
        this.mRawData = rawData;
        byte[] infoHeaderData = this.mRawInfoHeader;
        this.mBitCount = Utils.getShortFromArray(infoHeaderData, 14);
        this.mWidth = (int) Utils.getLongFromArray(infoHeaderData, 4);
        this.mHeight = (int) Utils.getLongFromArray(infoHeaderData, 8);
    }
}
