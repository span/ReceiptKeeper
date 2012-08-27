package net.danielkvist.receipttracker.content;

import net.danielkvist.receipttracker.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.AbstractChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.content.Context;
import android.graphics.Color;

public class PieChartView extends GraphicalView
{

	public static final int COLOR_GREEN = Color.parseColor("#62c51a");
	public static final int COLOR_ORANGE = Color.parseColor("#ff6c0a");
	public static final int COLOR_BLUE = Color.parseColor("#23bae9");

	private PieChartView(Context context, AbstractChart arg1)
	{
		super(context, arg1);
	}

	public static GraphicalView getNewInstance(Context context, int income, int costs)
	{
		return ChartFactory.getPieChartView(context, getDataSet(context, income, costs), getRenderer());
	}

	private static DefaultRenderer getRenderer()
	{
		int[] colors = new int[] { COLOR_GREEN, COLOR_ORANGE, COLOR_BLUE };

		DefaultRenderer defaultRenderer = new DefaultRenderer();
		for (int color : colors)
		{
			SimpleSeriesRenderer simpleRenderer = new SimpleSeriesRenderer();
			simpleRenderer.setColor(color);
			defaultRenderer.addSeriesRenderer(simpleRenderer);
		}
		defaultRenderer.setShowLabels(false);
//		defaultRenderer.setLegendTextSize(20.0f);
		defaultRenderer.setShowLegend(false);
		return defaultRenderer;
	}

	private static CategorySeries getDataSet(Context context, int income, int costs)
	{
		CategorySeries series = new CategorySeries("Chart");
		series.add(context.getString(R.string.income), income);
		series.add(context.getString(R.string.costs), costs);
		series.add(context.getString(R.string.total), income - costs);
		return series;
	}
}
