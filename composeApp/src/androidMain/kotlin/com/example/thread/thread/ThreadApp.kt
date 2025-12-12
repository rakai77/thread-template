package com.example.thread.thread

import android.app.Application
import com.example.thread.thread.di.initKoinModule
import com.example.thread.thread.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class ThreadApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinModule(
            additionalModules = listOf(viewModelModule),
            appDeclaration = {
                androidContext(this@ThreadApp)
                androidLogger()
            }
        )
    }
}