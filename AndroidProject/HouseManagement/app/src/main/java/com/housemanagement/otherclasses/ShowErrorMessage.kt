package com.housemanagement.otherclasses

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import kotlin.system.exitProcess


class ShowErrorMessage {
    companion object {
        @JvmStatic fun show(context: Context, title: String, message: String, value: Int) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setCancelable(false)

            builder.setNegativeButton(
                "Ок",
                DialogInterface.OnClickListener { dialog, id ->
                    //android.os.Process.killProcess(android.os.Process.myPid())
                    if(value == 1) exitProcess(0)
                    else dialog.cancel()
                })

            val alert: AlertDialog = builder.create()
            if(!(context as Activity).isFinishing) alert.show()

        }
    }
}