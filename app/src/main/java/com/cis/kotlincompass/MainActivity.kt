package com.cis.kotlincompass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var manager: SensorManager? = null
    var listener: SensorListener? = null

    var accValue: FloatArray? = null
    var magValue: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        listener = SensorListener()

        startBtn.setOnClickListener {
            val sensorAccel = manager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val sensorMagnet = manager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            manager?.registerListener(listener, sensorAccel, SensorManager.SENSOR_DELAY_UI)
            manager?.registerListener(listener, sensorMagnet, SensorManager.SENSOR_DELAY_UI)
        }

        stopBtn.setOnClickListener {
            manager?.unregisterListener(listener)
        }
    }

    inner class SensorListener : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent?) {
            when (event?.sensor?.type) {
                // 가속도 센서를 통해 얻어진 값은 accValue 에 담고
                Sensor.TYPE_ACCELEROMETER -> {
                    accValue = event.values.clone()
                }

                // 마그네틱 필드를 통해 얻어진 값은 magValue에 담는
                Sensor.TYPE_MAGNETIC_FIELD -> {
                    magValue = event.values.clone()
                }
            }

            if (magValue != null && accValue != null) {
                // 가속도 센서와 마그테닉 필드를 통해 얻어진 값을 행렬 연산하게 되면 방위값을 얻어올 수 있고,
                // 그 값이 radian 으로 나오기 때문에 각도값으로 변경하기 위해 radian2Degree 메소드를 정의하고 사용하였다.
                val r = FloatArray(9)
                val i = FloatArray(9)

                SensorManager.getRotationMatrix(r, i, accValue, magValue)

                val values = FloatArray(3)
                SensorManager.getOrientation(r, values)

                val azimuth = radian2Degree(values[0])
                val pitch = radian2Degree(values[1])
                val roll = radian2Degree(values[2])

                tv.text = "방위값 : ${azimuth}\n"
                tv.append("좌우 기울기 : ${pitch}\n")
                tv.append("앞뒤 기울기 : ${roll}")
            }
        }

    }

    fun radian2Degree(radian : Float) : Float {
        return radian * 180 / Math.PI.toFloat()
    }
}
