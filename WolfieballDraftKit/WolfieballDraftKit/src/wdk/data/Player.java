/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wdk.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author McKillaGorilla
 */
public class Player {
    final StringProperty topic;
    final IntegerProperty sessions;
    final StringProperty TYPE;
    final StringProperty TEAM;
    final StringProperty LAST_NAME;
    final StringProperty FIRST_NAME;
    final StringProperty QP;
    final IntegerProperty AB;
    final IntegerProperty R;
    final IntegerProperty H;
    final IntegerProperty HR;
    final IntegerProperty RBI;
    final IntegerProperty SB;
    final DoubleProperty IP;
    final IntegerProperty ER;
    final IntegerProperty W;
    final IntegerProperty SV;
    final IntegerProperty H_PITCHER;
    final IntegerProperty BB;
    final IntegerProperty K;
    final StringProperty Position;
    final IntegerProperty RW;
    final IntegerProperty HRSV;
    final IntegerProperty RBIK;
    final StringProperty SBERA;
    final StringProperty BAWHIP;
    final StringProperty NOTES;
    final IntegerProperty YEAR_OF_BIRTH;
    final StringProperty NATION_OF_BIRTH;
    final StringProperty CONTRACT;
    final IntegerProperty SALARY;
    final StringProperty FANTASYTEAM;
    final StringProperty FANTASYPOSITION;
    final DoubleProperty BA;
    final DoubleProperty ERA;
    final DoubleProperty WHIP;
    final DoubleProperty estimatedValue;
    int R_rank;
    int HR_rank;
    int RBI_rank;
    int SB_rank;
    int BA_rank;
    int W_rank;
    int SV_rank;
    int K_rank;
    int ERA_rank;
    int WHIP_rank;
    double avg_rank_raw;
    int avg_rank_hitter;
    int avg_rank_pitcher;
    
    public static final String DEFAULT_TOPIC = "<ENTER TOPIC>";
    public static final int DEFAULT_SESSIONS = 1;    
    public static final String DEFAULT_TYPE = "N/A";
    public static final String DEFAULT_TEAM = "N/A";
    public static final String DEFAULT_LAST_NAME = "N/A";
    public static final String DEFAULT_FIRST_NAME = "N/A";
    public static final String DEFAULT_QP = "N/A";
    public static final int DEFAULT_AB = 0;
    public static final int DEFAULT_R = 0;
    public static final int DEFAULT_HR = 0;
    public static final int DEFAULT_H = 0;
    public static final int DEFAULT_RBI = 0;
    public static final int DEFAULT_SB = 0;
    public static final double DEFAULT_IP = 0.0;
    public static final int DEFAULT_ER = 0;
    public static final int DEFAULT_W = 0;
    public static final int DEFAULT_SV = 0;
    public static final int DEFAULT_H_PITCHER = 0;
    public static final int DEFAULT_BB = 0;
    public static final int DEFAULT_K = 0;
    public static final String DEFAULT_Position = "N/A";
    public static final int DEFAULT_RW = 0;
    public static final int DEFAULT_HRSV = 0;
    public static final int DEFAULT_RBIK = 0;
    public static final String DEFAULT_SBERA = "N/A";
    public static final String DEFAULT_BAWHIP = "N/A";
    public static final String DEFAULT_NOTES = "N/A";
    public static final int DEFAULT_YEAR_OF_BIRTH = 0;
    public static final String DEFAULT_NATION_OF_BIRTH = "N/A";
    public static final String DEFAULT_CONTRACT = "N/A";
    public static final int DEFAULT_SALARY = 0;
    public static final String DEFAULT_FANTASYTEAM = "N/A";
    public static final String DEFAULT_FANTASYPOSITION = "N/A";
    
    public Player() {
        topic = new SimpleStringProperty(DEFAULT_TOPIC);
        sessions = new SimpleIntegerProperty(DEFAULT_SESSIONS);
        TYPE = new SimpleStringProperty(DEFAULT_TYPE);
        TEAM = new SimpleStringProperty(DEFAULT_TEAM);
        LAST_NAME = new SimpleStringProperty(DEFAULT_LAST_NAME);
        FIRST_NAME = new SimpleStringProperty(DEFAULT_FIRST_NAME);
        QP = new SimpleStringProperty(DEFAULT_QP);
        AB = new SimpleIntegerProperty(DEFAULT_AB);
        R = new SimpleIntegerProperty(DEFAULT_R);
        H = new SimpleIntegerProperty(DEFAULT_H);
        HR = new SimpleIntegerProperty(DEFAULT_HR);
        RBI = new SimpleIntegerProperty(DEFAULT_RBI);
        SB = new SimpleIntegerProperty(DEFAULT_SB);
        IP = new SimpleDoubleProperty(DEFAULT_IP);
        ER = new SimpleIntegerProperty(DEFAULT_ER);
        W = new SimpleIntegerProperty(DEFAULT_W);
        SV = new SimpleIntegerProperty(DEFAULT_SV);
        H_PITCHER = new SimpleIntegerProperty(DEFAULT_H_PITCHER);
        BB = new SimpleIntegerProperty(DEFAULT_BB);
        K = new SimpleIntegerProperty(DEFAULT_K);
        Position = new SimpleStringProperty(DEFAULT_Position);
        RW = new SimpleIntegerProperty(DEFAULT_RW);
        HRSV = new SimpleIntegerProperty(DEFAULT_HRSV);
        RBIK = new SimpleIntegerProperty(DEFAULT_RBIK);
        SBERA = new SimpleStringProperty(DEFAULT_SBERA);
        BAWHIP = new SimpleStringProperty(DEFAULT_BAWHIP);
        NOTES = new SimpleStringProperty(DEFAULT_NOTES);
        YEAR_OF_BIRTH = new SimpleIntegerProperty(DEFAULT_YEAR_OF_BIRTH);
        NATION_OF_BIRTH = new SimpleStringProperty(DEFAULT_NATION_OF_BIRTH);
        CONTRACT = new SimpleStringProperty(DEFAULT_CONTRACT);
        SALARY = new SimpleIntegerProperty(DEFAULT_SALARY);
        FANTASYTEAM = new SimpleStringProperty(DEFAULT_CONTRACT);
        FANTASYPOSITION = new SimpleStringProperty(DEFAULT_CONTRACT);
        BA = new SimpleDoubleProperty(DEFAULT_IP);
        ERA = new SimpleDoubleProperty(DEFAULT_IP);
        WHIP = new SimpleDoubleProperty(DEFAULT_IP);
        estimatedValue = new SimpleDoubleProperty(DEFAULT_IP);
    }
    
    public void reset() {
        setTopic(DEFAULT_TOPIC);
        setSessions(DEFAULT_SESSIONS);
        setTYPE(DEFAULT_TYPE);
        setTEAM(DEFAULT_TEAM);
        setLAST_NAME(DEFAULT_LAST_NAME);
        setFIRST_NAME(DEFAULT_FIRST_NAME);
        setQP(DEFAULT_QP);
        setAB(DEFAULT_AB);
        setR(DEFAULT_R);
        setH(DEFAULT_H);
        setHR(DEFAULT_HR);
        setRBI(DEFAULT_RBI);
        setSB(DEFAULT_SB);
        setIP(DEFAULT_IP);
        setER(DEFAULT_ER);
        setW(DEFAULT_W);
        setSV(DEFAULT_SV);
        setH_PITCHER(DEFAULT_H_PITCHER);
        setBB(DEFAULT_BB);
        setK(DEFAULT_K);
        setPosition(DEFAULT_Position);
        setRW(DEFAULT_RW);
        setHRSV(DEFAULT_HRSV);
        setRBIK(DEFAULT_RBIK);
        setSBERA(DEFAULT_SBERA);
        setBAWHIP(DEFAULT_BAWHIP);
        setNOTES(DEFAULT_NOTES);
        setYEAR_OF_BIRTH(DEFAULT_YEAR_OF_BIRTH);
        setNATION_OF_BIRTH(DEFAULT_NATION_OF_BIRTH);
        setCONTRACT(DEFAULT_CONTRACT);
        setSALARY(DEFAULT_SALARY);
        setFANTASYTEAM(DEFAULT_NATION_OF_BIRTH);
        setFANTASYPOSITION(DEFAULT_NATION_OF_BIRTH);
        setBA(DEFAULT_IP);
        setERA(DEFAULT_IP);
        setWHIP(DEFAULT_IP);
        setEstimatedValue(DEFAULT_IP);
    }
    
    public String getTopic() {
        return topic.get();
    }
    
    public void setTopic(String initTopic) {
        topic.set(initTopic);
    }
    
    public StringProperty topicProperty() {
        return topic;
    }
    
    public String getTYPE() {
        return TYPE.get();
    }
    
    public void setTYPE(String initTYPE) {
        TYPE.set(initTYPE);
    }
    
    public StringProperty TYPEProperty() {
        return TYPE;
    }
    
    public String getTEAM() {
        return TEAM.get();
    }
    
    public void setTEAM(String initTEAM) {
        TEAM.set(initTEAM);
    }
    
    public StringProperty TEAMProperty() {
        return TEAM;
    }
    
    public String getLAST_NAME() {
        return LAST_NAME.get();
    }
    
    public void setLAST_NAME(String initLAST_NAME) {
        LAST_NAME.set(initLAST_NAME);
    }
    
    public StringProperty LAST_NAMEProperty() {
        return LAST_NAME;
    }
    
    public String getFIRST_NAME() {
        return FIRST_NAME.get();
    }
    
    public void setFIRST_NAME(String initFIRST_NAME) {
        FIRST_NAME.set(initFIRST_NAME);
    }
    
    public StringProperty FIRST_NAMEProperty() {
        return FIRST_NAME;
    }
    
    public String getQP() {
        return QP.get();
    }
    
    public void setQP(String initQP) {
        QP.set(initQP);
    }
    
    public StringProperty QPProperty() {
        return QP;
    }
    
    public int getAB() {
        return AB.get();
    }
    
    public void setAB(int initAB) {
        AB.set(initAB);
    }
    
    public IntegerProperty ABProperty() {
        return AB;
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
    
    public int getH() {
        return H.get();
    }
    
    public void setH(int initH) {
        H.set(initH);
    }
    
    public IntegerProperty HProperty() {
        return H;
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
    
    public double getIP() {
        return IP.get();
    }
    
    public void setIP(double initIP) {
        IP.set(initIP);
    }
    
    public DoubleProperty IPProperty() {
        return IP;
    }
    
    public int getER() {
        return ER.get();
    }
    
    public void setER(int initER) {
        ER.set(initER);
    }
    
    public IntegerProperty ERProperty() {
        return ER;
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
    
    public int getH_PITCHER() {
        return H_PITCHER.get();
    }
    
    public void setH_PITCHER(int initH_PITCHER) {
        H_PITCHER.set(initH_PITCHER);
    }
    
    public IntegerProperty H_PITCHERProperty() {
        return H_PITCHER;
    }
    
    public int getBB() {
        return BB.get();
    }
    
    public void setBB(int initBB) {
        BB.set(initBB);
    }
    
    public IntegerProperty BBProperty() {
        return BB;
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
    
    public String getPosition() {
        return Position.get();
    }
    
    public void setPosition(String initPosition) {
        Position.set(initPosition);
    }
    
    public StringProperty PositionProperty() {
        return Position;
    }
    
    public int getRW() {
        return RW.get();
    }
    
    public void setRW(int initRW) {
        RW.set(initRW);
    }
    
    public IntegerProperty RWProperty() {
        return RW;
    }
    
    public int getHRSV() {
        return HRSV.get();
    }
    
    public void setHRSV(int initHRSV) {
        HRSV.set(initHRSV);
    }
    
    public IntegerProperty HRSVProperty() {
        return HRSV;
    }
    
    public int getRBIK() {
        return RBIK.get();
    }
    
    public void setRBIK(int initRBIK) {
        RBIK.set(initRBIK);
    }
    
    public IntegerProperty RBIKProperty() {
        return RBIK;
    }
    
    public String getSBERA() {
        return SBERA.get();
    }
    
    public void setSBERA(String initSBERA) {
        SBERA.set(initSBERA);
    }
    
    public StringProperty SBERAProperty() {
        return SBERA;
    }
    
    public String getBAWHIP() {
        return BAWHIP.get();
    }
    
    public void setBAWHIP(String initBAWHIP) {
        BAWHIP.set(initBAWHIP);
    }
    
    public StringProperty BAWHIPProperty() {
        return BAWHIP;
    }
    
    public String getNOTES() {
        return NOTES.get();
    }
    
    public void setNOTES(String initNOTES) {
        NOTES.set(initNOTES);
    }
    
    public StringProperty NOTESProperty() {
        return NOTES;
    }
    
    public int getYEAR_OF_BIRTH() {
        return YEAR_OF_BIRTH.get();
    }
    
    public void setYEAR_OF_BIRTH(int initYEAR_OF_BIRTH) {
        YEAR_OF_BIRTH.set(initYEAR_OF_BIRTH);
    }
    
    public IntegerProperty YEAR_OF_BIRTHProperty() {
        return YEAR_OF_BIRTH;
    }
    
    public String getNATION_OF_BIRTH() {
        return NATION_OF_BIRTH.get();
    }
    
    public void setNATION_OF_BIRTH(String initNATION_OF_BIRTH) {
        NATION_OF_BIRTH.set(initNATION_OF_BIRTH);
    }
    
    public StringProperty NATION_OF_BIRTHProperty() {
        return NATION_OF_BIRTH;
    }
    
    public Integer getSessions() {
        return sessions.get();
    }
    
    public void setSessions(Integer initSessions) {
        sessions.set(initSessions);
    }
    
    public IntegerProperty sessionsProperty() {
        return sessions;
    }
    
    public String getCONTRACT() {
        return CONTRACT.get();
    }
    
    public void setCONTRACT(String initCONTRACT) {
        CONTRACT.set(initCONTRACT);
    }
    
    public StringProperty CONTRACTProperty() {
        return CONTRACT;
    }
    
    public int getSALARY() {
        return SALARY.get();
    }
    
    public void setSALARY(int initSALARY) {
        SALARY.set(initSALARY);
    }
    
    public IntegerProperty SALARYProperty() {
        return SALARY;
    }
    
    public String getFANTASYTEAM() {
        return FANTASYTEAM.get();
    }
    
    public void setFANTASYTEAM(String initFANTASYTEAM) {
        FANTASYTEAM.set(initFANTASYTEAM);
    }
    
    public StringProperty FANTASYTEAMProperty() {
        return FANTASYTEAM;
    }
    
    public String getFANTASYPOSITION() {
        return FANTASYPOSITION.get();
    }
    
    public void setFANTASYPOSITION(String initFANTASYPOSITION) {
        FANTASYPOSITION.set(initFANTASYPOSITION);
    }
    
    public StringProperty FANTASYPOSITIONProperty() {
        return FANTASYPOSITION;
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
    
    public double getEstimatedValue() {
        return estimatedValue.get();
    }
    
    public void setEstimatedValue(double initEstimatedValue) {
        estimatedValue.set(initEstimatedValue);
    }
    
    public DoubleProperty estimatedValueProperty() {
        return estimatedValue;
    }
    
    public int getR_rank(){
        return R_rank;
    }
    
    public int getHR_rank(){
        return HR_rank;
    }
    
    public int getRBI_rank(){
        return RBI_rank;
    }
    
    public int getSB_rank(){
        return SB_rank;
    }
    
    public int getBA_rank(){
        return BA_rank;
    }
    
    public int getW_rank(){
        return W_rank;
    }
    
    public int getSV_rank(){
        return SV_rank;
    }
    
    public int getK_rank(){
        return K_rank;
    }
    
    public int getERA_rank(){
        return ERA_rank;
    }
    
    public int getWHIP_rank(){
        return WHIP_rank;
    }
    
    public double getAvg_rank_raw(){
        return avg_rank_raw;
    }
    
    public int getAvg_rank_hitter(){
        return avg_rank_hitter;
    }
    
    public int getAvg_rank_pitcher(){
        return avg_rank_pitcher;
    }
    
    public void setR_rank(int a){
        R_rank = a;
    }
    
    public void setHR_rank(int a){
        HR_rank = a;
    }
    
    public void setRBI_rank(int a){
        RBI_rank = a;
    }
    
    public void setSB_rank(int a){
        SB_rank = a;
    }
    
    public void setBA_rank(int a){
        BA_rank = a;
    }
    
    public void setW_rank(int a){
        W_rank = a;
    }
    
    public void setSV_rank(int a){
        SV_rank = a;
    }
    
    public void setK_rank(int a){
        K_rank = a;
    }
    
    public void setERA_rank(int a){
        ERA_rank = a;
    }
    
    public void setWHIP_rank(int a){
        WHIP_rank = a;
    }
    
    public void setAvg_rank_raw(double a){
        avg_rank_raw = a;
    }
    
    public void setAvg_rank_hitter(int a){
        avg_rank_hitter = a;
    }
    
    public void setAvg_rank_pitcher(int a){
        avg_rank_pitcher = a;
    }
}
