package xin.cyun.sdt.io

import java.io.{DataInput, DataOutput}

/**
  * Created by BING on 2018/10/19.
  */
class UploadInfoWritable extends Writable {
  val reqType = xin.cyun.sdt.server.REQUEST_TYPE.UPLOAD.id
  var name: String = _
  var len: Long = 0L
  var compressed: Int = 1 //1 is compress
  var compressCodec:String="zip"

  override def write(out: DataOutput): this.type = {
    out.writeLong(len)
    out.writeUTF(name)
    out.writeInt(compressed)
    out.writeUTF(compressCodec)
    this
  }

  override def readFields(in: DataInput): this.type = {
    len = in.readLong()
    name = in.readUTF()
    compressed = in.readInt()
    compressCodec = in.readUTF()
    this
  }
}
