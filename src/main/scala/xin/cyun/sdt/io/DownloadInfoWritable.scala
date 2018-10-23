package xin.cyun.sdt.io

import java.io.{DataInput, DataOutput}

/**
  * Created by BING on 2018/10/23.
  */
class DownloadInfoWritable extends Writable {
  val reqType = xin.cyun.sdt.server.REQUEST_TYPE.DOWNLOAD.id
  var name: String = _
  var len: Long = 0L

  override def write(out: DataOutput): this.type = {
    out.writeLong(len)
    out.writeUTF(name)
    this
  }

  override def readFields(in: DataInput): this.type = {
    len = in.readLong()
    name = in.readUTF()
    this
  }
}
