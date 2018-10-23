package xin.cyun.sdt.client

import java.io._
import java.net.Socket

import org.apache.commons.cli.{DefaultParser, Option, Options, ParseException}
import org.apache.commons.io.IOUtils
import org.apache.log4j.PropertyConfigurator
import org.slf4j.{Logger, LoggerFactory}
import xin.cyun.sdt.Tools
import xin.cyun.sdt.bean.{ClientConfig, ConfigParser}
import xin.cyun.sdt.io.UploadInfoWritable

/**
  * Created by BING on 2018/10/18.
  */
class DataClient {
  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[DataClient])

  private var socket: Socket = _
  private var isNewSocket: Boolean = false

  def start(args: Array[String]): Unit = {
    val options = new Options()
    val pOpt = new Option("p", "configpath", true, "config path")
    pOpt.setRequired(true)
    options.addOption(pOpt)
    val fOpt = new Option("f", "configfile", true, "config file path")
    fOpt.setRequired(true)
    options.addOption(fOpt)

    var configFileOpt: scala.Option[File] = None
    try {
      val parser = new DefaultParser() //Array[String]("-c", "/data/","-f","/data/s.conf")
      val cmd = parser.parse(options, args)
      val configPath = cmd.getOptionValue("c")
      val configFile = new File(cmd.getOptionValue("f"))
      if (!configFile.exists()) {
        println("file not found:" + configFile.getAbsolutePath)
        return
      }
      configFileOpt = Some(configFile)
      val log4jFile = new File(configPath, "log4j.properties")
      try {
        if (log4jFile.exists()) {
          PropertyConfigurator.configure(log4jFile.getAbsolutePath)
        } else {
          val logOpt = Tools.getFileAsStream("log4j.properties", "log4j.file")
          if (logOpt.nonEmpty) {
            PropertyConfigurator.configure(logOpt.get)
          }
        }
      } catch {
        case e: Throwable =>
          println("load log4j.properties error.error:")
          e.printStackTrace()
      }
    } catch {
      case e: ParseException =>
        println("parse argument error.")
        Tools.printHelp(options)
        return
    }
    val configParser = new ConfigParser()
    configParser.loadProps(configFileOpt.get)
    val conf = configParser.getClientConfig()
    val dataPath = new File(conf.dataPath)
    if (!dataPath.exists()) dataPath.mkdirs()
    if (!conf.deleteData) {
      val backupPath = new File(conf.backupData)
      if (!backupPath.exists()) backupPath.mkdirs()
    }
    sendFiles(conf)
  }

  private def makeConnected(conf: ClientConfig): Unit = {
    var retryIndex: Int = 0
    isNewSocket = false
    while ((socket == null || socket.isClosed || !socket.isConnected) && (conf.retryCount <= 0 || retryIndex < conf.retryCount)) {
      try {
        socket = null
        setSocket(conf)
        isNewSocket = true
      } catch {
        case e: Throwable =>
          retryIndex += 1
          logger.warn("connect count:" + retryIndex + ",error:" + e.getMessage)
          Tools.tryBlock(socket.close())
          Thread.sleep(conf.retrySleep)
      }
    }
  }

  private def setSocket(conf: ClientConfig): Unit = {
    if (socket != null) {
      Tools.tryBlock(socket.close())
      socket = new Socket(conf.host, conf.port)
    } else {
      socket = new Socket(conf.host, conf.port)
    }
    logger.info("connect to " + conf.host + ",port:" + conf.port)
  }

  private def sendFiles(conf: ClientConfig): Unit = {
    val uploadInfo = new UploadInfoWritable
    var out: DataOutputStream = null
    var in: DataInputStream = null
    var address: String = ""
    var isShutdown = false
    while (!isShutdown) {
      val srcPath = new File(conf.dataPath)
      val srcFiles = srcPath.listFiles().filter(f => f.isFile && f.length() > 0)
      if (srcFiles.nonEmpty) {
        for (f <- srcFiles if (!isShutdown)) {
          try {
            makeConnected(conf)
            if (socket == null) {
              isShutdown = true
            } else {
              if (isNewSocket) {
                address = socket.getInetAddress.getHostAddress
                out = new DataOutputStream(socket.getOutputStream)
                in = new DataInputStream(socket.getInputStream)
              }
              val s = System.currentTimeMillis()
              uploadInfo.name = f.getName
              uploadInfo.len = f.length()
              out.writeInt(uploadInfo.reqType)
              uploadInfo.write(out)
              sendFile(f, out, uploadInfo, conf)
              val elapsed = System.currentTimeMillis() - s
              logger.info("send file:" + f.getName + ",dataLen:" + uploadInfo.len + ",elapsed:" + elapsed)
            }
          } catch {
            case e: EOFException =>
              logger.info(address + " disconnect,port:" + conf.port)
            case e: Throwable =>
              logger.error(address + " send file error:" + e.getMessage)
          }
        }
      } else {
        Thread.sleep(5000)
      }
    }
  }

  private def sendFile(f: File, out: DataOutputStream, uploadInfo: UploadInfoWritable, conf: ClientConfig): Unit = {
    val fis = new FileInputStream(f)
    IOUtils.copyLarge(fis, out)
    fis.close()
    if (conf.deleteData) {
      val isOk = f.delete()
      logger.debug("deleted " + isOk + " file:" + f.getName)
      if (!isOk) {
        logger.warn("delete file failed:" + f.getName)
      }
    } else {
      val finalName = if (conf.backupSuffix == null) f.getName else f.getName.concat(conf.backupSuffix)
      val backPath = new File(conf.backupData, finalName)
      if (backPath.exists()) backPath.delete()
      val isOk = f.renameTo(backPath)
      logger.debug("renamed " + isOk + " file " + f.getName + " to " + backPath.getName)
      if (!isOk) {
        logger.warn("rename file failed:" + f.getName)
      }
    }
  }
}

object DataClient {
  def main(args: Array[String]): Unit = {
    new DataClient().start(args)
  }
}
