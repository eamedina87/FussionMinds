package ec.erickmedina.fussionminds.beats;

/**
 *
 * @author Santiago Fernandez Dapena
 */

public class QRSDetect {
    private int preBlank = SampleRate.getMs195();
    private int filterDelay = ((SampleRate.getDerivLength() / 2)
            + (SampleRate.getLpBufferLgth() / 2 - 1)
            + ((SampleRate.getHpBufferLgth() - 1) / 2) +
            preBlank); // Filter delays plus 195 ms blanking delay.
    private int derDelay = SampleRate.getDerivLength() + filterDelay +
            SampleRate.getDerivLength();

    private int minPeakAmp = 7; // Prevents detections of peaks smaller than 150 uV.
    private double th = .73125; // Threshold coefficient; Valor original .3125
    private int[] ddBuffer; // Buffer holding derivative data.
    private static int ddPtr;

    private static int detThresh;
    private static int qpkcnt = 0;
    private static int[] qrsbuf = new int[8];
    private static int[] noise = new int[8];
    private static int[] rrbuf = new int[8];
    private static int[] rsetBuff = new int[8];
    private static int rsetCount = 0;
    private static int nmean, qmean, rrmean;
    private static int count, sbpeak = 0, sbloc;
    private int sbcount = SampleRate.getMs1500();
    private static int maxder, lastmax;
    private static int initBlank, initMax;
    private static int preBlankCnt, tempPeak;
    private static int lastDatum;
    private static int max = 0, timeSinceMax = 0;
    private QRSFilter qrsFilter;

    /** Creates a new instance of QRSDetect */
    public QRSDetect() {

    }

    public void resetQRSDetec() {
        /* Initialize all buffers to 0 on the first call. */
        for (int i = 0; i < 8; ++i) {
            noise[i] = 0; /* Initialize noise buffer */
            rrbuf[i] = SampleRate.getMs1000();
            /* and R-to-R interval buffer. */
        }
        qpkcnt = 0;
        maxder = lastmax = count = sbpeak = 0;
        initBlank = initMax = preBlankCnt = ddPtr = 0;
        sbcount = SampleRate.getMs1500();

        preBlank = SampleRate.getMs195();
        filterDelay = ((SampleRate.getDerivLength() / 2)
                + (SampleRate.getLpBufferLgth() / 2 - 1)
                + ((SampleRate.getHpBufferLgth() - 1) / 2) +
                preBlank); // Filter delays plus 195 ms blanking delay.
        derDelay = SampleRate.getDerivLength() + filterDelay +
                SampleRate.getDerivLength();

        ddBuffer = new int[derDelay]; // Buffer holding derivative data.
        qrsFilter = new QRSFilter(); /* initialize filters. */
        peak(0, 1); /* initialize peak. */
    }

    /**
     * Realiza la busqueda de los QRS presentes en el ECG
     *
     * @param datum int
     * @param time int
     * @return int
     */
    public int qrsDetect(int datum, int time) {

        int fdatum = 0;
        int qrsDelay = 0;
        int newPeak, aPeak;

        fdatum = qrsFilter.qrsFilter(datum); /* Filter data. */

        /* Wait until normal detector is ready before calling early detections. */
        aPeak = peak(fdatum, 0);

        if (aPeak < minPeakAmp) {
            aPeak = 0;
        }

        // Hold any peak that is detected for 195 ms
        // in case a bigger one comes along.  There
        // can only be one QRS complex in any 195 ms window.

        newPeak = 0;
        if (aPeak != 0 && preBlankCnt == 0) { // If there has been no peak for 195 ms,  save this one and start counting.
            if (SampleRate.getDebug()) {
                //TODO se what does this due and how can we fix it
                //HeartMonitorActivity.addHighligths(time);

                //Signal s = (Signal) JSWBManager.getSignalManager().getSignals().toArray()[0];

            }

            tempPeak = aPeak;
            preBlankCnt = preBlank; // MS195
        }

        else if (aPeak == 0 && preBlankCnt != 0) { // If we have held onto a peak for,  195 ms pass it on for evaluation.
            if (--preBlankCnt == 0) {
                newPeak = tempPeak;
            }
        }

        else if (aPeak != 0) { // If we were holding a peak, but,  this ones bigger, save it and
            if (aPeak > tempPeak) { // start counting to 195 ms again.
                tempPeak = aPeak;
                preBlankCnt = preBlank; // MS195
            } else if (--preBlankCnt == 0) {
                newPeak = tempPeak;
            }
        }

        /* Save derivative of raw signal for T-wave and baseline
           shift discrimination. */

        ddBuffer[ddPtr] = qrsFilter.deriv1(datum, 0);
        if (++ddPtr == derDelay) {
            ddPtr = 0;
        }

        /* Initialize the qrs peak buffer with the first eight 	*/
        /* local maximum peaks detected. */

        if (qpkcnt < 8) {
            ++count;
            if (newPeak > 0) {
                count = SampleRate.getWindowWidth();
            }
            if (++initBlank == SampleRate.getMs1000()) {
                initBlank = 0;
                qrsbuf[qpkcnt] = initMax;
                initMax = 0;
                ++qpkcnt;
                if (qpkcnt == 8) {
                    qmean = mean(qrsbuf, 8);
                    nmean = 0;
                    rrmean = SampleRate.getMs1000();
                    sbcount = SampleRate.getMs1500() + SampleRate.getMs150();
                    detThresh = thresh(qmean, nmean);
                }
            }
            if (newPeak > initMax) {
                initMax = newPeak;
            }
        }

        else
        /* Else test for a qrs. */
        {
            ++count;

            if (newPeak > 0) {
                // TODO find out what is the purpose of this
                /**if (SampleRate.getDebug()) {
                    Signal s = JSWBManager.getSignalManager().getSignal(
                            "ECG-filtrado");
                    if (s != null) {
                        DefaultInstantMark m = new DefaultInstantMark();
                        long tt = (s.getStart() + (time - newPeak) * 10);
                        m.setTitle(tt + "");
                        if (tt == 2682780620L) {
                            System.out.println("");
                        }
                        final long t = s.getStart() + (time - newPeak) * 10;
                        m.setMarkTime(t);
                        s.addMark(m);
                    }
                }**/

                /* Check for maximum derivative and matching minima and maxima
                 for T-wave and baseline shift rejection.  Only consider this
                   peak if it doesn't seem to be a base line shift. */

                if (blsCheck(ddBuffer, ddPtr) == 0) {
                    // Classify the beat as a QRS complex
                    // if the peak is larger than the detection threshold.

                    if (newPeak > detThresh) {
                        //copia las primeras 7 posiciones de qrsbuf a qrsbuf empezando
                        //por la posicion 1
                        qrsbuf = desplazarDerechaArray(qrsbuf);
                        qrsbuf[0] = newPeak;
                        qmean = mean(qrsbuf, 8);
                        detThresh = thresh(qmean, nmean);
                        rrbuf = desplazarDerechaArray(rrbuf);
                        rrbuf[0] = count - SampleRate.getWindowWidth();
                        rrmean = mean(rrbuf, 8);
                        sbcount = rrmean + (rrmean / 2) +
                                SampleRate.getWindowWidth();
                        count = SampleRate.getWindowWidth();

                        sbpeak = 0;

                        lastmax = maxder;
                        maxder = 0;
                        qrsDelay = SampleRate.getWindowWidth() + filterDelay;
                        initBlank = initMax = rsetCount = 0;
                    }

                    // If a peak isn't a QRS update noise buffer and estimate.
                    // Store the peak for possible search back.
                    else {
                        noise = desplazarDerechaArray(noise);
                        noise[0] = newPeak;
                        nmean = mean(noise, 8);
                        detThresh = thresh(qmean, nmean);

                        // Don't include early peaks (which might be T-waves)
                        // in the search back process.  A T-wave can mask
                        // a small following QRS.

                        if ((newPeak > sbpeak) &&
                                ((count - SampleRate.getWindowWidth()) >=
                                        SampleRate.getMs360())) {
                            sbpeak = newPeak;
                            sbloc = count - SampleRate.getWindowWidth();
                        }
                    }
                }
            }

            /* Test for search back condition.  If a QRS is found in  */
            /* search back update the QRS buffer and det_thresh.      */

            if ((count > sbcount) && (sbpeak > (detThresh / 2))) {

                if (SampleRate.getDebug()) {
                    System.out.println("Test for search back condition.");
                }
                qrsbuf = desplazarDerechaArray(qrsbuf);
                qrsbuf[0] = sbpeak;
                qmean = mean(qrsbuf, 8);
                detThresh = thresh(qmean, nmean);
                rrbuf = desplazarDerechaArray(rrbuf);
                rrbuf[0] = sbloc;
                rrmean = mean(rrbuf, 8);
                sbcount = rrmean + (rrmean / 2) + SampleRate.getWindowWidth();
                qrsDelay = count - sbloc;
                count = count - sbloc;
                qrsDelay += filterDelay;
                sbpeak = 0;
                lastmax = maxder;
                maxder = 0;

                initBlank = 0;
                initMax = 0;
                rsetCount = 0;
            }
        }

        // In the background estimate threshold to replace adaptive threshold
        // if eight seconds elapses without a QRS detection.

        if (qpkcnt == 8) {
            if (++initBlank == SampleRate.getMs1000()) {
                initBlank = 0;
                rsetBuff[rsetCount] = initMax;
                initMax = 0;
                ++rsetCount;

                // Reset threshold if it has been 8 seconds without
                // a detection.

                if (rsetCount == 8) {
                    if (SampleRate.getDebug()) {
                        System.out.println("if eight seconds elapses without a QRS detection");
                    }
                    for (int i = 0; i < 8; ++i) {
                        qrsbuf[i] = rsetBuff[i];
                        noise[i] = 0;
                    }
                    qmean = mean(rsetBuff, 8);
                    nmean = 0;
                    rrmean = SampleRate.getMs1000();
                    sbcount = SampleRate.getMs1500() + SampleRate.getMs150();
                    detThresh = thresh(qmean, nmean);
                    initBlank = 0;
                    initMax = 0;
                    rsetCount = 0;
                }
            }
            if (newPeak > initMax) {
                initMax = newPeak;
            }
        }

        return (qrsDelay);
    }

    /**
     * Devuelve la altura de un pico cuando la senhal cae por debajo de la mitad
     * de su maximo o si han pasado mas de 95 ms.
     *
     * @param datum int
     * @param init int
     * @return int
     */
    private int peak(int datum, int init) {

        int pk = 0;

        if (init == 1) {
            max = timeSinceMax = 0;
        }

        if (timeSinceMax > 0) {
            ++timeSinceMax;
        }

        if ((datum > lastDatum) && (datum > max)) {
            max = datum;
            if (max > 2) {
                timeSinceMax = 1;
            }
        }

        //Si el valor de un Maximo cae por debajo de la mitad estamos seguros de que es pico
        else if (datum < (max / 2)) {
            pk = max;
            max = 0;
            timeSinceMax = 0;
        }
        //Si han pasado mas de 95 ms desde el maximo Lo convertimos en pico
        else if (timeSinceMax > SampleRate.getMs95()) {
            pk = max;
            max = 0;
            timeSinceMax = 0;
        }
        lastDatum = datum;

        return (pk);
    }

    /**
     * Realiza la comoprobacion de si ha ocurrido un cambio en la linea
     * base. Busca pendientes positivas y negativas de practicamente la
     * misma magnitud en una ventana de 220 ms.
     *
     * @param dBuf int[]
     * @param dbPtr int
     * @return int
     */
    private int blsCheck(int[] dBuf, int dbPtr) {

        int maxb, min, maxt, mint, t, x;
        maxb = min = 0;
        maxt = mint = 0;

        for (t = 0; t < SampleRate.getMs220(); ++t) {
            x = dBuf[dbPtr];
            if (x > maxb) {
                maxt = t;
                maxb = x;
            } else if (x < min) {
                mint = t;
                min = x;
            }
            if (++dbPtr == derDelay) {
                dbPtr = 0;
            }
        }

        maxder = maxb;
        min = -min;

        /* Possible beat if a maximum and minimum pair are found
           where the interval between them is less than 150 ms. */

        if ((maxb > (min / 8)) && (min > (maxb / 8)) &&
                (Math.abs(maxt - mint) < SampleRate.getMs150())) {
            return (0);
        } else {
            return (0); //@cambio  return (1);
        }
    }

    /**
     * Calcula la media de un array de enteros
     *
     * @param array int[]
     * @param datnum int
     * @return int
     */
    private int mean(int[] array, int datnum) {
        long sum;
        int i;

        for (i = 0, sum = 0; i < datnum; ++i) {
            sum += array[i];
        }
        sum /= datnum;

        return ((int) sum);
    }

    /**
     * Calcula el umbral de deteccion a partir de las medias de los valores
     * de qrs y de ruido
     *
     * @param qmean int
     * @param nmean int
     * @return int
     */
    private int thresh(int qmean, int nmean) {
        int thrsh, dmed;
        double temp;
        dmed = qmean - nmean;
        temp = dmed;
        temp *= th;
        dmed = (int) temp;
        thrsh = nmean + dmed; /* dmed * THRESHOLD */

        return (thrsh);
    }

    /**
     * Realiza un desplazamiento a la derecha de los elementos de un array
     *
     * @param array int[]
     * @return int[]
     */
    private int[] desplazarDerechaArray(int[] array) {
        for (int i = 1; i < 7; i++) {
            array[i] = array[i - 1];
        }
        return (array);
    }

}
