package app.vdh.org.vdhapp.extenstions

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

fun <T> Activity.navigateTo(klass: Class<T>, data: Bundle? = null) {
    val intent = Intent(this, klass)
    data?.let {
        intent.putExtras(data)
    }
    this.startActivity(intent)
}

fun <T : BottomSheetDialogFragment> AppCompatActivity.openBottomDialogFragment(fragment: T, tag: String) {
    fragment.show(supportFragmentManager, tag)
}