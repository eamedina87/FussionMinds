/*
 * Bdac.java
 * Created on 19 de diciembre de 2007, 12:58
 */
package ec.erickmedina.fussionminds.beats;
/**
 *
 * @author Santiago Fernandez Dapena
 */

public class Bdac {
    private int beatSampleRate = 100;
    private double beatMSPerSample = ((double) 1000 / (double) beatSampleRate);
    private int beatMS400 = ((int) (400 / beatMSPerSample + 0.5));
    private int beatMS1000 = beatSampleRate;

    private int beatLgth = beatMS1000;
    private int fidMark = beatMS400;

    private int ecgBufferLength = 1000; // Should be long enough for a beat
    // plus extra space to accommodate
    // the maximum detection delay.
    private int beatQueLength = 10; // Length of que for beats awaiting
    // classification.  Because of
    // detection delays, Multiple beats
    // can occur before there is enough data
    // to classify the first beat in the que.

    private int[] ecgBuffer = new int[ecgBufferLength]; //Circular data buffer
    private int ecgBufferIndex = 0;
    private int[] beatQue = new int[beatQueLength]; //Buffer of detection delays
    private int beatQueCount;
    private int rrCount = 0;
    private int initBeatFlag = 1;

    private QRSDetect qrsDetect;

    /** Creates a new instance of Bdac */
    public Bdac() {
        qrsDetect = new QRSDetect();
    }

    public void resetBdac() {
        qrsDetect.resetQRSDetec(); // Reset the qrs detector
        rrCount = 0;
        initBeatFlag = 1;
        beatQueCount = 0; // Flush the beat que.
    }

    /**
     * Calcula el numero de muestra donde aparece un latido
     *
     * @param ecgSample int
     * @param t int
     * @return int
     */
    public int beatDetect(int ecgSample, int t) {
        int detectDelay;
        int i;
        int rr = 0;
        int fidAdj = 0;

        // Store new sample in the circular buffer.
        ecgBuffer[ecgBufferIndex] = ecgSample;
        if (++ecgBufferIndex == ecgBufferLength) {
            ecgBufferIndex = 0;
        }

        // Increment RRInterval count.
        ++rrCount;

        // Increment detection delays for any beats in the que.
        for (i = 0; i < beatQueCount; ++i) {
            ++beatQue[i];
        }

        // Run the sample through the QRS detector.
        detectDelay = qrsDetect.qrsDetect(ecgSample, t);

        if (detectDelay != 0) {
            beatQue[beatQueCount] = detectDelay;
            ++beatQueCount;
        }

        // Return if no beat is ready for classification.
        if ((beatQue[0] <
                (beatLgth - fidMark) *
                        (SampleRate.getSampleRate() / beatSampleRate))
                || (beatQueCount == 0)) {
            return 0;
        }

        detectDelay = rrCount = beatQue[0];

        // Update the QUE.
        for (i = 0; i < beatQueCount - 1; ++i) {
            beatQue[i] = beatQue[i + 1];
        }
        --beatQueCount;

        // Skip the first beat.
        if (initBeatFlag == 1) {
            initBeatFlag = 0;
            fidAdj = 0;
        } else {
            fidAdj *= SampleRate.getSampleRate() / beatSampleRate;
        }

        // Limit the fiducial mark adjustment in case of problems with
        // beat onset and offset estimation.
        if (fidAdj > SampleRate.getMs80()) {
            fidAdj = SampleRate.getMs80();
        } else if (fidAdj < -SampleRate.getMs80()) {
            fidAdj = -SampleRate.getMs80();
        }

        if (detectDelay == 0) {
            rrCount += rr;
            return 0;
        } else {
            return (detectDelay - fidAdj);
        }
    }

}


