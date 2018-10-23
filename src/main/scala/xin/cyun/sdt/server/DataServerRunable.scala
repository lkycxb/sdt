package xin.cyun.sdt.server

import java.io.{DataInputStream, DataOutputStream}
import java.net.Socket

import org.slf4j.{Logger, LoggerFactory}
import xin.cyun.sdt.Tools
import xin.cyun.sdt.bean.ServerConfig

/**
  * Created by BING on 2018/10/23.
  */
class DataServerRunable(s: Socket, threadIndex: Int, conf: ServerConfig, address: String) extends Runnable {

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[DataServerRunable])

  override def run(): Unit = {
    try {
      val requestManager = new RequestManager
      val in = new DataInputStream(s.getInputStream)
      val out = new DataOutputStream(s.getOutputStream)
      while (DataServer.isRunning) {
        requestManager.handler(in, out, conf, address)
      }
    } finally {
      DataServer.thMap.remove(threadIndex)
      Tools.tryBlock(s.close())
    }
  }


}

