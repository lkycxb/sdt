package xin.cyun.sdt.bean

import java.io.{File, FileInputStream}
import java.util.Properties

import xin.cyun.sdt.CommonConfigs._

import scala.collection.mutable.HashMap

/**
  * Created by BING on 2018/10/23.
  */
class ConfigParser {

  private val configMap = new HashMap[String, String]()


  def loadProps(configFile: File): Unit = {
    val props = new Properties()
    try {
      val fis = new FileInputStream(configFile)
      props.load(fis)
      fis.close()
    } catch {
      case e: Throwable =>
        println("load config error:" + e.getMessage)
    }
    val iter = props.entrySet().iterator()
    while (iter.hasNext) {
      val kv = iter.next()
      configMap += kv.getKey.toString -> kv.getValue.toString
    }
    //scala.collection.JavaConverters.mapAsScalaMap(props).toMap.map(kv => (kv._1.toString, kv._2.toString))
  }

  def getVal(k: String): Option[String] = configMap.get(k)

  def getConf(k: String): String = {
    getVal(k).get
  }

  def getConf(k: String, v: String): String = getVal(k).getOrElse(v)

  def getLong(k: String, v: Long): Long = getVal(k).getOrElse(v.toString).toLong

  def getInt(k: String, v: Int): Long = getVal(k).getOrElse(v.toString).toInt

  def getServerConfig(): ServerConfig = {
    val conf = new ServerConfig
    val dataPath: String = getConf(DATA_PATH, DATA_PATH_DEFAULT)
    conf.dataPath = dataPath
    val dataTmpSuffix: String = getConf(DATA_TEMP_SUFFIX, DATA_TEMP_SUFFIX_DEFAULT)
    conf.dataTmpSuffix = dataTmpSuffix

    val port: Int = getConf(PORT, PORT_DEFAULT).toInt
    conf.port = port
    val bufferSize: Int = getConf(BUFFER_SIZE, BUFFER_SIZE_DEFAULT).toInt
    conf.bufferSize = bufferSize
    val threadSize: Int = getConf(THREAD_SIZE, THREAD_SIZE_DEFAULT).toInt
    conf.threadSize = threadSize

    conf
  }

  def getClientConfig(): ClientConfig = {
    val conf = new ClientConfig
    val dataPath: String = getConf(DATA_PATH, DATA_PATH_DEFAULT)
    conf.dataPath = dataPath
    val host: String = getConf(HOST, HOST_DEFAULT)
    conf.host = host
    val port: Int = getConf(PORT, PORT_DEFAULT).toInt
    conf.port = port
    val bufferSize: Int = getConf(BUFFER_SIZE, BUFFER_SIZE_DEFAULT).toInt
    conf.bufferSize = bufferSize
    val deleteData = getConf(DELETE_DATA, DELETE_DATA_DEFAULT).toBoolean
    conf.deleteData = deleteData
    if (!deleteData) {
      val backupPath = getConf(BACKUP_PATH, BACKUP_PATH_DEFAULT)
      conf.backupData = backupPath
      val backupSuffix = getConf(BACKUP_SUFFIX)
      conf.backupSuffix = backupSuffix
    }
    val retryCount = getConf(RETRY_COUNT, RETRY_COUNT_DEFAULT).toInt
    conf.retryCount = retryCount

    val retrySleep = getConf(RETRY_SLEEP, RETRY_SLEEP_DEFAULT).toInt
    conf.retrySleep = retrySleep

    val compressed: Boolean = getConf(COMPRESSED, COMPRESSED_DEFAULT).toBoolean
    conf.compressed = compressed
    conf
  }


}
