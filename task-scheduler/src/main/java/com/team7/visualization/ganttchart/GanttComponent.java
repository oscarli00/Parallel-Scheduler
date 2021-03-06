package com.team7.visualization.ganttchart;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is created to provide a Gantt Chart implementation to display the current schedule.
 * Inspiration for creating this class has come from the website below:
 * https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch
 *
 * Note that all public contributions on StackOverflow are automatically registered under Creative
 * Commons Attribution-ShareAlike licence.
 * https://stackoverflow.com/help/licensing
 * https://creativecommons.org/licenses/by-sa/4.0/
 */
public class GanttComponent<X,Y> extends XYChart<X,Y> {
    public static class ExtraData {
        public long length;
        public String styleClass;
        public String label;

        public ExtraData(long length, String styleClass, String label) {
            super();
            this.length = length;
            this.styleClass = styleClass;
            this.label = label;
        }

        public long getLength() {
            return length;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public String getStyleClass() {
            return styleClass;
        }

        public void setStyleClass(String styleClass) {
            this.styleClass = styleClass;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    private double blockHeight = 10;

    public GanttComponent(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    public GanttComponent(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    @Override
    protected void layoutPlotChildren() {
        for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex++) {
            Series<X,Y> series = getData().get(seriesIndex);

            Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
            while(iter.hasNext()) {
                Data<X,Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());

                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }

                Node block = item.getNode();
                Rectangle ellipse;

                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane)item.getNode();

                        if (region.getShape() == null) {
                            ellipse = new Rectangle(getLength(item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            ellipse = (Rectangle)region.getShape();
                        } else {
                            return;
                        }

                        ellipse.setWidth( getLength( item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
                        ellipse.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;

                        region.setShape(null);
                        region.setShape(ellipse);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        // This creates the label for the task name, and also centers it.
                        Label label = new Label(getLabel(item.getExtraValue()));
                        label.setFont(new Font(16));
                        label.setStyle("-fx-text-fill: #ffffff");
                        label.setPadding(new Insets(ellipse.getHeight() / 1.3, 0, 0, ellipse.getWidth() / 1.1));
                        BorderPane bp = new BorderPane(label);
                        bp.setPrefWidth(250);
                        bp.setMinWidth(250);
                        region.getChildren().add(bp);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    @Override
    protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    @Override
    protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    @Override
    protected void dataItemChanged(Data<X, Y> item) {
        throw new RuntimeException();
    }

    @Override
    protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
        for (int counter = 0; counter < series.getData().size(); counter++) {
            Data<X,Y> item = series.getData().get(counter);
            Node container = createContainer(series, seriesIndex, item, counter);
            getPlotChildren().add(container);
        }
    }

    @Override
    protected void seriesRemoved(final Series<X,Y> series) {
        for (XYChart.Data<X,Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }

        removeSeriesFromDisplay(series);
    }

    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add(getStyleClass(item.getExtraValue()));
        return container;
    }

    @Override protected void updateAxisRange() {
        final Axis<X> xAxis = getXAxis();
        final Axis<Y> yAxis = getYAxis();
        List<X> xData = xAxis.isAutoRanging() ? new ArrayList<>() : null;
        List<Y> yData = yAxis.isAutoRanging() ? new ArrayList<>() : null;

        if(xData != null || yData != null) {
            for(Series<X,Y> series : getData()) {
                for(Data<X,Y> data: series.getData()) {
                    if(xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xAxis.toRealValue(xAxis.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if(yData != null){
                        yData.add(data.getYValue());
                    }
                }
            }

            if(xData != null)
                xAxis.invalidateRange(xData);

            if(yData != null)
                yAxis.invalidateRange(yData);
        }
    }

    private static String getStyleClass (Object obj) {
        return ((ExtraData) obj).getStyleClass();
    }

    private static double getLength (Object obj) {
        return ((ExtraData) obj).getLength();
    }

    public static String getLabel (Object obj) {
        return ((ExtraData) obj).getLabel();
    }

    public double getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight( double blockHeight) {
        this.blockHeight = blockHeight;
    }
}