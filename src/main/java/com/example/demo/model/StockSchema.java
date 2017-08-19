package com.example.demo.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class StockSchema implements Serializable {

	private static final long serialVersionUID = 3195319209729967426L;

	/**
	 * ID
	 */
	@Field(value = "id")
	private String id;
	/**
	 * 股票代碼
	 */
	@Field(value = "stockId_s")
	private String stockId;
	/**
	 * 股票名稱
	 */
	@Field(value = "name_txt_cjk")
	private String name;
	/*
	 * 交易日期
	 */
	@Field(value = "date_dt")
	private Date date;
	/*
	 * 成交股數
	 */
	@Field(value = "dealStockAmount_i")
	private Integer dealStockAmount;
	/*
	 * 成交金額
	 */
	@Field(value = "dealMoneyAmount_i")
	private Integer dealMoneyAmount;
	/*
	 * 開盤價
	 */
	@Field(value = "openingPrice_d")
	private Double openingPrice;
	/*
	 * 最高價
	 */
	@Field(value = "highestPrice_d")
	private Double highestPrice;
	/*
	 * 最低價
	 */
	@Field(value = "lowestPrice_d")
	private Double lowestPrice;
	/*
	 * 收盤價
	 */
	@Field(value = "closingPrice_d")
	private Double closingPrice;
	/*
	 * 漲跌價差
	 */
	@Field(value = "differencePrice_d")
	private Double differencePrice;
	/*
	 * 成交筆數
	 */
	@Field(value = "transactionsNumber_i")
	private Integer transactionsNumber;

	@Override
	public String toString() {
		return "Stock [id=" + id + ", stockId=" + stockId + ", name=" + name + ", date=" + date + ", dealStockAmount="
				+ dealStockAmount + ", dealMoneyAmount=" + dealMoneyAmount + ", openingPrice=" + openingPrice
				+ ", highestPrice=" + highestPrice + ", lowestPrice=" + lowestPrice + ", closingPrice=" + closingPrice
				+ ", differencePrice=" + differencePrice + ", transactionsNumber=" + transactionsNumber + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getDealStockAmount() {
		return dealStockAmount;
	}

	public void setDealStockAmount(Integer dealStockAmount) {
		this.dealStockAmount = dealStockAmount;
	}

	public Integer getDealMoneyAmount() {
		return dealMoneyAmount;
	}

	public void setDealMoneyAmount(Integer dealMoneyAmount) {
		this.dealMoneyAmount = dealMoneyAmount;
	}

	public Double getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(Double openingPrice) {
		this.openingPrice = openingPrice;
	}

	public Double getHighestPrice() {
		return highestPrice;
	}

	public void setHighestPrice(Double highestPrice) {
		this.highestPrice = highestPrice;
	}

	public Double getLowestPrice() {
		return lowestPrice;
	}

	public void setLowestPrice(Double lowestPrice) {
		this.lowestPrice = lowestPrice;
	}

	public Double getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(Double closingPrice) {
		this.closingPrice = closingPrice;
	}

	public Double getDifferencePrice() {
		return differencePrice;
	}

	public void setDifferencePrice(Double differencePrice) {
		this.differencePrice = differencePrice;
	}

	public Integer getTransactionsNumber() {
		return transactionsNumber;
	}

	public void setTransactionsNumber(Integer transactionsNumber) {
		this.transactionsNumber = transactionsNumber;
	}

}
