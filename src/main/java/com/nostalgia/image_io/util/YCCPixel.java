package com.nostalgia.image_io.util;

public final class YCCPixel extends Pixel {
    private double Y;
    private double Cb;
    private double Cr;

    public YCCPixel(double Y, double Cb, double Cr) {
        this.Y = Y;
        this.Cb = Cb;
        this.Cr = Cr;
    }

    public final RGBPixel toRGBPixel() {
        int r = (int) (this.Y + 1.402D * (this.Cr - (double) 128));
        int g = (int) (this.Y - 0.34414D * (this.Cb - (double) 128) - 0.71414D * (this.Cr - (double) 128));
        int b = (int) (this.Y + 1.772D * (this.Cb - (double) 128));
        return new RGBPixel((byte) 255, (byte) r, (byte) g, (byte) b);
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getCb() {
        return Cb;
    }

    public void setCb(double cb) {
        Cb = cb;
    }

    public double getCr() {
        return Cr;
    }

    public void setCr(double cr) {
        Cr = cr;
    }
}
