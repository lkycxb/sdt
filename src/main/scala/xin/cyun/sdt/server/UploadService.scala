package xin.cyun.sdt.server

import java.io._

import org.slf4j.{Logger, LoggerFactory}
import xin.cyun.sdt.bean.ServerConfig
import xin.cyun.sdt.io.UploadInfoWritable

/**
  * Created by BING on 2018/10/23.
  */
class UploadService(in: DataInputStream, out: DataOutputStream, conf: ServerConfig) extends Service {

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[UploadService])

  override def run(): Unit = {
    val uploadInfo = new UploadInfoWritable()
    uploadInfo.readFields(in)
    upload(uploadInfo)
  }

  private def upload(f: UploadInfoWritable): Unit = {
    val start = System.currentTimeMillis()
    var curLen: Long = 0L
    val tempFile = new File(conf.dataPath, f.name + conf.dataTmpSuffix)
    if (tempFile.exists()) tempFile.delete()
    val formalFile = new File(conf.dataPath, f.name)
    val bos = new BufferedOutputStream(new FileOutputStream(tempFile))

    var buff = new Array[Byte](conf.bufferSize)
    while (curLen != f.len) {
      if ((curLen + conf.bufferSize) > f.len) {
        buff = new Array[Byte]((f.len - curLen).toInt)
      }
      //val buff = new Array[Byte](refReadLen)
      val readLen = in.read(buff)
      curLen += readLen
      bos.write(buff, 0, readLen)
    }
    bos.close()
    if (formalFile.exists()) formalFile.delete()
    tempFile.renameTo(formalFile)
    val elapsed = System.currentTimeMillis() - start
    logger.info("received " + f.name + ",len:" + curLen + ",elapsed:" + elapsed)
  }


}
