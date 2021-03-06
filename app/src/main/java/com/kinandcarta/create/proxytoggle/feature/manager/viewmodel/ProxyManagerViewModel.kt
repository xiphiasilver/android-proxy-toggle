package com.kinandcarta.create.proxytoggle.feature.manager.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.kinandcarta.create.proxytoggle.android.DeviceSettingsManager
import com.kinandcarta.create.proxytoggle.android.ProxyValidator
import com.kinandcarta.create.proxytoggle.android.ThemeSwitcher
import com.kinandcarta.create.proxytoggle.extensions.SingleLiveEvent
import com.kinandcarta.create.proxytoggle.feature.manager.view.ProxyManagerEvent
import com.kinandcarta.create.proxytoggle.feature.manager.view.ProxyState
import com.kinandcarta.create.proxytoggle.model.Proxy
import com.kinandcarta.create.proxytoggle.settings.AppSettings

class ProxyManagerViewModel @ViewModelInject constructor(
    private val deviceSettingsManager: DeviceSettingsManager,
    private val proxyValidator: ProxyValidator,
    private val appSettings: AppSettings,
    private val themeSwitcher: ThemeSwitcher
) : ViewModel() {

    val proxyEvent = SingleLiveEvent<ProxyManagerEvent>()

    val proxyState = Transformations.map(deviceSettingsManager.proxySetting) { proxy ->
        if (proxy.isEnabled) {
            ProxyState.Enabled(proxy.address, proxy.port)
        } else ProxyState.Disabled()
    }

    val lastUsedProxy: Proxy
        get() = appSettings.lastUsedProxy

    fun enableProxy(address: String, port: String) {
        when {
            !proxyValidator.isValidIP(address) -> {
                proxyEvent.value = ProxyManagerEvent.InvalidAddress
            }
            !proxyValidator.isValidPort(port) -> {
                proxyEvent.value = ProxyManagerEvent.InvalidPort
            }
            else -> {
                deviceSettingsManager.enableProxy(Proxy(address, port))
            }
        }
    }

    fun disableProxy() {
        deviceSettingsManager.disableProxy()
    }

    fun toggleTheme() = themeSwitcher.toggleTheme()
}
