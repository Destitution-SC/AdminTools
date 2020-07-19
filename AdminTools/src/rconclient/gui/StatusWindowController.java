/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rconclient.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import rconclient.util.WindowLoader;
import rconclient.querry.api.ApiQuerry;
import rconclient.querry.mc.MinecraftPing;
import rconclient.querry.mc.MinecraftPingOptions;
import rconclient.querry.mc.MinecraftPingReply;
import rconclient.querry.mc.QuerryUtils;
import rconclient.util.Data;

/**
 * FXML Controller class
 *
 * @author lukak
 */
public class StatusWindowController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    /*
    Status dots
    Naming scheme is :
    sd<number>
    s-status
    d-tot
    statusDot<number> but shortened
     */
    @FXML
    private Pane sd1;
    @FXML
    private Pane sd2;
    @FXML
    private Pane sd3;
    @FXML
    private Pane sd4;
    @FXML
    private Pane sd5;
    @FXML
    private Pane sd6;
    @FXML
    private Pane sd7;
    @FXML
    private ProgressBar pbApi;

    //For mc serv status
    @FXML
    private ImageView sFavicon;
    @FXML
    private Label sVer;
    @FXML
    private Label sPlayers;
    @FXML
    private ListView sOnlinePlayers;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tickMcRefresh();
        tickApi();
    }

    @FXML
    private void loadRcon() {
        WindowLoader.loadRcon(rootPane);
    }

    @FXML
    private void loadSettings() {
        WindowLoader.loadSettings(rootPane);
    }

    @FXML
    public void handleMouseClick(MouseEvent arg0) {
        //System.out.println("clicked on " + sOnlinePlayers.getSelectionModel().getSelectedItem());
    }

    private void refreshApiStatus() {
        //set progressbar to spin
        pbApi.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        Thread st = new Thread(() -> { //new thread worker
            //Initilse array list
            ArrayList<String> arrStatus = new ArrayList<String>();
            try {
                arrStatus = ApiQuerry.querry(); //querry
            } catch (IOException ex) {
                Logger.getLogger(StatusWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            final ArrayList<String> arrStatus_ = arrStatus; //final arraylist
            Platform.runLater(() -> {
                //
                sd1.setStyle("-fx-background-color : " + arrStatus_.get(0) + ";");
                sd2.setStyle("-fx-background-color : " + arrStatus_.get(2) + ";");
                sd3.setStyle("-fx-background-color : " + arrStatus_.get(3) + ";");
                sd4.setStyle("-fx-background-color : " + arrStatus_.get(4) + ";");
                sd5.setStyle("-fx-background-color : " + arrStatus_.get(5) + ";");
                sd6.setStyle("-fx-background-color : " + arrStatus_.get(6) + ";");
                sd7.setStyle("-fx-background-color : " + arrStatus_.get(7) + ";");
                pbApi.setProgress(0);
            });
        });
        st.start();
    }

    private void tickApi() {
        Thread ratf = new Thread(() -> {
            Platform.runLater(() -> {
                refreshApiStatus();
            });
            Data data = Data.getInstance();
            try {
                Thread.sleep(Math.round(data.getQuerryMojangApiRefreshRate()) * 1000);
            } catch (InterruptedException ex) {

            }
            tickApi();
        });
        ratf.start();
    }

    @FXML
    private void mcStatusRefresh() {
        Thread mcsr = new Thread(() -> {
            Data d = Data.getInstance(); //get data class instance
            MinecraftPingReply data; //get querry
            try {
                data = new MinecraftPing().getPing(new MinecraftPingOptions().setHostname(d.getHost()).setPort(25565)); //create querry

                //Set all the text and images 
                Platform.runLater(() -> {
                    sVer.setText("Version : " + data.getVersion().getName());
                    sPlayers.setText("Players : " + data.getPlayers().getOnline() + " / " + data.getPlayers().getMax());
                    sFavicon.setImage(QuerryUtils.convertToImage(data.getFavicon()));
                });

                //Set player list
                Platform.runLater(() -> {
                    try {
                        if (sOnlinePlayers.getItems() != null) {
                            sOnlinePlayers.getItems().clear();
                        }
                        for (int i = 0; i < data.getPlayers().getSample().size(); i++) {
                            sOnlinePlayers.getItems().add(data.getPlayers().getSample().get(i).getName());
                        }

                    } catch (Exception e) {

                    }
                });

            } catch (IOException ex) {

            }
        });
        mcsr.start();
    }

    private void tickMcRefresh() {
        Thread tmrt = new Thread(() -> {
            Platform.runLater(() -> {
                mcStatusRefresh();
            });
            Data d = Data.getInstance();
            try {
                Thread.sleep(d.getQuerryMcRefreshRate() * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(StatusWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
            tickMcRefresh();
        });
        tmrt.start();
    }
}
