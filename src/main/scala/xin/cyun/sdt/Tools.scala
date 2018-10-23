package xin.cyun.sdt

import java.io.EOFException

import org.apache.commons.cli.{HelpFormatter, Options}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by BING on 2018/10/18.
  */
object Tools {

  def runThread(block: => Unit): Unit = {
    new Thread(new Runnable {
      override def run(): Unit = block
    }).start()
  }

  def tryBlock(block: => Unit): Unit = {
    try {
      block
    } catch {
      case _: Throwable =>
    }
  }


  def toInt(readBuffer: Array[Byte]): Int = {
    val ch1: Int = readBuffer(0)
    val ch2: Int = readBuffer(1)
    val ch3: Int = readBuffer(2)
    val ch4: Int = readBuffer(3)
    if ((ch1 | ch2 | ch3 | ch4) < 0)
      throw new EOFException()
    ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0))
  }

  def toLong(readBuffer: Array[Byte]): Long = {
    ((readBuffer(0).toLong << 56) +
      ((readBuffer(1) & 255).toLong << 48) +
      ((readBuffer(2) & 255).toLong << 40) +
      ((readBuffer(3) & 255).toLong << 32) +
      ((readBuffer(4) & 255).toLong << 24) +
      ((readBuffer(5) & 255).toLong << 16) +
      ((readBuffer(6) & 255).toLong << 8) +
      ((readBuffer(7) & 255).toLong << 0))
  }

  def toString(readBuffer: Array[Byte]): String = new String(readBuffer)


  def toBytes(v: Int): Array[Byte] = {
    val writeBuffer = new ArrayBuffer[Byte](4)
    writeBuffer += ((v >>> 24) & 0xFF).toByte
    writeBuffer += ((v >>> 16) & 0xFF).toByte
    writeBuffer += ((v >>> 8) & 0xFF).toByte
    writeBuffer += ((v >>> 0) & 0xFF).toByte
    writeBuffer.toArray
  }

  def toBytes(v: Long): Array[Byte] = {
    val writeBuffer = new ArrayBuffer[Byte](8)
    writeBuffer += (v >>> 56).toByte
    writeBuffer += (v >>> 48).toByte
    writeBuffer += (v >>> 40).toByte
    writeBuffer += (v >>> 32).toByte
    writeBuffer += (v >>> 24).toByte
    writeBuffer += (v >>> 16).toByte
    writeBuffer += (v >>> 8).toByte
    writeBuffer += (v >>> 0).toByte
    writeBuffer.toArray
  }

  def toBytes(v: String): Array[Byte] = v.getBytes(CommonConfigs.CHARSET_UTF8)

  private val checkPathNameArr = Array(
    "conf",
    "config",
    "/etc",
    "src/main/resources",
    "src/test/resources"
  )

  def getFile(name: String, envConfigName: String = ""): Option[java.io.File] = {
    var f = new java.io.File(name)
    if (f.exists()) {
      return Some(f)
    }
    if (envConfigName != "") {
      val configFile = System.getProperty(envConfigName)
      if (null != configFile && configFile.nonEmpty) {
        f = new java.io.File(configFile)
        if (f.exists()) {
          return Some(f)
        }
      }
    }

    val cur = System.getProperty("user.dir")
    f = new java.io.File(cur, name)
    if (f.exists()) {
      return Some(f)
    }
    for (p <- checkPathNameArr) {
      if (p.startsWith("/")) {
        f = new java.io.File(p, name)
      } else {
        f = new java.io.File(cur + java.io.File.separator + p, name)
      }
      if (f.exists()) {
        return Some(f)
      }
    }
    None
  }

  def getFileAsStream(name: String, envConfigName: String = ""): Option[java.io.InputStream] = {
    val f = getFile(name, envConfigName)
    if (f.isDefined) {
      println(s"find file:$name,path:${f.get.getAbsolutePath}")
      return Some(new java.io.FileInputStream(f.get))
    }
    try {
      println(s"load file:$name from classpath.")
      val is = getClass.getResourceAsStream(name)
      if (is == null) {
        return None
      }
      return Some(is)
    } catch {
      case e: Throwable => println("getFileAsStream error," + e)
    }
    None
  }

  def printHelp(options: Options): Unit = {
    val hf = new HelpFormatter()
    hf.setWidth(110)
    hf.printHelp("DataServer", options, true)
  }

}
