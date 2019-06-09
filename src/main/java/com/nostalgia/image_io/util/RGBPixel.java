package com.nostalgia.image_io.util;

public final class RGBPixel extends Pixel {
    private byte A;
    private byte R;
    private byte G;
    private byte B;

    public RGBPixel(byte A, byte R, byte G, byte B) {
        this.A = A;
        this.R = R;
        this.G = G;
        this.B = B;
    }

    public final HSIPixel toHSIPixel() {
        int r = this.R & 255;
        int g = this.G & 255;
        int b = this.B & 255;
        double theta;
        if (r == b && b == g) {
            theta = 0.0D;
        } else {
            double temp = (double) (r - b + (r - g)) / 2.0D / Math.sqrt(0.0D + (double) ((r - g) * (r - g)) + (double) ((r - b) * (g - b)));
            theta = Math.acos(temp) / Math.PI * (double) 180;
        }

        double h = b <= g ? theta : (double) 360 - theta;
        double i = (double) (r + g + b) / 3.0D;
        double temp = r + g + b == 0 ? 1.0E-4D : (double) (r + g + b);
        double s = (double) 1 - 3.0D * (double) Math.min(Math.min(r, g), b) / temp;
        return new HSIPixel(h, s, i);
    }

    public final YCCPixel toYCCPixel() {
        int r = this.R & 255;
        int g = this.G & 255;
        int b = this.B & 255;
        double y = 0.299D * (double) r + 0.57D * (double) g + 0.114D * (double) b;
        double cb = 0.5D * (double) b - 0.3313D * (double) g - 0.1687D * (double) r + (double) 128;
        double cr = 0.5D * (double) r - 0.4187D * (double) g - 0.0813D * (double) b + (double) 128;
        return new YCCPixel(y, cb, cr);
    }

    public byte getA() {
        return A;
    }

    public void setA(byte a) {
        A = a;
    }

    public byte getR() {
        return R;
    }

    public void setR(byte r) {
        R = r;
    }

    public byte getG() {
        return G;
    }

    public void setG(byte g) {
        G = g;
    }

    public byte getB() {
        return B;
    }

    public void setB(byte b) {
        B = b;
    }
}