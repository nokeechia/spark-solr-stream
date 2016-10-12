package org.roboutik.dao.impl.query

/**
  * Created by Keech on 12/10/2016.
  */
trait SortType
case object ASC extends SortType {override def toString:String = "asc"}
case object DESC extends SortType {override def toString:String = "desc"}