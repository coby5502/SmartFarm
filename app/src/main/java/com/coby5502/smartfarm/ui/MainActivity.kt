package com.coby5502.smartfarm.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.coby5502.smartfarm.R
import com.coby5502.smartfarm.databinding.ActivityMainBinding
import com.coby5502.smartfarm.util.*
import com.coby5502.smartfarm.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModel<MainViewModel>()

    var mBluetoothAdapter: BluetoothAdapter? = null
    var recv: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, REQUEST_ALL_PERMISSION)
        }

        initObserving()

        var btn_led = findViewById<ImageButton>(R.id.btn_led)
        var btn_led_check = 0

        btn_led.setOnClickListener {
            if(btn_led_check == 0) {
                btn_led.setImageResource(R.drawable.led_off)
                viewModel.onClickSendData("led on")
                btn_led_check = 1
            } else {
                btn_led.setImageResource(R.drawable.led_on)
                viewModel.onClickSendData("led off")
                btn_led_check = 0
            }
        }

        var btn_water = findViewById<ImageButton>(R.id.btn_water)
        var btn_water_check = 0

        btn_water.setOnClickListener {
            if(btn_water_check == 0) {
                btn_water.setImageResource(R.drawable.water_off)
                viewModel.onClickSendData("water on")
                btn_water_check = 1
            } else {
                btn_water.setImageResource(R.drawable.water_on)
                viewModel.onClickSendData("water off")
                btn_water_check = 0
            }
        }

        var btn_fan = findViewById<ImageButton>(R.id.btn_fan)
        var btn_fan_check = 0

        btn_fan.setOnClickListener {
            if(btn_fan_check == 0) {
                btn_fan.setImageResource(R.drawable.fan_off)
                viewModel.onClickSendData("fan on")
                btn_fan_check = 1
            } else {
                btn_fan.setImageResource(R.drawable.fan_on)
                viewModel.onClickSendData("fan off")
                btn_fan_check = 0
            }
        }

        var btn_curtain = findViewById<ImageButton>(R.id.btn_curtain)
        var btn_curtain_check = 0

        btn_curtain.setOnClickListener {
            if(btn_curtain_check == 0) {
                btn_curtain.setImageResource(R.drawable.curtain_off)
                viewModel.onClickSendData("curtain on")
                btn_curtain_check = 1
            } else {
                btn_curtain.setImageResource(R.drawable.curtain_on)
                viewModel.onClickSendData("curtain off")
                btn_curtain_check = 0
            }
        }
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            viewModel.onClickConnect()
        }
    }


    private fun initObserving(){

        //Progress
        viewModel.inProgress.observe(this, {
            if (it.getContentIfNotHandled() == true) {
                viewModel.inProgressView.set(true)
            } else {
                viewModel.inProgressView.set(false)
            }
        })
        //Progress text
        viewModel.progressState.observe(this, {
            viewModel.txtProgress.set(it)
        })

        //Bluetooth On 요청
        viewModel.requestBleOn.observe(this, {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startForResult.launch(enableBtIntent)

        })

        //Bluetooth Connect/Disconnect Event
        viewModel.connected.observe(this, {
            if (it != null) {
                if (it) {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(true)
                    Util.showNotification("디바이스와 연결되었습니다.")
                } else {
                    viewModel.setInProgress(false)
                    viewModel.btnConnected.set(false)
                    Util.showNotification("디바이스와 연결이 해제되었습니다.")
                }
            }
        })

        //Bluetooth Connect Error
        viewModel.connectError.observe(this, {
            Util.showNotification("Connect Error. Please check the device")
            viewModel.setInProgress(false)
        })

        //Data Receive
        viewModel.putTxt.observe(this, {
            if (it != null) {
                recv += it
                sv_read_data.fullScroll(View.FOCUS_DOWN)
                viewModel.txtRead.set(recv)
            }
        })
    }
    private fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (context?.let { ActivityCompat.checkSelfPermission(it, permission) }
                    != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }
    // Permission check
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_ALL_PERMISSION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
                } else {
                    requestPermissions(permissions, REQUEST_ALL_PERMISSION)
                    Toast.makeText(this, "Permissions must be granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.txtRead.set("here you can see the message come")
    }

    override fun onPause(){
        super.onPause()
        viewModel.unregisterReceiver()
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        viewModel.setInProgress(false)
    }

}