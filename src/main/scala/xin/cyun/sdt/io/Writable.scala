package xin.cyun.sdt.io

import java.io.{DataInput, DataOutput, IOException}

/**
  * Created by BING on 2018/10/18.
  */
trait Writable {
  @throws[IOException]
  def write(out: DataOutput): this.type

  @throws[IOException]
  def readFields(in: DataInput): this.type
}
