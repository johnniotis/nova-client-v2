package dev.nova.setting;

public class NumSetting extends Setting {
    public final double min, max, step;
    private double value;

    public NumSetting(String name, double value, double min, double max, double step) {
        super(name);
        this.value = value; this.min = min; this.max = max; this.step = step;
    }

    public double get() { return value; }

    /** fraction 0..1 along the slider */
    public void setFraction(double f) {
        f = Math.max(0, Math.min(1, f));
        double raw = min + f * (max - min);
        value = Math.max(min, Math.min(max, Math.round(raw / step) * step));
    }

    public double fraction() { return (value - min) / (max - min); }
}
