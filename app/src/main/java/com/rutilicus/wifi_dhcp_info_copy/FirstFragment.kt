package com.rutilicus.wifi_dhcp_info_copy

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rutilicus.wifi_dhcp_info_copy.databinding.FragmentFirstBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private lateinit var viewModel: WifiSettingsViewModel

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

    private fun invalidateAll() {
        viewModel.ssid.value = ""
        viewModel.ipSetting.value = ""
        viewModel.ipAddress.value = ""
        viewModel.gateway.value = ""
        viewModel.networkPrefixLength.value = ""
        viewModel.dns1.value = ""
        viewModel.dns2.value = ""

        viewModel.ssidButtonVisible.value = View.INVISIBLE
        viewModel.ipSettingButtonVisible.value = View.INVISIBLE
        viewModel.ipAddressButtonVisible.value = View.INVISIBLE
        viewModel.gatewayButtonVisible.value = View.INVISIBLE
        viewModel.networkPrefixLengthButtonVisible.value = View.INVISIBLE
        viewModel.dns1ButtonVisible.value = View.INVISIBLE
        viewModel.dns2ButtonVisible.value = View.INVISIBLE
        viewModel.copyAllButtonVisible.value = View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.application?.let {
            viewModel = WifiSettingsViewModel(it)
        }

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.model = viewModel

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        invalidateAll()

        val wifiManager = activity?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        wifiManager.connectionInfo.apply {
            if (networkId != -1) {
                if (activity?.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    for (conf in wifiManager.configuredNetworks) {
                        if (conf.networkId == this.networkId) {
                            viewModel.ssid.value = ssid
                            viewModel.ssidButtonVisible.value = View.VISIBLE
                            if (conf.toString().lowercase(
                                    Locale.getDefault()).indexOf("DHCP".lowercase(Locale.getDefault())) > -1) {
                                viewModel.ipSetting.value = resources.getString(R.string.config_dhcp)
                                viewModel.ipSettingButtonVisible.value = View.VISIBLE
                                wifiManager.dhcpInfo.apply {
                                    viewModel.ipAddress.value = ip2String(ipAddress)
                                    viewModel.ipAddressButtonVisible.value = View.VISIBLE
                                    viewModel.gateway.value = ip2String(gateway)
                                    viewModel.gatewayButtonVisible.value = View.VISIBLE
                                    viewModel.networkPrefixLength.value = netmask.countOneBits().toString()
                                    viewModel.networkPrefixLengthButtonVisible.value = View.VISIBLE
                                    viewModel.dns1.value = ip2String(dns1)
                                    viewModel.dns1ButtonVisible.value = View.VISIBLE
                                    viewModel.dns2.value = ip2String(dns2)
                                    viewModel.dns2ButtonVisible.value = View.VISIBLE
                                }
                            } else {
                                viewModel.ipSetting.value = resources.getString(R.string.config_static)
                                viewModel.ipSettingButtonVisible.value = View.VISIBLE
                            }
                            viewModel.copyAllButtonVisible.value = View.VISIBLE
                            break
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}