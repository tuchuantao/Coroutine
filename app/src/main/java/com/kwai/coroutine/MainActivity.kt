package com.kwai.coroutine

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import com.kwai.coroutine.base.BaseActivity
import com.kwai.coroutine.databinding.ActivityMainBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity<ActivityMainBinding>() {

  companion object {
    const val TAG = "MainActivity"
    const val THREAD_COUNT = 1000
    const val SLEEP_TIME = 20L
  }

  override fun getLayoutResId(): Int {
    return R.layout.activity_main
  }

  override fun initView(savedInstanceState: Bundle?) {
    // Example 1：获取用户姓名
    binding.btnRxjava.setOnClickListener {
      getUserNameByRX()
    }

    binding.btnCoroutine.setOnClickListener {
      getUserNameByCoroutine()
    }

    // Example 2：协程与线程的效率对比
    binding.threadCost1.setOnClickListener {
      var startTime = SystemClock.elapsedRealtime()
      repeat(THREAD_COUNT) {
        Thread(kotlinx.coroutines.Runnable {
          Thread.sleep(SLEEP_TIME)
          printLog(TAG,"threadCost1: $it  cost time=${SystemClock.elapsedRealtime() - startTime}")
        }).start()
      }
    }
    binding.coroutineCost1.setOnClickListener {
      var startTime = SystemClock.elapsedRealtime()
      repeat(THREAD_COUNT) {
        GlobalScope.launch {
          delay(SLEEP_TIME)
          printLog(TAG, "coroutineCost1: $it  cost time=${SystemClock.elapsedRealtime() - startTime}")
        }
      }
    }
    binding.coroutineCost2.setOnClickListener {
      var startTime = SystemClock.elapsedRealtime()
      repeat(THREAD_COUNT) {
        GlobalScope.launch {
          Thread.sleep(SLEEP_TIME)
          printLog(TAG,"coroutineCost2: $it  cost time=${SystemClock.elapsedRealtime() - startTime}")
        }
      }
    }
    binding.threadCost2.setOnClickListener {
      var executor: ThreadPoolExecutor =
        ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 128, 60, TimeUnit.SECONDS, LinkedBlockingQueue())

      var startTime = SystemClock.elapsedRealtime()
      repeat(THREAD_COUNT) {
        executor.execute(Runnable {
          Thread.sleep(SLEEP_TIME)
          printLog(TAG, "threadCost2: $it  cost time=${SystemClock.elapsedRealtime() - startTime}")
        })
      }
    }

    // Global.launch()
    binding.launch.setOnClickListener {
      Test.test()
    }
  }

  private fun getUserNameByRX() {
    Observable.create<String> {
      it.onNext(getUserToken())
    }.map { token ->
      getUserNameByToken(token)
    }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        binding.userName.text = "RXJava: $it"
      }, {})
  }

  private fun getUserNameByCoroutine() {
    GlobalScope.launch(Dispatchers.Main) {
      var token = withContext(Dispatchers.Default) { getUserToken() }
      var userName = withContext(Dispatchers.Default) { getUserNameByToken(token) }
      binding.userName.text = "协程: $userName"
    }
  }

  private fun getUserToken(): String {
    printLog(TAG, "getUserToken()")
    return "token"
  }

  private fun getUserNameByToken(token: String): String {
    printLog(TAG, "getUserNameByToken()")
    return "李四"
  }

}

fun printLog(tag: String, msg: String) {
  Log.d(tag, "[${Thread.currentThread()} ]: $msg")
}