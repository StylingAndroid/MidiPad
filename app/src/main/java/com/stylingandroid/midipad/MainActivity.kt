package com.stylingandroid.midipad

import android.arch.lifecycle.LifecycleRegistry
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import com.stylingandroid.midipad.midi.MidiController
import com.stylingandroid.midipad.ui.DeviceAdapter
import com.stylingandroid.midipad.ui.MidiPad
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val lifecycleRegistry: LifecycleRegistry by lazyFast { LifecycleRegistry(this) }
    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    private val midiController: MidiController by viewModelProvider {
        MidiController(application)
    }

    private val deviceAdapter: DeviceAdapter by lazyFast {
        DeviceAdapter(this, { it.inputPortCount > 0 })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_toolbar)
        supportActionBar?.apply {
            setTitle(R.string.app_name)
        }

        midiController.observeDevices(this, deviceAdapter)

        supportFragmentManager.beginTransaction()?.also {
            it.replace(R.id.main_content, Fragment.instantiate(this, MidiPad::class.java.canonicalName))
            it.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.findItem(R.id.app_bar_selector)?.actionView?.apply {
            findViewById<Spinner>(R.id.output_selector)?.apply {
                adapter = deviceAdapter
                onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        midiController.closeAll()
                    }

                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        deviceAdapter[position].apply {
                            midiController.open(this)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun onDestroy() {
        midiController.closeAll()
        midiController.removeObserver(deviceAdapter)
        super.onDestroy()
    }
}
