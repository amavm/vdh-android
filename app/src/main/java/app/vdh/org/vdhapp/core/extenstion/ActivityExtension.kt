package app.vdh.org.vdhapp.core.extenstion

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun <T> Activity.navigateTo(klass: Class<T>, data: Bundle? = null) {
    val intent = Intent(this, klass)
    data?.let {
        intent.putExtras(data)
    }
    startActivity(intent)
}

fun <T> Fragment.navigateTo(klass: Class<T>, data: Bundle? = null) {
    val intent = Intent(context, klass)
    data?.let {
        intent.putExtras(data)
    }
    startActivity(intent)
}

fun <T : BottomSheetDialogFragment> AppCompatActivity.openBottomDialogFragment(fragment: T, tag: String) {
    fragment.show(supportFragmentManager, tag)
}