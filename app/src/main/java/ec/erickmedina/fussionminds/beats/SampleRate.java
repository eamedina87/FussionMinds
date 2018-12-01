package ec.erickmedina.fussionminds.beats;

/**
 *
 * @author Santiago Fernandez Dapena
 */


public class SampleRate {

    private static int sampleRate = 100; //Sample rate in Hz.100
    private static double msPerSample = (double) 1000 / (double) getSampleRate();

    private static int ms10 = (int) (10 / SampleRate.getMsPerSample() + 0.5);
    private static int ms25 = (int) (25 / SampleRate.getMsPerSample() + 0.5);
    private static int ms30 = (int) (30 / SampleRate.getMsPerSample() + 0.5);
    private static int ms80 = (int) (80 / SampleRate.getMsPerSample() + 0.5);
    private static int ms95 = (int) (95 / SampleRate.getMsPerSample() + 0.5);
    private static int ms100 = (int) (100 / SampleRate.getMsPerSample() + 0.5);
    private static int ms125 = (int) (125 / SampleRate.getMsPerSample() + 0.5);
    private static int ms150 = (int) (150 / SampleRate.getMsPerSample() + 0.5);
    private static int ms160 = (int) (160 / SampleRate.getMsPerSample() + 0.5);
    private static int ms175 = (int) (175 / SampleRate.getMsPerSample() + 0.5);
    private static int ms195 = (int) (195 / SampleRate.getMsPerSample() + 0.5);
    private static int ms200 = (int) (200 / SampleRate.getMsPerSample() + 0.5);
    private static int ms220 = (int) (220 / SampleRate.getMsPerSample() + 0.5);
    private static int ms250 = (int) (250 / SampleRate.getMsPerSample() + 0.5);
    private static int ms300 = (int) (300 / SampleRate.getMsPerSample() + 0.5);
    private static int ms360 = (int) (360 / SampleRate.getMsPerSample() + 0.5);
    private static int ms450 = (int) (450 / SampleRate.getMsPerSample() + 0.5);
    private static int ms1000 = getSampleRate();
    private static int ms1500 = (int) (1500 / SampleRate.getMsPerSample());

    private static int derivLength = ms10;
    private static int lpBufferLgth = (int) (2 * ms25);
    private static int hpBufferLgth = ms125;
    private static int windowWidth = ms80; //Moving window integration width ms80
    private static boolean debug = false;

    public static int getSampleRate() {
        return sampleRate;
    }

    /**
     * Inicializa los valores de SampleRate
     *
     * @param sampleRate int
     */
    public static void setSampleRate(int sampleRate) {
        SampleRate.sampleRate = sampleRate;

        SampleRate.msPerSample = (double) 1000 / (double) getSampleRate();
        SampleRate.ms10 = (int) (10 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms25 = (int) (25 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms30 = (int) (30 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms80 = (int) (80 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms95 = (int) (95 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms100 = (int) (100 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms125 = (int) (125 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms150 = (int) (150 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms160 = (int) (160 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms175 = (int) (175 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms195 = (int) (195 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms200 = (int) (200 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms220 = (int) (220 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms250 = (int) (250 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms300 = (int) (300 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms360 = (int) (360 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms450 = (int) (450 / SampleRate.getMsPerSample() + 0.5);
        SampleRate.ms1000 = getSampleRate();
        SampleRate.ms1500 = (int) (1500 / SampleRate.getMsPerSample());

        SampleRate.derivLength = SampleRate.getMs10();
        SampleRate.lpBufferLgth = (int) (2 * SampleRate.getMs25());
        SampleRate.hpBufferLgth = SampleRate.getMs125();
        SampleRate.windowWidth = SampleRate.getMs80();
    }

    public static double getMsPerSample() {
        return msPerSample;
    }

    public static int getMs10() {
        return ms10;
    }

    public static int getMs25() {
        return ms25;
    }

    public static int getMs80() {
        return ms80;
    }

    public static int getMs125() {
        return ms125;
    }

    public static int getMs30() {
        return ms30;
    }

    public static int getMs95() {
        return ms95;
    }

    public static int getMs100() {
        return ms100;
    }

    public static int getMs150() {
        return ms150;
    }

    public static int getMs160() {
        return ms160;
    }

    public static int getMs175() {
        return ms175;
    }

    public static int getMs195() {
        return ms195;
    }

    public static int getMs200() {
        return ms200;
    }

    public static int getMs220() {
        return ms220;
    }

    public static int getMs250() {
        return ms250;
    }

    public static int getMs300() {
        return ms300;
    }

    public static int getMs360() {
        return ms360;
    }

    public static int getMs450() {
        return ms450;
    }

    public static int getMs1000() {
        return ms1000;
    }

    public static int getMs1500() {
        return ms1500;
    }

    public static int getDerivLength() {
        return derivLength;
    }

    public static int getLpBufferLgth() {
        return lpBufferLgth;
    }

    public static int getHpBufferLgth() {
        return hpBufferLgth;
    }

    public static int getWindowWidth() {
        return windowWidth;
    }

    public static boolean getDebug() {
        return debug;
    }
}
