package org.roboutik.dao.impl.query

import org.apache.solr.client.solrj.SolrQuery
import org.apache.solr.client.solrj.SolrQuery.ORDER

/**
  * Created by Keech on 12/10/2016.
  */
class OrderQueryBuilder {
  private var _id: Option[String] = None
  private var orderId: Option[String] = None
  private var versionTimestamp: Option[VersionTimestampRange] = None
  private var tcid: Option[String] = None
  private var tradingSessionId: Option[Long] = None
  private var marketId: Option[Long] = None
  private var version: Option[Long] = None
  private var sortParameters: Option[SortParameters] = None
  private var documentRange: Option[DocumentRange] = None
  private var fieldList: Option[String] = None
  private var allFieldsFlag: Boolean = false
  val solrDocType = "\"order\""
  val defaultValue = "*"

  def setID(id: String): OrderQueryBuilder = {
    _id = Some(id); this
  }

  /* returning this to enable method chaining. */
  def setOrderId(oid: String): OrderQueryBuilder = {
    orderId = Some(oid); this
  }

  def setVersionTimestamp(timestampRange: VersionTimestampRange): OrderQueryBuilder = {
    versionTimestamp = Some(timestampRange); this
  }

  def setTcid(t: String): OrderQueryBuilder = {
    tcid = Some(t); this
  }

  def setTradingSession(tradingSess: Long): OrderQueryBuilder = {
    tradingSessionId = Some(tradingSess); this
  }

  /* returning this to enable method chaining. */
  def setMarketId(marketID: Long): OrderQueryBuilder = {
    marketId = Some(marketID); this
  }

  def setVersion(versn: Long): OrderQueryBuilder = {
    version = Some(versn); this
  }

  def setSort(s: SortParameters): OrderQueryBuilder = {
    sortParameters = Some(s); this
  }

  def setDocumentRange(dR: DocumentRange): OrderQueryBuilder = {
    documentRange = Some(dR); this
  }

  /* returning this to enable method chaining. */
  def setFieldList(fL: String): OrderQueryBuilder = {
    fieldList = Some(fL); this
  }

  def setAllFieldsFlag(star: Boolean): OrderQueryBuilder = {
    allFieldsFlag = star; this
  }

  def buildString() ={}

  def build():SolrQuery = {
    val q = new SolrQuery(if (!allFieldsFlag) {
      s"+type:$solrDocType +orderId:${orderId.getOrElse(defaultValue)} +tradingSession:${tradingSessionId.getOrElse(defaultValue)} " +
        s"+id:${_id.getOrElse(defaultValue)} +marketId_l:${marketId.getOrElse(defaultValue)} +version:${version.getOrElse(defaultValue)} " +
        s"+tcid:${tcid.getOrElse(defaultValue)}"
    } else {
      "*:*"
    }) /*+
        s"+versionTimestamp:[${params.versionTimestamp.getOrElse(VersionTimestampRange()).startTime} TO "+
        s"${params.versionTimestamp.getOrElse(VersionTimestampRange()).endTime}]")*/
    if (sortParameters.isDefined) {
      q.addSort("id", if (sortParameters.get.sortType == DESC) ORDER.desc else ORDER.asc)
    }
    if (documentRange.isDefined) {
      q.setRows(documentRange.get.numberOfRows).setStart(documentRange.get.startDocument)
    } else {
      q.setRows(Int.MaxValue)
    }
    if (versionTimestamp.isDefined) {
      q.addFilterQuery(s"versionTimestamp:[${versionTimestamp.get.startTime.toString} TO " +
        s"${versionTimestamp.get.endTime.toString}]")
    }
    if (fieldList.isDefined) {
      q.addField(fieldList.get)
    }
    q
  }
}