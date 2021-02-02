package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    protected List<File> files_temp;
    protected ArrayList<String> file_list = new ArrayList<>();
    protected ArrayList<Cv> cv_list = new ArrayList<>();
    protected ScatterChart sc_show;

    public void openFc(){
        FileChooser fc = new FileChooser();
        fc.setTitle("IMPORT DOCUMENTS");
        files_temp = fc.showOpenMultipleDialog(new Stage());
        for(int i = 0; i < files_temp.size(); i++){
            String path = files_temp.get(i).getPath();
            cvs_lst.getItems().add(path);
            file_list.add(path);
        }
    }

    public void setContent(){
        String filepath = selectedFilepath();
        String text = showSelectedFile(filepath);
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
        String filepath = selectedFilepath();
        double [] percValues = percentAnalyze(filepath);
        int [] values = analyze(filepath);
        hk_matches_label.setText(String.format("%.1f", percValues[0]) + "%");
        sk_matches_label.setText(String.format("%.1f", percValues[1]) + "%");
        double d = distance(values[0], values[1]);
        d_match_label.setText(String.format("%.2f", d) + " lu");
    }

    public int [] analyze(String filepath){
        hk_arr = hk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        sk_arr = sk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        String text = showSelectedFile(filepath).toLowerCase(Locale.ROOT);
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

    public double [] percentAnalyze(String filepath){
        hk_arr = hk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        sk_arr = sk_keys.getText().replace("\n", "").toLowerCase(Locale.ROOT).split(",");
        String text = showSelectedFile(filepath).toLowerCase(Locale.ROOT);
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

    public String showSelectedFile(String filepath) {
        try {
            String text = readPdf.readPdf(filepath);
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
        String filepath = selectedFilepath();
        Cv cv = createCvObject(filepath);
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Keys");
        NumberAxis yAxis = new NumberAxis(0, 100, 10);
        yAxis.setLabel("Matches (%)");
        BarChart bc = new BarChart(xAxis, yAxis);
        bc.setTitle(cv.getName());
        XYChart.Series series = new XYChart.Series();
        series.setName("Matches (in percent)");
        series.getData().add(new XYChart.Data<>("Hard Keys", cv.getHkMatches()));
        series.getData().add(new XYChart.Data<>("Soft Keys", cv.getSkMatches()));
        bc.getData().add(series);
        content_pane.setContent(bc);
    }



    public ScatterChart createScatterShart() {
        NumberAxis xAxis = new NumberAxis(0, 110, 10);
        NumberAxis yAxis = new NumberAxis(0, 110, 10);
        ScatterChart sc = new ScatterChart(xAxis, yAxis);
        xAxis.setLabel("Hard Keys");
        yAxis.setLabel("Soft Keys");
        sc.setTitle("Candidates Matches");
        for(int i = 0; i < cv_list.size(); i++){
            XYChart.Series cand = new XYChart.Series();
            Cv temp = cv_list.get(i);
            cand.setName(temp.getName());
            cand.getData().add(new XYChart.Data<>(temp.getHkMatches(), temp.getSkMatches()));
            sc.getData().add(cand);
        }
        return sc;
    }

    public void showScatterShart(ScatterChart sc){
        content_pane.setContent(sc);
    }

    public void createCvObjectAll(){
        for(int i = 0; i < file_list.size(); i++){
            String filepath = file_list.get(i);
            Cv temp = createCvObject(filepath);
            cv_list.add(temp);
        }
    }

    public void analyzeAll(){
        createCvObjectAll();
        if(sc_show == null){
            sc_show = createScatterShart();
        }
        showScatterShart(sc_show);
    }

    public Cv createCvObject(String filepath){
        double [] percValues = percentAnalyze(filepath);
        int [] values = analyze(filepath);
        String [] name = filepath.split("/");
        double d_key = distance(values[0], values[1]);
        Cv newCv = new Cv(filepath, name[name.length-1], percValues[0], percValues[1], d_key);
        return newCv;
    }

}
