package com.example.demo.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery.SortClause;

public class QueryForm implements Serializable {

	private static final long serialVersionUID = 1L;

	private String query = "*:*";

	private String[] fQuery = null;

	private String beginDate;

	private String endDate;

	private List<SortClause> sorts;

	private Boolean dataImportStatus;

	/** 上漲/下跌/持平 狀態選項 */
	private String trendStatus;

	/** 上漲/下跌/持平 天數 */
	private Integer riseDays;

	/** 持平時波動範圍 */
	private Integer stableRange;

	/** 周六日修正天數 */
	private Integer offsetDays = 0;

	/** 最低收盤價 */
	private Double minClosingPrice;

	/** 最高收盤價 */
	private Double maxClosingPrice;

	/** 最小成交股數 */
	private Integer minDealStockAmount;

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String[] getfQuery() {
		return fQuery;
	}

	public void setfQuery(String[] fQuery) {
		this.fQuery = fQuery;
	}

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<SortClause> getSorts() {
		return sorts;
	}

	public void setSorts(List<SortClause> sorts) {
		this.sorts = sorts;
	}

	public Boolean getDataImportStatus() {
		return dataImportStatus;
	}

	public void setDataImportStatus(Boolean dataImportStatus) {
		this.dataImportStatus = dataImportStatus;
	}

	public String getTrendStatus() {
		return trendStatus;
	}

	public void setTrendStatus(String trendStatus) {
		this.trendStatus = trendStatus;
	}

	public Integer getRiseDays() {
		return riseDays;
	}

	public void setRiseDays(Integer riseDays) {
		this.riseDays = riseDays;
	}

	public Integer getOffsetDays() {
		return offsetDays;
	}

	public void setOffsetDays(Integer offsetDays) {
		this.offsetDays = offsetDays;
	}

	public Double getMinClosingPrice() {
		return minClosingPrice;
	}

	public void setMinClosingPrice(Double minClosingPrice) {
		this.minClosingPrice = minClosingPrice;
	}

	public Double getMaxClosingPrice() {
		return maxClosingPrice;
	}

	public void setMaxClosingPrice(Double maxClosingPrice) {
		this.maxClosingPrice = maxClosingPrice;
	}

	public Integer getMinDealStockAmount() {
		return minDealStockAmount;
	}

	public void setMinDealStockAmount(Integer minDealStockAmount) {
		this.minDealStockAmount = minDealStockAmount;
	}

	public Integer getStableRange() {
		return stableRange;
	}

	public void setStableRange(Integer stableRange) {
		this.stableRange = stableRange;
	}

}
