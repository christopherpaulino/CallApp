package com.frontic.callapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telecom.VideoProfile
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi

class CallHandler(context: Context) {

    private var callManagerContext = context;
    private var telecomManager : TelecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
    private lateinit var phoneAccountHandle: PhoneAccountHandle

    fun init() {
        val componentName = ComponentName(callManagerContext, CallConnectionService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            phoneAccountHandle = PhoneAccountHandle(componentName, "SIP Calling")
            val phoneAccount = PhoneAccount.builder(phoneAccountHandle, "SIP Calling")
                .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER).build()
            telecomManager.registerPhoneAccount(phoneAccount)
        }
    }

    fun startIncomingCall(phone: String) {
        if (callManagerContext.checkSelfPermission(Manifest.permission.MANAGE_OWN_CALLS) ==
            PackageManager.PERMISSION_GRANTED) {
            val extras = Bundle()

            extras.putString(CallConnectionService.PHONE, phone)

            extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            extras.putBoolean(TelecomManager.EXTRA_START_CALL_WITH_SPEAKERPHONE, true)
            
            var isCallPermitted = false
            isCallPermitted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telecomManager.isIncomingCallPermitted(phoneAccountHandle)
            } else {
                true
            }
            try {
                Log.e("startIncomingCall: ",extras.toString()+"\n"+isCallPermitted)
                telecomManager.addNewIncomingCall(phoneAccountHandle, extras)
            } catch (e: SecurityException) {
                val intent = Intent()
                intent.action = TelecomManager.ACTION_CHANGE_PHONE_ACCOUNTS
//                val componentName = ComponentName("com.android.server.telecom", "com.android.server.telecom.settings.EnableAccountPreferenceActivity")
//                intent.setComponent(componentName)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                callManagerContext.startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(callManagerContext,"Error occured:"+e.message, Toast.LENGTH_LONG).show()
            }
        } else {
            Log.e("startIncomingCall: ","Permission not granted")
        }
    }

}