package ec.erickmedina.fussionminds.monitor

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.AsyncTask
import android.util.Log
import com.bitalino.comm.BITalinoDevice
import com.bitalino.comm.BITalinoFrame
import ec.erickmedina.fussionminds.beats.Bdac
import ec.erickmedina.fussionminds.beats.SampleRate
import ec.erickmedina.fussionminds.entities.MonitorResult
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class MonitorPresenterImpl : MonitorContract.MonitorPresenter {

    private var mView : MonitorContract.MonitorView? = null
    private var mTask: TestAsyncTask? = null
    private val MYUUID = UUID
            //.fromString("00001101-0000-1000-8000-00805F9B34FB")
            .randomUUID()
    private var beatSampleCount: Int = 0
    private var RRSamplecount: Int = 0

    fun setView(view:MonitorContract.MonitorView){
        mView = view
    }

    private var mBdac: Bdac? = null

    override fun onCreate() {
        mBdac = Bdac()
    }

    override fun startMonitor() {
        startBeatDetection()
        mTask = TestAsyncTask()
        mTask?.execute()
    }

    override fun stopMonitor() {
        mTask?.cancel(true)
    }

    override fun getMonitorSettings() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



    private fun startBeatDetection() {
        mBdac?.resetBdac()
        SampleRate.setSampleRate(SAMPLE_RATE)
        mBdac?.resetBdac()
        beatSampleCount = 0
    }

    private var delay: Int? = 0

    private fun addSampletoBeatDetection(frame: BITalinoFrame) {
        val f = frame.getAnalog(1)
        ++beatSampleCount
        ++RRSamplecount
        delay = mBdac?.beatDetect(f.toInt(), beatSampleCount.toInt())

        // If a beat was detected, annotate the beat location
        // and type.
        if (delay != 0) {
            val detectionTimeR = beatSampleCount - delay!!
            calculateRRTime()
        }
    }



    private fun calculateRRTime() {
        val tempRR = RRSamplecount.toFloat() / SAMPLE_RATE//sample rate
        //tvRR.setText("RR. " + tempRR + "seg")
        //val xValue = xValueGenerator(timeCounter)
        //val e = Entry(xValue, tempRR)
        //rrValues.add(e)
        //mHMFrame.addRR(e)
        val beats = 60 / tempRR
        mView?.onMonitorResultSuccess(MonitorResult(0, 0, beats.toInt(), "ok"))
        //tvBpm.setText("Bpm: $beats")
        //val e2 = Entry(xValue, beats)
        //bpmValues.add(e2)
        //mHMFrame.addBpm(e2)
        RRSamplecount = 0
        //sendRRBpm(tempRR, beats)

    }

    private val SAMPLE_RATE = 1000

    private inner class TestAsyncTask : AsyncTask<Void, String, Void>() {
        //private val tvLog = findViewById(R.id.log) as TextView
        private var dev: BluetoothDevice? = null
        private var sock: BluetoothSocket? = null
        private val `is`: InputStream? = null
        private val os: OutputStream? = null
        private var bitalino: BITalinoDevice? = null

        private var testInitiated: Boolean = false

        override fun doInBackground(vararg paramses: Void): Void? {
            try {
                //val remoteDevice = "20:16:12:21:98:74"
                val remoteDevice = "20:16:12:21:98:74"

                val btAdapter = BluetoothAdapter.getDefaultAdapter()
                dev = btAdapter.getRemoteDevice(remoteDevice)

                /*
 * Establish Bluetooth connection
 *
 * Because discovery is a heavyweight procedure for the Bluetooth adapter,
 * this method should always be called before attempting to connect to a
 * remote device with connect(). Discovery is not managed by the Activity,
 * but is run as a system service, so an application should always call
 * cancel discovery even if it did not directly request a discovery, just to
 * be sure. If Bluetooth state is not STATE_ON, this API will return false.
 *
 * see
 * http://developer.android.com/reference/android/bluetooth/BluetoothAdapter
 * .html#cancelDiscovery()
 */
                Log.d("erick", "Stopping Bluetooth discovery.")
                btAdapter.cancelDiscovery()

                dev!!.uuids
                sock = dev!!.createRfcommSocketToServiceRecord(MYUUID)
                try {
                    sock!!.connect();

                } catch (e1: IOException) {
                    Log.e("",e1.message)
                    try {
                        Log.e("","trying fallback...");

                        sock = dev!!.javaClass.getMethod("createRfcommSocket", *arrayOf<Class<*>>(Int::class.javaPrimitiveType!!)).invoke(dev, 1) as BluetoothSocket?
                        sock!!.connect()
                        Log.e("", "Connected")
                    } catch (e2: Exception) {
                        Log.e("", "Couldn't establish Bluetooth connection!")
                        return null
                    }
                }

                Log.e("","Connected");
                testInitiated = true

                bitalino = BITalinoDevice(SAMPLE_RATE, intArrayOf(1, 2, 4))
                publishProgress("Connecting to BITalino [$remoteDevice]..")
                bitalino!!.open(sock!!.inputStream, sock!!.outputStream)
                publishProgress("Connected.")

                // get BITalino version
                publishProgress("Version: " + bitalino!!.version())

                // start acquisition on predefined analog channels
                bitalino!!.start()

                // read until task is stopped
                var counter = 0
                while (counter < 100) {
                    val numberOfSamplesToRead = SAMPLE_RATE
                    publishProgress("Reading $numberOfSamplesToRead samples..")
                    val frames = bitalino!!.read(numberOfSamplesToRead)

                    // present data in screen
                    for (frame in frames) {
                        val ecg = frame.getAnalog(1)
                        val eda = frame.getAnalog(2)
                        val acc = frame.getAnalog(4)
                        addSampletoBeatDetection(frame)
                        //Log.d("erick", "ecg:$ecg eda:$eda acc:$acc")
                    }
                    counter++
                }

                // trigger digital outputs
                // int[] digital = { 1, 1, 1, 1 };
                // device.trigger(digital);

            } catch (e: Exception) {
                Log.e("erick", "There was an error.", e)
            }

            return null
        }

        override fun onProgressUpdate(vararg values: String) {
            Log.d("erick", "bitalino response $values")
            //mView?.onMonitorResultSuccess()
        }

        override fun onCancelled() {
            // stop acquisition and close bluetooth connection
            try {
                bitalino!!.stop()
                publishProgress("BITalino is stopped")

                sock!!.close()
                publishProgress("And we're done! :-)")
            } catch (e: Exception) {
                Log.e("erick", "There was an error.", e)
            }

        }

    }
}