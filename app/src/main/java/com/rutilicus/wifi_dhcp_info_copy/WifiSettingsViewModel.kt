package com.rutilicus.wifi_dhcp_info_copy

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class WifiSettingsViewModel(application: Application): AndroidViewModel(application) {
    var ssid: MutableLiveData<String> = MutableLiveData<String>("")
    var ipSetting: MutableLiveData<String> = MutableLiveData<String>("")
    var ipAddress: MutableLiveData<String> = MutableLiveData<String>("")
    var gateway: MutableLiveData<String> = MutableLiveData<String>("")
    var networkPrefixLength: MutableLiveData<String> = MutableLiveData<String>("")
    var dns1: MutableLiveData<String> = MutableLiveData<String>("")
    var dns2: MutableLiveData<String> = MutableLiveData<String>("")

    var ssidButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var ipSettingButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var ipAddressButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var gatewayButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var networkPrefixLengthButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var dns1ButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var dns2ButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)
    var copyAllButtonVisible: MutableLiveData<Int> = MutableLiveData<Int>(View.INVISIBLE)

    fun copyClipboard(value: String) {
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", value))
    }

    fun copyAllClipboard() {
        val data = """
            ssid: ${ssid.value}
            ipSetting: ${ipSetting.value}
            ipAddress: ${ipAddress.value}
            gateway: ${gateway.value}
            networkPrefixLength: ${networkPrefixLength.value}
            dns1: ${dns1.value}
            dns2: ${dns2.value}
        """.trimIndent()
        val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("", data))
    }
}
