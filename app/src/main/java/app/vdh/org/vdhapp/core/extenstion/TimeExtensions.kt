package app.vdh.org.vdhapp.core.extenstion

import java.util.concurrent.TimeUnit

fun Int.toMillisecondFromNow() = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(toLong())