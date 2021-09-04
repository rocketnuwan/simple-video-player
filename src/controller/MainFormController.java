package controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Slider;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

public class MainFormController {

    public MediaView mediaView;
    public Slider progressBar;
    public Slider volumeSlider;
    private String path;
    private MediaPlayer mediaPlayer;


    public void initialize(){

    }

    public void btnChooseFileOnAction(ActionEvent actionEvent) {
        play(null);
    }

    public void play(DragEvent dragEvent){

        File file;
        if(dragEvent==null){
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(null);
        }else{
            Dragboard dragboard = dragEvent.getDragboard();
            file = dragboard.getFiles().get(0);
        }
        path=file.toURI().toString();

        if(path != null){
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            DoubleProperty widthProp = mediaView.fitWidthProperty();
            DoubleProperty heightProp = mediaView.fitHeightProperty();

            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));

            mediaPlayer.setOnReady(new Runnable() {
                @Override
                public void run() {
                    progressBar.setMax(media.getDuration().toSeconds());
                }
            });

            mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
                @Override
                public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                    //progressBar.setMax(media.getDuration().toSeconds());
                    progressBar.setValue(newValue.toSeconds());
                }
            });
            progressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });
            progressBar.setOnMouseDragged(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    mediaPlayer.seek(Duration.seconds(progressBar.getValue()));
                }
            });


            volumeSlider.setValue(mediaPlayer.getVolume()*100);

            volumeSlider.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable observable) {
                    mediaPlayer.setVolume(volumeSlider.getValue()/100);
                }
            });

            mediaPlayer.play();
        }
    }

    public void btnPlayOnAction(ActionEvent actionEvent) {
        mediaPlayer.play();
        mediaPlayer.setRate(1);
    }

    public void btnPauseOnAction(ActionEvent actionEvent) {
        mediaPlayer.pause();
    }

    public void btnStopOnAction(ActionEvent actionEvent) {
        mediaPlayer.stop();
    }

    public void btnSlowRateOnAction(ActionEvent actionEvent) {
        mediaPlayer.setRate(0.5);
    }

    public void btnFastRateOnAction(ActionEvent actionEvent) {
        mediaPlayer.setRate(2);
    }

    public void btnSkipForwardOnAction(ActionEvent actionEvent) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }

    public void btnSkipReverseOnAction(ActionEvent actionEvent) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-10)));
    }

    public void onMouseDragDropped(DragEvent dragEvent) {
        play(dragEvent);
    }
}
