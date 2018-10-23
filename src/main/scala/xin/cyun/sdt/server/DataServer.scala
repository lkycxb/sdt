package xin.cyun.sdt.server

import java.io._
import java.net.{ServerSocket, Socket}

import org.apache.commons.cli.{DefaultParser, Option, Options, ParseException}
import org.apache.log4j.PropertyConfigurator
import org.slf4j.{Logger, LoggerFactory}
import xin.cyun.sdt.Tools
import xin.cyun.sdt.bean.{ConfigParser, ServerConfig}

/**
  * Created by BING on 2018/10/18.
  */
class DataServer {

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[DataServer])

  private var ss: ServerSocket = _


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
    val conf = configParser.getServerConfig()

    logger.info("data server starting...")
    val dataPath = new File(conf.dataPath)
    if (!dataPath.exists()) dataPath.mkdirs()

    ss = new ServerSocket(conf.port)
    logger.info("data server started,listener port:" + conf.port)

    var threadIndex: Int = 0
    while (DataServer.isRunning) {
      checkThreadSize(conf.threadSize)
      val s = ss.accept()
      val address = s.getInetAddress.getHostAddress
      logger.info("connected:" + address + ",threadIndex:" + threadIndex)
      val dsr = new DataServerRunable(s, threadIndex, conf, address)
      val t = new Thread(dsr)
      DataServer.thMap.put(threadIndex, t)
      t.start()
      threadIndex += 1
    }
  }

  def checkThreadSize(threadSize:Int): Unit = {
    while (DataServer.thMap.size >= threadSize) {
      logger.info("wait 1s,max thread size:" + threadSize + ",curSize:" + DataServer.thMap.size)
      Thread.sleep(1000)
    }
  }


  def stop(): Unit = {
    DataServer.isRunning = false
    var totalWait: Long = 0L
    val waitTime: Long = 1000L
    val maxWait: Long = 300000L //5min
    while (!DataServer.thMap.isEmpty && totalWait <= maxWait) {
      Thread.sleep(waitTime)
      println("wait stop time:" + waitTime)
      totalWait += waitTime
    }
    ss.close()
    println("stoped")
  }
}



object DataServer {
  @volatile var isRunning = true
  //private val dsrMap = new java.util.concurrent.ConcurrentHashMap[Int,DataServerRunable]()
  val thMap = new java.util.concurrent.ConcurrentHashMap[Int, Thread]()
  //private val clearMap = new java.util.concurrent.ConcurrentHashMap[Int,Thread]()

  def main(args: Array[String]): Unit = {

    val server = new DataServer()
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = server.stop()
    }))
    server.start(args)
  }
}
