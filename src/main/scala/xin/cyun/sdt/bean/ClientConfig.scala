package xin.cyun.sdt.bean

/**
  * Created by BING on 2018/10/23.
  */
class ClientConfig {
  var dataPath: String = _

  var host: String = _

  var port: Int = _

  var bufferSize: Int = _

  var deleteData: Boolean = true
  var backupData: String = _
  var backupSuffix: String = _
  var retryCount:Int=0
  var retrySleep:Long=1000
  var compressed: Boolean = false

}
