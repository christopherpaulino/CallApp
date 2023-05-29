package com.frontic.callapp

import android.os.Build
import android.telecom.*
import android.util.Log
import android.widget.Toast

class CallConnectionService: ConnectionService() {

    companion object {
        const val PHONE = "PHONE"
        var conn: CallConnection? = null
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        val bundle = request!!.extras
        val phone = bundle.getString(PHONE)

        conn = CallConnection(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            conn?.connectionProperties = Connection.PROPERTY_SELF_MANAGED
        }
        conn?.setCallerDisplayName(phone, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setAddress(request.address, TelecomManager.PRESENTATION_ALLOWED)
        conn?.setInitializing()
        conn?.setActive()
        return conn!!
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
        Log.e("onCreateIncomingFailed:", request.toString())
        Toast.makeText(applicationContext, "onCreateIncomingConnectionFailed", Toast.LENGTH_LONG)
            .show();
    }
}