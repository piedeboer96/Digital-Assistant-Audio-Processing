package org.monitor;

public class SoundPressureLevel {

    /**
     * Returns the dBSPL for a buffer.
     */
    public double soundPressureLevel(final float[] buffer) {
        double power = 0.0;

        for (float element : buffer) {
            power += element * element;
        }

        double value = Math.sqrt(power) / buffer.length;
        return 20.0 * Math.log10(value);
    }


}
