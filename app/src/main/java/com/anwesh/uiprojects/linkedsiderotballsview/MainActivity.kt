package com.anwesh.uiprojects.linkedsiderotballsview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.siderotballsview.SideRotBallsView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SideRotBallsView.create(this)
    }
}
