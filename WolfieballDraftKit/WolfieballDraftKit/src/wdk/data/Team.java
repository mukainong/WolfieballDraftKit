/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Mukai Nong
 */
public class Team {
    final StringProperty name;
    final StringProperty owner;
    final IntegerProperty playersNeeded;
    final IntegerProperty moneyLeft;
    final IntegerProperty moneyPP;
    final IntegerProperty R;
    final IntegerProperty HR;
    final IntegerProperty RBI;
    final IntegerProperty SB;
    final DoubleProperty BA;
    final IntegerProperty W;
    final IntegerProperty SV;
    final IntegerProperty K;
    final DoubleProperty ERA;
    final DoubleProperty WHIP;
    final StringProperty BA_DISPLAY;
    final StringProperty ERA_DISPLAY;
    final StringProperty WHIP_DISPLAY;
    final IntegerProperty totalPoints;
    ObservableList<Player> startingLineUpPlayers;
    ObservableList<Player> taxiSquadPlayers;
    ArrayList<Player> CList = new ArrayList<>();
    ArrayList<Player> _1BList = new ArrayList<>();
    ArrayList<Player> CIList = new ArrayList<>();
    ArrayList<Player> _3BList = new ArrayList<>();
    ArrayList<Player> _2BList = new ArrayList<>();
    ArrayList<Player> MIList = new ArrayList<>();
    ArrayList<Player> SSList = new ArrayList<>();
    ArrayList<Player> UList = new ArrayList<>();
    ArrayList<Player> OFList = new ArrayList<>();
    ArrayList<Player> PList = new ArrayList<>();
    public static final String DEFAULT_NAME = "";
    public static final String DEFAULT_OWNER = "";    
    
    public Team() {
        name = new SimpleStringProperty(DEFAULT_NAME);
        owner = new SimpleStringProperty(DEFAULT_OWNER);
        playersNeeded = new SimpleIntegerProperty(23);
        moneyLeft = new SimpleIntegerProperty(260);
        moneyPP = new SimpleIntegerProperty(11);
        R = new SimpleIntegerProperty(0);
        HR = new SimpleIntegerProperty(0);
        RBI = new SimpleIntegerProperty(0);
        SB = new SimpleIntegerProperty(0);
        BA = new SimpleDoubleProperty(0.0);
        W = new SimpleIntegerProperty(0);
        SV = new SimpleIntegerProperty(0);
        K = new SimpleIntegerProperty(0);
        ERA = new SimpleDoubleProperty(0.0);
        WHIP = new SimpleDoubleProperty(0.0);
        totalPoints = new SimpleIntegerProperty(0);
        BA_DISPLAY = new SimpleStringProperty("0.000");
        ERA_DISPLAY = new SimpleStringProperty("0.00");
        WHIP_DISPLAY = new SimpleStringProperty("0.00");
        startingLineUpPlayers = FXCollections.observableArrayList();
        taxiSquadPlayers = FXCollections.observableArrayList();
    }
    
    public void reset() {
        setName(DEFAULT_NAME);
        setOwner(DEFAULT_OWNER);
    }
    
    public String getName() {
        return name.get();
    }
    
    public void setName(String initName) {
        name.set(initName);
    }
    
    public StringProperty nameProperty() {
        return name;
    }
    
    public String getOwner() {
        return owner.get();
    }
    
    public void setOwner(String initOwner) {
        owner.set(initOwner);
    }
    
    public StringProperty ownerProperty() {
        return owner;
    }
    
    public ObservableList<Player> getStartingLineUpPlayers() {
        return startingLineUpPlayers;
    }
    
    public ObservableList<Player> getTaxiSquadPlayers() {
        return taxiSquadPlayers;
    }
    
    public void addPlayer(Player p) {
        startingLineUpPlayers.add(p);
        //Collections.sort(scheduleItems);
    }
    
    public void removePlayer(Player p) {
        startingLineUpPlayers.remove(p);
        //Collections.sort(scheduleItems);
    }
    
    public void clearStartingLineUpPlayers() {
        startingLineUpPlayers.clear();
    }
    
    public ArrayList getCList(){
        return CList;
    }
    
    public ArrayList get1BList(){
        return _1BList;
    }
    
    public ArrayList getCIList(){
        return CIList;
    }
    
    public ArrayList get3BList(){
        return _3BList;
    }
    
    public ArrayList get2BList(){
        return _2BList;
    }
    
    public ArrayList getMIList(){
        return MIList;
    }
    
    public ArrayList getSSList(){
        return SSList;
    }
    
    public ArrayList getUList(){
        return UList;
    }
    
    public ArrayList getOFList(){
        return OFList;
    }

    public ArrayList getPList(){
        return PList;
    }
    
    public int getPlayersNeeded() {
        return playersNeeded.get();
    }
    
    public void setPlayersNeeded(int initPlayersNeeded) {
        playersNeeded.set(initPlayersNeeded);
    }
    
    public IntegerProperty playersNeededProperty() {
        return playersNeeded;
    }
    
    public int getMoneyLeft() {
        return moneyLeft.get();
    }
    
    public void setMoneyLeft(int initMoneyLeft) {
        moneyLeft.set(initMoneyLeft);
    }
    
    public IntegerProperty moneyLeftProperty() {
        return moneyLeft;
    }
    
    public int getMoneyPP() {
        return moneyPP.get();
    }
    
    public void setMoneyPP(int initMoneyPP) {
        moneyPP.set(initMoneyPP);
    }
    
    public IntegerProperty moneyPPProperty() {
        return moneyPP;
    }
    
    public int getR() {
        return R.get();
    }
    
    public void setR(int initR) {
        R.set(initR);
    }
    
    public IntegerProperty RProperty() {
        return R;
    }
    
    public int getHR() {
        return HR.get();
    }
    
    public void setHR(int initHR) {
        HR.set(initHR);
    }
    
    public IntegerProperty HRProperty() {
        return HR;
    }
    
    public int getRBI() {
        return RBI.get();
    }
    
    public void setRBI(int initRBI) {
        RBI.set(initRBI);
    }
    
    public IntegerProperty RBIProperty() {
        return RBI;
    }
    
    public int getSB() {
        return SB.get();
    }
    
    public void setSB(int initSB) {
        SB.set(initSB);
    }
    
    public IntegerProperty SBProperty() {
        return SB;
    }
    
    public double getBA() {
        return BA.get();
    }
    
    public void setBA(double initBA) {
        BA.set(initBA);
    }
    
    public DoubleProperty BAProperty() {
        return BA;
    }
    
    public int getW() {
        return W.get();
    }
    
    public void setW(int initW) {
        W.set(initW);
    }
    
    public IntegerProperty WProperty() {
        return W;
    }
    
    public int getSV() {
        return SV.get();
    }
    
    public void setSV(int initSV) {
        SV.set(initSV);
    }
    
    public IntegerProperty SVProperty() {
        return SV;
    }
    
    public int getK() {
        return K.get();
    }
    
    public void setK(int initK) {
        K.set(initK);
    }
    
    public IntegerProperty KProperty() {
        return K;
    }
    
    public double getERA() {
        return ERA.get();
    }
    
    public void setERA(double initERA) {
        ERA.set(initERA);
    }
    
    public DoubleProperty ERAProperty() {
        return ERA;
    }
    
    public double getWHIP() {
        return WHIP.get();
    }
    
    public void setWHIP(double initWHIP) {
        WHIP.set(initWHIP);
    }
    
    public DoubleProperty WHIPProperty() {
        return WHIP;
    }
    
    public int getTotalPoints() {
        return totalPoints.get();
    }
    
    public void setTotalPoints(int initTotalPoints) {
        totalPoints.set(initTotalPoints);
    }
    
    public IntegerProperty totalPointsProperty() {
        return totalPoints;
    }
    
    public String getBA_DISPLAY() {
        return BA_DISPLAY.get();
    }
    
    public void setBA_DISPLAY(String initBA_DISPLAY) {
        BA_DISPLAY.set(initBA_DISPLAY);
    }
    
    public StringProperty BA_DISPLAYProperty() {
        return BA_DISPLAY;
    }
    
    public String getERA_DISPLAY() {
        return ERA_DISPLAY.get();
    }
    
    public void setERA_DISPLAY(String initERA_DISPLAY) {
        ERA_DISPLAY.set(initERA_DISPLAY);
    }
    
    public StringProperty ERA_DISPLAYProperty() {
        return ERA_DISPLAY;
    }
    
    public String getWHIP_DISPLAY() {
        return WHIP_DISPLAY.get();
    }
    
    public void setWHIP_DISPLAY(String initWHIP_DISPLAY) {
        WHIP_DISPLAY.set(initWHIP_DISPLAY);
    }
    
    public StringProperty WHIP_DISPLAYProperty() {
        return WHIP_DISPLAY;
    }
}
