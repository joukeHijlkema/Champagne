/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Champagne;

import Champagne.Items.Competence;
import Champagne.Items.Eleve;
import Champagne.Items.Matiere;
import Champagne.Items.Note;
import Champagne.Items.Observation;
import Champagne.Items.Periode;
import Champagne.Items.SousMatiere;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author hylkema
 */
public final class dataBase {

  Connection com = null;
  public String ENSEIGNANT;
  public int CYCLE;
  public String ACADEMIE;
  public String DEPARTEMENT;
  public String CIRCONSCRIPTION;
  public String ECOLE;
  public String ADRESSE;
  public String COURIELLE;
  public String CLASSE;
  public String TELEPHONE;

  public dataBase(String name) {
    try {
      Class.forName("org.sqlite.JDBC");
      String sep = System.getProperty("file.separator");
      String confDir = System.getProperty("user.home") + sep + ".Champagne";
      File FP = new File(confDir);
      FP.mkdir();
      com = DriverManager.getConnection("jdbc:sqlite:" + confDir + sep + name + ".db");
    } catch (ClassNotFoundException | SQLException e) {
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);
    }
    System.out.println("Opened database successfully");

    //check for tables
    if (!tableExists("Eleves")) {
      createTable("Eleves");
    }
    if (!tableExists("Periodes")) {
      createTable("Periodes");
    }
    if (!tableExists("Competences")) {
      createTable("Competences");
    }
    if (!tableExists("Matieres")) {
      createTable("Matieres");
    }
    if (!tableExists("SousMatieres")) {
      createTable("SousMatieres");
    }
    if (!tableExists("Notes")) {
      createTable("Notes");
    }
    if (!tableExists("Observations")) {
      createTable("Observations");
    }
    if (!tableExists("persoData")) {
      createTable("persoData");
    }
    
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM persoData");
      while (rs.next()) {
        this.ENSEIGNANT = rs.getString("ENSEIGNANT");
        this.CYCLE =  rs.getInt("CYCLE");
        this.ACADEMIE = rs.getString("ACADEMIE");
        this.DEPARTEMENT = rs.getString("DEPARTEMENT");
        this.CIRCONSCRIPTION = rs.getString("CIRCONSCRIPTION");
        this.ECOLE = rs.getString("ECOLE");
        this.ADRESSE = rs.getString("ADRESSE");
        this.COURIELLE = rs.getString("COURIELLE");
        this.CLASSE = rs.getString("CLASSE");
        this.TELEPHONE = rs.getString("TELEPHONE");
        break;
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public boolean tableExists(String name) {
    boolean out = false;
    String q = String.format("SELECT name FROM sqlite_master WHERE type='table' AND name='%s'", this.Esc(name));
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      out = rs.getNString("name") != null;
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      System.err.println(ex.getMessage());
      return out;
    }
    return out;
  }

  private void createTable(String name) {
    String sql = null;
    switch (name) {
      case "Eleves":
        sql = "CREATE TABLE Eleves "
         + "(ID      INTEGER PRIMARY KEY,"
         + " NOM     TEXT            NOT NULL,"
         + " PRENOM  TEXT            NOT NULL,"
         + " NIVEAU  INT             NOT NULL,"
         + " ANNEE   INT             NOT NULL)";
        break;
      case "Periodes":
        sql = "CREATE TABLE Periodes "
         + "(ID      INTEGER PRIMARY KEY ,"
         + " ANNEE   INT   NOT NULL,"
         + " TRIMESTRE INT NOT NULL)";
        break;
      case "Competences":
        sql = "CREATE TABLE Competences "
         + "(ID      INTEGER PRIMARY KEY ,"
         + " MAT     INTEGER NOT NULL ,"
         + " SMAT    INTEGER NOT NULL ,"
         + " COMP    TEXT   NOT NULL)";
        break;
      case "Matieres":
        sql = "CREATE TABLE Matieres "
         + "(ID     INTEGER PRIMARY KEY ,"
         + " MAT    TEXT   NOT NULL)";
        break;
      case "SousMatieres":
        sql = "CREATE TABLE SousMatieres "
         + "(ID      INTEGER PRIMARY KEY ,"
         + " MAT     INTEGER NOT NULL ,"
         + " SMAT    TEXT   NOT NULL)";
        break;
      case "Notes":
        sql = "CREATE TABLE Notes "
         + "(ID      INTEGER PRIMARY KEY, "
         + "PERIODE INTEGER NOT NULL, "
         + "ELEVE   INTEGER NOT NULL, "
         + "COMP    INTEGER NOT NULL, "
         + "MAT     INTEGER NOT NULL, "
         + "SMAT    INTEGER NOT NULL, "
         + "NOTE    TEXT    NOT NULL)";
        break;
      case "Observations":
        sql = "CREATE TABLE Observations "
         + "(ID      INTEGER PRIMARY KEY, "
         + "PERIODE INTEGER NOT NULL, "
         + "ELEVE   INTEGER NOT NULL,"
         + "OBSERVATION TEXT NOT NULL)";
        break;
      case "persoData":
        sql = "CREATE TABLE persoData "
         + "(ID      INTEGER PRIMARY KEY, "
         + "ENSEIGNANT TEXT NOT NULL, "
         + "ACADEMIE TEXT NOT NULL, "
         + "DEPARTEMENT TEXT NOT NULL, "
         + "CIRCONSCRIPTION TEXT NOT NULL, "
         + "ECOLE TEXT NOT NULL, "
         + "ADRESSE TEXT NOT NULL, "
         + "COURIELLE TEXT NOT NULL, "
         + "TELEPHONE TEXT NOT NULL, "
         + "CLASSE TEXT NOT NULL, "
         + "CYCLE   INTEGER DEFAULT 1)";
        break;
      default:
        System.out.println(name + " is not a valid table name");
    }
    try {
      Statement stmt = com.createStatement();
      stmt.execute(sql);
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("sql = " + sql);
      System.out.println("ex = " + ex);
    }

  }

  private int add(String selectString, String insertString, String updateString) {
    int id = -1;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(selectString);
      if (!rs.isClosed()) {
        id = rs.getInt("ID");
        updateString += " WHERE ID=" + id;
        stmt.executeUpdate(updateString);
      }
      if (id <= 0) {
        stmt.executeUpdate(insertString);
        rs = stmt.executeQuery(selectString);
        id = rs.getInt("ID");
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("selectString = " + selectString);
      System.out.println("insertString = " + insertString);
      System.out.println("updateString = " + updateString);
      System.out.println("add : ex = " + ex);
    }

    return id;
  }

  public int addPeriode(Periode p) {
    String q1 = String.format("SELECT ID FROM Periodes WHERE ANNEE=%d AND TRIMESTRE=%d", p.ANNEE, p.TRIMESTRE);
    String q2 = String.format("INSERT INTO Periodes (ANNEE,TRIMESTRE) VALUES (%d,%d)", p.ANNEE, p.TRIMESTRE);
    String q3 = String.format("UPDATE Periodes SET ANNEE=%d,TRIMESTRE=%d", p.ANNEE, p.TRIMESTRE);

    return add(q1, q2, q3);
  }

  public int addEleve(Eleve e) {
    String q1 = String.format("SELECT ID FROM Eleves WHERE NOM='%s' AND PRENOM='%s'", this.Esc(e.NOM), this.Esc(e.PRENOM));
    String q2 = String.format("INSERT INTO Eleves (NOM,PRENOM,NIVEAU,ANNEE) VALUES ('%s','%s',%d,%d)", this.Esc(e.NOM), this.Esc(e.PRENOM), e.NIVEAU, e.ANNEE);
    String q3 = String.format("UPDATE Eleves SET NOM='%s',PRENOM='%s',NIVEAU=%d", this.Esc(e.NOM), this.Esc(e.PRENOM), e.NIVEAU);
    return add(q1, q2, q3);
  }

  public int addMat(Matiere m) {
    String q1 = String.format("SELECT ID FROM Matieres WHERE MAT='%s'", this.Esc(m.MAT));
    String q2 = String.format("INSERT INTO Matieres (MAT) VALUES ('%s')", this.Esc(m.MAT));
    String q3 = String.format("UPDATE Matieres SET MAT='%s'", this.Esc(m.MAT));
    return add(q1, q2, q3);
  }

  public int addSmat(SousMatiere s) {
    String q1 = String.format("SELECT ID FROM SousMatieres WHERE SMAT='%s'", this.Esc(s.SMAT));
    String q2 = String.format("INSERT INTO SousMatieres (SMAT,MAT) VALUES ('%s',%d)", this.Esc(s.SMAT), s.MAT);
    String q3 = String.format("UPDATE SousMatieres SET SMAT='%s',MAT=%d", this.Esc(s.SMAT), s.MAT);
    return add(q1, q2, q3);
  }

  public int addComp(Competence c) {
    String q1 = String.format("SELECT ID FROM Competences WHERE COMP='%s'", this.Esc(c.COMP));
    String q2 = String.format("INSERT INTO Competences (COMP,MAT,SMAT) VALUES ('%s',%d,%d)", this.Esc(c.COMP), c.MAT, c.SMAT);
    String q3 = String.format("UPDATE Competences SET COMP='%s',MAT=%d,SMAT=%d", this.Esc(c.COMP), c.MAT, c.SMAT);
    return add(q1, q2, q3);
  }

  public int addNote(Note n) {
    if (n.NOTE.isEmpty()) {
      return 0;
    }
    String q1 = String.format("SELECT ID FROM Notes WHERE PERIODE=%d AND ELEVE=%d AND COMP=%d AND MAT=%d AND SMAT=%d", n.PERIODE, n.ELEVE, n.COMP, n.MAT, n.SMAT);
    String q2 = String.format("INSERT INTO Notes (PERIODE,ELEVE,COMP,NOTE,MAT,SMAT) VALUES (%d,%d,%d,'%s',%d,%d)", n.PERIODE, n.ELEVE, n.COMP, this.Esc(n.NOTE), n.MAT, n.SMAT);
    String q3 = String.format("UPDATE Notes SET PERIODE=%d,ELEVE=%d,COMP=%d,NOTE='%s',MAT=%d,SMAT=%d", n.PERIODE, n.ELEVE, n.COMP, this.Esc(n.NOTE), n.MAT, n.SMAT);
    return add(q1, q2, q3);
  }

  public int addObservation(Observation o) {
    String q1 = String.format("SELECT ID FROM Observations WHERE PERIODE=%s AND ELEVE =%d", o.PERIODE, o.ELEVE);
    String q2 = String.format("INSERT INTO Observations (PERIODE,ELEVE,OBSERVATION) VALUES (%d,%d,'%s')", o.PERIODE, o.ELEVE, this.Esc(o.OBSERVATION));
    String q3 = String.format("UPDATE Observations SET PERIODE=%d,ELEVE=%d,OBSERVATION='%s'", o.PERIODE, o.ELEVE, this.Esc(o.OBSERVATION));
    return add(q1, q2, q3);
  }

  public Iterable<Periode> getPeriodes() {
    List<Periode> out = new ArrayList<>();
    Periode p;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Periodes");
      while (rs.next()) {
        p = new Periode(rs.getInt("ID"), rs.getInt("ANNEE"), rs.getInt("TRIMESTRE"));
        out.add(p);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  public Iterable<Eleve> getEleves() {
    List<Eleve> out = new ArrayList<>();
    Eleve e;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Eleves ORDER BY NOM");
      while (rs.next()) {
        e = new Eleve(rs.getInt("ID"), this.unesc(rs.getString("NOM")), this.unesc(rs.getString("PRENOM")), rs.getInt("NIVEAU"), rs.getInt("ANNEE"));
        out.add(e);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  public Iterable<Eleve> getElevesPerYear(int annee) {
    List<Eleve> out = new ArrayList<>();
    Eleve e;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Eleves WHERE ANNEE=" + annee + " ORDER BY NOM");
      while (rs.next()) {
        e = new Eleve(rs.getInt("ID"), this.unesc(rs.getString("NOM")), this.unesc(rs.getString("PRENOM")), rs.getInt("NIVEAU"), rs.getInt("ANNEE"));
        out.add(e);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  public Iterable<Integer> getEleves(int pId) {
    List<Integer> out = new ArrayList<>();
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT DISTINCT ELEVE FROM Notes WHERE PERIODE=" + pId);
      while (rs.next()) {
        out.add(rs.getInt("ELEVE"));
      }
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  Iterable<Competence> getCompetences(int iMat, int iSmat) {
    List<Competence> out = new ArrayList<>();
    Competence comp;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM Competences WHERE MAT=%d AND SMAT=%d ORDER BY COMP", iMat, iSmat));
      while (rs.next()) {
        comp = new Competence(rs.getInt("ID"), rs.getInt("MAT"), rs.getInt("SMAT"), this.unesc(rs.getString("COMP")));
        out.add(comp);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  Iterable<Matiere> getMatieres() {
    List<Matiere> out = new ArrayList<>();
    Matiere mat;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM Matieres ORDER BY MAT");
      while (rs.next()) {
        mat = new Matiere(rs.getInt("ID"), this.unesc(rs.getString("MAT")));
        out.add(mat);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  public Iterable<Matiere> getMatieres(int pId) {
    List<Matiere> out = new ArrayList<>();
    Matiere mat;
    int mId;
    String q = String.format("SELECT DISTINCT MAT FROM Notes WHERE PERIODE=%d AND NOT NOTE='-'", pId);
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      while (rs.next()) {
        mId = rs.getInt("MAT");
        mat = new Matiere(mId, getString("SELECT MAT FROM Matieres WHERE ID=" + mId, "MAT", "Erreur!"));
        out.add(mat);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    Collections.sort(out, new Comparator() {
      @Override
      public int compare(Object m1, Object m2) {
        return (((Matiere) m1).MAT.compareTo(((Matiere) m2).MAT));
      }
    });
    return out;
  }

  Iterable<SousMatiere> getSousMatieres(int ID
  ) {
    List<SousMatiere> out = new ArrayList<>();
    SousMatiere smat;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery("SELECT * FROM SousMatieres WHERE MAT=" + ID + " ORDER BY SMAT");
      while (rs.next()) {
        smat = new SousMatiere(rs.getInt("ID"), rs.getInt("MAT"), this.unesc(rs.getString("SMAT")));
        out.add(smat);
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  void delete(String deleteString) {
    System.out.println("query : " + deleteString);
    try {
      Statement stmt = com.createStatement();
      stmt.executeUpdate(deleteString);
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("deleteString = " + deleteString);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  void deleteComp(int mId, int smId, int id
  ) {
    System.out.println("deleteCOmp ...");
    delete(String.format("DELETE FROM Competences WHERE ID=%d AND MAT=%d AND SMAT=%d", id, mId, smId));
    //evetualy clean up orphanaged items in smat and mat
    if (count(String.format("SELECT COMP FROM Competences WHERE MAT=%d AND SMAT=%d", mId, smId)) == 0) {
      delete(String.format("DELETE FROM SousMatieres WHERE ID=%d", smId));
    }
    if (count(String.format("SELECT SMAT FROM SousMatieres WHERE MAT=%d", mId)) == 0) {
      delete(String.format("DELETE FROM Matieres WHERE ID=%d", mId));
    }
  }

  void delete(Periode p
  ) {
    delete(String.format("DELETE FROM Periodes WHERE ID=%d", p.ID));
  }

  void delete(Eleve e
  ) {
    delete(String.format("DELETE FROM Eleves WHERE ID=%d", e.ID));
  }

  String getNote(Eleve e, Competence c
  ) {
    String Note = "";
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(String.format("SELECT NOTE FROM Notes WHERE ELEVE=%d AND COMP=%d", e.ID, c.ID));
      if (!rs.isClosed()) {
        Note = this.unesc(rs.getString("NOTE"));
        System.out.println(String.format("SELECT NOTE FROM Notes WHERE ELEVE=%d AND COMP=%d", e.ID, c.ID) + "returned nothing");
        System.out.println("Nore = " + Note);
        rs.close();
      } else {
        System.out.println(String.format("SELECT NOTE FROM Notes WHERE ELEVE=%d AND COMP=%d", e.ID, c.ID) + "returned nothing");
      }
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return Note;
  }

  Eleve getEleve(String eleve
  ) {
    Eleve e = null;
    String[] items = eleve.split(",");
    String q = String.format("SELECT * FROM Eleves WHERE NOM='%s' AND PRENOM='%s'", this.Esc(items[0].trim()), this.Esc(items[1].trim()));
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      if (!rs.isClosed()) {
        e = new Eleve(rs.getInt("ID"), this.unesc(rs.getString("NOM")), this.unesc(rs.getString("PRENOM")), rs.getInt("NIVEAU"), rs.getInt("ANNEE"));
        rs.close();
      } else {
        System.out.println("query error : " + q);
      }
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return e;
  }

  public Eleve getEleve(int eId) {
    Eleve e = null;
    String q = String.format("SELECT * FROM Eleves WHERE ID=" + eId);
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      e = new Eleve(rs.getInt("ID"), this.unesc(rs.getString("NOM")), this.unesc(rs.getString("PRENOM")), rs.getInt("NIVEAU"), rs.getInt("ANNEE"));
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return e;
  }

  public List<SousMatiere> getSmat(Matiere mat, int pId, int eId) {
    List<SousMatiere> out = new ArrayList<>();

    for (int i : getIntList(String.format("SELECT SMAT FROM NOTES WHERE MAT=%d AND PERIODE=%d AND ELEVE=%d AND NOT NOTE='-'", mat.ID, pId, eId))) {
      out.add(this.getSousMatiere(i));
    }
    Collections.sort(out, new Comparator() {
      @Override
      public int compare(Object m1, Object m2) {
        return (((SousMatiere) m1).SMAT.compareTo(((SousMatiere) m2).SMAT));
      }
    });
    return out;
  }

  public String getNote(int pId, int eId, String mat, String smat, String comp) {
    int mId = this.getMatId(mat);
    int smId = this.getSMatId(mId, smat);
    int cId = this.getCompId(mId, smId, comp);
    return this.getString(String.format("SELECT NOTE FROM Notes WHERE PERIODE=%d AND ELEVE=%d AND MAT=%d AND SMAT=%d AND COMP=%d", pId, eId, mId, smId, cId), "NOTE", "Erreur !!");
  }

  public String getNote(int pId, int eId, int mId, int sId, int cId) {
    return getString(String.format("SELECT NOTE FROM Notes WHERE PERIODE=%d AND ELEVE=%d AND MAT=%d AND SMAT=%d AND COMP=%d", pId, eId, mId, sId, cId), "NOTE", "-");
  }

  public int getCompId(int matId, int smatId, String comp) {
    return getInt(String.format("SELECT ID FROM Competences WHERE COMP='%s' AND MAT=%d and SMAT=%d", this.Esc(comp), matId, smatId), "ID");
  }

  public int getMatId(String mat) {
    return getInt(String.format("SELECT ID FROM Matieres WHERE MAT='%s'", this.Esc(mat)), "ID");
  }

  public int getSMatId(int matId, String smat) {
    return getInt(String.format("SELECT ID FROM SousMatieres WHERE SMAT='%s' AND MAT=%d", this.Esc(smat), matId), "ID");
  }

  public int getEleveId(String nom, String prenom) {
    return getInt(String.format("SELECT ID FROM Eleves WHERE NOM='%s' AND PRENOM='%s'", this.Esc(nom), this.Esc(prenom)), "ID");
  }

  private String getString(String query, String col, String def) {
    String out = def;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      out = this.unesc(rs.getString(col));
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
//      System.out.println("empty query = " + query);
//      System.out.println("col = " + col);
//      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out.replace("'", "''").trim();
  }

  private int getInt(String query, String col) {
    int out = 0;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      if (rs.isClosed()) {
        return 0;
      }
      out = rs.getInt(col);
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("query = " + query);
      System.out.println("col = " + col);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  private List<String> getStringList(String query, String col) {
    List<String> out = new ArrayList<>();
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        out.add(this.unesc(rs.getString(col)));
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("query = " + query);
      System.out.println("col = " + col);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    Collections.sort(out);
    return out;
  }

  private Set<Integer> getIntList(String query) {
    Set<Integer> out = new HashSet<>();
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        out.add(rs.getInt(1));
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("query = " + query);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  public int saveObservation(Periode p, Eleve e, String o) {
    String q1 = String.format("SELECT ID FROM Observations WHERE ELEVE=%d AND PERIODE=%d", p.ID, e.ID);
    String q2 = String.format("INSERT INTO Observations (PERIODE,ELEVE,OBSERVATION) VALUES (%d,%d,'%s')", p.ID, e.ID, this.Esc(o));
    String q3 = String.format("UPDATE Observations SET PERIODE=%d,ELEVE=%d,OBSERVATION='%s'", p.ID, e.ID, this.Esc(o));
    return add(q1, q2, q3);
  }

  private String Esc(String s) {

    return s.
     replace("'", "''").
     replace("…", "...").trim();
  }

  public String getObservation(int pId, int eId) {
    return this.unesc(getString(String.format("SELECT OBSERVATION FROM Observations WHERE PERIODE=%d AND ELEVE=%d", pId, eId), "OBSERVATION", "-"));
  }

  public String getObservation(Periode p, Eleve e) {
    return getObservation(p.ID, e.ID);
  }

  private int count(String query) {
    int count = 0;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        count++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return count;
  }

  public Competence getComp(int ci) {
    Competence c = null;
    String q = String.format("SELECT * FROM Competences WHERE ID=" + ci);
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);

      c = new Competence(rs.getInt("ID"), rs.getInt("MAT"), rs.getInt("SMAT"), this.unesc(rs.getString("COMP")));
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      JOptionPane.showMessageDialog(null, "Un souci avec la base de données. Ne touche plus a rien et demande a Jouke !!\n Note le suivant : " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return c;
  }

  public SousMatiere getSousMatiere(int i) {
    SousMatiere s = null;
    String q = String.format("SELECT * FROM SousMatieres WHERE ID=%d", i);
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      s = new SousMatiere(rs.getInt("ID"), rs.getInt("MAT"), this.unesc(rs.getString("SMAT")));
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return s;
  }

  public List<Note> getNotes(Matiere mat, SousMatiere smat, Periode p, Eleve e) {
    String q = String.format("SELECT * FROM Notes WHERE MAT=%d AND SMAT=%d AND PERIODE=%d AND ELEVE=%d ORDER BY COMP", mat.ID, smat.ID, p.ID, e.ID);
    List<Note> out = new ArrayList<>();
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      while (rs.next()) {
        out.add(new Note(rs.getInt("ID"), rs.getInt("PERIODE"), rs.getInt("ELEVE"), rs.getInt("MAT"), rs.getInt("SMAT"), rs.getInt("COMP"), this.unesc(rs.getString("NOTE"))));
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return out;
  }

  private String unesc(String str) {
    return str.replace("''", "'");
  }

  int getNbNotes(String q) {
    int count = 0;
    try {
      Statement stmt = com.createStatement();
      ResultSet rs = stmt.executeQuery(q);
      while (rs.next()) {
        count++;
      }
      rs.close();
      stmt.close();
    } catch (SQLException ex) {
      System.out.println("q = " + q);
      Logger.getLogger(dataBase.class.getName()).log(Level.SEVERE, null, ex);
    }
    return count;
  }

  Object getNbNotes(Periode p, Eleve e, Matiere m) {
    String q = String.format("SELECT * FROM Notes WHERE MAT=%d AND PERIODE=%d AND ELEVE=%d ", m.ID, p.ID, e.ID);
    return getNbNotes(q);
  }

  Object getNbNotes(Periode p, Eleve e, Matiere m, SousMatiere s) {
    String q = String.format("SELECT * FROM Notes WHERE MAT=%d AND SMAT=%d AND PERIODE=%d AND ELEVE=%d ", m.ID, s.ID, p.ID, e.ID);
    return getNbNotes(q);
  }

  void deleteNote(int P, int E, int M, int SM, int C) {
    delete(String.format("DELETE FROM Notes WHERE PERIODE=%d AND ELEVE=%d AND MAT=%d AND SMAT=%d AND COMP=%d", P, E, M, SM, C));
  }

  public int setPersoData(String AC,String DE,String CI,String EC,String AD,String CO,String TE,String CL,String EN, int CY) {
    String q1 = String.format("SELECT * FROM persoData");
    String q2 = String.format("INSERT INTO persoData (ACADEMIE,DEPARTEMENT,CIRCONSCRIPTION,ECOLE,ADRESSE,COURIELLE,TELEPHONE,CLASSE,ENSEIGNANT,CYCLE)");
    q2+= String.format("VALUES ('%s','%s','%s','%s','%s','%s','%s','%s','%s',%d)",AC,DE,CI,EC,AD,CO,TE,CL,EN,CY );
    String q3 = String.format("UPDATE persoData SET ");
    q3+=String.format("ACADEMIE='%s',",AC);
    q3+=String.format("DEPARTEMENT='%s',",DE);
    q3+=String.format("CIRCONSCRIPTION='%s',",CI);
    q3+=String.format("ECOLE='%s',",EC);
    q3+=String.format("ADRESSE='%s',",AD);
    q3+=String.format("COURIELLE='%s',",CO);
    q3+=String.format("TELEPHONE='%s',",TE);
    q3+=String.format("CLASSE='%s',",CL);
    q3+=String.format("ENSEIGNANT='%s',",EN);
    q3+=String.format("CYCLE=%d",CY);
    return add(q1, q2, q3);
  }

  private static class pData {

    String Enseignant;
    int Cycle;
  }

  
}
