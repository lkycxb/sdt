package xin.cyun.sdt

import junit.framework.TestCase
import xin.cyun.sdt.client.DataClient

/**
  * Created by BING on 2018/10/18.
  */
class TestClient extends TestCase {

  def testClient(): Unit = {
    val args = Array[String]("-p", "src/main/resources/", "-f", "src/main/resources/client.conf")
    DataClient.main(args)
  }
}
