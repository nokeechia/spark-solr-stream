package org.roboutik.dao.impl.query

/**
  * Created by Keech on 12/10/2016.
  */
trait DataParameters
case class VersionTimestampRange(startTime: Long =0L, endTime:Long = Long.MaxValue)
case class SortParameters(sortType: SortType, fieldId: String) {
  override def toString:String = s"sort=$fieldId ${sortType.toString}"
}
case class DocumentRange(startDocument: Int, numberOfRows: Int){
  override def toString: String = s"start=$startDocument rows=$numberOfRows"
}