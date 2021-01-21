package com.kwai.coroutine

import kotlinx.coroutines.*

/**
 * Created by tuchuantao on 2021/1/18
 */
object Test {

  const val TAG = "test"

  fun test() {
    GlobalScope.launch(Dispatchers.Main) {
      printLog(TAG, "launch start")
      var token = getUserToken()
      printLog(TAG, "launch token=$token")
      var userName = getUserNameByToken(token)
      printLog(TAG, "launch end userName=$userName")
    }
  }

  private suspend fun getUserToken(): String {
    return GlobalScope.async {
      printLog(TAG, "getUserToken()")
      delay(1000) // 模拟耗时
      "token"
    }.await()
  }

  private suspend fun getUserNameByToken(token: String): String {
    return GlobalScope.async {
      printLog(TAG, "getUserNameByToken()")
      //delay(1000) // 模拟耗时
      "李四"
    }.await()
  }

//  private suspend fun getUserAge(): Deferred<Int> {
//    return GlobalScope.async {
//      printLog(TAG, "getUserAge()")
//      18
//    }
//  }
//
//  private suspend fun getUserAge2(): Int {
//    return 18
//  }

}