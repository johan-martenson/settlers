package org.appland.settlers.assets;

/**
 * @param red   uint 8
 * @param green uint 8
 * @param blue  uint 8
 */
public record RGBColor(byte red, byte green, byte blue) {

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
