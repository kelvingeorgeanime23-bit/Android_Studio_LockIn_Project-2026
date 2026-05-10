package com.kelvin.lockin

import android.app.Application
import com.kelvin.lockin.ui.screens.focusmode.FocusModeViewModel

class LockInApp : Application() {
    val focusModeViewModel: FocusModeViewModel by lazy {
        FocusModeViewModel(this)
    }
}