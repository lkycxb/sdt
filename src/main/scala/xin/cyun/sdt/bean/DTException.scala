package xin.cyun.sdt.bean

/**
  * Created by BING on 2018/10/18.
  */
class DTException(msg: String, th: Throwable) extends Exception {
  def this(msg: String) {
    this(msg, null)
  }

  def this(th: Throwable) {
    this(th.getMessage, th)
  }
}
