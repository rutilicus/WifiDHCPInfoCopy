package com.example.wifi_dhcp_dns_changer

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.navigation.fragment.findNavController
import com.example.wifi_dhcp_dns_changer.databinding.FragmentFirstBinding
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private lateinit var textViewSsid: TextView
    private lateinit var textViewIpSetting: TextView
    private lateinit var textViewGateway: TextView
    private lateinit var textViewNetworkPrefixLength: TextView
    private lateinit var textViewDns1: TextView
    private lateinit var textViewDns2: TextView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun ip2String(ip: Int): String {
        var ret = (ip and 0xFF).toString()

        for (i in 1..3) {
            ret = (ip.ushr(i * 8) and 0xFF).toString() + "." + ret
        }

        return ret
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root.apply {
            textViewSsid = this.findViewById(R.id.textview_ssid)
            textViewIpSetting = this.findViewById(R.id.textview_ip_setting)
            textViewGateway = this.findViewById(R.id.textview_gateway)
            textViewNetworkPrefixLength = this.findViewById(R.id.textview_network_prefix_length)
            textViewDns1 = this.findViewById(R.id.textview_dns_1)
            textViewDns2 = this.findViewById(R.id.textview_dns_2)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
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
                                    textViewGateway.text = ip2String(gateway)
                                    textViewNetworkPrefixLength.text = netmask.countOneBits().toString()
                                    textViewDns1.text = ip2String(dns1)
                                    textViewDns2.text = ip2String(dns2)
                                }
                            } else {
                                textViewIpSetting.setText(R.string.config_static)
                                textViewGateway.text = ""
                                textViewNetworkPrefixLength.text = ""
                                textViewDns1.text = ""
                                textViewDns2.text = ""
                            }
                            break
                        }
                    }
                }
            } else {
                textViewSsid.text = ""
                textViewIpSetting.text = ""
                textViewGateway.text = ""
                textViewNetworkPrefixLength.text = ""
                textViewDns1.text = ""
                textViewDns2.text = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}