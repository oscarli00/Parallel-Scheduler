package com.team7.visualization;

import com.team7.model.Graph;
import com.team7.model.Schedule;
import com.team7.model.Task;
import com.team7.parsing.Config;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import static com.team7.visualization.ScheduleRepresentation.ExtraData;

import java.util.*;

public class VisualizationDriver extends Application {
    private static Config _config;
    private static Schedule _schedule;
    private static Graph _graph;

    public static void main(Schedule schedule, Graph graph, Config config) {
        _config = config;
        _schedule = schedule;
        _graph = graph;
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println(_schedule.toString());

        // Creating and configuring the chart.
        final NumberAxis xAxis = new NumberAxis();
        final CategoryAxis yAxis = new CategoryAxis();
        final ScheduleRepresentation<Number, String> chart = new ScheduleRepresentation<>(xAxis, yAxis);
        chart.setTitle("Schedule Visualization");
        chart.setLegendVisible(false);
        chart.setBlockHeight(10);

        // Configuring the axis.
        xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);
        yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);

        // Registering the processors.
        List<String> processorTitles = new ArrayList<>();
        List<XYChart.Series> processorSeries = new ArrayList<>();
        for (int counter = 0; counter < _config.getNumOfProcessors(); counter++) {
            processorTitles.add("Processor " + counter);
            processorSeries.add(new XYChart.Series());
        }

        // Inserting each task into the graph, by iterating through them.
        for (Task t : _graph.getNodes()) {
            String machine = String.valueOf(_schedule.getTaskProcessor(t));
            int startTime = _schedule.getTaskStartTime(t);
            int length = t.getWeight();
            XYChart.Series series = processorSeries.get(_schedule.getTaskProcessor(t));
            series.getData().add(new XYChart.Data(startTime, machine, new ExtraData(length, "status-grey")));
        }

        // Adding each series to the final graph.
        for (int counter = 0; counter < processorSeries.size(); counter++) {
            chart.getData().add(counter, processorSeries.get(counter));
        }

        // Setting properties for the primary stage, and showing it.
        chart.getStylesheets().add(getClass().getResource("/stylesheets/schedule-representation.css").toExternalForm());
        primaryStage.setTitle("The Marauders: Task Visualization");
        primaryStage.setScene(new Scene(chart, 620, 350));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
