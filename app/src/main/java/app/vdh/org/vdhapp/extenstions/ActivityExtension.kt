package app.vdh.org.vdhapp.extenstions

import android.app.Activity
import android.content.Intent
import android.os.Bundle

fun <T> Activity.navigateTo(klass: Class<T>, data: Bundle? = null) {
    val intent = Intent(this, klass)
    data?.let {
        intent.putExtras(data)
    }
    this.startActivity(intent)
}