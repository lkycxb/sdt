package xin.cyun.sdt

import java.nio.charset.Charset

/**
  * Created by BING on 2018/10/18.
  */
object CommonConfigs {

  val COMMA_CHAR = ","
  val CHARSET_UTF8 = Charset.forName("UTF-8")

  val DATA_PATH = "data.path"
  val DATA_PATH_DEFAULT = "/tmp/data"
  val DATA_TEMP_SUFFIX = "data.temp.suffix"
  val DATA_TEMP_SUFFIX_DEFAULT = ".tmp"

  val PORT = "server.port"
  val PORT_DEFAULT = "9099"

  val BUFFER_SIZE = "buffer.size"
  val BUFFER_SIZE_DEFAULT = "4096"
  val THREAD_SIZE="thread.size"
  val THREAD_SIZE_DEFAULT="1000"

  val COMPRESSED="compressed"
  val COMPRESSED_DEFAULT="true"

  //client


  val HOST = "server.host"
  val HOST_DEFAULT = "127.0.0.1"

  val DELETE_DATA = "data.delete"
  val DELETE_DATA_DEFAULT = "true"

  val BACKUP_PATH = "backup.path"
  val BACKUP_PATH_DEFAULT = "/tmp/backup"
  val BACKUP_SUFFIX="backup.suffix"
  val BACKUP_SUFFIX_DEFAULT=".bak"
  val RETRY_COUNT="retry.connect.count"
  val RETRY_COUNT_DEFAULT="0"
  val RETRY_SLEEP="retry.connect.sleep"
  val RETRY_SLEEP_DEFAULT="2000"
}
