package com.example.demo.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.bean.QueryForm;
import com.example.demo.bean.ResponseMessage;
import com.example.demo.model.StockSchema;

@Service
public class SolrService {

	static final String SOLR_URL = "http://localhost:8983/solr/core0";

	static final String SOLR_PING_URL = SOLR_URL + "/admin/ping";

	static final String DATE_FORMAT = "%s/%s/%s";

	@Autowired
	StockService stockService;

	public List<StockSchema> query(QueryForm form) {
		List<StockSchema> list = new ArrayList<StockSchema>();

		HttpSolrClient client = new HttpSolrClient(SOLR_URL);
		client.setConnectionTimeout(5000);

		SolrQuery query = new SolrQuery();
		query.setRows(2147483647);
		query.setQuery(form.getQuery().trim());
		if (form.getfQuery() != null) {
			query.setFilterQueries(form.getfQuery());
		}
		if (form.getSorts() != null) {
			query.setSorts(form.getSorts());
		}
		System.out.println("Query string:" + query.toString());
		try {
			QueryResponse response = client.query(query);
			SolrDocumentList docs = response.getResults();
			DocumentObjectBinder binder = new DocumentObjectBinder();
			list = binder.getBeans(StockSchema.class, docs);
			client.close();
			System.out.println("Find:" + response.getResults().getNumFound());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	@SuppressWarnings("deprecation")
	public ResponseMessage addDocs(boolean doFull) {
		ResponseMessage rpsMsg = new ResponseMessage();
		long t1 = System.currentTimeMillis();
		long t2;
		try {
			// 先檢查有沒有開啟solr
			URL url = new URL(SOLR_PING_URL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			int code = connection.getResponseCode();
			if (code != 200) {
				rpsMsg.setIsSuccess(false);
				rpsMsg.setMessage("請開啟solr");
				return rpsMsg;
			}

			Collection<StockSchema> docs = stockService.readStockData(doFull);

			if (docs != null && docs.size() > 0) {
				HttpSolrClient client = new HttpSolrClient(SOLR_URL);
				int maxRows = 10000;
				BigDecimal total = new BigDecimal(String.valueOf(docs.size()));
				BigDecimal div = new BigDecimal(String.valueOf(maxRows));
				int loopCount = ((total.divide(div, 0, BigDecimal.ROUND_UP)).toBigInteger()).intValue();

				for (int i = 0; i < loopCount; i++) {
					try {
						Collection<StockSchema> subDocs = docs.parallelStream().skip(i * maxRows).limit(maxRows)
								.collect(Collectors.toList());
						client.addBeans(subDocs);
						client.optimize();
						client.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("Solr addDocs round:" + (i + 1) + "/" + loopCount);
				}
				client.close();
				docs = null;
			}
			t2 = System.currentTimeMillis();
			saveImportDate();
			System.out.println("Solr addDocs done, cost time: " + (double) ((t2 - t1) / 1000) + " sec.");
		} catch (Exception e) {
			System.out.println(e);
			rpsMsg.setIsSuccess(false);
			return rpsMsg;
		}
		rpsMsg.setIsSuccess(true);
		return rpsMsg;
	}

	/**
	 * 儲存index匯入日期<br>
	 * 1. 儲存至此專案<br>
	 * 2. 儲存至python專案，供爬蟲往回爬至指定日期
	 */
	private void saveImportDate() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int year = c.get(Calendar.YEAR);
		int chYear = c.get(Calendar.YEAR) - 1911;
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		// 因六日休市，故若六日爬資料的話，則import date設定為禮拜五當天
		int week = c.get(Calendar.DAY_OF_WEEK);// 星期六=7,星期日=1
		if (week == 7)
			day -= 1;
		if (week == 1)
			day -= 2;

		String yearText = String.valueOf(year);
		String chYearText = String.valueOf(chYear);
		String monthText = month < 10 ? "0" + month : String.valueOf(month);
		String dayText1 = day < 10 ? "0" + day : String.valueOf(day);
		String dayText2 = day < 10 ? "0" + day : String.valueOf(day);

		List<String> line1 = new ArrayList<String>();
		line1.add(String.format(DATE_FORMAT, chYearText, monthText, dayText1));
		List<String> line2 = new ArrayList<String>();
		line2.add(String.format(DATE_FORMAT, yearText, monthText, dayText2));
		try {
			Files.write(Paths.get("./dataImport.txt"), line1);
			Files.write(Paths.get("D:\\Project\\tsec-master\\dataImport.txt"), line2);
			System.out.println("Save import date done.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean delAllDocs() {
		try {
			HttpSolrClient client = new HttpSolrClient(SOLR_URL);
			client.deleteByQuery("*:*");
			client.commit();
			client.close();
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		return true;
	}

	public static void main(String[] a) {
		// System.out.println("Add docs is " + addDocs(false));
		// System.out.println("Del docs is " + delAllDocs() + ".");

	}
}
