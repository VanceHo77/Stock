package com.example.demo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.bean.QueryForm;
import com.example.demo.bean.ResponseMessage;
import com.example.demo.model.StockSchema;
import com.example.demo.service.SolrService;
import com.example.demo.service.StockService;

/**
 * Handles requests for the application home page.
 */
@Controller
public class StockController {

	private static final Logger logger = LoggerFactory.getLogger(StockController.class);

	@Autowired
	StockService stockService;

	@Autowired
	SolrService solrkService;

	@RequestMapping(value = "/query/risingTrend")
	public String query(@ModelAttribute QueryForm form, Model model) {
		List<StockSchema> list = stockService.query(form);
		model.addAttribute("dataList", list);
		return "result";
	}

	@RequestMapping(value = "/do/dataImport")
	public @ResponseBody ResponseMessage doDataImport(@ModelAttribute QueryForm form, Model model) {
		return solrkService.addDocs(form.getDataImportStatus());
	}
}
