package xin.cyun.sdt.server

import java.io.{DataInputStream, DataOutputStream}

import xin.cyun.sdt.bean.ServerConfig

/**
  * Created by BING on 2018/10/23.
  */
class DownloadService(in: DataInputStream, out: DataOutputStream, conf: ServerConfig) extends Service {
  override def run(): Unit = {
    in.read()
  }
}
