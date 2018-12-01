/*
 * QRSFilter.java
 * Created on 18 de diciembre de 2007, 11:03
 */
package ec.erickmedina.fussionminds.beats;

/**
 *
 * @author Santiago Fernandez Dapena
 */

public class QRSFilter {
    private static int lpY1, lpY2;
    private static int[] lpData;
    private static int lpPtr;
    private static long hpY = 0;
    private static int[] hpData;
    private static int hpPtr = 0;

    private static int deriv1DerI = 0;

    private static int deriv2DerI = 0;
    private static int mvWintSum = 0;
    private static int[] mvWintData;
    private static int mvWintPtr = 0;
    private static int[] deriv1DerBuff;
    private static int[] deriv2DerBuff;

    /** Creates a new instance of QRSFilter */
    public QRSFilter() {
        deriv1DerBuff = new int[SampleRate.getWindowWidth()];
        deriv2DerBuff = new int[SampleRate.getDerivLength()];
        hpFilt(0, 1);
        lpFilt(0, 1);
        mvWint(0, 1);
        deriv1(0, 1);
        deriv2(0, 1);
    }

    /**
     * Toma las muestras de un ECG como entrada y devuelve la muestra de una senhal
     * que es una estimacion de la energia en el ancho de banda de un QRS. Es decir,
     * la senhal tiene una protuberancia cuando ocurre un QRS.
     *
     * @param datum
     * @return int
     */
    public int qrsFilter(int datum) {
        int fdatum;

        fdatum = lpFilt(datum, 0); // Low pass filter data.
        fdatum = hpFilt(fdatum, 0); // High pass filter data.
        fdatum = deriv2(fdatum, 0); // Take the derivative.
        fdatum = Math.abs(fdatum); // Take the absolute value.
        fdatum = mvWint(fdatum, 0); // Average over an 80 ms window .
        return (fdatum);
    }

    /**
     * Realiza un filtro paso alto segun la siguiente ecuacion:
     * y[n] = y[n-1] + x[n] - x[n-128 ms]
     * z[n] = x[n-64 ms] - y[n]
     *
     * @param datum int
     * @param init int
     * @return int
     */
    public int hpFilt(int datum, int init) {

        int z;
        int halfPtr;

        if (init != 0) {
            hpData = new int[SampleRate.getHpBufferLgth()];
            for (hpPtr = 0; hpPtr < SampleRate.getHpBufferLgth(); ++hpPtr) {
                hpData[hpPtr] = 0;
            }
            hpPtr = 0;
            hpY = 0;
            return 0;
        }

        hpY += datum - hpData[hpPtr];
        halfPtr = hpPtr - (SampleRate.getHpBufferLgth() / 2);
        if (halfPtr < 0) {
            halfPtr += SampleRate.getHpBufferLgth();
        }
        z = (hpData[halfPtr] - ((int) hpY / SampleRate.getHpBufferLgth()));

        hpData[hpPtr] = datum;
        if (++hpPtr == SampleRate.getHpBufferLgth()) {
            hpPtr = 0;
        }

        return (z);
    }

    /**
     * Realiza un filtro paso bajo segun la ecuacion:
     * y[n] = 2*y[n-1] - y[n-2] + x[n] - 2*x[t-24 ms] + x[t-48 ms]
     *
     * @param datum int
     * @param init int
     * @return int
     */
    public int lpFilt(int datum, int init) {

        int y0;
        int output;
        int halfPtr;

        if (init != 0) {
            lpData = new int[SampleRate.getLpBufferLgth()];
            for (lpPtr = 0; lpPtr < SampleRate.getLpBufferLgth(); ++lpPtr) {
                lpData[lpPtr] = 0;
            }
            lpY1 = lpY2 = 0;
            lpPtr = 0;
            return 0;
        }
        halfPtr = lpPtr - (SampleRate.getLpBufferLgth() / 2); // Use halfPtr to index
        if (halfPtr < 0) {
            halfPtr += SampleRate.getLpBufferLgth();
        }

        y0 = ((lpY1 * 2) - lpY2 + datum - (lpData[halfPtr] * 2) + lpData[lpPtr]);
        lpY2 = lpY1;
        lpY1 = y0;
        output = y0 /
                ((SampleRate.getLpBufferLgth() * SampleRate.getLpBufferLgth()) /
                        4);
        lpData[lpPtr] = datum; // Stick most recent sample into
        if (++lpPtr == SampleRate.getLpBufferLgth()) { // the circular buffer and update
            lpPtr = 0; // the buffer pointer.
        }
        return (output);
    }

    /**
     * Realiza la media de los valores de la senhal en las ultimas WINDOW_WIDTH muestras
     *
     * @param datum int
     * @param init int
     * @return int
     */
    public int mvWint(int datum, int init) {

        int output;
        if (init != 0) {
            mvWintData = new int[SampleRate.getWindowWidth()];
            for (mvWintPtr = 0; mvWintPtr < SampleRate.getWindowWidth();
                 ++mvWintPtr) {
                mvWintData[mvWintPtr] = 0;
            }
            mvWintSum = 0;
            mvWintPtr = 0;
            return 0;
        }
        mvWintSum += datum;
        mvWintSum -= mvWintData[mvWintPtr];
        mvWintData[mvWintPtr] = datum;
        if (++mvWintPtr == SampleRate.getWindowWidth()) {
            mvWintPtr = 0;
        }
        if ((mvWintSum / SampleRate.getWindowWidth()) > 32000) {
            output = 32000;
        } else {
            output = (mvWintSum / SampleRate.getWindowWidth());
        }
        return (output);
    }

    /**
     * Realiza una derivada segun la ecuacion:
     * y[n] = x[n] - x[n - 10ms]
     *
     * @param x int
     * @param init int
     * @return int
     */
    public int deriv1(int x, int init) {

        int y;

        if (init != 0) {
            for (deriv1DerI = 0; deriv1DerI < SampleRate.getDerivLength();
                 ++deriv1DerI) {
                deriv1DerBuff[deriv1DerI] = 0;
            }
            deriv1DerI = 0;
            return (0);
        }

        y = (int) (x - deriv1DerBuff[deriv1DerI]);
        deriv1DerBuff[deriv1DerI] = x;
        if (++deriv1DerI == SampleRate.getDerivLength()) {
            deriv1DerI = 0;
        }
        return (y);
    }

    /**
     * Realiza una derivada segun la ecuacion:
     * y[n] = x[n] - x[n - 10ms]
     *
     * @param x int
     * @param init int
     * @return int
     */
    public int deriv2(int x, int init) {

        int y;

        if (init != 0) {
            for (deriv2DerI = 0; deriv2DerI < SampleRate.getDerivLength();
                 ++deriv2DerI) {
                deriv2DerBuff[deriv2DerI] = 0;
            }
            deriv2DerI = 0;
            return (0);
        }

        y = (x - deriv2DerBuff[deriv2DerI]);
        deriv2DerBuff[deriv2DerI] = x;
        if (++deriv2DerI == SampleRate.getDerivLength()) {
            deriv2DerI = 0;
        }
        return (y);
    }
}
