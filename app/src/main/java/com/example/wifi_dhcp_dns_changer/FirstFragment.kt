package com.example.wifi_dhcp_dns_changer

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wifi_dhcp_dns_changer.databinding.FragmentFirstBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private lateinit var textViewSsid: TextView
    private lateinit var textViewIpSetting: TextView
    private lateinit var textViewIpAddress: TextView
    private lateinit var textViewGateway: TextView
    private lateinit var textViewNetworkPrefixLength: TextView
    private lateinit var textViewDns1: TextView
    private lateinit var textViewDns2: TextView
    private lateinit var buttonSettingChange: Button

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun ip2String(ip: Int): String {
        var ret = (ip and 0xFF).toString()

        for (i in 1..3) {
            ret += ".${(ip.ushr(i * 8) and 0xFF)}"
        }

        return ret
    }

    private fun getEnumValue(className: String, value: String): Any {
        val enumConstants = Class.forName(className).enumConstants as Array<Enum<*>>
        return enumConstants.first { it.name == value }
    }

    private fun callMethod(
        classObject: Any, methodName: String, parameterTypes: Array<String>, parameterValues: Array<Any>) {
        val paramClasses = Array(parameterTypes.size) {
            Class.forName(parameterTypes[it])
        }
        val method = classObject.javaClass.getDeclaredMethod(methodName, *paramClasses)
        method.invoke(classObject, *parameterValues)
    }

    private fun copyWifiConf(src: WifiConfiguration, dst: WifiConfiguration) {
        dst.BSSID = src.BSSID
        dst.FQDN = src.FQDN
        dst.SSID = dst.SSID
        dst.allowedAuthAlgorithms = src.allowedAuthAlgorithms
        dst.allowedGroupCiphers = src.allowedGroupCiphers
        dst.allowedKeyManagement = src.allowedKeyManagement
        dst.allowedPairwiseCiphers = src.allowedPairwiseCiphers
        dst.allowedProtocols = src.allowedProtocols
        dst.enterpriseConfig = src.enterpriseConfig
        dst.hiddenSSID = src.hiddenSSID
        dst.networkId = src.networkId
        dst.preSharedKey = src.preSharedKey
        dst.priority = src.priority
        dst.providerFriendlyName = src.providerFriendlyName
        dst.roamingConsortiumIds = src.roamingConsortiumIds
        dst.status = src.status
        dst.wepKeys = src.wepKeys
        dst.wepTxKeyIndex = src.wepTxKeyIndex
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root.apply {
            textViewSsid = this.findViewById(R.id.textview_ssid)
            textViewIpSetting = this.findViewById(R.id.textview_ip_setting)
            textViewIpAddress = this.findViewById(R.id.textview_ip_address)
            textViewGateway = this.findViewById(R.id.textview_gateway)
            textViewNetworkPrefixLength = this.findViewById(R.id.textview_network_prefix_length)
            textViewDns1 = this.findViewById(R.id.textview_dns_1)
            textViewDns2 = this.findViewById(R.id.textview_dns_2)
            buttonSettingChange = this.findViewById(R.id.button_setting_change)
        }

    }

    override fun onResume() {
        super.onResume()

        val wifiManager = activity?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifiManager.connectionInfo.apply {
            if (networkId != -1) {
                if (activity?.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    for (conf in wifiManager.configuredNetworks) {
                        if (conf.networkId == this.networkId) {
                            textViewSsid.text = ssid
                            if (conf.toString().lowercase(
                                    Locale.getDefault()).indexOf("DHCP".lowercase(Locale.getDefault())) > -1) {
                                textViewIpSetting.setText(R.string.config_dhcp)
                                wifiManager.dhcpInfo.apply {
                                    textViewIpAddress.text = ip2String(ipAddress)
                                    textViewGateway.text = ip2String(gateway)
                                    textViewNetworkPrefixLength.text = netmask.countOneBits().toString()
                                    textViewDns1.text = ip2String(dns1)
                                    textViewDns2.text = ip2String(dns2)
                                }
                            } else {
                                textViewIpSetting.setText(R.string.config_static)
                                textViewGateway.text = ""
                                textViewIpAddress.text = ""
                                textViewNetworkPrefixLength.text = ""
                                textViewDns1.text = ""
                                textViewDns2.text = ""
                                buttonSettingChange.setText(R.string.set_dhcp)
                                buttonSettingChange.setOnClickListener {
                                    wifiManager.disconnect()

                                    val newConf = WifiConfiguration()
                                    copyWifiConf(conf, newConf)
                                    callMethod(
                                        newConf,
                                        "setIpAssignment",
                                        arrayOf("android.net.IpConfiguration\$IpAssignment"),
                                        arrayOf(getEnumValue(
                                            "android.net.IpConfiguration\$IpAssignment", "DHCP"))
                                    )

                                    wifiManager.updateNetwork(newConf)

                                    wifiManager.enableNetwork(newConf.networkId, true)

                                    activity?.finish()
                                }
                            }
                            break
                        }
                    }
                }
            } else {
                textViewSsid.text = ""
                textViewIpSetting.text = ""
                textViewIpAddress.text = ""
                textViewGateway.text = ""
                textViewNetworkPrefixLength.text = ""
                textViewDns1.text = ""
                textViewDns2.text = ""
                buttonSettingChange.text = ""
                buttonSettingChange.setOnClickListener { /* NOP */ }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}