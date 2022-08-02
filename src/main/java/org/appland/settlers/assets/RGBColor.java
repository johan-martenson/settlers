package org.appland.settlers.assets;

public class RGBColor {

    private final byte red;   // uint 8
    private final byte green; // uint 8
    private final byte blue;  // uint 8

    public RGBColor(byte red, byte green, byte blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public byte getBlue() {
        return blue;
    }

    public byte getGreen() {
        return green;
    }

    public byte getRed() {
        return red;
    }

    @Override
    public String toString() {
        return "RGBColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    public boolean matches(byte red, byte green, byte blue) {
        return this.red == red && this.green == green && this.blue == blue;
    }
}
