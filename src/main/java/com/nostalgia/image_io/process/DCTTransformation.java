package com.nostalgia.image_io.process;

import com.nostalgia.image_io.util.BMPImage;
import com.nostalgia.image_io.util.Utils;

public class DCTTransformation {
    public static BMPImage DCTTransform(BMPImage srcBmp, int n) {
        BMPImage bmp = srcBmp.copy();
        int[][] quantization = new int[][]{
                {16, 11, 10, 16, 24, 40, 51, 61},
                {12, 12, 14, 19, 26, 58, 60, 55},
                {14, 13, 16, 24, 40, 57, 69, 56},
                {14, 17, 22, 29, 51, 87, 80, 62},
                {18, 22, 37, 56, 68, 109, 103, 77},
                {24, 35, 55, 64, 81, 104, 113, 92},
                {49, 64, 78, 87, 103, 121, 120, 101},
                {72, 92, 95, 98, 112, 100, 103, 99},
        };
        int[][] img = new int[bmp.getHeight()][bmp.getWidth()];
        int[][] block = new int[n][n];
        double[][] blockG = new double[n][n];
        double w = bmp.getWidth();
        double h = bmp.getHeight();
        double p = 1, q = 1;
        int u = 0, v = 0, x = 0, y = 0, i = 0, j = 0;

        for (i = 0; i < bmp.getHeight(); i++) {
            for (j = 0; j < bmp.getWidth(); j++) {
                img[i][j] = Utils.int2Rgb(Utils.getPixelValue(bmp, i, j)).getR() - 127;
            }
        }

        for (i = 0; i < h / n - 1; i++) {
            for (j = 0; j < w / n - 1; j++) {
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        block[u][v] = 0;
                    }
                }
                for (u = 0; u < n; u++) {
                    System.out.println(" ");
                    for (v = 0; v < n; v++) {
                        blockG[u][v] = 0;
                        for (x = 0; x < n; x++) {
                            for (y = 0; y < n; y++) {
                                double temp = Math.cos((2 * x + 1) * u * Math.PI / (2 * (double) n)) *
                                        Math.cos((2 * y + 1) * v * Math.PI / (2 * (double) n)) *
                                        img[i * n + x][j * n + y];
                                blockG[u][v] += temp;
                            }
                        }
                        if (u == 0) {
                            p = 1;
                        } else if (u > 0) {
                            p = Math.sqrt(2);
                        }
                        if (v == 0) {
                            q = 1;
                        } else if (v > 0) {
                            q = Math.sqrt(2);
                        }
                        double count = ((p * q) / n) * blockG[u][v];
                        block[u][v] = (int) (count / quantization[u][v]);
                        Utils.setPixelValue(bmp, i * n + u, j * n + v, block[u][v]);
                    }
                }
            }
        }

        return bmp;
    }

    public static BMPImage inverseDCTTransform(BMPImage srcBmp, int n) {
        BMPImage bmp = srcBmp.copy();
        int[][] quantization = new int[][]{
                {16, 11, 10, 16, 24, 40, 51, 61},
                {12, 12, 14, 19, 26, 58, 60, 55},
                {14, 13, 16, 24, 40, 57, 69, 56},
                {14, 17, 22, 29, 51, 87, 80, 62},
                {18, 22, 37, 56, 68, 109, 103, 77},
                {24, 35, 55, 64, 81, 104, 113, 92},
                {49, 64, 78, 87, 103, 121, 120, 101},
                {72, 92, 95, 98, 112, 100, 103, 99},
        };
        int[][] img = new int[bmp.getHeight()][bmp.getWidth()];
        double[][] img2 = new double[bmp.getHeight()][bmp.getWidth()];
        int[][] block = new int[n][n];
        double[][] blockG = new double[n][n];
        double w = bmp.getWidth();
        double h = bmp.getHeight();
        double p = 1, q = 1;
        int u = 0, v = 0, x = 0, y = 0, i = 0, j = 0;
        for (i = 0; i < bmp.getHeight(); i++) {
            for (j = 0; j < bmp.getWidth(); j++) {
                img[i][j] = Utils.int2Rgb(Utils.getPixelValue(bmp, i, j)).getR();
            }
        }

        for (i = 0; i < h / n - 1; i++) {
            for (j = 0; j < w / n - 1; j++) {
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        block[u][v] = 0;
                    }
                }
                for (u = 0; u < n; u++) {
                    System.out.println(" ");
                    for (v = 0; v < n; v++) {
                        blockG[u][v] = 0;
                        for (x = 0; x < n; x++) {
                            for (y = 0; y < n; y++) {
                                double temp = Math.cos((2 * x + 1) * u * Math.PI / (2 * (double) n)) *
                                        Math.cos((2 * y + 1) * v * Math.PI / (2 * (double) n)) *
                                        img[i * n + x][j * n + y];
                                blockG[u][v] += temp;
                            }
                        }
                        if (u == 0) {
                            p = 1;
                        } else if (u > 0) {
                            p = Math.sqrt(2);
                        }
                        if (v == 0) {
                            q = 1;
                        } else if (v > 0) {
                            q = Math.sqrt(2);
                        }
                        double count = ((p * q) / n) * blockG[u][v];
                        img2[i * n + u][j * n + v] = count / quantization[u][v];
                    }
                }
            }
        }

        for (i = 0; i < h / n - 1; i++) {
            for (j = 0; j < w / n - 1; j++) {
                for (x = 0; x < n; x++) {
                    for (y = 0; y < n; y++) {
                        blockG[x][y] = 0;
                        for (u = 0; u < n; u++) {
                            for (v = 0; v < n; v++) {
                                if (u == 0) {
                                    p = 1;
                                } else if (u > 0) {
                                    p = Math.sqrt(2);
                                }
                                if (v == 0) {
                                    q = 1;
                                } else if (v > 0) {
                                    q = Math.sqrt(2);
                                }
                                double temp = p * q * Math.cos((2 * x + 1) * u * Math.PI / (2 * (double) n)) *
                                        Math.cos((2 * y + 1) * v * Math.PI / (2 * (double) n)) *
                                        img2[i * n + u][j * n + v] * quantization[u][v];
                                blockG[x][y] += temp;
                            }
                        }
                        block[x][y] = (int) (blockG[x][y] / n);
                    }
                }
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        Utils.setPixelValue(bmp, i * n + u, j * n + v, block[u][v]);
                        block[u][v] = 0;
                    }
                }
            }
        }

        return bmp;
    }

    public static BMPImage inverseDCTTransformDrop50Percent(BMPImage srcBmp, int n) {
        BMPImage bmp = srcBmp.copy();
        int[][] quantization = new int[][]{
                {16, 11, 10, 16, 24, 40, 51, 61},
                {12, 12, 14, 19, 26, 58, 60, 55},
                {14, 13, 16, 24, 40, 57, 69, 56},
                {14, 17, 22, 29, 51, 87, 80, 62},
                {18, 22, 37, 56, 68, 109, 103, 77},
                {24, 35, 55, 64, 81, 104, 113, 92},
                {49, 64, 78, 87, 103, 121, 120, 101},
                {72, 92, 95, 98, 112, 100, 103, 99},
        };
        int[][] img = new int[bmp.getHeight()][bmp.getWidth()];
        double[][] img2 = new double[bmp.getHeight()][bmp.getWidth()];
        int[][] block = new int[n][n];
        double[][] blockG = new double[n][n];
        double w = bmp.getWidth();
        double h = bmp.getHeight();
        double p = 1, q = 1;
        int u = 0, v = 0, x = 0, y = 0, i = 0, j = 0;
        for (i = 0; i < bmp.getHeight(); i++) {
            for (j = 0; j < bmp.getWidth(); j++) {
                img[i][j] = Utils.int2Rgb(Utils.getPixelValue(bmp, i, j)).getR();
            }
        }
        for (i = 0; i < h / n - 1; i++) {
            for (j = 0; j < w / n - 1; j++) {
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        block[u][v] = 0;
                    }
                }
                for (u = 0; u < n; u++) {
                    System.out.println(" ");
                    for (v = 0; v < n; v++) {
                        blockG[u][v] = 0;
                        for (x = 0; x < n; x++) {
                            for (y = 0; y < n; y++) {
                                double temp = Math.cos((2 * x + 1) * u * Math.PI / (2 * (double) n)) *
                                        Math.cos((2 * y + 1) * v * Math.PI / (2 * (double) n)) *
                                        img[i * n + x][j * n + y];
                                blockG[u][v] += temp;
                            }
                        }
                        if (u == 0) {
                            p = 1;
                        } else if (u > 0) {
                            p = Math.sqrt(2);
                        }
                        if (v == 0) {
                            q = 1;
                        } else if (v > 0) {
                            q = Math.sqrt(2);
                        }
                        double count = ((p * q) / n) * blockG[u][v];
                        img2[i * n + u][j * n + v] = count / quantization[u][v];
                    }
                }
            }
        }

        for (i = 0; i < h / n - 1; i++) {
            for (j = 0; j < w / n - 1; j++) {
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        if (u + v > n - 1)
                            img2[i * n + u][j * n + v] = 0.0;
                    }
                }

                for (x = 0; x < n; x++) {
                    for (y = 0; y < n; y++) {
                        blockG[x][y] = 0;
                        for (u = 0; u < n; u++) {
                            for (v = 0; v < n; v++) {
                                if (u == 0) {
                                    p = 1;
                                } else if (u > 0) {
                                    p = Math.sqrt(2);
                                }
                                if (v == 0) {
                                    q = 1;
                                } else if (v > 0) {
                                    q = Math.sqrt(2);
                                }
                                double temp = p * q * Math.cos((2 * x + 1) * u * Math.PI / (2 * (double) n)) *
                                        Math.cos((2 * y + 1) * v * Math.PI / (2 * (double) n)) *
                                        img2[i * n + u][j * n + v] * quantization[u][v];
                                blockG[x][y] += temp;
                            }
                        }
                        block[x][y] = (int) (blockG[x][y] / n);
                    }
                }
                for (u = 0; u < n; u++) {
                    for (v = 0; v < n; v++) {
                        Utils.setPixelValue(bmp, i * n + u, j * n + v, block[u][v]);
                        block[u][v] = 0;
                    }
                }
            }
        }

        return bmp;
    }
}
