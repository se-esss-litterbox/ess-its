package se.esss.litterbox.its.toshibagwt.client.googleplots;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.LineChart;
import com.googlecode.gwt.charts.client.corechart.LineChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;


public class TimeLineChartPlotPanel extends HorizontalPanel
{
	private LineChart lineChart;
	private DataTable dataTable;
	private LineChartOptions options;
	private ChartLoader chartLoader;
	private ChartRunnable chartRunnable;
	private int numPts;
	private int numTraces;
	private String title;
	private String haxisLabel;
	private String yaxisLabel;
	private String[] legend;
	private String plotWidth;
	private String plotHeight;
	private boolean loaded = false;
	
	public int getNumPts() {return numPts;}
	public int getNumTraces() {return numTraces;}
	public String getTitle() {return title;}
	public String getHaxisLabel() {return haxisLabel;}
	public String getYaxisLabel() {return yaxisLabel;}
	public String[] getLegend() {return legend;}
	public String getPlotWidth() {return plotWidth;}
	public String getPlotHeight() {return plotHeight;}
	public boolean isLoaded() {return loaded;}


	public TimeLineChartPlotPanel(int numPts, int numTraces, String title, String haxisLabel, String yaxisLabel, String[] legend, String plotWidth, String plotHeight) 
	{
		loaded = false;
		this.numPts = numPts;
		this.numTraces = numTraces;
		this.title = title;
		this.haxisLabel = haxisLabel;
		this.yaxisLabel = yaxisLabel;
		this.plotWidth = plotWidth;
		this.plotHeight = plotHeight;
		this.legend = legend;
	}

	public void initialize() 
	{
		loaded = false;
		chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartRunnable = new ChartRunnable(this);
		chartLoader.loadApi(chartRunnable);
	}

	private void setup()
	{
		// Prepare the data
		dataTable = DataTable.create();
//		dataTable.addColumn(ColumnType.NUMBER, haxisLabel);
		dataTable.addColumn(ColumnType.NUMBER, haxisLabel);
		for (int ii = 0; ii < numTraces; ++ii) dataTable.addColumn(ColumnType.NUMBER, legend[ii]);
		dataTable.addRows(numPts);
		

		// Set options
		options = LineChartOptions.create();
		options.setBackgroundColor("#f0f0f0");
		options.setTitle(title);
		options.setHAxis(HAxis.create(haxisLabel));
		options.setVAxis(VAxis.create(yaxisLabel));

	}
	public void draw(double timeSec, double[] yaxisData) 
	{
		int irow = dataTable.getNumberOfRows();
		if (irow == numPts)
		{
			dataTable.removeRow(0);
			irow = irow - 1;
		}
		dataTable.addRow();
		dataTable.setValue(irow, 0, timeSec);
		for (int itrace = 0; itrace < numTraces; ++itrace)
		{
			dataTable.setValue(irow, itrace + 1, yaxisData[itrace]);
		}
		lineChart.draw(dataTable, options);
	}

	private static class ChartRunnable implements Runnable
	{
		TimeLineChartPlotPanel lineChartPlotPanel;
		private ChartRunnable(TimeLineChartPlotPanel lineChartPlotPanel)
		{
			this.lineChartPlotPanel = lineChartPlotPanel;
		}
		@Override
		public void run() 
		{
			lineChartPlotPanel.lineChart = new LineChart();
			lineChartPlotPanel.add(lineChartPlotPanel.lineChart);
			lineChartPlotPanel.setup();
			lineChartPlotPanel.lineChart.setWidth(lineChartPlotPanel.plotWidth);
			lineChartPlotPanel.lineChart.setHeight(lineChartPlotPanel.plotHeight);
			lineChartPlotPanel.loaded = true;
		}
		
	}
}
