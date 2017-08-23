package com.example.demo.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.bean.QueryForm;
import com.example.demo.model.StockSchema;
import com.example.demo.model.TrendStatus;
import com.example.demo.util.DateUtil;

@Service
public class StockService {

	@Autowired
	SolrService solrService;

	static final String BASE_PATH = "D:/Project/tsec-master/";

	static final String DATA_DIR = BASE_PATH + "data";

	static final String MAPPING_FILE_PATH = BASE_PATH + "股票名稱代碼對照表.csv";

	/**
	 * 因股票名稱不是太重要就不解析了
	 */
	@Deprecated
	static final String MAPPING_URL = "http://isin.twse.com.tw/isin/C_public.jsp?strMode=2";

	static Map<String, String> StockMappings;

	private static String lastUpdated;

	static {
		try {
			StockMappings = readMapping();
			lastUpdated = Files.lines(Paths.get("./dataImport.txt")).findFirst().get();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String, String> readMapping() throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		Path path = Paths.get(MAPPING_FILE_PATH);
		if (Files.notExists(path)) {
			throw new FileNotFoundException();
		}

		Files.lines(path, Charset.forName("utf-8")).forEach(line -> addMapping(map, line));

		System.out.println("ReadMapping done, size:" + map.size());
		return map;
	}

	private static void addMapping(Map<String, String> map, String line) {
		String[] ary = line.split(",");
		if (ary.length > 7) {
			for (int i = 0; i < ary.length; i++) {
				if (!ary[i].isEmpty()) {
					map.put(ary[i], ary[++i]);
				}
			}
		}
	}

	public List<StockSchema> readStockData(boolean doFull) throws IOException {
		List<StockSchema> list = new ArrayList<StockSchema>();
		Files.newDirectoryStream(Paths.get(DATA_DIR), path -> path.toFile().isFile())
				.forEach(f -> doParserFile(list, f, doFull));
		System.out.println("Total size:" + list.size());
		return list;
	}

	private static void doParserFile(List<StockSchema> list, Path f, boolean doFull) {
		try {
			Files.lines(f, Charset.forName("Cp1252"))
					.forEach(line -> setStock(list, line, f.getFileName().toString(), doFull));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void setStock(List<StockSchema> list, String line, String stockId, boolean doFull) {
		String[] ary = line.split(",");
		StockSchema stock = new StockSchema();
		if (ary.length > 8) {
			String date = ary[0];
			// 只取大於或等於當天的資料
			try {
				if (doDelta(date) || doFull) {
					stockId = stockId.split("\\.")[0];
					String name = StockMappings.get(stockId);
					name = name == null ? stockId : name;
					String id = stockId + "_" + date;

					stock.setId(id);
					stock.setStockId(stockId);
					stock.setName(name);
					stock.setDate(DateUtil.stringToDate(date));
					stock.setDealStockAmount(parseInt(ary[1]));
					stock.setDealMoneyAmount(parseInt(ary[2]));
					stock.setOpeningPrice(parseDouble(ary[3]));
					stock.setHighestPrice(parseDouble(ary[4]));
					stock.setLowestPrice(parseDouble(ary[5]));
					stock.setClosingPrice(parseDouble(ary[6]));
					stock.setDifferencePrice(parseDouble(ary[7]));
					stock.setTransactionsNumber(parseInt(ary[8]));
					list.add(stock);
					System.out.println("Set stock:" + stock.getId() + ", date:" + stock.getDate());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	private static boolean doDelta(String csvDate) throws ParseException {
		Instant i1 = DateUtil.parserDateText(lastUpdated);
		Instant i2 = DateUtil.parserDateText(csvDate);
		boolean b1 = i1.isBefore(i2);
		boolean b2 = i1.equals(i2);
		return b1 || b2;
	}

	private static int parseInt(String i) {
		try {
			return Integer.parseInt(i);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static double parseDouble(String d) {
		try {
			return Double.parseDouble(d);
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public List<StockSchema> query(QueryForm form) {

		String[] fqAry = new String[3];
		fqAry[0] = "date_dt:[NOW-" + (form.getRiseDays() + form.getOffsetDays()) + "DAY TO NOW]";// 抓前N天的資料
		fqAry[1] = "closingPrice_d:[" + form.getMinClosingPrice() + " TO " + form.getMaxClosingPrice() + "]";
		fqAry[2] = "dealStockAmount_i:[" + form.getMinDealStockAmount() + " TO *]";

		form.setfQuery(fqAry);
		List<SortClause> sortList = new ArrayList<>();
		sortList.add(SortClause.asc("stockId_s"));
		sortList.add(SortClause.asc("date_dt"));
		form.setSorts(sortList);

		List<StockSchema> stockList = solrService.query(form);

		StockSchema beforeStock = null;
		// 上漲/跌/持平次數
		int riseCount = 0;
		// 符合條件
		List<String> hitStockStrs = new ArrayList<>();
		List<StockSchema> hitStocks = new ArrayList<>();

		if (stockList != null && stockList.size() > 0) {
			for (StockSchema stock : stockList) {
				double curPrice = stock.getClosingPrice();
				if (stock.getStockId().length() > 4) {// 只挑數字長度4以內的，避免選到一些投資組合的股票
					continue;
				} else if (curPrice > form.getMaxClosingPrice()) {
					continue;
				} else if (beforeStock == null || !beforeStock.getStockId().equals(stock.getStockId())) {
					if (riseCount == (form.getRiseDays() - 1)) {
						// 符合條件的股票
						System.out.println(beforeStock.toString());
						hitStockStrs.add(beforeStock.toString());
						hitStocks.add(beforeStock);
					}
					beforeStock = stock;
					riseCount = 0;
					continue;
				} else if (beforeStock.getStockId().equals(stock.getStockId())) {
					// 上漲/下跌/持平趨勢選擇
					if (TrendStatus.上漲.name().equals(form.getTrendStatus())) {
						if (curPrice >= beforeStock.getClosingPrice()) {
							beforeStock = stock;
							riseCount++;
							continue;
						}
					} else if (TrendStatus.下跌.name().equals(form.getTrendStatus())) {
						if (curPrice <= beforeStock.getClosingPrice()) {
							beforeStock = stock;
							riseCount++;
							continue;
						}
					} else if (TrendStatus.持平.name().equals(form.getTrendStatus())) {
						double percent = beforeStock.getClosingPrice() * 0.01 * form.getStableRange();
						double lower = curPrice - percent;
						double upper = curPrice + percent;
						if ((lower <= curPrice) && (curPrice) <= upper) {
							beforeStock = stock;
							riseCount++;
							continue;
						}
					}
				}

			}
			if (riseCount == (form.getRiseDays() - 1)) {
				// 符合條件的股票
				System.out.println(beforeStock.toString());
				hitStockStrs.add(beforeStock.toString());
				hitStocks.add(beforeStock);
			}
			// 依收盤價由低到高排序
			hitStocks = hitStocks.stream().sorted((p1, p2) -> p1.getClosingPrice().compareTo(p2.getClosingPrice()))
					.collect(Collectors.toList());
			// 匯出
			try {
				Files.write(
						Paths.get("C:\\Users\\Vance\\Desktop\\stock-filter-riseDay(" + form.getRiseDays() + ").txt"),
						hitStockStrs, Charset.forName("UTF-8"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Filter result records:" + hitStocks.size());
		return hitStocks;
	}

	public void chooseThrend(QueryForm form, double curPrice, double beforeClosingPrice) {
		if (TrendStatus.上漲.name().equals(form.getTrendStatus())) {

		} else if (TrendStatus.下跌.name().equals(form.getTrendStatus())) {

		} else if (TrendStatus.持平.name().equals(form.getTrendStatus())) {

		}

	}

	public static void main(String... strings) throws IOException, ParseException, InterruptedException {
		// StockService.readStockData();
		System.out.println(doDelta("106/08/23"));

	}
}
