package me.dmdev.rxpm.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * @author Dmitriy Gorbunov
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.container, TextSearchFragment())
                    .commit()
        }
    }

}