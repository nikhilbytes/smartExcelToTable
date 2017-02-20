package com.exceltotable.smartcomparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class SmartComparator {

	public List<String> excelcolumns;
	public List<String> tablecolumns;
	public List<String> tablecolumnsWithLowerCase;
	public Map<String, String> explicitMappedcolumns;
	public Map<String, String> resultmap;

	public SmartComparator(List tablecolumns, List excelcolumns,
			Map explicitMappedcolumns) {
		this.excelcolumns = excelcolumns;
		this.explicitMappedcolumns = explicitMappedcolumns;
		this.tablecolumns = tablecolumns;
		this.resultmap = new LinkedHashMap<String, String>();
		this.tablecolumnsWithLowerCase = new ArrayList<>();
	}

	public Map findbestMatchColumn() throws Exception {
		InitilizeResultMapAndTablelist();
		compareExplicitMapping();
		compareEqualString();
		findBestMatchUsingJaroWinklerDistance();

		return resultmap;
	}

	public void compareEqualString() {
		ListIterator<String> iterator = excelcolumns.listIterator();
		while (iterator.hasNext()) {
			String temp = iterator.next();
			String check = temp.toLowerCase();
			int i = tablecolumnsWithLowerCase.indexOf(temp.toLowerCase());
			if (tablecolumnsWithLowerCase.indexOf(temp.toLowerCase()) != -1) {
				resultmap.put(temp, tablecolumns.get(i));
				iterator.remove();
				// tablecolumnsWithLowerCase.remove(temp);
			}

		}
	}

	public void compareExplicitMapping() throws Exception {

		if (explicitMappedcolumns != null && excelcolumns.size() != 0) {
			resultmap.putAll(explicitMappedcolumns);
			for (Entry<String, String> entry : explicitMappedcolumns.entrySet()) {
				String temp = entry.getValue();
				if (tablecolumns.indexOf(temp) != -1) {
					excelcolumns.remove(entry.getKey());
					// //tablecolumnsWithLowerCase.remove(entry.getKey().toLowerCase());
				} else
					throw new Exception("Column Not Found: "
							+ entry.getValue().toString());

			}
		}
	}

	public void findBestMatchUsingJaroWinklerDistance() {
		Map holdDistance = new TreeMap<Double, List<String>>(
				Collections.reverseOrder());
		double tempForeachDistance = 0.0;
		if (excelcolumns.size() != 0) {
			for (Entry<String, String> entry : resultmap.entrySet()) {
				if (!entry.getValue().equals("")) {
					tablecolumns.remove(tablecolumns.indexOf(entry.getValue()));
					tablecolumnsWithLowerCase.remove(tablecolumnsWithLowerCase
							.indexOf(entry.getValue().toLowerCase()));
				}
			}
			ListIterator<String> iterator = excelcolumns.listIterator();
			while (iterator.hasNext()) {
				String temp = iterator.next();
				for (String item : tablecolumnsWithLowerCase) {
					System.out.println("Distance: "
							+ temp
							+ "&"
							+ item
							+ " :"
							+ StringUtils.getJaroWinklerDistance(item,
									temp.toLowerCase()));
					if ((tempForeachDistance = StringUtils
							.getJaroWinklerDistance(item, temp.toLowerCase())) >= 0.6) {
						List temp1 = new ArrayList<String>();
						temp1.add(temp);
						temp1.add(item);
						holdDistance.put(tempForeachDistance, temp1);

					}

				}
				System.out.println("hiii");
				List hh = (List) ((Entry) (holdDistance.entrySet().iterator()
						.next())).getValue();
				temp = hh.get(0).toString();
				String item = hh.get(1).toString();
				;
				resultmap.put(temp, tablecolumns.get(tablecolumnsWithLowerCase
						.indexOf(item)));
				iterator.remove();
				tablecolumnsWithLowerCase.remove(temp.toLowerCase());
				tablecolumns.remove(temp);
				holdDistance.clear();
			}
		}
		System.out.println("done");
	}

	public void InitilizeResultMapAndTablelist() {
		for (String excelcolumn : excelcolumns) {
			resultmap.put(excelcolumn, "");
		}
		ListIterator<String> iterator = tablecolumns.listIterator();
		while (iterator.hasNext()) {
			tablecolumnsWithLowerCase.add(iterator.next().toLowerCase());
		}
	}

}
