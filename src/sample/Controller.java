package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Controller {
    @FXML
    private ListView cvs_lst;
    @FXML
    private ScrollPane content_pane;
    @FXML
    private Label hk_sign_label, sk_sign_label, hk_matches_label, sk_matches_label, d_match_label;
    @FXML
    private Slider hk_slider, sk_slider;
    @FXML
    private TextArea hk_keys, sk_keys;

    sample.ReadPdf readPdf = new sample.ReadPdf();

    protected int hk_value = 10;
    protected int sk_value = 10;
    protected String [] hk_arr;
    protected String [] sk_arr;
    protected List<File> files_list;
    protected List<Cv> cv_list;

    public void openFc(){
        FileChooser fc = new FileChooser();
        fc.setTitle("IMPORT DOCUMENTS");
        files_list = fc.showOpenMultipleDialog(new Stage());
        for(int i = 0; i < files_list.size(); i++){
            cvs_lst.getItems().add(files_list.get(i).getPath());
        }
    }

    public void setContent(){
        String text = showSelectedFile();
        Text txt = new Text(text);
        content_pane.setContent(txt);
        flyAnalyze();
    }

    public void setHkSignLabel() {
        hk_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                hk_value = (int)hk_slider.getValue();
                hk_sign_label.textProperty().setValue(String.valueOf((int)hk_slider.getValue()));
            }
        });
    }

    public void setSkSignLabel() {
        sk_slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                sk_value = (int)sk_slider.getValue();
                sk_sign_label.textProperty().setValue(String.valueOf((int)sk_slider.getValue()));
            }
        });
    }

    public void flyAnalyze() {
        double [] percValues = percentAnalyze();
        int [] values = analyze();
        hk_matches_label.setText(String.format("%.1f", percValues[0]) + "%");
        sk_matches_label.setText(String.format("%.1f", percValues[1]) + "%");
        double d = distance(values[0], values[1]);
        d_match_label.setText(String.format("%.2f", d) + " lu");
    }

    public int [] analyze(){
        hk_arr = hk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        sk_arr = sk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        String text = showSelectedFile().toLowerCase(Locale.ROOT);
        int hk_matches = 0;
        int sk_matches = 0;
        for(int i = 0; i < hk_arr.length; i++){
            if(text.contains(hk_arr[i].trim()) && hk_arr[i] != ""){
                hk_matches++;
            }
        }
        for(int i = 0; i < sk_arr.length; i++){
            if(text.contains(sk_arr[i].trim()) && sk_arr[i] != ""){
                sk_matches++;
            }
        }
        int [] values = {hk_matches, sk_matches};
        return values;
    }

    public double [] percentAnalyze(){
        hk_arr = hk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        sk_arr = sk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        String text = showSelectedFile().toLowerCase(Locale.ROOT);
        int hk_matches = 0;
        int sk_matches = 0;
        for(int i = 0; i < hk_arr.length; i++){
            if(text.contains(hk_arr[i].trim()) && hk_arr[i] != ""){
                hk_matches++;
            }
        }
        for(int i = 0; i < sk_arr.length; i++){
            if(text.contains(sk_arr[i].trim()) && sk_arr[i] != ""){
                sk_matches++;
            }
        }
        double [] values = {(hk_matches/(double)hk_arr.length)*100, (sk_matches/(double)sk_arr.length)*100};
        return values;
    }

    public String showSelectedFile() {
        String file = selectedFilepath();
        try {
            String text = readPdf.readPdf(file);
            return text;
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }
        return null;
    }

    public String selectedFilepath(){
        String file = cvs_lst.getSelectionModel().getSelectedItem().toString();
        return file;
    }

    public double distance(int hk_matches, int sk_matches){
        return Math.sqrt(((hk_value-hk_matches)*(hk_value-hk_matches)) + ((sk_value-sk_matches)*(sk_value-sk_matches)));
    }

    public void analyzeOne(){
        Cv cv = createCvObject();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Keys");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Matches");
        BarChart bc = new BarChart(xAxis, yAxis);
        bc.setTitle(cv.getName());
        XYChart.Series series = new XYChart.Series();
        series.setName("Matches");
        series.getData().add(new XYChart.Data<>("Hard Keys", cv.getHkMatches()));
        System.out.println(cv.getHkMatches());
        System.out.println((int)cv.getHkMatches());
        series.getData().add(new XYChart.Data<>("Soft Keys", cv.getSkMatches()));
        bc.getData().add(series);
        content_pane.setContent(bc);
    }




    public void analyzeAll() {
        System.out.println("PlaceHolder");
    }

    public Cv createCvObject(){
        String filepath = selectedFilepath();
        double [] percValues = percentAnalyze();
        int [] values = analyze();
        String [] name = filepath.split("/");
        double d_key = distance(values[0], values[1]);
        Cv newCv = new Cv(filepath, name[name.length-1], percValues[0], percValues[1], d_key);
        return newCv;
    }

}
