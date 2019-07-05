package com.git.gdsbuilder.generalization;

import org.json.simple.JSONObject;

public class GeneralizationOption {

	public static String FIR = "0";
	public static String SEC = "1";

	public static String ELIMINATION = "Elimination";
	public static String SIMPLIFICATION = "Simplification";

	private static String NAME = "name";
	private static String TOLERANCE = "tolerance";
	private static String REPEAT = "repeat";
	private static String MERGE = "merge";

	private String num;
	private String name;
	private double tolerance;
	private int repeat;
	private boolean merge;

	public void createGeneraliationOpt(String num, JSONObject opt) {

		this.num = num;
		this.name = (String) opt.get(NAME);
		this.tolerance = Double.parseDouble(opt.get(TOLERANCE).toString());
		if (name.equals(SIMPLIFICATION)) {
			this.repeat = Integer.parseInt(opt.get(REPEAT).toString());
			this.merge = (boolean) opt.get(MERGE);
		}
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getTolerance() {
		return tolerance;
	}

	public void setTolerance(double tolerance) {
		this.tolerance = tolerance;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

}
