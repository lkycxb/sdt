package xin.cyun.sdt.server

import java.io.{DataInputStream, DataOutputStream, EOFException}

import org.slf4j.{Logger, LoggerFactory}
import xin.cyun.sdt.bean.ServerConfig

/**
  * Created by BING on 2018/10/23.
  */
class RequestManager {

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[RequestManager])

  def handler(in: DataInputStream, out: DataOutputStream, conf: ServerConfig, address: String): Unit = {
    try {
      val service = getService(in, out, conf)
      service.run()
    } catch {
      case _: EOFException =>
        logger.info(address + " disconnected.")
      case e: Throwable =>
        logger.info("request error:" + e.getMessage)
        e.printStackTrace()

    }
  }

  private def getService(in: DataInputStream, out: DataOutputStream, conf: ServerConfig): Service = {
    val reqTypeInt = in.readInt()
    val reqType = REQUEST_TYPE.values.find(t => t.id == reqTypeInt).getOrElse(REQUEST_TYPE.DOWNLOAD)
    reqType match {
      case REQUEST_TYPE.UPLOAD => new UploadService(in, out, conf)

      case REQUEST_TYPE.DOWNLOAD => new DownloadService(in, out, conf)

      case _ => new NonService

    }
  }

}
