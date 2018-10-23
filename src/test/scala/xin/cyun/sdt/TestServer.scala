package xin.cyun.sdt

import junit.framework.TestCase
import xin.cyun.sdt.server.DataServer

/**
  * Created by BING on 2018/10/18.
  */
class TestServer extends TestCase {

  def testServer(): Unit = {
    val args = Array[String]("-p", "src/main/resources/", "-f", "src/main/resources/server.conf")
    DataServer.main(args)
  }

}
