package com.nostalgia.image_io.util;

public final class HSIPixel extends Pixel {
    private double H;
    private double S;
    private double I;

    public HSIPixel(double H, double S, double I) {
        this.H = H;
        this.S = S;
        this.I = I;
    }

    public final RGBPixel toRGBPixel() {
        int r = 0;
        int g = 0;
        int b = 0;
        if (this.H <= (double) 120) {
            b = (int) (this.I * ((double) 1 - this.S));
            r = (int) (this.I * ((double) 1 + this.S * Math.cos(this.H / (double) 180 * Math.PI) / Math.cos(((double) 60 - this.H) / (double) 180 * Math.PI)));
            g = (int) ((double) 3 * this.I - (double) (r + b));
        } else {
            double h;
            if (this.H <= (double) 240) {
                h = this.H - (double) 120;
                r = (int) (this.I * ((double) 1 - this.S));
                g = (int) (this.I * ((double) 1 + this.S * Math.cos(h / (double) 180 * Math.PI) / Math.cos(((double) 60 - h) / (double) 180 * Math.PI)));
                b = (int) ((double) 3 * this.I - (double) (r + g));
            } else {
                h = this.H - (double) 240;
                g = (int) (this.I * ((double) 1 - this.S));
                b = (int) (this.I * ((double) 1 + this.S * Math.cos(h / (double) 180 * Math.PI) / Math.cos(((double) 60 - h) / (double) 180 * Math.PI)));
                r = (int) ((double) 3 * this.I - (double) (g + b));
            }
        }

        return new RGBPixel((byte) 255, (byte) r, (byte) g, (byte) b);
    }

    public double getH() {
        return H;
    }

    public void setH(double h) {
        H = h;
    }

    public double getS() {
        return S;
    }

    public void setS(double s) {
        S = s;
    }

    public double getI() {
        return I;
    }

    public void setI(double i) {
        I = i;
    }
}
